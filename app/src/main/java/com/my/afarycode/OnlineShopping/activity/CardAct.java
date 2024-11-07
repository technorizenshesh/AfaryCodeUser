package com.my.afarycode.OnlineShopping.activity;

import static com.my.afarycode.OnlineShopping.CheckOutScreen.subTotal;
import static com.my.afarycode.OnlineShopping.CheckOutScreen.totalPriceToToPay1;
import static com.my.afarycode.OnlineShopping.CheckOutScreen.total_price_to_to_pay;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.google.gson.Gson;
import com.my.afarycode.OnlineShopping.CheckOutScreen;
import com.my.afarycode.OnlineShopping.Model.Add_to_Wish;
import com.my.afarycode.OnlineShopping.Model.CartModal;
import com.my.afarycode.OnlineShopping.Model.DeleteCartModal;
import com.my.afarycode.OnlineShopping.Model.HomeShopeProductModel;
import com.my.afarycode.OnlineShopping.Model.UpdateCartModal;
import com.my.afarycode.OnlineShopping.ProductDetailAct;
import com.my.afarycode.OnlineShopping.adapter.CardAdapter;
import com.my.afarycode.OnlineShopping.constant.PreferenceConnector;
import com.my.afarycode.OnlineShopping.helper.DataManager;
import com.my.afarycode.R;
import com.my.afarycode.Splash;
import com.my.afarycode.databinding.ActivityCardBinding;
import com.my.afarycode.listener.OnPositionListener;
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

