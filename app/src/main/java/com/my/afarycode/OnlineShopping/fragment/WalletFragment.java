package com.my.afarycode.OnlineShopping.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.my.afarycode.OnlineShopping.Model.AddAvailable;
import com.my.afarycode.OnlineShopping.Model.Add_Wish_To_Cart_Modal;
import com.my.afarycode.OnlineShopping.Model.CategoryModal;
import com.my.afarycode.OnlineShopping.Model.GetProfileModal;
import com.my.afarycode.OnlineShopping.Model.GetTransferDetails;
import com.my.afarycode.OnlineShopping.Model.HomeShopeProductModel;
import com.my.afarycode.OnlineShopping.WishListActivity;
import com.my.afarycode.OnlineShopping.activity.CheckOutDeliveryAct;
import com.my.afarycode.OnlineShopping.adapter.WalletAdapter;
import com.my.afarycode.OnlineShopping.constant.PreferenceConnector;
import com.my.afarycode.OnlineShopping.helper.DataManager;
import com.my.afarycode.OnlineShopping.listener.AskListener;
import com.my.afarycode.R;
import com.my.afarycode.databinding.FragmentWalletBinding;
import com.my.afarycode.ratrofit.AfaryCode;
import com.my.afarycode.ratrofit.ApiClient;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class WalletFragment extends Fragment implements AskListener {

    FragmentWalletBinding binding;

    WalletAdapter mAdapter;
    private ArrayList<HomeShopeProductModel> modelList = new ArrayList<>();
    private AfaryCode apiInterface;
    private ArrayList<GetTransferDetails.Result> get_result = new ArrayList<>();
    GetProfileModal data;
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_wallet, container, false);


        apiInterface = ApiClient.getClient(getContext()).create(AfaryCode.class);

        GetProfile();
       // GetAvailableBal();
        GetTransactionAPI();

        binding.RRback.setOnClickListener(v -> {
            getFragmentManager().popBackStack();
        });


        //binding.recyclerWallet

        binding.txtAddMoney.setOnClickListener(v -> {
           // BottomAddFragment bottomSheetFragment = new BottomAddFragment(getActivity());
            new BottomAddFragment(getActivity()).callBack(this::ask).show(getActivity().getSupportFragmentManager(), "ModalBottomSheet");
        });

        binding.txtWithdrawMoney.setOnClickListener(v -> {
           // WithDrawFragment bottomSheetFragment = new WithDrawFragment(getActivity());
           new WithDrawFragment(getActivity(),data.getResult().getWallet()).show(getActivity().getSupportFragmentManager(), "ModalBottomSheet");
        });

        binding.txtTransactMoney.setOnClickListener(v -> {
            TransferMOneyFragment bottomSheetFragment = new TransferMOneyFragment(getActivity());
            bottomSheetFragment.show(getActivity().getSupportFragmentManager(), "ModalBottomSheet");
        });

        return binding.getRoot();
    }


    private void GetProfile() {

        DataManager.getInstance().showProgressMessage(getActivity(), "Please wait...");
        Map<String, String> map = new HashMap<>();
        map.put("user_id", PreferenceConnector.readString(getActivity(), PreferenceConnector.User_id, ""));
        Call<GetProfileModal> loginCall = apiInterface.get_profile(map);

        loginCall.enqueue(new Callback<GetProfileModal>() {
            @Override
            public void onResponse(Call<GetProfileModal> call,
                                   Response<GetProfileModal> response) {

                DataManager.getInstance().hideProgressMessage();

                try {

                    data = response.body();
                    String dataResponse = new Gson().toJson(response.body());
                    Log.e("MapMap", "GET RESPONSE" + dataResponse);

                    if (data.status.equals("1")) {
                        binding.textAmount.setText("$ " + data.result.getWallet());
                    } else if (data.status.equals("0")) {
                        Toast.makeText(getActivity(), data.message /*getString(R.string.wrong_username_password)*/, Toast.LENGTH_SHORT).show();
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




    private void GetAvailableBal() {

        DataManager.getInstance().showProgressMessage((Activity) getContext(), "Please wait...");
        Map<String,String> headerMap = new HashMap<>();
        headerMap.put("Authorization","Bearer " +PreferenceConnector.readString(getActivity(), PreferenceConnector.access_token,""));
        headerMap.put("Accept","application/json");

        Map<String, String> map = new HashMap<>();
        map.put("user_id", PreferenceConnector.readString(getContext(), PreferenceConnector.User_id, ""));
        Log.e("MapMap", "" + map);

        Call<AddAvailable> loginCall = apiInterface.get_available(headerMap,map);

        loginCall.enqueue(new Callback<AddAvailable>() {

            @Override
            public void onResponse(Call<AddAvailable> call, Response<AddAvailable> response) {

                DataManager.getInstance().hideProgressMessage();

                try {
                    AddAvailable data = response.body();
                    String dataResponse = new Gson().toJson(response.body());
                    Log.e("MapMap", "Exersice_List" + dataResponse);

                    if (data.status.equals("1")) {

                        binding.textAmount.setText("$ " + data.result.getWallet());

                    } else if (data.status.equals("0")) {
                        Toast.makeText(getContext(), data.message, Toast.LENGTH_SHORT).show();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<AddAvailable> call, Throwable t) {
                call.cancel();
                DataManager.getInstance().hideProgressMessage();
            }
        });
    }

    private void GetTransactionAPI() {

        DataManager.getInstance().showProgressMessage((Activity) getContext(), "Please wait...");
        Map<String,String> headerMap = new HashMap<>();
        headerMap.put("Authorization","Bearer " +PreferenceConnector.readString(getActivity(), PreferenceConnector.access_token,""));
        headerMap.put("Accept","application/json");

        Map<String, String> map = new HashMap<>();
        map.put("user_id", PreferenceConnector.readString(getContext(), PreferenceConnector.User_id, ""));
        Log.e("MapMap", "" + map);

        Call<GetTransferDetails> loginCall = apiInterface.get_transfer_money(headerMap,map);

        loginCall.enqueue(new Callback<GetTransferDetails>() {

            @Override
            public void onResponse(Call<GetTransferDetails> call, Response<GetTransferDetails> response) {

                DataManager.getInstance().hideProgressMessage();

                try {
                    GetTransferDetails data = response.body();
                    String dataResponse = new Gson().toJson(response.body());
                    Log.e("MapMap", "Exersice_List" + dataResponse);

                    if (data.status.equals("1")) {
                        get_result.clear();
                        get_result.addAll(data.getResult());
                        setAdapter();

                    } else if (data.status.equals("0")) {
                        Toast.makeText(getContext(), data.message, Toast.LENGTH_SHORT).show();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<GetTransferDetails> call, Throwable t) {
                call.cancel();
                DataManager.getInstance().hideProgressMessage();
            }
        });
    }

    private void setAdapter() {

        mAdapter = new WalletAdapter(getActivity(), get_result);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        binding.recyclerWallet.setLayoutManager(linearLayoutManager);
        binding.recyclerWallet.setAdapter(mAdapter);
    }

    @Override
    public void ask(String value) {
        GetTransactionAPI();
        GetProfile();
    }
}