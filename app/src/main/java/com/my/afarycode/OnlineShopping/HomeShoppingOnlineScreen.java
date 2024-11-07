package com.my.afarycode.OnlineShopping;

import static android.app.Activity.RESULT_OK;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.core.view.GravityCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.android.gms.common.api.Status;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.gson.Gson;
import com.my.afarycode.OnlineShopping.Model.CartModal;
import com.my.afarycode.OnlineShopping.Model.GetProfileModal;
import com.my.afarycode.OnlineShopping.Model.GetRestorentsModal;
import com.my.afarycode.OnlineShopping.Model.GetRestorentsModalCopy;
import com.my.afarycode.OnlineShopping.Model.GetShopingCategoryModal;
import com.my.afarycode.OnlineShopping.Model.HomeOfferModel;
import com.my.afarycode.OnlineShopping.Model.ProductItemModel;
import com.my.afarycode.OnlineShopping.Model.ShopModel;
import com.my.afarycode.OnlineShopping.Model.UpdateProfileModal;
import com.my.afarycode.OnlineShopping.activity.CardAct;
import com.my.afarycode.OnlineShopping.activity.CheckOutDeliveryAct;
import com.my.afarycode.OnlineShopping.adapter.HomeShoppingNearsetRestorents;
import com.my.afarycode.OnlineShopping.adapter.ProductAdapter2;
import com.my.afarycode.OnlineShopping.adapter.SubCategoryAdapter;
import com.my.afarycode.OnlineShopping.constant.PreferenceConnector;
import com.my.afarycode.OnlineShopping.deliveryaddress.DeliveryAddress;
import com.my.afarycode.OnlineShopping.fragment.MyAddressFragment;
import com.my.afarycode.OnlineShopping.fragment.MyProfileFragment;
import com.my.afarycode.OnlineShopping.helper.DataManager;
import com.my.afarycode.OnlineShopping.listener.onItemClickListener;
import com.my.afarycode.OnlineShopping.myorder.MyOrderScreen;
import com.my.afarycode.OnlineShopping.servercommunication.GPSTracker;
import com.my.afarycode.R;
import com.my.afarycode.Splash;
import com.my.afarycode.databinding.ActivityHomeShoppingNavBinding;
import com.my.afarycode.ratrofit.AfaryCode;
import com.my.afarycode.ratrofit.ApiClient;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeShoppingOnlineScreen extends Fragment implements onItemClickListener {
    public String TAG = "HomeShoppingOnlineScreen";
    ActivityHomeShoppingNavBinding binding;
    private ArrayList<HomeOfferModel> modelList = new ArrayList<>();
    private ArrayList<GetShopingCategoryModal.Result> get_result = new ArrayList<>();
    private List<ShopModel.Result> get_result1 = new ArrayList<>();
    private List<ShopModel.Result> allShops = new ArrayList<>();

    private static final String ALPHABET = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";

    private ArrayList<ProductItemModel.Result> arrayList = new ArrayList<>();

    private static final int MY_PERMISSION_CONSTANT = 5;

    Fragment fragment;
    private AfaryCode apiInterface;
    private SubCategoryAdapter adapter;
    private HomeShoppingNearsetRestorents adapter1;
    private GPSTracker gpsTracker;
    private double lat;
    private double lon;
    private String cat_id, categoryId = "";

    ProductAdapter2 adapterSearch;

    private String str_image_path = "";
    private static final int REQUEST_CAMERA = 1;
    private static final int SELECT_FILE = 2;
    private static final int MY_PERMISSION_CONSTANT2 = 5;
    GetProfileModal data;
    Bitmap oneBitmap = null;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = DataBindingUtil.inflate(inflater, R.layout.activity_home_shopping_nav, container, false);
        Bundle bundle = this.getArguments();

        if (bundle != null) {
            cat_id = bundle.getString("cat_id");
        }


        if (checkPermisssionForReadStorage()) {

            gpsTracker = new GPSTracker(getActivity());
            lat = gpsTracker.getLatitude();
            lon = gpsTracker.getLongitude();

            Geocoder geocoder = new Geocoder(getContext(), Locale.getDefault());
            try {
                List<Address> addresses = geocoder.getFromLocation(lat, lon, 1);
                Log.e("addresses>>>", "" + addresses.get(0).getAddressLine(0));

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        apiInterface = ApiClient.getClient(getContext()).create(AfaryCode.class);

        GetCategoryAPi();
        initViews();
        // GetProfileAPI();
        //GetDemo();



        return binding.getRoot();
    }

    private void initViews() {
        binding.dashboard.RRMenu.setOnClickListener(v -> {
            navmenu();
        });


        binding.dashboard.imgCard.setOnClickListener(v -> {

            //  fragment = new CardActivity();
            //    loadFragment(fragment);

            startActivity(new Intent(getActivity(), CardAct.class));

        });

        binding.childNavDrawer.RRDelivryAddress.setOnClickListener(v -> {
            startActivity(new Intent(getActivity(), DeliveryAddress.class));
        });

        binding.dashboard.imgSearch.setOnClickListener(v -> {
            binding.dashboard.searchEtHome.setVisibility(View.VISIBLE);
        });

        binding.dashboard.tvViewAll.setOnClickListener(v -> {
         //   adapter1 = new HomeShoppingNearsetRestorents(getActivity(), get_result1, get_result1.size());
         //   binding.dashboard.recyclerShop.setLayoutManager(new GridLayoutManager(getActivity(), 2));
         //   binding.dashboard.recyclerShop.setAdapter(adapter1);
          /* if(allShops.size()>4) {
               get_result1.clear();
               get_result1 = allShops;
               adapter1 = new HomeShoppingNearsetRestorents(getActivity(), allShops, allShops.size());
               binding.dashboard.recyclerShop.setLayoutManager(new GridLayoutManager(getActivity(), 2));
               binding.dashboard.recyclerShop.setAdapter(adapter1);
               binding.dashboard.tvViewAll.setVisibility(View.GONE);
           }*/
            GetNearestRestorentsAPI("view all");

        });

        binding.dashboard.tvViewAllProduct.setOnClickListener(v -> {
            adapterSearch = new ProductAdapter2(getActivity(), arrayList, arrayList.size());
            binding.dashboard.rvProduct.setLayoutManager(new GridLayoutManager(getActivity(), 2));
            binding.dashboard.rvProduct.setAdapter(adapterSearch);
        });


        binding.dashboard.searchEtHome.setOnClickListener(v -> {
          //  startActivity(new Intent(getActivity(), SearchAct.class));
        });

        binding.dashboard.searchEtHome.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                try {

                    adapter1.filter(s.toString());

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


        binding.childNavDrawer.imgUser.setOnClickListener(v -> {
            //   fragment = new UpdateProfile();
            //   loadFragment(fragment);
         /*   if (checkPermissionForReadStorage11()) {
                showImageSelection();
            }*/


            if (Build.VERSION.SDK_INT >= 33) {
                if (checkPermissionFor12Above()) showImageSelection();
            } else {
                if (checkPermissionForReadStorage11()) showImageSelection();
            }

        });


        binding.childNavDrawer.RROrderHistory.setOnClickListener(v -> {
            startActivity(new Intent(getActivity(), OrderHistoryScreen.class));
        });


        binding.childNavDrawer.RRMyOrder.setOnClickListener(v -> {
            startActivity(new Intent(getActivity(), MyOrderScreen.class));

        });

        binding.childNavDrawer.RRwishList.setOnClickListener(v -> {
            fragment = new WishListActivity();
            loadFragment(fragment);
        });

        adapterSearch = new ProductAdapter2(getActivity(), arrayList, 4);
        binding.dashboard.rvProduct.setLayoutManager(new GridLayoutManager(getActivity(), 2));
        binding.dashboard.rvProduct.setAdapter(adapterSearch);


       // adapter1 = new HomeShoppingNearsetRestorents(getActivity(), get_result1, 4);
     //   adapter1 = new HomeShoppingNearsetRestorents(getActivity(), get_result1.subList(0,Math.min(4,get_result1.size())), get_result1.size());
     //   binding.dashboard.recyclerShop.setLayoutManager(new GridLayoutManager(getActivity(), 2));
     //   binding.dashboard.recyclerShop.setAdapter(adapter1);
    }

    @Override
    public void onResume() {
        GetCategoryAPi();
        GetProfileAPI();
        GetCartItem();
        super.onResume();
    }

    private boolean checkPermisssionForReadStorage() {

        if (ContextCompat.checkSelfPermission(getContext(),
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(getContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(getContext(),
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                    Manifest.permission.ACCESS_FINE_LOCATION) ||
                    ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                            Manifest.permission.ACCESS_COARSE_LOCATION)) {

                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION
                                , Manifest.permission.ACCESS_COARSE_LOCATION},
                        MY_PERMISSION_CONSTANT);

                gpsTracker = new GPSTracker(getActivity());
                lat = gpsTracker.getLatitude();
                lon = gpsTracker.getLongitude();
                Log.e("latitute", "" + lat);
                Log.e("longitute", "" + lon);


            } else {
                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION
                                , Manifest.permission.ACCESS_COARSE_LOCATION},
                        MY_PERMISSION_CONSTANT);
            }

            return false;

        } else {
            return true;
        }
    }

    private void GetProfileAPI() {

        DataManager.getInstance().showProgressMessage(getActivity(), getString(R.string.please_wait));
        Map<String, String> map = new HashMap<>();
        map.put("user_id", PreferenceConnector.readString(getActivity(), PreferenceConnector.User_id, ""));
        map.put("register_id", PreferenceConnector.readString(getActivity(), PreferenceConnector.Register_id, ""));

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

                        binding.childNavDrawer.txtUserName.setText(data.getResult().getUserName());
                        binding.childNavDrawer.tvEmail.setText(data.getResult().getEmail());
                        binding.childNavDrawer.tvMobile.setText(data.getResult().getMobile());

                        Log.e("image>>>", data.getResult().image);

                        Picasso.get().load(data.getResult().image).placeholder(R.drawable.user_default).error(R.drawable.user_default).into(binding.childNavDrawer.imgUser);


                    } else if (data.status.equals("0")) {
                        Toast.makeText(getActivity(), data.message /*getString(R.string.wrong_username_password)*/, Toast.LENGTH_SHORT).show();
                    } else if (data.status.equals("5")) {
                        PreferenceConnector.writeString(getActivity(), PreferenceConnector.LoginStatus, "false");
                        startActivity(new Intent(getActivity(), Splash.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP));
                        getActivity().finish();
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

    private void GetNearestRestorentsAPI(String cat_id) {

        DataManager.getInstance().showProgressMessage(getActivity(), getString(R.string.please_wait));

        Map<String, String> headerMap = new HashMap<>();
        headerMap.put("Authorization", "Bearer " + PreferenceConnector.readString(getActivity(), PreferenceConnector.access_token, ""));
        headerMap.put("Accept", "application/json");

        Map<String, String> map = new HashMap<>();
        map.put("user_id", PreferenceConnector.readString(getContext(), PreferenceConnector.User_id, ""));
        map.put("latitute",  lat+"");
        map.put("longitute",  lon+"");
        map.put("category_id", "" + cat_id);
        map.put("register_id", PreferenceConnector.readString(getActivity(), PreferenceConnector.Register_id, ""));
        map.put("country_id", PreferenceConnector.readString(getActivity(), PreferenceConnector.countryId, ""));

        Log.e("MapMap", "EXERSICE LIST" + map);
        Call<ResponseBody> loginCall = apiInterface.get_restaurant(headerMap, map);
        loginCall.enqueue(new Callback<ResponseBody>() {

            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                DataManager.getInstance().hideProgressMessage();

                try {
                    String stringResponse = response.body().string();
                    JSONObject jsonObject = new JSONObject(stringResponse);
                    Log.e("MapMap", "near_List" + stringResponse);
                    if(cat_id.isEmpty()) {
                        getProduct(PreferenceConnector.readString(getActivity(), PreferenceConnector.countryId, ""), cat_id);
                    }
                    else if(cat_id.contentEquals("view all")){
                    }
                    else {
                        getProduct(PreferenceConnector.readString(getActivity(), PreferenceConnector.countryId, ""), cat_id);
                    }

                    if (jsonObject.getString("status").equals("1")) {
                        ShopModel mainCateModel = new Gson().fromJson(stringResponse, ShopModel.class);
                        binding.dashboard.rlShops.setVisibility(View.VISIBLE);
                        allShops = new ArrayList<>();
                        get_result1.clear();

                        get_result1.addAll(mainCateModel.getResult());
                        allShops.addAll(mainCateModel.getResult());

                        if(cat_id.isEmpty()) {
                            adapter1 = new HomeShoppingNearsetRestorents(getActivity(), get_result1.subList(0, Math.min(4, get_result1.size())), get_result1.size());
                            binding.dashboard.recyclerShop.setLayoutManager(new GridLayoutManager(getActivity(), 2));
                            binding.dashboard.recyclerShop.setAdapter(adapter1);
                        }
                        else if(cat_id.equals("view all")){
                            Log.e("chala=====","view all==="+ allShops.size());
                            adapter1 = new HomeShoppingNearsetRestorents(getActivity(), allShops, allShops.size());
                            binding.dashboard.recyclerShop.setLayoutManager(new GridLayoutManager(getActivity(), 2));
                            binding.dashboard.recyclerShop.setAdapter(adapter1);
                        }
                        else {
                                adapter1 = new HomeShoppingNearsetRestorents(getActivity(), get_result1.subList(0, Math.min(4, get_result1.size())), get_result1.size());
                                binding.dashboard.recyclerShop.setLayoutManager(new GridLayoutManager(getActivity(), 2));
                                binding.dashboard.recyclerShop.setAdapter(adapter1);

                        }
                     //   adapter1.notifyDataSetChanged();
                       /* if (get_result1.size() > 4)
                            binding.dashboard.tvViewAll.setVisibility(View.VISIBLE);
                        else binding.dashboard.tvViewAll.setVisibility(View.GONE);*/

                    } else if (jsonObject.getString("status").equals("0")) {
                        binding.dashboard.rlShops.setVisibility(View.GONE);
                        get_result1.clear();
                        adapter1.notifyDataSetChanged();
                        Toast.makeText(getContext(), jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                    } else if (jsonObject.optString("status").equals("5")) {
                        PreferenceConnector.writeString(getActivity(), PreferenceConnector.LoginStatus, "false");
                        startActivity(new Intent(getActivity(), Splash.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP));
                        getActivity().finish();
                    }


                } catch (Exception e) {
                    e.printStackTrace();
                   // Log.e("error in near shop====",e,);
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                call.cancel();
                DataManager.getInstance().hideProgressMessage();
            }
        });
    }

    private void GetCategoryAPi() {

        DataManager.getInstance().showProgressMessage(getActivity(), getString(R.string.please_wait));
        Map<String, String> headerMap = new HashMap<>();
        headerMap.put("Authorization", "Bearer " + PreferenceConnector.readString(getActivity(), PreferenceConnector.access_token, ""));
        headerMap.put("Accept", "application/json");
        Map<String, String> map = new HashMap<>();
        map.put("user_id", PreferenceConnector.readString(getContext(), PreferenceConnector.User_id, ""));
        map.put("cat_id", cat_id);
        map.put("register_id", PreferenceConnector.readString(getActivity(), PreferenceConnector.Register_id, ""));
        map.put("country_id", PreferenceConnector.readString(getActivity(), PreferenceConnector.countryId, ""));


        Log.e("MapMap", "EXERSICE LIST" + map);
        Call<GetShopingCategoryModal> loginCall = apiInterface.get_shopping_category(headerMap, map);
        loginCall.enqueue(new Callback<GetShopingCategoryModal>() {

            @Override
            public void onResponse(Call<GetShopingCategoryModal> call, Response<GetShopingCategoryModal> response) {
                DataManager.getInstance().hideProgressMessage();

                try {
                    GetShopingCategoryModal data = response.body();
                    String dataResponse = new Gson().toJson(response.body());
                    Log.e("MapMap", "Exersice_List" + dataResponse);

                    if (data.status.equals("1")) {

                        get_result.clear();
                        get_result.addAll(data.getResult());

                       // get_result.get(0).setClickOn(true);
                        adapter = new SubCategoryAdapter(getActivity(), get_result, HomeShoppingOnlineScreen.this);

                        // binding.dashboard.categoryList.setHasFixedSize(true);
                        binding.dashboard.categoryList.setLayoutManager(new LinearLayoutManager(getActivity(),
                                LinearLayoutManager.HORIZONTAL, false));

                        binding.dashboard.categoryList.setAdapter(adapter);
                        binding.dashboard.tvCountryProduct.setText(getString(R.string.latest_product_in) + " " + PreferenceConnector.readString(getActivity(), PreferenceConnector.countryName, ""));
                       // GetNearestRestorentsAPI(get_result.get(0).getId());
                        GetNearestRestorentsAPI("");

                    } else if (data.status.equals("0")) {
                        Toast.makeText(getContext(), data.message, Toast.LENGTH_SHORT).show();
                    } else if (data.status.equals("5")) {
                        PreferenceConnector.writeString(getActivity(), PreferenceConnector.LoginStatus, "false");
                        startActivity(new Intent(getActivity(), Splash.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP));
                        getActivity().finish();
                    }


                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<GetShopingCategoryModal> call, Throwable t) {
                call.cancel();
                DataManager.getInstance().hideProgressMessage();
            }
        });
    }


    public void navmenu() {
        if (binding.drawer.isDrawerOpen(GravityCompat.START)) {
            binding.drawer.closeDrawer(GravityCompat.START);
        } else {
            binding.drawer.openDrawer(GravityCompat.START);
        }
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


    private void GetCartItem() {

        // DataManager.getInstance().showProgressMessage(getActivity(), "Please wait...");
        Map<String, String> headerMap = new HashMap<>();
        headerMap.put("Authorization", "Bearer " + PreferenceConnector.readString(getActivity(), PreferenceConnector.access_token, ""));
        headerMap.put("Accept", "application/json");

        Map<String, String> map = new HashMap<>();
        map.put("user_id", PreferenceConnector.readString(getContext(), PreferenceConnector.User_id, ""));
        map.put("register_id", PreferenceConnector.readString(getActivity(), PreferenceConnector.Register_id, ""));
        map.put("country_id",PreferenceConnector.readString(getActivity(), PreferenceConnector.countryId, ""));

        Log.e("MapMap", "EXERSICE LIST" + map);

        Call<CartModal> loginCall = apiInterface.get_cart(headerMap, map);

        loginCall.enqueue(new Callback<CartModal>() {

            @Override
            public void onResponse(Call<CartModal> call, Response<CartModal> response) {
                DataManager.getInstance().hideProgressMessage();
                try {
                    CartModal data = response.body();
                    String dataResponse = new Gson().toJson(response.body());
                    Log.e("MapMap", "Exersice_List" + dataResponse);

                    if (data.status.equals("1")) {
                        if (data.getResult().size() > 0) {
                            binding.dashboard.reqcount.setVisibility(View.VISIBLE);
                            binding.dashboard.reqcount.setText(data.getResult().size() + "");
                        } else {
                            //  binding.RRbtm.setVisibility(View.GONE);
                            binding.dashboard.reqcount.setVisibility(View.GONE);

                        }

                    } else if (data.status.equals("0")) {
                        //  Toast.makeText(getContext(), "No Data Found !!!!", Toast.LENGTH_SHORT).show();
                        binding.dashboard.reqcount.setVisibility(View.GONE);


                    } else if (data.status.equals("5")) {
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

    @Override
    public void onItem(int position) {
        categoryId = get_result.get(position).getId();

        for (int i =0;i<get_result.size();i++){
            get_result.get(i).setClickOn(false);
        }

        get_result.get(position).setClickOn(true);

        adapter.notifyDataSetChanged();
        GetNearestRestorentsAPI(categoryId);
        //  getProduct(PreferenceConnector.readString(getActivity(), PreferenceConnector.countryId,""),categoryId);

    }


    private void getProduct(String countryId, String categoryId) {
        Map<String, String> map = new HashMap<>();
        Map<String, String> headerMap = new HashMap<>();
        headerMap.put("Authorization", "Bearer " + PreferenceConnector.readString(getActivity(), PreferenceConnector.access_token, ""));
        headerMap.put("Accept", "application/json");


        map.put("user_id", PreferenceConnector.readString(getContext(), PreferenceConnector.User_id, ""));
        map.put("country_id", countryId);
        map.put("category_id", categoryId);
        map.put("register_id", PreferenceConnector.readString(getActivity(), PreferenceConnector.Register_id, ""));

        Log.e(TAG, "getProduct Search Request = " + map.toString());
        Call<ResponseBody> call = apiInterface.getAllProductCatCountry(headerMap, map);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                try {
                    String responseString = response.body().string();
                    JSONObject jsonObject = new JSONObject(responseString);
                    Log.e(TAG, "getProduct Search Response = " + responseString);
                    if (jsonObject.getString("status").equals("1")) {
                        ProductItemModel model = new Gson().fromJson(responseString, ProductItemModel.class);
                        binding.dashboard.rlProduct.setVisibility(View.VISIBLE);
                        arrayList.clear();
                        arrayList.addAll(model.getResult());
                        adapterSearch.notifyDataSetChanged();
                        //  binding.tvNotFound.setVisibility(View.GONE);
                     //   if (arrayList.size() > 1)
                       //     binding.dashboard.tvViewAllProduct.setVisibility(View.VISIBLE);
                      //  else binding.dashboard.tvViewAllProduct.setVisibility(View.GONE);
                    } else if (jsonObject.getString("status").equals("5")) {
                        PreferenceConnector.writeString(getActivity(), PreferenceConnector.LoginStatus, "false");
                        startActivity(new Intent(getActivity(), Splash.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP));
                        getActivity().finish();

                    } else {
                        binding.dashboard.rlProduct.setVisibility(View.GONE);
                        arrayList.clear();
                        adapterSearch.notifyDataSetChanged();
                        // binding.tvNotFound.setVisibility(View.VISIBLE);

                    }

                } catch (Exception e) {
                    // Toast.makeText(mContext, "Exception = " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.e("Exception", "Exception = " + e.getMessage());
                }

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                //binding.swiperRefresh.setRefreshing(false);
            }
        });
    }


    public void showImageSelection() {

        final Dialog dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().getAttributes().windowAnimations = android.R.style.Widget_Material_ListPopupWindow;
        dialog.setContentView(R.layout.dialog_show_image_selection1);
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        Window window = dialog.getWindow();
        lp.copyFrom(window.getAttributes());
        //This makes the dialog take up the full width
        lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        window.setAttributes(lp);
        LinearLayout layoutCamera = (LinearLayout) dialog.findViewById(R.id.layoutCemera);
        LinearLayout layoutGallary = (LinearLayout) dialog.findViewById(R.id.layoutGallary);
        layoutCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                dialog.cancel();
                openCamera();
            }
        });
        layoutGallary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                dialog.cancel();
                getPhotoFromGallary();
            }
        });
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
    }

    private void getPhotoFromGallary() {
      /*  Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        startActivityForResult(Intent.createChooser(intent, "Select Image"), SELECT_FILE);*/


        Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        startActivityForResult(Intent.createChooser(intent, "Select Image"), SELECT_FILE);

    }

    private void openCamera() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {

            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(getActivity(),
                        "com.my.afarycode.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_CAMERA);
            }
        }
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" /*+ timeStamp + "_"*/;
//        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        str_image_path = image.getAbsolutePath();


        return image;
    }

    //CHECKING FOR Camera STATUS
    public boolean checkPermissionForReadStorage11() {
        if (ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED

                ||

                ContextCompat.checkSelfPermission(getActivity(),
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED
                ||

                ContextCompat.checkSelfPermission(getActivity(),
                        Manifest.permission.READ_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED
        ) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                    Manifest.permission.CAMERA)

                    ||

                    ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                            Manifest.permission.READ_EXTERNAL_STORAGE)
                    ||
                    ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                            Manifest.permission.WRITE_EXTERNAL_STORAGE)


            ) {


                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        MY_PERMISSION_CONSTANT2);

            } else {

                //explain("Please Allow Location Permission");
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        MY_PERMISSION_CONSTANT2);
            }
            return false;
        } else {

            //  explain("Please Allow Location Permission");
            return true;
        }
    }


    public boolean checkPermissionFor12Above() {
        if (ContextCompat.checkSelfPermission(requireActivity(),
                Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED

                ||

                ContextCompat.checkSelfPermission(requireActivity(),
                        Manifest.permission.READ_MEDIA_IMAGES)
                        != PackageManager.PERMISSION_GRANTED

        ) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(),
                    Manifest.permission.CAMERA)

                    ||

                    ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(),
                            Manifest.permission.READ_MEDIA_IMAGES)
            ) {


                ActivityCompat.requestPermissions(requireActivity(),
                        new String[]{Manifest.permission.CAMERA, Manifest.permission.READ_MEDIA_IMAGES},
                        101);

            } else {

                //explain("Please Allow Location Permission");
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(requireActivity(),
                        new String[]{Manifest.permission.CAMERA, Manifest.permission.READ_MEDIA_IMAGES},
                        101);
            }
            return false;
        } else {

            //  explain("Please Allow Location Permission");
            return true;
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case MY_PERMISSION_CONSTANT2: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0) {
                    boolean camera = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean read_external_storage = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                    boolean write_external_storage = grantResults[2] == PackageManager.PERMISSION_GRANTED;
                    if (camera && read_external_storage && write_external_storage) {
                        showImageSelection();
                    } else {
                        Toast.makeText(getActivity(), " permission denied, boo! Disable the functionality that depends on this permission.", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getActivity(), "  permission denied, boo! Disable the functionality that depends on this permission.", Toast.LENGTH_SHORT).show();
                }
                // return;
            }


            case 101: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0) {
                    boolean camera = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean read_external_storage = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                    if (camera && read_external_storage) {
                        showImageSelection();
                    } else {
                        Toast.makeText(requireActivity(), "12 permission denied, boo! Disable the functionality that depends on this permission.", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(requireActivity(), "12 permission denied, boo! Disable the functionality that depends on this permission.", Toast.LENGTH_SHORT).show();
                }
                // return;
            }


        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
/*
            if (requestCode == AUTOCOMPLETE_REQUEST_CODE_ADDRESS) {

                Place place = Autocomplete.getPlaceFromIntent(data);
                try {
                    Log.e("addressStreet====", place.getAddress());
                    address = place.getAddress();
                    latitude = place.getLatLng().latitude;
                    longitude = place.getLatLng().longitude;
                    //  city = DataManager.getInstance().getAddress(SignupAct.this,latitude,longitude);
                    //  binding.tvCity.setVisibility(View.VISIBLE);
                    //   binding.tvCity.setText(city);
                    binding.tvAddress.setText(place.getAddress());
                    latitude = place.getLatLng().latitude;
                    longitude = place.getLatLng().longitude;
                } catch (Exception e) {
                    e.printStackTrace();
                    //setMarker(latLng);
                }

            }
*/
            if (resultCode == AutocompleteActivity.RESULT_ERROR) {
                // TODO: Handle the error.
                Status status = Autocomplete.getStatusFromIntent(data);
            } else if (requestCode == SELECT_FILE) {
                str_image_path = DataManager.getInstance().getRealPathFromURI(getActivity(), data.getData());

                try {
                    oneBitmap = MediaStore.Images.Media.getBitmap(requireActivity().getContentResolver(), data.getData());
                       /* if(oneBitmap!=null) {
                            oneBitmap = resizeBitmap(oneBitmap, 3000, 3000);
                        }*/

                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                Log.e("bitmap===", oneBitmap + "");
                Glide.with(getActivity())
                        .load(str_image_path)
                        .centerCrop()
                        .into(binding.childNavDrawer.imgUser);

                UpDateAPi();


            } else if (requestCode == REQUEST_CAMERA) {
                Glide.with(getActivity())
                        .load(str_image_path)
                        .centerCrop()
                        .into(binding.childNavDrawer.imgUser);


                Glide.with(requireActivity())
                        .asBitmap()
                        .load(str_image_path)  // URL or file path
                        .centerCrop()
                        .into(new CustomTarget<Bitmap>() {
                            @Override
                            public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                                // Bitmap is now ready to use
                                // Do something with the Bitmap
                                oneBitmap = resource;
                                binding.childNavDrawer.imgUser.setImageBitmap(resource);
                            }

                            @Override
                            public void onLoadCleared(@Nullable Drawable placeholder) {
                                // Handle cleanup if necessary
                            }
                        });


                UpDateAPi();

            }


        }


    }

    private void UpDateAPi() {

        DataManager.getInstance().showProgressMessage(getActivity(), getString(R.string.please_wait));
        MultipartBody.Part filePart;

        Map<String, String> headerMap = new HashMap<>();
        headerMap.put("Authorization", "Bearer " + PreferenceConnector.readString(getActivity(), PreferenceConnector.access_token, ""));
        headerMap.put("Accept", "application/json");

        if (oneBitmap != null) {
            File file =  persistImage(oneBitmap, generateString(12),requireActivity());  //DataManager.saveBitmapToFile(requireActivity(), oneBitmap, "image.jpg");
            //filePart1 = MultipartBody.Part.createFormData("image", file.getName(), RequestBody.create(MediaType.parse("image_1/*"), file));

            //  File file = DataManager.getInstance().saveBitmapToFile(new File((str_image_path)));

            filePart = MultipartBody.Part.createFormData("image", file.getName(),
                    RequestBody.create(MediaType.parse("image/*"), file));

            Log.e("str_image_path1>>>", "" + str_image_path);

        } else {

            RequestBody attachmentEmpty = RequestBody.create(MediaType.parse("text/plain"), "");
            filePart = MultipartBody.Part.createFormData("attachment", "", attachmentEmpty);
        }

        RequestBody user_id = RequestBody.create(MediaType.parse("text/plain"), PreferenceConnector.readString(getContext(), PreferenceConnector.User_id, ""));
        RequestBody name = RequestBody.create(MediaType.parse("text/plain"), data.getResult().getName());
        RequestBody user_name = RequestBody.create(MediaType.parse("text/plain"), data.getResult().getUserName());
        RequestBody email = RequestBody.create(MediaType.parse("text/plain"), data.getResult().getEmail());
        RequestBody mobile = RequestBody.create(MediaType.parse("text/plain"), data.getResult().getMobile());
        RequestBody etAddresslogin = RequestBody.create(MediaType.parse("text/plain"), "");
        RequestBody regisId = RequestBody.create(MediaType.parse("text/plain"), PreferenceConnector.readString(getContext(), PreferenceConnector.Register_id, ""));


        Call<UpdateProfileModal> signupCall = apiInterface.update_profile(headerMap, user_id, name, user_name, email, mobile, etAddresslogin, regisId, filePart);

        signupCall.enqueue(new Callback<UpdateProfileModal>() {
            @Override
            public void onResponse(Call<UpdateProfileModal> call, Response<UpdateProfileModal> response) {
                DataManager.getInstance().hideProgressMessage();
                try {
                    UpdateProfileModal data = response.body();

                    if (data.status.equals("1")) {
                        String dataResponse = new Gson().toJson(response.body());
                        Log.e("MapMap", "EDIT PROFILE RESPONSE" + dataResponse);
                        // Toast.makeText(getContext(), data.message, Toast.LENGTH_SHORT).show();
                        GetProfileAPI();
                    } else if (data.status.equals("0")) {
                        Toast.makeText(getContext(), data.message, Toast.LENGTH_SHORT).show();
                    } else if (data.status.equals("5")) {
                        PreferenceConnector.writeString(getActivity(), PreferenceConnector.LoginStatus, "false");
                        startActivity(new Intent(getActivity(), Splash.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP));
                        getActivity().finish();

                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<UpdateProfileModal> call, Throwable t) {
                call.cancel();
                DataManager.getInstance().hideProgressMessage();
            }

        });


    }


    private File persistImage(Bitmap bitmap, String name, Context context) {
        File filesDir = context.getFilesDir();
        File imageFile = new File(filesDir, name + ".jpg");

        Log.e("image name===", name);
        OutputStream os;
        try {
            os = new FileOutputStream(imageFile);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 70, os);
            os.flush();
            os.close();
        } catch (Exception e) {
            Log.e("TAG", "persistImage: " + e.getMessage());
        }

        return imageFile;

    }


    public String generateString(int length) {
        Random random = new Random();
        StringBuilder builder = new StringBuilder(length);

        for (int i = 0; i < length; i++) {
            builder.append(ALPHABET.charAt(random.nextInt(ALPHABET.length())));
        }

        return builder.toString();
    }





}