package com.my.afarycode.OnlineShopping;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.gson.Gson;
import com.my.afarycode.OnlineShopping.Model.GetNotificationModal;
import com.my.afarycode.OnlineShopping.Model.GetViewModal;
import com.my.afarycode.OnlineShopping.Model.GetWishListModal;
import com.my.afarycode.OnlineShopping.Model.HomeShopeProductModel;
import com.my.afarycode.OnlineShopping.activity.CheckOutDeliveryAct;
import com.my.afarycode.OnlineShopping.adapter.MyWishListAdapter;
import com.my.afarycode.OnlineShopping.adapter.NotificationAdapter;
import com.my.afarycode.OnlineShopping.adapter.WalletAdapter;
import com.my.afarycode.OnlineShopping.constant.PreferenceConnector;
import com.my.afarycode.OnlineShopping.helper.DataManager;
import com.my.afarycode.R;
import com.my.afarycode.Splash;
import com.my.afarycode.databinding.ActivityNotificationScreenBinding;
import com.my.afarycode.ratrofit.AfaryCode;
import com.my.afarycode.ratrofit.ApiClient;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NotificationScreen extends AppCompatActivity {

    ActivityNotificationScreenBinding binding;

    NotificationAdapter mAdapter;
    private ArrayList<GetViewModal.Result> get_result = new ArrayList<>();
    private ArrayList<GetNotificationModal.Result> get_result1 = new ArrayList<>();
    private AfaryCode apiInterface;
    private NotificationAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_notification_screen);
        apiInterface = ApiClient.getClient(this).create(AfaryCode.class);

        binding.RRback.setOnClickListener(v -> {
            onBackPressed();
        });

       /// GetNotificationView();
        GetNotificationList();

        setAdapter();

    }

    private void GetNotificationList() {

        DataManager.getInstance().showProgressMessage(this, getString(R.string.please_wait));
        Map<String,String> headerMap = new HashMap<>();
        headerMap.put("Authorization","Bearer " +PreferenceConnector.readString(NotificationScreen.this, PreferenceConnector.access_token,""));
        headerMap.put("Accept","application/json");
        Map<String, String> map = new HashMap<>();
        map.put("user_id",PreferenceConnector.readString(NotificationScreen.this, PreferenceConnector.User_id, "") );
        map.put("register_id", PreferenceConnector.readString(NotificationScreen.this, PreferenceConnector.Register_id, ""));
        Log.e("MapMap", "Notification Request" + map);

        Call<ResponseBody> loginCall = apiInterface.notifi_list(headerMap,map);

        loginCall.enqueue(new Callback<ResponseBody>() {

            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                DataManager.getInstance().hideProgressMessage();

                try {
                    String stringResponse = response.body().string();
                    JSONObject jsonObject = new JSONObject(stringResponse);
                    Log.e("MapMap", "notification RESPONSE" + stringResponse);
                    if (jsonObject.getString("status").equals("1")) {
                        binding.tvNotFound.setVisibility(View.GONE);
                        get_result1.clear();
                        GetNotificationModal model = new Gson().fromJson(stringResponse,GetNotificationModal.class);
                        get_result1.addAll(model.getResult());
                        binding.recyclerNotification.setAdapter(new NotificationAdapter(NotificationScreen.this,get_result1));

                    }

                 else if (jsonObject.getString("status").equals("0")) {
                        binding.tvNotFound.setVisibility(View.VISIBLE);
                        Toast.makeText(NotificationScreen.this, jsonObject.getString("message").toString(), Toast.LENGTH_SHORT).show();                    }

                    else if (jsonObject.getString("status").equals("5")) {
                        PreferenceConnector.writeString(NotificationScreen.this, PreferenceConnector.LoginStatus, "false");
                        startActivity(new Intent(NotificationScreen.this, Splash.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP));
                        finish();
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



    private void GetNotificationView() {

        DataManager.getInstance().showProgressMessage(this, "Please wait...");
        Map<String,String> headerMap = new HashMap<>();
        headerMap.put("Authorization","Bearer " +PreferenceConnector.readString(NotificationScreen.this, PreferenceConnector.access_token,""));
        headerMap.put("Accept","application/json");

        Map<String, String> map = new HashMap<>();
        map.put("user_id", "8");

        Log.e("MapMap", "EXERSICE LIST" + map);

        Call<GetViewModal> loginCall = apiInterface.notifi_count_view(headerMap,map);
        loginCall.enqueue(new Callback<GetViewModal>() {

            @Override
            public void onResponse(Call<GetViewModal> call, Response<GetViewModal> response) {

                DataManager.getInstance().hideProgressMessage();

                try {

                    GetViewModal data = response.body();
                    String dataResponse = new Gson().toJson(response.body());
                    Log.e("MapMap", "Exersice_List" + dataResponse);


                    if (data.status.equals("1")) {

                        Log.e("view>>>>", "view!!!");

                    } else if (data.status.equals("0")) {
                        Toast.makeText(NotificationScreen.this, data.message, Toast.LENGTH_SHORT).show();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<GetViewModal> call, Throwable t) {
                call.cancel();
                DataManager.getInstance().hideProgressMessage();
            }
        });
    }

    private void setAdapter() {

/*
        mAdapter = new NotificationAdapter(NotificationScreen.this, modelList);
        binding.recyclerNotification.setHasFixedSize(true);
        // use a linear layout manager
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(NotificationScreen.this);
        binding.recyclerNotification.setLayoutManager(linearLayoutManager);
        binding.recyclerNotification.setAdapter(mAdapter);*/

    }
}