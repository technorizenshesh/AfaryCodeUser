package com.my.afarycode.OnlineShopping;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.google.gson.Gson;
import com.my.afarycode.OnlineShopping.Model.SignupModel;
import com.my.afarycode.OnlineShopping.constant.PreferenceConnector;
import com.my.afarycode.OnlineShopping.helper.DataManager;
import com.my.afarycode.R;
import com.my.afarycode.databinding.ActivitySignUpBinding;
import com.my.afarycode.ratrofit.AfaryCode;
import com.my.afarycode.ratrofit.ApiClient;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SignUpActivity extends AppCompatActivity {

    ActivitySignUpBinding binding;
    private AfaryCode apiInterface;
    private String code;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_sign_up);
        apiInterface = ApiClient.getClient(this).create(AfaryCode.class);

        SetupUi();

    }

    private void SetupUi() {

        binding.RRSigUp.setOnClickListener(v -> {
            code = binding.ccp.getSelectedCountryCode();
            Log.e("code>>>", code);

            if(binding.name.getText().toString().trim().isEmpty()) {
                binding.name.setError(getString(R.string.can_not_be_empty));
                Toast.makeText(SignUpActivity.this, getString(R.string.please_enter_name), Toast.LENGTH_SHORT).show();
            }
           else if(binding.userName.getText().toString().trim().isEmpty()) {
                binding.userName.setError(getString(R.string.can_not_be_empty));
                Toast.makeText(SignUpActivity.this, getString(R.string.please_enter_user_name), Toast.LENGTH_SHORT).show();
            }

            else if (binding.email.getText().toString().trim().isEmpty()) {
                binding.email.setError(getString(R.string.can_not_be_empty));
                Toast.makeText(SignUpActivity.this, getString(R.string.please_enter_email), Toast.LENGTH_SHORT).show();
            } else if (binding.phone.getText().toString().trim().isEmpty()) {
                binding.phone.setError(getString(R.string.can_not_be_empty));
                Toast.makeText(SignUpActivity.this, getString(R.string.please_enter_mobile_number), Toast.LENGTH_SHORT).show();
            } else if (binding.password.getText().toString().trim().isEmpty()) {
                binding.password.setError(getString(R.string.can_not_be_empty));
                Toast.makeText(SignUpActivity.this, getString(R.string.please_enter_password), Toast.LENGTH_SHORT).show();
            } else if (!binding.password.getText().toString().equals(binding.confirmPassword.getText().toString())) {
                binding.confirmPassword.setError(getString(R.string.password_should_be_same));

            } else {
                SignUpAPi();
            }
          }
        );

        binding.txtLogin.setOnClickListener(v -> {
            startActivity(new Intent(SignUpActivity.this, LoginActivity.class).putExtra("type",""));
        });

    }

    private void SignUpAPi() {

        DataManager.getInstance().showProgressMessage
                (SignUpActivity.this, "Please wait...");

        Map<String, String> map = new HashMap<>();
        map.put("user_name", binding.userName.getText().toString());
        map.put("name", binding.name.getText().toString());
        map.put("email", binding.email.getText().toString().trim());
        map.put("password", binding.password.getText().toString());
        map.put("mobile",binding.phone.getText().toString());
        map.put("country_code",code);
        map.put("country","");

        map.put("register_id", PreferenceConnector.readString(SignUpActivity.this,
                PreferenceConnector.Firebash_Token, ""));

        map.put("type", "User");

        Log.e("MapMap", "LOGIN REQUEST" + map);

        Call<SignupModel> SignupCall = apiInterface.signup(map);

        SignupCall.enqueue(new Callback<SignupModel>() {
            @Override
            public void onResponse(Call<SignupModel> call, Response<SignupModel> response) {
                DataManager.getInstance().hideProgressMessage();

                try {
                    SignupModel data = response.body();
                    String dataResponse = new Gson().toJson(response.body());
                    Log.e("MapMap", "LOGIN RESPONSE" + dataResponse);

                    if (data.status.equals("1")) {

                        String user_id = data.result.id;
                        String moble_no = data.result.mobile;
                        String firstName = data.result.getName();
                        String email1 = data.result.email;
                        String password = data.result.password;
                        String otp = data.result.otp;

                        Toast.makeText(SignUpActivity.this, data.message, Toast.LENGTH_SHORT).show();

                        startActivity(new Intent(SignUpActivity.this, HomeActivity.class)
                                .putExtra("status", "")
                                .putExtra("msg", ""));

                        PreferenceConnector.writeString(SignUpActivity.this, PreferenceConnector.LoginStatus, "true");
                        PreferenceConnector.writeString(SignUpActivity.this, PreferenceConnector.User_id, user_id);
                        PreferenceConnector.writeString(SignUpActivity.this, PreferenceConnector.User_email, email1);
                        PreferenceConnector.writeString(SignUpActivity.this, PreferenceConnector.User_Mobile, moble_no);
                        PreferenceConnector.writeString(SignUpActivity.this, PreferenceConnector.User_First_name, firstName);
                        PreferenceConnector.writeString(SignUpActivity.this, PreferenceConnector.Password, password);
                        PreferenceConnector.writeString(SignUpActivity.this, PreferenceConnector.OTP, otp);

                    } else if (data.status.equals("0")) {
                        Toast.makeText(SignUpActivity.this, data.message, Toast.LENGTH_SHORT).show();
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