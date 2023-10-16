package com.my.afarycode.OnlineShopping.orderdetails;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;


import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.my.afarycode.OnlineShopping.Model.GetProfileModal;
import com.my.afarycode.OnlineShopping.activity.CheckOutDeliveryAct;
import com.my.afarycode.OnlineShopping.chat.ChatAct;
import com.my.afarycode.OnlineShopping.constant.PreferenceConnector;
import com.my.afarycode.OnlineShopping.helper.DataManager;
import com.my.afarycode.OnlineShopping.myorder.MyOrderScreen;
import com.my.afarycode.OnlineShopping.myorder.OrderModel;
import com.my.afarycode.R;
import com.my.afarycode.databinding.ActivityOrderDetailsBinding;
import com.my.afarycode.ratrofit.AfaryCode;
import com.my.afarycode.ratrofit.ApiClient;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class OrderDetailsAct extends AppCompatActivity {
    public String TAG = "OrderDetailsAct";
    ActivityOrderDetailsBinding binding;
    String orderId ="",orderStatus="",userName="",userId="",userImg="",sellerId="",sellerName,sellerImg="";
    OrderDetailsModel model;
    private AfaryCode apiInterface;
    double totalPriceToToPay=0.0,taxN1=0.0,taxN2=0.0,platFormsFees=0.0,deliveryFees=0.0,subTotal=0.0;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_order_details);
        apiInterface = ApiClient.getClient(this).create(AfaryCode.class);
        initViews();


    }

    private void initViews() {

        if(getIntent()!=null) {
            orderId  = getIntent().getStringExtra("id");

        }

        callOrderDetail();

        binding.backNavigation.setOnClickListener(v -> onBackPressed());

        binding.btnChat.setOnClickListener(v -> {
            startActivity(new Intent(this, ChatAct.class)
                    .putExtra("UserId",model.getResult().getSellerId())
                    .putExtra("UserName",sellerName)
                    .putExtra("UserImage",sellerImg)
                    .putExtra("id",userId)
                    .putExtra("name",userName)
                    .putExtra("img",userImg));
        });


        GetProfileAPI();
    }

    private void callOrderDetail() {
        DataManager.getInstance().showProgressMessage(this, "Please wait...");
        Map<String,String> headerMap = new HashMap<>();
        headerMap.put("Authorization","Bearer " +PreferenceConnector.readString(OrderDetailsAct.this, PreferenceConnector.access_token,""));
        headerMap.put("Accept","application/json");

        Map<String, String> map = new HashMap<>();
        map.put("order_id", orderId);
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
                        binding.tvAfaryCode.setText(model.getResult().getAfaryCode());
                        Glide.with(OrderDetailsAct.this).load(model.getResult().getProductList().get(0).getProductImages()).into(binding.productImg);
                        GetSellerProfileAPI(model.getResult().getSellerId());
/*
                        if (model.getResult().getShopImage().size() == 1) {
                            binding.llOne.setVisibility(View.VISIBLE);
                            binding.llTwo.setVisibility(View.GONE);
                            binding.llThree.setVisibility(View.GONE);
                            binding.llFour.setVisibility(View.GONE);
                           // Glide.with(OrderDetailsAct.this).load(model.getResult().getShopImage().get(0).getImage()).into(binding.productImage);

                            Glide.with(OrderDetailsAct.this).load(model.getResult().getShopImage().get(0).getImage()).into(binding.productImage);


                        } else if (model.getResult().getShopImage().size() == 2) {
                            binding.llOne.setVisibility(View.GONE);
                            binding.llTwo.setVisibility(View.VISIBLE);
                            binding.llThree.setVisibility(View.GONE);
                            binding.llFour.setVisibility(View.GONE);
                            Glide.with(OrderDetailsAct.this).load(model.getResult().getShopImage().get(0).getImage()).into(binding.productImageTw01);
                            Glide.with(OrderDetailsAct.this).load(model.getResult().getShopImage().get(1).getImage()).into(binding.productImageTwo2);

                        } else if (model.getResult().getShopImage().size() == 3) {
                            binding.llOne.setVisibility(View.GONE);
                            binding.llTwo.setVisibility(View.GONE);
                            binding.llThree.setVisibility(View.VISIBLE);
                            binding.llFour.setVisibility(View.GONE);
                            Glide.with(OrderDetailsAct.this).load(model.getResult().getShopImage().get(0).getImage()).into(binding.productImageThree1);
                            Glide.with(OrderDetailsAct.this).load(model.getResult().getShopImage().get(1).getImage()).into(binding.productImageThree2);
                            Glide.with(OrderDetailsAct.this).load(model.getResult().getShopImage().get(2).getImage()).into(binding.productImageThree3);

                        } else if (model.getResult().getShopImage().size() <= 4) {
                            binding.llOne.setVisibility(View.GONE);
                            binding.llTwo.setVisibility(View.GONE);
                            binding.llThree.setVisibility(View.GONE);
                            binding.llFour.setVisibility(View.VISIBLE);
                            Glide.with(OrderDetailsAct.this).load(model.getResult().getShopImage().get(0).getImage()).into(binding.productImageFour1);
                            Glide.with(OrderDetailsAct.this).load(model.getResult().getShopImage().get(1).getImage()).into(binding.productImageFour2);
                            Glide.with(OrderDetailsAct.this).load(model.getResult().getShopImage().get(2).getImage()).into(binding.productImageFour3);
                            binding.tvImgCount.setText("+" + (model.getResult().getShopImage().size() - 3));

                        }
*/


                        totalPriceToToPay = Double.parseDouble(model.getResult().getTotalAmount());
                        taxN1 = Double.parseDouble(model.getResult().getTaxN1());
                        taxN2 = Double.parseDouble(model.getResult().getTaxN2());
                        platFormsFees = Double.parseDouble(model.getResult().getPlatFormsFees());
                        deliveryFees = Double.parseDouble(model.getResult().getDeliveryCharges());
                        subTotal =  totalPriceToToPay - (taxN1+taxN2+platFormsFees+deliveryFees);

                        binding.plateformFees.setText("Rs. " + String.format("%.2f", platFormsFees));
                        binding.tvTax1.setText("Rs. " + String.format("%.2f", taxN1));
                        binding.tvtax2.setText("Rs. " + String.format("%.2f", taxN2));
                        binding.tvDelivery.setText("Rs. " + String.format("%.2f", deliveryFees));
                        binding.tvTotalAmt.setText("Rs. " + String.format("%.2f", totalPriceToToPay));
                        binding.subTotal.setText("Rs. " + String.format("%.2f", subTotal));

                        binding.rvDetails.setAdapter(new ItemsAdapter(OrderDetailsAct.this, (ArrayList<OrderDetailsModel.Result.Product>) model.getResult().getProductList()));

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


    private void GetProfileAPI() {
        Map<String, String> map = new HashMap<>();
        map.put("user_id", PreferenceConnector.readString(OrderDetailsAct.this, PreferenceConnector.User_id, ""));
        Call<GetProfileModal> loginCall = apiInterface.get_profile(map);

        loginCall.enqueue(new Callback<GetProfileModal>() {
            @Override
            public void onResponse(Call<GetProfileModal> call,
                                   Response<GetProfileModal> response) {

                DataManager.getInstance().hideProgressMessage();

                try {

                    GetProfileModal data = response.body();
                    String dataResponse = new Gson().toJson(response.body());

                    Log.e("MapMap", "GET RESPONSE" + dataResponse);

                    if (data.status.equals("1")) {

                       userName = data.getResult().userName;
                       userImg = data.getResult().image;
                        userId = data.getResult().id;



                    } else if (data.status.equals("0")) {
                       // Toast.makeText(OrderDetailsAct.this, data.message /*getString(R.string.wrong_username_password)*/, Toast.LENGTH_SHORT).show();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<GetProfileModal> call, Throwable t) {
                call.cancel();
                DataManager.getInstance().hideProgressMessage();
            }
        });
    }

    private void GetSellerProfileAPI(String id) {
        Map<String, String> map = new HashMap<>();
        map.put("user_id",id);
        Call<GetProfileModal> loginCall = apiInterface.get_profile(map);

        loginCall.enqueue(new Callback<GetProfileModal>() {
            @Override
            public void onResponse(Call<GetProfileModal> call,
                                   Response<GetProfileModal> response) {

                DataManager.getInstance().hideProgressMessage();

                try {

                    GetProfileModal data = response.body();
                    String dataResponse = new Gson().toJson(response.body());

                    Log.e("MapMap", "GET RESPONSE" + dataResponse);

                    if (data.status.equals("1")) {

                        sellerName = data.getResult().userName;
                        sellerImg = data.getResult().image;
                        sellerId = data.getResult().id;



                    } else if (data.status.equals("0")) {
                        // Toast.makeText(OrderDetailsAct.this, data.message /*getString(R.string.wrong_username_password)*/, Toast.LENGTH_SHORT).show();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<GetProfileModal> call, Throwable t) {
                call.cancel();
                DataManager.getInstance().hideProgressMessage();
            }
        });
    }





}
