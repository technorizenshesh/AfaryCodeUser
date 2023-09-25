package com.my.afarycode.OnlineShopping;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.google.gson.Gson;
import com.my.afarycode.OnlineShopping.Model.Add_Address_Modal;
import com.my.afarycode.OnlineShopping.Model.CountryModel;
import com.my.afarycode.OnlineShopping.Model.DeliveryAgencyModel;
import com.my.afarycode.OnlineShopping.Model.DeliveryTypeModel;
import com.my.afarycode.OnlineShopping.Model.LocationModel;
import com.my.afarycode.OnlineShopping.Model.SignupModel;
import com.my.afarycode.OnlineShopping.activity.CheckOutDeliveryAct;
import com.my.afarycode.OnlineShopping.adapter.DeliveryAgencyAdapter;
import com.my.afarycode.OnlineShopping.adapter.DeliveryTypeAdapter;
import com.my.afarycode.OnlineShopping.adapter.LocationAdapter;
import com.my.afarycode.OnlineShopping.bottomsheet.EditAddressFragment;
import com.my.afarycode.OnlineShopping.constant.PreferenceConnector;
import com.my.afarycode.OnlineShopping.fragment.AddAddressFragment;
import com.my.afarycode.OnlineShopping.helper.DataManager;
import com.my.afarycode.OnlineShopping.helper.GooglePlacesAutocompleteActivity;
import com.my.afarycode.OnlineShopping.helper.NetworkAvailablity;
import com.my.afarycode.OnlineShopping.listener.addAddressListener;
import com.my.afarycode.OnlineShopping.listener.onPosListener;
import com.my.afarycode.R;
import com.my.afarycode.databinding.ActivityCheckOutDeliveryBinding;
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

public class CheckOutDelivery extends Fragment implements addAddressListener , onPosListener {
    public String TAG = "CheckOutDelivery";
    ActivityCheckOutDeliveryBinding binding;

    private View promptsView;
    private AlertDialog alertDialog;

    private View promptsView1;
    private AlertDialog alertDialog1;
    public static EditText address_select;
    private AfaryCode apiInterface;
    private EditText city;
    private EditText location_details;
    private EditText phone_number;
    private String phone_no = "", type = "",shopId="";
    ArrayList<LocationModel.Result> arrayList;
    LocationAdapter adapter;

    ArrayList<DeliveryTypeModel.Result> deliverArrayList;
    DeliveryTypeAdapter deliveryTypeAdapter;

    String deliveryAgencyType="",agencyId="";
    String deliveryCharge="0.0";
    String deliveryType="",lat="",deliveryYesNo="No";
    String bottomSheetStatus="";

    DeliveryAgencyAdapter deliveryAgencyAdapter;


