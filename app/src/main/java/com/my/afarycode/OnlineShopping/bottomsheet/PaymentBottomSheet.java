package com.my.afarycode.OnlineShopping.bottomsheet;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
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
import com.my.afarycode.OnlineShopping.CheckPaymentStatusAct;
import com.my.afarycode.OnlineShopping.HomeActivity;
import com.my.afarycode.OnlineShopping.Model.AddWalletModal;
import com.my.afarycode.OnlineShopping.activity.CheckOutDeliveryAct;
import com.my.afarycode.OnlineShopping.constant.PreferenceConnector;
import com.my.afarycode.OnlineShopping.helper.DataManager;
import com.my.afarycode.OnlineShopping.helper.MyService;
import com.my.afarycode.OnlineShopping.listener.AskListener;
import com.my.afarycode.R;
import com.my.afarycode.Splash;
import com.my.afarycode.databinding.FragmentPaymentBootmsheetBinding;
import com.my.afarycode.ratrofit.AfaryCode;
import com.my.afarycode.ratrofit.ApiClient;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import okhttp3.ResponseBody;
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



    BroadcastReceiver PaymentStatusReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.e("PaymentStatusReceiver ====","=====");
            try {
                JSONObject object = new JSONObject(intent.getStringExtra("object"));
                if(intent.getStringExtra("object")!= null) {
                    JSONObject result = object.getJSONObject("result");
                    Log.e("PaymentStatusReceiver ====",intent.getStringExtra("object")+"");

                    if(object.getString("status").equals("1")){
                        Toast.makeText(getActivity(), object.getString("message"), Toast.LENGTH_SHORT).show();
                      //  startActivity(new Intent(getActivity(), HomeActivity.class)
                         //       .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP));
                      //  finish();
                        listener.ask("Your Money Is Add SuccessFully ","add money");
                        dismiss();

                    }


                    else if (object.getString("status").equals("3")) {
                        Log.e("Payment under processing ====",intent.getStringExtra("object")+"");


                    } else {
                        Toast.makeText(getActivity(), object.getString("message"), Toast.LENGTH_SHORT).show();
                       // startActivity(new Intent(getActivity.this,HomeActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP));
                       // finish();
                        listener.ask("Failed ","");
                        dismiss();
                    }


                }


            }
            catch (Exception e){
                e.printStackTrace();
            }

        }
    };





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
       // binding.loader.setVisibility(View.VISIBLE);

        DataManager.getInstance().showProgressMessage(getActivity(), "Please wait...");
        Map<String,String> headerMap = new HashMap<>();
        headerMap.put("Authorization","Bearer " + PreferenceConnector.readString(getActivity(), PreferenceConnector.access_token,""));
        headerMap.put("Accept","application/json");

        Map<String, String> map = new HashMap<>();
        map.put("user_id", PreferenceConnector.readString(getContext(), PreferenceConnector.User_id, ""));
        map.put("amount", money);

        map.put("operateur", operator);
        map.put("num_marchand", operatorNumber);
        map.put("user_number", number);
        map.put("datetime", DataManager.getCurrent());
        map.put("register_id", PreferenceConnector.readString(getActivity(), PreferenceConnector.Register_id, ""));


        Log.e("MapMap", "WALLET RECHARGE REQUEST" + map);

        Call<ResponseBody> SignupCall = apiInterface.add_money(headerMap,map);

        SignupCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                DataManager.getInstance().hideProgressMessage();
             //   binding.loader.setVisibility(View.GONE);

                try {
                    String responseData = response.body() != null ? response.body().string() : "";
                    JSONObject object = new JSONObject(responseData);
                    Log.e(TAG, "WALLET RECHARGE RESPONSE" + object);
                    if (object.getString("status").equals("1")) {
                        binding.rlPayment.setVisibility(View.GONE);
                        binding.rlPaymentStatus.setVisibility(View.VISIBLE);
                        PreferenceConnector.writeString(getActivity(),PreferenceConnector.transId,object.getJSONObject("ressult").getString("reference"));
                        PreferenceConnector.writeString(getActivity(),PreferenceConnector.serviceType,"AddMoney");
                        getActivity().startService(new Intent(getActivity(), MyService.class));


                    } else if (object.getString("status").equals("0")) {
                        binding.rlPayment.setVisibility(View.VISIBLE);
                        binding.rlPaymentStatus.setVisibility(View.GONE);
                        dismiss();
                        Toast.makeText(getActivity(), object.getString("message"), Toast.LENGTH_SHORT).show();
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
               // binding.loader.setVisibility(View.GONE);

            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().registerReceiver(PaymentStatusReceiver, new IntentFilter("check_payment_status"));

    }

    @Override
    public void onStop() {
        super.onStop();
        getActivity().stopService(new Intent(getActivity(), MyService.class));
        getActivity().unregisterReceiver(PaymentStatusReceiver);

    }
}