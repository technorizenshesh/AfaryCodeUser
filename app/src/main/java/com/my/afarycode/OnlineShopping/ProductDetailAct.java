package com.my.afarycode.OnlineShopping;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
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
import com.my.afarycode.OnlineShopping.adapter.ReviewAdapter;
import com.my.afarycode.OnlineShopping.adapter.ReviewProductAdapter;
import com.my.afarycode.OnlineShopping.adapter.SliderAdapterExample;
import com.my.afarycode.OnlineShopping.constant.PreferenceConnector;
import com.my.afarycode.OnlineShopping.helper.DataManager;
import com.my.afarycode.R;
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
import java.util.List;
import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProductDetailAct extends AppCompatActivity {
    public String TAG ="ProductDetailAct";
    ReviewProductAdapter mAdapter;
    private ArrayList<HomeOfferModel> modelList = new ArrayList<>();
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

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_shopping_product_detail);
        apiInterface = ApiClient.getClient(ProductDetailAct.this).create(AfaryCode.class);
        initViews();
    }

    private void initViews() {
        if(getIntent()!=null){
            product_id = getIntent().getStringExtra("product_id");
            restaurant_id = getIntent().getStringExtra("restaurant_id");
            productPrice = getIntent().getStringExtra("productPrice");
        }

        binding.RRback.setOnClickListener(v -> {
            onBackPressed();});

        GetProductDetailsAPI(product_id, restaurant_id);


        binding.txtWright.setOnClickListener(v -> {
            startActivity(new Intent(ProductDetailAct.this, WrightActivity.class)
                    .putExtra("product_id", product_id)
                    .putExtra("restaurant_id", restaurant_id));

        });


        binding.cardAdd.setOnClickListener(v -> {

           if (get_result1.get(0).getProductStock().equalsIgnoreCase("In Stock"))dialogContinue();
           else Toast.makeText(this, getString(R.string.out_of_stock), Toast.LENGTH_SHORT).show();

        });


        binding.tvShopName.setOnClickListener(v -> {startActivity(new Intent(ProductDetailAct.this,ShopDetailsAct.class)
                .putExtra("shopId",restaurant_id)
                .putExtra("sellerId",get_result1.get(0).getSellerId()));});



        binding.rlCart.setOnClickListener(v -> {
            startActivity(new Intent(ProductDetailAct.this, CardAct.class));
        });

        binding.llicon.setOnClickListener(v -> {
            AddToWIshListAPI(product_id);
        });


        binding.tvSize.setOnClickListener(v -> {
           if(validateNameArrayList.size()==1) showDropDownSize(v,binding.tvSize,validateNameArrayList.get(0).getAttributeName());
        });

        binding.tvColor.setOnClickListener(v -> {
            if(validateNameArrayList.size()==2) showDropDownSize(v,binding.tvColor,validateNameArrayList.get(1).getAttributeName());

        });

        setAdapter();
    }


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

        mAdapter.SetOnItemClickListener(new ReviewProductAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position, HomeOfferModel model) {

            }
        });
    }



    private void GetProductDetailsAPI(String product_id, String restaurant_id) {

        DataManager.getInstance().showProgressMessage(ProductDetailAct.this, "Please wait...");
        Map<String,String> headerMap = new HashMap<>();
        headerMap.put("Authorization","Bearer " +PreferenceConnector.readString(ProductDetailAct.this, PreferenceConnector.access_token,""));
        headerMap.put("Accept","application/json");

        Map<String, String> map = new HashMap<>();
        map.put("user_id", PreferenceConnector.readString(ProductDetailAct.this, PreferenceConnector.User_id, ""));
        map.put("restaurant_id", restaurant_id);
        map.put("pro_id", product_id);

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
                    GetCartItem();
                    if (data.status.equals("1")) {

                        get_result1.clear();
                        validateNameArrayList.clear();
                        get_result1.addAll(data.getResult());
                        productImgList.add(get_result1.get(0).getImage());
                        productImgList.add(get_result1.get(0).getImage1());
                        validateNameArrayList.addAll(get_result1.get(0).getValidateName());
                        if(get_result1.get(0).getDeliveryCharges().equalsIgnoreCase("0"))
                            binding.ivDeliveryType.setVisibility(View.VISIBLE);
                        else binding.ivDeliveryType.setVisibility(View.GONE);
                        if(validateNameArrayList.size()==1){
                            binding.tvSize.setVisibility(View.VISIBLE);
                            binding.tvColor.setVisibility(View.GONE);
                            binding.tvSize.setText("Select "+validateNameArrayList.get(0).getName());

                        }

                        else if(validateNameArrayList.size()==2){
                            binding.tvSize.setVisibility(View.VISIBLE);
                            binding.tvColor.setVisibility(View.VISIBLE);
                            binding.tvSize.setText("Select "+validateNameArrayList.get(0).getName());
                            binding.tvColor.setText("Select "+validateNameArrayList.get(1).getName());

                        }

                      /*  if (!get_result1.get(0).image.equals("")) {
                            Picasso.get().load(get_result1.get(0).image).into(binding.imgShop);
                        }*/

                        binding.tvSellerName.setText(" "+get_result1.get(0).getSellerName().trim());
                        binding.tvShopName.setText(get_result1.get(0).getShopName().trim());

                        if (get_result1.get(0).getProductStock().equalsIgnoreCase("In Stock")){
                            binding.tvStock.setText(getString(R.string.in_stock));
                            binding.tvStock.setTextColor(getResources().getColor(R.color.colorGreen));

                        }else {
                                    binding.tvStock.setText(getString(R.string.out_of_stock));
                            binding.tvStock.setTextColor(getResources().getColor(R.color.red));
                        }
                        if(get_result1.get(0).getVerify().equalsIgnoreCase("Yes"))  binding.ivVerify.setVisibility(View.VISIBLE);
                         else  binding.ivVerify.setVisibility(View.GONE);
                        if(get_result1.get(0).getAddedtowishlist().equalsIgnoreCase("Yes")){
                            binding.ivStar.setImageResource(R.drawable.ic_red_star);
                        }else binding.ivStar.setImageResource(R.drawable.outline);


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
                        binding.productDetails.setText(get_result1.get(0).productDetails);
                        binding.productPrice.setText("Rs. " + get_result1.get(0).productPrice);
                        binding.productPrice1.setText("Rs. " + get_result1.get(0).productPrice);

                        if(get_result1.get(0).getDeliveryCharges().equalsIgnoreCase("1")) binding.switchDelivery.setChecked(true);
                          else binding.switchDelivery.setChecked(false);
                    } else if (data.status.equals("0")) {
                        Toast.makeText(ProductDetailAct.this, data.message, Toast.LENGTH_SHORT).show();
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

    public void dialogContinue(){
        Dialog mDialog = new Dialog(ProductDetailAct.this);
        mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mDialog.setContentView(R.layout.dialog_continue);
        mDialog.setCancelable(false);
        mDialog.setCanceledOnTouchOutside(false);

        AppCompatButton btnContinue = mDialog.findViewById(R.id.btnContinue);
        AppCompatButton btnCheckout = mDialog.findViewById(R.id.btnCheckout);

        btnCheckout.setOnClickListener(v -> {
            mDialog.dismiss();
            Add_To_Cart_API(product_id, restaurant_id, productPrice,"checkOut");

        });

        btnContinue.setOnClickListener(v -> {
            mDialog.dismiss();
          //  Add_To_Cart_API(product_id, restaurant_id, productPrice,"continue");
             finish();

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


    private void Add_To_Cart_API(String product_id, String restaurant_id, String productPrice,String chk) {

        DataManager.getInstance().showProgressMessage(ProductDetailAct.this, "Please wait...");

        Map<String,String> headerMap = new HashMap<>();
        headerMap.put("Authorization","Bearer " +PreferenceConnector.readString(ProductDetailAct.this, PreferenceConnector.access_token,""));
        headerMap.put("Accept","application/json");

        Map<String, String> map = new HashMap<>();
        map.put("item_id", product_id);
        map.put("user_id", PreferenceConnector.readString(ProductDetailAct.this, PreferenceConnector.User_id, ""));
        map.put("shop_id", restaurant_id);
        map.put("quantity", "1");
        map.put("amount", productPrice);
        map.put("cat_id",PreferenceConnector.readString(ProductDetailAct.this, PreferenceConnector.Cat_id, ""));


        Log.e("MapMap", "cart_params" + map);

        Call<ResponseBody> reviewModalCall = apiInterface.add_to_cart(headerMap,map);

        reviewModalCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                DataManager.getInstance().hideProgressMessage();

                try {
                    String responseData = response.body() != null ? response.body().string() : "";
                    JSONObject object = new JSONObject(responseData);
                    Log.e(TAG, "AddToCart RESPONSE" + object);
                    if (object.getString("status").equals("1")) {
                        Toast.makeText(ProductDetailAct.this, "Add Cart SuccessFully ",Toast.LENGTH_SHORT).show();
                        if(chk.equalsIgnoreCase("checkOut")) startActivity(new Intent(ProductDetailAct.this, CheckOutDeliveryAct.class));
                        else startActivity(new Intent(ProductDetailAct.this, CardAct.class));
                    }
                    else if (object.getString("status").equals("0")) {
                        Toast.makeText(ProductDetailAct.this, object.getString("message"), Toast.LENGTH_SHORT).show();
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


    private void GetCartItem() {

        // DataManager.getInstance().showProgressMessage(getActivity(), "Please wait...");
        Map<String,String> headerMap = new HashMap<>();
        headerMap.put("Authorization","Bearer " +PreferenceConnector.readString(ProductDetailAct.this, PreferenceConnector.access_token,""));
        headerMap.put("Accept","application/json");
        Map<String, String> map = new HashMap<>();
        map.put("user_id", PreferenceConnector.readString(ProductDetailAct.this, PreferenceConnector.User_id, ""));
        Log.e("MapMap", "EXERSICE111 LIST" + map);

        Call<CartModal> loginCall = apiInterface.get_cart(headerMap,map);

        loginCall.enqueue(new Callback<CartModal>() {

            @Override
            public void onResponse(Call<CartModal> call, Response<CartModal> response) {
                DataManager.getInstance().hideProgressMessage();
                try {
                    CartModal data = response.body();
                    String dataResponse = new Gson().toJson(response.body());
                    Log.e("MapMap", "Exersice_List1234" + dataResponse);

                    if (data.status.equals("1")) {


                        if(data.getResult().size()>0){
                            binding.reqcountCart.setVisibility(View.VISIBLE);
                            binding.reqcountCart.setText(data.getResult().size()+"");
                        }else {
                            binding.reqcountCart.setVisibility(View.GONE);

                        }

                    } else if (data.status.equals("0")) {
                        // Toast.makeText(getContext(), "No Data Found !!!!", Toast.LENGTH_SHORT).show();
                        binding.reqcountCart.setVisibility(View.GONE);


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
        Map<String,String> headerMap = new HashMap<>();
        headerMap.put("Authorization","Bearer " +PreferenceConnector.readString(ProductDetailAct.this, PreferenceConnector.access_token,""));
        headerMap.put("Accept","application/json");

        Map<String, String> map = new HashMap<>();
        map.put("user_id", PreferenceConnector.readString(ProductDetailAct.this, PreferenceConnector.User_id, ""));
        map.put("product_id", productId);
        Log.e("MapMap", "Add to WishList LIST" + map);

        Call<ResponseBody> loginCall = apiInterface.addToFavApi(headerMap,map);

        loginCall.enqueue(new Callback<ResponseBody>() {

            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                DataManager.getInstance().hideProgressMessage();

                try {
                    Log.e("response===",response.body().toString());
                    String stringResponse = response.body().string();
                    JSONObject jsonObject = new JSONObject(stringResponse);
                    GetProductDetailsAPI(product_id, restaurant_id);

                    if (jsonObject.getString("status").toString().equals("1")) {
                        Toast.makeText(ProductDetailAct.this, " Add Wish List ", Toast.LENGTH_SHORT).show();
                    }   else if (jsonObject.getString("status").toString().equals("0")) {

                        Toast.makeText(ProductDetailAct.this, jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
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
                if(stringList.get(i).getName().equalsIgnoreCase(menuItem.getTitle().toString())) {
                  //  countryId = stringList.get(i).getId();

                }
            }
            return true;
        });
        popupMenu.show();
    }


}
