package com.my.afarycode.OnlineShopping.bottomsheet;

import android.app.Dialog;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.databinding.DataBindingUtil;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.gson.Gson;
import com.my.afarycode.OnlineShopping.CheckOutPayment;
import com.my.afarycode.OnlineShopping.Model.AddWalletModal;
import com.my.afarycode.OnlineShopping.activity.CheckOutDeliveryAct;
import com.my.afarycode.OnlineShopping.constant.PreferenceConnector;
import com.my.afarycode.OnlineShopping.helper.DataManager;
import com.my.afarycode.OnlineShopping.listener.AskListener;
import com.my.afarycode.R;
import com.my.afarycode.databinding.FragmentPaymentBootmsheetBinding;
import com.my.afarycode.ratrofit.AfaryCode;
import com.my.afarycode.ratrofit.ApiClient;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PaymentBottomSheet extends BottomSheetDialogFragment {
    public String TAG = "PaymentBottomSheet";
    BottomSheetDialog dialog;
    FragmentPaymentBootmsheetBinding binding;
    private BottomSheetBehavior<View> mBehavior;
    public AskListener listener;
    String money="";
    private AfaryCode apiInterface;




    public PaymentBottomSheet(String money) {
        this.money = money;
    }


    public PaymentBottomSheet callBack(AskListener listener) {
        this.listener = listener;
        return this;
    }



    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        dialog = (BottomSheetDialog) super.onCreateDialog(savedInstanceState);
        binding = DataBindingUtil.inflate(LayoutInflater.from(getActivity()), R.layout.fragment_payment_bootmsheet, null, false);
        dialog.setContentView(binding.getRoot());
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        mBehavior = BottomSheetBehavior.from((View) binding.getRoot().getParent());
        mBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        initBinding();
        return  dialog;
    }

    private void initBinding() {

        apiInterface = ApiClient.getClient(getActivity()).create(AfaryCode.class);

        binding.llMoov.setOnClickListener(view -> {
            dialogMoov("MC","060110217");
        });

        binding.llAirtel.setOnClickListener(view -> {
            dialogAirtel("AM","074272732");
        });
    }



    public void dialogAirtel(String operator,String operatorNumber){
        Dialog mDialog = new Dialog(getActivity());
        mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mDialog.setContentView(R.layout.dialog_airtel);
        mDialog.setCancelable(false);
        mDialog.setCanceledOnTouchOutside(false);

        EditText edNumber = mDialog.findViewById(R.id.edNumber);
        AppCompatButton btnBack = mDialog.findViewById(R.id.btnBack);
        AppCompatButton btnPayNow = mDialog.findViewById(R.id.btnPayNow);


        btnPayNow.setText("$"+money + " " +getString(R.string.pay_now));

        btnBack.setOnClickListener(v -> {
            mDialog.dismiss();

        });

        btnPayNow.setOnClickListener(v -> {
            if(edNumber.getText().toString().equals(""))
                Toast.makeText(getActivity(), "Please enter number", Toast.LENGTH_SHORT).show();

            else {
                mDialog.dismiss();
                PaymentAPI(operator,edNumber.getText().toString(), operatorNumber);
            }

        });
        mDialog.show();

    }

    public void dialogMoov(String operator,String operatorNumber){
        Dialog mDialog = new Dialog(getActivity());
        mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mDialog.setContentView(R.layout.dialog_moov);
        mDialog.setCancelable(false);
        mDialog.setCanceledOnTouchOutside(false);

        EditText edNumber = mDialog.findViewById(R.id.edNumber);
        AppCompatButton btnBack = mDialog.findViewById(R.id.btnBack);
        AppCompatButton btnPayNow = mDialog.findViewById(R.id.btnPayNow);


        btnPayNow.setText("$"+money + " " +getString(R.string.pay_now));
        btnBack.setOnClickListener(v -> {
            mDialog.dismiss();

        });

        btnPayNow.setOnClickListener(v -> {
            if(edNumber.getText().toString().equals(""))
                Toast.makeText(getActivity(), "Please enter number", Toast.LENGTH_SHORT).show();

            else {
                mDialog.dismiss();
                PaymentAPI(operator,edNumber.getText().toString(), operatorNumber);
            }

        });

        mDialog.show();
    }


    private void PaymentAPI(String operator,String number,String  operatorNumber) {
        binding.loader.setVisibility(View.VISIBLE);

      //  DataManager.getInstance().showProgressMessage(getActivity(), "Please wait...");

        Map<String,String> headerMap = new HashMap<>();
        headerMap.put("Authorization","Bearer " + PreferenceConnector.readString(getActivity(), PreferenceConnector.access_token,""));
        headerMap.put("Accept","application/json");

        Map<String, String> map = new HashMap<>();
        map.put("user_id", PreferenceConnector.readString(getContext(), PreferenceConnector.User_id, ""));
        map.put("amount", money);

        map.put("operateur", operator);
        map.put("num_marchand", operatorNumber);
        map.put("user_number", number);

        Log.e("MapMap", "WALLET RECHARGE REQUEST" + map);

        Call<AddWalletModal> SignupCall = apiInterface.add_money(headerMap,map);

        SignupCall.enqueue(new Callback<AddWalletModal>() {
            @Override
            public void onResponse(Call<AddWalletModal> call, Response<AddWalletModal> response) {
                DataManager.getInstance().hideProgressMessage();
                binding.loader.setVisibility(View.GONE);

                try {
                    AddWalletModal data = response.body();
                    String dataResponse = new Gson().toJson(response.body());
                    Log.e("MapMap", "WALLET RECHARGE RESPONSE" + dataResponse);

                    if (data.status.equals("1")) {
                        listener.ask("Your Money Is Add SuccessFully ");
                        dismiss();

                    } else if (data.status.equals("0")) {
                        dismiss();
                        Toast.makeText(getActivity(), data.message, Toast.LENGTH_SHORT).show();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<AddWalletModal> call, Throwable t) {
                call.cancel();
                DataManager.getInstance().hideProgressMessage();
                binding.loader.setVisibility(View.GONE);

            }
        });
    }


}