package com.my.afarycode.OnlineShopping;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.my.afarycode.OnlineShopping.Model.Add_to_Wish;
import com.my.afarycode.OnlineShopping.Model.CartModal;
import com.my.afarycode.OnlineShopping.Model.DeleteCartModal;
import com.my.afarycode.OnlineShopping.Model.DeliveryModel;
import com.my.afarycode.OnlineShopping.Model.Get_Transaction_Modal;
import com.my.afarycode.OnlineShopping.Model.HomeShopeProductModel;
import com.my.afarycode.OnlineShopping.Model.LocationModel;
import com.my.afarycode.OnlineShopping.Model.UpdateCartModal;
import com.my.afarycode.OnlineShopping.activity.CheckOutDeliveryAct;
import com.my.afarycode.OnlineShopping.adapter.CardAdapter;
import com.my.afarycode.OnlineShopping.adapter.CheckoutAdapter;
import com.my.afarycode.OnlineShopping.constant.PreferenceConnector;
import com.my.afarycode.OnlineShopping.helper.DataManager;
import com.my.afarycode.OnlineShopping.helper.NetworkAvailablity;
import com.my.afarycode.R;
import com.my.afarycode.Splash;
import com.my.afarycode.databinding.ActivityCheckOutScreenBinding;
import com.my.afarycode.listener.OnPositionListener;
import com.my.afarycode.ratrofit.AfaryCode;
import com.my.afarycode.ratrofit.ApiClient;

