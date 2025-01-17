package com.my.afarycode.OnlineShopping;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.gson.Gson;
import com.my.afarycode.OnlineShopping.Model.ForgotPasswordModal;
import com.my.afarycode.OnlineShopping.bottomsheet.SendAdminRequestBottomSheet;
import com.my.afarycode.OnlineShopping.bottomsheet.WebViewBottomSheet;
import com.my.afarycode.OnlineShopping.constant.PreferenceConnector;
import com.my.afarycode.OnlineShopping.helper.DataManager;
import com.my.afarycode.OnlineShopping.helper.NetworkAvailablity;
import com.my.afarycode.OnlineShopping.listener.AskListener;
import com.my.afarycode.R;
import com.my.afarycode.databinding.ActivityForgotPasswordBinding;
import com.my.afarycode.ratrofit.AfaryCode;
import com.my.afarycode.ratrofit.ApiClient;
import com.my.afarycode.ratrofit.Constant;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ForgotPassword extends AppCompatActivity implements AskListener {

    ActivityForgotPasswordBinding binding;
    private AfaryCode apiInterface;
    private String code;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_forgot_password);
        apiInterface = ApiClient.getClient(this).create(AfaryCode.class);
        SetupUI();
    }


    private void SetupUI() {

        binding.RRSend.setOnClickListener(v -> {

            //  code = binding.ccp.getSelectedCountryCode();
            //  Log.e("code>>>", code);

            if (binding.edEmail.getText().toString().trim().isEmpty()) {
                binding.edEmail.setError(getString(R.string.can_not_be_empty));

                Toast.makeText(ForgotPassword.this, getString(R.string.please_enter_valied_email), Toast.LENGTH_SHORT).show();

            } else {

                //  startActivity(new Intent(ForgotPassword.this, VerificationScreen.class)
                //      .putExtra("user_email", binding.edtmobile.getText().toString()));
                if(NetworkAvailablity.checkNetworkStatus(ForgotPassword.this)) ForgotPasswordAPI(binding.edEmail.getText().toString());
                else Toast.makeText(ForgotPassword.this, getString(R.string.network_failure), Toast.LENGTH_SHORT).show();

            }
        });


        binding.llEmail.setOnClickListener(v -> {
            binding.llEmail.setBackground(getDrawable(R.drawable.rounded_corner_stroke_10));
            binding.llSendAdmin.setBackground(getDrawable(R.drawable.rounded_corner_white_10));
        });


        binding.llSendAdmin.setOnClickListener(v -> {
                    binding.llEmail.setBackground(getDrawable(R.drawable.rounded_corner_white_10));
                    binding.llSendAdmin.setBackground(getDrawable(R.drawable.rounded_corner_stroke_10));
                    new SendAdminRequestBottomSheet(ForgotPassword.this).callBack(this::ask).show(getSupportFragmentManager(), "");

                }
        );



        binding.edEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // No action needed here
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Validate email and update button visibility
                if (isValidEmail(s.toString())) {
                    binding.RRSend.setVisibility(View.VISIBLE);
                } else {
                    binding.RRSend.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                // No action needed here
            }
        });



    }



    private boolean isValidEmail(CharSequence email) {
        // Check if the email is valid using Android's Patterns utility
        return Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }


    private void ForgotPasswordAPI(String user_email) {

        DataManager.getInstance().showProgressMessage(ForgotPassword.this, getString(R.string.please_wait));
        Map<String, String> map = new HashMap<>();
        map.put("email", user_email);
        map.put("user_type", "User");

        Log.e("MapMap", "FORGOT PASSWORD REQUEST" + map);
        Call<ResponseBody> loginCall = apiInterface.forgot_passwordNew(map);

        loginCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                DataManager.getInstance().hideProgressMessage();

                try {

                    Log.e("response===", response.body().toString());
                    String stringResponse = response.body().string();
                    JSONObject jsonObject = new JSONObject(stringResponse);


                    if (jsonObject.getString("status").equals("1")) {

                        Toast.makeText(ForgotPassword.this, getString(R.string.please_check_your_email_address), Toast.LENGTH_LONG).show();
                        finish();

                    } else if (jsonObject.getString("status").equals("0")) {
                        Toast.makeText(ForgotPassword.this, jsonObject.getString("message"), Toast.LENGTH_LONG).show();
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

    @Override
    public void ask(String value, String status) {
        finish();
    }
}