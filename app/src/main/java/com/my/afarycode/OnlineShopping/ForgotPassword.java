package com.my.afarycode.OnlineShopping;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.gson.Gson;
import com.my.afarycode.OnlineShopping.Model.ForgotPasswordModal;
import com.my.afarycode.OnlineShopping.constant.PreferenceConnector;
import com.my.afarycode.OnlineShopping.helper.DataManager;
import com.my.afarycode.R;
import com.my.afarycode.databinding.ActivityForgotPasswordBinding;
import com.my.afarycode.ratrofit.AfaryCode;
import com.my.afarycode.ratrofit.ApiClient;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ForgotPassword extends AppCompatActivity {

    ActivityForgotPasswordBinding binding;
    private AfaryCode apiInterface;
    private FirebaseAuth mAuth;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    private String mVerificationId;
    private PhoneAuthProvider.ForceResendingToken mResendToken;
    private String code;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_forgot_password);
        apiInterface = ApiClient.getClient(this).create(AfaryCode.class);
        SetupUI();
    }


    private void SetupUI() {

        binding.RRLogin.setOnClickListener(v -> {

            code = binding.ccp.getSelectedCountryCode();
            Log.e("code>>>", code);

            if (binding.edtmobile.getText().toString().trim().isEmpty()) {
                binding.edtmobile.setError("Field cannot be empty");

                Toast.makeText(ForgotPassword.this, "Please Enter Valid Mobile", Toast.LENGTH_SHORT).show();

            } else {
                ForgotPasswordAPI();
            }
        });
    }

    private void ForgotPasswordAPI() {

        DataManager.getInstance().showProgressMessage(ForgotPassword.this, "Please wait...");
        Map<String, String> map = new HashMap<>();
        map.put("number", "+" + code + binding.edtmobile.getText().toString());

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

                        Toast.makeText(ForgotPassword.this, "" + data.status, Toast.LENGTH_SHORT).show();
                        PreferenceConnector.writeString(ForgotPassword.this, PreferenceConnector.User_id, data.result.id);
                        startActivity(new Intent(ForgotPassword.this, VerificationScreen.class)
                                .putExtra("user_id", data.result.id)
                                .putExtra("user_email", data.result.email)
                                .putExtra("otp", data.result.otp)
                                .putExtra("mobile", "+" + code + binding.edtmobile.getText().toString()));


                    } else if (data.status.equals("0")) {
                        Toast.makeText(ForgotPassword.this, "Mobile Number Already Exist ", Toast.LENGTH_SHORT).show();
                        Toast.makeText(ForgotPassword.this, data.message, Toast.LENGTH_SHORT).show();
                    }

                } catch (Exception e) {
                    Toast.makeText(ForgotPassword.this, "Mobile Number Already Exist ", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ForgotPasswordModal> call, Throwable t) {
                call.cancel();
                Toast.makeText(ForgotPassword.this, "Mobile Number Already Exist ", Toast.LENGTH_SHORT).show();
                DataManager.getInstance().hideProgressMessage();
            }
        });
    }
}