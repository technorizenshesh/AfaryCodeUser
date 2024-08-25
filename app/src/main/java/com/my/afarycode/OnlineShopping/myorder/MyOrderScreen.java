package com.my.afarycode.OnlineShopping.myorder;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;
import com.my.afarycode.OnlineShopping.Model.Get_Transaction_Modal;
import com.my.afarycode.OnlineShopping.activity.CheckOutDeliveryAct;
import com.my.afarycode.OnlineShopping.constant.PreferenceConnector;
import com.my.afarycode.OnlineShopping.helper.DataManager;
import com.my.afarycode.R;
import com.my.afarycode.Splash;
import com.my.afarycode.databinding.ActivityMyOrderScreenBinding;
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

public class MyOrderScreen extends AppCompatActivity implements OrderListener{
    public String TAG = "MyOrderScreen";
    ActivityMyOrderScreenBinding binding;
    OrderAdapter adapter ;
    private ArrayList<OrderModel.Result> arrayList;
    private AfaryCode apiInterface;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_my_order_screen);
        apiInterface = ApiClient.getClient(this).create(AfaryCode.class);
        initViews();


    }

    private void initViews() {
        arrayList = new ArrayList<>();


        adapter = new OrderAdapter(MyOrderScreen.this, arrayList,MyOrderScreen.this);
        binding.rvOrders.setAdapter(adapter);


        binding.RRback.setOnClickListener(v -> {
            onBackPressed();
        });
    }

    private void GetOrderHistoryAPI() {
        DataManager.getInstance().showProgressMessage(this, "Please wait...");
        Map<String,String> headerMap = new HashMap<>();
        headerMap.put("Authorization","Bearer " +PreferenceConnector.readString(MyOrderScreen.this, PreferenceConnector.access_token,""));
        headerMap.put("Accept","application/json");

        Map<String, String> map = new HashMap<>();
        map.put("user_id", PreferenceConnector.readString(MyOrderScreen.this, PreferenceConnector.User_id, ""));
        map.put("register_id", PreferenceConnector.readString(MyOrderScreen.this, PreferenceConnector.Register_id, ""));

        Log.e(TAG, "My Order Request" + map);
        Call<ResponseBody> loginCall = apiInterface.getAllOnlineOrderApi(headerMap,map);
        loginCall.enqueue(new Callback<ResponseBody>() {

            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                DataManager.getInstance().hideProgressMessage();

                try {


                    String stringResponse = response.body().string();
                    JSONObject jsonObject = new JSONObject(stringResponse);
                    Log.e("response===",response.body().toString());
                    if (jsonObject.getString("status").toString().equals("1")) {
                        // binding.tvNotFount.setVisibility(View.GONE);
                        OrderModel model = new Gson().fromJson(stringResponse,OrderModel.class);
                        arrayList.clear();
                        arrayList.addAll(model.getResult());
                        adapter.notifyDataSetChanged();
                    } else if (jsonObject.getString("status").toString().equals("0")) {
                        // binding.tvNotFount.setVisibility(View.VISIBLE);
                        Toast.makeText(MyOrderScreen.this, jsonObject.getString("message").toString(), Toast.LENGTH_SHORT).show();
                        arrayList.clear();
                        adapter.notifyDataSetChanged();
                    }

                    else if (jsonObject.getString("status").equals("5")) {
                        // Toast.makeText(getContext(), "No Data Found !!!!", Toast.LENGTH_SHORT).show();

                        PreferenceConnector.writeString(MyOrderScreen.this, PreferenceConnector.LoginStatus, "false");
                        startActivity(new Intent(MyOrderScreen.this, Splash.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP));
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

    @Override
    public void onOrder(OrderModel.Result result) {

    }

    @Override
    protected void onResume() {
        super.onResume();
        GetOrderHistoryAPI();

    }
}