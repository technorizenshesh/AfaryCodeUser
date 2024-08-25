package com.my.afarycode.OnlineShopping.fragment;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.gson.Gson;
import com.my.afarycode.OnlineShopping.CheckOutDelivery;
import com.my.afarycode.OnlineShopping.Model.DeliveryAgencyModel;
import com.my.afarycode.OnlineShopping.Model.HomeShopeProductModel;
import com.my.afarycode.OnlineShopping.Model.LocationModel;
import com.my.afarycode.OnlineShopping.MyBookingDetails;
import com.my.afarycode.OnlineShopping.adapter.LocationAdapter;
import com.my.afarycode.OnlineShopping.adapter.MyAddAddressAdapter;
import com.my.afarycode.OnlineShopping.adapter.MyAddressAdapter;
import com.my.afarycode.OnlineShopping.adapter.MyBookingAdapter;
import com.my.afarycode.OnlineShopping.bottomsheet.EditAddressFragment;
import com.my.afarycode.OnlineShopping.constant.PreferenceConnector;
import com.my.afarycode.OnlineShopping.helper.DataManager;
import com.my.afarycode.OnlineShopping.helper.NetworkAvailablity;
import com.my.afarycode.OnlineShopping.listener.addAddressListener;
import com.my.afarycode.OnlineShopping.listener.onPosListener;
import com.my.afarycode.R;
import com.my.afarycode.Splash;
import com.my.afarycode.databinding.FragmentMyAddressBinding;
import com.my.afarycode.databinding.FragmentMyBookingBinding;
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


public class MyAddressFragment extends Fragment implements addAddressListener, onPosListener {
    public String TAG = "MyAddressFragment";

    FragmentMyAddressBinding binding;

    Fragment fragment;

    ArrayList<LocationModel.Result> arrayList= new ArrayList<>();
    MyAddressAdapter adapter;

    private AfaryCode apiInterface;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        apiInterface = ApiClient.getClient(getContext()).create(AfaryCode.class);
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_my_address, container, false);

        binding.RRback.setOnClickListener(v ->{
            getFragmentManager().popBackStack();
        });

        adapter = new MyAddressAdapter(getActivity(),arrayList, MyAddressFragment.this);
        binding.rvLocation.setAdapter(adapter);

        if(NetworkAvailablity.checkNetworkStatus(getActivity())) getLocation();
        else Toast.makeText(getActivity(), getString(R.string.network_failure), Toast.LENGTH_SHORT).show();

        return binding.getRoot();

    }

    public void getLocation(){
        DataManager.getInstance().showProgressMessage(getActivity(), getString(R.string.please_wait));

        Map<String,String> headerMap = new HashMap<>();
        headerMap.put("Authorization","Bearer " + PreferenceConnector.readString(getActivity(), PreferenceConnector.access_token,""));
        headerMap.put("Accept","application/json");

        Map<String, String> map = new HashMap<>();
        map.put("user_id", PreferenceConnector.readString(getActivity(), PreferenceConnector.User_id, ""));
        map.put("register_id", PreferenceConnector.readString(getActivity(), PreferenceConnector.Register_id, ""));

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
                        PreferenceConnector.writeString(getActivity(), PreferenceConnector.LoginStatus, "false");
                        startActivity(new Intent(getActivity(), Splash.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP));
                        getActivity().finish();
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



    public boolean loadFragment(Fragment fragment) {
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        if (fragment != null) {
            fragmentManager.beginTransaction()
                    .addToBackStack("Home")
                    .replace(R.id.fragment_homeContainer, fragment)//, tag)
                    .commit();
            return true;
        }
        return false;
    }

    @Override
    public void onAddress(String note) {
        if(NetworkAvailablity.checkNetworkStatus(getActivity())) getLocation();
        else Toast.makeText(getActivity(), getString(R.string.network_failure), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onPos(int position) {

    }

    @Override
    public void onPos(int position, String Type) {

        if(Type.equals("Delete")) DeleteAlert(arrayList.get(position).getId());

        else if(Type.equals("Edit"))
            new EditAddressFragment(arrayList.get(position)).callBack(this::onAddress).show(getActivity().getSupportFragmentManager(),"");

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
        map.put("register_id", PreferenceConnector.readString(getActivity(), PreferenceConnector.Register_id, ""));
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

                    else if (object.getString("status").equals("5")) {
                        PreferenceConnector.writeString(getActivity(), PreferenceConnector.LoginStatus, "false");
                        startActivity(new Intent(getActivity(), Splash.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP));
                        getActivity().finish();
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



}