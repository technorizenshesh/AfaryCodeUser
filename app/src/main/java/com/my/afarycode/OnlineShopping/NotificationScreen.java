package com.my.afarycode.OnlineShopping;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

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
import com.my.afarycode.databinding.ActivityNotificationScreenBinding;
import com.my.afarycode.ratrofit.AfaryCode;
import com.my.afarycode.ratrofit.ApiClient;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

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

        GetNotificationView();
        GetNotificationList();

        setAdapter();

    }

    private void GetNotificationList() {

        DataManager.getInstance().showProgressMessage(this, "Please wait...");
        Map<String,String> headerMap = new HashMap<>();
        headerMap.put("Authorization","Bearer " +PreferenceConnector.readString(NotificationScreen.this, PreferenceConnector.access_token,""));
        headerMap.put("Accept","application/json");
        Map<String, String> map = new HashMap<>();
        map.put("user_id", "8");

        Log.e("MapMap", "EXERSICE LIST" + map);

        Call<GetNotificationModal> loginCall = apiInterface.notifi_list(headerMap,map);

        loginCall.enqueue(new Callback<GetNotificationModal>() {

            @Override
            public void onResponse(Call<GetNotificationModal> call, Response<GetNotificationModal> response) {

                DataManager.getInstance().hideProgressMessage();

                try {

                    GetNotificationModal data = response.body();
                    String dataResponse = new Gson().toJson(response.body());
                    Log.e("MapMap", "Exersice_List" + dataResponse);


                    if(data.status.equals("1")) {

                        get_result1.clear();
                        get_result1.addAll(data.result);

                        adapter = new NotificationAdapter(NotificationScreen.this, get_result1);
                        binding.recyclerNotification.setLayoutManager
                                (new LinearLayoutManager(NotificationScreen.this));

                        binding.recyclerNotification.setAdapter(adapter);

                    } else if (data.status.equals("0")) {
                        Toast.makeText(NotificationScreen.this, data.message, Toast.LENGTH_SHORT).show();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<GetNotificationModal> call, Throwable t) {
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