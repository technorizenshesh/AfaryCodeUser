package com.my.afarycode.OnlineShopping;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;
import com.my.afarycode.OnlineShopping.Model.LoginModel;
import com.my.afarycode.OnlineShopping.constant.PreferenceConnector;
import com.my.afarycode.OnlineShopping.helper.DataManager;
import com.my.afarycode.OnlineShopping.helper.NetworkAvailablity;
import com.my.afarycode.R;
import com.my.afarycode.databinding.ActivityChangePasswordBinding;
import com.my.afarycode.ratrofit.AfaryCode;
import com.my.afarycode.ratrofit.ApiClient;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChangePasswordActivity extends AppCompatActivity {

    ActivityChangePasswordBinding binding;
    private AfaryCode apiInterface;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_change_password);
        apiInterface = ApiClient.getClient(this).create(AfaryCode.class);

        SetupUI();


    }

    private void SetupUI() {
        binding.changePassword.setOnClickListener(v -> {

            if (binding.newPassword.getText().toString().trim().isEmpty()) {
                binding.newPassword.setError(getString(R.string.can_not_be_empty));
                Toast.makeText(this, getString(R.string.please_enter_password), Toast.LENGTH_SHORT).show();
            } else if (binding.repeatPassword.getText().toString().trim().isEmpty()) {
                binding.repeatPassword.setError(getString(R.string.can_not_be_empty));
                Toast.makeText(this, getString(R.string.please_enter_confirm_password), Toast.LENGTH_SHORT).show();
            } else if (!binding.repeatPassword.getText().toString()
                    .equals(binding.newPassword.getText().toString().trim().isEmpty())) {
                binding.repeatPassword.setError(getString(R.string.can_not_be_empty));
                Toast.makeText(this, getString(R.string.password_should_be_same), Toast.LENGTH_SHORT).show();

            } else {

                if(NetworkAvailablity.checkNetworkStatus(ChangePasswordActivity.this)) ChangePasswordAPI();
                else Toast.makeText(ChangePasswordActivity.this, getString(R.string.network_failure), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void ChangePasswordAPI() {

        DataManager.getInstance().showProgressMessage(this, getString(R.string.please_wait));
        Map<String, String> map = new HashMap<>();
        map.put("user_id", PreferenceConnector.readString(this, PreferenceConnector.User_id, ""));
        map.put("password", binding.newPassword.getText().toString());

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

                        startActivity(new Intent(ChangePasswordActivity.this, LoginActivity.class)
                                .putExtra("status", "login3"));

                    } else if (jsonObject.getString("status").equals("0")) {
                        Toast.makeText(ChangePasswordActivity.this, jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
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