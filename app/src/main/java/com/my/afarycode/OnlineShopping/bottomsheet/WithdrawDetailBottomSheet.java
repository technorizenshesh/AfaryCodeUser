package com.my.afarycode.OnlineShopping.bottomsheet;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.gson.Gson;
import com.my.afarycode.OnlineShopping.Model.GetProfileModal;
import com.my.afarycode.OnlineShopping.constant.PreferenceConnector;
import com.my.afarycode.OnlineShopping.helper.DataManager;
import com.my.afarycode.OnlineShopping.helper.NetworkAvailablity;
import com.my.afarycode.OnlineShopping.listener.AskListener;
import com.my.afarycode.R;
import com.my.afarycode.Splash;
import com.my.afarycode.ratrofit.AfaryCode;
import com.my.afarycode.ratrofit.ApiClient;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class WithdrawDetailBottomSheet extends BottomSheetDialogFragment {
    Context context;
    private RelativeLayout done_withdraw;
    private AfaryCode apiInterface;
    private EditText edHelp;
    private EditText withdrawal_money;
    private TextView tvNote,tvTransaction,tvAmount,tvNewBalance;
    String dataResponse="",id="",walletBal="";
    AskListener listener;
    public WithdrawDetailBottomSheet(Context context,String dataResponse,String walletBal) {
        this.context = context;
        this.dataResponse = dataResponse;
        this.walletBal = walletBal;
    }


    public WithdrawDetailBottomSheet callBack(AskListener listener) {
        this.listener = listener;
        return this;
    }


    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @SuppressLint("RestrictedApi")
    public void setupDialog(Dialog dialog, int style) {

        View contentView = View.inflate(getContext(), R.layout.fragmen_withdraw_money_detail, (ViewGroup) null);

        edHelp = contentView.findViewById(R.id.edHelp);
        done_withdraw = contentView.findViewById(R.id.done_withdraw);

        apiInterface = ApiClient.getClient(context).create(AfaryCode.class);

        tvNote  = contentView.findViewById(R.id.tvNote);
        tvTransaction  = contentView.findViewById(R.id.tvTransaction);
        tvAmount  = contentView.findViewById(R.id.tvAmount);
        tvNewBalance  = contentView.findViewById(R.id.tvNewBalance);

        try {
            JSONObject jsonObject = new JSONObject(dataResponse);
             JSONObject resultObj = jsonObject.getJSONObject("result");
             id = resultObj.getString("id");

            tvTransaction.setText(Html.fromHtml("<font color='#000'>"  + "<b>"  + getString(R.string.transaction_id) + "</b>" + "</font>"+ "<font color='#9098B1'>"  +
                    resultObj.getString("transaction_id")+"</font>"));

            tvAmount.setText(Html.fromHtml("<font color='#000'>"  + "<b>"  + getString(R.string.amounts)  + "</b>" + "</font>"+ "<font color='#9098B1'>" +
                  "FCFA"+  resultObj.getString("amount")+"</font>"));

            tvNewBalance.setText(Html.fromHtml("<font color='#000'>"  + "<b>"  + getString(R.string.new_balance) + "</b>" + "</font>"+ "<font color='#9098B1'>" +
                    String.format("%.2f",Double.parseDouble(walletBal) - Double.parseDouble(resultObj.getString("amount")))+"</font>"));



        } catch (JSONException e) {
            throw new RuntimeException(e);
        }


        tvNote.setText(Html.fromHtml("<font color='#000'>"  + "<b>"  + getString(R.string.note) + "</b>" + "</font>"+ "<font color='#01709B'>"  +
                getString(R.string.go_and_click)+"</font>"));



        done_withdraw.setOnClickListener(v -> {


            if(NetworkAvailablity.checkNetworkStatus(requireActivity())) sendWithdrawRequestAPI();
            else Toast.makeText(requireActivity(), getString(R.string.network_failure), Toast.LENGTH_SHORT).show();
        });

        dialog.setContentView(contentView);

        ((View) contentView.getParent()).setBackgroundColor(getResources().getColor(android.R.color.transparent));

    }

    private void sendWithdrawRequestAPI() {
        DataManager.getInstance().showProgressMessage(getActivity(), getString(R.string.please_wait));

        Map<String,String> headerMap = new HashMap<>();
        headerMap.put("Authorization","Bearer " + PreferenceConnector.readString(getActivity(), PreferenceConnector.access_token,""));
        headerMap.put("Accept","application/json");

        Map<String, String> map = new HashMap<>();
        map.put("user_help", edHelp.getText().toString());
        map.put("request_id", id);
        map.put("register_id", PreferenceConnector.readString(getActivity(), PreferenceConnector.Register_id, ""));
        map.put("user_id", PreferenceConnector.readString(getActivity(), PreferenceConnector.User_id, ""));

        Log.e("MapMap", "Send Withdraw Details Request" + map);
        Call<ResponseBody> SignupCall = apiInterface.withdraw_money_update(headerMap,map);

        SignupCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                DataManager.getInstance().hideProgressMessage();

                try {
                    String responseData = response.body() != null ? response.body().string() : "";
                    JSONObject object = new JSONObject(responseData);
                    Log.e("MapMap", "Send Withdraw Details RESPONSE" + object);
                    if (object.optString("status").equals("1")) {
                        Toast.makeText(getContext(), "Send Withdraw Request Successful", Toast.LENGTH_SHORT).show();
                        listener.ask(responseData,"withdrawTwo");
                        dismiss();
                    } else if (object.optString("status").equals("0")) {
                        listener.ask("","withdrawTwo");
                        dismiss();
                        Toast.makeText(getContext(), object.optString("message"), Toast.LENGTH_SHORT).show();

                    }

                    else if (object.getString("status").equals("5")) {
                        PreferenceConnector.writeString(getActivity(), PreferenceConnector.LoginStatus, "false");
                        startActivity(new Intent(getActivity(), Splash.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP));
                        getActivity().finish();
                    }


                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                call.cancel();
                DataManager.getInstance().hideProgressMessage();
            }
        });
    }




}
