package com.my.afarycode.OnlineShopping;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.gson.Gson;
import com.my.afarycode.OnlineShopping.Model.CartModal;
import com.my.afarycode.OnlineShopping.Model.GetRestorentsModal;
import com.my.afarycode.OnlineShopping.Model.GetWishListModal;
import com.my.afarycode.OnlineShopping.Model.HomeShopeProductModel;
import com.my.afarycode.OnlineShopping.Model.ShoppingStoreDetailsModal;
import com.my.afarycode.OnlineShopping.activity.CheckOutDeliveryAct;
import com.my.afarycode.OnlineShopping.adapter.MyBookingAdapter;
import com.my.afarycode.OnlineShopping.adapter.MyWishListAdapter;
import com.my.afarycode.OnlineShopping.adapter.ShoppingStoreAdapter;
import com.my.afarycode.OnlineShopping.constant.PreferenceConnector;
import com.my.afarycode.OnlineShopping.helper.DataManager;
import com.my.afarycode.OnlineShopping.myorder.OrderModel;
import com.my.afarycode.R;
import com.my.afarycode.databinding.ActivityWishListBinding;
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

public class WishListActivity extends Fragment {

    MyWishListAdapter mAdapter;
    private ArrayList<GetWishListModal.Result> get_result;

    ActivityWishListBinding binding;
    private AfaryCode apiInterface;
    private MyWishListAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.activity_wish_list, container, false);
        apiInterface = ApiClient.getClient(getContext()).create(AfaryCode.class);
        SetupUI();

        return binding.getRoot();

    }

    private void SetupUI() {
        get_result = new ArrayList<>();

        adapter = new MyWishListAdapter(getActivity(), get_result);
        binding.recyclerwishList.setAdapter(adapter);

         GetWishListAPI();

        binding.RRback.setOnClickListener(v -> {
            getActivity().onBackPressed();
        });


    }

    private void GetWishListAPI() {

        DataManager.getInstance().showProgressMessage(getActivity(), "Please wait...");

        Map<String,String> headerMap = new HashMap<>();
        headerMap.put("Authorization","Bearer " +PreferenceConnector.readString(getActivity(), PreferenceConnector.access_token,""));
        headerMap.put("Accept","application/json");
        Map<String, String> map = new HashMap<>();
        map.put("user_id", PreferenceConnector.readString(getContext(), PreferenceConnector.User_id, ""));

        Log.e("MapMap", "EXERSICE LIST" + map);

        Call<ResponseBody> loginCall = apiInterface.get_wish_list(headerMap,map);

        loginCall.enqueue(new Callback<ResponseBody>() {

            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                DataManager.getInstance().hideProgressMessage();

                try {


                    String stringResponse = response.body().string();
                    JSONObject jsonObject = new JSONObject(stringResponse);
                    Log.e("response===",response.body().toString());

                    if(jsonObject.getString("status").equals("1")) {
                        GetWishListModal model = new Gson().fromJson(stringResponse,GetWishListModal.class);
                        get_result.clear();
                        get_result.addAll(model.result);
                        adapter.notifyDataSetChanged();



                    } else if (jsonObject.getString("status").equals("0")) {
                        Toast.makeText(getContext(),jsonObject.getString("message") , Toast.LENGTH_SHORT).show();
                        get_result.clear();
                        adapter.notifyDataSetChanged();

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