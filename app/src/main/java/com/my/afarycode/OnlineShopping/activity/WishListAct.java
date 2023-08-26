package com.my.afarycode.OnlineShopping.activity;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.gson.Gson;
import com.my.afarycode.OnlineShopping.Model.GetWishListModal;
import com.my.afarycode.OnlineShopping.adapter.MyWishListAdapter;
import com.my.afarycode.OnlineShopping.constant.PreferenceConnector;
import com.my.afarycode.OnlineShopping.helper.DataManager;
import com.my.afarycode.R;
import com.my.afarycode.databinding.ActivityWishListBinding;
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

public class WishListAct extends AppCompatActivity {
    ActivityWishListBinding binding;
    private ArrayList<GetWishListModal.Result> get_result;
    private AfaryCode apiInterface;
    private MyWishListAdapter adapter;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding =  DataBindingUtil.setContentView(this,R.layout.activity_wish_list);
        apiInterface = ApiClient.getClient(WishListAct.this).create(AfaryCode.class);

        initViews();
    }

    private void initViews() {
        get_result = new ArrayList<>();

        binding.RRback.setOnClickListener(v -> {
            onBackPressed();
        });
    }


    private void GetWishListAPI() {

        DataManager.getInstance().showProgressMessage(WishListAct.this, "Please wait...");
        Map<String,String> headerMap = new HashMap<>();
        headerMap.put("Authorization","Bearer " +PreferenceConnector.readString(WishListAct.this, PreferenceConnector.access_token,""));
        headerMap.put("Accept","application/json");

        Map<String, String> map = new HashMap<>();
        map.put("user_id", PreferenceConnector.readString(WishListAct.this, PreferenceConnector.User_id, ""));

        Log.e("MapMap", "EXERSICE LIST" + map);

        Call<ResponseBody> loginCall = apiInterface.get_wish_list(headerMap,map);

        loginCall.enqueue(new Callback<ResponseBody>() {

            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                DataManager.getInstance().hideProgressMessage();

                try {


                    String stringResponse = response.body().string();
                    JSONObject jsonObject = new JSONObject(stringResponse);
                    Log.e("response===",response.body().toString());

                    if(jsonObject.getString("status").equals("1")) {
                        GetWishListModal model = new Gson().fromJson(stringResponse,GetWishListModal.class);
                        get_result.clear();
                        get_result.addAll(model.getResult());
                        adapter.notifyDataSetChanged();



                    } else if (jsonObject.getString("status").equals("0")) {
                        Toast.makeText(WishListAct.this,jsonObject.getString("message") , Toast.LENGTH_SHORT).show();
                        get_result.clear();
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

}
