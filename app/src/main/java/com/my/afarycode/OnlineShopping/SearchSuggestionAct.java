package com.my.afarycode.OnlineShopping;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;

import com.google.gson.Gson;
import com.my.afarycode.OnlineShopping.Model.CountryModel;
import com.my.afarycode.OnlineShopping.Model.ProductItemModel;
import com.my.afarycode.OnlineShopping.Model.ShopModel;
import com.my.afarycode.OnlineShopping.Model.SuggestProductModel;
import com.my.afarycode.OnlineShopping.adapter.AdapterSearch;
import com.my.afarycode.OnlineShopping.adapter.HomeShoppingNearsetRestorents;
import com.my.afarycode.OnlineShopping.adapter.ProductAdapter2;
import com.my.afarycode.OnlineShopping.adapter.SuggestionAdapter;
import com.my.afarycode.OnlineShopping.constant.PreferenceConnector;
import com.my.afarycode.OnlineShopping.helper.DataManager;
import com.my.afarycode.OnlineShopping.listener.onItemClickListener;
import com.my.afarycode.OnlineShopping.servercommunication.GPSTracker;
import com.my.afarycode.R;
import com.my.afarycode.Splash;
import com.my.afarycode.databinding.ActivitySearchBinding;
import com.my.afarycode.ratrofit.AfaryCode;
import com.my.afarycode.ratrofit.ApiClient;

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

public class SearchSuggestionAct extends Fragment implements onItemClickListener {
    public String TAG = "SearchAct";
    AfaryCode apiInterface;
    private ArrayList<ProductItemModel.Result> arrayList = new ArrayList<>();
   // private AdapterSearch adapter;
    private ActivitySearchBinding binding;
    private String queryString = "",subCatId="",catId="";

    private List<ShopModel.Result> get_result1 = new ArrayList<>();
    private List<ShopModel.Result> allShops = new ArrayList<>();
    private HomeShoppingNearsetRestorents adapter1;
    private GPSTracker gpsTracker;
    private double lat;
    private double lon;
    private static final int MY_PERMISSION_CONSTANT = 5;
    ProductAdapter2 adapterSearch;


