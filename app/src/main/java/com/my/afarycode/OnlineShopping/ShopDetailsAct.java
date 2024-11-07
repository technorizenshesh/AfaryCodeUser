package com.my.afarycode.OnlineShopping;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.google.gson.Gson;
import com.my.afarycode.OnlineShopping.Model.ShopDetailModel;
import com.my.afarycode.OnlineShopping.Model.ShoppingProductModal;
import com.my.afarycode.OnlineShopping.adapter.ShopProductAdapter;
import com.my.afarycode.OnlineShopping.adapter.SliderAdapterExample;
import com.my.afarycode.OnlineShopping.constant.PreferenceConnector;
import com.my.afarycode.OnlineShopping.helper.DataManager;
import com.my.afarycode.R;
import com.my.afarycode.Splash;
import com.my.afarycode.databinding.ActivityShopDetailsBinding;
import com.my.afarycode.databinding.ActivityShoppingProductDetailBinding;
import com.my.afarycode.ratrofit.AfaryCode;
import com.my.afarycode.ratrofit.ApiClient;
import com.smarteist.autoimageslider.IndicatorView.animation.type.IndicatorAnimationType;
import com.smarteist.autoimageslider.SliderAnimations;
import com.smarteist.autoimageslider.SliderView;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ShopDetailsAct extends AppCompatActivity {
    ActivityShopDetailsBinding binding;

    private AfaryCode apiInterface;
    private String shopId="",sellerId="",currency="";
    SliderAdapterExample adapter;
    ArrayList<String>banner_array_list;
    ArrayList<ShopDetailModel.Result.Product>productArrayList;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_shop_details);
        apiInterface = ApiClient.getClient(ShopDetailsAct.this).create(AfaryCode.class);
        initViews();
    }

    private void initViews() {
        banner_array_list = new ArrayList<>();
        productArrayList = new ArrayList<>();

        if(getIntent()!=null){
            shopId = getIntent().getStringExtra("shopId");
            sellerId = getIntent().getStringExtra("sellerId");


            GetShopDetailsAPI(shopId,sellerId);
        }


        binding.RRback.setOnClickListener(v -> {
            onBackPressed();});

    }

    private void GetShopDetailsAPI(String shopId,String sellerId) {

        DataManager.getInstance().showProgressMessage(ShopDetailsAct.this, "Please wait...");
        Map<String,String> headerMap = new HashMap<>();
        headerMap.put("Authorization","Bearer " + PreferenceConnector.readString(ShopDetailsAct.this, PreferenceConnector.access_token,""));
        headerMap.put("Accept","application/json");

        Map<String, String> map = new HashMap<>();
        map.put("user_id",sellerId);
      //  map.put("user_id",PreferenceConnector.readString(ShopDetailsAct.this,PreferenceConnector.User_id,""));

        map.put("shop_id", shopId);
        map.put("register_id", PreferenceConnector.readString(ShopDetailsAct.this, PreferenceConnector.Register_id, ""));
        map.put("country_id",PreferenceConnector.readString(ShopDetailsAct.this, PreferenceConnector.countryId, ""));

        Log.e("MapMap", "Shop Details====" + map);

        Call<ResponseBody> loginCall = apiInterface.get_shop_detail(headerMap,map);

        loginCall.enqueue(new Callback<ResponseBody>() {

            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                DataManager.getInstance().hideProgressMessage();
                try {
                    Log.e("response===",response.body().toString());
                    String stringResponse = response.body().string();
                    JSONObject jsonObject = new JSONObject(stringResponse);
                    if (jsonObject.getString("status").toString().equals("1")) {
                        ShopDetailModel shopDetailModel = new Gson().fromJson(stringResponse,ShopDetailModel.class);
                        //  binding.tvNotFount.setVisibility(View.GONE);
                        //  Glide.with(ShopDetailsFragment.this).load(shopDetailModel.getResult().getImage1()).into(binding.ivCover);
                        binding.tvAddress.setText(shopDetailModel.getResult().getCity_name());
                        binding.tvFullAddress.setText(shopDetailModel.getResult().getAddress());
                        binding.tvShopName.setText(shopDetailModel.getResult().getName());
                        binding.tvTitle.setText(shopDetailModel.getResult().getName());

                        banner_array_list.clear();
                        binding.tvClose.setText("Close Time : "+shopDetailModel.getResult().getCloseTime());

                        banner_array_list.add(shopDetailModel.getResult().getImage1());
                        banner_array_list.add(shopDetailModel.getResult().getImage2());
                        banner_array_list.add(shopDetailModel.getResult().getImage3());

                        adapter = new SliderAdapterExample(ShopDetailsAct.this, banner_array_list);
                        binding.imageSlider.setSliderAdapter(adapter);
                        binding.imageSlider.setIndicatorAnimation(IndicatorAnimationType.WORM); //set indicator animation by using SliderLayout.IndicatorAnimations. :WORM or THIN_WORM or COLOR or DROP or FILL or NONE or SCALE or SCALE_DOWN or SLIDE and SWAP!!
                        binding.imageSlider.setSliderTransformAnimation(SliderAnimations.SIMPLETRANSFORMATION);
                        binding.imageSlider.setAutoCycleDirection(SliderView.AUTO_CYCLE_DIRECTION_BACK_AND_FORTH);
                        //    binding.imageSlider2.setIndicatorSelectedColor(R.color.colorPrimary);
                        //    binding.imageSlider2.setIndicatorUnselectedColor(Color.GRAY);
                        binding.imageSlider.setScrollTimeInSec(3);
                        binding.imageSlider.setAutoCycle(true);
                        binding.imageSlider.startAutoCycle();



                      /*  if(shopDetailModel.getResult().getCurrency().equalsIgnoreCase("Dollars")) currency = "USD";
                        else if(shopDetailModel.getResult().getCurrency().equalsIgnoreCase("Euro")) currency = "EUR";
                        else if(shopDetailModel.getResult().getCurrency().equalsIgnoreCase("Franc CFA")) currency = "XAF";
                        else if(shopDetailModel.getResult().getCurrency().equalsIgnoreCase("INDIA RUPEE")) currency = "INR";
                        else  currency = "";*/

                        if(shopDetailModel.getResult().getProduct().size()!=0){
                            productArrayList.clear();
                            productArrayList.addAll(shopDetailModel.getResult().getProduct());
                            binding.rvProduct.setAdapter(new ShopProductAdapter(ShopDetailsAct.this, productArrayList,currency));
                        }
                        else {
                            productArrayList.clear();
                            binding.rvProduct.setAdapter(new ShopProductAdapter(ShopDetailsAct.this, productArrayList,""));

                        }

                    } else if (jsonObject.getString("status").toString().equals("0")) {
                        //  binding.tvNotFount.setVisibility(View.VISIBLE);
                        Toast.makeText(ShopDetailsAct.this, jsonObject.getString("message").toString(), Toast.LENGTH_SHORT).show();
                    }

                    else if (jsonObject.getString("status").equals("5")) {
                        PreferenceConnector.writeString(ShopDetailsAct.this, PreferenceConnector.LoginStatus, "false");
                        startActivity(new Intent(ShopDetailsAct.this, Splash.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP));
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