    ArrayList<DeliveryAgencyModel.Result> deliveryAgencyList;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.activity_check_out_delivery, container, false);

        apiInterface = ApiClient.getClient(getContext()).create(AfaryCode.class);

        binding.RRback.setOnClickListener(v -> {

            getActivity().onBackPressed();

        });

       /* binding.RRdeliverhome.setOnClickListener(v -> {
            AlertDaliogOne("Delivery to my home");

        });

        binding.RRone.setOnClickListener(v -> {

           // AlertDaliogOne("Delivery to my office");




        });

        binding.RRTwo.setOnClickListener(v -> {

            AlertDaliogTwo();

        });

        binding.RRThree.setOnClickListener(v -> {

            AlertDaliogThree();

        });*/
      /*  binding.rdGrp.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener()
        {
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch(checkedId){
                    case R.id.rdDontWant:
                        deliveryType = "Don't want delivery";
                        if(adapter!=null) adapter.changesOnSelection(false);
                        PreferenceConnector.writeString(getActivity(), PreferenceConnector.LAT, "0.0");
                        PreferenceConnector.writeString(getActivity(), PreferenceConnector.LON,"0.0");
                        break;
                    case R.id.rdAddAddress:
                        deliveryType = "delivery on address";
                        callBottomSheet();
                        break;


                }
            }
        });*/















        binding.llDelivry.setOnClickListener(v -> {
         /*   if(deliveryType.equalsIgnoreCase("Don't want delivery")){
                startActivity(new Intent(getActivity(), CheckOutScreen.class));
            }
              if(deliveryType.equalsIgnoreCase("delivery on address")) {
                if(lat.equals("")) Toast.makeText(getActivity(), getString(R.string.please_select_address_type), Toast.LENGTH_SHORT).show();
                else  startActivity(new Intent(getActivity(), CheckOutScreen.class));
            }
            else*/ if(deliveryType.equalsIgnoreCase("")) {
                Toast.makeText(getActivity(), getString(R.string.please_select_address_type), Toast.LENGTH_SHORT).show();
            }
              else  startActivity(new Intent(getActivity(), CheckOutScreen.class)
                        .putExtra("agency",deliveryAgencyType)
                        .putExtra("charge",deliveryCharge)
                        .putExtra("agencyId",agencyId)
                        .putExtra("deliveryYesNo",deliveryYesNo));

            //else startActivity(new Intent(getActivity(), CheckOutScreen.class));

        });

        arrayList = new ArrayList<>();
        deliverArrayList = new ArrayList<>();



        adapter = new LocationAdapter(getActivity(),arrayList,CheckOutDelivery.this);
        binding.rvLocation.setAdapter(adapter);

        deliveryTypeAdapter = new DeliveryTypeAdapter(getActivity(),deliverArrayList,CheckOutDelivery.this);
        binding.rvDeliveryType.setAdapter(deliveryTypeAdapter);


        deliveryAgencyAdapter = new DeliveryAgencyAdapter(getActivity(),deliveryAgencyList,CheckOutDelivery.this);
        binding.rvDeliveryAgency.setAdapter(deliveryAgencyAdapter);


        binding.llAddress.setOnClickListener(view -> callBottomSheet("",""));


        binding.rdDontDelivery.setOnClickListener(view -> {
            deliveryType = "I don't want to be delivered, I will come and collect package myself";
            deliveryYesNo = "Yes";
            binding.rdDontDelivery.setChecked(true);

            for (int i=0;i<deliverArrayList.size();i++){
                deliverArrayList.get(i).setChk(false);
            }
            deliveryTypeAdapter.notifyDataSetChanged();


            for (int i=0;i<arrayList.size();i++){
                arrayList.get(i).setChk(false);
            }
            adapter.notifyDataSetChanged();

            dialogDontDelivery();
        });



        return binding.getRoot();
    }





    @Override
    public void onResume() {
        super.onResume();
        if(NetworkAvailablity.checkNetworkStatus(getActivity())) getLocation();
        else Toast.makeText(getActivity(), getString(R.string.network_failure), Toast.LENGTH_SHORT).show();


    }

    public void callBottomSheet(String title,String id){
        new AddAddressFragment(title,id).callBack(this::onAddress).show(getActivity().getSupportFragmentManager(),"");
    }

    private void AlertDaliog() {

        LayoutInflater li;
        TextView txtSKip;
        TextView txtok;
        AlertDialog.Builder alertDialogBuilder;
        li = LayoutInflater.from(getActivity());
        promptsView = li.inflate(R.layout.alert_delivery_one, null);
        txtSKip = (TextView) promptsView.findViewById(R.id.txtSKip);
        txtok = (TextView) promptsView.findViewById(R.id.txtok);
        alertDialogBuilder = new AlertDialog.Builder(getActivity());
        alertDialogBuilder.setView(promptsView);

        txtSKip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                alertDialog.dismiss();
            }
        });

        txtok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                alertDialog.dismiss();
            }
        });

        alertDialog = alertDialogBuilder.create();
        alertDialog.show();

        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

    }

    private void AlertDaliogOne(String str) {

        LayoutInflater li;
        TextView txtSKip;
        TextView txtok;
        TextView txtName;
        AlertDialog.Builder alertDialogBuilder;
        li = LayoutInflater.from(getActivity());

        promptsView1 = li.inflate(R.layout.alert_delivery_two, null);
        txtSKip = (TextView) promptsView1.findViewById(R.id.txtSKip);
        txtok = (TextView) promptsView1.findViewById(R.id.txtok);
        address_select = (EditText) promptsView1.findViewById(R.id.address_select);
        txtName = (TextView) promptsView1.findViewById(R.id.txtName);
        location_details = (EditText) promptsView1.findViewById(R.id.location_details);
        city = (EditText) promptsView1.findViewById(R.id.city);
        phone_no = "";
        type = "Home Address";
        alertDialogBuilder = new AlertDialog.Builder(getActivity());
        alertDialogBuilder.setView(promptsView1);

        address_select.setOnClickListener(v -> {
            startActivity(new Intent(getContext(),
                    GooglePlacesAutocompleteActivity.class).putExtra("get_address", "map"));
        });

        txtName.setText(str);
        txtSKip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog1.dismiss();
            }
        });

        txtok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                SetAddressAPI();

                alertDialog1.dismiss();
            }

        });

        alertDialog1 = alertDialogBuilder.create();
        alertDialog1.show();
        alertDialog1.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

    }

    private void SetAddressAPI() {

        DataManager.getInstance().showProgressMessage(getActivity(), "Please wait...");

        Map<String,String> headerMap = new HashMap<>();
        headerMap.put("Authorization","Bearer " +PreferenceConnector.readString(getActivity(), PreferenceConnector.access_token,""));
        headerMap.put("Accept","application/json");


        Map<String, String> map = new HashMap<>();
        map.put("user_id", PreferenceConnector.readString(getContext(), PreferenceConnector.User_id, ""));
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

                        Toast.makeText(getContext(), data.message, Toast.LENGTH_SHORT).show();


                    } else if (data.status.equals("0")) {
                        Toast.makeText(getActivity(), data.message, Toast.LENGTH_SHORT).show();
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

    private void AlertDaliogTwo() {

        LayoutInflater li;
        TextView txtSKip;
        TextView txtok;
        AlertDialog.Builder alertDialogBuilder;
        li = LayoutInflater.from(getActivity());
        promptsView1 = li.inflate(R.layout.alert_delivery_three, null);

        type = "Delevery To Another Person ";

        location_details = (EditText) promptsView1.findViewById(R.id.location_details);

        phone_number = (EditText) promptsView1.findViewById(R.id.phone_number);

        phone_no = phone_number.getText().toString().trim();
        txtSKip = (TextView) promptsView1.findViewById(R.id.txtSKip);
        txtok = (TextView) promptsView1.findViewById(R.id.txtok);
        alertDialogBuilder = new AlertDialog.Builder(getActivity());
        alertDialogBuilder.setView(promptsView1);

        txtSKip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                alertDialog1.dismiss();
            }
        });

        txtok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                SetAddressAPI();

                alertDialog1.dismiss();
            }
        });

        alertDialog1 = alertDialogBuilder.create();
        alertDialog1.show();

        alertDialog1.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

    }

    private void AlertDaliogThree() {

        LayoutInflater li;
        TextView txtSKip;
        TextView txtok;
        AlertDialog.Builder alertDialogBuilder;
        li = LayoutInflater.from(getActivity());
        promptsView1 = li.inflate(R.layout.alert_delivery_four, null);

        txtSKip = (TextView) promptsView1.findViewById(R.id.txtSKip);
        txtok = (TextView) promptsView1.findViewById(R.id.txtok);
        phone_no = "";
        type = "Pick Store";

        alertDialogBuilder = new AlertDialog.Builder(getActivity());
        alertDialogBuilder.setView(promptsView1);

        txtSKip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                alertDialog1.dismiss();
            }
        });

        txtok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                SetAddressAPI();
                alertDialog1.dismiss();
            }
        });

        alertDialog1 = alertDialogBuilder.create();
        alertDialog1.show();
        alertDialog1.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

    }

    @Override
    public void onAddress(String note) {
        bottomSheetStatus = note;
        if(NetworkAvailablity.checkNetworkStatus(getActivity())) getLocation();
        else Toast.makeText(getActivity(), getString(R.string.network_failure), Toast.LENGTH_SHORT).show();




    }




    public void getLocation(){
        DataManager.getInstance().showProgressMessage(getActivity(), getString(R.string.please_wait));

        Map<String,String> headerMap = new HashMap<>();
        headerMap.put("Authorization","Bearer " +PreferenceConnector.readString(getActivity(), PreferenceConnector.access_token,""));
        headerMap.put("Accept","application/json");

        Map<String, String> map = new HashMap<>();
        map.put("user_id", PreferenceConnector.readString(getActivity(), PreferenceConnector.User_id, ""));
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

                    geDeliveryType();

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
        DataManager.getInstance().showProgressMessage(getActivity(), getString(R.string.please_wait));

        Map<String,String> headerMap = new HashMap<>();
        headerMap.put("Authorization","Bearer " +PreferenceConnector.readString(getActivity(), PreferenceConnector.access_token,""));
        headerMap.put("Accept","application/json");
        Call<ResponseBody> loginCall = apiInterface.getDelivery(headerMap);
        loginCall.enqueue(new Callback<ResponseBody>() {
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
                        deliveryTypeAdapter.notifyDataSetChanged();


                    } else if (object.optString("status").equals("0")) {
                        deliverArrayList.clear();
                        deliveryTypeAdapter.notifyDataSetChanged();

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
           // binding.rdDontWant.setChecked(false);
           // binding.rdAddAddress.setChecked(true);
            lat = arrayList.get(position).getLat();
            deliveryYesNo = "No";
            binding.rdDontDelivery.setChecked(false);
            PreferenceConnector.writeString(getActivity(), PreferenceConnector.LAT, arrayList.get(position).getLat());
            PreferenceConnector.writeString(getActivity(), PreferenceConnector.LON, arrayList.get(position).getLon());
            getDeliveryAgency(arrayList.get(position).getId(),shopId);

        }

       else if(Type.equals("Edit"))
            new EditAddressFragment(arrayList.get(position)).callBack(this::onAddress).show(getActivity().getSupportFragmentManager(),"");


        else if(Type.equals("Deliver to my home")) {
            deliveryType = deliverArrayList.get(position).getTitle();
           // deliveryYesNo = "No";
            for (int i=0;i<deliverArrayList.size();i++){
                deliverArrayList.get(i).setChk(false);
            }
            deliverArrayList.get(position).setChk(true);
            deliveryTypeAdapter.notifyItemChanged(position);
            binding.rdDontDelivery.setChecked(false);
            deliveryAgencyList.clear();
            deliveryAgencyAdapter.notifyDataSetChanged();
            callBottomSheet(deliverArrayList.get(position).getTitle(),deliverArrayList.get(position).getId());


        }

        else if(Type.equals("Deliver to my office")) {
            deliveryType = deliverArrayList.get(position).getTitle();
           // deliveryYesNo = "No";

            for (int i=0;i<deliverArrayList.size();i++){
                deliverArrayList.get(i).setChk(false);
            }
            deliverArrayList.get(position).setChk(true);
            deliveryTypeAdapter.notifyItemChanged(position);
            binding.rdDontDelivery.setChecked(false);
            deliveryAgencyList.clear();
            deliveryAgencyAdapter.notifyDataSetChanged();
            callBottomSheet(deliverArrayList.get(position).getTitle(),deliverArrayList.get(position).getId());



        }

        else if(Type.equals("Deliver to another person")) {
           // callBottomSheet("",deliverArrayList.get(position).getId());
            deliveryType = deliverArrayList.get(position).getTitle();
           // deliveryYesNo = "No";
            for (int i=0;i<deliverArrayList.size();i++){
                deliverArrayList.get(i).setChk(false);
            }
            deliverArrayList.get(position).setChk(true);
            deliveryTypeAdapter.notifyItemChanged(position);
            binding.rdDontDelivery.setChecked(false);
            deliveryAgencyList.clear();
            deliveryAgencyAdapter.notifyDataSetChanged();
            callBottomSheet(deliverArrayList.get(position).getTitle(),deliverArrayList.get(position).getId());


        }

        else if(Type.equals("Deliver where i am now")) {
            //callBottomSheet("",deliverArrayList.get(position).getId());
            deliveryType = deliverArrayList.get(position).getTitle();
          //  deliveryYesNo = "No";
            for (int i=0;i<deliverArrayList.size();i++){
                deliverArrayList.get(i).setChk(false);
            }
            deliverArrayList.get(position).setChk(true);
            deliveryTypeAdapter.notifyItemChanged(position);
            binding.rdDontDelivery.setChecked(false);
            deliveryAgencyList.clear();
            deliveryAgencyAdapter.notifyDataSetChanged();
            callBottomSheet(deliverArrayList.get(position).getTitle(),deliverArrayList.get(position).getId());

        }

        else if(Type.equals("")) {
           // deliveryType = deliverArrayList.get(position).getTitle();
              dialogDontDelivery();
        }

        else if(Type.equals("deliveryAgency")) {
            deliveryCharge = deliveryAgencyList.get(position).getPrice()+"";
            agencyId = deliveryAgencyList.get(position).getId();
        }

    }

    private void dialogDontDelivery() {
        LayoutInflater li;
        TextView txtSKip;
        TextView txtok;
        AlertDialog.Builder alertDialogBuilder;
        li = LayoutInflater.from(getActivity());

        promptsView1 = li.inflate(R.layout.dialog_dont_delivery, null);
        txtSKip = (TextView) promptsView1.findViewById(R.id.txtSKip);
        txtok = (TextView) promptsView1.findViewById(R.id.txtok);

        alertDialogBuilder = new AlertDialog.Builder(getActivity());
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
                PreferenceConnector.writeString(getActivity(), PreferenceConnector.LAT, "");
                PreferenceConnector.writeString(getActivity(), PreferenceConnector.LON, "");
                startActivity(new Intent(getActivity(), CheckOutScreen.class)
                        .putExtra("agency",deliveryAgencyType)
                        .putExtra("charge",deliveryCharge)
                        .putExtra("agencyId",agencyId)
                        .putExtra("deliveryYesNo",deliveryYesNo));
            }

        });

        alertDialog1 = alertDialogBuilder.create();
        alertDialog1.show();
        alertDialog1.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
    }



    public void DeleteAlert(String id1){
        AlertDialog.Builder  builder1 = new AlertDialog.Builder(getActivity());
        builder1.setMessage(getResources().getString(R.string.are_you_sure_you_want_to_delete_this_address));
        builder1.setCancelable(false);

        builder1.setPositiveButton(
                "Yes",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                        if(NetworkAvailablity.checkNetworkStatus(getActivity())) DeleteLocation(id1);
                        else Toast.makeText(getActivity(), getString(R.string.network_failure), Toast.LENGTH_SHORT).show();
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
        DataManager.getInstance().showProgressMessage(getActivity(), getString(R.string.please_wait));
        Map<String,String> headerMap = new HashMap<>();
        headerMap.put("Authorization","Bearer " +PreferenceConnector.readString(getActivity(), PreferenceConnector.access_token,""));
        headerMap.put("Accept","application/json");

        Map<String, String> map = new HashMap<>();
        map.put("user_id", PreferenceConnector.readString(getActivity(), PreferenceConnector.User_id, ""));
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
                        if(NetworkAvailablity.checkNetworkStatus(getActivity())) getLocation();
                        else Toast.makeText(getActivity(), getString(R.string.network_failure), Toast.LENGTH_SHORT).show();
                    } else if (object.optString("status").equals("0")) {
                        Toast.makeText(getActivity(), object.getString("message"), Toast.LENGTH_SHORT).show();


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

    private void getDeliveryAgency(String addressId,String productId) {
        DataManager.getInstance().showProgressMessage(getActivity(), getString(R.string.please_wait));
        Map<String,String> headerMap = new HashMap<>();
        headerMap.put("Authorization","Bearer " +PreferenceConnector.readString(getActivity(), PreferenceConnector.access_token,""));
        headerMap.put("Accept","application/json");

        Map<String, String> map = new HashMap<>();
        map.put("user_id", PreferenceConnector.readString(getActivity(), PreferenceConnector.User_id, ""));
        map.put("address_id", addressId);
        map.put("shop_id", productId);

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
                        deliveryAgencyAdapter.notifyDataSetChanged();
                        deliveryAgencyType ="International";


                    } else if (object.optString("status").equals("0")) {
                        deliveryAgencyList.clear();
                        deliveryAgencyAdapter.notifyDataSetChanged();
                        deliveryAgencyType ="Afary Code";

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