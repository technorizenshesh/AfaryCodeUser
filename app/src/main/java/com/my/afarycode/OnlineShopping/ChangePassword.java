package com.my.afarycode.OnlineShopping;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.gson.Gson;
import com.my.afarycode.OnlineShopping.Model.LoginModel;
import com.my.afarycode.OnlineShopping.constant.PreferenceConnector;
import com.my.afarycode.OnlineShopping.helper.DataManager;
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

public class ChangePassword extends Fragment {

    ActivityChangePasswordBinding binding;
    private AfaryCode apiInterface;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.activity_change_password, container, false);
        apiInterface = ApiClient.getClient(getContext()).create(AfaryCode.class);

        SetupUI();

        binding.RRback.setOnClickListener(v -> {
            getActivity().onBackPressed();

        });

        return binding.getRoot();
    }

    private void SetupUI() {

        binding.changePassword.setOnClickListener(v -> {

            if (binding.oldPassword.getText().toString().trim().isEmpty()) {
                binding.oldPassword.setError("Field cannot be empty");
                Toast.makeText(getContext(), "please enter old password ", Toast.LENGTH_SHORT).show();
            }
            else if (binding.newPassword.getText().toString().trim().isEmpty()) {
                binding.newPassword.setError("Field cannot be empty");
                Toast.makeText(getContext(), "please enter password ", Toast.LENGTH_SHORT).show();
            } else if (binding.repeatPassword.getText().toString().trim().isEmpty()) {
                binding.repeatPassword.setError("Field cannot be empty");
                Toast.makeText(getContext(), "please enter password ", Toast.LENGTH_SHORT).show();
            } else if (!binding.repeatPassword.getText().toString().equals(binding.newPassword.getText().toString().trim().isEmpty())) {
                binding.repeatPassword.setError("Field cannot be empty");
                Toast.makeText(getContext(), "Password Shuold Be Same", Toast.LENGTH_SHORT).show();

            } else {
                ChangePasswordAPI();
            }
        });
    }

    private void ChangePasswordAPI() {

        DataManager.getInstance().showProgressMessage(getActivity(), "Please wait...");
        Map<String, String> map = new HashMap<>();
        map.put("user_id", PreferenceConnector.readString(getContext(), PreferenceConnector.User_id, ""));
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
                        String user_id = data.result.id;
                       // startActivity(new Intent(getContext(), LoginActivity.class)
                            ///    .putExtra("status", "login3"));
                        Toast.makeText(getContext(), getString(R.string.password_successfully_changes), Toast.LENGTH_SHORT).show();


                    } else if (jsonObject.getString("status").equals("0")) {
                        Toast.makeText(getContext(), jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
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