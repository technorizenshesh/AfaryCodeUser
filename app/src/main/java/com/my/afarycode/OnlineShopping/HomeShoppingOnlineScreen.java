package com.my.afarycode.OnlineShopping;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.my.afarycode.OnlineShopping.Model.CartModal;
import com.my.afarycode.OnlineShopping.Model.GetProfileModal;
import com.my.afarycode.OnlineShopping.Model.GetRestorentsModal;
import com.my.afarycode.OnlineShopping.Model.GetRestorentsModalCopy;
import com.my.afarycode.OnlineShopping.Model.GetShopingCategoryModal;
import com.my.afarycode.OnlineShopping.Model.HomeOfferModel;
import com.my.afarycode.OnlineShopping.Model.ProductItemModel;
import com.my.afarycode.OnlineShopping.activity.CardAct;
import com.my.afarycode.OnlineShopping.activity.CheckOutDeliveryAct;
import com.my.afarycode.OnlineShopping.adapter.HomeShoppingNearsetRestorents;
import com.my.afarycode.OnlineShopping.adapter.ProductAdapter2;
import com.my.afarycode.OnlineShopping.adapter.SubCategoryAdapter;
import com.my.afarycode.OnlineShopping.constant.PreferenceConnector;
import com.my.afarycode.OnlineShopping.deliveryaddress.DeliveryAddress;
import com.my.afarycode.OnlineShopping.fragment.MyAddressFragment;
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

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeShoppingOnlineScreen extends Fragment implements onItemClickListener {
    public String TAG = "HomeShoppingOnlineScreen";
    ActivityHomeShoppingNavBinding binding;
    private ArrayList<HomeOfferModel> modelList = new ArrayList<>();
    private ArrayList<GetShopingCategoryModal.Result> get_result = new ArrayList<>();
    private ArrayList<GetRestorentsModalCopy.Result> get_result1 = new ArrayList<>();

    private ArrayList<ProductItemModel.Result> arrayList = new ArrayList<>();

    private static final int MY_PERMISSION_CONSTANT = 5;

    Fragment fragment;
    private AfaryCode apiInterface;
    private SubCategoryAdapter adapter;
    private HomeShoppingNearsetRestorents adapter1;
    private GPSTracker gpsTracker;
    private double lat;
    private double lon;
    private String cat_id,categoryId="";

    ProductAdapter2 adapterSearch;

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
        GetCartItem();


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
            startActivity(new Intent(getActivity(),DeliveryAddress.class));
        });

        binding.dashboard.imgSearch.setOnClickListener(v -> {
            binding.dashboard.searchEtHome.setVisibility(View.VISIBLE);
        });

        binding.dashboard.tvViewAll.setOnClickListener(v -> {
            adapter1 = new HomeShoppingNearsetRestorents(getActivity(), get_result1,get_result1.size());
            binding.dashboard.recyclerShop.setLayoutManager(new GridLayoutManager(getActivity(), 2));
            binding.dashboard.recyclerShop.setAdapter(adapter1);
        });

        binding.dashboard.tvViewAllProduct.setOnClickListener(v->{
                adapterSearch = new ProductAdapter2(getActivity(), arrayList,arrayList.size());
        binding.dashboard.rvProduct.setLayoutManager(new GridLayoutManager(getActivity(), 2));
        binding.dashboard.rvProduct.setAdapter(adapterSearch);
        });



        binding.dashboard.searchEtHome.setOnClickListener(v -> {
            startActivity(new Intent(getActivity(),SearchAct.class));
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
            fragment = new UpdateProfile();
            loadFragment(fragment);

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

        adapterSearch = new ProductAdapter2(getActivity(), arrayList,4);
        binding.dashboard.rvProduct.setLayoutManager(new GridLayoutManager(getActivity(), 2));
        binding.dashboard.rvProduct.setAdapter(adapterSearch);


        adapter1 = new HomeShoppingNearsetRestorents(getActivity(), get_result1,4);
        binding.dashboard.recyclerShop.setLayoutManager(new GridLayoutManager(getActivity(), 2));
        binding.dashboard.recyclerShop.setAdapter(adapter1);
    }

    @Override
    public void onResume() {
        GetCategoryAPi();
        GetProfileAPI();

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

        DataManager.getInstance().showProgressMessage(getActivity(), "Please wait...");
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

                    GetProfileModal data = response.body();
                    String dataResponse = new Gson().toJson(response.body());


                    Log.e("MapMap", "GET RESPONSE" + dataResponse);

                    if (data.status.equals("1")) {

                        binding.childNavDrawer.txtUserName.setText(data.getResult().userName);

                        Log.e("image>>>", data.getResult().image);

                        Picasso.get().load(data.getResult().image).placeholder(R.drawable.user_default).error(R.drawable.user_default).into(binding.childNavDrawer.imgUser);


                    } else if (data.status.equals("0")) {
                        Toast.makeText(getActivity(), data.message /*getString(R.string.wrong_username_password)*/, Toast.LENGTH_SHORT).show();
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
            public void onFailure(Call<GetProfileModal> call, Throwable t) {
                call.cancel();
                DataManager.getInstance().hideProgressMessage();
            }
        });
    }

    private void GetNearestRestorentsAPI(String cat_id) {

        DataManager.getInstance().showProgressMessage(getActivity(), "Please wait...");

        Map<String,String> headerMap = new HashMap<>();
        headerMap.put("Authorization","Bearer " +PreferenceConnector.readString(getActivity(), PreferenceConnector.access_token,""));
        headerMap.put("Accept","application/json");

        Map<String, String> map = new HashMap<>();
        map.put("user_id", PreferenceConnector.readString(getContext(), PreferenceConnector.User_id, ""));
        map.put("latitute", "" + lat);
        map.put("longitute", "" + lon);
        map.put("category_id", "" + cat_id);
        map.put("register_id", PreferenceConnector.readString(getActivity(), PreferenceConnector.Register_id, ""));
        map.put("country_id",PreferenceConnector.readString(getActivity(), PreferenceConnector.countryId,""));

        Log.e("MapMap", "EXERSICE LIST" + map);
        Call<ResponseBody> loginCall = apiInterface.get_restaurant(headerMap,map);
        loginCall.enqueue(new Callback<ResponseBody>() {

            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                DataManager.getInstance().hideProgressMessage();

                try {
                    String stringResponse =  response.body().string();
                    JSONObject jsonObject = new JSONObject(stringResponse);
                    Log.e("MapMap", "near_List" + stringResponse);
                    getProduct(PreferenceConnector.readString(getActivity(), PreferenceConnector.countryId,""),cat_id);

                    if (jsonObject.getString("status").equals("1")) {
                        GetRestorentsModalCopy mainCateModel = new Gson().fromJson(stringResponse, GetRestorentsModalCopy.class);
                        binding.dashboard.rlShops.setVisibility(View.VISIBLE);
                        get_result1.clear();
                        get_result1.addAll(mainCateModel.getResult());
                        adapter1.notifyDataSetChanged();
                        if(get_result1.size()>1) binding.dashboard.tvViewAll.setVisibility(View.VISIBLE);
                        else binding.dashboard.tvViewAll.setVisibility(View.GONE);

                    } else if (jsonObject.getString("status").equals("0")) {
                        binding.dashboard.rlShops.setVisibility(View.GONE);
                        get_result1.clear();
                        adapter1.notifyDataSetChanged();
                        Toast.makeText(getContext(), jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                    }

                    else if (jsonObject.optString("status").equals("5")) {
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

    private void GetCategoryAPi() {

        DataManager.getInstance().showProgressMessage(getActivity(), "Please wait...");
        Map<String,String> headerMap = new HashMap<>();
        headerMap.put("Authorization","Bearer " +PreferenceConnector.readString(getActivity(), PreferenceConnector.access_token,""));
        headerMap.put("Accept","application/json");
        Map<String, String> map = new HashMap<>();
        map.put("user_id", PreferenceConnector.readString(getContext(), PreferenceConnector.User_id, ""));
        map.put("cat_id", cat_id);
        map.put("register_id", PreferenceConnector.readString(getActivity(), PreferenceConnector.Register_id, ""));
        map.put("country_id",PreferenceConnector.readString(getActivity(), PreferenceConnector.countryId,""));


        Log.e("MapMap", "EXERSICE LIST" + map);
        Call<GetShopingCategoryModal> loginCall = apiInterface.get_shopping_category(headerMap,map);
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

                        adapter = new SubCategoryAdapter(getActivity(), get_result,HomeShoppingOnlineScreen.this);

                       // binding.dashboard.categoryList.setHasFixedSize(true);
                        binding.dashboard.categoryList.setLayoutManager(new LinearLayoutManager(getActivity(),
                                LinearLayoutManager.HORIZONTAL, false));

                        binding.dashboard.categoryList.setAdapter(adapter);
                        binding.dashboard.tvCountryProduct.setText(getString(R.string.latest_product_in)+" " + PreferenceConnector.readString(getActivity(), PreferenceConnector.countryName,""));
                        GetNearestRestorentsAPI(get_result.get(0).getId());

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
                        if(data.getResult().size()>0){
                            binding.dashboard.reqcount.setVisibility(View.VISIBLE);
                            binding.dashboard.reqcount.setText(data.getResult().size()+"");
                        }else {
                            //  binding.RRbtm.setVisibility(View.GONE);
                            binding.dashboard.reqcount.setVisibility(View.GONE);

                        }

                    } else if (data.status.equals("0")) {
                      //  Toast.makeText(getContext(), "No Data Found !!!!", Toast.LENGTH_SHORT).show();
                        binding.dashboard.reqcount.setVisibility(View.GONE);


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

    @Override
    public void onItem(int position) {
        categoryId = get_result.get(position).getId();
        GetNearestRestorentsAPI(categoryId);
      //  getProduct(PreferenceConnector.readString(getActivity(), PreferenceConnector.countryId,""),categoryId);

    }


    private void getProduct(String countryId,String categoryId) {
        Map<String,String>map = new HashMap<>();
        Map<String,String> headerMap = new HashMap<>();
        headerMap.put("Authorization","Bearer " +PreferenceConnector.readString(getActivity(), PreferenceConnector.access_token,""));
        headerMap.put("Accept","application/json");


         map.put("user_id",PreferenceConnector.readString(getContext(), PreferenceConnector.User_id, ""));
         map.put("country_id",countryId);
        map.put("category_id",categoryId);
        map.put("register_id", PreferenceConnector.readString(getActivity(), PreferenceConnector.Register_id, ""));

        Log.e(TAG,"getProduct Search Request = "+map.toString());
        Call<ResponseBody> call = apiInterface.getAllProductCatCountry(headerMap,map);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                try {
                    String responseString = response.body().string();
                    JSONObject jsonObject = new JSONObject(responseString);
                    Log.e(TAG,"getProduct Search Response = " + responseString);
                    if(jsonObject.getString("status").equals("1")) {
                        ProductItemModel model = new Gson().fromJson(responseString, ProductItemModel.class);
                        binding.dashboard.rlProduct.setVisibility(View.VISIBLE);
                        arrayList.clear();
                        arrayList.addAll(model.getResult());
                        adapterSearch.notifyDataSetChanged();
                        //  binding.tvNotFound.setVisibility(View.GONE);
                        if(arrayList.size()>1) binding.dashboard.tvViewAllProduct.setVisibility(View.VISIBLE);
                        else binding.dashboard.tvViewAllProduct.setVisibility(View.GONE);
                    }
                    else if (jsonObject.getString("status").equals("5")) {
                        PreferenceConnector.writeString(getActivity(), PreferenceConnector.LoginStatus, "false");
                        startActivity(new Intent(getActivity(), Splash.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP));
                        getActivity().finish();

                    }


                    else {
                        binding.dashboard.rlProduct.setVisibility(View.GONE);
                        arrayList.clear();
                        adapterSearch.notifyDataSetChanged();
                        // binding.tvNotFound.setVisibility(View.VISIBLE);

                    }

                } catch (Exception e) {
                    // Toast.makeText(mContext, "Exception = " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.e("Exception","Exception = " + e.getMessage());
                }

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                //binding.swiperRefresh.setRefreshing(false);
            }
        });
    }





}