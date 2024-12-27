package com.my.afarycode.OnlineShopping.ratereview;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.my.afarycode.OnlineShopping.HomeActivity;
import com.my.afarycode.OnlineShopping.constant.PreferenceConnector;
import com.my.afarycode.OnlineShopping.helper.DataManager;
import com.my.afarycode.OnlineShopping.helper.NetworkAvailablity;
import com.my.afarycode.OnlineShopping.orderdetails.ItemsAdapter;
import com.my.afarycode.OnlineShopping.orderdetails.OrderDetailsAct;
import com.my.afarycode.OnlineShopping.orderdetails.OrderDetailsModel;
import com.my.afarycode.R;
import com.my.afarycode.Splash;
import com.my.afarycode.databinding.ActivityRateReviewBinding;
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

public class RateReviewAct extends AppCompatActivity {
    public String TAG = "RateReviewAct";
    private AfaryCode apiInterface;
    String orderId ="";

    ActivityRateReviewBinding binding;
    OrderDetailsModel model;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_rate_review);
        apiInterface = ApiClient.getClient(this).create(AfaryCode.class);

        initViews();
    }

    private void initViews() {
        if(getIntent()!=null) {
            orderId  = getIntent().getStringExtra("order_id");

        }

        binding.llBack.setOnClickListener(v -> finish());

        binding.rlSubmit.setOnClickListener(v -> {
                    if(NetworkAvailablity.checkNetworkStatus(RateReviewAct.this)) giveRate();
                    else Toast.makeText(RateReviewAct.this, getString(R.string.network_failure), Toast.LENGTH_SHORT).show();
                });




        if(NetworkAvailablity.checkNetworkStatus(RateReviewAct.this)) callOrderDetail();
        else Toast.makeText(RateReviewAct.this, getString(R.string.network_failure), Toast.LENGTH_SHORT).show();
    }

    private void giveRate() {
            DataManager.getInstance().showProgressMessage(this, getString(R.string.please_wait));
            Map<String,String> headerMap = new HashMap<>();
            headerMap.put("Authorization","Bearer " + PreferenceConnector.readString(RateReviewAct.this, PreferenceConnector.access_token,""));
            headerMap.put("Accept","application/json");

            Map<String, String> map = new HashMap<>();
            map.put("user_id", PreferenceConnector.readString(RateReviewAct.this, PreferenceConnector.User_id, ""));
            map.put("product_id", model.getResult().getItemId());
            map.put("product_rating", String.valueOf(binding.productRating.getRating()));
            map.put("product_review", binding.edProductReview.getText().toString());
            map.put("delivery_person_name", model.getResult().getDeliveryPerson().getDeliveryPersonName());
            map.put("delivery_person_email", model.getResult().getDeliveryPerson().getDeliveryPersonEmail());
            map.put("delivery_rating", String.valueOf(binding.deliveryRating.getRating()));
            map.put("delivery_review", binding.edDeliveryReview.getText().toString());
            map.put("register_id", PreferenceConnector.readString(RateReviewAct.this, PreferenceConnector.Register_id, ""));

        Log.e(TAG, "giveRate Request" + map);
            Call<ResponseBody> loginCall = apiInterface.giveRateReviewApi(headerMap,map);
            loginCall.enqueue(new Callback<ResponseBody>() {

                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    DataManager.getInstance().hideProgressMessage();

                    try {
                        String stringResponse = response.body().string();
                        JSONObject jsonObject = new JSONObject(stringResponse);
                        Log.e("response===", response.body().string());
                        if (jsonObject.getString("status").toString().equals("1")) {
                            Toast.makeText(RateReviewAct.this, getString(R.string.rate_sueccessfully), Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(RateReviewAct.this, HomeActivity.class)
                                    .putExtra("status","")
                                    .putExtra("msg","")
                                    .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK| Intent.FLAG_ACTIVITY_CLEAR_TOP));
                            finish();
                        }

                        else if (jsonObject.getString("status").equals("5")) {
                            PreferenceConnector.writeString(RateReviewAct.this, PreferenceConnector.LoginStatus, "false");
                            startActivity(new Intent(RateReviewAct.this, Splash.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP));
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



    private void callOrderDetail() {
        DataManager.getInstance().showProgressMessage(this, "Please wait...");
        Map<String,String> headerMap = new HashMap<>();
        headerMap.put("Authorization","Bearer " + PreferenceConnector.readString(RateReviewAct.this, PreferenceConnector.access_token,""));
        headerMap.put("Accept","application/json");

        Map<String, String> map = new HashMap<>();
        map.put("order_id", orderId);
        map.put("register_id", PreferenceConnector.readString(RateReviewAct.this, PreferenceConnector.Register_id, ""));

        Log.e(TAG, "My OrderDetails Request" + map);
        Call<ResponseBody> loginCall = apiInterface.getDetailOnlineOrderApi(headerMap,map);
        loginCall.enqueue(new Callback<ResponseBody>() {

            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                DataManager.getInstance().hideProgressMessage();

                try {
                    String stringResponse = response.body().string();
                    JSONObject jsonObject = new JSONObject(stringResponse);
                    Log.e("response===", response.body().string());
                    if (jsonObject.getString("status").toString().equals("1")) {
                        // binding.tvNotFount.setVisibility(View.GONE);
                        model = new Gson().fromJson(stringResponse, OrderDetailsModel.class);
                        Glide.with(RateReviewAct.this).load(model.getResult().getProductList().get(0).getProductImages())
                                .placeholder(R.drawable.user_default).into(binding.ivProduct);
                        Glide.with(RateReviewAct.this).load(model.getResult().getDeliveryPerson().getDeliveryPersonImage())
                                .placeholder(R.drawable.user_default).into(binding.ivDelivery);

                          binding.tvProductName.setText(model.getResult().getProductList().get(0).getProductName());
                          binding.tvDeliveryPersonName.setText(model.getResult().getDeliveryPerson().getDeliveryPersonName());

                    }
                    else if (jsonObject.getString("status").equals("5")) {
                        PreferenceConnector.writeString(RateReviewAct.this, PreferenceConnector.LoginStatus, "false");
                        startActivity(new Intent(RateReviewAct.this, Splash.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP));
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

}
