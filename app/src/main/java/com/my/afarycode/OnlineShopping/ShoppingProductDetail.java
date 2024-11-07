package com.my.afarycode.OnlineShopping;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Toast;

import com.google.gson.Gson;
import com.my.afarycode.OnlineShopping.Model.Add_To_Cart_Modal;
import com.my.afarycode.OnlineShopping.Model.CartModal;
import com.my.afarycode.OnlineShopping.Model.HomeOfferModel;
import com.my.afarycode.OnlineShopping.Model.ReviewModal;
import com.my.afarycode.OnlineShopping.Model.ShoppingProductModal;
import com.my.afarycode.OnlineShopping.Model.ShoppingStoreDetailsModal;
import com.my.afarycode.OnlineShopping.activity.CheckOutDeliveryAct;
import com.my.afarycode.OnlineShopping.adapter.ReviewAdapter;
import com.my.afarycode.OnlineShopping.adapter.ReviewProductAdapter;
import com.my.afarycode.OnlineShopping.adapter.ShopOnlineAdapter;
import com.my.afarycode.OnlineShopping.adapter.ShoppingStoreAdapter;
import com.my.afarycode.OnlineShopping.adapter.SliderAdapterExample;
import com.my.afarycode.OnlineShopping.constant.PreferenceConnector;
import com.my.afarycode.OnlineShopping.helper.DataManager;
import com.my.afarycode.R;
import com.my.afarycode.Splash;
import com.my.afarycode.databinding.ActivityShoppingProductDetail2Binding;
import com.my.afarycode.databinding.ActivityShoppingProductDetailBinding;
import com.my.afarycode.ratrofit.AfaryCode;
import com.my.afarycode.ratrofit.ApiClient;
import com.smarteist.autoimageslider.IndicatorView.animation.type.IndicatorAnimationType;
import com.smarteist.autoimageslider.SliderAnimations;
import com.smarteist.autoimageslider.SliderView;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ShoppingProductDetail extends Fragment {
    public String TAG ="ShoppingProductDetail";
    ActivityShoppingProductDetail2Binding binding;

    ReviewProductAdapter mAdapter;
    private ArrayList<HomeOfferModel> modelList = new ArrayList<>();
    private ArrayList<ShoppingProductModal.Result> get_result1 = new ArrayList<>();
    // private ArrayList<ShoppingProductModal.Review> get_result = new ArrayList<>();

    Fragment fragment;
    private String product_id;
    private String restaurant_id;
    private AfaryCode apiInterface;
    private ReviewAdapter adapter;
    private String productPrice;
    SliderAdapterExample adapter1;
    private ArrayList<String>banner_array_list;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.activity_shopping_product_detail2, container, false);
        apiInterface = ApiClient.getClient(getContext()).create(AfaryCode.class);
        banner_array_list = new ArrayList<>();

        product_id = getArguments().getString("product_id");
        restaurant_id = getArguments().getString("restaurant_id");
        productPrice = getArguments().getString("productPrice");

        binding.RRback.setOnClickListener(v -> {

            getActivity().onBackPressed();

        });

        binding.txtWright.setOnClickListener(v -> {
            startActivity(new Intent(getActivity(), WrightActivity.class)
                    .putExtra("product_id", product_id)
                    .putExtra("restaurant_id", restaurant_id));

        });


        binding.cardAdd.setOnClickListener(v -> {


            dialogContinue();


        });

    /*    binding.llicon.setOnClickListener(v -> {
            Toast.makeText(getContext(), "chalalalla=====", Toast.LENGTH_SHORT).show();

        });*/


       GetProductDetailsAPI(product_id, restaurant_id);

      //  setAdapter();

        return binding.getRoot();

    }

    private void Add_To_Cart_API(String product_id, String restaurant_id, String productPrice) {

        DataManager.getInstance().showProgressMessage(getActivity(), "Please wait...");

        Map<String,String> headerMap = new HashMap<>();
        headerMap.put("Authorization","Bearer " +PreferenceConnector.readString(getActivity(), PreferenceConnector.access_token,""));
        headerMap.put("Accept","application/json");

        Map<String, String> map = new HashMap<>();
        map.put("item_id", product_id);
        map.put("user_id", PreferenceConnector.readString(getActivity(), PreferenceConnector.User_id, ""));
        map.put("shop_id", restaurant_id);
        map.put("quantity", "1");
        map.put("amount", productPrice);
        map.put("cat_id",PreferenceConnector.readString(getActivity(), PreferenceConnector.Cat_id, ""));
        map.put("register_id", PreferenceConnector.readString(getActivity(), PreferenceConnector.Register_id, ""));

        Log.e("MapMap", "cart_params" + map);
        Call<ResponseBody> reviewModalCall = apiInterface.add_to_cart(headerMap,map);

        reviewModalCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                DataManager.getInstance().hideProgressMessage();

                try {
                  //  Add_To_Cart_Modal data = response.body();
                  //  String dataResponse = new Gson().toJson(response.body());
                 //   Log.e("", "LOGIN RESPONSE" + dataResponse);

                    String responseData = response.body() != null ? response.body().string() : "";
                    JSONObject object = new JSONObject(responseData);
                    Log.e(TAG, "AddToCart RESPONSE" + object);
                    if (object.getString("status").equals("1")) {

                        Toast.makeText(getActivity(), "Add Cart SuccessFully ",
                                Toast.LENGTH_SHORT).show();

                        fragment = new CardActivity();
                        loadFragment(fragment);


                        // finish();

                    } else if (object.getString("status").equals("0")) {
                        Toast.makeText(getActivity(), object.getString("message"), Toast.LENGTH_SHORT).show();
                    }

                    else if (object.getString("status").equals("5")) {
                        // Toast.makeText(getContext(), "No Data Found !!!!", Toast.LENGTH_SHORT).show();

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


    private void GetProductDetailsAPI(String product_id, String restaurant_id) {

        DataManager.getInstance().showProgressMessage(getActivity(), "Please wait...");
        Map<String,String> headerMap = new HashMap<>();
        headerMap.put("Authorization","Bearer " +PreferenceConnector.readString(getActivity(), PreferenceConnector.access_token,""));
        headerMap.put("Accept","application/json");

        Map<String, String> map = new HashMap<>();
        map.put("user_id", PreferenceConnector.readString(getContext(), PreferenceConnector.User_id, ""));
        map.put("restaurant_id", restaurant_id);
        map.put("pro_id", product_id);
        map.put("register_id", PreferenceConnector.readString(getActivity(), PreferenceConnector.Register_id, ""));
        map.put("country_id",PreferenceConnector.readString(getActivity(), PreferenceConnector.countryId, ""));

        Log.e("MapMap", "EXERSICE LIST" + map);

        Call<ShoppingProductModal> loginCall = apiInterface.get_product_detail(headerMap,map);

        loginCall.enqueue(new Callback<ShoppingProductModal>() {

            @Override
            public void onResponse(Call<ShoppingProductModal> call, Response<ShoppingProductModal> response) {
                DataManager.getInstance().hideProgressMessage();

                try {

                    ShoppingProductModal data = response.body();
                    String dataResponse = new Gson().toJson(response.body());
                    Log.e("MapMap", "Exersice_List" + dataResponse);

                    if (data.status.equals("1")) {

                        get_result1.clear();
                        get_result1.addAll(data.getResult());

                        if (!get_result1.get(0).image.equals("")) {
                            Picasso.get().load(get_result1.get(0).image).into(binding.imgShop);
                        }

                        banner_array_list.add(data.getResult().get(0).image);
                        banner_array_list.add(data.getResult().get(0).image1);

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


                        binding.tvTitle.setText(get_result1.get(0).productName);
                        binding.shopName.setText(get_result1.get(0).productName);
                        binding.productDetails.setText(get_result1.get(0).productDetails);
                        binding.productPrice.setText(get_result1.get(0).getLocalCurrency() + get_result1.get(0).getLocalPrice());
                        binding.productPrice1.setText(get_result1.get(0).getLocalCurrency() + get_result1.get(0).getLocalPrice());
                        if(get_result1.get(0).getDeliveryCharges().equals("0")) binding.switchDelivery.setChecked(false);
                         else  binding.switchDelivery.setChecked(true);
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
            public void onFailure(Call<ShoppingProductModal> call, Throwable t) {
                call.cancel();
                DataManager.getInstance().hideProgressMessage();
            }
        });
    }


/*
    private void setAdapter() {

        modelList.add(new HomeOfferModel("Reliance Fresh"));
        modelList.add(new HomeOfferModel("Big Basket"));
        modelList.add(new HomeOfferModel("Mall8Door"));

        mAdapter = new ReviewProductAdapter(getActivity(), modelList);
        binding.recyclerReview.setHasFixedSize(true);
        // use a linear layout manager
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        binding.recyclerReview.setLayoutManager(linearLayoutManager);
        binding.recyclerReview.setAdapter(mAdapter);

        mAdapter.SetOnItemClickListener(new ReviewProductAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position, HomeOfferModel model) {

            }
        });
    }
*/

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



    public void dialogContinue(){
        Dialog mDialog = new Dialog(getActivity());
        mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mDialog.setContentView(R.layout.dialog_continue);
        mDialog.setCancelable(false);
        mDialog.setCanceledOnTouchOutside(false);

        AppCompatButton btnContinue = mDialog.findViewById(R.id.btnContinue);
        AppCompatButton btnCheckout = mDialog.findViewById(R.id.btnCheckout);

        btnCheckout.setOnClickListener(v -> {
            mDialog.dismiss();

            Bundle bundle = new Bundle();
            bundle.putString("cat_id",PreferenceConnector.readString(getActivity(), PreferenceConnector.Cat_id, ""));
            fragment = new HomeShoppingOnlineScreen();
            fragment.setArguments(bundle);
            loadFragment(fragment);
        });

        btnContinue.setOnClickListener(v -> {
            mDialog.dismiss();
            Add_To_Cart_API(product_id, restaurant_id, productPrice);

        });

        mDialog.show();
    }


}