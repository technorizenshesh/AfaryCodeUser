package com.my.afarycode.OnlineShopping.fragment;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.gson.Gson;
import com.my.afarycode.OnlineShopping.ChangePassword;
import com.my.afarycode.OnlineShopping.HomeShoppingOnlineScreen;
import com.my.afarycode.OnlineShopping.Model.BannerModal1;
import com.my.afarycode.OnlineShopping.Model.CartModal;
import com.my.afarycode.OnlineShopping.Model.CategoryModal;
import com.my.afarycode.OnlineShopping.Model.CountryModel;
import com.my.afarycode.OnlineShopping.Model.GetProfileModal;
import com.my.afarycode.OnlineShopping.Model.HomeShopeProductModel;
import com.my.afarycode.OnlineShopping.Model.ProductItemModel;
import com.my.afarycode.OnlineShopping.NotificationScreen;
import com.my.afarycode.OnlineShopping.ProductListAct;
import com.my.afarycode.OnlineShopping.SearchAct;
import com.my.afarycode.OnlineShopping.SignUpActivity;
import com.my.afarycode.OnlineShopping.WellcomeScreen;
import com.my.afarycode.OnlineShopping.activity.CardAct;
import com.my.afarycode.OnlineShopping.adapter.BannerAdapter1;
import com.my.afarycode.OnlineShopping.adapter.CategoryAdapter;
import com.my.afarycode.OnlineShopping.adapter.HomeBannerAdapter;
import com.my.afarycode.OnlineShopping.adapter.HomeTopOfferAdapter;
import com.my.afarycode.OnlineShopping.adapter.ProductAdapter2;
import com.my.afarycode.OnlineShopping.adapter.SliderAdapterExample;
import com.my.afarycode.OnlineShopping.bottomsheet.CountryBottomSheet;
import com.my.afarycode.OnlineShopping.constant.PreferenceConnector;
import com.my.afarycode.OnlineShopping.helper.DataManager;
import com.my.afarycode.OnlineShopping.listener.SearchListener;
import com.my.afarycode.OnlineShopping.servercommunication.GPSTracker;
import com.my.afarycode.R;
import com.my.afarycode.Splash;
import com.my.afarycode.databinding.HomeFragmentBinding;
import com.my.afarycode.ratrofit.AfaryCode;
import com.my.afarycode.ratrofit.ApiClient;
import com.smarteist.autoimageslider.IndicatorView.animation.type.IndicatorAnimationType;
import com.smarteist.autoimageslider.SliderAnimations;
import com.smarteist.autoimageslider.SliderView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ScheduledExecutorService;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class HomeFragment extends Fragment implements SearchListener {
    public String TAG = "HomeFragment";
    HomeFragmentBinding binding;
    HomeTopOfferAdapter mAdapter;
    HomeBannerAdapter mAdapterBanner;
    private ArrayList<HomeShopeProductModel> modelList = new ArrayList<>();
    private ArrayList<HomeShopeProductModel> modelListRestaurentBanner = new ArrayList<>();
    private ArrayList<HomeShopeProductModel> modelListBanner = new ArrayList<>();
    private ArrayList<CategoryModal.Result> get_result = new ArrayList<>();
    private ArrayList<BannerModal1.Result> get_result1 = new ArrayList<>();
    private ArrayList<String> banner_array_list = new ArrayList<>();
    private ArrayList<String> banner_array_list1 = new ArrayList<>();
    private ArrayList<ProductItemModel.Result> arrayList = new ArrayList<>();
    ArrayList<CountryModel.Result> countryArrayList;
    int PERMISSION_ID = 44;
    Fragment fragment;
    private AfaryCode apiInterface;
    private CategoryAdapter adapter;
    private BannerAdapter1 mAdapterBanner1;
    private GPSTracker gpsTracker;
    private ScheduledExecutorService scheduleTaskExecutor;
    private String notification_unseen_count;
    private String cart_unseen_count;
    private JSONObject result1;
    private String counter_message_int;
    private SliderAdapterExample adapter1;
    private SliderAdapterExample adapter2;
    private ArrayList<CartModal.Result> get_result11 = new ArrayList<>();

    List<Address> addresses;
    ProductAdapter2 adapterSearch;
    private String countryNames="",lastCountryName="",lastCountryId="",countryId="",countryLastId ="";

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.home_fragment, container, false);
        apiInterface = ApiClient.getClient(getContext()).create(AfaryCode.class);
        countryArrayList = new ArrayList<>();

        SetupUI();
        getAllCountry();

        binding.LLShopOnline.setOnClickListener(v -> {
            fragment = new HomeShoppingOnlineScreen();
            loadFragment(fragment);
        });

        binding.llicon.setOnClickListener(v -> {
            //   fragment = new CardActivity();
            // loadFragment(fragment);
            startActivity(new Intent(getActivity(), CardAct.class));


        });



        binding.searchEtHome.setOnClickListener(v -> {
            if(!countryId.equalsIgnoreCase("")){
                PreferenceConnector.writeString(getActivity(),PreferenceConnector.filterType,"");
                startActivity(new Intent(getActivity(), ProductListAct.class)
                        .putExtra("title","")
                        .putExtra("countryId",countryId));
            }
            else Toast.makeText(getActivity(), getString(R.string.please_add_country), Toast.LENGTH_SHORT).show();


        });


      //  setAdapter();

      //  setAdapterBannerMain();
       // setAdapterRestaurentBanner();


        binding.tvChangeLocation.setOnClickListener(v -> {
            new CountryBottomSheet(countryNames).callBack(this::search).show(getChildFragmentManager(),"");
        });

        return binding.getRoot();

    }

    private void getNotificationCounter() {
        Map<String,String> headerMap = new HashMap<>();
        headerMap.put("Authorization","Bearer " +PreferenceConnector.readString(getActivity(), PreferenceConnector.access_token,""));
        headerMap.put("Accept","application/json");

        Map<String, String> map = new HashMap<>();
        map.put("user_id", PreferenceConnector.readString(getActivity(), PreferenceConnector.User_id, ""));
        map.put("register_id", PreferenceConnector.readString(getActivity(), PreferenceConnector.Register_id, ""));

        Call<ResponseBody> loginCall = apiInterface.getNotificationCounterApi(headerMap,map);

        loginCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call,
                                   Response<ResponseBody> response) {
                try {
                    String responseString = response.body().string();
                    JSONObject jsonObject = new JSONObject(responseString);
                    GetCartItem();
                    Log.e(TAG,"get notification counter Response = " + responseString);
                    if(jsonObject.getString("status").equals("1")) {
                        if (!jsonObject.getString("result").equals("0")) {
                            binding.reqcount.setVisibility(View.VISIBLE);
                            binding.reqcount.setText(jsonObject.getString("result"));
                        }
                        else binding.reqcount.setVisibility(View.GONE);
                    } else {
                        // binding.tvNotFound.setVisibility(View.VISIBLE);

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


    private void GetProfile() {
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
                       String g =  getString(R.string.hello);
                        binding.tvNames.setText(g+" "+data.getResult().getUserName());
                     //   Log.e("country name4",countryNames);

                        // lastCountryName
                        if(PreferenceConnector.readString(getActivity(),PreferenceConnector.FROM,"").equalsIgnoreCase("splash")){
                            if(!data.getResult().getCountryName().equals("")) {
                                lastCountryName = data.getResult().getCountryName();
                                lastCountryId =  data.getResult().getCountry();
                                if (countryArrayList.size() > 0) {
                                    for (int i = 0; i < countryArrayList.size(); i++) {
                                        if (countryNames.equals(countryArrayList.get(i).getName())) {
                                              countryId = countryArrayList.get(i).getId();
                                             //lastCountryId = countryArrayList.get(i).getId();
                                            PreferenceConnector.writeString(getActivity(), PreferenceConnector.countryId, lastCountryId);
                                            PreferenceConnector.writeString(getActivity(), PreferenceConnector.countryName, lastCountryName);

                                        }
                                    }
                                }
                            }
                            if(!lastCountryName.equalsIgnoreCase(countryNames))
                            dialogAddLocation(lastCountryName,countryNames,countryId);
                            else {
                                countryNames = data.getResult().getCountryName();
                                Log.e("country name3",countryNames);
                              //  lastCountryName =  data.getResult().getCountryName();
                                binding.address.setText(data.getResult().getCountryName());
                                binding.tvProduct.setText(getString(R.string.latest_product_in)+" " + countryNames );
                                Log.e("countryListSize===",countryArrayList.size()+"");
                                if(countryArrayList.size()>0) {
                                    for (int i = 0; i < countryArrayList.size(); i++) {
                                        if (countryNames.equals(countryArrayList.get(i).getName())) {
                                            countryId = countryArrayList.get(i).getId();
                                           // lastCountryId = countryArrayList.get(i).getId();
                                            PreferenceConnector.writeString(getActivity(), PreferenceConnector.countryId,countryId);
                                            PreferenceConnector.writeString(getActivity(), PreferenceConnector.countryName,countryNames);
                                            getProduct(countryId);
                                        }
                                    }
                                }
                            }
                        }
                        else {
                            if(!data.getResult().getCountryName().equals("")) {
                                countryNames = data.getResult().getCountryName();
                                Log.e("country name4",countryNames);
                                lastCountryName =  data.getResult().getCountryName();
                                binding.address.setText(data.getResult().getCountryName());
                                binding.tvProduct.setText(getString(R.string.latest_product_in)+" " + countryNames );
                                Log.e("countryListSize===",countryArrayList.size()+"");
                                if(countryArrayList.size()>0) {
                                    for (int i = 0; i < countryArrayList.size(); i++) {
                                        if (countryNames.equals(countryArrayList.get(i).getName())) {
                                            countryId = countryArrayList.get(i).getId();
                                            lastCountryId = countryArrayList.get(i).getId();
                                            PreferenceConnector.writeString(getActivity(), PreferenceConnector.countryId,countryId);
                                            PreferenceConnector.writeString(getActivity(), PreferenceConnector.countryName,countryNames);
                                            getProduct(countryId);
                                        }
                                    }
                                }

                            }
                            else {
                                binding.address.setText("" + addresses.get(0).getCountryName());
                                binding.tvProduct.setText(getString(R.string.latest_product_in)+" " + addresses.get(0).getCountryName() );

                                if(countryArrayList.size()>0) {
                                    for (int i = 0; i < countryArrayList.size(); i++) {
                                        if (countryNames.equals(countryArrayList.get(i).getName())) {
                                            countryId = countryArrayList.get(i).getId();
                                            lastCountryId = countryArrayList.get(i).getId();
                                            PreferenceConnector.writeString(getActivity(), PreferenceConnector.countryId,countryId);
                                            PreferenceConnector.writeString(getActivity(), PreferenceConnector.countryName,countryNames);
                                            getProduct(countryId);
                                        }
                                    }
                                }
                            }
                            Log.e("image>>>", data.getResult().image);
                        }


                        getTitle();
                        if(data.getResult().getPasswordRequestStatus().equalsIgnoreCase("CHANGE_BY_ADMIN")) openResetPasswordAlert();
                      //  if(data.getResult().getLanguage().equalsIgnoreCase("en")) changeLocale("en");
                      //  else if(data.getResult().getLanguage().equalsIgnoreCase("fr")) changeLocale("fr");
                     //   else changeLocale("en");

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






    private void openResetPasswordAlert() {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setMessage(getString(R.string.please_change_your_password))
                    .setCancelable(false)
                    .setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                            fragment = new ChangePassword();
                            loadFragment(fragment);
                        }
                    }).setNegativeButton(getString(R.string.no), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });

            AlertDialog alertDialog = builder.create();
            alertDialog.show();

    }

    private void getTitle() {
        Map<String,String> headerMap = new HashMap<>();
        headerMap.put("Authorization","Bearer " +PreferenceConnector.readString(getActivity(), PreferenceConnector.access_token,""));
        headerMap.put("Accept","application/json");

        Map<String, String> map = new HashMap<>();
        map.put("user_id", PreferenceConnector.readString(getActivity(), PreferenceConnector.User_id, ""));
        map.put("register_id", PreferenceConnector.readString(getActivity(), PreferenceConnector.Register_id, ""));

        Call<ResponseBody> loginCall = apiInterface.getHomeTitle(headerMap,map);

        loginCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call,
                                   Response<ResponseBody> response) {
                try {
                    String responseString = response.body().string();
                    JSONObject jsonObject = new JSONObject(responseString);
                    GetCartItem();
                    Log.e(TAG,"getHome Title Response = " + responseString);
                    if(jsonObject.getString("status").equals("1")) {
                        JSONObject jsonObject11 = jsonObject.getJSONObject("result");
                        binding.tvAnnouncements.setText(jsonObject11.getString("description"));
                        binding.tvAnnouncements1.setText(jsonObject11.getString("description2"));
                    } else {
                        // binding.tvNotFound.setVisibility(View.VISIBLE);

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
    public void search(String countryName) {
        if(!countryName.equalsIgnoreCase("")){
            this.countryNames = countryName;
            for (int i =0;i<countryArrayList.size();i++){
                if (countryNames.equals(countryArrayList.get(i).getName()))
                    countryId = countryArrayList.get(i).getId();
            }
            getProduct(countryId);
            PreferenceConnector.writeString(getActivity(), PreferenceConnector.countryId,countryId);
            PreferenceConnector.writeString(getActivity(), PreferenceConnector.countryName,countryName);

            binding.address.setText(countryNames);
            binding.tvProduct.setText(getString(R.string.latest_product_in)+" " + countryNames );

        }
    }




    private boolean checkPermissions() {
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        return false;
    }


    private void requestPermissions() {
        ActivityCompat.requestPermissions(
                getActivity(),
                new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION},
                PERMISSION_ID
        );
    }

    private boolean isLocationEnabled() {
        LocationManager locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
                LocationManager.NETWORK_PROVIDER
        );
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == PERMISSION_ID) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                setCurrentLoc();
            }
        }
    }







    private void setCurrentLoc() {
        gpsTracker = new GPSTracker(getActivity());
        double lat = gpsTracker.getLatitude();
        double lon = gpsTracker.getLongitude();

        Geocoder geocoder = new Geocoder(getContext(), Locale.getDefault());
        try {
            addresses = geocoder.getFromLocation(lat, lon, 1);
            Log.e("addresses>>>", "" + addresses.get(0).getAddressLine(0));



            if(addresses.get(0).getCountryName().equalsIgnoreCase("Inde"))
            {
                countryNames = "India";
            }else {
                countryNames = addresses.get(0).getCountryName();
            }

            Log.e("country name5",countryNames);
            GetProfile();



        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void GetBannerAPi() {
        DataManager.getInstance().showProgressMessage(getActivity(), getString(R.string.please_wait));
        Map<String,String> headerMap = new HashMap<>();
        headerMap.put("Authorization","Bearer " +PreferenceConnector.readString(getActivity(), PreferenceConnector.access_token,""));
        headerMap.put("Accept","application/json");
        Map<String, String> map = new HashMap<>();
        map.put("user_id", PreferenceConnector.readString(getContext(), PreferenceConnector.User_id, ""));
        map.put("register_id", PreferenceConnector.readString(getActivity(), PreferenceConnector.Register_id, ""));
        map.put("type","Home1");

        if(!lastCountryName.equalsIgnoreCase("")){
            map.put("country_id",countryNames);
            Log.e("country name1=====",countryNames);
        }

            //   map.put("country_id", lastCountryName);
       else{
           map.put("country_id", addresses.get(0).getCountryName()+"");
            Log.e("country name2=====",addresses.get(0).getCountryName()+"");

        }
        Log.e("MapMap", "EXERSICE LIST" + map);
        Call<BannerModal1> loginCall = apiInterface.get_slider(headerMap,map);

        loginCall.enqueue(new Callback<BannerModal1>() {

            @Override
            public void onResponse(Call<BannerModal1> call, Response<BannerModal1> response) {
                DataManager.getInstance().hideProgressMessage();

                try {

                    BannerModal1 data = response.body();
                    String dataResponse = new Gson().toJson(response.body());
                    Log.e("MapMap", "Exersice_List" + dataResponse);
                    GetBannerAPi2();
                    if (data.status.equals("1")) {

                        get_result1.clear();
                        get_result1.addAll(data.getResult());
                        banner_array_list.clear();
                        binding.imageSlider.setVisibility(View.VISIBLE);
                        for (int i = 0; get_result1.size() > i; i++) {
                            banner_array_list.add(get_result1.get(i).image);

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

                        }


                    } else if (data.status.equals("0")) {
                        banner_array_list.clear();
                        binding.imageSlider.dataSetChanged();
                        binding.imageSlider.setVisibility(View.GONE);

                    }

                    else if (data.status.equals("5")) {
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
            public void onFailure(Call<BannerModal1> call, Throwable t) {
                call.cancel();
                DataManager.getInstance().hideProgressMessage();
            }
        });
    }

    private void GetBannerAPi2() {
        DataManager.getInstance().showProgressMessage(getActivity(), getString(R.string.please_wait));
        Map<String,String> headerMap = new HashMap<>();
        headerMap.put("Authorization","Bearer " +PreferenceConnector.readString(getActivity(), PreferenceConnector.access_token,""));
        headerMap.put("Accept","application/json");
        Map<String, String> map = new HashMap<>();
        map.put("user_id", PreferenceConnector.readString(getContext(), PreferenceConnector.User_id, ""));
        map.put("register_id", PreferenceConnector.readString(getActivity(), PreferenceConnector.Register_id, ""));
        map.put("type","Home2");

        if(!countryNames.equalsIgnoreCase("")) map.put("country_id", countryNames);
        else map.put("country_id", addresses.get(0).getCountryName()+"");
        Log.e("MapMap", "EXERSICE LIST" + map);
        Call<BannerModal1> loginCall = apiInterface.get_slider(headerMap,map);

        loginCall.enqueue(new Callback<BannerModal1>() {

            @Override
            public void onResponse(Call<BannerModal1> call, Response<BannerModal1> response) {
                DataManager.getInstance().hideProgressMessage();

                try {

                    BannerModal1 data = response.body();
                    String dataResponse = new Gson().toJson(response.body());
                    Log.e("MapMap", "Exersice_List" + dataResponse);

                    if (data.status.equals("1")) {

                        get_result1.clear();
                        get_result1.addAll(data.getResult());
                        banner_array_list1.clear();
                        binding.imageSlider2.setVisibility(View.VISIBLE);
                        for (int i = 0; get_result1.size() > i; i++) {
                            banner_array_list1.add(get_result1.get(i).image);

                            adapter2 = new SliderAdapterExample(getContext(), banner_array_list1);
                            binding.imageSlider2.setSliderAdapter(adapter2);
                            binding.imageSlider2.setIndicatorAnimation(IndicatorAnimationType.WORM); //set indicator animation by using SliderLayout.IndicatorAnimations. :WORM or THIN_WORM or COLOR or DROP or FILL or NONE or SCALE or SCALE_DOWN or SLIDE and SWAP!!
                            binding.imageSlider2.setSliderTransformAnimation(SliderAnimations.SIMPLETRANSFORMATION);
                            binding.imageSlider2.setAutoCycleDirection(SliderView.AUTO_CYCLE_DIRECTION_BACK_AND_FORTH);
                        //    binding.imageSlider2.setIndicatorSelectedColor(R.color.colorPrimary);
                        //    binding.imageSlider2.setIndicatorUnselectedColor(Color.GRAY);
                            binding.imageSlider2.setScrollTimeInSec(3);
                            binding.imageSlider2.setAutoCycle(true);
                            binding.imageSlider2.startAutoCycle();
                        }


                    } else if (data.status.equals("0")) {
                        banner_array_list1.clear();
                        binding.imageSlider2.dataSetChanged();
                        binding.imageSlider2.setVisibility(View.GONE);

                        // Toast.makeText(getContext(), data.message, Toast.LENGTH_SHORT).show();
                    }

                    else if (data.status.equals("5")) {
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
            public void onFailure(Call<BannerModal1> call, Throwable t) {
                call.cancel();
                DataManager.getInstance().hideProgressMessage();
            }
        });
    }



    private void SetupUI() {

        GetCategoryAPi();

        binding.layNoti.setOnClickListener(v -> {
            startActivity(new Intent(getActivity(), NotificationScreen.class));
        });

        adapter = new CategoryAdapter(getActivity(), get_result);
        binding.categoryList.setLayoutManager(new GridLayoutManager(getContext(), 4));
        binding.categoryList.setAdapter(adapter);

        binding.recyclerBannerMain.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, true));
        binding.recyclerBannerMain.setAdapter(mAdapterBanner);

    }

    private void GetCategoryAPi() {

        DataManager.getInstance().showProgressMessage(getActivity(), getString(R.string.please_wait));
        Map<String,String> headerMap = new HashMap<>();
        headerMap.put("Authorization","Bearer " +PreferenceConnector.readString(getActivity(), PreferenceConnector.access_token,""));
        headerMap.put("Accept","application/json");

        Map<String, String> map = new HashMap<>();
        map.put("user_id", PreferenceConnector.readString(getContext(), PreferenceConnector.User_id, ""));
        map.put("register_id", PreferenceConnector.readString(getActivity(), PreferenceConnector.Register_id, ""));

        Log.e("MapMap", "EXERSICE LIST" + map);
        Call<CategoryModal> loginCall = apiInterface.get_category(headerMap,map);
        loginCall.enqueue(new Callback<CategoryModal>() {

            @Override
            public void onResponse(Call<CategoryModal> call, Response<CategoryModal> response) {
                DataManager.getInstance().hideProgressMessage();

                try {
                    CategoryModal data = response.body();
                    String dataResponse = new Gson().toJson(response.body());
                    Log.e("MapMap", "Exersice_List" + dataResponse);

                    if (data.status.equals("1")) {

                        get_result.clear();
                        get_result.addAll(data.getResult());
                        adapter.notifyDataSetChanged();

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
            public void onFailure(Call<CategoryModal> call, Throwable t) {
                call.cancel();
                DataManager.getInstance().hideProgressMessage();
            }
        });
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


    private void getProduct(String countryId) {
       Map<String,String>map = new HashMap<>();
        Map<String,String> headerMap = new HashMap<>();
        headerMap.put("Authorization","Bearer " +PreferenceConnector.readString(getActivity(), PreferenceConnector.access_token,""));
        headerMap.put("Accept","application/json");
       // map.put("user_id",PreferenceConnector.readString(getContext(), PreferenceConnector.User_id, ""));

        map.put("user_id", PreferenceConnector.readString(getActivity(), PreferenceConnector.User_id, ""));
        map.put("country_id",countryId);
        map.put("register_id", PreferenceConnector.readString(getActivity(), PreferenceConnector.Register_id, ""));

        Log.e(TAG,"getProduct Search Request = "+map.toString());
        Call<ResponseBody> call = apiInterface.getAllProduct(headerMap,map);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                try {
                    String responseString = response.body().string();
                    JSONObject jsonObject = new JSONObject(responseString);
                    GetBannerAPi();
                    Log.e(TAG,"getProduct Search Response = " + responseString);
                    if(jsonObject.getString("status").equals("1")) {
                        binding.tvProduct.setVisibility(View.VISIBLE);
                        ProductItemModel model = new Gson().fromJson(responseString, ProductItemModel.class);
                        arrayList.clear();
                        arrayList.addAll(model.getResult());
                        adapterSearch = new ProductAdapter2(getActivity(), arrayList,arrayList.size());
                        binding.recyclernearme.setAdapter(adapterSearch);
                        adapterSearch.notifyDataSetChanged();
                      //  binding.tvNotFound.setVisibility(View.GONE);

                    }

                    else if (jsonObject.getString("status").equals("5")) {
                        PreferenceConnector.writeString(getActivity(), PreferenceConnector.LoginStatus, "false");
                        startActivity(new Intent(getActivity(), Splash.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP));
                        getActivity().finish();

                    }


                    else {
                        arrayList.clear();
                        adapterSearch = new ProductAdapter2(getActivity(), arrayList,arrayList.size());
                        binding.recyclernearme.setAdapter(adapterSearch);
                        adapterSearch.notifyDataSetChanged();
                        binding.tvProduct.setVisibility(View.GONE);

                        // binding.tvNotFound.setVisibility(View.VISIBLE);

                    }

                   // GetBannerAPi();


                } catch (Exception e) {
                    // Toast.makeText(mContext, "Exception = " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.e("Exception","Exception = " + e.getMessage());
                    GetBannerAPi();
                }

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                //binding.swiperRefresh.setRefreshing(false);
            }
        });
    }


    private void getAllCountry() {
        DataManager.getInstance().showProgressMessage(getActivity(), getString(R.string.please_wait));
        Map<String,String> headerMap = new HashMap<>();
        headerMap.put("Authorization","Bearer " +PreferenceConnector.readString(getActivity(), PreferenceConnector.access_token,""));
        headerMap.put("Accept","application/json");

        Map<String,String> map = new HashMap<>();


        map.put("register_id", PreferenceConnector.readString(getActivity(), PreferenceConnector.Register_id, ""));

        Call<ResponseBody> chatCount = apiInterface.getAllCountry(headerMap);
        chatCount.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                DataManager.getInstance().hideProgressMessage();
                try {
                    String responseData = response.body() != null ? response.body().string() : "";
                    JSONObject object = new JSONObject(responseData);
                    Log.e(TAG, "Get All Country RESPONSE" + object);

                    if (object.optString("status").equals("1")) {
                        CountryModel data = new Gson().fromJson(responseData, CountryModel.class);
                        countryArrayList.clear();
                        countryArrayList.addAll(data.getResult());
                        setCurrentLoc();
                       /* if (checkPermissions()) {
                            if (isLocationEnabled()) { setCurrentLoc();}
                            else {
                                Toast.makeText(getActivity(), "Turn on location", Toast.LENGTH_LONG).show();
                                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                startActivity(intent);

                            }
                        } else {
                            requestPermissions();
                        }*/
                    } else if (object.optString("status").equals("0")) {
                        countryArrayList.clear();

                    }

                    else if (object.optString("status").equals("5")) {
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


    private void GetCartItem() {

        // DataManager.getInstance().showProgressMessage(getActivity(), "Please wait...");
        Map<String,String> headerMap = new HashMap<>();
        headerMap.put("Authorization","Bearer " +PreferenceConnector.readString(getActivity(), PreferenceConnector.access_token,""));
        headerMap.put("Accept","application/json");
        Map<String, String> map = new HashMap<>();
        map.put("user_id", PreferenceConnector.readString(getContext(), PreferenceConnector.User_id, ""));
        map.put("register_id", PreferenceConnector.readString(getActivity(), PreferenceConnector.Register_id, ""));

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
                        get_result11.clear();
                        get_result11.addAll(data.getResult());

                        if(get_result.size()>0){
                            binding.llicon.setVisibility(View.VISIBLE);
                            binding.reqcountCart.setVisibility(View.VISIBLE);
                            binding.reqcountCart.setText(get_result11.size()+"");
                        }else {
                            binding.llicon.setVisibility(View.VISIBLE);
                            binding.reqcountCart.setVisibility(View.GONE);

                        }

                    } else if (data.status.equals("0")) {
                       // Toast.makeText(getContext(), "No Data Found !!!!", Toast.LENGTH_SHORT).show();
                        get_result11.clear();
                        binding.llicon.setVisibility(View.VISIBLE);
                        binding.reqcountCart.setVisibility(View.GONE);


                    }

                    else if (data.status.equals("5")) {
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
            public void onFailure(Call<CartModal> call, Throwable t) {
                call.cancel();
                DataManager.getInstance().hideProgressMessage();
            }
        });
    }





    private void dialogAddLocation(String countryName, String countryNameNew,String countryId) {
        Dialog mDialog = new Dialog(getActivity());
        mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mDialog.setContentView(R.layout.dialog_set_country);
        // mDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        TextView textView = mDialog.findViewById(R.id.tvTitle);
        TextView tvYes = mDialog.findViewById(R.id.tvYes);
        TextView tvBack = mDialog.findViewById(R.id.tvBack);



        if(countryName.equalsIgnoreCase(""))
        {

            for (int i = 0; i < countryArrayList.size(); i++) {
                if (countryNameNew.equals(countryArrayList.get(i).getName())) {
                    lastCountryId = countryArrayList.get(i).getId();

                    PreferenceConnector.writeString(getActivity(), PreferenceConnector.countryId,lastCountryId);
                    PreferenceConnector.writeString(getActivity(), PreferenceConnector.countryName,countryName);
                    getProduct(countryId);
                }
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
                textView.setText(Html.fromHtml(getString(R.string.your_current_location_set_to)+" " + "<font color='#EE0000'>" + countryNameNew+ "</font>" + "<br>" + "<br>" + getString(R.string.do_you_maintain) + " "+ "<font color='#EE0000'>"  + countryNameNew + "</font>" + "?",Html.FROM_HTML_MODE_LEGACY));
           else  textView.setText(Html.fromHtml(getString(R.string.your_current_location_set_to)+" " + "<font color='#EE0000'>" + countryNameNew+ "</font>" + "<br>" + "<br>" + getString(R.string.do_you_maintain) + " "+ "<font color='#EE0000'>"  + countryNameNew + "</font>" + "?"));
        }

         else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
                textView.setText(Html.fromHtml(getString(R.string.your_location_is) + " " + "<font color='#EE0000'>" + countryNameNew + "</font>" + " " + getString(R.string.has_changes_to) + "<br>" + "<br>" + getString(R.string.your_current_location_is_set_to) + " " + "<font color='#EE0000'>" + countryName + "</font>" + "<br>" + "<br>"+getString(R.string.do_you_want_to_maintain_your_location_on)+" "
                        + "<font color='#EE0000'>" + countryName + "</font>" + "?",Html.FROM_HTML_MODE_LEGACY));
           else textView.setText(Html.fromHtml(getString(R.string.your_location_is) + " " + "<font color='#EE0000'>" + countryNameNew + "</font>" + " " + getString(R.string.has_changes_to) + "<br>" + "<br>" + getString(R.string.your_current_location_is_set_to) + " " + "<font color='#EE0000'>" + countryName + "</font>" + "<br>" + "<br>" +getString(R.string.do_you_want_to_maintain_your_location_on)+" "
                    + "<font color='#EE0000'>" + countryName + "</font>" + "?"));

        }


        WindowManager.LayoutParams lp = mDialog.getWindow().getAttributes();
        lp.dimAmount = 0.8f;
        mDialog.getWindow().setAttributes(lp);
        mDialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        mDialog.setCancelable(false);
        mDialog.setCanceledOnTouchOutside(false);



        tvBack.setOnClickListener(v -> {
            mDialog.dismiss();
            updateCountry(countryId,countryNameNew);
        });

        tvYes.setOnClickListener(v -> {
            mDialog.dismiss();
           if(!countryName.equalsIgnoreCase("")) updateCountry(lastCountryId,countryName);
           else updateCountry(lastCountryId,countryNameNew);

        });


        mDialog.show();

    }


    private void updateCountry(String countryId,String country) {
        DataManager.getInstance().showProgressMessage(getActivity(), getString(R.string.please_wait));
        Map<String,String> headerMap = new HashMap<>();
        headerMap.put("Authorization","Bearer " +PreferenceConnector.readString(getActivity(), PreferenceConnector.access_token,""));
        headerMap.put("Accept","application/json");

        Map<String, String> map = new HashMap<>();
        map.put("user_id", PreferenceConnector.readString(getActivity(), PreferenceConnector.User_id, ""));
        map.put("country", countryId);
        map.put("register_id", PreferenceConnector.readString(getActivity(), PreferenceConnector.Register_id, ""));
        Log.e("update country request==="+country,map.toString());

        Call<ResponseBody> loginCall = apiInterface.updateCountryApi(headerMap,map);

        loginCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call,
                                   Response<ResponseBody> response) {
                DataManager.getInstance().hideProgressMessage();
                try {
                    String responseString = response.body().string();
                    JSONObject jsonObject = new JSONObject(responseString);
                    Log.e(TAG,"update country  Response = " + responseString);
                    if(jsonObject.getString("status").equals("1")) {
                        JSONObject jsonObject11 = jsonObject.getJSONObject("result");
                      //  listener.search(country);
                       // dialog.dismiss();
                        binding.address.setText(country);
                        countryNames = country;
                        binding.tvProduct.setVisibility(View.VISIBLE);
                        binding.tvProduct.setText(getString(R.string.latest_product_in)+" " + country );
                        PreferenceConnector.writeString(getActivity(), PreferenceConnector.FROM, "");
                        PreferenceConnector.writeString(getActivity(), PreferenceConnector.countryName,country);
                        PreferenceConnector.writeString(getActivity(), PreferenceConnector.countryId,countryId);

                        getProduct(countryId);
                    }

                    else if (jsonObject.getString("status").equals("0")) {


                    }
                    else if (jsonObject.getString("status").equals("5")) {
                        PreferenceConnector.writeString(getActivity(), PreferenceConnector.LoginStatus, "false");
                        startActivity(new Intent(getActivity(), Splash.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP));
                        getActivity().finish();
                    }

                    else {
                        // binding.tvNotFound.setVisibility(View.VISIBLE);

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
    public void onResume() {
        super.onResume();
        getNotificationCounter();
    }
}