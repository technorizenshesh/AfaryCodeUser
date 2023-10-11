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

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import okhttp3.ResponseBody;
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

                Toast.makeText(ForgotPassword.this, "Please Enter Valid Email", Toast.LENGTH_SHORT).show();

            } else {
                ForgotPasswordAPI(binding.edtmobile.getText().toString());
              //  startActivity(new Intent(ForgotPassword.this, VerificationScreen.class)
                  //      .putExtra("user_email", binding.edtmobile.getText().toString()));


            }
        });
    }




    private void ForgotPasswordAPI(String user_email) {

        DataManager.getInstance().showProgressMessage(ForgotPassword.this, "Please wait...");
        Map<String, String> map = new HashMap<>();
        map.put("email", user_email);
        Log.e("MapMap", "FORGOT PASSWORD REQUEST" + map);
        Call<ResponseBody> loginCall = apiInterface.forgot_passwordNew(map);

        loginCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                DataManager.getInstance().hideProgressMessage();

                try {

                    Log.e("response===",response.body().toString());
                    String stringResponse = response.body().string();
                    JSONObject jsonObject = new JSONObject(stringResponse);



                    if (jsonObject.getString("status").equals("1")) {

                        Toast.makeText(ForgotPassword.this, "Please check your email we have sent a link to your email address", Toast.LENGTH_SHORT).show();
                        finish();

                    } else if (jsonObject.getString("status").equals("0")) {
                        Toast.makeText(ForgotPassword.this, jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
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