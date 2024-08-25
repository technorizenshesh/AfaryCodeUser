package com.my.afarycode.OnlineShopping.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.google.gson.Gson;
import com.my.afarycode.OnlineShopping.constant.PreferenceConnector;
import com.my.afarycode.OnlineShopping.helper.DataManager;
import com.my.afarycode.OnlineShopping.myorder.OrderAdapter;
import com.my.afarycode.OnlineShopping.myorder.OrderListener;
import com.my.afarycode.OnlineShopping.myorder.OrderModel;
import com.my.afarycode.OnlineShopping.ratereview.RateReviewAct;
import com.my.afarycode.R;
import com.my.afarycode.Splash;
import com.my.afarycode.databinding.FragmentCancelOrderBinding;
import com.my.afarycode.databinding.FragmentCompleteOrderBinding;
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

public class OrderCancelFragment extends Fragment implements OrderListener {
    public String TAG = "OrderCancelFragment";

    FragmentCancelOrderBinding binding;
    private AfaryCode apiInterface;
    OrderAdapter adapter;

    private ArrayList<OrderModel.Result> arrayList;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_cancel_order, container, false);
        apiInterface = ApiClient.getClient(getActivity()).create(AfaryCode.class);

        return binding.getRoot();
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        arrayList = new ArrayList<>();


        adapter = new OrderAdapter(getActivity(), arrayList,OrderCancelFragment.this);
        binding.rvCancel.setAdapter(adapter);

        getOrderHistory();
    }

    private void getOrderHistory() {
        DataManager.getInstance().showProgressMessage(getActivity(), "Please wait...");
        Map<String,String> headerMap = new HashMap<>();
        headerMap.put("Authorization","Bearer " + PreferenceConnector.readString(getActivity(), PreferenceConnector.access_token,""));
        headerMap.put("Accept","application/json");

        Map<String, String> map = new HashMap<>();
        map.put("user_id", PreferenceConnector.readString(getActivity(), PreferenceConnector.User_id, ""));
        map.put("status", "Cancelled");
        map.put("register_id", PreferenceConnector.readString(getActivity(), PreferenceConnector.Register_id, ""));

        Log.e(TAG, "EXERSICE LIST" + map);
        Call<ResponseBody> loginCall = apiInterface.getHistoryOnlineOrderApi(headerMap,map);

        loginCall.enqueue(new Callback<ResponseBody>() {

            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                DataManager.getInstance().hideProgressMessage();

                try {

                    String stringResponse = response.body().string();
                    JSONObject jsonObject = new JSONObject(stringResponse);
                    Log.e("response===",response.body().toString());
                    if (jsonObject.getString("status").toString().equals("1")) {
                        // binding.tvNotFount.setVisibility(View.GONE);
                        OrderModel model = new Gson().fromJson(stringResponse,OrderModel.class);
                        arrayList.clear();
                        arrayList.addAll(model.getResult());
                        adapter.notifyDataSetChanged();
                    } else if (jsonObject.getString("status").toString().equals("0")) {
                        // binding.tvNotFount.setVisibility(View.VISIBLE);
                        Toast.makeText(getActivity(), jsonObject.getString("message").toString(), Toast.LENGTH_SHORT).show();
                        arrayList.clear();
                        adapter.notifyDataSetChanged();
                    }
                    else if (jsonObject.getString("status").equals("5")) {
                        PreferenceConnector.writeString(getActivity(), PreferenceConnector.LoginStatus, "false");
                        startActivity(new Intent(getActivity(), Splash.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP));
                        getActivity().finish();

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

    @Override
    public void onOrder(OrderModel.Result result) {

    }
}
