package com.my.afarycode.OnlineShopping;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.gson.Gson;
import com.my.afarycode.OnlineShopping.Model.Add_To_Cart_Modal;
import com.my.afarycode.OnlineShopping.Model.Add_to_Wish;
import com.my.afarycode.OnlineShopping.Model.CartModal;
import com.my.afarycode.OnlineShopping.Model.CountryModel;
import com.my.afarycode.OnlineShopping.Model.HomeOfferModel;
import com.my.afarycode.OnlineShopping.Model.ShoppingProductModal;
import com.my.afarycode.OnlineShopping.activity.CardAct;
import com.my.afarycode.OnlineShopping.activity.CheckOutDeliveryAct;
import com.my.afarycode.OnlineShopping.adapter.MainAttributeAdapter;
import com.my.afarycode.OnlineShopping.adapter.MainAttributeAdapter2;
import com.my.afarycode.OnlineShopping.adapter.ReviewAdapter;
import com.my.afarycode.OnlineShopping.adapter.ReviewProductAdapter;
import com.my.afarycode.OnlineShopping.adapter.SliderAdapterExample;
import com.my.afarycode.OnlineShopping.constant.PreferenceConnector;
import com.my.afarycode.OnlineShopping.helper.DataManager;
import com.my.afarycode.OnlineShopping.listener.MainClickListener;
import com.my.afarycode.R;
import com.my.afarycode.Splash;
import com.my.afarycode.databinding.ActivityShoppingProductDetailBinding;
import com.my.afarycode.ratrofit.AfaryCode;
import com.my.afarycode.ratrofit.ApiClient;
import com.smarteist.autoimageslider.IndicatorView.animation.type.IndicatorAnimationType;
import com.smarteist.autoimageslider.SliderAnimations;
import com.smarteist.autoimageslider.SliderView;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProductDetailAct extends AppCompatActivity implements MainClickListener {
    public String TAG = "ProductDetailAct";
    ReviewProductAdapter mAdapter;
    //  private ArrayList<HomeOfferModel> modelList = new ArrayList<>();
    private ArrayList<ShoppingProductModal.Result> get_result1 = new ArrayList<>();

    private ArrayList<ShoppingProductModal.Result.ValidateName> validateNameArrayList = new ArrayList<>();

    private ArrayList<String> productImgList = new ArrayList<>();
    private SliderAdapterExample adapter1;


    ActivityShoppingProductDetailBinding binding;
    private String product_id;
    private String restaurant_id;
    private AfaryCode apiInterface;
    private ReviewAdapter adapter;
    private String productPrice;
    Fragment fragment;
    JSONArray jsonArray = new JSONArray();
    boolean checkRead = false;
    String chkAtleast = "";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_shopping_product_detail);
        apiInterface = ApiClient.getClient(ProductDetailAct.this).create(AfaryCode.class);
        initViews();
    }

    private void initViews() {
        if (getIntent() != null) {
            product_id = getIntent().getStringExtra("product_id");
            restaurant_id = getIntent().getStringExtra("restaurant_id");
            productPrice = getIntent().getStringExtra("productPrice");
        }

        //  binding.tvShopTag.setText(Html.fromHtml("<font >" + "<b>" + "See other products from" + "<br>" +"this shop by clicking here"+ "</b>" + "</font>"));

        binding.RRback.setOnClickListener(v -> {
            onBackPressed();
        });

        GetProductDetailsAPI(product_id, restaurant_id);


        binding.txtWright.setOnClickListener(v -> {
            startActivity(new Intent(ProductDetailAct.this, WrightActivity.class)
                    .putExtra("product_id", product_id)
                    .putExtra("restaurant_id", restaurant_id));

        });


        binding.cardAdd.setOnClickListener(v -> {

            if (get_result1.get(0).getInStock().equalsIgnoreCase("Yes")) dialogContinue();
            else {
                //Toast.makeText(this, getString(R.string.out_of_stock), Toast.LENGTH_SHORT).show();
                Add_To_Cart_API(product_id, restaurant_id, productPrice, "checkOut");

            }

        });


        binding.tvShopTag.setOnClickListener(v -> {
            startActivity(new Intent(ProductDetailAct.this, ShopDetailsAct.class)
                    .putExtra("shopId", restaurant_id)
                    .putExtra("sellerId", get_result1.get(0).getSellerId()));
        });


        binding.rlCart.setOnClickListener(v -> {
            startActivity(new Intent(ProductDetailAct.this, CardAct.class));
        });

        binding.llicon.setOnClickListener(v -> {
            AddToWIshListAPI(product_id);
        });


        binding.tvSize.setOnClickListener(v -> {
            if (validateNameArrayList.size() == 1)
                showDropDownSize(v, binding.tvSize, validateNameArrayList.get(0).getAttributeName());
        });

        binding.tvColor.setOnClickListener(v -> {
            if (validateNameArrayList.size() == 2)
                showDropDownSize(v, binding.tvColor, validateNameArrayList.get(1).getAttributeName());

        });


        binding.btnReadMore.setOnClickListener(view -> {
          /*  if(checkRead==false){
                binding.productDetails.setMaxLines(Integer.MAX_VALUE);//your TextView
                checkRead = true;
            }else {
                binding.productDetails.setMaxLines(5);//your TextView
                checkRead = false;

            }*/

            toggleText();

        });


        binding.tvCheckAvailable.setOnClickListener(v -> {
            checkProductAvailability(product_id);
        });


    }


    private void toggleText() {
        if (checkRead) {
            // Collapse to one line
            binding.productDetails.setMaxLines(1);
            binding.btnReadMore.setText("READ MORE");
        } else {
            // Expand to five lines
            binding.productDetails.setMaxLines(5);
            binding.btnReadMore.setText("READ LESS");
        }
        checkRead = !checkRead; // Toggle the state
    }


    private void checkIfTextIsLongEnough() {
        binding.productDetails.post(new Runnable() {
            @Override
            public void run() {
                if (binding.productDetails.getLineCount() > 1) {
                    binding.btnReadMore.setVisibility(View.VISIBLE);
                } else {
                    binding.btnReadMore.setVisibility(View.GONE);
                }
            }
        });
    }
