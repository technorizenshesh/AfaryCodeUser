package com.my.afarycode.OnlineShopping;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

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
import com.my.afarycode.OnlineShopping.fragment.HomeFragment;
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

public class ChangePassword extends Fragment {

    ActivityChangePasswordBinding binding;
    private AfaryCode apiInterface;
    Fragment fragment;

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
                binding.oldPassword.setError(getString(R.string.can_not_be_empty));
                Toast.makeText(getContext(), getString(R.string.please_enter_old_password), Toast.LENGTH_SHORT).show();
            }
            else if (binding.newPassword.getText().toString().trim().isEmpty()) {
                binding.newPassword.setError(getString(R.string.can_not_be_empty));
                Toast.makeText(getContext(), getString(R.string.please_enter_password), Toast.LENGTH_SHORT).show();
            } else if (binding.repeatPassword.getText().toString().trim().isEmpty()) {
                binding.repeatPassword.setError(getString(R.string.can_not_be_empty));
                Toast.makeText(getContext(), getString(R.string.please_enter_confirm_password), Toast.LENGTH_SHORT).show();
            }

            else if (!binding.repeatPassword.getText().toString().equals(binding.newPassword.getText().toString())) {
                binding.repeatPassword.setError(getString(R.string.can_not_be_empty));
                Toast.makeText(getContext(), getString(R.string.password_should_be_same), Toast.LENGTH_SHORT).show();

            } else {

                if(NetworkAvailablity.checkNetworkStatus(requireActivity())) ChangePasswordAPI();
                else Toast.makeText(requireActivity(), getString(R.string.network_failure), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void ChangePasswordAPI() {

        DataManager.getInstance().showProgressMessage(getActivity(), getString(R.string.please_wait));
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
                        fragment = new HomeFragment();
                        loadFragment(fragment);
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


    public boolean loadFragment(Fragment fragment) {
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        if (fragment != null) {
            // Clear the back stack before adding a new fragment
            fragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);

            fragmentManager.beginTransaction()
                    .replace(R.id.fragment_homeContainer, fragment)
                    .addToBackStack("Home")
                    .commit();
            return true;
        }
        return false;
    }

}