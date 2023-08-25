/*
package com.my.afarycode.OnlineShopping.fragment;

import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.gson.Gson;
import com.my.afarycode.OnlineShopping.CheckOutDelivery;
import com.my.afarycode.OnlineShopping.Model.LocationModel;
import com.my.afarycode.OnlineShopping.adapter.LocationAdapter;
import com.my.afarycode.OnlineShopping.constant.PreferenceConnector;
import com.my.afarycode.OnlineShopping.helper.DataManager;
import com.my.afarycode.OnlineShopping.listener.addAddressListener;
import com.my.afarycode.R;
import com.my.afarycode.databinding.FragmentAddAddressBinding;
import com.my.afarycode.databinding.FragmentMyAddressBinding;
import com.my.afarycode.databinding.FragmentMyHomeAddressBinding;
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

public class AddMyHomeAddressBottomSheet extends BottomSheetDialogFragment {
    public String TAG = "AddMyHomeAddressBottomSheet";
    BottomSheetDialog dialog;
    private BottomSheetBehavior<View> mBehavior;
    FragmentMyHomeAddressBinding binding;
    public static View v1;

    AfaryCode apiInterface;
    addAddressListener listener;

    ArrayList<LocationModel.Result> arrayList;
    LocationAdapter adapter;

    public AddMyHomeAddressBottomSheet() {
    }

    public AddMyHomeAddressBottomSheet callBack(addAddressListener listener) {
        this.listener = listener;
        return this;
    }


    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        dialog = (BottomSheetDialog) super.onCreateDialog(savedInstanceState);
        binding = DataBindingUtil.inflate(LayoutInflater.from(getActivity()), R.layout.fragment_my_home_address, null, false);
        dialog.setContentView(binding.getRoot());
        apiInterface = ApiClient.getClient(getActivity()).create(AfaryCode.class);
        mBehavior = BottomSheetBehavior.from((View) binding.getRoot().getParent());
        mBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        initBinding();
        return dialog;
    }

    private void initBinding() {
        arrayList = new ArrayList<>();

        adapter = new LocationAdapter(getActivity(),arrayList, AddMyHomeAddressBottomSheet.this);
        binding.rvLocation.setAdapter(adapter);

    }


    public void getLocation(){
        DataManager.getInstance().showProgressMessage(getActivity(), getString(R.string.please_wait));

        Map<String,String> headerMap = new HashMap<>();
        headerMap.put("Authorization","Bearer " + PreferenceConnector.readString(getActivity(), PreferenceConnector.access_token,""));
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

}
*/
