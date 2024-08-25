package com.my.afarycode.OnlineShopping;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.GridLayoutManager;

import com.google.gson.Gson;
import com.my.afarycode.OnlineShopping.Model.CartModal;
import com.my.afarycode.OnlineShopping.Model.HomeShopeProductModel;
import com.my.afarycode.OnlineShopping.Model.ShoppingStoreDetailsModal;
import com.my.afarycode.OnlineShopping.activity.CardAct;
import com.my.afarycode.OnlineShopping.activity.CheckOutDeliveryAct;
import com.my.afarycode.OnlineShopping.adapter.ShopProductListAdapter;
import com.my.afarycode.OnlineShopping.adapter.ShoppingStoreAdapter;
import com.my.afarycode.OnlineShopping.adapter.SliderAdapterExample;
import com.my.afarycode.OnlineShopping.constant.PreferenceConnector;
import com.my.afarycode.OnlineShopping.helper.DataManager;
import com.my.afarycode.R;
import com.my.afarycode.Splash;
import com.my.afarycode.databinding.ActivityAllShopOnlineBinding;
import com.my.afarycode.ratrofit.AfaryCode;
import com.my.afarycode.ratrofit.ApiClient;
import com.smarteist.autoimageslider.IndicatorView.animation.type.IndicatorAnimationType;
import com.smarteist.autoimageslider.SliderAnimations;
import com.smarteist.autoimageslider.SliderView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AllShopOnlineActivity extends Fragment {

    ActivityAllShopOnlineBinding binding;

    ShopProductListAdapter mAdapter;
    private ArrayList<HomeShopeProductModel> modelList = new ArrayList<>();
    private ArrayList<ShoppingStoreDetailsModal.Result> get_result1 = new ArrayList<>();

    Fragment fragment;
    private String restorents_id = "";
    private AfaryCode apiInterface;
    private ShoppingStoreAdapter adapter;
    public static TextView item_quantity;
    private ArrayList<CartModal.Result> get_result;
    SliderAdapterExample adapter1;
    private ArrayList<String>banner_array_list;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        binding = DataBindingUtil.inflate(inflater, R.layout.activity_all_shop_online, container, false);

        SetupUI();

        apiInterface = ApiClient.getClient(getContext()).create(AfaryCode.class);

        restorents_id = getArguments().getString("restorents_id");

        binding.RRback.setOnClickListener(v -> {
            getActivity().onBackPressed();
        });

        binding.cardGo.setOnClickListener(v -> {
         //   fragment = new CardActivity();
          // loadFragment(fragment);
            startActivity(new Intent(getActivity(), CardAct.class));


        });


        binding.llicon.setOnClickListener(v -> {
            //   fragment = new CardActivity();
            // loadFragment(fragment);
            startActivity(new Intent(getActivity(), CardAct.class));


        });




        GetRestorentsDetailsAPI(restorents_id);

        setAdapter();

        return binding.getRoot();
    }

    private void SetupUI() {
        get_result = new ArrayList<>();
        banner_array_list = new ArrayList<>();
        item_quantity = (TextView) binding.getRoot().findViewById(R.id.item_quantity);
    }

    private void GetRestorentsDetailsAPI(String restorents_id) {

        DataManager.getInstance().showProgressMessage(getActivity(), "Please wait...");


        Map<String,String> headerMap = new HashMap<>();
        headerMap.put("Authorization","Bearer " +PreferenceConnector.readString(getActivity(), PreferenceConnector.access_token,""));
        headerMap.put("Accept","application/json");

        Map<String, String> map = new HashMap<>();
        map.put("user_id", PreferenceConnector.readString(getContext(), PreferenceConnector.User_id, ""));
        map.put("restaurant_id", restorents_id);
        map.put("register_id", PreferenceConnector.readString(getActivity(), PreferenceConnector.Register_id, ""));

        Log.e("MapMap", "EXERSICE LIST" + map);

        Call<ShoppingStoreDetailsModal> loginCall = apiInterface.get_restaurant_detail(headerMap,map);

        loginCall.enqueue(new Callback<ShoppingStoreDetailsModal>() {

            @Override
            public void onResponse(Call<ShoppingStoreDetailsModal> call,
                                   Response<ShoppingStoreDetailsModal> response) {

                DataManager.getInstance().hideProgressMessage();

                try {

                    ShoppingStoreDetailsModal data = response.body();
                    String dataResponse = new Gson().toJson(response.body());
                    Log.e("MapMap", "Exersice_List" + dataResponse);

                    if (data.status.equals("1")) {

                        get_result1.clear();
                        get_result1.addAll(data.getResult());

                        if (!data.image.equals("")) {
                            Picasso.get().load(data.image1).into(binding.imgShop);
                        }

                        binding.tvTitleName.setText(data.name);
                        binding.tvTitleAddress.setText(data.address);
                        binding.storeName.setText(data.name);
                        binding.addressStore.setText(data.address);
                        binding.time.setText("Opens: " + data.openTime + " | Close: " + data.closeTime);


                            banner_array_list.add(data.image1);
                            banner_array_list.add(data.image2);
                            banner_array_list.add(data.image3);

                            adapter1 = new SliderAdapterExample(getContext(), banner_array_list);
                            binding.imageSlider.setSliderAdapter(adapter1);
                            binding.imageSlider.setIndicatorAnimation(IndicatorAnimationType.WORM); //set indicator animation by using SliderLayout.IndicatorAnimations. :WORM or THIN_WORM or COLOR or DROP or FILL or NONE or SCALE or SCALE_DOWN or SLIDE and SWAP!!
                            binding.imageSlider.setSliderTransformAnimation(SliderAnimations.SIMPLETRANSFORMATION);
                            binding.imageSlider.setAutoCycleDirection(SliderView.AUTO_CYCLE_DIRECTION_BACK_AND_FORTH);
                            //     binding.imageSlider.setIndicatorSelectedColor(R.color.colorPrimary);
                            //      binding.imageSlider.setIndicatorUnselectedColor(Color.GRAY);
                            binding.imageSlider.setScrollTimeInSec(3);
                            binding.imageSlider.setAutoCycle(true);
                            binding.imageSlider.startAutoCycle();


                        adapter = new ShoppingStoreAdapter(getActivity(), get_result1);

                        binding.recyclerAllShopStore.setLayoutManager
                                (new GridLayoutManager(getActivity(), 2));

                        binding.recyclerAllShopStore.setAdapter(adapter);
                        GetCartItem();

                    } else if (data.status.equals("0")) {
                        Toast.makeText(getContext(), data.message, Toast.LENGTH_SHORT).show();
                    }

                    else if (data.status.equals("5")) {
                        PreferenceConnector.writeString(getActivity(), PreferenceConnector.LoginStatus, "false");
                        startActivity(new Intent(getActivity(), Splash.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP));
                        getActivity().finish();
                    }


                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ShoppingStoreDetailsModal> call, Throwable t) {
                call.cancel();
                DataManager.getInstance().hideProgressMessage();
            }
        });
    }

    private void setAdapter() {

        modelList.add(new HomeShopeProductModel("Being Human Shirt", R.drawable.img1));
        modelList.add(new HomeShopeProductModel("Being Human Shirt", R.drawable.img2));
        modelList.add(new HomeShopeProductModel("Being Human Shirt", R.drawable.img3));
        modelList.add(new HomeShopeProductModel("Being Human Shirt", R.drawable.img4));
    }

    public boolean loadFragment(Fragment fragment) {
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        if (fragment != null) {
            fragmentManager.beginTransaction()
                    .addToBackStack("Home")
                    .replace(R.id.fragment_homeContainer, fragment)
                    .commit();
            return true;
        }
        return false;
    }

    @Override
    public void onResume() {
        GetRestorentsDetailsAPI(restorents_id);
        super.onResume();

    }

    private void GetCartItem() {

       // DataManager.getInstance().showProgressMessage(getActivity(), "Please wait...");
        Map<String,String> headerMap = new HashMap<>();
        headerMap.put("Authorization","Bearer " +PreferenceConnector.readString(getActivity(), PreferenceConnector.access_token,""));
        headerMap.put("Accept","application/json");
        Map<String, String> map = new HashMap<>();
        map.put("user_id", PreferenceConnector.readString(getContext(), PreferenceConnector.User_id, ""));
        map.put("register_id", PreferenceConnector.readString(getActivity(), PreferenceConnector.Register_id, ""));

        Log.e("MapMap", "EXERSICE LIST" + map);

        Call<CartModal> loginCall = apiInterface.get_cart(headerMap,map);

        loginCall.enqueue(new Callback<CartModal>() {

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

                        if(get_result.size()>0){
                            binding.RRbtm.setVisibility(View.VISIBLE);
                            binding.reqcount.setVisibility(View.VISIBLE);
                            binding.reqcount.setText(get_result.size()+"");
                            binding.itemQuantity.setText(get_result.size()+" Item");
                        }else {
                            binding.RRbtm.setVisibility(View.GONE);
                            binding.reqcount.setVisibility(View.GONE);

                        }

                    } else if (data.status.equals("0")) {
                        Toast.makeText(getContext(), "No Data Found !!!!", Toast.LENGTH_SHORT).show();
                        get_result.clear();
                        binding.RRbtm.setVisibility(View.GONE);
                        binding.reqcount.setVisibility(View.GONE);


                    }

                    else if (data.status.equals("5")) {
                        PreferenceConnector.writeString(getActivity(), PreferenceConnector.LoginStatus, "false");
                        startActivity(new Intent(getActivity(), Splash.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP));
                        getActivity().finish();
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




}