package com.my.afarycode.OnlineShopping.fragment;

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
import com.my.afarycode.OnlineShopping.Model.DeliveryAgencyModel;
import com.my.afarycode.OnlineShopping.Model.TransferMoneyModal;
import com.my.afarycode.OnlineShopping.Model.WithDrawalMoney;
import com.my.afarycode.OnlineShopping.activity.CheckOutDeliveryAct;
import com.my.afarycode.OnlineShopping.bottomsheet.PaymentBottomSheet;
import com.my.afarycode.OnlineShopping.bottomsheet.WithdrawDetailBottomSheet;
import com.my.afarycode.OnlineShopping.constant.PreferenceConnector;
import com.my.afarycode.OnlineShopping.helper.DataManager;
import com.my.afarycode.OnlineShopping.listener.AskListener;
import com.my.afarycode.R;
import com.my.afarycode.Splash;
import com.my.afarycode.ratrofit.AfaryCode;
import com.my.afarycode.ratrofit.ApiClient;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class WithDrawFragment extends BottomSheetDialogFragment implements AskListener{
    Context context;
    private RelativeLayout done_withdraw;
    private AfaryCode apiInterface;
    private EditText edtEmail;
    private EditText withdrawal_money;
    private TextView tvNote;
    String walletBal="0";
    AskListener listener;

    public WithDrawFragment(Context context,String walletBal) {
        this.context = context;
        this.walletBal = walletBal;
    }


    public WithDrawFragment callBack(AskListener listener) {
        this.listener = listener;
        return this;
    }


    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @SuppressLint("RestrictedApi")
    public void setupDialog(Dialog dialog, int style) {

        View contentView = View.inflate(getContext(), R.layout.fragmen_withdraw_money, (ViewGroup) null);

        edtEmail = contentView.findViewById(R.id.edtEmail);
        done_withdraw = contentView.findViewById(R.id.done_withdraw);

        apiInterface = ApiClient.getClient(context).create(AfaryCode.class);

        withdrawal_money = contentView.findViewById(R.id.withdrawal_money);
        tvNote  = contentView.findViewById(R.id.tvNote);
        tvNote.setText(Html.fromHtml("<font color='#000'>"  + "<b>"  + "Note:" + "</b>" + "</font>"+ "<font color='#01709B'>" +
                "Go and Click on the transaction that interests you in the list of transactions then copy its ID and paste it here"+"</font>"));



        done_withdraw.setOnClickListener(v -> {
           if(!withdrawal_money.getText().toString().equalsIgnoreCase("")) WithDrawalAPI();
            else Toast.makeText(context, "Please enter transaction id", Toast.LENGTH_SHORT).show();
        });

        dialog.setContentView(contentView);

        ((View) contentView.getParent()).setBackgroundColor(getResources().getColor(android.R.color.transparent));

    }

    private void WithDrawalAPI() {
        DataManager.getInstance().showProgressMessage(getActivity(), "Please wait...");

        Map<String,String> headerMap = new HashMap<>();
        headerMap.put("Authorization","Bearer " +PreferenceConnector.readString(getActivity(), PreferenceConnector.access_token,""));
        headerMap.put("Accept","application/json");

        Map<String, String> map = new HashMap<>();
        map.put("user_id", PreferenceConnector.readString(getContext(), PreferenceConnector.User_id, ""));
        map.put("transaction_id", withdrawal_money.getText().toString());
        map.put("datetime", DataManager.getCurrent());
        map.put("register_id", PreferenceConnector.readString(getActivity(), PreferenceConnector.Register_id, ""));

        Log.e("MapMap", "Send Withdraw Request" + map);
        Call<ResponseBody> SignupCall = apiInterface.withdraw_money(headerMap,map);

        SignupCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                DataManager.getInstance().hideProgressMessage();

                try {
                    String responseData = response.body() != null ? response.body().string() : "";
                    JSONObject object = new JSONObject(responseData);
                    Log.e("MapMap", "Send Withdraw Request RESPONSE" + object);
                    if (object.optString("status").equals("1")) {
                       // Toast.makeText(getContext(), "Send Withdraw Request Successful", Toast.LENGTH_SHORT).show();
                        callMethod(responseData);
                    } else if (object.optString("status").equals("0")) {
                        listener.ask("","withdraw");
                        dismiss();
                        Toast.makeText(getContext(), object.optString("message"), Toast.LENGTH_SHORT).show();

                    }

                    else if (object.optString("status").equals("5")) {
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

    private void callMethod(String responseData) {
        new WithdrawDetailBottomSheet(getActivity(),responseData,walletBal).callBack(this::ask).show(getActivity().getSupportFragmentManager(), "ModalBottomSheet");

    }

    @Override
    public void ask(String value,String status) {
        listener.ask("","withdrawOne");
        dismiss();
    }


}