/*
    private void setAdapter() {

        modelList.add(new HomeOfferModel("Reliance Fresh"));
        modelList.add(new HomeOfferModel("Big Basket"));
        modelList.add(new HomeOfferModel("Mall8Door"));

        mAdapter = new ReviewProductAdapter(ProductDetailAct.this, modelList);
        binding.recyclerReview.setHasFixedSize(true);
        // use a linear layout manager
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(ProductDetailAct.this);
        binding.recyclerReview.setLayoutManager(linearLayoutManager);
        binding.recyclerReview.setAdapter(mAdapter);

    }
*/


    private void GetProductDetailsAPI(String product_id, String restaurant_id) {

        DataManager.getInstance().showProgressMessage(ProductDetailAct.this, getString(R.string.please_wait));
        Map<String, String> headerMap = new HashMap<>();
        headerMap.put("Authorization", "Bearer " + PreferenceConnector.readString(ProductDetailAct.this, PreferenceConnector.access_token, ""));
        headerMap.put("Accept", "application/json");

        Map<String, String> map = new HashMap<>();
        map.put("user_id", PreferenceConnector.readString(ProductDetailAct.this, PreferenceConnector.User_id, ""));
        map.put("restaurant_id", restaurant_id);
        map.put("pro_id", product_id);
        map.put("country_id",PreferenceConnector.readString(ProductDetailAct.this, PreferenceConnector.countryId, ""));
        map.put("register_id", PreferenceConnector.readString(ProductDetailAct.this, PreferenceConnector.Register_id, ""));

        Log.e("MapMap", "EXERSICE LIST" + map);

        Call<ShoppingProductModal> loginCall = apiInterface.get_product_detail(headerMap, map);

        loginCall.enqueue(new Callback<ShoppingProductModal>() {

            @Override
            public void onResponse(Call<ShoppingProductModal> call, Response<ShoppingProductModal> response) {
                DataManager.getInstance().hideProgressMessage();

                try {

                    ShoppingProductModal data = response.body();
                    String dataResponse = new Gson().toJson(response.body());
                    Log.e("MapMap", "Exersice_List" + dataResponse);
                    GetCartItem();
                    if (data.status.equals("1")) {

                        get_result1.clear();
                        validateNameArrayList.clear();
                        get_result1.addAll(data.getResult());
                        //  productImgList.add(get_result1.get(0).getImage());
                        //  productImgList.add(get_result1.get(0).getImage1());


                        if (!get_result1.get(0).getProductImages().equalsIgnoreCase("")) {
                            productImgList.add(get_result1.get(0).getProductImages());
                        }

                        if (!get_result1.get(0).getImage1().equalsIgnoreCase("")) {
                            productImgList.add(get_result1.get(0).getImage1());
                        }
                        if (!get_result1.get(0).getImage2().equalsIgnoreCase("")) {
                            productImgList.add(get_result1.get(0).getImage2());
                        }

                        if (!get_result1.get(0).getImage3().equalsIgnoreCase("")) {
                            productImgList.add(get_result1.get(0).getImage3());
                        }

                        validateNameArrayList.addAll(get_result1.get(0).getValidateName());
                        if (get_result1.get(0).getDeliveryCharges().equalsIgnoreCase("1"))
                            binding.ivDeliveryType.setVisibility(View.VISIBLE);
                        else binding.ivDeliveryType.setVisibility(View.GONE);

                    /*    if (get_result1.get(0).getDeliveryCharges().equalsIgnoreCase("1"))
                            binding.ivDeliveryType.setVisibility(View.GONE);
                        else binding.ivDeliveryType.setVisibility(View.VISIBLE);*/

                        if (validateNameArrayList.size() > 0) {
                            // binding.rvMain.setAdapter(new MainAttributeAdapter(ProductDetailAct.this, validateNameArrayList, ProductDetailAct.this));
                            binding.rvMain.setVisibility(View.VISIBLE);
                            binding.rvMain.setAdapter(new MainAttributeAdapter2(ProductDetailAct.this, validateNameArrayList, ProductDetailAct.this));
                        } else {
                            binding.rvMain.setVisibility(View.GONE);

                        }

                        if (!get_result1.get(0).getProductBrand().equalsIgnoreCase("")) {
                            binding.tvBrand.setVisibility(View.VISIBLE);
                            binding.tvBrand.setText(getString(R.string.brand) + " : " + get_result1.get(0).getProductBrand());

                        } else {
                            binding.tvBrand.setVisibility(View.GONE);

                        }

                        if (!get_result1.get(0).getNumberOfSold().equalsIgnoreCase("0") ) {
                            binding.tvSold.setVisibility(View.VISIBLE);
                            binding.tvSold.setText(get_result1.get(0).getNumberOfSold() + " " + getString(R.string.has_been_sold));

                        } else {
                            binding.tvSold.setVisibility(View.GONE);

                        }





                      /*  if(validateNameArrayList.size()==1){
                            binding.tvSize.setVisibility(View.VISIBLE);
                            binding.tvColor.setVisibility(View.GONE);
                            binding.tvSize.setText("Select "+validateNameArrayList.get(0).getName());

                        }

                        else if(validateNameArrayList.size()==2){
                            binding.tvSize.setVisibility(View.VISIBLE);
                            binding.tvColor.setVisibility(View.VISIBLE);
                            binding.tvSize.setText("Select "+validateNameArrayList.get(0).getName());
                            binding.tvColor.setText("Select "+validateNameArrayList.get(1).getName());

                        }*/

                      /*  if (!get_result1.get(0).image.equals("")) {
                            Picasso.get().load(get_result1.get(0).image).into(binding.imgShop);
                        }*/

                        if (get_result1.get(0).getReview().size() > 0) {
                            binding.tvReview.setVisibility(View.VISIBLE);
                            binding.recyclerReview.setVisibility(View.VISIBLE);
                            binding.recyclerReview.setAdapter(new ReviewProductAdapter(ProductDetailAct.this, (ArrayList<ShoppingProductModal.Result.review>) get_result1.get(0).getReview()));

                        } else {
                            binding.tvReview.setVisibility(View.GONE);
                            binding.recyclerReview.setVisibility(View.GONE);

                        }


                        binding.tvSellerName.setText(" " + get_result1.get(0).getSellerName().trim());
                        binding.tvShopTag.setText(Html.fromHtml("<font >" + "<b>" + "See other products from" + "<br>" + "this shop by clicking here " + "</b>" + "</font>" + "<font color='#0288D1'>" + "<b>" + get_result1.get(0).getShopName().trim() + "</b>" + "</font>"));


                        if (get_result1.get(0).getInStock().equalsIgnoreCase("Yes")) {
                            binding.llCheckProduct.setVisibility(View.GONE);
                            binding.rlStock.setVisibility(View.VISIBLE);
                            binding.tvStock.setVisibility(View.VISIBLE);
                            binding.tvStock.setText(getString(R.string.in_stock));
                            binding.tvStock.setTextColor(getResources().getColor(R.color.colorGreen));

                        } else {
                            // binding.tvStock.setText(getString(R.string.out_of_stock));
                            // binding.tvStock.setTextColor(getResources().getColor(R.color.red));
                            binding.llCheckProduct.setVisibility(View.VISIBLE);
                            binding.rlStock.setVisibility(View.GONE);

                        }
                        if (get_result1.get(0).getVerify().equalsIgnoreCase("Yes")) {
                            binding.ivVerify.setVisibility(View.VISIBLE);
                            binding.rlCertifySeller.setVisibility(View.VISIBLE);

                        } else {
                            binding.ivVerify.setVisibility(View.GONE);
                            binding.rlCertifySeller.setVisibility(View.GONE);
                        }
                        if (get_result1.get(0).getAddedtowishlist().equalsIgnoreCase("Yes")) {
                            binding.ivStar.setImageResource(R.drawable.ic_red_star);
                        } else binding.ivStar.setImageResource(R.drawable.outline);


                        adapter1 = new SliderAdapterExample(ProductDetailAct.this, productImgList);
                        binding.productImgSlider.setSliderAdapter(adapter1);
                        binding.productImgSlider.setIndicatorAnimation(IndicatorAnimationType.WORM); //set indicator animation by using SliderLayout.IndicatorAnimations. :WORM or THIN_WORM or COLOR or DROP or FILL or NONE or SCALE or SCALE_DOWN or SLIDE and SWAP!!
                        binding.productImgSlider.setSliderTransformAnimation(SliderAnimations.SIMPLETRANSFORMATION);
                        binding.productImgSlider.setAutoCycleDirection(SliderView.AUTO_CYCLE_DIRECTION_BACK_AND_FORTH);
                        //     binding.imageSlider.setIndicatorSelectedColor(R.color.colorPrimary);
                        //      binding.imageSlider.setIndicatorUnselectedColor(Color.GRAY);
                        binding.productImgSlider.setScrollTimeInSec(3);
                        binding.productImgSlider.setAutoCycle(true);
                        binding.productImgSlider.startAutoCycle();

                        binding.tvTitle.setText(get_result1.get(0).productName);
                        binding.shopName.setText(get_result1.get(0).productName.toUpperCase());
                        // binding.productDetails.setMaxLines(5);//your TextView


                        binding.productDetails.setText(get_result1.get(0).productDetails);
                        binding.productPrice.setText(get_result1.get(0).getShowCurrencyCode() + get_result1.get(0).getLocalPrice());
                        binding.productPrice1.setText(get_result1.get(0).getShowCurrencyCode() + get_result1.get(0).getLocalPrice());

                        binding.tvRate.setText(get_result1.get(0).getAvgRating() + " ");
                        binding.ratingbar.setRating(Float.parseFloat(get_result1.get(0).getAvgRating()));


                        if (get_result1.get(0).getDeliveryCharges().equalsIgnoreCase("1"))
                            binding.switchDelivery.setChecked(true);
                        else binding.switchDelivery.setChecked(false);
                    } else if (data.status.equals("0")) {
                        Toast.makeText(ProductDetailAct.this, data.message, Toast.LENGTH_SHORT).show();
                    } else if (data.status.equals("5")) {
                        PreferenceConnector.writeString(ProductDetailAct.this, PreferenceConnector.LoginStatus, "false");
                        startActivity(new Intent(ProductDetailAct.this, Splash.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP));
                        finish();

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

    public void dialogContinue() {
        Dialog mDialog = new Dialog(ProductDetailAct.this);
        mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mDialog.setContentView(R.layout.dialog_continue);
        mDialog.setCancelable(false);
        mDialog.setCanceledOnTouchOutside(false);

        AppCompatButton btnContinue = mDialog.findViewById(R.id.btnContinue);
        AppCompatButton btnCheckout = mDialog.findViewById(R.id.btnCheckout);

        btnCheckout.setOnClickListener(v -> {
            mDialog.dismiss();
            if (validateNameArrayList.size() == 0) {
                Add_To_Cart_API(product_id, restaurant_id, productPrice, "checkOut");
            } else {
                if (chkAtleast.equalsIgnoreCase(""))
                    Toast.makeText(ProductDetailAct.this, getString(R.string.please_select_atleast_one_attribute), Toast.LENGTH_SHORT).show();
                else Add_To_Cart_API(product_id, restaurant_id, productPrice, "checkOut");
            }
        });

        btnContinue.setOnClickListener(v -> {
            mDialog.dismiss();
            if (validateNameArrayList.size() == 0) {
                Add_To_Cart_API(product_id, restaurant_id, productPrice, "continue");
            } else {
                if (chkAtleast.equalsIgnoreCase(""))
                    Toast.makeText(ProductDetailAct.this, getString(R.string.please_select_atleast_one_attribute), Toast.LENGTH_SHORT).show();
                else Add_To_Cart_API(product_id, restaurant_id, productPrice, "continue");
                // finish();
            }
        });

        mDialog.show();
    }

    public boolean loadFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        if (fragment != null) {
            fragmentManager.beginTransaction()
                    .addToBackStack("Home")
                    .replace(R.id.fragment_Container, fragment)//, tag)
                    .commit();
            return true;
        }
        return false;
    }


    private void Add_To_Cart_API(String product_id, String restaurant_id, String productPrice, String chk) {

        DataManager.getInstance().showProgressMessage(ProductDetailAct.this, "Please wait...");

        Map<String, String> headerMap = new HashMap<>();
        headerMap.put("Authorization", "Bearer " + PreferenceConnector.readString(ProductDetailAct.this, PreferenceConnector.access_token, ""));
        headerMap.put("Accept", "application/json");

        Map<String, String> map = new HashMap<>();
        map.put("item_id", product_id);
        map.put("user_id", PreferenceConnector.readString(ProductDetailAct.this, PreferenceConnector.User_id, ""));
        map.put("shop_id", restaurant_id);
        map.put("quantity", "1");
        map.put("amount", productPrice);
        map.put("cat_id", PreferenceConnector.readString(ProductDetailAct.this, PreferenceConnector.Cat_id, ""));
        map.put("register_id", PreferenceConnector.readString(ProductDetailAct.this, PreferenceConnector.Register_id, ""));

        if (validateNameArrayList.size() > 0) {
            map.put("options", jsonArray.toString());
        } else map.put("options", "[]");

        Log.e("MapMap", "cart_params" + map);

        Call<ResponseBody> reviewModalCall = apiInterface.add_to_cart(headerMap, map);

        reviewModalCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                DataManager.getInstance().hideProgressMessage();

                try {
                    String responseData = response.body() != null ? response.body().string() : "";
                    JSONObject object = new JSONObject(responseData);
                    Log.e(TAG, "AddToCart RESPONSE" + object);
                    if (object.getString("status").equals("1")) {
                        Toast.makeText(ProductDetailAct.this, getString(R.string.add_to_cart_successfully), Toast.LENGTH_SHORT).show();
                        if (chk.equalsIgnoreCase("checkOut"))
                            startActivity(new Intent(ProductDetailAct.this, CheckOutDeliveryAct.class).putExtra("sellerId", restaurant_id));
                        else if (chk.equalsIgnoreCase("continue")) finish();
                        else startActivity(new Intent(ProductDetailAct.this, CardAct.class));
                    } else if (object.getString("status").equals("0")) {
                        Toast.makeText(ProductDetailAct.this, object.getString("message"), Toast.LENGTH_SHORT).show();
                    } else if (object.getString("status").equals("5")) {
                        PreferenceConnector.writeString(ProductDetailAct.this, PreferenceConnector.LoginStatus, "false");
                        startActivity(new Intent(ProductDetailAct.this, Splash.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP));
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

    private String getOption() {
        JSONArray jsonArray = new JSONArray();
        for (int i = 0; i < validateNameArrayList.size(); i++) {
            for (int j = 0; j < validateNameArrayList.get(i).getAttributeName().size(); j++) {
                if (validateNameArrayList.get(i).getAttributeName().get(j).isChk() == true) {
                    try {
                        JSONObject jsonObject = new JSONObject();
                        jsonObject.put("mainName", validateNameArrayList.get(i).getName());
                        jsonObject.put("innerName", validateNameArrayList.get(i).getAttributeName().get(j).getName());

                        //  jsonObject.put(validateNameArrayList.get(i).getName().toString(),validateNameArrayList.get(i).getAttributeName().get(j).getName().toString());
                        jsonArray.put(jsonObject);
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }
        Log.e("Added attribute value==", jsonArray + "");
        return jsonArray + "";
    }


    private void GetCartItem() {

        // DataManager.getInstance().showProgressMessage(getActivity(), "Please wait...");
        Map<String, String> headerMap = new HashMap<>();
        headerMap.put("Authorization", "Bearer " + PreferenceConnector.readString(ProductDetailAct.this, PreferenceConnector.access_token, ""));
        headerMap.put("Accept", "application/json");
        Map<String, String> map = new HashMap<>();
        map.put("user_id", PreferenceConnector.readString(ProductDetailAct.this, PreferenceConnector.User_id, ""));
        map.put("register_id", PreferenceConnector.readString(ProductDetailAct.this, PreferenceConnector.Register_id, ""));
        map.put("country_id",PreferenceConnector.readString(ProductDetailAct.this, PreferenceConnector.countryId, ""));

        Log.e("MapMap", "EXERSICE111 LIST" + map);

        Call<CartModal> loginCall = apiInterface.get_cart(headerMap, map);

        loginCall.enqueue(new Callback<CartModal>() {

            @Override
            public void onResponse(Call<CartModal> call, Response<CartModal> response) {
                DataManager.getInstance().hideProgressMessage();
                try {
                    CartModal data = response.body();
                    String dataResponse = new Gson().toJson(response.body());
                    Log.e("MapMap", "Exersice_List1234" + dataResponse);

                    if (data.status.equals("1")) {


                        if (data.getResult().size() > 0) {
                            binding.reqcountCart.setVisibility(View.VISIBLE);
                            binding.reqcountCart.setText(data.getResult().size() + "");
                        } else {
                            binding.reqcountCart.setVisibility(View.GONE);

                        }

                    } else if (data.status.equals("0")) {
                        // Toast.makeText(getContext(), "No Data Found !!!!", Toast.LENGTH_SHORT).show();
                        binding.reqcountCart.setVisibility(View.GONE);


                    } else if (data.status.equals("5")) {
                        PreferenceConnector.writeString(ProductDetailAct.this, PreferenceConnector.LoginStatus, "false");
                        startActivity(new Intent(ProductDetailAct.this, Splash.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP));
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


    private void AddToWIshListAPI(String productId) {

        DataManager.getInstance().showProgressMessage(ProductDetailAct.this, "Please wait...");
        Map<String, String> headerMap = new HashMap<>();
        headerMap.put("Authorization", "Bearer " + PreferenceConnector.readString(ProductDetailAct.this, PreferenceConnector.access_token, ""));
        headerMap.put("Accept", "application/json");

        Map<String, String> map = new HashMap<>();
        map.put("user_id", PreferenceConnector.readString(ProductDetailAct.this, PreferenceConnector.User_id, ""));
        map.put("product_id", productId);
        map.put("register_id", PreferenceConnector.readString(ProductDetailAct.this, PreferenceConnector.Register_id, ""));

        Log.e("MapMap", "Add to WishList LIST" + map);

        Call<ResponseBody> loginCall = apiInterface.addToFavApi(headerMap, map);

        loginCall.enqueue(new Callback<ResponseBody>() {

            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                DataManager.getInstance().hideProgressMessage();

                try {
                    Log.e("response===", response.body().toString());
                    String stringResponse = response.body().string();
                    JSONObject jsonObject = new JSONObject(stringResponse);
                    GetProductDetailsAPI(product_id, restaurant_id);

                    if (jsonObject.getString("status").toString().equals("1")) {
                        Toast.makeText(ProductDetailAct.this, " Add Wish List ", Toast.LENGTH_SHORT).show();
                    } else if (jsonObject.getString("status").toString().equals("0")) {

                        Toast.makeText(ProductDetailAct.this, jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                    } else if (jsonObject.getString("status").equals("5")) {
                        PreferenceConnector.writeString(ProductDetailAct.this, PreferenceConnector.LoginStatus, "false");
                        startActivity(new Intent(ProductDetailAct.this, Splash.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP));
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


    private void checkProductAvailability(String productId) {

        DataManager.getInstance().showProgressMessage(ProductDetailAct.this, "Please wait...");
        Map<String, String> headerMap = new HashMap<>();
        headerMap.put("Authorization", "Bearer " + PreferenceConnector.readString(ProductDetailAct.this, PreferenceConnector.access_token, ""));
        headerMap.put("Accept", "application/json");

        Map<String, String> map = new HashMap<>();
        map.put("user_id", PreferenceConnector.readString(ProductDetailAct.this, PreferenceConnector.User_id, ""));
        map.put("product_id", productId);
        map.put("register_id", PreferenceConnector.readString(ProductDetailAct.this, PreferenceConnector.Register_id, ""));

        Log.e("MapMap", "Check Product availability LIST" + map);

        Call<ResponseBody> loginCall = apiInterface.checkProductAvailabilityApi(headerMap, map);

        loginCall.enqueue(new Callback<ResponseBody>() {

            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                DataManager.getInstance().hideProgressMessage();

                try {
                    Log.e("response===", response.body().toString());
                    String stringResponse = response.body().string();
                    JSONObject jsonObject = new JSONObject(stringResponse);
                    GetProductDetailsAPI(product_id, restaurant_id);

                    if (jsonObject.getString("status").toString().equals("1")) {
                        //    Toast.makeText(ProductDetailAct.this, " Add Wish List ", Toast.LENGTH_SHORT).show();
                    } else if (jsonObject.getString("status").toString().equals("0")) {

                        //  Toast.makeText(ProductDetailAct.this, jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                    } else if (jsonObject.getString("status").equals("5")) {
                        PreferenceConnector.writeString(ProductDetailAct.this, PreferenceConnector.LoginStatus, "false");
                        startActivity(new Intent(ProductDetailAct.this, Splash.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP));
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


    private void showDropDownSize(View v, TextView textView, List<ShoppingProductModal.Result.ValidateName.AttributeName> stringList) {
        PopupMenu popupMenu = new PopupMenu(ProductDetailAct.this, v);
        for (int i = 0; i < stringList.size(); i++) {
            popupMenu.getMenu().add(stringList.get(i).getName());
        }
        popupMenu.setOnMenuItemClickListener(menuItem -> {
            textView.setText(menuItem.getTitle());
            for (int i = 0; i < stringList.size(); i++) {
                if (stringList.get(i).getName().equalsIgnoreCase(menuItem.getTitle().toString())) {
                    //  countryId = stringList.get(i).getId();

                }
            }
            return true;
        });
        popupMenu.show();
    }


    @Override
    public void mainClick(String mainVal, String innerVal, int mainPosition, int innerPosition) {
/*
        for (int i = 0; i < validateNameArrayList.size(); i++) {
            if (i == mainPosition) {
                for (int j = 0; j < validateNameArrayList.get(i).getAttributeName().size(); j++) {
                    if (j == innerPosition) {
                        for (int k = 0; k < validateNameArrayList.get(i).getAttributeName().size(); k++) {
                            validateNameArrayList.get(i).getAttributeName().get(k).setChk(false);

                        }
                        validateNameArrayList.get(i).getAttributeName().get(j).setChk(true);
                        validateNameArrayList.get(i).setChecked(true);
                    }
                }
            }



        }
*/


        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("mainName", mainVal);
            jsonObject.put("innerName", innerVal);

            //  jsonObject.put(validateNameArrayList.get(i).getName().toString(),validateNameArrayList.get(i).getAttributeName().get(j).getName().toString());
            jsonArray.put(jsonObject);
            chkAtleast = jsonArray.toString();
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
        Log.e("Added attribute value==", jsonArray + "");
    }
}
