package com.my.afarycode.OnlineShopping.activity;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.my.afarycode.OnlineShopping.CheckOutDelivery;
import com.my.afarycode.OnlineShopping.CheckOutPayment;
import com.my.afarycode.OnlineShopping.CheckOutScreen;
import com.my.afarycode.OnlineShopping.Model.Add_Address_Modal;
import com.my.afarycode.OnlineShopping.Model.DeliveryAgencyModel;
import com.my.afarycode.OnlineShopping.Model.DeliveryPartnerModel;
import com.my.afarycode.OnlineShopping.Model.DeliveryTypeModel;
import com.my.afarycode.OnlineShopping.Model.LocationModel;
import com.my.afarycode.OnlineShopping.adapter.DeliveryAgencyAdapter;
import com.my.afarycode.OnlineShopping.adapter.DeliveryPartnerAdapter;
import com.my.afarycode.OnlineShopping.adapter.DeliveryTypeAdapter;
import com.my.afarycode.OnlineShopping.adapter.LocationAdapter;
import com.my.afarycode.OnlineShopping.bottomsheet.EditAddressFragment;
import com.my.afarycode.OnlineShopping.constant.PreferenceConnector;
import com.my.afarycode.OnlineShopping.fragment.AddAddressFragment;
import com.my.afarycode.OnlineShopping.helper.DataManager;
import com.my.afarycode.OnlineShopping.helper.NetworkAvailablity;
import com.my.afarycode.OnlineShopping.listener.addAddressListener;
import com.my.afarycode.OnlineShopping.listener.onPosListener;
import com.my.afarycode.R;
import com.my.afarycode.Splash;
import com.my.afarycode.databinding.ActivityCheckOutDeliveryBinding;
import com.my.afarycode.ratrofit.AfaryCode;
import com.my.afarycode.ratrofit.ApiClient;

import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CheckOutDeliveryAct extends AppCompatActivity implements addAddressListener, onPosListener {
    public String TAG = "CheckOutDelivery";
    ActivityCheckOutDeliveryBinding binding;


    private View promptsView1;
    private AlertDialog alertDialog1;
    public static EditText address_select;
    private AfaryCode apiInterface;
    private EditText city;
    private EditText location_details;
    private EditText phone_number;
    private String phone_no = "", type = "",productId="",shopId="",cityType="",selectAddressCityId="";
    ArrayList<LocationModel.Result> arrayList;
    LocationAdapter adapter;
    String deliveryType="",lat="",deliveryAgencyName="",deliveryAgencyImg="",selectedDeliveryAddress="";
    String deliveryAgencyType="",agencyId="",deliveryYesNo="No",deliveryMethod="",addressId="",aa="",whoWillDeliver="",urbanDelivery="",deliveryPlaceAccuracy="";
    String deliveryCharge="0.0",deliveryPartnerCharge="",partnerId="",partnerMethod="",deliveryPartnerName="",deliveryPartnerImg="";




    ArrayList<DeliveryTypeModel.Result> deliverArrayList;
    DeliveryTypeAdapter deliveryTypeAdapter;


    DeliveryAgencyAdapter deliveryAgencyAdapter;
    Dialog mDialog,deliveryPartnerDialog,deliveryAccuracyDialog;


    ArrayList<DeliveryAgencyModel.Result> deliveryAgencyList;
    ArrayList<DeliveryPartnerModel.Result> deliveryPartnerList;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_check_out_delivery);
        apiInterface = ApiClient.getClient(CheckOutDeliveryAct.this).create(AfaryCode.class);

        if (getIntent()!=null)
        {
            shopId = getIntent().getStringExtra("sellerId");


        }

        binding.RRback.setOnClickListener(v -> {
            onBackPressed();
        });


   /*     binding.rdGrp.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener()
        {
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch(checkedId){
                    case R.id.rdDontWant:
                        deliveryType = "Don't want delivery";
                        if(adapter!=null) adapter.changesOnSelection(false);
                        PreferenceConnector.writeString(CheckOutDeliveryAct.this, PreferenceConnector.LAT, "0.0");
                        PreferenceConnector.writeString(CheckOutDeliveryAct.this, PreferenceConnector.LON,"0.0");
                        break;
                    case R.id.rdAddAddress:
                        deliveryType = "delivery on address";
                        callBottomSheet();
                        break;


                }
            }
        });*/


        binding.llAddress.setOnClickListener(view -> callBottomSheet("",""));



