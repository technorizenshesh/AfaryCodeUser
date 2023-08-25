package com.my.afarycode.OnlineShopping.fragment;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.gson.Gson;
import com.my.afarycode.OnlineShopping.Model.AddWalletModal;
import com.my.afarycode.OnlineShopping.Model.TransferMoneyModal;
import com.my.afarycode.OnlineShopping.activity.CheckOutDeliveryAct;
import com.my.afarycode.OnlineShopping.constant.PreferenceConnector;
import com.my.afarycode.OnlineShopping.helper.DataManager;
import com.my.afarycode.R;
import com.my.afarycode.ratrofit.AfaryCode;
import com.my.afarycode.ratrofit.ApiClient;
import com.rilixtech.widget.countrycodepicker.CountryCodePicker;

import java.util.HashMap;
import java.util.Map;

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

    public TransferMOneyFragment(Context context) {
        this.context = context;
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

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

            TransferMoneyAPI("+" + code + mobile_no_et.getText().toString()
                    , et_money.getText().toString());
        });

        dialog.setContentView(contentView);

        ((View) contentView.getParent()).setBackgroundColor(getResources().getColor(android.R.color.transparent));

    }

     private void TransferMoneyAPI(String mobile_no_et, String add_money) {
        DataManager.getInstance().showProgressMessage(getActivity(), "Please wait...");
         Map<String,String> headerMap = new HashMap<>();
         headerMap.put("Authorization","Bearer " +PreferenceConnector.readString(getActivity(), PreferenceConnector.access_token,""));
         headerMap.put("Accept","application/json");

        Map<String, String> map = new HashMap<>();
        map.put("user_id", PreferenceConnector.readString(getContext(), PreferenceConnector.User_id, ""));
        map.put("mobile", add_money);
        map.put("money", mobile_no_et);

        Log.e("MapMap", "LOGIN REQUEST" + map);

        Call<TransferMoneyModal> SignupCall = apiInterface.transfer_money(headerMap,map);

        SignupCall.enqueue(new Callback<TransferMoneyModal>() {
            @Override
            public void onResponse(Call<TransferMoneyModal> call, Response<TransferMoneyModal> response) {
                DataManager.getInstance().hideProgressMessage();

                try {
                    TransferMoneyModal data = response.body();
                    String dataResponse = new Gson().toJson(response.body());
                    Log.e("MapMap", "LOGIN RESPONSE" + dataResponse);

                    if (data.status.equals("1")) {
                        dismiss();
                        Toast.makeText(getContext(), "Your Money Transfer  SuccessFully ", Toast.LENGTH_SHORT).show();

                    } else if (data.status.equals("0")) {
                        Toast.makeText(getActivity(), data.message, Toast.LENGTH_SHORT).show();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<TransferMoneyModal> call, Throwable t) {
                call.cancel();
                DataManager.getInstance().hideProgressMessage();
            }
        });
    }
}
