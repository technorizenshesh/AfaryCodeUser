package com.my.afarycode.OnlineShopping;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.google.android.gms.auth.api.phone.SmsRetriever;
import com.google.android.gms.auth.api.phone.SmsRetrieverClient;

import com.google.gson.Gson;
import com.my.afarycode.OnlineShopping.Model.SignupModel;
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
    String userName="",name="",email="",password="",mobile="",countryCode="",country="",language="",type="",registerId="";
    public static final String TAG = VerificationScreen.class.getSimpleName();
    private static final int REQ_USER_CONSENT = 200;
    SmsBroadcastReceiver smsBroadcastReceiver;
    private static final long TIMER_DURATION = 60000; // 1 minute in milliseconds
    private static final long TIMER_INTERVAL = 1000;  // 1 second interval
    private int resendButtonCount=0;

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
           // userId = getIntent().getStringExtra("user_id");
           // mobile = getIntent().getStringExtra("mobile");
          // countryCode = "+"+getIntent().getStringExtra("countryCode");

            userName = getIntent().getStringExtra("user_name");
            name = getIntent().getStringExtra("name");
            email = getIntent().getStringExtra("email");
            password = getIntent().getStringExtra("password");
            mobile = getIntent().getStringExtra("mobile");
            countryCode = getIntent().getStringExtra("country_code");
            country = getIntent().getStringExtra("country");
            language = getIntent().getStringExtra("language");
            type = getIntent().getStringExtra("type");
            registerId = getIntent().getStringExtra("register_id");



            binding.description.setText(getString(R.string.otp_text1) + " "+ "+" +countryCode +mobile +" " + getString(R.string.otp_text2));
        }

        /* mobile = "9755463923";
        countryCode = "+91";
        binding.description.setText(getString(R.string.otp_text1) + " " +countryCode +mobile +" " + getString(R.string.otp_text2));*/


        binding.btnVerify.setOnClickListener(v -> {
            if(binding.Otp.getOTP().equals("")){
                Toast.makeText(VerificationScreen.this,getString(R.string.enter_otp),Toast.LENGTH_LONG).show();
            }
            else {
                VerificationAPI();
            }

        });

        binding.resendOtp.setOnClickListener(v -> {
           resendButtonCount++;
            binding.Otp.setOTP("");
            startSmartUserConsent();
            sendVerificationCode(mobile,countryCode);
        });


        binding.tvWhatsapp.setOnClickListener(v -> {
            binding.Otp.setOTP("");
            sendVerificationCodeOnWhatsApp(mobile,countryCode);
        });



       // sendOnServerNumber(mobile,countryCode);

        sendVerificationCode(mobile,countryCode);
    }


    private void startTimer() {
        new CountDownTimer(TIMER_DURATION, TIMER_INTERVAL) {

            @Override
            public void onTick(long millisUntilFinished) {
                // Update the timer TextView with the remaining time
                long seconds = millisUntilFinished / 1000;
                binding.tvTimer.setText(String.format("%02d:%02d", seconds / 60, seconds % 60));
            }

            @Override
            public void onFinish() {
                // Timer finished, show the resend button
                binding.resendOtp.setVisibility(View.VISIBLE);
                binding.tvTimer.setVisibility(View.GONE);

            }
        }.start();
    }





    private void sendOnServerNumber(String mobileNumber,String countryCode) {
        DataManager.getInstance().showProgressMessage(VerificationScreen.this, getString(R.string.please_wait));
        Map<String, String> map = new HashMap<>();
        map.put("mobile_number",mobileNumber);
        map.put("country_code",countryCode);
        Log.e("Send number On Server", " Send number On Server Request ==="+ map);
        Call<ResponseBody> loginCall = apiInterface.sendNumberOnServerApi(map);

        loginCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                DataManager.getInstance().hideProgressMessage();

                try {

                    Log.e("Send number On Server ===", response.body().toString());
                    String stringResponse = response.body().string();
                    JSONObject jsonObject = new JSONObject(stringResponse);
                    if (jsonObject.getString("status").equals("1")) {
                        sendVerificationCode(mobile,countryCode);
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




    private void sendVerificationCode(String mobileNumber,String countryCode) {
        DataManager.getInstance().showProgressMessage(VerificationScreen.this, getString(R.string.please_wait));
        Map<String, String> map = new HashMap<>();
        map.put("mobile_number",mobileNumber);
        map.put("country_code","+"+countryCode);
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
                        if(resendButtonCount>=2){
                            binding.rlResendOtp.setVisibility(View.GONE);
                        }
                        else {
                            binding.rlResendOtp.setVisibility(View.VISIBLE);
                            binding.tvTimer.setVisibility(View.VISIBLE);
                            binding.resendOtp.setVisibility(View.GONE);
                            startTimer();
                        }


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

    private void sendVerificationCodeOnWhatsApp(String mobileNumber,String countryCode) {
        DataManager.getInstance().showProgressMessage(VerificationScreen.this, getString(R.string.please_wait));
        Map<String, String> map = new HashMap<>();
        map.put("mobile_number",mobileNumber);
        map.put("country_code","+"+countryCode);
        Log.e("OtpScreen", " Otp WhatsApp Request ==="+ map);
        Call<ResponseBody> loginCall = apiInterface.sendWhatsAppOtpApi(map);

        loginCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                DataManager.getInstance().hideProgressMessage();

                try {

                    Log.e("Otp WhatsApp response===", response.body().toString());
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
        map.put("country_code","+"+countryCode);
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
                        SignUpAPi();

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




    private void SignUpAPi() {

        DataManager.getInstance().showProgressMessage
                (VerificationScreen.this, getString(R.string.please_wait));

        Map<String, String> map = new HashMap<>();
        map.put("user_name",userName);
        map.put("name", name);
        map.put("email", email);
        map.put("password", password);
        map.put("mobile", mobile);
        map.put("country_code", countryCode);
        map.put("country", "");
        map.put("language", language);
        map.put("type", type);
        map.put("register_id", PreferenceConnector.readString(VerificationScreen.this,
                PreferenceConnector.Firebash_Token, ""));

        Log.e("MapMap", "Signup REQUEST" + map);

        Call<SignupModel> SignupCall = apiInterface.signup(map);

        SignupCall.enqueue(new Callback<SignupModel>() {
            @Override
            public void onResponse(Call<SignupModel> call, Response<SignupModel> response) {
                DataManager.getInstance().hideProgressMessage();

                try {
                    SignupModel data = response.body();
                    String dataResponse = new Gson().toJson(response.body());
                    Log.e("MapMap", "Signup RESPONSE" + dataResponse);

                    if (data.status.equals("1")) {

                        String user_id = data.result.id;
                        String moble_no = data.result.mobile;
                        String firstName = data.result.getName();
                        String email1 = data.result.email;
                        String password = data.result.password;
                        String otp = data.result.otp;
                        String lang = data.getResult().getLanguage();
                        String token = data.result.getAccessToken();
                        String username = data.result.userName;
                        String img = data.result.image;
                        //  String countryCode = data.result.image;
                        //  Toast.makeText(SignUpActivity.this, data.message, Toast.LENGTH_SHORT).show();

                        PreferenceConnector.writeString(VerificationScreen.this, PreferenceConnector.LoginStatus, "true");
                        PreferenceConnector.writeString(VerificationScreen.this, PreferenceConnector.User_id, user_id);
                        PreferenceConnector.writeString(VerificationScreen.this, PreferenceConnector.User_email, email1);
                        PreferenceConnector.writeString(VerificationScreen.this, PreferenceConnector.User_Mobile, moble_no);
                        PreferenceConnector.writeString(VerificationScreen.this, PreferenceConnector.Password, password);
                        PreferenceConnector.writeString(VerificationScreen.this, PreferenceConnector.User_First_name, firstName);
                        PreferenceConnector.writeString(VerificationScreen.this, PreferenceConnector.User_name, username);
                        PreferenceConnector.writeString(VerificationScreen.this, PreferenceConnector.User_img, img);
                        PreferenceConnector.writeString(VerificationScreen.this, PreferenceConnector.access_token, token);
                        PreferenceConnector.writeString(VerificationScreen.this, PreferenceConnector.Register_id, data.getResult().getRegisterId());
                        PreferenceConnector.writeString(VerificationScreen.this, PreferenceConnector.LANGUAGE, lang);
                       // Toast.makeText(VerificationScreen.this,getString(R.string.), Toast.LENGTH_SHORT).show();


                        startActivity(new Intent(VerificationScreen.this, HomeActivity.class)
                                .putExtra("status", "")
                                .putExtra("msg", "").addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP));
                        finish();



                    } else if (data.status.equals("0")) {
                        Toast.makeText(VerificationScreen.this, data.message, Toast.LENGTH_SHORT).show();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<SignupModel> call, Throwable t) {
                call.cancel();
                DataManager.getInstance().hideProgressMessage();
                // Toast.makeText(SignUpActivity.this, "Email Already Exist", Toast.LENGTH_SHORT).show();
            }
        });
    }


}