/*
        binding.llDelivry.setOnClickListener(v -> {
            if(deliveryType.equalsIgnoreCase("")) Toast.makeText(CheckOutDeliveryAct.this, getString(R.string.please_select_address_type), Toast.LENGTH_SHORT).show();
            else startActivity(new Intent(CheckOutDeliveryAct.this, CheckOutScreen.class));
        });*/


        binding.llDelivry.setOnClickListener(v -> {
          /*  if(deliveryType.equalsIgnoreCase("Don't want delivery")){
                startActivity(new Intent(CheckOutDeliveryAct.this, CheckOutScreen.class));
            }
            else  if(deliveryType.equalsIgnoreCase("delivery on address")) {
                if(lat.equals("")) Toast.makeText(CheckOutDeliveryAct.this, getString(R.string.please_select_address_type), Toast.LENGTH_SHORT).show();
                else  startActivity(new Intent(CheckOutDeliveryAct.this, CheckOutScreen.class));
            }
            else */if(deliveryType.equalsIgnoreCase("")) {
                Toast.makeText(CheckOutDeliveryAct.this, getString(R.string.please_select_address_type), Toast.LENGTH_SHORT).show();
            }
            else if ( deliveryAgencyList.size()>1 && agencyId.equals("") ) {
                Toast.makeText(CheckOutDeliveryAct.this, getString(R.string.please_select_delivery_agency), Toast.LENGTH_SHORT).show();
            } else {
                Log.e("agencyList size====",deliveryAgencyList.size()+"");
                Log.e("delivery agency id====",agencyId);
                startActivity(new Intent(CheckOutDeliveryAct.this, CheckOutScreen.class)
                        .putExtra("agency",deliveryAgencyType)
                        .putExtra("charge",deliveryCharge)
                        .putExtra("agencyId",agencyId)
                        .putExtra("deliveryYesNo",deliveryYesNo)
                        .putExtra("deliveryMethod",deliveryMethod)
                        .putExtra("deliveryAgencyName",deliveryAgencyName)
                        .putExtra("deliveryAgencyImg",deliveryAgencyImg)
                        .putExtra("addressId",addressId)
                        .putExtra("aa",aa));
                deliveryMethod = "";
                addressId ="";
                agencyId="";
                deliveryType="";
                Log.e("country id=====", PreferenceConnector.readString(CheckOutDeliveryAct.this, PreferenceConnector.countryId, ""));

            }

            //else startActivity(new Intent(getActivity(), CheckOutScreen.class));

        });







        arrayList = new ArrayList<>();
        deliverArrayList = new ArrayList<>();
        deliveryAgencyList = new ArrayList<>();
        deliveryPartnerList = new ArrayList<>();

        adapter = new LocationAdapter(CheckOutDeliveryAct.this,arrayList, CheckOutDeliveryAct.this);
        binding.rvLocation.setAdapter(adapter);


        deliveryTypeAdapter = new DeliveryTypeAdapter(CheckOutDeliveryAct.this,deliverArrayList,CheckOutDeliveryAct.this);
        binding.rvDeliveryType.setAdapter(deliveryTypeAdapter);


      //  deliveryAgencyAdapter = new DeliveryAgencyAdapter(CheckOutDeliveryAct.this,deliveryAgencyList,CheckOutDeliveryAct.this);
      //  binding.rvDeliveryAgency.setAdapter(deliveryAgencyAdapter);



        binding.rdDontDelivery.setOnClickListener(view -> {
            deliveryType = "I don't want to be delivered, I will come and collect package myself";
            deliveryYesNo = "Yes";
            aa ="NATIONAL";
            binding.rdDontDelivery.setChecked(true);

            for (int i=0;i<deliverArrayList.size();i++){
                deliverArrayList.get(i).setChk(false);
            }
            deliveryTypeAdapter.notifyDataSetChanged();

            for (int i=0;i<arrayList.size();i++){
                arrayList.get(i).setChk(false);
            }
            adapter.notifyDataSetChanged();
          //  deliveryAgencyList.clear();
          //  deliveryAgencyAdapter.notifyDataSetChanged();

            dialogDontDelivery();
        });

    }

    @Override
    public void onResume() {
        super.onResume();
        if(NetworkAvailablity.checkNetworkStatus(CheckOutDeliveryAct.this)) getLocation();
        else Toast.makeText(CheckOutDeliveryAct.this, getString(R.string.network_failure), Toast.LENGTH_SHORT).show();
    }

    public void callBottomSheet(String title,String id){
        new AddAddressFragment(title,id).callBack(this::onAddress).show(getSupportFragmentManager(),"");
    }

    private void SetAddressAPI() {

        DataManager.getInstance().showProgressMessage(CheckOutDeliveryAct.this, "Please wait...");
        Map<String,String> headerMap = new HashMap<>();
        headerMap.put("Authorization","Bearer " +PreferenceConnector.readString(CheckOutDeliveryAct.this, PreferenceConnector.access_token,""));
        headerMap.put("Accept","application/json");

        Map<String, String> map = new HashMap<>();
        map.put("user_id", PreferenceConnector.readString(CheckOutDeliveryAct.this, PreferenceConnector.User_id, ""));
        map.put("indicate_address_1", address_select.getText().toString());
        map.put("indicate_address_2", "");
        map.put("indicate_address_3", "");
        map.put("location_1", location_details.getText().toString());
        map.put("location_2", location_details.getText().toString());
        map.put("location_3", "");
        map.put("city_1", city.getText().toString());
        map.put("city_2", "");
        map.put("city_3", "");
        map.put("phone", "" + phone_no);
        map.put("type", "" + type);
        map.put("register_id", PreferenceConnector.readString(CheckOutDeliveryAct.this, PreferenceConnector.Register_id, ""));

        Log.e("MapMap", "LOGIN REQUEST" + map);

        Call<Add_Address_Modal> SignupCall = apiInterface.add_address(headerMap,map);

        SignupCall.enqueue(new Callback<Add_Address_Modal>() {
            @Override
            public void onResponse(Call<Add_Address_Modal> call, Response<Add_Address_Modal> response) {
                DataManager.getInstance().hideProgressMessage();

                try {
                    Add_Address_Modal data = response.body();
                    String dataResponse = new Gson().toJson(response.body());
                    Log.e("MapMap", "LOGIN RESPONSE" + dataResponse);

                    if (data.status.equals("1")) {

                        Toast.makeText(CheckOutDeliveryAct.this, data.message, Toast.LENGTH_SHORT).show();

                    } else if (data.status.equals("0")) {
                        Toast.makeText(CheckOutDeliveryAct.this, data.message, Toast.LENGTH_SHORT).show();
                    }

                    else if (data.status.equals("5")) {
                        PreferenceConnector.writeString(CheckOutDeliveryAct.this, PreferenceConnector.LoginStatus, "false");
                        startActivity(new Intent(CheckOutDeliveryAct.this, Splash.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP));
                        finish();
                    }


                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<Add_Address_Modal> call, Throwable t) {
                call.cancel();
                DataManager.getInstance().hideProgressMessage();
            }
        });
    }

    @Override
    public void onAddress(String note) {
        if(NetworkAvailablity.checkNetworkStatus(CheckOutDeliveryAct.this)) getLocation();
        else Toast.makeText(CheckOutDeliveryAct.this, getString(R.string.network_failure), Toast.LENGTH_SHORT).show();    }

    public void getLocation(){
        DataManager.getInstance().showProgressMessage(CheckOutDeliveryAct.this, getString(R.string.please_wait));
        Map<String,String> headerMap = new HashMap<>();
        headerMap.put("Authorization","Bearer " +PreferenceConnector.readString(CheckOutDeliveryAct.this, PreferenceConnector.access_token,""));
        headerMap.put("Accept","application/json");

        Map<String, String> map = new HashMap<>();
        map.put("user_id", PreferenceConnector.readString(CheckOutDeliveryAct.this, PreferenceConnector.User_id, ""));
        map.put("register_id", PreferenceConnector.readString(CheckOutDeliveryAct.this, PreferenceConnector.Register_id, ""));

        Log.e(TAG, "Get Location Request :" + map);
        Call<ResponseBody> loginCall = apiInterface.getAddress(headerMap,map);
        loginCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                DataManager.getInstance().hideProgressMessage();

                try {
                    String responseData = response.body() != null ? response.body().string() : "";
                    JSONObject object = new JSONObject(responseData);
                    Log.e(TAG, "Get Location RESPONSE" + object);

                    if (object.optString("status").equals("1")) {
                        LocationModel data = new Gson().fromJson(responseData, LocationModel.class);
                        arrayList.clear();
                        arrayList.addAll(data.getResult());
                        adapter.notifyDataSetChanged();

                    } else if (object.optString("status").equals("0")) {
                        arrayList.clear();
                        adapter.notifyDataSetChanged();

                    }

                    else if (object.getString("status").equals("5")) {
                        PreferenceConnector.writeString(CheckOutDeliveryAct.this, PreferenceConnector.LoginStatus, "false");
                        startActivity(new Intent(CheckOutDeliveryAct.this, Splash.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP));
                        finish();
                    }
                    if(NetworkAvailablity.checkNetworkStatus(CheckOutDeliveryAct.this)) geDeliveryType();
                    else Toast.makeText(CheckOutDeliveryAct.this, getString(R.string.network_failure), Toast.LENGTH_SHORT).show();



                }  catch (Exception e) {
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


    public void geDeliveryType(){
        DataManager.getInstance().showProgressMessage(CheckOutDeliveryAct.this, getString(R.string.please_wait));

        Map<String,String> headerMap = new HashMap<>();
        headerMap.put("Authorization","Bearer " +PreferenceConnector.readString(CheckOutDeliveryAct.this, PreferenceConnector.access_token,""));
        headerMap.put("Accept","application/json");
        Call<ResponseBody> loginCall = apiInterface.getDelivery(headerMap,PreferenceConnector.readString(CheckOutDeliveryAct.this, PreferenceConnector.Register_id,"")
                ,PreferenceConnector.readString(CheckOutDeliveryAct.this, PreferenceConnector.User_id,""));        loginCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                DataManager.getInstance().hideProgressMessage();

                try {
                    String responseData = response.body() != null ? response.body().string() : "";
                    JSONObject object = new JSONObject(responseData);
                    Log.e(TAG, "Get Location RESPONSE" + object);

                    if (object.optString("status").equals("1")) {
                        DeliveryTypeModel data = new Gson().fromJson(responseData, DeliveryTypeModel.class);
                        deliverArrayList.clear();
                        deliverArrayList.addAll(data.getResult());
                        //"title":"My Home Address" "type":"My Home Address"
                        if(arrayList.size()>0) {
                            for (int j = 0; j < arrayList.size(); j++) {
                            for (int i = 0; i < deliverArrayList.size(); i++) {
                                if (deliverArrayList.get(i).getTitle().equalsIgnoreCase(arrayList.get(j).getType())) {
                                    //deliverArrayList.get(i).setVisible(false);
                                    deliverArrayList.remove(i);
                                }
                                //else  deliverArrayList.get(i).setVisible(true);
                                  //  Log.e("delivery compare====",deliverArrayList.get(i).getTitle() +"   position   " + arrayList.get(j).getType());

                               //    Log.e("delivery Type====",deliverArrayList.get(i).isVisible()+"   position " + i);

                                }
                            }
                        }else
                        {
                            for (int j = 0; j < deliverArrayList.size(); j++) {
                                   deliverArrayList.get(j).setVisible(true);}
                        }


                        deliveryTypeAdapter.notifyDataSetChanged();

                    } else if (object.optString("status").equals("0")) {
                        deliverArrayList.clear();
                        deliveryTypeAdapter.notifyDataSetChanged();

                    }

                    else if (object.getString("status").equals("5")) {
                        PreferenceConnector.writeString(CheckOutDeliveryAct.this, PreferenceConnector.LoginStatus, "false");
                        startActivity(new Intent(CheckOutDeliveryAct.this, Splash.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP));
                        finish();
                    }


                }  catch (Exception e) {
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
    public void onPos(int position) {

    }

    @Override
    public void onPos(int position, String Type) {
        if(Type.equals("Delete")) DeleteAlert(arrayList.get(position).getId());

        else if(Type.equals("Save")) {
          //  binding.rdDontWant.setChecked(false);
         //   binding.rdAddAddress.setChecked(true);
            lat = arrayList.get(position).getLat();
            deliveryType = arrayList.get(position).getLat();
            PreferenceConnector.writeString(CheckOutDeliveryAct.this, PreferenceConnector.LAT, arrayList.get(position).getLat());
            PreferenceConnector.writeString(CheckOutDeliveryAct.this, PreferenceConnector.LON, arrayList.get(position).getLon());
            PreferenceConnector.writeString(CheckOutDeliveryAct.this, PreferenceConnector.COUNTRY_ID, arrayList.get(position).getCountry());
            PreferenceConnector.writeString(CheckOutDeliveryAct.this,PreferenceConnector.ADDRESS_ID,arrayList.get(position).getId());
            deliveryYesNo = "No";
            binding.rdDontDelivery.setChecked(false);
            addressId=arrayList.get(position).getId();
            selectAddressCityId = arrayList.get(position).getCity();
            selectedDeliveryAddress = arrayList.get(position).getAddress();
            Log.e("select city id====",selectAddressCityId);

            //  arrayList.get(position).getCountry();

       //     if(NetworkAvailablity.checkNetworkStatus(CheckOutDeliveryAct.this)) getDeliveryAgency(arrayList.get(position).getId(),shopId,"");
       //     else Toast.makeText(CheckOutDeliveryAct.this, getString(R.string.network_failure), Toast.LENGTH_SHORT).show();


        //    if(NetworkAvailablity.checkNetworkStatus(CheckOutDeliveryAct.this)) getAllTax(arrayList.get(position).getId(),shopId);
        //    else Toast.makeText(CheckOutDeliveryAct.this, getString(R.string.network_failure), Toast.LENGTH_SHORT).show();

            if(NetworkAvailablity.checkNetworkStatus(CheckOutDeliveryAct.this)) getDeliveryAvailability(addressId);
            else Toast.makeText(CheckOutDeliveryAct.this, getString(R.string.network_failure), Toast.LENGTH_SHORT).show();
        }

        else if(Type.equals("Edit"))
            new EditAddressFragment(arrayList.get(position)).callBack(this::onAddress).show(getSupportFragmentManager(),"");

        else if(Type.equals("Deliver to my home")) {
            deliveryType = deliverArrayList.get(position).getTitle();
            //deliveryYesNo = "No";
            for (int i=0;i<deliverArrayList.size();i++){
                deliverArrayList.get(i).setChk(false);
            }
            deliverArrayList.get(position).setChk(true);
            deliveryTypeAdapter.notifyItemChanged(position);
            binding.rdDontDelivery.setChecked(false);
          //  deliveryAgencyList.clear();
           // deliveryAgencyAdapter.notifyDataSetChanged();
            callBottomSheet(deliverArrayList.get(position).getTitle(),deliverArrayList.get(position).getId());

        }

        else if(Type.equals("Deliver to my office")) {
          //  deliveryType = deliverArrayList.get(position).getTitle();

            deliveryType = deliverArrayList.get(position).getTitle();
            //deliveryYesNo = "No";
            for (int i=0;i<deliverArrayList.size();i++){
                deliverArrayList.get(i).setChk(false);
            }
            deliverArrayList.get(position).setChk(true);
            deliveryTypeAdapter.notifyItemChanged(position);
            binding.rdDontDelivery.setChecked(false);
          //  deliveryAgencyList.clear();
          //  deliveryAgencyAdapter.notifyDataSetChanged();
            callBottomSheet(deliverArrayList.get(position).getTitle(),deliverArrayList.get(position).getId());


        }

        else if(Type.equals("Deliver to another person")) {
           // deliveryType = deliverArrayList.get(position).getTitle();
           // callBottomSheet("",deliverArrayList.get(position).getId());
            deliveryType = deliverArrayList.get(position).getTitle();
          //  deliveryYesNo = "No";
            for (int i=0;i<deliverArrayList.size();i++){
                deliverArrayList.get(i).setChk(false);
            }
            deliverArrayList.get(position).setChk(true);
            deliveryTypeAdapter.notifyItemChanged(position);
            binding.rdDontDelivery.setChecked(false);
          //  deliveryAgencyList.clear();
          //  deliveryAgencyAdapter.notifyDataSetChanged();
            callBottomSheet(deliverArrayList.get(position).getTitle(),deliverArrayList.get(position).getId());


        }

        else if(Type.equals("Deliver where i am now")) {
           // deliveryType = deliverArrayList.get(position).getTitle();
           //  callBottomSheet("",deliverArrayList.get(position).getId());
            deliveryType = deliverArrayList.get(position).getTitle();
           // deliveryYesNo = "No";
            for (int i=0;i<deliverArrayList.size();i++){
                deliverArrayList.get(i).setChk(false);
            }
            deliverArrayList.get(position).setChk(true);
            deliveryTypeAdapter.notifyItemChanged(position);
            binding.rdDontDelivery.setChecked(false);
         //   deliveryAgencyList.clear();
         //   deliveryAgencyAdapter.notifyDataSetChanged();

            callBottomSheet(deliverArrayList.get(position).getTitle(),deliverArrayList.get(position).getId());


        }

      /*  else if(Type.equals("I don’t want to be delivered, I will come and collect package myself")) {
            // deliveryType = deliverArrayList.get(position).getTitle();
            dialogDontDelivery();
        }*/


        else if(Type.equals("deliveryAgency")) {
            deliveryCharge = deliveryAgencyList.get(position).getPrice()+"";
            agencyId = deliveryAgencyList.get(position).getId();
            deliveryMethod = deliveryAgencyList.get(position).getDeliveryMethod();


            deliveryAgencyName = deliveryAgencyList.get(position).getName();
            deliveryAgencyImg = deliveryAgencyList.get(position).getImage();
            Log.e("select delivery Agency==",deliveryAgencyName);
             deliveryAgencyAdapter.notifyDataSetChanged();
           // mDialog.dismiss();

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (mDialog != null && mDialog.isShowing()) {
                        mDialog.dismiss();

                    }
                }
            });



        }



    }

    @Override
    public void onPos(int position, String Type, Dialog dialog) {

        if(cityType.equals("TYPE1") || cityType.equals("TYPE4")){
            deliveryCharge = deliveryAgencyList.get(position).getPrice() + "";
            agencyId = deliveryAgencyList.get(position).getId();
            deliveryMethod = deliveryAgencyList.get(position).getDeliveryMethod();


            deliveryAgencyName = deliveryAgencyList.get(position).getName();
            deliveryAgencyImg = deliveryAgencyList.get(position).getImage();

            Log.e("select delivery Agency==", deliveryAgencyName);
            // deliveryAgencyAdapter.notifyDataSetChanged();
            mDialog.dismiss();

            startActivity(new Intent(CheckOutDeliveryAct.this, CheckOutScreen.class)
                    .putExtra("agency", deliveryAgencyType)
                    .putExtra("charge", deliveryCharge)
                    .putExtra("agencyId", agencyId)
                    .putExtra("deliveryYesNo", deliveryYesNo)
                    .putExtra("deliveryMethod", deliveryMethod)
                    .putExtra("deliveryAgencyName", deliveryAgencyName)
                    .putExtra("deliveryAgencyImg", deliveryAgencyImg)
                    .putExtra("addressId", addressId)
                    .putExtra("aa", aa)

                    .putExtra("cityType",cityType)

                    .putExtra("whoWillDeliver", whoWillDeliver)
                    .putExtra("urbanDelivery", urbanDelivery)
                    .putExtra("deliveryPlaceAccuracy", deliveryPlaceAccuracy)

                    .putExtra("deliveryPartnerCharge", deliveryPartnerCharge)
                    .putExtra("partnerId", partnerId)
                    .putExtra("partnerMethod", partnerMethod)
                    .putExtra("deliveryPartnerName", deliveryPartnerName)
                    .putExtra("deliveryPartnerImg", deliveryPartnerImg)

                    .putExtra("urbanDeliveryCostID", "")
                    .putExtra("urbanDeliveryStatus", "")
                    .putExtra("urbanDeliveryCostPrice","")
            );

            deliveryMethod = "";
            addressId = "";
            agencyId = "";
            deliveryType = "";

        }

       else if(cityType.equals("TYPE6")){
            // international delivery
            deliveryCharge = deliveryAgencyList.get(position).getPrice() + "";
            agencyId = deliveryAgencyList.get(position).getId();
            deliveryMethod = deliveryAgencyList.get(position).getDeliveryMethod();


            deliveryAgencyName = deliveryAgencyList.get(position).getName();
            deliveryAgencyImg = deliveryAgencyList.get(position).getImage();

            Log.e("select delivery Agency==", deliveryAgencyName);
            // deliveryAgencyAdapter.notifyDataSetChanged();
            mDialog.dismiss();

            startActivity(new Intent(CheckOutDeliveryAct.this, CheckOutScreen.class)
                    .putExtra("agency", deliveryAgencyType)
                    .putExtra("charge", deliveryCharge)
                    .putExtra("agencyId", agencyId)
                    .putExtra("deliveryYesNo", deliveryYesNo)
                    .putExtra("deliveryMethod", deliveryMethod)
                    .putExtra("deliveryAgencyName", deliveryAgencyName)
                    .putExtra("deliveryAgencyImg", deliveryAgencyImg)
                    .putExtra("addressId", addressId)
                    .putExtra("aa", aa)

                    .putExtra("cityType",cityType)

                    .putExtra("whoWillDeliver", whoWillDeliver)
                    .putExtra("urbanDelivery", urbanDelivery)
                    .putExtra("deliveryPlaceAccuracy", deliveryPlaceAccuracy)

                    .putExtra("deliveryPartnerCharge", deliveryPartnerCharge)
                    .putExtra("partnerId", partnerId)
                    .putExtra("partnerMethod", partnerMethod)
                    .putExtra("deliveryPartnerName", deliveryPartnerName)
                    .putExtra("deliveryPartnerImg", deliveryPartnerImg)

                    .putExtra("urbanDeliveryCostID", "")
                    .putExtra("urbanDeliveryStatus", "")
                    .putExtra("urbanDeliveryCostPrice","")
            );

            deliveryMethod = "";
            addressId = "";
            agencyId = "";
            deliveryType = "";

        }


        else {
            if (Type.equals("deliveryAgency")) {
                deliveryCharge = deliveryAgencyList.get(position).getPrice() + "";
                agencyId = deliveryAgencyList.get(position).getId();
                deliveryMethod = deliveryAgencyList.get(position).getDeliveryMethod();


                deliveryAgencyName = deliveryAgencyList.get(position).getName();
                deliveryAgencyImg = deliveryAgencyList.get(position).getImage();
                Log.e("select delivery Agency==", deliveryAgencyName);
                // deliveryAgencyAdapter.notifyDataSetChanged();
                mDialog.dismiss();

                if (whoWillDeliver.equals("Partners")) {
                    getDeliveryPartnerApi(addressId);
                } else {
                    startActivity(new Intent(CheckOutDeliveryAct.this, CheckOutScreen.class)
                            .putExtra("agency", deliveryAgencyType)
                            .putExtra("charge", deliveryCharge)
                            .putExtra("agencyId", agencyId)
                            .putExtra("deliveryYesNo", deliveryYesNo)
                            .putExtra("deliveryMethod", deliveryMethod)
                            .putExtra("deliveryAgencyName", deliveryAgencyName)
                            .putExtra("deliveryAgencyImg", deliveryAgencyImg)
                            .putExtra("addressId", addressId)
                            .putExtra("aa", aa)

                            .putExtra("cityType",cityType)

                            .putExtra("whoWillDeliver", whoWillDeliver)
                            .putExtra("urbanDelivery", urbanDelivery)
                            .putExtra("deliveryPlaceAccuracy", deliveryPlaceAccuracy)

                            .putExtra("deliveryPartnerCharge", deliveryPartnerCharge)
                            .putExtra("partnerId", partnerId)
                            .putExtra("partnerMethod", partnerMethod)
                            .putExtra("deliveryPartnerName", deliveryPartnerName)
                            .putExtra("deliveryPartnerImg", deliveryPartnerImg)

                            .putExtra("urbanDeliveryCostID", "")
                            .putExtra("urbanDeliveryStatus", "")
                            .putExtra("urbanDeliveryCostPrice","")
                    );
                }


                deliveryMethod = "";
              //  addressId = "";
             //   agencyId = "";
                deliveryType = "";
            } else {
                deliveryPartnerCharge = deliveryPartnerList.get(position).getPrice() + "";
                partnerId = deliveryPartnerList.get(position).getId();
                partnerMethod = "intercity partner";


                deliveryPartnerName = deliveryPartnerList.get(position).getIntercityPartner();
                deliveryPartnerImg = deliveryPartnerList.get(position).getIntercityPartnerImage();
                Log.e("select deliveryPartner==", deliveryPartnerName);
                // deliveryAgencyAdapter.notifyDataSetChanged();
                deliveryPartnerDialog.dismiss();

                if (urbanDelivery.equals("Active")) {
                    // getDeliveryPartnerApi(addressId);
                   // dialogDeliveryAccuracy("");
                   deliveryAccuracyPrice(partnerId,addressId);

                } else {
                    startActivity(new Intent(CheckOutDeliveryAct.this, CheckOutScreen.class)
                            .putExtra("agency", deliveryAgencyType)
                            .putExtra("charge", deliveryCharge)
                            .putExtra("agencyId", agencyId)
                            .putExtra("deliveryYesNo", deliveryYesNo)
                            .putExtra("deliveryMethod", deliveryMethod)
                            .putExtra("deliveryAgencyName", deliveryAgencyName)
                            .putExtra("deliveryAgencyImg", deliveryAgencyImg)
                            .putExtra("addressId", addressId)
                            .putExtra("aa", aa)

                            .putExtra("cityType",cityType)

                            .putExtra("whoWillDeliver", whoWillDeliver)
                            .putExtra("urbanDelivery", urbanDelivery)
                            .putExtra("deliveryPlaceAccuracy", deliveryPlaceAccuracy)

                            .putExtra("deliveryPartnerCharge", deliveryPartnerCharge)
                            .putExtra("partnerId", partnerId)
                            .putExtra("partnerMethod", partnerMethod)
                            .putExtra("deliveryPartnerName", deliveryPartnerName)
                            .putExtra("deliveryPartnerImg", deliveryPartnerImg)

                            .putExtra("urbanDeliveryCostID", "")
                            .putExtra("urbanDeliveryStatus", "")
                            .putExtra("urbanDeliveryCostPrice","")


                    );
                }


            }

        }

    }

    private void deliveryAccuracyPrice(String partnerId, String addressId) {
        DataManager.getInstance().showProgressMessage(CheckOutDeliveryAct.this, getString(R.string.please_wait));
        Map<String, String> map = new HashMap<>();
            map.put("intercity_partner_setting_id", partnerId);
            map.put("address_id", addressId);
            Log.e(TAG, "Delivery place accuracy Request :" + map);

            Call<ResponseBody> chatCount = apiInterface.getDeliveryAccuracyApi(map);
            chatCount.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    DataManager.getInstance().hideProgressMessage();
                    try {
                        String responseData = response.body() != null ? response.body().string() : "";
                        JSONObject object = new JSONObject(responseData);
                        Log.e(TAG, "Delivery place accuracy RESPONSE" + object);
                        if (object.optString("status").equals("1")) {
                            dialogDeliveryAccuracy(object.optString("delivery_cost"),object.optString("intercity_partner_setting_id"));

                        } else if (object.optString("status").equals("0")) {

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


    public void  getAllTax(String addressId,String shopId){
        DataManager.getInstance().showProgressMessage(CheckOutDeliveryAct.this, getString(R.string.please_wait));
        Map<String,String> headerMap = new HashMap<>();
        headerMap.put("Authorization","Bearer " +PreferenceConnector.readString(CheckOutDeliveryAct.this, PreferenceConnector.access_token,""));
        headerMap.put("Accept","application/json");

        Map<String, String> map = new HashMap<>();
        map.put("user_id", PreferenceConnector.readString(CheckOutDeliveryAct.this, PreferenceConnector.User_id, ""));
        map.put("drop_lat", PreferenceConnector.readString(CheckOutDeliveryAct.this, PreferenceConnector.LAT, ""));
        map.put("drop_lon", PreferenceConnector.readString(CheckOutDeliveryAct.this, PreferenceConnector.LON, ""));
        map.put("country_id",PreferenceConnector.readString(CheckOutDeliveryAct.this, PreferenceConnector.COUNTRY_ID,""));
        map.put("delivery_partner","");
        map.put("self_collect",deliveryYesNo);
        map.put("type",deliveryMethod);
        if(addressId!=null) map.put("address_id",addressId);
        else  map.put("address_id","");
        map.put("register_id", PreferenceConnector.readString(CheckOutDeliveryAct.this, PreferenceConnector.Register_id, ""));
        map.put("type_delivery",cityType);
        map.put("urban_delivery",urbanDelivery);
        map.put("who_will_deliver",whoWillDeliver);
        map.put("intercity_partner_id",partnerId);
        map.put("intercity_partner_price","");
        map.put("delivery_place_accuracy_price","");
        map.put("delivery_place_accuracy_status","");
        map.put("delivery_place_accuracy_vehicle_id","");

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
                        // aa = object.getJSONArray("result").getJSONObject(0).getString("delivery_calculation");

                        //String  deliveryFees = numberFormat.parse(object.getString("total_delivery_fees")).doubleValue();


                        JSONArray array = object.getJSONArray("get_delivery_partner");
                        deliveryAgencyList.clear();

                        Type listType = new TypeToken<List<DeliveryAgencyModel.Result>>() {}.getType();
                        List<DeliveryAgencyModel.Result> parsedList = new Gson().fromJson(array.toString(), listType);


                        // DeliveryAgencyModel data = new Gson().fromJson(responseData, DeliveryAgencyModel.class);

                        deliveryAgencyList.addAll(parsedList);
                     //   deliveryAgencyAdapter.notifyDataSetChanged();


                        deliveryAgencyDialog(CheckOutDeliveryAct.this,getString(R.string.select_vehicle),addressId);


                        //   if(NetworkAvailablity.checkNetworkStatus(CheckOutDeliveryAct.this)) getDeliveryAgency(addressId,shopId, deliveryFees);
                     //   else Toast.makeText(CheckOutDeliveryAct.this, getString(R.string.network_failure), Toast.LENGTH_SHORT).show();


                    } else if (object.optString("status").equals("0")) {
                        Toast.makeText(CheckOutDeliveryAct.this, object.getString("message"), Toast.LENGTH_SHORT).show();
                        deliveryAgencyList.clear();
                        deliveryAgencyAdapter.notifyDataSetChanged();

                    }
                    else if (object.getString("status").equals("5")) {
                        PreferenceConnector.writeString(CheckOutDeliveryAct.this, PreferenceConnector.LoginStatus, "false");
                        startActivity(new Intent(CheckOutDeliveryAct.this, Splash.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP));
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







    private void dialogDontDelivery() {
        LayoutInflater li;
        TextView txtSKip;
        TextView txtok;
        AlertDialog.Builder alertDialogBuilder;
        li = LayoutInflater.from(CheckOutDeliveryAct.this);

        promptsView1 = li.inflate(R.layout.dialog_dont_delivery, null);
        txtSKip = (TextView) promptsView1.findViewById(R.id.txtSKip);
        txtok = (TextView) promptsView1.findViewById(R.id.txtok);

        alertDialogBuilder = new AlertDialog.Builder(CheckOutDeliveryAct.this);
        alertDialogBuilder.setView(promptsView1);
        alertDialogBuilder.setCancelable(false);



        txtSKip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                binding.rdDontDelivery.setChecked(false);
                deliveryYesNo = "No";
                alertDialog1.dismiss();
            }
        });

        txtok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog1.dismiss();
                PreferenceConnector.writeString(CheckOutDeliveryAct.this, PreferenceConnector.LAT, "");
                PreferenceConnector.writeString(CheckOutDeliveryAct.this, PreferenceConnector.LON, "");
                startActivity(new Intent(CheckOutDeliveryAct.this, CheckOutScreen.class)
                        .putExtra("agency",deliveryAgencyType)
                        .putExtra("charge",deliveryCharge)
                        .putExtra("agencyId",agencyId)
                        .putExtra("deliveryYesNo",deliveryYesNo)
                        .putExtra("deliveryMethod",deliveryMethod)
                        .putExtra("deliveryAgencyName",deliveryAgencyName)
                        .putExtra("deliveryAgencyImg",deliveryAgencyImg)
                        .putExtra("aa",aa)

                        .putExtra("cityType",cityType)

                        .putExtra("whoWillDeliver", whoWillDeliver)
                        .putExtra("urbanDelivery", urbanDelivery)
                        .putExtra("deliveryPlaceAccuracy", deliveryPlaceAccuracy)

                        .putExtra("deliveryPartnerCharge", deliveryPartnerCharge)
                        .putExtra("partnerId", partnerId)
                        .putExtra("partnerMethod", partnerMethod)
                        .putExtra("deliveryPartnerName", deliveryPartnerName)
                        .putExtra("deliveryPartnerImg", deliveryPartnerImg)

                        .putExtra("urbanDeliveryCostID", "")
                        .putExtra("urbanDeliveryCostPrice", "")
                        .putExtra("urbanDeliveryStatus", "")

                );

            }

        });

        alertDialog1 = alertDialogBuilder.create();
        alertDialog1.show();
        alertDialog1.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
    }


    public void DeleteAlert(String id1){
        AlertDialog.Builder  builder1 = new AlertDialog.Builder(CheckOutDeliveryAct.this);
        builder1.setMessage(getResources().getString(R.string.are_you_sure_you_want_to_delete_this_address));
        builder1.setCancelable(false);

        builder1.setPositiveButton(
                "Yes",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                        if(NetworkAvailablity.checkNetworkStatus(CheckOutDeliveryAct.this)) DeleteLocation(id1);
                        else Toast.makeText(CheckOutDeliveryAct.this, getString(R.string.network_failure), Toast.LENGTH_SHORT).show();
                    }
                });

        builder1.setNegativeButton(
                "No",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });

        AlertDialog alert11 = builder1.create();
        alert11.show();
    }



    public void DeleteLocation(String id){
        DataManager.getInstance().showProgressMessage(CheckOutDeliveryAct.this, getString(R.string.please_wait));

        Map<String,String> headerMap = new HashMap<>();
        headerMap.put("Authorization","Bearer " +PreferenceConnector.readString(CheckOutDeliveryAct.this, PreferenceConnector.access_token,""));
        headerMap.put("Accept","application/json");

        Map<String, String> map = new HashMap<>();
        map.put("user_id", PreferenceConnector.readString(CheckOutDeliveryAct.this, PreferenceConnector.User_id, ""));
        map.put("register_id", PreferenceConnector.readString(CheckOutDeliveryAct.this, PreferenceConnector.Register_id, ""));

        map.put("id", id);
        Log.e(TAG, "Delete Location Request :" + map);
        Call<ResponseBody> loginCall = apiInterface.deleteAddres(headerMap,map);
        loginCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                DataManager.getInstance().hideProgressMessage();

                try {
                    String responseData = response.body() != null ? response.body().string() : "";
                    JSONObject object = new JSONObject(responseData);
                    Log.e(TAG, "Delete Location RESPONSE" + object);

                    if (object.optString("status").equals("1")) {
                        if(NetworkAvailablity.checkNetworkStatus(CheckOutDeliveryAct.this)) getLocation();
                        else Toast.makeText(CheckOutDeliveryAct.this, getString(R.string.network_failure), Toast.LENGTH_SHORT).show();
                    } else if (object.optString("status").equals("0")) {
                        Toast.makeText(CheckOutDeliveryAct.this, object.getString("message"), Toast.LENGTH_SHORT).show();


                    }

                    else if (object.getString("status").equals("5")) {
                        PreferenceConnector.writeString(CheckOutDeliveryAct.this, PreferenceConnector.LoginStatus, "false");
                        startActivity(new Intent(CheckOutDeliveryAct.this, Splash.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP));
                        finish();
                    }


                }
                catch (Exception e) {
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


    private void getDeliveryAgency(String addressId,String productId,String deliveryFees) {
        DataManager.getInstance().showProgressMessage(CheckOutDeliveryAct.this, getString(R.string.please_wait));
        Map<String,String> headerMap = new HashMap<>();
        headerMap.put("Authorization","Bearer " +PreferenceConnector.readString(CheckOutDeliveryAct.this, PreferenceConnector.access_token,""));
        headerMap.put("Accept","application/json");

        Map<String, String> map = new HashMap<>();
        map.put("user_id", PreferenceConnector.readString(CheckOutDeliveryAct.this, PreferenceConnector.User_id, ""));
        map.put("address_id", addressId);
        map.put("shop_id", productId);
        map.put("register_id", PreferenceConnector.readString(CheckOutDeliveryAct.this, PreferenceConnector.Register_id, ""));
        map.put("country_id", PreferenceConnector.readString(CheckOutDeliveryAct.this, PreferenceConnector.countryId, ""));

        Log.e(TAG, "Delivery Agency Request :" + map);

        Call<ResponseBody> chatCount = apiInterface.getDeliveryAgencyApi(headerMap,map);
        chatCount.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                DataManager.getInstance().hideProgressMessage();
                try {
                    String responseData = response.body() != null ? response.body().string() : "";
                    JSONObject object = new JSONObject(responseData);



                    Log.e(TAG, "Delivery Agency RESPONSE" + object);
                    if (object.optString("status").equals("1")) {
                        DeliveryAgencyModel data = new Gson().fromJson(responseData, DeliveryAgencyModel.class);
                        deliveryAgencyList.clear();
                        deliveryAgencyList.addAll(data.getResult());
                       // deliveryAgencyAdapter.notifyDataSetChanged();
                        deliveryAgencyType ="International";
                      //  deliveryAgencyDialog(CheckOutDeliveryAct.this);

                    } else if (object.optString("status").equals("0")) {
                        deliveryAgencyList.clear();
                      //  deliveryAgencyAdapter.notifyDataSetChanged();
                        deliveryAgencyType ="Afary Code";
                        agencyId ="";
                    }



                    else if (object.getString("status").equals("5")) {
                        PreferenceConnector.writeString(CheckOutDeliveryAct.this, PreferenceConnector.LoginStatus, "false");
                        startActivity(new Intent(CheckOutDeliveryAct.this, Splash.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP));
                       finish();
                    }


                    if(NetworkAvailablity.checkNetworkStatus(CheckOutDeliveryAct.this))  getDeliveryAvailability(addressId);
                    else Toast.makeText(CheckOutDeliveryAct.this, getString(R.string.network_failure), Toast.LENGTH_SHORT).show();



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





    private void getDeliveryAvailability(String addressId) {
        DataManager.getInstance().showProgressMessage(CheckOutDeliveryAct.this, getString(R.string.please_wait));
        Map<String,String> headerMap = new HashMap<>();
        headerMap.put("Authorization","Bearer " +PreferenceConnector.readString(CheckOutDeliveryAct.this, PreferenceConnector.access_token,""));
        headerMap.put("Accept","application/json");

        Map<String, String> map = new HashMap<>();
        map.put("address_id", addressId);
        map.put("user_id", PreferenceConnector.readString(CheckOutDeliveryAct.this, PreferenceConnector.User_id, ""));

        Log.e(TAG, "Delivery Availability Request :" + map);

        Call<ResponseBody> chatCount = apiInterface.getDeliveryAvailabilityApi(headerMap,map);
        chatCount.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                DataManager.getInstance().hideProgressMessage();
                try {
                    String responseData = response.body() != null ? response.body().string() : "";
                    JSONObject object = new JSONObject(responseData);
                    Log.e(TAG, "Delivery Availability RESPONSE" + object);
                    aa= object.optString("order_type");
                    whoWillDeliver =  object.optString("who_will_deliver");
                    urbanDelivery = object.optString("is_urban_delivery_active");


                    /*if(object.optString("order_type").equals("NATIONAL")) {

                        // Delivery and Availibitly both are not available
                        if (object.optString("status").equals("1")) {
                            binding.rvDeliveryAgency.setVisibility(View.GONE);
                            uncheckAddressList();
                            ShowAvailableResultDialog(getString(R.string.alert), getString(R.string.sorry_this_country_not_served_yet), object.getString("status"));

                        }

                        // 'Delivery and Availibitly Both are Available
                        else if (object.getString("status").equals("2")) {
                          //  binding.rvDeliveryAgency.setVisibility(View.VISIBLE);
                            deliveryAgencyDialog(CheckOutDeliveryAct.this,getString(R.string.select_vehicle));

                        }


                        // Delivery are not available but Availibitly are available
                        else if (object.getString("status").equals("3")) {
                            binding.rvDeliveryAgency.setVisibility(View.GONE);
                            uncheckAddressList();
                            ShowAvailableResultDialog(getString(R.string.alert), getString(R.string.we_available_on_this_country_but_you_will_be_collect_your_packege_your_self_please_select), object.getString("status"));

                        }

                        // Delivery are available but Availibitly are not available
                        else if (object.getString("status").equals("4")) {
                         //   binding.rvDeliveryAgency.setVisibility(View.VISIBLE);
                            deliveryAgencyDialog(CheckOutDeliveryAct.this,getString(R.string.select_vehicle));

                            // uncheckAddressList();
                            // ShowAvailableResultDialog(getString(R.string.alert), getString(R.string.this_country_cannot_be_delivered), object.getString("status"));
                        }


                        // Delivery and Availibitly Both are Available But City not available
                        else if (object.getString("status").equals("5")) {
                            binding.rvDeliveryAgency.setVisibility(View.GONE);
                            uncheckAddressList();
                            ShowAvailableResultDialog(getString(R.string.alert), getString(R.string.we_do_not_available_on_this_city), object.getString("status"));
                        }

                    }

                    else {
                        // INTERNATIONAL

                        // Delivery and Availibitly both are not available
                        if (object.optString("status").equals("1")) {
                            binding.rvDeliveryAgency.setVisibility(View.GONE);
                            uncheckAddressList();
                            ShowAvailableResultDialog(getString(R.string.alert), getString(R.string.we_do_not_available_on_this_country), object.getString("status"));

                        }

                        // 'Delivery and Availibitly Both are Available
                        else if (object.getString("status").equals("2")) {
                          //  binding.rvDeliveryAgency.setVisibility(View.VISIBLE);
                            deliveryAgencyDialog(CheckOutDeliveryAct.this,getString(R.string.select_shipping_agency));
                        }


                        // Delivery are not available but Availibitly are available
                        else if (object.getString("status").equals("3")) {
                            binding.rvDeliveryAgency.setVisibility(View.GONE);
                            uncheckAddressList();
                            ShowAvailableResultDialog22(getString(R.string.alert), getString(R.string.we_available_on_this_country_but_you_will_be_collect_your_packege_from_shipping_agency), object.getString("status"));

                        }

                        // Delivery are available but Availibitly are not available
                        else if (object.getString("status").equals("4")) {
                            binding.rvDeliveryAgency.setVisibility(View.GONE);
                            uncheckAddressList();
                            ShowAvailableResultDialog(getString(R.string.alert), getString(R.string.this_country_cannot_be_delivered), object.getString("status"));
                        }


                        // Delivery and Availibitly Both are Available But City not available
                        else if (object.getString("status").equals("5")) {
                            binding.rvDeliveryAgency.setVisibility(View.GONE);
                            uncheckAddressList();
                            ShowAvailableResultDialog22(getString(R.string.alert), getString(R.string.unben_delivery_not), object.getString("status"));
                        }
                    }
*/


                    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
                    /* for check city functionality */
                    if (object.optString("status").equals("9")) {
                        cityType = "TYPE1";
                        binding.rvDeliveryAgency.setVisibility(View.GONE);
                       // uncheckAddressList();
                     //   ShowAvailableResultDialog(getString(R.string.alert), getString(R.string.sorry_this_country_not_served_yet), object.getString("status"));
                    //    deliveryAgencyDialog(CheckOutDeliveryAct.this,getString(R.string.select_vehicle),addressId);

                        if(NetworkAvailablity.checkNetworkStatus(CheckOutDeliveryAct.this)) getAllTax(addressId,shopId);
                        else Toast.makeText(CheckOutDeliveryAct.this, getString(R.string.network_failure), Toast.LENGTH_SHORT).show();

                    }

                   else if (object.optString("status").equals("7") || object.optString("status").equals("10")) {
                        cityType = "TYPE2";
                      //  binding.rvDeliveryAgency.setVisibility(View.GONE);
                     //   uncheckAddressList();
                     //   ShowAvailableResultDialog(getString(R.string.alert), getString(R.string.sorry_this_country_not_served_yet), object.getString("status"));
                     //   deliveryAgencyDialog(CheckOutDeliveryAct.this,getString(R.string.select_vehicle),addressId);
                        if(NetworkAvailablity.checkNetworkStatus(CheckOutDeliveryAct.this)) getAllTax(addressId,shopId);
                        else Toast.makeText(CheckOutDeliveryAct.this, getString(R.string.network_failure), Toast.LENGTH_SHORT).show();
                    }


                    else if (object.optString("status").equals("4") || object.optString("status").equals("5")
                    || object.optString("status").equals("6") ) {
                        cityType = "TYPE3";
                        binding.rvDeliveryAgency.setVisibility(View.GONE);
                        uncheckAddressList();
                        ShowAvailableResultDialog(getString(R.string.alert), getString(R.string.dear_customer)+"\n"+getString(R.string.sorry_we_do_not_serve_this_city_at_the_moment_please_choose_another_city_or_change_your_delivery_address), object.getString("status"));

                    }

                    else if (object.optString("status").equals("8") || object.optString("status").equals("11")) {
                        cityType = "TYPE4";
                        binding.rvDeliveryAgency.setVisibility(View.GONE);
                        uncheckAddressList();
                      //  ShowAvailableResultDialog(getString(R.string.alert), getString(R.string.sorry_this_country_not_served_yet), object.getString("status"));
                     //   deliveryAgencyDialog(CheckOutDeliveryAct.this,getString(R.string.select_vehicle),addressId);

                        if(NetworkAvailablity.checkNetworkStatus(CheckOutDeliveryAct.this)) getAllTax(addressId,shopId);
                        else Toast.makeText(CheckOutDeliveryAct.this, getString(R.string.network_failure), Toast.LENGTH_SHORT).show();
                    }


                    else if (object.optString("status").equals("1")) {
                        cityType = "TYPE5";
                        binding.rvDeliveryAgency.setVisibility(View.GONE);
                        uncheckAddressList();
                      //  ShowAvailableResultDialog(getString(R.string.alert), getString(R.string.sorry_this_country_not_served_yet), object.getString("status"));
                        ShowAvailableResultDialog(getString(R.string.alert), getString(R.string.we_do_not_available_on_this_country), object.getString("status"));

                    }


                    else if (object.optString("status").equals("2")) {
                        cityType = "TYPE6";
                        binding.rvDeliveryAgency.setVisibility(View.GONE);
                        uncheckAddressList();
                    //    ShowAvailableResultDialog(getString(R.string.alert), getString(R.string.sorry_this_country_not_served_yet), object.getString("status"));

                        if(NetworkAvailablity.checkNetworkStatus(CheckOutDeliveryAct.this)) getAllTax(addressId,shopId);
                        else Toast.makeText(CheckOutDeliveryAct.this, getString(R.string.network_failure), Toast.LENGTH_SHORT).show();


                    }


                    else if (object.optString("status").equals("3") ) {
                        cityType = "TYPE7";
                        binding.rvDeliveryAgency.setVisibility(View.GONE);
                        uncheckAddressList();
                     //   ShowAvailableResultDialog(getString(R.string.alert), getString(R.string.sorry_this_country_not_served_yet), object.getString("status"));

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

    private void uncheckAddressList() {
        deliveryType = "";
        for (int i=0;i<deliverArrayList.size();i++){
            deliverArrayList.get(i).setChk(false);
        }
        deliveryTypeAdapter.notifyDataSetChanged();


        for (int i=0;i<arrayList.size();i++){
            arrayList.get(i).setChk(false);
        }
        adapter.notifyDataSetChanged();
    }



    private void ShowAvailableResultDialog(String title,String msg,String status){
        AlertDialog.Builder builder = new AlertDialog.Builder(CheckOutDeliveryAct.this);
        builder.setTitle(title)
                .setMessage(msg)
                .setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }) .show();
              /*  .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })*/
        // Show the dialog
    }


    private void ShowAvailableResultDialog22(String title,String msg,String status){
        AlertDialog.Builder builder = new AlertDialog.Builder(CheckOutDeliveryAct.this);
        builder.setTitle(title)
                .setMessage(msg)
                .setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setNegativeButton(getString(R.string.skip), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }) .show();
        // Show the dialog
    }



    private void deliveryAgencyDialog(Context context,String title,String addressId) {
        mDialog = new Dialog(context);
        mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mDialog.setContentView(R.layout.dialog_delivery_agency);
        mDialog.setCancelable(true);
        mDialog.setCanceledOnTouchOutside(true);

        this.addressId = addressId;

        RecyclerView rvDeliveryAgency = mDialog.findViewById(R.id.rvDeliveryAgency);
        TextView tvTitle = mDialog.findViewById(R.id.tvTitle);
        TextView tvSubTitle = mDialog.findViewById(R.id.tvSubTitle);

        tvTitle.setText(title);
        tvSubTitle.setText(getString(R.string.depending_on_the_volume_and_weight_of_your_order_please_choose_appropriate_method_for_your_delivery));


        DeliveryAgencyAdapter   deliveryAgencyAdapter = new DeliveryAgencyAdapter(CheckOutDeliveryAct.this,deliveryAgencyList, CheckOutDeliveryAct.this,mDialog);
        rvDeliveryAgency.setAdapter(deliveryAgencyAdapter);

        mDialog.show();
    }



    private void getDeliveryPartnerApi(String addressId) {
        DataManager.getInstance().showProgressMessage(CheckOutDeliveryAct.this, getString(R.string.please_wait));
        Map<String,String> headerMap = new HashMap<>();
        headerMap.put("Authorization","Bearer " +PreferenceConnector.readString(CheckOutDeliveryAct.this, PreferenceConnector.access_token,""));
        headerMap.put("Accept","application/json");

        Map<String, String> map = new HashMap<>();
      //  map.put("address_id", addressId);
     //  map.put("user_id", PreferenceConnector.readString(CheckOutDeliveryAct.this, PreferenceConnector.User_id, ""));

        map.put("shop_id", shopId);
        map.put("city_end", selectAddressCityId);

      //  map.put("register_id", PreferenceConnector.readString(CheckOutDeliveryAct.this, PreferenceConnector.Register_id, ""));
     //   map.put("country_id", PreferenceConnector.readString(CheckOutDeliveryAct.this, PreferenceConnector.countryId, ""));

        Log.e(TAG, "Delivery partner Request :" + map);

        Call<ResponseBody> chatCount = apiInterface.getDeliveryPartnerApi(headerMap,map);
        chatCount.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                DataManager.getInstance().hideProgressMessage();
                try {
                    String responseData = response.body() != null ? response.body().string() : "";
                    JSONObject object = new JSONObject(responseData);
                    Log.e(TAG, "Delivery partner RESPONSE" + object);

                    Log.e(TAG, "Delivery Agency RESPONSE" + object);
                    if (object.optString("status").equals("1")) {
                        DeliveryPartnerModel data = new Gson().fromJson(responseData, DeliveryPartnerModel.class);
                        deliveryPartnerList.clear();
                        deliveryPartnerList.addAll(data.getResult());
                        dialogDeliveryPartner();

                    } else if (object.optString("status").equals("0")) {
                        deliveryPartnerList.clear();
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

    private void dialogDeliveryPartner() {
        deliveryPartnerDialog = new Dialog(CheckOutDeliveryAct.this);
        deliveryPartnerDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        deliveryPartnerDialog.setContentView(R.layout.dialog_delivery_partner);

        deliveryPartnerDialog.setCancelable(true);
        deliveryPartnerDialog.setCanceledOnTouchOutside(true);

        RecyclerView rvDeliveryAgency = deliveryPartnerDialog.findViewById(R.id.rvDeliveryAgency);
        TextView tvTitle = deliveryPartnerDialog.findViewById(R.id.tvTitle);
        TextView tvSubTitle = deliveryPartnerDialog.findViewById(R.id.tvSubTitle);
        TextView tvNote = deliveryPartnerDialog.findViewById(R.id.tvNote);


        tvTitle.setText(getText(R.string.select_delivery_partner));
        tvSubTitle.setText(getText(R.string.delivery_to_this_city_is_provided_by_our_partner_please_choose_the_partner_who_will_carry_out_delivery));
        tvNote.setText(getText(R.string.please_note_that_this_price_is_standard_rate_it_may_be_subject_to_change));



        DeliveryPartnerAdapter deliveryPartnerAdapter = new DeliveryPartnerAdapter(CheckOutDeliveryAct.this,deliveryPartnerList, CheckOutDeliveryAct.this,deliveryPartnerDialog);
        rvDeliveryAgency.setAdapter(deliveryPartnerAdapter);

        deliveryPartnerDialog.show();
    }

    private void dialogDeliveryAccuracy(String price,String id) {
        deliveryAccuracyDialog = new Dialog(CheckOutDeliveryAct.this);
        deliveryAccuracyDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        deliveryAccuracyDialog.setContentView(R.layout.dialog_delivery_accuracy);
        deliveryAccuracyDialog.setCancelable(true);
        deliveryAccuracyDialog.setCanceledOnTouchOutside(true);

        RelativeLayout rlPickUpOffice = deliveryAccuracyDialog.findViewById(R.id.rlPickUpOffice);
        RelativeLayout rlDeliverAddress = deliveryAccuracyDialog.findViewById(R.id.rlDeliverAddress);

        RadioButton rdPickOffice = deliveryAccuracyDialog.findViewById(R.id.rdPickOffice);
        RadioButton rdDeliverAddress = deliveryAccuracyDialog.findViewById(R.id.rdDeliverAddress);

        rdPickOffice.setText(getString(R.string.i_am_picking_up_the_package_at_the_office) + " (" + deliveryPartnerName + ")");
        rdDeliverAddress.setText(getString(R.string.i_would_rather_be_delivered_to_the_address_i_indicated) + " (" + selectedDeliveryAddress + ")");

        TextView tvPrice11 = deliveryAccuracyDialog.findViewById(R.id.tvPrice11);
        TextView tvPrice22 = deliveryAccuracyDialog.findViewById(R.id.tvPrice22);

        tvPrice22.setText("FCFA" + price);

        rdPickOffice.setOnClickListener(v -> {
            rdPickOffice.setChecked(true);
            rdDeliverAddress.setChecked(false);
            deliveryAccuracyDialog.dismiss();

            startActivity(new Intent(CheckOutDeliveryAct.this, CheckOutScreen.class)
                    .putExtra("agency",deliveryAgencyType)
                    .putExtra("charge",deliveryCharge)
                    .putExtra("agencyId",agencyId)
                    .putExtra("deliveryYesNo",deliveryYesNo)
                    .putExtra("deliveryMethod",deliveryMethod)
                    .putExtra("deliveryAgencyName",deliveryAgencyName)
                    .putExtra("deliveryAgencyImg",deliveryAgencyImg)
                    .putExtra("aa",aa)

                    .putExtra("cityType",cityType)

                    .putExtra("whoWillDeliver", whoWillDeliver)
                    .putExtra("urbanDelivery", urbanDelivery)
                    .putExtra("deliveryPlaceAccuracy", deliveryPlaceAccuracy)

                    .putExtra("deliveryPartnerCharge", deliveryPartnerCharge)
                    .putExtra("partnerId", partnerId)
                    .putExtra("partnerMethod", partnerMethod)
                    .putExtra("deliveryPartnerName", deliveryPartnerName)
                    .putExtra("deliveryPartnerImg", deliveryPartnerImg)

                   // .putExtra("urbanDeliveryCost", "00")
                  //  .putExtra("urbanDeliverySelectAddress", "")

                    .putExtra("urbanDeliveryCostID","0")
                    .putExtra("urbanDeliveryCostPrice", "00")
                    .putExtra("urbanDeliveryStatus", "No")

            );

        });

        rdDeliverAddress.setOnClickListener(v -> {
            rdPickOffice.setChecked(false);
            rdDeliverAddress.setChecked(true);
            deliveryAccuracyDialog.dismiss();

            startActivity(new Intent(CheckOutDeliveryAct.this, CheckOutScreen.class)
                    .putExtra("agency",deliveryAgencyType)
                    .putExtra("charge",deliveryCharge)
                    .putExtra("agencyId",agencyId)
                    .putExtra("deliveryYesNo",deliveryYesNo)
                    .putExtra("deliveryMethod",deliveryMethod)
                    .putExtra("deliveryAgencyName",deliveryAgencyName)
                    .putExtra("deliveryAgencyImg",deliveryAgencyImg)
                    .putExtra("aa",aa)

                    .putExtra("cityType",cityType)

                    .putExtra("whoWillDeliver", whoWillDeliver)
                    .putExtra("urbanDelivery", urbanDelivery)
                    .putExtra("deliveryPlaceAccuracy", deliveryPlaceAccuracy)

                    .putExtra("deliveryPartnerCharge", deliveryPartnerCharge)
                    .putExtra("partnerId", partnerId)
                    .putExtra("partnerMethod", partnerMethod)
                    .putExtra("deliveryPartnerName", deliveryPartnerName)
                    .putExtra("deliveryPartnerImg", deliveryPartnerImg)

                    .putExtra("urbanDeliveryCostID",id)
                    .putExtra("urbanDeliveryCostPrice", price)
                    .putExtra("urbanDeliveryStatus", "Yes")

            );

        });



        deliveryAccuracyDialog.show();
    }



}
