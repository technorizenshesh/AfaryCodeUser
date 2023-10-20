package com.my.afarycode.OnlineShopping;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;
import com.my.afarycode.OnlineShopping.Model.Get_Transaction_Modal;
import com.my.afarycode.OnlineShopping.activity.CheckOutDeliveryAct;
import com.my.afarycode.OnlineShopping.constant.PreferenceConnector;
import com.my.afarycode.OnlineShopping.helper.DataManager;
import com.my.afarycode.OnlineShopping.myorder.MyOrderScreen;
import com.my.afarycode.OnlineShopping.myorder.OrderAdapter;
import com.my.afarycode.OnlineShopping.myorder.OrderListener;
import com.my.afarycode.OnlineShopping.myorder.OrderModel;
import com.my.afarycode.R;
import com.my.afarycode.databinding.ActivityOrderHistoryScreenBinding;
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

public class OrderHistoryScreen extends AppCompatActivity implements OrderListener {
    public String TAG = "OrderHistoryScreen";
    OrderAdapter adapter;
    ActivityOrderHistoryScreenBinding binding;
    private AfaryCode apiInterface;
    private ArrayList<OrderModel.Result> arrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_order_history_screen);
        apiInterface = ApiClient.getClient(this).create(AfaryCode.class);
        initViews();
    }

    private void initViews() {
        arrayList = new ArrayList<>();


        adapter = new OrderAdapter(OrderHistoryScreen.this, arrayList,OrderHistoryScreen.this);
        binding.recyclerOrderHistory.setAdapter(adapter);

        binding.RRback.setOnClickListener(v -> {
            finish();

        });

    }


    private void getOrderHistory() {
        DataManager.getInstance().showProgressMessage(this, "Please wait...");
        Map<String,String> headerMap = new HashMap<>();
        headerMap.put("Authorization","Bearer " +PreferenceConnector.readString(OrderHistoryScreen.this, PreferenceConnector.access_token,""));
        headerMap.put("Accept","application/json");

        Map<String, String> map = new HashMap<>();
        map.put("user_id", PreferenceConnector.readString(this, PreferenceConnector.User_id, ""));
        Log.e(TAG, "EXERSICE LIST" + map);
        Call<ResponseBody> loginCall = apiInterface.getHistoryOnlineOrderApi(headerMap,map);

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
                        Toast.makeText(OrderHistoryScreen.this, jsonObject.getString("message").toString(), Toast.LENGTH_SHORT).show();
                        arrayList.clear();
                        adapter.notifyDataSetChanged();
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
        getOrderHistory();

    }
}