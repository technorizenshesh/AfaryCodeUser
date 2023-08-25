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
import com.my.afarycode.OnlineShopping.Model.TransferMoneyModal;
import com.my.afarycode.OnlineShopping.Model.WithDrawalMoney;
import com.my.afarycode.OnlineShopping.activity.CheckOutDeliveryAct;
import com.my.afarycode.OnlineShopping.constant.PreferenceConnector;
import com.my.afarycode.OnlineShopping.helper.DataManager;
import com.my.afarycode.R;
import com.my.afarycode.ratrofit.AfaryCode;
import com.my.afarycode.ratrofit.ApiClient;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class WithDrawFragment extends BottomSheetDialogFragment {
    Context context;
    private RelativeLayout done_withdraw;
    private AfaryCode apiInterface;
    private EditText edtEmail;
    private EditText withdrawal_money;

    public WithDrawFragment(Context context) {
        this.context = context;
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public void setupDialog(Dialog dialog, int style) {

        View contentView = View.inflate(getContext(), R.layout.fragmen_withdraw_money, (ViewGroup) null);

        edtEmail = contentView.findViewById(R.id.edtEmail);
        done_withdraw = contentView.findViewById(R.id.done_withdraw);

        apiInterface = ApiClient.getClient(context).create(AfaryCode.class);

        withdrawal_money = contentView.findViewById(R.id.withdrawal_money);

        done_withdraw.setOnClickListener(v -> {
            WithDrawalAPI();
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
        map.put("money", withdrawal_money.getText().toString());

        Log.e("MapMap", "LOGIN REQUEST" + map);

        Call<WithDrawalMoney> SignupCall = apiInterface.withdraw_money(headerMap,map);

        SignupCall.enqueue(new Callback<WithDrawalMoney>() {
            @Override
            public void onResponse(Call<WithDrawalMoney> call, Response<WithDrawalMoney> response) {
                DataManager.getInstance().hideProgressMessage();

                try {
                    WithDrawalMoney data = response.body();
                    String dataResponse = new Gson().toJson(response.body());
                    Log.e("MapMap", "LOGIN RESPONSE" + dataResponse);

                    if (data.status.equals("1")) {
                        Toast.makeText(getContext(), "Send Request Successfull", Toast.LENGTH_SHORT).show();
                        dismiss();

                    } else if (data.status.equals("0")) {
                        Toast.makeText(getActivity(), data.message, Toast.LENGTH_SHORT).show();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<WithDrawalMoney> call, Throwable t) {
                call.cancel();
                DataManager.getInstance().hideProgressMessage();
            }
        });
    }
}
