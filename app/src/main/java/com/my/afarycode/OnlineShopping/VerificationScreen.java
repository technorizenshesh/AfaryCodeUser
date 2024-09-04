package com.my.afarycode.OnlineShopping;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.google.android.gms.auth.api.phone.SmsRetriever;
import com.google.android.gms.auth.api.phone.SmsRetrieverClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.gson.Gson;
import com.my.afarycode.OnlineShopping.Model.CheckOtp;
import com.my.afarycode.OnlineShopping.Model.ForgotPasswordModal;
import com.my.afarycode.OnlineShopping.constant.PreferenceConnector;
import com.my.afarycode.OnlineShopping.helper.DataManager;
import com.my.afarycode.OnlineShopping.helper.MySMSBroadcastReceiver;
import com.my.afarycode.R;
import com.my.afarycode.databinding.ActivityVerificationScreenBinding;
import com.my.afarycode.ratrofit.AfaryCode;
import com.my.afarycode.ratrofit.ApiClient;
import com.my.afarycode.readotp.AppSignatureHashHelper;
import com.my.afarycode.readotp.SMSReceiver;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class VerificationScreen extends AppCompatActivity {
    ActivityVerificationScreenBinding binding;
    private AfaryCode apiInterface;
    String userId="",mobile="",countryCode="";
    public static final String TAG = VerificationScreen.class.getSimpleName();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_verification_screen);
        apiInterface = ApiClient.getClient(this).create(AfaryCode.class);
        SetupUI();
    }



    private void SetupUI() {



        SmsRetrieverClient client = SmsRetriever.getClient(this /* context */);

// Starts SmsRetriever, which waits for ONE matching SMS message until timeout
// (5 minutes). The matching SMS message will be sent via a Broadcast Intent with
// action SmsRetriever#SMS_RETRIEVED_ACTION.
        Task<Void> task = client.startSmsRetriever();

// Listen for success/failure of the start Task. If in a background thread, this
// can be made blocking using Tasks.await(task, [timeout]);
        task.addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                // Successfully started retriever, expect broadcast intent
                // ...
            }
        });

        task.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                // Failed to start retriever, inspect Exception for more details
                // ...
            }
        });




        if(getIntent()!=null){
            userId = getIntent().getStringExtra("user_id");
            mobile = getIntent().getStringExtra("mobile");
           countryCode = "+"+getIntent().getStringExtra("countryCode");

            binding.description.setText(getString(R.string.otp_text) + " " + mobile +" " + getString(R.string.with_6_digit));
        }


      //  mobile = "9111276286";
     //   countryCode = "+91";

      //  binding.description.setText(getString(R.string.otp_text) + " " + mobile +" " + getString(R.string.with_6_digit));

        binding.description.setText(getString(R.string.otp_text1)+ " " + mobile + " " + getString(R.string.otp_text2));


        binding.btnVerify.setOnClickListener(v -> {
            if(binding.Otp.getOTP().equals("")){
                Toast.makeText(VerificationScreen.this,getString(R.string.enter_otp),Toast.LENGTH_LONG).show();
            }
            else {
                VerificationAPI();
            }

        });

        binding.resendOtp.setOnClickListener(v -> {
            sendVerificationCode(mobile,countryCode);
        });

        sendVerificationCode(mobile,countryCode);


        MySMSBroadcastReceiver receiver = new MySMSBroadcastReceiver();
        IntentFilter filter = new IntentFilter(SmsRetriever.SMS_RETRIEVED_ACTION);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            registerReceiver(receiver, filter, Context.RECEIVER_EXPORTED);
        }
        else {
            registerReceiver(receiver, filter);

        }

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