package com.my.afarycode.OnlineShopping.orderdetails;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;


import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.my.afarycode.OnlineShopping.HomeActivity;
import com.my.afarycode.OnlineShopping.Model.GetProfileModal;
import com.my.afarycode.OnlineShopping.ProductDetailAct;
import com.my.afarycode.OnlineShopping.activity.CardAct;
import com.my.afarycode.OnlineShopping.activity.CheckOutDeliveryAct;
import com.my.afarycode.OnlineShopping.chat.ChatAct;
import com.my.afarycode.OnlineShopping.constant.PreferenceConnector;
import com.my.afarycode.OnlineShopping.helper.DataManager;
import com.my.afarycode.OnlineShopping.myorder.MyOrderScreen;
import com.my.afarycode.OnlineShopping.myorder.OrderModel;
import com.my.afarycode.R;
import com.my.afarycode.Splash;
import com.my.afarycode.databinding.ActivityOrderDetailsBinding;
import com.my.afarycode.ratrofit.AfaryCode;
import com.my.afarycode.ratrofit.ApiClient;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class OrderDetailsAct extends AppCompatActivity implements ItemOrderListener{
    public String TAG = "OrderDetailsAct";
    ActivityOrderDetailsBinding binding;
    String orderId ="",orderStatus="",userName="",userId="",userImg="",sellerId="",sellerName,sellerImg="";
    OrderDetailsModel model;
    private AfaryCode apiInterface;
    ItemsAdapter itemsAdapter;
    ArrayList<OrderDetailsModel.Result.Product>arrayList;
   // double totalPriceToToPay=0.0,taxN1=0.0,taxN2=0.0,platFormsFees=0.0,deliveryFees=0.0,subTotal=0.0;
    String totalPriceToToPay= "0",subTotal="0",taxN1="0",taxN2="0",platFormsFees="0",deliveryFees="0";

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

        arrayList = new ArrayList<>();

        itemsAdapter = new ItemsAdapter(OrderDetailsAct.this,arrayList,OrderDetailsAct.this);
        binding.rvDetails.setAdapter(itemsAdapter);


        callOrderDetail();

        binding.backNavigation.setOnClickListener(v -> finish());

        binding.btnChat.setOnClickListener(v -> {
            startActivity(new Intent(this, ChatAct.class)
                    .putExtra("UserId",model.getResult().getSellerId())
                    .putExtra("UserName",sellerName)
                    .putExtra("UserImage",sellerImg)
                    .putExtra("id",userId)
                    .putExtra("name",userName)
                    .putExtra("img",userImg));
        });


       // PreferenceConnector.writeString(OrderDetailsAct.this,"afaryCode",model.getResult().getAfaryCode());

        binding.btnAccept.setOnClickListener(v ->
        {
            if(binding.btnAccept.getText().equals(getString(R.string.re_order))){
                addReOrder(orderId);
            }

            else {
                if (model != null) {
                    if (model.getResult().getStatus().equals("Accepted") || model.getResult().getStatus().equalsIgnoreCase("PickedUp")) {
                        PreferenceConnector.writeString(OrderDetailsAct.this, "afaryCode", model.getResult().getAfaryCode());
                        PreferenceConnector.writeString(OrderDetailsAct.this, "orderId", model.getResult().getDeliveryPerson().getOrderId());
                        startActivity(new Intent(OrderDetailsAct.this, OrderTrackAct.class)
                                .putExtra("orderDetails", model.getResult()));
                    }
                }
           }

        });


        binding.btnDecline.setOnClickListener(view -> {
            if(model!=null){
              if(model.getResult().getStatus().equalsIgnoreCase("Pending") || model.getResult().getStatus().equalsIgnoreCase("Accepted")) alertCancelOrder();
            }
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
        map.put("register_id", PreferenceConnector.readString(OrderDetailsAct.this, PreferenceConnector.Register_id, ""));
        map.put("user_id", PreferenceConnector.readString(OrderDetailsAct.this, PreferenceConnector.User_id, ""));
        map.put("country_id",PreferenceConnector.readString(OrderDetailsAct.this, PreferenceConnector.countryId, ""));


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
                      //  Glide.with(OrderDetailsAct.this).load(model.getResult().getProductList().get(0).getProductImages()).into(binding.productImg);
                        binding.tvShopName.setText(model.getResult().getProductList().get(0).getShopName());
                        binding.tvOrderNumber.setText(orderId);
                        binding.tvDate.setText(model.getResult().getProductList().get(0).getDateTime());



                        GetSellerProfileAPI(model.getResult().getSellerId());

                        if (model.getResult().getProductList().size() == 1) {
                            binding.llOne.setVisibility(View.VISIBLE);
                            binding.llTwo.setVisibility(View.GONE);
                            binding.llThree.setVisibility(View.GONE);
                            binding.llFour.setVisibility(View.GONE);
                           // Glide.with(OrderDetailsAct.this).load(model.getResult().getShopImage().get(0).getImage()).into(binding.productImage);

                            Glide.with(OrderDetailsAct.this).load(model.getResult().getProductList().get(0).getProductImages()).into(binding.productImage);


                        } else if (model.getResult().getProductList().size() == 2) {
                            binding.llOne.setVisibility(View.GONE);
                            binding.llTwo.setVisibility(View.VISIBLE);
                            binding.llThree.setVisibility(View.GONE);
                            binding.llFour.setVisibility(View.GONE);
                            Glide.with(OrderDetailsAct.this).load(model.getResult().getProductList().get(0).getProductImages()).into(binding.productImageTw01);
                            Glide.with(OrderDetailsAct.this).load(model.getResult().getProductList().get(1).getProductImages()).into(binding.productImageTwo2);

                        } else if (model.getResult().getProductList().size() == 3) {
                            binding.llOne.setVisibility(View.GONE);
                            binding.llTwo.setVisibility(View.GONE);
                            binding.llThree.setVisibility(View.VISIBLE);
                            binding.llFour.setVisibility(View.GONE);
                            Glide.with(OrderDetailsAct.this).load(model.getResult().getProductList().get(0).getProductImages()).into(binding.productImageThree1);
                            Glide.with(OrderDetailsAct.this).load(model.getResult().getProductList().get(1).getProductImages()).into(binding.productImageThree2);
                            Glide.with(OrderDetailsAct.this).load(model.getResult().getProductList().get(2).getProductImages()).into(binding.productImageThree3);

                        } else if (model.getResult().getProductList().size() <= 4) {
                            binding.llOne.setVisibility(View.GONE);
                            binding.llTwo.setVisibility(View.GONE);
                            binding.llThree.setVisibility(View.GONE);
                            binding.llFour.setVisibility(View.VISIBLE);
                            Glide.with(OrderDetailsAct.this).load(model.getResult().getProductList().get(0).getProductImages()).into(binding.productImageFour1);
                            Glide.with(OrderDetailsAct.this).load(model.getResult().getProductList().get(1).getProductImages()).into(binding.productImageFour2);
                            Glide.with(OrderDetailsAct.this).load(model.getResult().getProductList().get(2).getProductImages()).into(binding.productImageFour3);
                            binding.tvImgCount.setText("+" + (model.getResult().getProductList().size() - 3));

                        }

                        if(model.getResult().getStatus().equals("Pending")){
                            binding.llButtons.setVisibility(View.VISIBLE);
                            binding.btnAccept.setText(getString(R.string.accept));
                            binding.btnAccept.setVisibility(View.GONE);
                            binding.tvAfaryCode.setVisibility(View.GONE);
                            binding.rlDeliveryPerson.setVisibility(View.GONE);
                            binding.btnDecline.setVisibility(View.VISIBLE);


                        }
                        else if(model.getResult().getStatus().equals("Accepted")){
                            binding.btnDecline.setVisibility(View.GONE);
                            if(model.getResult().getSelfCollect().equals("Yes")){
                                binding.llButtons.setVisibility(View.VISIBLE);
                                binding.btnAccept.setVisibility(View.GONE);
                                binding.tvAfaryCode.setVisibility(View.VISIBLE);
                                binding.rlDeliveryPerson.setVisibility(View.GONE);
                                binding.tvAfaryCode.setText(model.getResult().getCutomer_afary_code());

                            }
                            else {
                                binding.llButtons.setVisibility(View.VISIBLE);
                                binding.btnAccept.setVisibility(View.GONE);
                                binding.tvAfaryCode.setVisibility(View.GONE);
                                binding.rlDeliveryPerson.setVisibility(View.GONE);
                            }


                        }

                        else if(model.getResult().getStatus().equals("PickedUp")) {
                            binding.llButtons.setVisibility(View.GONE);
                            binding.btnAccept.setVisibility(View.GONE);
                            binding.tvAfaryCode.setVisibility(View.VISIBLE);
                            binding.btnDecline.setVisibility(View.GONE);
                            binding.btnChat.setVisibility(View.GONE);
                            //binding.btnAccept.setText(getString(R.string.track_order));
                         //   binding.btnAccept.setText(getString(R.string.re_order));
                            binding.tvAfaryCode.setText(model.getResult().getDeliveryPerson().getCutomerAfaryCode());

                            if(jsonObject.getJSONObject("result").isNull("delivery_person"))  { //    model.getResult().getDeliveryPerson()==null){
                                binding.rlDeliveryPerson.setVisibility(View.GONE);
                            }
                            else {
                                    binding.rlDeliveryPerson.setVisibility(View.VISIBLE);
                                    binding.tvDeliveryPerson.setText(getString(R.string.person_to_be_delivered)+ " "+Html.fromHtml("<font color='#000'>" + "<b>" + model.getResult().getDeliveryPerson().getDeliveryPersonName() +
                                        " "+model.getResult().getDeliveryPerson().getDeliveryPersonNumber() +"</b>" + " the delivery person is on his way to you. Thanks" + "</font>"));
                            }
                        }

                        else if(model.getResult().getStatus().equals("Completed")) {
                            binding.llButtons.setVisibility(View.VISIBLE);
                            binding.btnAccept.setVisibility(View.VISIBLE);
                            binding.tvAfaryCode.setVisibility(View.VISIBLE);
                            binding.btnDecline.setVisibility(View.GONE);
                            binding.btnChat.setVisibility(View.GONE);
                            binding.btnAccept.setText(getString(R.string.re_order));
                            binding.tvAfaryCode.setText(model.getResult().getDeliveryPerson().getCutomerAfaryCode());

                            if(model.getResult().getSelfCollect().equals("Yes")) {
                                binding.rlDeliveryPerson.setVisibility(View.GONE);

                            }
                            else {
                                if(jsonObject.getJSONObject("result").isNull("delivery_person"))  { //    model.getResult().getDeliveryPerson()==null){
                                    binding.rlDeliveryPerson.setVisibility(View.GONE);
                                }
                                else {
                                    binding.rlDeliveryPerson.setVisibility(View.VISIBLE);
                                    binding.tvDeliveryPerson.setText(getString(R.string.person_to_be_delivered)+ " "+Html.fromHtml("<font color='#000'>" + "<b>" + model.getResult().getDeliveryPerson().getDeliveryPersonName() +
                                            " "+model.getResult().getDeliveryPerson().getDeliveryPersonNumber() +"</b>" + " the delivery person is on his way to you. Thanks" + "</font>"));

                                    //  }
                                }
                            }

                        }






                        else if(model.getResult().getStatus().equals("Cancelled")){
                            binding.llButtons.setVisibility(View.GONE);
                            binding.tvAfaryCode.setVisibility(View.GONE);
                          //  binding.btnAccept.setVisibility(View.VISIBLE);
                          ///  binding.btnAccept.setText(getString(R.string.re_order));

                        }

                        else if(model.getResult().getStatus().equals("Cancelled_by_user")){
                            binding.llButtons.setVisibility(View.VISIBLE);
                            binding.tvAfaryCode.setVisibility(View.GONE);
                            binding.btnAccept.setVisibility(View.VISIBLE);
                            binding.btnDecline.setVisibility(View.GONE);
                            binding.btnChat.setVisibility(View.GONE);
                            binding.btnAccept.setText(getString(R.string.re_order));
                        }



                        taxN1 = model.getResult().getTaxN1();
                        taxN2 = model.getResult().getTaxN2();
                        platFormsFees = model.getResult().getPlatFormsFees();
                        deliveryFees = model.getResult().getDeliveryCharges();
                        totalPriceToToPay = parseFrenchNumber( model.getResult().getTotalAmount())+"";
                        subTotal =   totalPriceToToPay;//totalPriceToToPay - (taxN1+taxN2+platFormsFees+deliveryFees);

                     //   subTotal =  Double.parseDouble(model.getResult().getPrice());  // - deliveryFees;

                     //   totalPriceToToPay = Double.parseDouble(model.getResult().getPrice())
                     //           + Double.parseDouble(model.getResult().getPlatFormsFees())
                     //           + Double.parseDouble(model.getResult().getDeliveryCharges())
                     //           + Double.parseDouble(model.getResult().getTaxN1())
                     //           + Double.parseDouble(model.getResult().getTaxN2());



                        binding.plateformFees.setText("FCFA" +  platFormsFees);
                        binding.tvTax1.setText("FCFA" +  taxN1);
                        binding.tvtax2.setText("FCFA" +  taxN2);
                        binding.tvDelivery.setText("FCFA" +  deliveryFees);
                        binding.tvTotalAmt.setText("FCFA" + totalPriceToToPay);
                        binding.subTotal.setText("FCFA" + subTotal);


                        arrayList.clear();
                        arrayList.addAll(model.getResult().getProductList());
                        itemsAdapter.notifyDataSetChanged();

                        if(!jsonObject.getJSONObject("result").getString("address").isEmpty()){
                            if(model.getResult().getSelfCollect().equals("Yes")) {
                                binding.llDeliveryAddress.setVisibility(View.GONE);
                            }else
                            {
                                binding.llDeliveryAddress.setVisibility(View.VISIBLE);
                                binding.tvDeliveryAddress.setText(jsonObject.getJSONObject("result").getString("address"));
                            }
                        }
                        else {
                            binding.llDeliveryAddress.setVisibility(View.GONE);

                        }

                        if(!jsonObject.getJSONObject("result").isNull("delivery_progress")){
                            binding.rvOrderStatus.setVisibility(View.VISIBLE);
                            binding.rvOrderStatus.setAdapter(new OrderStatusAdapter(OrderDetailsAct.this, (ArrayList<OrderDetailsModel.Result.DeliveryProgress>) model.getResult().getDeliveryProgress()));
                        }
                        else {
                            binding.rvOrderStatus.setVisibility(View.GONE);
                        }
                    }

                    else if (jsonObject.getString("status").equals("5")) {
                        // Toast.makeText(getContext(), "No Data Found !!!!", Toast.LENGTH_SHORT).show();

                        PreferenceConnector.writeString(OrderDetailsAct.this, PreferenceConnector.LoginStatus, "false");
                        startActivity(new Intent(OrderDetailsAct.this, Splash.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP));
                        finish();

                    }


                    else {
                        arrayList.clear();
                        itemsAdapter.notifyDataSetChanged();

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
        map.put("register_id", PreferenceConnector.readString(OrderDetailsAct.this, PreferenceConnector.Register_id, ""));
        map.put("country_id",PreferenceConnector.readString(OrderDetailsAct.this, PreferenceConnector.countryId, ""));

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

                    else if (data.status.equals("5")) {
                        PreferenceConnector.writeString(OrderDetailsAct.this, PreferenceConnector.LoginStatus, "false");
                        startActivity(new Intent(OrderDetailsAct.this, Splash.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP));
                        finish();
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
        map.put("country_id",PreferenceConnector.readString(OrderDetailsAct.this, PreferenceConnector.countryId, ""));

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


   private void  cancelOrderByUser(){
           DataManager.getInstance().showProgressMessage(this, "Please wait...");
           Map<String,String> headerMap = new HashMap<>();
           headerMap.put("Authorization","Bearer " +PreferenceConnector.readString(OrderDetailsAct.this, PreferenceConnector.access_token,""));
           headerMap.put("Accept","application/json");

           Map<String, String> map = new HashMap<>();
           map.put("order_id", orderId);
       map.put("user_id", model.getResult().getUserId());
       map.put("seller_id", model.getResult().getSellerId());
       map.put("status", "Cancelled_by_user");
       map.put("register_id", PreferenceConnector.readString(OrderDetailsAct.this, PreferenceConnector.Register_id, ""));

       Log.e(TAG, "Order Cancel Request" + map);
           Call<ResponseBody> loginCall = apiInterface.cancelOrderByUserApi(headerMap,map);
           loginCall.enqueue(new Callback<ResponseBody>() {

               @Override
               public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                   DataManager.getInstance().hideProgressMessage();

                   try {
                       String stringResponse = response.body().string();
                       JSONObject jsonObject = new JSONObject(stringResponse);
                       Log.e("response===", response.body().string());
                       if (jsonObject.getString("status").equals("1")) {
                           Toast.makeText(OrderDetailsAct.this, "Order Cancelled...", Toast.LENGTH_SHORT).show();
                           finish();
                       }

                       else if (jsonObject.getString("status").equals("5")) {
                           PreferenceConnector.writeString(OrderDetailsAct.this, PreferenceConnector.LoginStatus, "false");
                           startActivity(new Intent(OrderDetailsAct.this, Splash.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP));
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

    private void deleteItemByUser(String orderId){
        DataManager.getInstance().showProgressMessage(this, "Please wait...");
        Map<String,String> headerMap = new HashMap<>();
        headerMap.put("Authorization","Bearer " +PreferenceConnector.readString(OrderDetailsAct.this, PreferenceConnector.access_token,""));
        headerMap.put("Accept","application/json");

        Map<String, String> map = new HashMap<>();
        map.put("order_id", orderId);
        map.put("user_id", model.getResult().getUserId());
        map.put("register_id", PreferenceConnector.readString(OrderDetailsAct.this, PreferenceConnector.Register_id, ""));

        Log.e(TAG, "Delete Item Request" + map);
        Call<ResponseBody> loginCall = apiInterface.deleteItemByUserApi (headerMap,map);
        loginCall.enqueue(new Callback<ResponseBody>() {

            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                DataManager.getInstance().hideProgressMessage();

                try {
                    String stringResponse = response.body().string();
                    JSONObject jsonObject = new JSONObject(stringResponse);
                    Log.e("response===", response.body().string());
                    if (jsonObject.getString("status").equals("1")) {
                        Toast.makeText(OrderDetailsAct.this, "Item deleted...", Toast.LENGTH_SHORT).show();
                        callOrderDetail();
                    }

                    else if (jsonObject.getString("status").equals("5")) {
                        PreferenceConnector.writeString(OrderDetailsAct.this, PreferenceConnector.LoginStatus, "false");
                        startActivity(new Intent(OrderDetailsAct.this, Splash.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP));
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





    private void alertCancelOrder() {
        AlertDialog.Builder builder = new AlertDialog.Builder(OrderDetailsAct.this);
        builder.setMessage("Are you sure you want to refuse this order?")
                .setCancelable(false)
                .setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        cancelOrderByUser();
                    }
                }).setNegativeButton(getString(R.string.no), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();


    }


    private void alertDeleteItem(String orderId) {
        AlertDialog.Builder builder = new AlertDialog.Builder(OrderDetailsAct.this);
        builder.setMessage("Are you sure you want to delete this item?")
                .setCancelable(false)
                .setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        deleteItemByUser(orderId);
                    }
                }).setNegativeButton(getString(R.string.no), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();


    }


    @Override
    public void onItem(int position, OrderDetailsModel.Result.Product product) {
        alertDeleteItem(product.getOrderId());
    }



    private void addReOrder(String orderId) {


        Map<String, String> headerMap = new HashMap<>();
        headerMap.put("Authorization", "Bearer " + PreferenceConnector.readString(OrderDetailsAct.this, PreferenceConnector.access_token, ""));
        headerMap.put("Accept", "application/json");

        Map<String, String> map = new HashMap<>();
        map.put("order_id", orderId);
        Log.e("MapMap", "re-order" + map);

        Call<ResponseBody> reviewModalCall = apiInterface.reOrder(headerMap, map);

        reviewModalCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                DataManager.getInstance().hideProgressMessage();

                try {
                    String responseData = response.body() != null ? response.body().string() : "";
                    JSONObject object = new JSONObject(responseData);
                    Log.e(TAG, "Re- Order RESPONSE" + object);
                    if (object.getString("status").equals("1")) {
                         startActivity(new Intent(OrderDetailsAct.this, CardAct.class)
                                 .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP));
                         finish();
                    } else if (object.getString("status").equals("0")) {
                        Toast.makeText(OrderDetailsAct.this, object.getString("message"), Toast.LENGTH_SHORT).show();
                    } else if (object.getString("status").equals("5")) {
                        PreferenceConnector.writeString(OrderDetailsAct.this, PreferenceConnector.LoginStatus, "false");
                        startActivity(new Intent(OrderDetailsAct.this, Splash.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP));
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

    private int parseFrenchNumber(String number) {
        // Remove the commas and parse to an integer
        String cleanedNumber = number.replace(",", "");
        return Integer.parseInt(cleanedNumber);
    }

    // Function to format number in French style (with commas)
    private String formatToFrenchStyle(int number) {
        // Use the NumberFormat for the French locale
        NumberFormat numberFormat = NumberFormat.getInstance(Locale.FRANCE);
        return numberFormat.format(number);
    }

}