public class CardAct extends AppCompatActivity implements OnPositionListener {
    ActivityCardBinding binding;
    CardAdapter mAdapter;
    private ArrayList<HomeShopeProductModel> modelList = new ArrayList<>();
    Fragment fragment;
    private AfaryCode apiInterface;
    private ArrayList<CartModal.Result> get_result;
    private CardAdapter adapter;
    private String itemAmount="0";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_card);
        apiInterface = ApiClient.getClient(CardAct.this).create(AfaryCode.class);

        SetupUI();

        binding.RRback.setOnClickListener(v -> {onBackPressed();
        });

        binding.CheckOutScreen.setOnClickListener(v -> {
          //  fragment = new CheckOutDelivery();
          //  loadFragment(fragment);
            startActivity(new Intent(CardAct.this,CheckOutDeliveryAct.class)
                    .putExtra("sellerId",get_result.get(0).getShopId()));
        });

    }

    private void SetupUI() {
        get_result = new ArrayList<>();

        binding.recycler.setHasFixedSize(true);
        adapter = new CardAdapter(CardAct.this, get_result,CardAct.this);
        binding.recycler.setAdapter(adapter);

    }

    private void GetCartItem() {

        DataManager.getInstance().showProgressMessage(CardAct.this, "Please wait...");
        Map<String,String> headerMap = new HashMap<>();
        headerMap.put("Authorization","Bearer " +PreferenceConnector.readString(CardAct.this, PreferenceConnector.access_token,""));
        headerMap.put("Accept","application/json");

        Map<String, String> map = new HashMap<>();
        map.put("user_id", PreferenceConnector.readString(CardAct.this, PreferenceConnector.User_id, ""));
        map.put("register_id", PreferenceConnector.readString(CardAct.this, PreferenceConnector.Register_id, ""));
        map.put("country_id",PreferenceConnector.readString(CardAct.this, PreferenceConnector.countryId, ""));

        Log.e("MapMap", "EXERSICE LIST" + map);

        Call<CartModal> loginCall = apiInterface.get_cart(headerMap,map);

        loginCall.enqueue(new Callback<CartModal>() {

            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onResponse(Call<CartModal> call, Response<CartModal> response) {

                DataManager.getInstance().hideProgressMessage();

                try {

                    CartModal data = response.body();
                    String dataResponse = new Gson().toJson(response.body());
                    Log.e("MapMap", "Exersice_List" + dataResponse);

                    if (data.status.equals("1")) {
                        get_result.clear();
                        get_result.addAll(data.getResult());
                        adapter.notifyDataSetChanged();
                        binding.CheckOutScreen.setVisibility(View.VISIBLE);

                    } else if (data.status.equals("0")) {
                        Toast.makeText(CardAct.this, "No Data Found !!!!", Toast.LENGTH_SHORT).show();
                        binding.CheckOutScreen.setVisibility(View.GONE);
                        get_result.clear();
                        adapter.notifyDataSetChanged();


                    }

                    else if (data.status.equals("5")) {
                        PreferenceConnector.writeString(CardAct.this, PreferenceConnector.LoginStatus, "false");
                        startActivity(new Intent(CardAct.this, Splash.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP));
                        finish();
                    }


                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<CartModal> call, Throwable t) {
                call.cancel();
                DataManager.getInstance().hideProgressMessage();
            }
        });
    }



    @Override
    public void onResume() {
        super.onResume();
        GetCartItem();
    }

/*
    public boolean loadFragment(Fragment fragment) {
        FragmentManager fragmentManager = CardAct.this.getSupportFragmentManager();
        if (fragment != null) {
            fragmentManager.beginTransaction()
                    .addToBackStack("Home")
                    .replace(R.id.fragment_homeContainer, fragment)//, tag)
                    .commit();
            return true;
        }
        return false;
    }
*/


    private void AddToWIshListAPI(String cartId) {

        DataManager.getInstance().showProgressMessage(CardAct.this, "Please wait...");
        Map<String,String> headerMap = new HashMap<>();
        headerMap.put("Authorization","Bearer " +PreferenceConnector.readString(CardAct.this, PreferenceConnector.access_token,""));
        headerMap.put("Accept","application/json");



        Map<String, String> map = new HashMap<>();
        map.put("user_id", PreferenceConnector.readString(CardAct.this, PreferenceConnector.User_id, ""));
        map.put("product_id", cartId);
        map.put("register_id", PreferenceConnector.readString(CardAct.this, PreferenceConnector.Register_id, ""));

        Log.e("MapMap", "Add to WishList LIST" + map);


        Log.e("MapMap", "EXERSICE LIST" + map);

        Call<ResponseBody> loginCall = apiInterface.addToFavApi(headerMap,map);

        loginCall.enqueue(new Callback<ResponseBody>() {

            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                DataManager.getInstance().hideProgressMessage();


                try {
                    Log.e("response===",response.body().toString());
                    String stringResponse = response.body().string();
                    JSONObject jsonObject = new JSONObject(stringResponse);

                    if (jsonObject.getString("status").toString().equals("1")) {
                        Toast.makeText(CardAct.this, " Add Wish List ", Toast.LENGTH_SHORT).show();
                    }   else if (jsonObject.getString("status").toString().equals("0")) {

                        Toast.makeText(CardAct.this, jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                    }

                    else if (jsonObject.getString("status").equals("5")) {
                        PreferenceConnector.writeString(CardAct.this, PreferenceConnector.LoginStatus, "false");
                        startActivity(new Intent(CardAct.this, Splash.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP));
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

    private void DeleteAPI(String cartId,String id) {

        DataManager.getInstance().showProgressMessage(CardAct.this, "Please wait...");
        Map<String,String> headerMap = new HashMap<>();
        headerMap.put("Authorization","Bearer " +PreferenceConnector.readString(CardAct.this, PreferenceConnector.access_token,""));
        headerMap.put("Accept","application/json");

        Map<String, String> map = new HashMap<>();
        map.put("user_id", PreferenceConnector.readString(CardAct.this, PreferenceConnector.User_id, ""));
        map.put("register_id", PreferenceConnector.readString(CardAct.this, PreferenceConnector.Register_id, ""));

        map.put("cart_id", cartId);
        map.put("id", id);

        Log.e("MapMap", "EXERSICE LIST" + map);

        Call<DeleteCartModal> loginCall = apiInterface.delete_cart(headerMap,map);

        loginCall.enqueue(new Callback<DeleteCartModal>() {

            @Override
            public void onResponse(Call<DeleteCartModal> call, Response<DeleteCartModal> response) {

                DataManager.getInstance().hideProgressMessage();

                try {

                    DeleteCartModal data = response.body();
                    String dataResponse = new Gson().toJson(response.body());
                    Log.e("MapMap", "Exersice_List" + dataResponse);

                    if (data.status.equals("1")) {
                        GetCartItem();
                       /* fragment1 = new CardActivity();
                        loadFragment(fragment1);*/

                        Toast.makeText(CardAct.this, "delete cart item ", Toast.LENGTH_SHORT).show();

                    } else if (data.status.equals("0")) {
                        Toast.makeText(CardAct.this, data.message, Toast.LENGTH_SHORT).show();
                        GetCartItem();
                    }

                    else if (data.status.equals("5")) {
                        PreferenceConnector.writeString(CardAct.this, PreferenceConnector.LoginStatus, "false");
                        startActivity(new Intent(CardAct.this, Splash.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP));
                        finish();

                    }


                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<DeleteCartModal> call, Throwable t) {
                call.cancel();
                DataManager.getInstance().hideProgressMessage();
            }
        });

    }

    private void UpdateQuanityAPI(String cart_id, String proId,String id, int count) {

        DataManager.getInstance().showProgressMessage(CardAct.this, "Please wait...");
        Map<String,String> headerMap = new HashMap<>();
        headerMap.put("Authorization","Bearer " +PreferenceConnector.readString(CardAct.this, PreferenceConnector.access_token,""));
        headerMap.put("Accept","application/json");

        Map<String, String> map = new HashMap<>();
        map.put("user_id", PreferenceConnector.readString(CardAct.this, PreferenceConnector.User_id, ""));
        map.put("register_id", PreferenceConnector.readString(CardAct.this, PreferenceConnector.Register_id, ""));

        // map.put("cart_id", cart_id);
        map.put("pro_id", proId);
        map.put("id", id);
        map.put("quantity", String.valueOf(count));


        map.put("quantity", String.valueOf(count));

        Log.e("MapMap", "EXERSICE LIST" + map);

        Call<UpdateCartModal> loginCall = apiInterface.update_cart(headerMap,map);

        loginCall.enqueue(new Callback<UpdateCartModal>() {

            @Override
            public void onResponse(Call<UpdateCartModal> call, Response<UpdateCartModal> response) {

                DataManager.getInstance().hideProgressMessage();

                try {

                    UpdateCartModal data = response.body();
                    String dataResponse = new Gson().toJson(response.body());
                    Log.e("MapMap", "Exersice_List" + dataResponse);

                    if (data.status.equals("1")) {

                     //   itemAmount = data.getResult().itemAmount;
                     //   subTotal.setText("Rs. " + itemAmount);

                     //   double final_amount = totalPriceToToPay1 + Integer.valueOf(itemAmount);

                   //     total_price_to_to_pay.setText("Rs. " + final_amount);


                        Toast.makeText(CardAct.this, "update quantity", Toast.LENGTH_SHORT).show();

                    } else if (data.status.equals("0")) {
                        Toast.makeText(CardAct.this, data.message, Toast.LENGTH_SHORT).show();
                    }

                    else if (data.status.equals("5")) {
                        // Toast.makeText(getContext(), "No Data Found !!!!", Toast.LENGTH_SHORT).show();

                        PreferenceConnector.writeString(CardAct.this, PreferenceConnector.LoginStatus, "false");
                        startActivity(new Intent(CardAct.this, Splash.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP));
                        finish();

                    }


                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<UpdateCartModal> call, Throwable t) {
                call.cancel();
                DataManager.getInstance().hideProgressMessage();
            }
        });

    }


    @Override
    public void onPos(int position, String type, String value) {
        if(type.equals("Update")) UpdateQuanityAPI(get_result.get(position).cartId,get_result.get(position).getItemId(),get_result.get(position).getId(), Integer.parseInt(value));
        else if(type.equals("Delete")) DeleteAPI(get_result.get(position).cartId,get_result.get(position).getId());
        else if(type.equals("Wishlist")) AddToWIshListAPI(get_result.get(position).proId);

    }}
