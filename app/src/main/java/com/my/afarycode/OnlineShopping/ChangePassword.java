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
                Toast.makeText(getContext(), "Please enter old password ", Toast.LENGTH_SHORT).show();
            }
            else if (binding.newPassword.getText().toString().trim().isEmpty()) {
                binding.newPassword.setError("Field cannot be empty");
                Toast.makeText(getContext(), "Please enter password ", Toast.LENGTH_SHORT).show();
            } else if (binding.repeatPassword.getText().toString().trim().isEmpty()) {
                binding.repeatPassword.setError("Field cannot be empty");
                Toast.makeText(getContext(), "Please enter password ", Toast.LENGTH_SHORT).show();
            }



            else if (!binding.repeatPassword.getText().toString().equals(binding.newPassword.getText().toString())) {
                binding.repeatPassword.setError("Field cannot be empty");
                Toast.makeText(getContext(), "Password Should be Same", Toast.LENGTH_SHORT).show();

            } else {
                ChangePasswordAPI();
            }
        });
    }

    private void ChangePasswordAPI() {

        DataManager.getInstance().showProgressMessage(getActivity(), "Please wait...");
        Map<String,String> headerMap = new HashMap<>();
        headerMap.put("Authorization","Bearer " +PreferenceConnector.readString(getActivity(), PreferenceConnector.access_token,""));
        headerMap.put("Accept","application/json");
        HashMap<String,String> map = new HashMap<>();
        map.put("user_id", PreferenceConnector.readString(getContext(), PreferenceConnector.User_id, ""));
        map.put("old_password",binding.oldPassword.getText().toString());
        map.put("new_password",binding.newPassword.getText().toString());
        map.put("register_id", PreferenceConnector.readString(getActivity(), PreferenceConnector.Register_id, ""));



        Log.e("MapMap", "Change Password REQUEST" + map);
        Call<ResponseBody> loginCall = apiInterface.changePasswordRepo(headerMap,map);

        loginCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                DataManager.getInstance().hideProgressMessage();

                try {
                    String stringResponse = response.body().string();
                    JSONObject jsonObject = new JSONObject(stringResponse);
                    Log.e("MapMap", "Change Password RESPONSE" + stringResponse);
                    if (jsonObject.getString("status").equals("1")) {
                       // startActivity(new Intent(getContext(), LoginActivity.class)
                            ///    .putExtra("status", "login3"));
                        Toast.makeText(getContext(), getString(R.string.password_successfully_changes), Toast.LENGTH_SHORT).show();
                     //   getActivity().onBackPressed();

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