    private SuggestionAdapter adapter;
   // private List<SuggestProductModel.Result> suggestions=new ArrayList<>();
    private List<ProductItemModel.Result> suggestions=new ArrayList<>();


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        binding = DataBindingUtil.inflate(inflater, R.layout.activity_search, container, false);
        apiInterface = ApiClient.getClient(requireActivity()).create(AfaryCode.class);
        BindView();
        return binding.getRoot();
    }

    /*@Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_search);
        apiInterface = ApiClient.getClient(context).create(AfaryCode.class);
        BindView();
    }*/

    private void BindView() {
        //   getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if (checkPermisssionForReadStorage()) {

            gpsTracker = new GPSTracker(getActivity());
            lat = gpsTracker.getLatitude();
            lon = gpsTracker.getLongitude();

            Geocoder geocoder = new Geocoder(requireActivity(), Locale.getDefault());
            try {
                List<Address> addresses = geocoder.getFromLocation(lat, lon, 1);
                Log.e("addresses>>>", "" + addresses.get(0).getAddressLine(0));

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        binding.llMain.setVisibility(View.VISIBLE);
        binding.search.setThreshold(3);
        adapter = new SuggestionAdapter(requireActivity(), suggestions);
        binding.search.setAdapter(adapter);

        // Set the item click listener
        binding.search.setOnItemClickListener((parent, view, position, id) -> {

          //  if (position >= 0 && position < arrayList.size()) {
            ProductItemModel.Result selectedPlace = (ProductItemModel.Result) parent.getItemAtPosition(position);
                if (selectedPlace != null) {
                    binding.search.setText(selectedPlace.getProductName());
                    Log.e("Selected Data", "Selected position out of bounds: " + selectedPlace.getProductName().toString());

                  //  startActivity(new Intent(requireActivity(), ProductDetailAct.class)
                  //          .putExtra("product_id", selectedPlace.getProId())
                  //          .putExtra("restaurant_id", selectedPlace.getRestaurantId())
                   //         .putExtra("productPrice", selectedPlace.getProductPrice()));
                }
          //  } else {
           //     Log.e("Error", "Selected position out of bounds: " + position);
          //  }

        });


        binding.btnSearch.setOnClickListener(v -> {
            binding.llMain.setVisibility(View.VISIBLE);
            getSearchBtnProduct();
            GetShopByProductName("");
        });

        binding.search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Filter suggestions based on user input
                //adapter.getFilter().filter(s);

                if (s.length() > 0) {
                  // binding.llMain.setVisibility(View.GONE);
                    queryString = s.toString();
                    getSearchProduct(); // Call to fetch suggestions
                } else {
                    suggestions.clear(); // Clear suggestions if input is empty
                    adapter.notifyDataSetChanged();
                  //  arrayList.clear();
                  //  adapterSearch.notifyDataSetChanged();
                    GetNearestRestaurantAPI("");

                   // binding.llMain.setVisibility(View.VISIBLE);
                }

            }

            @Override
            public void afterTextChanged(Editable s) {}
        });





      /*  binding.search.setIconified(false);
        binding.search.setQueryHint(getString(R.string.search_here));
       // adapter = new AdapterSearch(this, arrayList, this);
       // binding.recyList.setAdapter(adapter);
      //  binding.swiperRefresh.setOnRefreshListener(this::getProduct);
        binding.search.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                finish();
                return false;
            }
        });
        binding.search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                queryString = s;
                getProduct();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                queryString = s;
                getProduct();
                return false;
            }
        });*/

        adapterSearch = new ProductAdapter2(requireActivity(), (ArrayList<ProductItemModel.Result>) arrayList, 4);
        binding.rvProduct.setLayoutManager(new GridLayoutManager(requireActivity(), 2));
        binding.rvProduct.setAdapter(adapterSearch);

        GetNearestRestaurantAPI("");


    }


    private boolean checkPermisssionForReadStorage() {

        if (ContextCompat.checkSelfPermission(requireActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(requireActivity(),
                Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(requireActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(),
                    Manifest.permission.ACCESS_FINE_LOCATION) ||
                    ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(),
                            Manifest.permission.ACCESS_COARSE_LOCATION)) {

                ActivityCompat.requestPermissions(requireActivity(),
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION
                                , Manifest.permission.ACCESS_COARSE_LOCATION},
                        MY_PERMISSION_CONSTANT);

                gpsTracker = new GPSTracker(getActivity());
                lat = gpsTracker.getLatitude();
                lon = gpsTracker.getLongitude();
                Log.e("latitute", "" + lat);
                Log.e("longitute", "" + lon);


            } else {
                ActivityCompat.requestPermissions(requireActivity(),
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION
                                , Manifest.permission.ACCESS_COARSE_LOCATION},
                        MY_PERMISSION_CONSTANT);
            }

            return false;

        } else {
            return true;
        }
    }




    private void getSearchProduct() {
        //binding.swiperRefresh.setRefreshing(true);
        Map<String,String> headerMap = new HashMap<>();
        headerMap.put("Authorization","Bearer " + PreferenceConnector.readString(requireActivity(), PreferenceConnector.access_token,""));
        headerMap.put("Accept","application/json");

        HashMap<String,String> param = new HashMap<>();
        param.put("title",queryString);
        param.put("user_id", PreferenceConnector.readString(requireActivity(), PreferenceConnector.User_id, ""));
        param.put("register_id", PreferenceConnector.readString(requireActivity(), PreferenceConnector.Register_id, ""));
        param.put("country_id", PreferenceConnector.readString(requireActivity(), PreferenceConnector.countryId, ""));

        Call<ResponseBody> call = apiInterface.searchSuggestionProduct(headerMap,param);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
               // binding.swiperRefresh.setRefreshing(false);

                try {
                    String responseString = response.body().string();
                    JSONObject jsonObject = new JSONObject(responseString);
                    Log.e(TAG,"getProduct Search Response = " + responseString);
                    if(jsonObject.getString("status").equals("1")) {
                        ProductItemModel model = new Gson().fromJson(responseString, ProductItemModel.class);
                        suggestions.clear();
                        suggestions.addAll(model.getResult());
                        adapter = new SuggestionAdapter(requireActivity(), suggestions);
                        binding.search.setAdapter(adapter);
                        adapter.notifyDataSetChanged();
                     //   adapterSearch.notifyDataSetChanged();
                        binding.tvNotFound.setVisibility(View.GONE);
                       // View v = new View(getActivity());
                     //   showDropDownProductSuggestion(v,arrayList);

                    }

                    else if (jsonObject.getString("status").equals("5")) {
                        PreferenceConnector.writeString(requireActivity(), PreferenceConnector.LoginStatus, "false");
                        startActivity(new Intent(requireActivity(), Splash.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP));
                        requireActivity().finish();

                    }



                    else {
                        suggestions.clear();
                        adapter.notifyDataSetChanged();
                     //   adapterSearch.notifyDataSetChanged();
                     //   binding.tvNotFound.setVisibility(View.VISIBLE);

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

    private void getSearchBtnProduct() {
        //binding.swiperRefresh.setRefreshing(true);
        DataManager.getInstance().showProgressMessage(getActivity(),getString(R.string.please_wait));
        Map<String,String> headerMap = new HashMap<>();
        headerMap.put("Authorization","Bearer " + PreferenceConnector.readString(requireActivity(), PreferenceConnector.access_token,""));
        headerMap.put("Accept","application/json");

        HashMap<String,String> param = new HashMap<>();
        param.put("title",queryString);
        param.put("user_id", PreferenceConnector.readString(requireActivity(), PreferenceConnector.User_id, ""));
        param.put("register_id", PreferenceConnector.readString(requireActivity(), PreferenceConnector.Register_id, ""));
        param.put("country_id", PreferenceConnector.readString(requireActivity(), PreferenceConnector.countryId, ""));

        Call<ResponseBody> call = apiInterface.searchSuggestionProduct(headerMap,param);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                // binding.swiperRefresh.setRefreshing(false);
                 DataManager.getInstance().hideProgressMessage();
                try {
                    String responseString = response.body().string();
                    JSONObject jsonObject = new JSONObject(responseString);
                    Log.e(TAG,"getProduct  btn Search Response = " + responseString);
                    if(jsonObject.getString("status").equals("1")) {
                        ProductItemModel model = new Gson().fromJson(responseString, ProductItemModel.class);
                        arrayList.clear();
                        arrayList.addAll(model.getResult());
                        adapterSearch.notifyDataSetChanged();
                        binding.tvNotFound.setVisibility(View.GONE);
                        // View v = new View(getActivity());
                        //   showDropDownProductSuggestion(v,arrayList);

                    }

                    else if (jsonObject.getString("status").equals("5")) {
                        PreferenceConnector.writeString(requireActivity(), PreferenceConnector.LoginStatus, "false");
                        startActivity(new Intent(requireActivity(), Splash.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP));
                        requireActivity().finish();

                    }



                    else {
                        arrayList.clear();
                        adapterSearch.notifyDataSetChanged();
                        binding.tvNotFound.setVisibility(View.VISIBLE);

                    }

                } catch (Exception e) {
                    // Toast.makeText(mContext, "Exception = " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.e("Exception","Exception = " + e.getMessage());
                    DataManager.getInstance().hideProgressMessage();

                }

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                //binding.swiperRefresh.setRefreshing(false);
                DataManager.getInstance().hideProgressMessage();

            }
        });
    }





    private void showDropDownProductSuggestion(View v, List<ProductItemModel.Result> stringList) {
        PopupMenu popupMenu = new PopupMenu(requireActivity(), v);
        for (int i = 0; i < stringList.size(); i++) {
            popupMenu.getMenu().add(stringList.get(i).getProductName());
        }
        popupMenu.setOnMenuItemClickListener(menuItem -> {
            for (int i = 0; i < stringList.size(); i++) {
                if(stringList.get(i).getProductName().equalsIgnoreCase(menuItem.getTitle().toString())) {
                    //countryId = stringList.get(i).getId();
                    startActivity(new Intent(requireActivity(), ProductDetailAct.class)
                            .putExtra("product_id",arrayList.get(i).getProId())
                            .putExtra("restaurant_id",arrayList.get(i).getRestaurantId())
                            .putExtra("productPrice",arrayList.get(i).getProductPrice()));
                }
            }
            return true;
        });
        popupMenu.show();
    }



    @Override
    public void onItem(int position) {

        if(arrayList.get(position).getCountryId().equalsIgnoreCase(PreferenceConnector.readString(requireActivity(), PreferenceConnector.countryId,"")))
            PreferenceConnector.writeString(requireActivity(),PreferenceConnector.filterType,"Domestic");
        else PreferenceConnector.writeString(requireActivity(),PreferenceConnector.filterType,"International");

        startActivity(new Intent(requireActivity(), ProductListAct.class)
                .putExtra("title",arrayList.get(position).getProductName())
                .putExtra("countryId",arrayList.get(position).getCountryId())
                .putExtra("by_screen","Search"));
    }


    private void getProduct(String countryId, String categoryId) {
        Map<String, String> map = new HashMap<>();
        Map<String, String> headerMap = new HashMap<>();
        headerMap.put("Authorization", "Bearer " + PreferenceConnector.readString(requireActivity(), PreferenceConnector.access_token, ""));
        headerMap.put("Accept", "application/json");


        map.put("user_id", PreferenceConnector.readString(requireActivity(), PreferenceConnector.User_id, ""));
        map.put("country_id", countryId);
        map.put("category_id", categoryId);
        map.put("register_id", PreferenceConnector.readString(requireActivity(), PreferenceConnector.Register_id, ""));

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
                        binding.rlProduct.setVisibility(View.VISIBLE);
                        arrayList.clear();
                        arrayList.addAll(model.getResult());
                        adapterSearch.notifyDataSetChanged();
                        //  binding.tvNotFound.setVisibility(View.GONE);
                        //   if (arrayList.size() > 1)
                        //     binding.dashboard.tvViewAllProduct.setVisibility(View.VISIBLE);
                        //  else binding.dashboard.tvViewAllProduct.setVisibility(View.GONE);
                    } else if (jsonObject.getString("status").equals("5")) {
                        PreferenceConnector.writeString(requireActivity(), PreferenceConnector.LoginStatus, "false");
                        startActivity(new Intent(requireActivity(), Splash.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP));
                        requireActivity().finish();

                    } else {
                        binding.rlProduct.setVisibility(View.GONE);
                        suggestions.clear();
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

    private void GetNearestRestaurantAPI(String cat_id) {

        DataManager.getInstance().showProgressMessage(requireActivity(), getString(R.string.please_wait));

        Map<String, String> headerMap = new HashMap<>();
        headerMap.put("Authorization", "Bearer " + PreferenceConnector.readString(requireActivity(), PreferenceConnector.access_token, ""));
        headerMap.put("Accept", "application/json");

        Map<String, String> map = new HashMap<>();
        map.put("user_id", PreferenceConnector.readString(requireActivity(), PreferenceConnector.User_id, ""));
        map.put("latitute",  lat+"");
        map.put("longitute",  lon+"");
        map.put("category_id", "" + cat_id);
        map.put("register_id", PreferenceConnector.readString(requireActivity(), PreferenceConnector.Register_id, ""));
        map.put("country_id", PreferenceConnector.readString(requireActivity(), PreferenceConnector.countryId, ""));

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
                        getProduct(PreferenceConnector.readString(requireActivity(), PreferenceConnector.countryId, ""), cat_id);
                    }
                    else if(cat_id.contentEquals("view all")){
                    }
                    else {
                        getProduct(PreferenceConnector.readString(requireActivity(), PreferenceConnector.countryId, ""), cat_id);
                    }

                    if (jsonObject.getString("status").equals("1")) {
                        ShopModel mainCateModel = new Gson().fromJson(stringResponse, ShopModel.class);
                        binding.rlShops.setVisibility(View.VISIBLE);
                        allShops = new ArrayList<>();
                        get_result1.clear();

                        get_result1.addAll(mainCateModel.getResult());
                        allShops.addAll(mainCateModel.getResult());

                        if(cat_id.isEmpty()) {
                            adapter1 = new HomeShoppingNearsetRestorents(requireActivity(), get_result1.subList(0, Math.min(4, get_result1.size())), get_result1.size());
                            binding.recyclerShop.setLayoutManager(new GridLayoutManager(requireActivity(), 2));
                            binding.recyclerShop.setAdapter(adapter1);
                        }
                        else if(cat_id.equals("view all")){
                            Log.e("chala=====","view all==="+ allShops.size());
                            adapter1 = new HomeShoppingNearsetRestorents(requireActivity(), allShops, allShops.size());
                            binding.recyclerShop.setLayoutManager(new GridLayoutManager(requireActivity(), 2));
                            binding.recyclerShop.setAdapter(adapter1);
                        }
                        else {
                            adapter1 = new HomeShoppingNearsetRestorents(requireActivity(), get_result1.subList(0, Math.min(4, get_result1.size())), get_result1.size());
                            binding.recyclerShop.setLayoutManager(new GridLayoutManager(requireActivity(), 2));
                            binding.recyclerShop.setAdapter(adapter1);

                        }
                        //   adapter1.notifyDataSetChanged();
                       /* if (get_result1.size() > 4)
                            binding.dashboard.tvViewAll.setVisibility(View.VISIBLE);
                        else binding.dashboard.tvViewAll.setVisibility(View.GONE);*/

                    } else if (jsonObject.getString("status").equals("0")) {
                        binding.rlShops.setVisibility(View.GONE);
                        get_result1.clear();
                        adapter1.notifyDataSetChanged();
                        Toast.makeText(requireActivity(), jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                    } else if (jsonObject.optString("status").equals("5")) {
                        PreferenceConnector.writeString(requireActivity(), PreferenceConnector.LoginStatus, "false");
                        startActivity(new Intent(requireActivity(), Splash.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP));
                        requireActivity().finish();
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

    private void GetShopByProductName(String cat_id) {

        DataManager.getInstance().showProgressMessage(requireActivity(), getString(R.string.please_wait));

        Map<String, String> headerMap = new HashMap<>();
        headerMap.put("Authorization", "Bearer " + PreferenceConnector.readString(requireActivity(), PreferenceConnector.access_token, ""));
        headerMap.put("Accept", "application/json");

        Map<String, String> map = new HashMap<>();
        map.put("user_id", PreferenceConnector.readString(requireActivity(), PreferenceConnector.User_id, ""));
      //  map.put("latitute",  lat+"");
     //   map.put("longitute",  lon+"");
     //   map.put("category_id", "" + cat_id);
     //   map.put("register_id", PreferenceConnector.readString(requireActivity(), PreferenceConnector.Register_id, ""));
        map.put("country_id", PreferenceConnector.readString(requireActivity(), PreferenceConnector.countryId, ""));
        map.put("stirng",queryString);


        Log.e("MapMap", "Search Shop by Product Name" + map);
        Call<ResponseBody> loginCall = apiInterface.get_shop_by_product_name(headerMap, map);
        loginCall.enqueue(new Callback<ResponseBody>() {

            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                DataManager.getInstance().hideProgressMessage();

                try {
                    String stringResponse = response.body().string();
                    JSONObject jsonObject = new JSONObject(stringResponse);
                    Log.e("MapMap", "Search Shop by Product Name" + stringResponse);
                    if (jsonObject.getString("status").equals("1")) {
                        ShopModel mainCateModel = new Gson().fromJson(stringResponse, ShopModel.class);
                        binding.rlShops.setVisibility(View.VISIBLE);
                        allShops = new ArrayList<>();
                        get_result1.clear();

                        get_result1.addAll(mainCateModel.getResult());
                        allShops.addAll(mainCateModel.getResult());

                        if(cat_id.isEmpty()) {
                            adapter1 = new HomeShoppingNearsetRestorents(requireActivity(), get_result1.subList(0, Math.min(4, get_result1.size())), get_result1.size());
                            binding.recyclerShop.setLayoutManager(new GridLayoutManager(requireActivity(), 2));
                            binding.recyclerShop.setAdapter(adapter1);
                        }
                        else if(cat_id.equals("view all")){
                            Log.e("chala=====","view all==="+ allShops.size());
                            adapter1 = new HomeShoppingNearsetRestorents(requireActivity(), allShops, allShops.size());
                            binding.recyclerShop.setLayoutManager(new GridLayoutManager(requireActivity(), 2));
                            binding.recyclerShop.setAdapter(adapter1);
                        }
                        else {
                            adapter1 = new HomeShoppingNearsetRestorents(requireActivity(), get_result1.subList(0, Math.min(4, get_result1.size())), get_result1.size());
                            binding.recyclerShop.setLayoutManager(new GridLayoutManager(requireActivity(), 2));
                            binding.recyclerShop.setAdapter(adapter1);

                        }
                        //   adapter1.notifyDataSetChanged();
                       /* if (get_result1.size() > 4)
                            binding.dashboard.tvViewAll.setVisibility(View.VISIBLE);
                        else binding.dashboard.tvViewAll.setVisibility(View.GONE);*/

                    } else if (jsonObject.getString("status").equals("0")) {
                        binding.rlShops.setVisibility(View.GONE);
                        get_result1.clear();
                        adapter1.notifyDataSetChanged();
                        Toast.makeText(requireActivity(), jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                    } else if (jsonObject.optString("status").equals("5")) {
                        PreferenceConnector.writeString(requireActivity(), PreferenceConnector.LoginStatus, "false");
                        startActivity(new Intent(requireActivity(), Splash.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP));
                        requireActivity().finish();
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






}
