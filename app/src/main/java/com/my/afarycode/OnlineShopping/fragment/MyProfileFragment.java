package com.my.afarycode.OnlineShopping.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.my.afarycode.OnlineShopping.ChangePassword;
import com.my.afarycode.OnlineShopping.OrderHistoryScreen;
import com.my.afarycode.OnlineShopping.helper.DataManager;
import com.my.afarycode.OnlineShopping.myorder.MyOrderScreen;
import com.my.afarycode.OnlineShopping.PrivacyPolicy;
import com.my.afarycode.OnlineShopping.TermsCondition;
import com.my.afarycode.OnlineShopping.UpdateProfile;
import com.my.afarycode.OnlineShopping.WishListActivity;
import com.my.afarycode.OnlineShopping.constant.PreferenceConnector;
import com.my.afarycode.R;
import com.my.afarycode.Splash;
import com.my.afarycode.databinding.FragmentMyprofileBinding;
import com.my.afarycode.ratrofit.AfaryCode;
import com.my.afarycode.ratrofit.ApiClient;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class MyProfileFragment extends Fragment {
public String TAG ="MyProfileFragment";
    FragmentMyprofileBinding binding;
    Fragment fragment;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_myprofile, container, false);

        binding.RRback.setOnClickListener(v -> {
            getFragmentManager().popBackStack();
        });


        binding.txtMyOrder.setOnClickListener(v -> {

            startActivity(new Intent(getActivity(), OrderHistoryScreen.class));

        });


        binding.RRLogout.setOnClickListener(v -> {
            Logout(PreferenceConnector.readString(getActivity(),PreferenceConnector.User_id,""),getActivity());
        });

        binding.txtWishList.setOnClickListener(v -> {

            fragment = new WishListActivity();
            loadFragment(fragment);


        });

        binding.txtChangePassword.setOnClickListener(v -> {
            fragment = new ChangePassword();
            loadFragment(fragment);

        });

        binding.txtUpdate.setOnClickListener(v -> {
            fragment = new UpdateProfile();
            loadFragment(fragment);

        });


        binding.txtOnlineOrder.setOnClickListener(v -> {
            startActivity(new Intent(getActivity(), MyOrderScreen.class));

        });


        binding.txtPrivacy.setOnClickListener(v -> {
            startActivity(new Intent(getActivity(), PrivacyPolicy.class));

        });




        binding.txtTerms.setOnClickListener(v -> {
            startActivity(new Intent(getActivity(), TermsCondition.class));
        });


        return binding.getRoot();

    }


    public boolean loadFragment(Fragment fragment) {
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        if (fragment != null) {
            fragmentManager.beginTransaction()
                    .addToBackStack("Home")
                    .replace(R.id.fragment_homeContainer, fragment)//, tag)
                    .commit();
            return true;
        }
        return false;
    }



    public  void Logout(String id, Context context) {
        DataManager.getInstance().showProgressMessage(getActivity(),"Please wait...");

        AfaryCode apiInterface = ApiClient.getClient(context.getApplicationContext()).create(AfaryCode.class);
        Map<String, String> map = new HashMap<>();
        map.put("user_id",id);
        Log.e(TAG,"User Logout Request "+map);
        Call<ResponseBody> loginCall = apiInterface.logoutApi(map);
        loginCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                DataManager.getInstance().hideProgressMessage();
                try {
                    String stringResponse = response.body().string();
                    JSONObject jsonObject = new JSONObject(stringResponse);

                    if(jsonObject.getString("status").equals("1")){
                        logttt();
                    }
                    else if(jsonObject.getString("status").equals("0")){
                        //App.showToast(context,"data not available", Toast.LENGTH_SHORT);
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

    private  void logttt() {
        PreferenceConnector.writeString(getActivity(), PreferenceConnector.LoginStatus, "false");
        startActivity(new Intent(getActivity(), Splash.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP));
        getActivity().finish();
    }


}