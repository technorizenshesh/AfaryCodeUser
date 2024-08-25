package com.my.afarycode.OnlineShopping;

import android.content.Intent;
import android.content.IntentFilter;
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
import com.my.afarycode.R;
import com.my.afarycode.databinding.ActivityVerificationScreenBinding;
import com.my.afarycode.ratrofit.AfaryCode;
import com.my.afarycode.ratrofit.ApiClient;
import com.my.afarycode.readotp.AppSignatureHashHelper;
import com.my.afarycode.readotp.SMSReceiver;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class VerificationScreen extends AppCompatActivity implements SMSReceiver.OTPReceiveListener {
    ActivityVerificationScreenBinding binding;
    private AfaryCode apiInterface;
    private EditText edit1;
    private EditText edit2;
    private String user_id;
    private String user_email;
    private String finalOtp;
    private String edit_string1;
    private String edit_string2;
    private String edit_string3;
    private String edit_string4;
    private String final_otp;
    private String mobile;
    private String edit_string5;
    private String edit_string6;
    private String id;
    private FirebaseAuth mAuth;

    public static final String TAG = VerificationScreen.class.getSimpleName();

    private SMSReceiver smsReceiver;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if (extras == null) {
                user_id = null;
            } else {
              //  user_id = extras.getString("user_id");
                user_email = extras.getString("user_email");
              //  finalOtp = extras.getString("otp");
               // mobile = extras.getString("mobile");
            }
        } /*else {
            user_email = (String) savedInstanceState.getSerializable("user_email");
        }*/

        binding = DataBindingUtil.setContentView(this, R.layout.activity_verification_screen);
        mAuth = FirebaseAuth.getInstance();
        apiInterface = ApiClient.getClient(this).create(AfaryCode.class);

        AppSignatureHashHelper appSignatureHashHelper = new AppSignatureHashHelper(this);

        // This code requires one time to get Hash keys do comment and share key
        Log.i(TAG, "HashKey: " + appSignatureHashHelper.getAppSignatures().get(0));

        startSMSListener();

        sendVerificationCode();

        SetupUI();
    }

     private void sendVerificationCode() {

        binding.description.setText(getString(R.string.otp_text1)+ " " + mobile + " " + getString(R.string.otp_text2));

        new CountDownTimer(60000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                binding.resendOtp.setText("" + millisUntilFinished / 1000);
                binding.resendOtp.setEnabled(false);
            }

            @Override
            public void onFinish() {
                binding.resendOtp.setText("resend");
                binding.resendOtp.setEnabled(true);
            }
        }.start();

       /* PhoneAuthProvider.getInstance().verifyPhoneNumber(mobile.replace(" ", "")
                , 60, TimeUnit.SECONDS, this,
                new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

                    @Override
                    public void onCodeSent(@NonNull String id, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                        VerificationScreen.this.id = id;
                        Toast.makeText(VerificationScreen.this, "Please enter 6 digit verification code", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                        Toast.makeText(VerificationScreen.this, "" + phoneAuthCredential.getSmsCode(), Toast.LENGTH_SHORT).show();
                        signInWithPhoneAuthCredential(phoneAuthCredential);

                    }

                    @Override
                    public void onVerificationFailed(@NonNull FirebaseException e) {
                        // ProjectUtil.pauseProgressDialog();
                        Toast.makeText(VerificationScreen.this, "Failed" + e, Toast.LENGTH_SHORT).show();
                    }

                });        */// OnVerificationStateChangedCallbacks
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            startActivity(new Intent(VerificationScreen.this, ChangePasswordActivity.class)
                                    .putExtra("user_id", user_id)
                                    .putExtra("status", "login3"));

                            Toast.makeText(VerificationScreen.this, "Otp  Match SuccessFully ", Toast.LENGTH_SHORT).show();
                        } else {
                            // ProjectUtil.pauseProgressDialog();
                            Toast.makeText(VerificationScreen.this, "Otp Not match.", Toast.LENGTH_SHORT).show();

                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {

                            }

                        }
                    }
                });
           }

    private void SetupUI() {

     /*   EditText[] edit = {binding.edit1, binding.edit2, binding.edit3, binding.edit4, binding.edit5, binding.edit6};

        binding.edit1.addTextChangedListener(new GenericTextWatcher(binding.edit1, edit));
        binding.edit2.addTextChangedListener(new GenericTextWatcher(binding.edit2, edit));
        binding.edit3.addTextChangedListener(new GenericTextWatcher(binding.edit3, edit));
        binding.edit4.addTextChangedListener(new GenericTextWatcher(binding.edit4, edit));
        binding.edit5.addTextChangedListener(new GenericTextWatcher(binding.edit5, edit));
        binding.edit6.addTextChangedListener(new GenericTextWatcher(binding.edit6, edit));*/


        binding.RRBook.setOnClickListener(v -> {

         /*   edit_string1 = binding.edit1.getText().toString();
            edit_string2 = binding.edit2.getText().toString();
            edit_string3 = binding.edit3.getText().toString();
            edit_string4 = binding.edit4.getText().toString();
            edit_string5 = binding.edit5.getText().toString();
            edit_string6 = binding.edit6.getText().toString();

            final_otp = edit_string1 + edit_string2 + edit_string3 + edit_string4 + edit_string5 + edit_string6;

            Log.e("final_otp>>", final_otp);

            PhoneAuthCredential credential = PhoneAuthProvider.getCredential(id, final_otp);

            signInWithPhoneAuthCredential(credential);*/
        });

        binding.resendOtp.setOnClickListener(v -> {
            ForgotPasswordAPI(user_email);
        });
    }

    private void ForgotPasswordAPI(String user_email) {

        DataManager.getInstance().showProgressMessage(VerificationScreen.this, "Please wait...");
        Map<String, String> map = new HashMap<>();
        map.put("email", user_email);
        Log.e("MapMap", "LOGIN REQUEST" + map);
        Call<ForgotPasswordModal> loginCall = apiInterface.forgot_password(map);

        loginCall.enqueue(new Callback<ForgotPasswordModal>() {
            @Override
            public void onResponse(Call<ForgotPasswordModal> call, Response<ForgotPasswordModal> response) {
                DataManager.getInstance().hideProgressMessage();

                try {

                    ForgotPasswordModal data = response.body();
                    String dataResponse = new Gson().toJson(response.body());
                    Log.e("MapMap", "LOGIN RESPONSE" + dataResponse);

                    if (data.status.equals("1")) {

                        Toast.makeText(VerificationScreen.this, "" + data.status, Toast.LENGTH_SHORT).show();

                    } else if (data.status.equals("0")) {
                        Toast.makeText(VerificationScreen.this, data.message, Toast.LENGTH_SHORT).show();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ForgotPasswordModal> call, Throwable t) {
                call.cancel();
                DataManager.getInstance().hideProgressMessage();
            }
        });
    }

    private void VerificationAPI() {

        DataManager.getInstance().showProgressMessage(VerificationScreen.this, "Please wait...");
        Map<String, String> map = new HashMap<>();
        map.put("user_id", user_id);
        map.put("otp", final_otp);

        Log.e("MapMap", "LOGIN REQUEST" + map);
        Call<CheckOtp> SignupCall = apiInterface.check_otp(map);
        SignupCall.enqueue(new Callback<CheckOtp>() {
            @Override
            public void onResponse(Call<CheckOtp> call, Response<CheckOtp> response) {
                DataManager.getInstance().hideProgressMessage();

                try {
                    CheckOtp data = response.body();
                    String dataResponse = new Gson().toJson(response.body());
                    Log.e("MapMap", "LOGIN RESPONSE" + dataResponse);

                    if (data.status.equals("1")) {

                        String user_id = data.result.id;
                        String moble_no = data.result.mobile;
                        String firstName = data.result.userName;
                        String email1 = data.result.email;
                        String password = data.result.password;

                        Toast.makeText(VerificationScreen.this, data.message, Toast.LENGTH_SHORT).show();


                    } else if (data.status.equals("0")) {
                        Toast.makeText(VerificationScreen.this, data.message, Toast.LENGTH_SHORT).show();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<CheckOtp> call, Throwable t) {
                call.cancel();
                DataManager.getInstance().hideProgressMessage();
            }
        });
    }

    @Override
    public void onOTPReceived(String otp) {

        if(otp.contains(":") && otp.contains(".")) {
             finalOtp = otp.substring( otp.indexOf(":")+1 , otp.indexOf(".") ).trim();
            //otpTextView.setOTP(otp);
            Toast.makeText(VerificationScreen.this,"The OTP is " + finalOtp, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onOTPTimeOut() {

    }

    @Override
    public void onOTPReceivedError(String error) {

    }

    private void startSMSListener() {
        try {
            smsReceiver = new SMSReceiver();
            smsReceiver.setOTPListener(this);

            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction(SmsRetriever.SMS_RETRIEVED_ACTION);
            this.registerReceiver(smsReceiver, intentFilter);

            SmsRetrieverClient client = SmsRetriever.getClient(this);

            Task<Void> task = client.startSmsRetriever();
            task.addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    // API successfully started
                }
            });

            task.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    // Fail to start API
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}