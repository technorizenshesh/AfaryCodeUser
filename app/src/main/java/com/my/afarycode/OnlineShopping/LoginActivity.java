package com.my.afarycode.OnlineShopping;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.google.gson.Gson;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.my.afarycode.OnlineShopping.Model.LoginModel;
import com.my.afarycode.OnlineShopping.bottomsheet.WebViewBottomSheet;
import com.my.afarycode.OnlineShopping.constant.PreferenceConnector;
import com.my.afarycode.OnlineShopping.helper.DataManager;
import com.my.afarycode.OnlineShopping.listener.AskListener;
import com.my.afarycode.R;
import com.my.afarycode.databinding.ActivityLoginBinding;
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

public class LoginActivity extends AppCompatActivity implements AskListener {

    ActivityLoginBinding binding;
    private AfaryCode apiInterface;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_login);
        apiInterface = ApiClient.getClient(this).create(AfaryCode.class);

        binding.txtRegister.setOnClickListener(v -> {
            //startActivity(new Intent(LoginActivity.this, SignUpActivity.class));

            startActivity(new Intent(this, AskAct.class));
            finish();
        });

        SetupUI();

        binding.txtForogtPassword.setOnClickListener(v -> {
            startActivity(new Intent(LoginActivity.this, ForgotPassword.class));
        });


        binding.txtTerms.setOnClickListener(v ->
               // startActivity(new Intent(LoginActivity.this, WebViewAct.class)
              //          .putExtra("url", Constant.TERMS_AND_CONDITIONS)
                //        .putExtra("title",getString(R.string.terms_and_conditions))));
                new WebViewBottomSheet(Constant.TERMS_AND_CONDITIONS,getString(R.string.terms_and_conditions)).callBack(this::ask).show(getSupportFragmentManager(),""));


    }

    private void SetupUI() {


        if(getIntent()!=null){
            if(getIntent().getStringExtra("type").equalsIgnoreCase("Become a Customer")){
                binding.tvTitle.setVisibility(View.VISIBLE);
            }
            else  binding.tvTitle.setVisibility(View.GONE);

        }



        binding.RRLogin.setOnClickListener(v -> {
            if (binding.edtEmail.getText().toString().trim().isEmpty()) {
                binding.edtEmail.setError("Field cannot be empty");
                Toast.makeText(LoginActivity.this, "please enter valid email", Toast.LENGTH_SHORT).show();
            } else if (binding.password.getText().toString().trim().isEmpty()) {
                binding.password.setError("Field cannot be empty");
                Toast.makeText(LoginActivity.this, "please enter password ", Toast.LENGTH_SHORT).show();
            } else {
                LoginAPi();
            }
        });
    }

      private void LoginAPi() {
        DataManager.getInstance().showProgressMessage(LoginActivity.this, "Please wait...");
        Map<String, String> map = new HashMap<>();
        map.put("email", binding.edtEmail.getText().toString());
        map.put("password", binding.password.getText().toString());
        map.put("lat", "");
        map.put("lon", "");
        map.put("register_id", PreferenceConnector.readString(LoginActivity.this, PreferenceConnector.Firebash_Token, ""));
        map.put("type", "User");
        map.put("country_code",binding.ccp.getSelectedCountryCode()+"");

        Log.e("MapMap", "LOGIN REQUEST" + map);
        Call<ResponseBody> loginCall = apiInterface.login(map);

        loginCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                DataManager.getInstance().hideProgressMessage();
                try {

                    String stringResponse = response.body().string();
                    JSONObject jsonObject = new JSONObject(stringResponse);
                    Log.e("MapMap", "LOGIN RESPONSE" + stringResponse);
                    if (jsonObject.getString("status").equals("1")) {
                        LoginModel data = new Gson().fromJson(stringResponse,LoginModel.class);
                        String user_id = data.result.id;
                        String moble_no = data.result.mobile;
                        String firstName = data.result.userName;
                        String email1 = data.result.email;
                        String password = data.result.password;

                        String username = data.result.userName;
                        String img = data.result.image;
                        String token = data.result.getAccessToken();



                        Toast.makeText(LoginActivity.this, "Login SuccessFully", Toast.LENGTH_SHORT).show();


                        PreferenceConnector.writeString(LoginActivity.this, PreferenceConnector.LoginStatus, "true");
                        PreferenceConnector.writeString(LoginActivity.this, PreferenceConnector.User_id, user_id);
                        PreferenceConnector.writeString(LoginActivity.this, PreferenceConnector.User_email, email1);
                        PreferenceConnector.writeString(LoginActivity.this, PreferenceConnector.User_Mobile, moble_no);
                        PreferenceConnector.writeString(LoginActivity.this, PreferenceConnector.Password, password);
                        PreferenceConnector.writeString(LoginActivity.this, PreferenceConnector.User_First_name, firstName);

                        PreferenceConnector.writeString(LoginActivity.this, PreferenceConnector.User_name, username);
                        PreferenceConnector.writeString(LoginActivity.this, PreferenceConnector.User_img, img);
                        PreferenceConnector.writeString(LoginActivity.this, PreferenceConnector.access_token, token);

                        PreferenceConnector.writeString(LoginActivity.this, PreferenceConnector.FROM, "splash");

                        PreferenceConnector.writeString(LoginActivity.this, PreferenceConnector.Register_id, data.getResult().getRegisterId());



                        startActivity(new Intent(LoginActivity.this, HomeActivity.class)
                                .putExtra("user_id", user_id)
                                .putExtra("status", "login").addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP));
                        finish();


                    } else if (jsonObject.getString("status").equals("0")) {
                        Toast.makeText(LoginActivity.this, jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
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

    }
}