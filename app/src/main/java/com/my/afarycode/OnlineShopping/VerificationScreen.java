package com.my.afarycode.OnlineShopping;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.google.android.gms.auth.api.phone.SmsRetriever;
import com.google.android.gms.auth.api.phone.SmsRetrieverClient;

import com.my.afarycode.OnlineShopping.constant.PreferenceConnector;
import com.my.afarycode.OnlineShopping.helper.DataManager;
import com.my.afarycode.readotp.SmsBroadcastReceiver;
import com.my.afarycode.R;
import com.my.afarycode.databinding.ActivityVerificationScreenBinding;
import com.my.afarycode.ratrofit.AfaryCode;
import com.my.afarycode.ratrofit.ApiClient;
import org.json.JSONObject;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class VerificationScreen extends AppCompatActivity {
    ActivityVerificationScreenBinding binding;
    private AfaryCode apiInterface;
    String userId="",mobile="",countryCode="";
    public static final String TAG = VerificationScreen.class.getSimpleName();
    private static final int REQ_USER_CONSENT = 200;
    SmsBroadcastReceiver smsBroadcastReceiver;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_verification_screen);
        apiInterface = ApiClient.getClient(this).create(AfaryCode.class);
        SetupUI();
        startSmartUserConsent();
    }


    private void SetupUI() {
        if(getIntent()!=null){
            userId = getIntent().getStringExtra("user_id");
            mobile = getIntent().getStringExtra("mobile");
           countryCode = "+"+getIntent().getStringExtra("countryCode");

            binding.description.setText(getString(R.string.otp_text1) + " " +countryCode +mobile +" " + getString(R.string.otp_text2));
        }

        //  mobile = "9755463923";
       //  countryCode = "+91";

        binding.btnVerify.setOnClickListener(v -> {
            if(binding.Otp.getOTP().equals("")){
                Toast.makeText(VerificationScreen.this,getString(R.string.enter_otp),Toast.LENGTH_LONG).show();
            }
            else {
                VerificationAPI();
            }

        });

        binding.resendOtp.setOnClickListener(v -> {
            binding.Otp.setOTP("");
            startSmartUserConsent();
            sendVerificationCode(mobile,countryCode);
        });

        sendVerificationCode(mobile,countryCode);
    }

    private void sendVerificationCode(String mobileNumber,String countryCode) {
        DataManager.getInstance().showProgressMessage(VerificationScreen.this, getString(R.string.please_wait));
        Map<String, String> map = new HashMap<>();
        map.put("mobile_number",mobileNumber);
        map.put("country_code",countryCode);
        Log.e("OtpScreen", " Otp Request ==="+ map);
        Call<ResponseBody> loginCall = apiInterface.sendOtpApi(map);

        loginCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                DataManager.getInstance().hideProgressMessage();

                try {

                    Log.e("response===", response.body().toString());
                    String stringResponse = response.body().string();
                    JSONObject jsonObject = new JSONObject(stringResponse);
                    if (jsonObject.getString("status").equals("1")) {
                    Toast.makeText(VerificationScreen.this,getString(R.string.otp_successfully_send),Toast.LENGTH_LONG).show();
                    } else if (jsonObject.getString("status").equals("0")) {
                        Toast.makeText(VerificationScreen.this, jsonObject.getString("message"), Toast.LENGTH_LONG).show();
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

    private void VerificationAPI() {
        DataManager.getInstance().showProgressMessage(VerificationScreen.this, getString(R.string.please_wait));
        Map<String, String> map = new HashMap<>();
        map.put("mobile_number",mobile);
        map.put("country_code",countryCode);
        map.put("otp",binding.Otp.getOTP());
        Log.e("OtpScreen", "Verify Otp Request ==="+ map);
        Call<ResponseBody> SignupCall = apiInterface.verifyOtpApi(map);
        SignupCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                DataManager.getInstance().hideProgressMessage();
                try {
                    Log.e("response===", response.body().toString());
                    String stringResponse = response.body().string();
                    JSONObject jsonObject = new JSONObject(stringResponse);
                    if (jsonObject.getString("status").equals("1")) {
                      //  Toast.makeText(VerificationScreen.this,getString(R.string.),Toast.LENGTH_LONG).show();
                        PreferenceConnector.writeString(VerificationScreen.this, PreferenceConnector.LoginStatus, "true");
                        startActivity(new Intent(VerificationScreen.this, HomeActivity.class)
                                .putExtra("status", "")
                                .putExtra("msg", ""));
                        finish();
                    } else if (jsonObject.getString("status").equals("0")) {
                        Toast.makeText(VerificationScreen.this, jsonObject.getString("message"), Toast.LENGTH_LONG).show();
                        sendVerificationCode(mobile,countryCode);

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



    private void startSmartUserConsent() {
        SmsRetrieverClient client = SmsRetriever.getClient(this);
        client.startSmsUserConsent(null);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQ_USER_CONSENT){

            if ((resultCode == RESULT_OK) && (data != null)){
                String message = data.getStringExtra(SmsRetriever.EXTRA_SMS_MESSAGE);
                getOtpFromMessage(message);
            }
        }

    }

    private void getOtpFromMessage(String message) {

        Pattern otpPattern = Pattern.compile("(|^)\\d{6}");
        Matcher matcher = otpPattern.matcher(message);
        if (matcher.find()){
            /// etOTP.setText();
            binding.Otp.setOTP(matcher.group(0));

        }

    }

    private void registerBroadcastReceiver(){

        smsBroadcastReceiver = new SmsBroadcastReceiver();

        smsBroadcastReceiver.smsBroadcastReceiverListener = new SmsBroadcastReceiver.SmsBroadcastReceiverListener() {
            @Override
            public void onSuccess(Intent intent) {

                startActivityForResult(intent,REQ_USER_CONSENT);

            }

            @Override
            public void onFailure() {

            }
        };
        IntentFilter intentFilter = new IntentFilter(SmsRetriever.SMS_RETRIEVED_ACTION);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            registerReceiver(smsBroadcastReceiver, intentFilter,Context.RECEIVER_NOT_EXPORTED);
            Log.e("11111:","====");

        }
        else {
            registerReceiver(smsBroadcastReceiver,intentFilter);
            Log.e("22222:","====");

        }


    }

    @Override
    protected void onStart() {
        super.onStart();
        registerBroadcastReceiver();

    }

    @Override
    protected void onStop() {
        super.onStop();
        unregisterReceiver(smsBroadcastReceiver);
    }



}