import org.json.JSONObject;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CheckOutScreen extends AppCompatActivity implements OnPositionListener {
    public String TAG = "CheckOutScreen";
    ActivityCheckOutScreenBinding binding;
    private AfaryCode apiInterface;
    private ArrayList<CartModal.Result> get_result;
    private CheckoutAdapter adapter;
    public static TextView subTotal;
    public static TextView tax2, total_price_to_to_pay, tax1, plateform_fees;
    public static double totalPriceToToPay1;
    private double totalPriceToToPay = 0, platFormsFees = 0.0, taxN1 = 0.0, taxN2 = 0.0, deliveryFees, mainTotalPay = 0.0;
    private String get_cart_id = "",deliveryMethod="";
    private ArrayList<String> get_cart_id_list = new ArrayList<>();
    private String itemAmount = "0";
    private ArrayList<DeliveryModel.Result> arrayList;
    String Chkkkk;


    String deliveryAgencyType="",deliveryYesNo="",addressId="";
    String deliveryCharge="0.0";

    String deliveryAgencyId="",sendToServer="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_check_out_screen);
        apiInterface = ApiClient.getClient(this).create(AfaryCode.class);

        binding.RRback.setOnClickListener(v -> {
            onBackPressed();
        });

        binding.cardCheckOut.setOnClickListener(v -> {

            startActivity(new Intent(CheckOutScreen.this, CheckOutPayment.class)
                  //  .putStringArrayListExtra("cart_id_array", get_cart_id)
                    .putExtra("cart_id_array", get_cart_id)
                    .putExtra("totalPriceToToPay", "" + totalPriceToToPay)
                    .putExtra("deliveryCharge", "" + deliveryFees)
                    .putExtra("platFormsFees", "" + platFormsFees)
                    .putExtra("taxN1", "" + taxN1)
                    .putExtra("taxN2", "" + taxN2)
                    .putExtra("sendToServer",sendToServer)
                    .putExtra("selfCollect",deliveryYesNo));

        });

        Setupui();
    }

    private void Setupui() {

        if(getIntent()!=null){
            deliveryAgencyType = getIntent().getStringExtra("agency");
            deliveryCharge = getIntent().getStringExtra("charge");
            deliveryAgencyId = getIntent().getStringExtra("agencyId");
            deliveryYesNo =  getIntent().getStringExtra("deliveryYesNo");
            deliveryMethod = getIntent().getStringExtra("deliveryMethod");
            addressId =  getIntent().getStringExtra("addressId");
        }

        get_result = new ArrayList<>();
        arrayList = new ArrayList<>();
        subTotal = (TextView) findViewById(R.id.sub_total);

        plateform_fees = (TextView) findViewById(R.id.plateform_fees);
        tax1 = (TextView) findViewById(R.id.tvTax1);
        tax2 = (TextView) findViewById(R.id.tvtax2);
        total_price_to_to_pay = (TextView) findViewById(R.id.total_price_to_to_pay);

        adapter = new CheckoutAdapter(CheckOutScreen.this, get_result, CheckOutScreen.this);
        binding.recyclerCheckout.setAdapter(adapter);


        GetCartItem();
    }

    private void GetCartItem() {

        DataManager.getInstance().showProgressMessage(this, getString(R.string.please_wait));
        Map<String,String> headerMap = new HashMap<>();
        headerMap.put("Authorization","Bearer " +PreferenceConnector.readString(CheckOutScreen.this, PreferenceConnector.access_token,""));
        headerMap.put("Accept","application/json");
        Map<String, String> map = new HashMap<>();
        map.put("user_id", PreferenceConnector.readString(this, PreferenceConnector.User_id, ""));
        map.put("register_id", PreferenceConnector.readString(this, PreferenceConnector.Register_id, ""));

        Log.e("MapMap", "FINAL CART  LIST" + map);

        Call<CartModal> loginCall = apiInterface.get_cart(headerMap,map);

        loginCall.enqueue(new Callback<CartModal>() {

            @Override
            public void onResponse(Call<CartModal> call, Response<CartModal> response) {

                DataManager.getInstance().hideProgressMessage();

                try {

                    CartModal data = response.body();
                    String dataResponse = new Gson().toJson(response.body());
                    Log.e("MapMap", "Cart_List" + dataResponse);

                    if (data.status.equals("1")) {

                        get_result.clear();
                        get_result.addAll(data.getResult());


                        adapter.notifyDataSetChanged();
                        binding.llTotals.setVisibility(View.VISIBLE);
                        binding.RRbtm.setVisibility(View.VISIBLE);
                        int currentValue = 0;
                        int lastValue = 0;

                    /*    for (int i = 0; i < get_result.size(); i++) {
                          //  get_cart_id = get_result.get(0).getCartId();
                            get_cart_id = get_result.get(0).getId();

                          //  get_cart_id_list.add(get_cart_id);
                            mainTotalPay = Double.parseDouble(get_result.get(i).getProductPrice()) * Integer.parseInt(get_result.get(i).getQuantity());

                            currentValue = i;


                            if (get_result.get(0).getShopId().equals(get_result.get(currentValue).getShopId())) {
                                Chkkkk = "Yes";
                                // Log.e("Zero Position==",get_result.get(0).getShopId());
                                Log.e(" Position==", +i + " " + "Value " + get_result.get(i).getShopId());

                            } else {
                                Chkkkk = "No";
                                // Log.e("Zero Position==",get_result.get(0).getShopId());
                                Log.e(" Position==", +i + " " + "Value " + get_result.get(i).getShopId());
                            }
                        }*/
                     //   Log.e("check log====", Chkkkk);
                      //  AllTax(get_result.get(0).cartId, mainTotalPay + "", Chkkkk);
                        getAllTax();

                    } else if (data.status.equals("0")) {
                        get_result.clear();
                        adapter.notifyDataSetChanged();
                        binding.llTotals.setVisibility(View.GONE);
                        binding.RRbtm.setVisibility(View.GONE);
                        Toast.makeText(CheckOutScreen.this, "No Data Found !!!!", Toast.LENGTH_SHORT).show();
                    }

                    else if (data.status.equals("5")) {
                        PreferenceConnector.writeString(CheckOutScreen.this, PreferenceConnector.LoginStatus, "false");
                        startActivity(new Intent(CheckOutScreen.this, Splash.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP));
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


    private void AddToWIshListAPI(String cartId, String chk) {

        DataManager.getInstance().showProgressMessage(CheckOutScreen.this, getString(R.string.please_wait));
        Map<String,String> headerMap = new HashMap<>();
        headerMap.put("Authorization","Bearer " +PreferenceConnector.readString(CheckOutScreen.this, PreferenceConnector.access_token,""));
        headerMap.put("Accept","application/json");

        Map<String, String> map = new HashMap<>();
        map.put("user_id", PreferenceConnector.readString(CheckOutScreen.this, PreferenceConnector.User_id, ""));
        map.put("product_id", cartId);
        map.put("register_id", PreferenceConnector.readString(CheckOutScreen.this, PreferenceConnector.Register_id, ""));

        Log.e("MapMap", "Add to WishList LIST" + map);

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
                        Toast.makeText(CheckOutScreen.this, " Add Wish List ", Toast.LENGTH_SHORT).show();
                    }   else if (jsonObject.getString("status").toString().equals("0")) {

                        Toast.makeText(CheckOutScreen.this, jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                    }

                    else if (jsonObject.getString("status").equals("5")) {
                        PreferenceConnector.writeString(CheckOutScreen.this, PreferenceConnector.LoginStatus, "false");
                        startActivity(new Intent(CheckOutScreen.this, Splash.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP));
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

    private void DeleteAPI(String cartId, String id) {

        DataManager.getInstance().showProgressMessage(CheckOutScreen.this, getString(R.string.please_wait));
        Map<String,String> headerMap = new HashMap<>();
        headerMap.put("Authorization","Bearer " +PreferenceConnector.readString(CheckOutScreen.this, PreferenceConnector.access_token,""));
        headerMap.put("Accept","application/json");

        Map<String, String> map = new HashMap<>();
        map.put("user_id", PreferenceConnector.readString(CheckOutScreen.this, PreferenceConnector.User_id, ""));
        map.put("register_id", PreferenceConnector.readString(CheckOutScreen.this, PreferenceConnector.Register_id, ""));

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

                        Toast.makeText(CheckOutScreen.this, "delete cart item ", Toast.LENGTH_SHORT).show();

                    } else if (data.status.equals("0")) {
                        Toast.makeText(CheckOutScreen.this, data.message, Toast.LENGTH_SHORT).show();
                        GetCartItem();
                    }

                    else if (data.status.equals("5")) {
                        PreferenceConnector.writeString(CheckOutScreen.this, PreferenceConnector.LoginStatus, "false");
                        startActivity(new Intent(CheckOutScreen.this, Splash.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP));
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

    private void UpdateQuanityAPI(String cart_id, String proId, String id, int count) {

        DataManager.getInstance().showProgressMessage(CheckOutScreen.this, getString(R.string.please_wait));
        Map<String,String> headerMap = new HashMap<>();
        headerMap.put("Authorization","Bearer " +PreferenceConnector.readString(CheckOutScreen.this, PreferenceConnector.access_token,""));
        headerMap.put("Accept","application/json");

        Map<String, String> map = new HashMap<>();
        map.put("user_id", PreferenceConnector.readString(CheckOutScreen.this, PreferenceConnector.User_id, ""));
        map.put("register_id", PreferenceConnector.readString(CheckOutScreen.this, PreferenceConnector.Register_id, ""));

        //  map.put("cart_id", cart_id);
        map.put("pro_id", proId);
        map.put("id", cart_id);
        map.put("quantity", count+"");

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

                        // mainTotalPay  = mainTotalPay + Double.parseDouble(data.getResult().itemAmount);
                        // subTotal.setText("Rs. " + String.format("%.2f",mainTotalPay));

                        //  double final_amount = totalPriceToToPay1 + mainTotalPay;

                        //total_price_to_to_pay.setText("Rs. " + String.format("%.2f",final_amount));


                        Toast.makeText(CheckOutScreen.this, "update quantity", Toast.LENGTH_SHORT).show();
                        GetCartItem();
                    } else if (data.status.equals("0")) {
                        Toast.makeText(CheckOutScreen.this, data.message, Toast.LENGTH_SHORT).show();
                    }
                    else if (data.status.equals("5")) {
                        PreferenceConnector.writeString(CheckOutScreen.this, PreferenceConnector.LoginStatus, "false");
                        startActivity(new Intent(CheckOutScreen.this, Splash.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP));
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
        if (type.equals("Update"))
            UpdateQuanityAPI(get_result.get(position).id, get_result.get(position).getItemId(), get_result.get(position).getId(), Integer.parseInt(value));
        else if (type.equals("Delete"))
            DeleteAPI(get_result.get(position).cartId, get_result.get(position).getId());
        else if (type.equals("Wishlist")) {
            AddToWIshListAPI(get_result.get(position).getItemId(), value);

        }
    }

    public void AllTax(String cartId, String totalAmount, String ShopComp) {
        DataManager.getInstance().showProgressMessage(CheckOutScreen.this, getString(R.string.please_wait));
        Map<String,String> headerMap = new HashMap<>();
        headerMap.put("Authorization","Bearer " +PreferenceConnector.readString(CheckOutScreen.this, PreferenceConnector.access_token,""));
        headerMap.put("Accept","application/json");

        Map<String, String> map = new HashMap<>();
        // map.put("user_id", PreferenceConnector.readString(CheckOutScreen.this, PreferenceConnector.User_id, ""));
        map.put("cart_id", cartId);
        map.put("pickuplat", PreferenceConnector.readString(CheckOutScreen.this, PreferenceConnector.LAT, ""));
        map.put("pickuplon", PreferenceConnector.readString(CheckOutScreen.this, PreferenceConnector.LON, ""));
        map.put("yes_no", ShopComp);
        map.put("country_id",PreferenceConnector.readString(CheckOutScreen.this, PreferenceConnector.COUNTRY_ID,""));
        map.put("register_id", PreferenceConnector.readString(CheckOutScreen.this, PreferenceConnector.Register_id, ""));

        Log.e(TAG, "Get All Taxs Request :" + map);
        Call<ResponseBody> loginCall = apiInterface.getAllTax(headerMap,map);
        loginCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                DataManager.getInstance().hideProgressMessage();

                try {
                    String responseData = response.body() != null ? response.body().string() : "";
                    JSONObject object = new JSONObject(responseData);
                    Log.e(TAG, "Get All Taxs RESPONSE" + object);

                    if (object.optString("status").equals("1")) {
                        //   JSONObject jsonObject = object.getJSONObject("result");
                        DeliveryModel data = new Gson().fromJson(responseData, DeliveryModel.class);
                        arrayList.clear();
                        arrayList.addAll(data.getResult());
                        if (ShopComp.equals("No")) differentShops();
                        deliveryFees = 0.0;
                        platFormsFees = 0.0;
                        taxN1 = 0.0;
                        taxN2 = 0.0;

                            for (int i = 0; i < arrayList.size(); i++) {
                                platFormsFees = platFormsFees + Double.parseDouble(arrayList.get(i).getPlatformFees());
                                taxN1 = taxN1 + Double.parseDouble(arrayList.get(i).getTaxesFirst());
                                taxN2 = taxN2;   //+ Double.parseDouble(arrayList.get(i).getTaxesSecond());
                               if(deliveryAgencyType.equalsIgnoreCase("Afary Code")) deliveryFees = deliveryFees + Double.parseDouble(arrayList.get(i).getDeliveryFees());
                                 else  deliveryFees = Double.parseDouble(deliveryCharge);
                            }

                            totalPriceToToPay1 = platFormsFees + taxN1 + taxN2
                                    + deliveryFees;

                            totalPriceToToPay = Double.parseDouble(totalAmount)

                                    + platFormsFees
                                    + taxN1
                                    + taxN2
                                    + deliveryFees;

                            binding.plateformFees.setText("Rs. " + String.format("%.2f", platFormsFees));
                            binding.tvTax1.setText("Rs. " + String.format("%.2f", taxN1));
                            binding.tvtax2.setText("Rs. " + String.format("%.2f", taxN2));
                            binding.tvDelivery.setText("Rs. " + String.format("%.2f", deliveryFees));
                            binding.totalPriceToToPay.setText("Rs. " + String.format("%.2f", totalPriceToToPay));
                   //     } else {
                     //       differentShops();
                    //    }

                        differentShops();

                    } else if (object.optString("status").equals("0")) {
                        Toast.makeText(CheckOutScreen.this, object.getString("message"), Toast.LENGTH_SHORT).show();


                    }

                    else if (object.getString("status").equals("5")) {
                        PreferenceConnector.writeString(CheckOutScreen.this, PreferenceConnector.LoginStatus, "false");
                        startActivity(new Intent(CheckOutScreen.this, Splash.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP));
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

    private void differentShops() {
        DeliveryModel.Result max =  Collections.max(arrayList);
        System.out.println("ArrayList max value : " + max.getKm() + " for emp id : " + max.getId());
        if (PreferenceConnector.readString(CheckOutScreen.this, PreferenceConnector.LAT, "").equals("0.0")) {
            for (int i=0;i<arrayList.size();i++){
               // if(arrayList.get(i).getDeliveryCharges().equals("0")){
                    arrayList.get(i).setDeliveryFees("0");
               // }
               /* else {
                    if (max.getKm() == arrayList.get(i).getKm()) {
                        arrayList.get(i).setDeliveryFees("1500");
                    } else arrayList.get(i).setDeliveryFees("15");
                }*/

            }

        } else {
            for (int i=0;i<arrayList.size();i++){
                if(arrayList.get(i).getDeliveryCharges().equals("0")){
                    arrayList.get(i).setDeliveryFees("0");
                }
                else {
                    if (max.getKm() == arrayList.get(i).getKm()) {
                        arrayList.get(i).setDeliveryFees("15");
                    } else arrayList.get(i).setDeliveryFees("15");
                }
            }
        }


        for (int j=0;j<arrayList.size();j++){
            Log.e("Check Valuess", "Kilometer====" + arrayList.get(j).getKm() + "  deliveryFee ===" + arrayList.get(j).getDeliveryFees() );
        }
        Log.e("array====",arrayList.size()+"");

    }


    public void  getAllTax(){
        DataManager.getInstance().showProgressMessage(CheckOutScreen.this, getString(R.string.please_wait));
        Map<String,String> headerMap = new HashMap<>();
        headerMap.put("Authorization","Bearer " +PreferenceConnector.readString(CheckOutScreen.this, PreferenceConnector.access_token,""));
        headerMap.put("Accept","application/json");

        Map<String, String> map = new HashMap<>();
        map.put("user_id", PreferenceConnector.readString(CheckOutScreen.this, PreferenceConnector.User_id, ""));
        map.put("drop_lat", PreferenceConnector.readString(CheckOutScreen.this, PreferenceConnector.LAT, ""));
        map.put("drop_lon", PreferenceConnector.readString(CheckOutScreen.this, PreferenceConnector.LON, ""));
        map.put("country_id",PreferenceConnector.readString(CheckOutScreen.this, PreferenceConnector.COUNTRY_ID,""));
        map.put("delivery_partner",deliveryAgencyId);
        map.put("self_collect",deliveryYesNo);
        map.put("type",deliveryMethod);
        if(addressId!=null) map.put("address_id",addressId);
        else  map.put("address_id","");
        map.put("register_id", PreferenceConnector.readString(CheckOutScreen.this, PreferenceConnector.Register_id, ""));

        Log.e(TAG, "Get AllTax Request :" + map);
        Call<ResponseBody> loginCall = apiInterface.getAllTaxNew(headerMap,map);

        loginCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                DataManager.getInstance().hideProgressMessage();

                try {
                    String responseData = response.body() != null ? response.body().string() : "";
                    JSONObject object = new JSONObject(responseData);
                    Log.e(TAG, "Get All Taxs RESPONSE" + object);

                    if (object.optString("status").equals("1")) {
                         // JSONObject jsonObject = object.getJSONObject("result");
                        sendToServer = object.toString();
                       // sendTaxToServer(object.toString());
                        Locale currentLocale = Locale.getDefault();
                        NumberFormat numberFormat = NumberFormat.getInstance(currentLocale);

                       // Parse values using NumberFormat
                        mainTotalPay = numberFormat.parse(object.getString("sub_total")).doubleValue();
                        taxN1 = numberFormat.parse(object.getString("taxes_first")).doubleValue();
                        taxN2 = numberFormat.parse(object.getString("taxes_second")).doubleValue();

                        // Handle platform fees
                        if (!object.getString("platform_fees").equalsIgnoreCase("")) {
                            platFormsFees = numberFormat.parse(object.getString("platform_fees")).doubleValue();
                        } else {
                            platFormsFees = 0.00;
                        }

                        deliveryFees = numberFormat.parse(object.getString("total_delivery_fees")).doubleValue();
                        totalPriceToToPay =  numberFormat.parse(object.getString("total_payable_amount")).doubleValue();




                      /*  mainTotalPay = Double.parseDouble(object.getString("sub_total"));
                        taxN1 = Double.parseDouble(object.getString("taxes_first"));
                        taxN2 = Double.parseDouble(object.getString("taxes_second"));
                      if(!object.getString("platform_fees").equalsIgnoreCase(""))
                      {
                          platFormsFees = Double.parseDouble(object.getString("platform_fees"));
                      }
                      else {
                          platFormsFees =0.00;
                      }
                        deliveryFees = Double.parseDouble(object.getString("total_delivery_fees"));
                    //    totalPriceToToPay = Double.parseDouble(String.format("%.2f", Double.parseDouble(object.getString("total_payable_amount"))));

                        totalPriceToToPay =  Double.parseDouble(object.getString("total_payable_amount"));
*/
                        binding.tvTaxOne.setText(Html.fromHtml("Tax 1 "+ "   " + "<font color='#EE0000'>" + object.getString("taxes_first_percentage") + "%" + "</font>"));
                        binding.tvTaxTwo.setText(Html.fromHtml("Tax 2 "+ "   " + "<font color='#EE0000'>" + object.getString("taxes_second_percentage") + "%" + "</font>"));

                    //    binding.tvDeliveryCharge.setText(Html.fromHtml("Delivery Charge  " + "<font color='#EE0000'>"+ "(" + object.getString("distance_km") + "km)" + "</font>"));



                        if(deliveryMethod.equalsIgnoreCase("Vehicle"))
                          binding.tvDeliveryCharge.setText(Html.fromHtml("Delivery Charge " + "<font color='#EE0000'>"+ "(" + object.getString("distance_km") + "km)" + "</font>"));
                      else if(deliveryMethod.equalsIgnoreCase("Partner"))
                          binding.tvDeliveryCharge.setText(Html.fromHtml("Shipping agency fees " + "<font color='#EE0000'>"+ "(" + object.getString("distance_km") + "km)" + "</font>"));
                      else binding.tvDeliveryCharge.setText(Html.fromHtml("Delivery Charge " + "<font color='#EE0000'>"+ "(" + object.getString("distance_km") + "km)" + "</font>"));








                      /*  totalPriceToToPay1 = platFormsFees + taxN1 + taxN2
                        + deliveryFees;

                        totalPriceToToPay = Double.parseDouble(totalAmount)

                                + taxN1
                                + platFormsFees
                                + taxN2
                                + deliveryFees;*/

                        binding.plateformFees.setText("Rs. " + String.format("%.2f", platFormsFees));
                        binding.tvTax1.setText("Rs. " + String.format("%.2f", taxN1));
                        binding.tvtax2.setText("Rs. " + String.format("%.2f", taxN2));
                        binding.tvDelivery.setText("Rs. " + String.format("%.2f", deliveryFees));
                        binding.totalPriceToToPay.setText("Rs. " + String.format("%.2f", totalPriceToToPay));
                        binding.subTotal.setText("Rs. " + String.format("%.2f", mainTotalPay));




                    } else if (object.optString("status").equals("0")) {
                        Toast.makeText(CheckOutScreen.this, object.getString("message"), Toast.LENGTH_SHORT).show();


                    }
                    else if (object.getString("status").equals("5")) {
                        PreferenceConnector.writeString(CheckOutScreen.this, PreferenceConnector.LoginStatus, "false");
                        startActivity(new Intent(CheckOutScreen.this, Splash.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP));
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






    public void  sendTaxToServer(String sendData){
      //  DataManager.getInstance().showProgressMessage(CheckOutScreen.this, getString(R.string.please_wait));
        Map<String,String> headerMap = new HashMap<>();
        headerMap.put("Authorization","Bearer " +PreferenceConnector.readString(CheckOutScreen.this, PreferenceConnector.access_token,""));
        headerMap.put("Accept","application/json");

        Map<String, String> map = new HashMap<>();
        map.put("user_id", PreferenceConnector.readString(CheckOutScreen.this, PreferenceConnector.User_id, ""));
        map.put("sub_orderdata",sendData);
        map.put("register_id", PreferenceConnector.readString(CheckOutScreen.this, PreferenceConnector.Register_id, ""));

        Log.e(TAG, "Send AllTax Server Request :" + map);
        Call<ResponseBody> loginCall = apiInterface.sendTaxTServerApi(headerMap,map);
        loginCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                DataManager.getInstance().hideProgressMessage();

                try {
                    String responseData = response.body() != null ? response.body().string() : "";
                    JSONObject object = new JSONObject(responseData);
                    Log.e(TAG, "Send All Tax Server RESPONSE" + object);

                    if (object.optString("status").equals("1")) {
                        // JSONObject jsonObject = object.getJSONObject("result");


                    } else if (object.optString("status").equals("0")) {
                        Toast.makeText(CheckOutScreen.this, object.getString("message"), Toast.LENGTH_SHORT).show();


                    }

                    else if (object.getString("status").equals("5")) {
                        PreferenceConnector.writeString(CheckOutScreen.this, PreferenceConnector.LoginStatus, "false");
                        startActivity(new Intent(CheckOutScreen.this, Splash.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP));
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