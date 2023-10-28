package com.my.afarycode.OnlineShopping.fragment;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatButton;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.gson.Gson;
import com.my.afarycode.OnlineShopping.CheckOutPayment;
import com.my.afarycode.OnlineShopping.CheckOutScreen;
import com.my.afarycode.OnlineShopping.HomeActivity;
import com.my.afarycode.OnlineShopping.Model.AddWalletModal;
import com.my.afarycode.OnlineShopping.Model.DeliveryAgencyModel;
import com.my.afarycode.OnlineShopping.Model.TransferMoneyModal;
import com.my.afarycode.OnlineShopping.activity.CheckOutDeliveryAct;
import com.my.afarycode.OnlineShopping.constant.PreferenceConnector;
import com.my.afarycode.OnlineShopping.helper.DataManager;
import com.my.afarycode.OnlineShopping.listener.AskListener;
import com.my.afarycode.R;
import com.my.afarycode.ratrofit.AfaryCode;
import com.my.afarycode.ratrofit.ApiClient;
import com.rilixtech.widget.countrycodepicker.CountryCodePicker;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class TransferMOneyFragment extends BottomSheetDialogFragment {

    Context context;
    private EditText mobile_no_et;
    private AfaryCode apiInterface;
    private RelativeLayout payment_done;
    private EditText et_money;
    private CountryCodePicker ccp;
    private String code;
    AskListener listener;
    public TransferMOneyFragment(Context context) {
        this.context = context;
    }


    public TransferMOneyFragment callBack(AskListener listener) {
        this.listener = listener;
        return this;
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @SuppressLint("RestrictedApi")
    public void setupDialog(Dialog dialog, int style) {
        View contentView = View.inflate(getContext(), R.layout.fragmen_transfer_money, (ViewGroup) null);
        apiInterface = ApiClient.getClient(context).create(AfaryCode.class);

        mobile_no_et = contentView.findViewById(R.id.mobile_no_et);
        payment_done = contentView.findViewById(R.id.payment_done);
        et_money = contentView.findViewById(R.id.et_money);
        ccp = contentView.findViewById(R.id.ccp);

        payment_done.setOnClickListener(v -> {
            code = ccp.getSelectedCountryCode();
            Log.e("code>>>", code);

            TransferMoneyAPI( code, mobile_no_et.getText().toString()
                    , et_money.getText().toString());
        });

        dialog.setContentView(contentView);

        ((View) contentView.getParent()).setBackgroundColor(getResources().getColor(android.R.color.transparent));

    }

     private void TransferMoneyAPI(String countryCode,String mobile_no_et, String add_money) {
        DataManager.getInstance().showProgressMessage(getActivity(), "Please wait...");
         Map<String,String> headerMap = new HashMap<>();
         headerMap.put("Authorization","Bearer " +PreferenceConnector.readString(getActivity(), PreferenceConnector.access_token,""));
         headerMap.put("Accept","application/json");

        Map<String, String> map = new HashMap<>();
        map.put("user_id", PreferenceConnector.readString(getContext(), PreferenceConnector.User_id, ""));
        map.put("amount", add_money);
        map.put("to_country_code", countryCode);
         map.put("to_mobile", mobile_no_et);
         map.put("datetime", DataManager.getCurrent());

        Log.e("MapMap", "TransferMoney REQUEST" + map);

        Call<ResponseBody> SignupCall = apiInterface.transfer_money(headerMap,map);

        SignupCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                DataManager.getInstance().hideProgressMessage();

                try {
                    String responseData = response.body() != null ? response.body().string() : "";
                    JSONObject object = new JSONObject(responseData);
                    Log.e("MapMap", "TransferMoney RESPONSE" + object);
                    if (object.optString("status").equals("1")) {
                        dialogNumberExit(object.getString("amount"),object.getString("total_amount"),object.getString("wallet_fees"),mobile_no_et , countryCode);

                        } else if (object.optString("status").equals("0")) {
                        AlertNumberNotExit();

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


    public void dialogNumberExit(String Amount,String totalAmount,String fee,String number,String countryCode){
        Dialog mDialog = new Dialog(getActivity());
        mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mDialog.setContentView(R.layout.dialog_transfer_money);
        mDialog.setCancelable(false);
        mDialog.setCanceledOnTouchOutside(false);

        TextView tvAmount = mDialog.findViewById(R.id.tvAmount);
        TextView tvFee = mDialog.findViewById(R.id.tvFee);
        TextView tvTotalAmount = mDialog.findViewById(R.id.tvTotalAmount);

        LinearLayout btnOk = mDialog.findViewById(R.id.btnOk);

        tvAmount.setText(Html.fromHtml("<font color='#000'>" + "<b>" + "Amount : " + "</b>" + "$" +Amount + "</font>"  ));
        tvFee.setText(Html.fromHtml("<font color='#000'>" + "<b>" + "Fees : " + "</b>" + "$" +fee + "</font>"  ));
        tvTotalAmount.setText(Html.fromHtml("<font color='#000'>" + "<b>" + "Total Amount : " + "</b>" + "$" + totalAmount + "</font>"  ));

        btnOk.setOnClickListener(v -> {
            mDialog.dismiss();
            TransferMoneyFirstAPI(countryCode,number,Amount,totalAmount,fee);


        });
        mDialog.show();

    }








    private void TransferMoneyFirstAPI(String countryCode,String number, String amount,String totalAmount, String fee) {
        DataManager.getInstance().showProgressMessage(getActivity(), "Please wait...");
        Map<String,String> headerMap = new HashMap<>();
        headerMap.put("Authorization","Bearer " +PreferenceConnector.readString(getActivity(), PreferenceConnector.access_token,""));
        headerMap.put("Accept","application/json");

        Map<String, String> map = new HashMap<>();
        map.put("user_id", PreferenceConnector.readString(getContext(), PreferenceConnector.User_id, ""));
        map.put("amount", amount);
        map.put("to_country_code", countryCode);
        map.put("to_mobile", number);
        map.put("datetime", DataManager.getCurrent());
        map.put("total_amount", totalAmount);
        map.put("admin_fees", fee);

        Log.e("MapMap", "TransferMoney First REQUEST" + map);

        Call<ResponseBody> SignupCall = apiInterface.transfer_moneyFirstApi(headerMap,map);

        SignupCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                DataManager.getInstance().hideProgressMessage();

                try {
                    String responseData = response.body() != null ? response.body().string() : "";
                    JSONObject object = new JSONObject(responseData);
                    Log.e("MapMap", "TransferMoney First RESPONSE" + object);
                    if (object.optString("status").equals("1")) {
                        listener.ask("","transfer");
                        dismiss();
                        Toast.makeText(getContext(), "Your Money Transfer  SuccessFully ", Toast.LENGTH_SHORT).show();

                    } else if (object.optString("status").equals("0")) {

                        Toast.makeText(getActivity(), object.optString("message"), Toast.LENGTH_SHORT).show();
                        listener.ask("","transfer");
                        dismiss();

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




    private void AlertNumberNotExit() {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("Your beneficiary does not have a wallet to receive the funds. send him this link to download our app. show link")
                .setCancelable(false)
                .setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();



    }



}
