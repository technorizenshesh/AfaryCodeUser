package com.my.afarycode.OnlineShopping.fragment;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.my.afarycode.OnlineShopping.AllShopOnlineActivity;
import com.my.afarycode.OnlineShopping.HomeActivity;
import com.my.afarycode.OnlineShopping.HomeShoppingOnlineScreen;
import com.my.afarycode.OnlineShopping.LoginActivity;
import com.my.afarycode.OnlineShopping.Model.BannerModal1;
import com.my.afarycode.OnlineShopping.Model.CartModal;
import com.my.afarycode.OnlineShopping.Model.CategoryModal;
import com.my.afarycode.OnlineShopping.Model.CountryModel;
import com.my.afarycode.OnlineShopping.Model.GetProfileModal;
import com.my.afarycode.OnlineShopping.Model.HomeOfferModel;
import com.my.afarycode.OnlineShopping.Model.HomeShopeProductModel;
import com.my.afarycode.OnlineShopping.Model.ProductItemModel;
import com.my.afarycode.OnlineShopping.Model.SliderItem;
import com.my.afarycode.OnlineShopping.NotificationScreen;
import com.my.afarycode.OnlineShopping.ProductListAct;
import com.my.afarycode.OnlineShopping.SearchScreen;
import com.my.afarycode.OnlineShopping.activity.CardAct;
import com.my.afarycode.OnlineShopping.activity.CheckOutDeliveryAct;
import com.my.afarycode.OnlineShopping.adapter.AdapterSearch;
import com.my.afarycode.OnlineShopping.adapter.BannerAdapter1;
import com.my.afarycode.OnlineShopping.adapter.CategoryAdapter;
import com.my.afarycode.OnlineShopping.adapter.HomeBannerAdapter;
import com.my.afarycode.OnlineShopping.adapter.HomeTopOfferAdapter;
import com.my.afarycode.OnlineShopping.adapter.ProductAdapter;
import com.my.afarycode.OnlineShopping.adapter.ProductAdapter2;
import com.my.afarycode.OnlineShopping.adapter.SliderAdapterExample;
import com.my.afarycode.OnlineShopping.bottomsheet.CountryBottomSheet;
import com.my.afarycode.OnlineShopping.constant.PreferenceConnector;
import com.my.afarycode.OnlineShopping.helper.DataManager;
import com.my.afarycode.OnlineShopping.listener.SearchListener;
import com.my.afarycode.OnlineShopping.listener.onItemClickListener;
import com.my.afarycode.OnlineShopping.servercommunication.GPSTracker;
import com.my.afarycode.R;
import com.my.afarycode.databinding.HomeFragmentBinding;
import com.my.afarycode.ratrofit.AfaryCode;
import com.my.afarycode.ratrofit.ApiClient;
import com.smarteist.autoimageslider.IndicatorView.animation.type.IndicatorAnimationType;
import com.smarteist.autoimageslider.SliderAnimations;
import com.smarteist.autoimageslider.SliderView;
import com.squareup.picasso.Picasso;

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
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

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
    private String countryNames="",countryId="";

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.home_fragment, container, false);
        apiInterface = ApiClient.getClient(getContext()).create(AfaryCode.class);
        countryArrayList = new ArrayList<>();

        SetupUI();
        getAllCountry();

       /* scheduleTaskExecutor = Executors.newScheduledThreadPool(5);
        scheduleTaskExecutor.scheduleAtFixedRate(new Runnable() {
            public void run() {
                new MyCounterVal().execute();
            }
        }, 0, 8, TimeUnit.SECONDS);*/



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
            if(!countryId.equalsIgnoreCase(""))startActivity(new Intent(getActivity(), ProductListAct.class)
                    .putExtra("countryId",countryId));
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


    private void GetProfile() {
        Map<String, String> map = new HashMap<>();
        map.put("user_id", PreferenceConnector.readString(getActivity(), PreferenceConnector.User_id, ""));
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
                        binding.tvNames.setText("Hello, "+data.getResult().getUserName());
                       if(!data.getResult().getCountryName().equals("")) {
                           countryNames = data.getResult().getCountryName();
                           binding.address.setText(data.getResult().getCountryName());
                           binding.tvProduct.setText("Latest Products in " + countryNames );
                           Log.e("countryListSize===",countryArrayList.size()+"");
                           if(countryArrayList.size()>0) {
                               for (int i = 0; i < countryArrayList.size(); i++) {
                                   if (countryNames.equals(countryArrayList.get(i).getName())) {
                                       countryId = countryArrayList.get(i).getId();
                                       PreferenceConnector.writeString(getActivity(), PreferenceConnector.countryId,countryId);
                                       PreferenceConnector.writeString(getActivity(), PreferenceConnector.countryName,countryNames);
                                       getProduct(countryId);
                                   }
                               }
                           }

                       }
                       else {
                           if(countryArrayList.size()>0) {
                               for (int i = 0; i < countryArrayList.size(); i++) {
                                   if (countryNames.equals(countryArrayList.get(i).getName())) {
                                       countryId = countryArrayList.get(i).getId();
                                       PreferenceConnector.writeString(getActivity(), PreferenceConnector.countryId,countryId);
                                       PreferenceConnector.writeString(getActivity(), PreferenceConnector.countryName,countryNames);
                                       getProduct(countryId);
                                   }
                               }
                           }
                       }
                        Log.e("image>>>", data.getResult().image);
                        getTitle();
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

    private void getTitle() {
        Map<String,String> headerMap = new HashMap<>();
        headerMap.put("Authorization","Bearer " +PreferenceConnector.readString(getActivity(), PreferenceConnector.access_token,""));
        headerMap.put("Accept","application/json");
        Call<ResponseBody> loginCall = apiInterface.getHomeTitle(headerMap);

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
            binding.tvProduct.setText("Latest Products in " + countryNames );

        }
    }


    private class MyCounterVal extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            try {
                super.onPreExecute();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        protected String doInBackground(String... strings) {
            try {
                String postReceiverUrl = "https://technorizen.com/afarycode/webservice/notifi_count";
                URL url = new URL(postReceiverUrl);
                Map<String, Object> params = new LinkedHashMap<>();
                params.put("user_id", PreferenceConnector.readString(getContext(), PreferenceConnector.User_id, ""));
                StringBuilder postData = new StringBuilder();
                for (Map.Entry<String, Object> param : params.entrySet()) {
                    if (postData.length() != 0) postData.append('&');
                    postData.append(URLEncoder.encode(param.getKey(), "UTF-8"));
                    postData.append('=');
                    postData.append(URLEncoder.encode(String.valueOf(param.getValue()), "UTF-8"));
                }
                String urlParameters = postData.toString();
                URLConnection conn = url.openConnection();
                conn.setDoOutput(true);
                OutputStreamWriter writer = new OutputStreamWriter(conn.getOutputStream());
                writer.write(urlParameters);
                writer.flush();
                String response = "";
                String line;
                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                while ((line = reader.readLine()) != null) {
                    response += line;
                }
                writer.close();
                reader.close();
                Log.e("MainTabCounter Hire", ">>>>>>>>>>>>" + response);
                return response;
            } catch (UnsupportedEncodingException e1) {
                e1.printStackTrace();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if (result == null) {
            } else if (result.isEmpty()) {
            } else {
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    if (jsonObject.getString("status").equalsIgnoreCase("1")) {
                        int message_unseen_count = 0;

                        result1 = jsonObject.getJSONObject("result");
                        counter_message_int = result1.getString("count");


                        //impliment by sagar panse //

                      /*  String delete_status = jsonObject.getString("delete_status");

                        if (delete_status.equalsIgnoreCase("Deactive")) {
                            mySession.signinusers(false);
                            mySession.logoutUser();
                            Intent i = new Intent(MainTabActivity.this, AccountTypeSelectionAct.class);
                            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            i.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                            startActivity(i);
                        }*/

                        //impliment by sagar panse //

               /*         notification_unseen_count = jsonObject.getString("notification_unseen_count");
                        cart_unseen_count = jsonObject.getString("cart_count");*/

            /*            Log.e("notification >> ", " >> " + notification_unseen_count);
                        Intent j = new Intent("Unseen Count");
                        j.putExtra("noticount", notification_unseen_count);
                        j.putExtra("cartcount", cart_unseen_count);
                        // j.putExtra("ngcash", ngcash);

                        getActivity().sendBroadcast(j);*/

                        if (counter_message_int == null || counter_message_int.equalsIgnoreCase("")) {

                        } else {
                            message_unseen_count = Integer.parseInt(counter_message_int);
                        }

                        if (message_unseen_count != 0) {

                            binding.reqcount.setText("" + counter_message_int);
                            binding.reqcount.setVisibility(View.VISIBLE);
                        } else {
                            binding.reqcount.setVisibility(View.GONE);
                        }

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
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

            binding.address.setText("" + addresses.get(0).getCountryName());
            binding.tvProduct.setText("Latest Products in " + addresses.get(0).getCountryName() );
            countryNames = addresses.get(0).getCountryName();
            GetProfile();



        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void GetBannerAPi() {
        DataManager.getInstance().showProgressMessage(getActivity(), "Please wait...");
        Map<String,String> headerMap = new HashMap<>();
        headerMap.put("Authorization","Bearer " +PreferenceConnector.readString(getActivity(), PreferenceConnector.access_token,""));
        headerMap.put("Accept","application/json");
        Map<String, String> map = new HashMap<>();
        map.put("user_id", PreferenceConnector.readString(getContext(), PreferenceConnector.User_id, ""));
      if(!countryNames.equalsIgnoreCase(""))
          map.put("country_id", countryNames);
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
                        banner_array_list.clear();
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
                        Toast.makeText(getContext(), data.message, Toast.LENGTH_SHORT).show();
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
        DataManager.getInstance().showProgressMessage(getActivity(), "Please wait...");
        Map<String,String> headerMap = new HashMap<>();
        headerMap.put("Authorization","Bearer " +PreferenceConnector.readString(getActivity(), PreferenceConnector.access_token,""));
        headerMap.put("Accept","application/json");
        Map<String, String> map = new HashMap<>();
        map.put("user_id", PreferenceConnector.readString(getContext(), PreferenceConnector.User_id, ""));
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
                        Toast.makeText(getContext(), data.message, Toast.LENGTH_SHORT).show();
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





        binding.reqcount.setOnClickListener(v -> {
            startActivity(new Intent(getActivity(), NotificationScreen.class));
        });

        adapter = new CategoryAdapter(getActivity(), get_result);
        binding.categoryList.setLayoutManager(new GridLayoutManager(getContext(), 4));
        binding.categoryList.setAdapter(adapter);

        binding.recyclerBannerMain.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, true));
        binding.recyclerBannerMain.setAdapter(mAdapterBanner);

    }

    private void GetCategoryAPi() {

        DataManager.getInstance().showProgressMessage(getActivity(), "Please wait...");
        Map<String,String> headerMap = new HashMap<>();
        headerMap.put("Authorization","Bearer " +PreferenceConnector.readString(getActivity(), PreferenceConnector.access_token,""));
        headerMap.put("Accept","application/json");

        Map<String, String> map = new HashMap<>();
        map.put("user_id", PreferenceConnector.readString(getContext(), PreferenceConnector.User_id, ""));
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


        map.put("country_id",countryId);
        Log.e(TAG,"getProduct Search Request = "+map.toString());
        Call<ResponseBody> call = apiInterface.getAllProduct(headerMap,map);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                try {
                    String responseString = response.body().string();
                    JSONObject jsonObject = new JSONObject(responseString);
                    Log.e(TAG,"getProduct Search Response = " + responseString);
                    if(jsonObject.getString("status").equals("1")) {
                        ProductItemModel model = new Gson().fromJson(responseString, ProductItemModel.class);
                        arrayList.clear();
                        arrayList.addAll(model.getResult());
                        adapterSearch = new ProductAdapter2(getActivity(), arrayList,arrayList.size());
                        binding.recyclernearme.setAdapter(adapterSearch);
                        adapterSearch.notifyDataSetChanged();
                      //  binding.tvNotFound.setVisibility(View.GONE);

                    } else {
                        arrayList.clear();
                        adapterSearch.notifyDataSetChanged();
                       // binding.tvNotFound.setVisibility(View.VISIBLE);

                    }

                    GetBannerAPi();
                    GetBannerAPi2();

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


    private void getAllCountry() {
        DataManager.getInstance().showProgressMessage(getActivity(), getString(R.string.please_wait));
        Map<String,String> headerMap = new HashMap<>();
        headerMap.put("Authorization","Bearer " +PreferenceConnector.readString(getActivity(), PreferenceConnector.access_token,""));
        headerMap.put("Accept","application/json");
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

                        if (checkPermissions()) {
                            if (isLocationEnabled()) { setCurrentLoc();}
                            else {
                                Toast.makeText(getActivity(), "Turn on location", Toast.LENGTH_LONG).show();
                                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                startActivity(intent);

                            }
                        } else {
                            requestPermissions();
                        }
                    } else if (object.optString("status").equals("0")) {
                        countryArrayList.clear();

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
                            binding.reqcountCart.setVisibility(View.VISIBLE);
                            binding.reqcountCart.setText(get_result11.size()+"");
                        }else {
                            binding.reqcountCart.setVisibility(View.GONE);

                        }

                    } else if (data.status.equals("0")) {
                       // Toast.makeText(getContext(), "No Data Found !!!!", Toast.LENGTH_SHORT).show();
                        get_result11.clear();
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



}