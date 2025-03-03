package com.my.afarycode.OnlineShopping.bottomsheet;

import static android.app.Activity.RESULT_OK;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.PopupMenu;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.databinding.DataBindingUtil;

import com.google.android.gms.common.api.Status;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.gson.Gson;
import com.my.afarycode.OnlineShopping.Model.CityModel;
import com.my.afarycode.OnlineShopping.Model.CountryModel;
import com.my.afarycode.OnlineShopping.Model.LocationModel;
import com.my.afarycode.OnlineShopping.Model.StateModel;
import com.my.afarycode.OnlineShopping.activity.CheckOutDeliveryAct;
import com.my.afarycode.OnlineShopping.constant.PreferenceConnector;
import com.my.afarycode.OnlineShopping.fragment.AddAddressFragment;
import com.my.afarycode.OnlineShopping.helper.DataManager;
import com.my.afarycode.OnlineShopping.helper.NetworkAvailablity;
import com.my.afarycode.OnlineShopping.listener.addAddressListener;
import com.my.afarycode.R;
import com.my.afarycode.Splash;
import com.my.afarycode.databinding.FragmentAddAddressBinding;
import com.my.afarycode.ratrofit.AfaryCode;
import com.my.afarycode.ratrofit.ApiClient;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditAddressFragment extends BottomSheetDialogFragment {
    public String TAG = "AddAddressFragment";
    BottomSheetDialog dialog;
    private BottomSheetBehavior<View> mBehavior;
    FragmentAddAddressBinding binding;
    public static TextView tvArea, tv1,tvCompleteadd;
    public static View v1;
    double latitude = 0.0, longitude = 0.0;
    int AUTOCOMPLETE_REQUEST_CODE_ADDRESS = 101;
    AfaryCode apiInterface;
    String address = "", city = "",addressType="",countryId="",stateId="",countryName="",cityId="";
    addAddressListener listener;
    LocationModel.Result result;

    ArrayList<CountryModel.Result> countryArrayList;
    ArrayList<StateModel.Result> stateArrayList;
    ArrayList<CityModel.Result> cityArrayList;


    public EditAddressFragment(LocationModel.Result result) {
        this.result = result;
    }

    public EditAddressFragment callBack(addAddressListener listener) {
        this.listener = listener;
        return this;
    }


    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        dialog = (BottomSheetDialog) super.onCreateDialog(savedInstanceState);
        binding = DataBindingUtil.inflate(LayoutInflater.from(getActivity()), R.layout.fragment_add_address, null, false);
        dialog.setContentView(binding.getRoot());
        apiInterface = ApiClient.getClient(getActivity()).create(AfaryCode.class);
        mBehavior = BottomSheetBehavior.from((View) binding.getRoot().getParent());
        mBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        initBinding();
        return dialog;
    }

    private void initBinding() {
        tvArea = dialog.findViewById(R.id.tvArea);
        tv1 = dialog.findViewById(R.id.tv1);
        v1 = dialog.findViewById(R.id.View1);
        tvCompleteadd = dialog.findViewById(R.id.tvCompleteadd);

        binding.tvAddressTitle.setText(getString(R.string.update_billing_address));

        countryArrayList = new ArrayList<>();
        stateArrayList = new ArrayList<>();
        cityArrayList = new ArrayList<>();

        binding.etTitle.setText(result.getAddressName());
        binding.etFname.setText(result.getFirstName());
        binding.etLname.setText(result.getLastName());
        binding.etEmail.setText(result.getEmail());
        binding.etMobile.setText(result.getPhone());
        tvCompleteadd.setText(result.getAddress());
        binding.etCountry.setText(result.getCountryName());
       // binding.etCountry.setText(result.getState());

        binding.etPostCode.setText(result.getPostcode());
        binding.etTown.setText(result.getCity());
        address = result.getAddress();
        countryId = result.getCountry();
        stateId = result.getState();
        cityId = result.getCity();

        Log.e("STATE id===",stateId);


        if(result.getType().equalsIgnoreCase("deliver_to_my_home")) {
            addressType = "deliver_to_my_home";

            binding.rdMyHome.setChecked(true);
            binding.rdMyOffice.setChecked(false);
            binding.rdAnotherPerson.setChecked(false);
            binding.rdWhereIAm.setChecked(false);

        }
        else if(result.getType().equalsIgnoreCase("deliver_to_my_office")) {
            addressType = "deliver_to_my_office";

            binding.rdMyHome.setChecked(false);
            binding.rdMyOffice.setChecked(true);
            binding.rdAnotherPerson.setChecked(false);
            binding.rdWhereIAm.setChecked(false);

        }
        else if(result.getType().equalsIgnoreCase("deliver_to_another_person")) {
            addressType = "deliver_to_another_person";

            binding.rdMyHome.setChecked(false);
            binding.rdMyOffice.setChecked(false);
            binding.rdAnotherPerson.setChecked(true);
            binding.rdWhereIAm.setChecked(false);

        }
        else if(result.getType().equalsIgnoreCase("deliver_where_i_am_now")) {
            addressType = "deliver_where_i_am_now";

            binding.rdMyHome.setChecked(false);
            binding.rdMyOffice.setChecked(false);
            binding.rdAnotherPerson.setChecked(false);
            binding.rdWhereIAm.setChecked(true);

        }



        if (!Places.isInitialized()) {
            Places.initialize(getActivity(), getString(R.string.place_api_key));
        }


        binding.btnAdd.setOnClickListener(v -> validation());

        //  binding.btnPin.setOnClickListener(v -> startActivity(new Intent(getActivity(),PinAddressAct.class)));


        binding.tvCompleteadd.setOnClickListener(v -> {
            List<Place.Field> fields = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG, Place.Field.ADDRESS);

            // Start the autocomplete intent.
            Intent intent = new Autocomplete.IntentBuilder(
                    AutocompleteActivityMode.FULLSCREEN, fields)
                    //.setCountry("SA")
                    .build(getActivity());

            startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE_ADDRESS);
        });


        binding.rdGrp.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener()
        {
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch(checkedId){
                    case R.id.rdMyHome:
                        addressType = "deliver_to_my_home";
                        break;
                    case R.id.rdMyOffice:
                        addressType = "deliver_to_my_office";
                        break;
                    case R.id.rdAnotherPerson:
                        addressType = "deliver_to_another_person";
                        break;
                    case R.id.rdWhereIAm:
                        addressType = "deliver_where_i_am_now";
                        break;

                }
            }
        });


       /* binding.etCountry.setOnClickListener(v -> {
            if (countryArrayList.size() > 0)
                showDropDownCountry(v, binding.etCountry, countryArrayList);
        });*/


        binding.etState.setOnClickListener(v -> {
            if (!stateArrayList.isEmpty())
                showDropDownState(v, binding.etState, stateArrayList);
        });

        binding.etTown.setOnClickListener(v -> {
            if (!cityArrayList.isEmpty())
                showDropDownCity(v, binding.etTown, cityArrayList);
        });


        if(NetworkAvailablity.checkNetworkStatus(requireActivity()))  getAllCountry();
        else Toast.makeText(requireActivity(), getString(R.string.network_failure), Toast.LENGTH_SHORT).show();

        if(NetworkAvailablity.checkNetworkStatus(requireActivity()))  getAllState11(countryId);
        else Toast.makeText(requireActivity(), getString(R.string.network_failure), Toast.LENGTH_SHORT).show();

        /*binding.etTown.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                // When the user finishes entering the email, call API to check if the email exists
                if (binding.etTown.getText().toString().trim().isEmpty()) {
                    //  binding.email.setError(getString(R.string.can_not_be_empty));
                    //  binding.email.setFocusable(true);
                    Toast.makeText(getActivity(), getString(R.string.please_enter_town), Toast.LENGTH_SHORT).show();
                }
                else {
                    checkCityExistence(binding.etTown.getText().toString());
                }
            }
        });*/


    }

    private void validation() {


        if(binding.etFname.getText().toString().equals("")){
            Toast.makeText(getActivity(), getString(R.string.please_enter_full_name), Toast.LENGTH_SHORT).show();
        }

      /*  else if(binding.etLname.getText().toString().equals("")){
            Toast.makeText(getActivity(), getString(R.string.please_enter_last_name), Toast.LENGTH_SHORT).show();
        }*/
        else if(binding.etEmail.getText().toString().equals("")){
            Toast.makeText(getActivity(), getString(R.string.please_enter_email), Toast.LENGTH_SHORT).show();
        }

        else if(binding.etMobile.getText().toString().equals("")){
            Toast.makeText(getActivity(), getString(R.string.please_enter_mobile_number), Toast.LENGTH_SHORT).show();
        }

        else if(binding.tvCompleteadd.getText().toString().equals("")){
            Toast.makeText(getActivity(), getString(R.string.please_select_address), Toast.LENGTH_SHORT).show();
        }

        else if(binding.etCountry.getText().toString().equals("")){
            Toast.makeText(getActivity(), getString(R.string.please_enter_country), Toast.LENGTH_SHORT).show();
        }

        else if(binding.etPostCode.getText().toString().equals("")){
            Toast.makeText(getActivity(), getString(R.string.please_enter_postcode), Toast.LENGTH_SHORT).show();
        }

        else if(binding.etTown.getText().toString().equals("")){
            Toast.makeText(getActivity(), getString(R.string.please_enter_town), Toast.LENGTH_SHORT).show();
        }

     /*   else if(addressType.equals("")){
            Toast.makeText(getActivity(), getString(R.string.please_select_address_type), Toast.LENGTH_SHORT).show();
        }*/
        else {
                if(NetworkAvailablity.checkNetworkStatus(getActivity())) UpdateLocation();
              else Toast.makeText(getActivity(), getString(R.string.network_failure), Toast.LENGTH_SHORT).show();
        }

    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == AUTOCOMPLETE_REQUEST_CODE_ADDRESS) {
            if (resultCode == RESULT_OK) {
                Place place = Autocomplete.getPlaceFromIntent(data);
                try {
                    Log.e("addressStreet====", place.getAddress());
                    address = place.getAddress();
                    latitude = place.getLatLng().latitude;
                    longitude = place.getLatLng().longitude;
                    city = DataManager.getInstance().getAddress(getActivity(), latitude, longitude);
                    countryName = DataManager.getInstance().getCountry(getActivity(), latitude, longitude);

                    tvArea.setVisibility(View.VISIBLE);
                    tv1.setVisibility(View.VISIBLE);
                    v1.setVisibility(View.VISIBLE);
                    tvArea.setText(city);
                    binding.tvCompleteadd.setText(place.getAddress());
                    latitude = place.getLatLng().latitude;
                    longitude = place.getLatLng().longitude;



                    if(countryArrayList!=null) {
                        for (int i = 0; i <= countryArrayList.size(); i++) {
                            if (countryArrayList.get(i).getName().equals(countryName) || countryArrayList.get(i).getNameFr().equals(countryName) ) {
                                countryId = countryArrayList.get(i).getId();
                                binding.etCountry.setText(countryArrayList.get(i).getName());
                                if(NetworkAvailablity.checkNetworkStatus(requireActivity())) getAllState(countryId);
                                else Toast.makeText(requireActivity(), getString(R.string.network_failure), Toast.LENGTH_SHORT).show();
                            }
                        }
                    }


                    // SessionManager.writeString(getActivity(), Constant.lat,String.valueOf(latitude));
                    // SessionManager.writeString(getActivity(), Constant.lon,String.valueOf(longitude));
                    //  SessionManager.writeString(getActivity(), Constant.address,address);

                } catch (Exception e) {
                    e.printStackTrace();
                    //setMarker(latLng);
                }

            } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
                // TODO: Handle the error.
                Status status = Autocomplete.getStatusFromIntent(data);
            }

        }

    }



    public void UpdateLocation(){
        DataManager.getInstance().showProgressMessage(getActivity(), getString(R.string.please_wait));
        Map<String,String> headerMap = new HashMap<>();
        headerMap.put("Authorization","Bearer " +PreferenceConnector.readString(getActivity(), PreferenceConnector.access_token,""));
        headerMap.put("Accept","application/json");

        Map<String, String> map = new HashMap<>();
        map.put("id",result.getId() );
        map.put("user_id", PreferenceConnector.readString(getActivity(), PreferenceConnector.User_id, ""));
        map.put("type", addressType);
        map.put("title_address",binding.etTitle.getText().toString());
        map.put("fname",binding.etFname.getText().toString());
        map.put("lname",binding.etLname.getText().toString());
        map.put("email",binding.etEmail.getText().toString());
        map.put("phone", binding.etMobile.getText().toString());
        map.put("address",address);
        map.put("lat",latitude+"");
        map.put("lon",longitude+"");
        map.put("country", countryId);
        map.put("state", stateId);
        map.put("zip", binding.etPostCode.getText().toString());
        map.put("city", cityId/*binding.etTown.getText().toString()*/);
        map.put("register_id", PreferenceConnector.readString(getActivity(), PreferenceConnector.Register_id, ""));

        Log.e(TAG, "Update Location Request :" + map);
        Call<ResponseBody> loginCall = apiInterface.updateAddress(headerMap,map);
        loginCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                DataManager.getInstance().hideProgressMessage();

                try {
                    String responseData = response.body() != null ? response.body().string() : "";
                    JSONObject object = new JSONObject(responseData);
                    Log.e(TAG, "Update Location RESPONSE" + object);
                    if (object.optString("status").equals("1")) {
                        Toast.makeText(getActivity(), getString(R.string.address_added_successfully), Toast.LENGTH_SHORT).show();
                        listener.onAddress("Added");
                        dismiss();
                    } else if (object.optString("status").equals("0")) {
                        Toast.makeText(getActivity(),object.optString("message"), Toast.LENGTH_SHORT).show();
                    }

                    else if (object.getString("status").equals("5")) {
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

    private void getAllCountry() {
        // DataManager.getInstance().showProgressMessage(getActivity(), getString(R.string.please_wait));
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


    private void getAllState(String countryId) {
        // DataManager.getInstance().showProgressMessage(getActivity(), getString(R.string.please_wait));
        Map<String,String> headerMap = new HashMap<>();
        headerMap.put("Authorization","Bearer " +PreferenceConnector.readString(getActivity(), PreferenceConnector.access_token,""));
        headerMap.put("Accept","application/json");

        HashMap<String, String> map = new HashMap<>();
        map.put("country_id", countryId);


        Call<ResponseBody> chatCount = apiInterface.getAllState(map);
        chatCount.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                DataManager.getInstance().hideProgressMessage();
                try {
                    String responseData = response.body() != null ? response.body().string() : "";
                    JSONObject object = new JSONObject(responseData);
                    Log.e(TAG, "Get All State RESPONSE" + object);

                    if (object.optString("status").equals("1")) {
                        StateModel stateModel = new Gson().fromJson(responseData, StateModel.class);
                        stateArrayList.clear();
                        stateArrayList.addAll(stateModel.getResult());
                        stateId = stateArrayList.get(0).getId();
                        binding.etState.setText(stateArrayList.get(0).getName());
                        getAllCity11(stateId);

                    } else if (object.optString("status").equals("0")) {
                        stateArrayList.clear();

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


    private void getAllState11(String countryId) {
        // DataManager.getInstance().showProgressMessage(getActivity(), getString(R.string.please_wait));
        Map<String,String> headerMap = new HashMap<>();
        headerMap.put("Authorization","Bearer " +PreferenceConnector.readString(getActivity(), PreferenceConnector.access_token,""));
        headerMap.put("Accept","application/json");

        HashMap<String, String> map = new HashMap<>();
        map.put("country_id", countryId);


        Call<ResponseBody> chatCount = apiInterface.getAllState(map);
        chatCount.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                DataManager.getInstance().hideProgressMessage();
                try {
                    String responseData = response.body() != null ? response.body().string() : "";
                    JSONObject object = new JSONObject(responseData);
                    Log.e(TAG, "Get All State RESPONSE" + object);

                    if (object.optString("status").equals("1")) {
                        StateModel stateModel = new Gson().fromJson(responseData, StateModel.class);
                        stateArrayList.clear();
                        stateArrayList.addAll(stateModel.getResult());

                        for(int i=0;i<stateArrayList.size();i++){
                            if(stateId.equals(stateArrayList.get(i).getId())){
                                stateId = stateArrayList.get(i).getId();
                                binding.etState.setText(stateArrayList.get(i).getName());
                            }
                        }
                        getAllCity(stateId);


                    } else if (object.optString("status").equals("0")) {
                        stateArrayList.clear();

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



    private void getAllCity(String stateId) {
        // DataManager.getInstance().showProgressMessage(getActivity(), getString(R.string.please_wait));
        Map<String,String> headerMap = new HashMap<>();
        headerMap.put("Authorization","Bearer " +PreferenceConnector.readString(getActivity(), PreferenceConnector.access_token,""));
        headerMap.put("Accept","application/json");

        HashMap<String, String> map = new HashMap<>();
        map.put("state_id", stateId);


        Call<ResponseBody> chatCount = apiInterface.getAllCity(map);
        chatCount.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                DataManager.getInstance().hideProgressMessage();
                try {
                    String responseData = response.body() != null ? response.body().string() : "";
                    JSONObject object = new JSONObject(responseData);
                    Log.e(TAG, "Get All city RESPONSE" + object);

                    if (object.optString("status").equals("1")) {
                        CityModel cityModel = new Gson().fromJson(responseData, CityModel.class);
                        cityArrayList.clear();
                        cityArrayList.addAll(cityModel.getResult());

                       if (cityArrayList.get(0).getName().equals(result.getCity())) {
                           cityId = cityArrayList.get(0).getId();
                           binding.etTown.setText(cityArrayList.get(0).getName());
                         //  checkCityExistence(cityArrayList.get(0).getName());
                           checkCityExistence(cityArrayList.get(0).getId());

                       }
                       else {
                         //  cityId = cityArrayList.get(0).getId();

                           for(int i=0;i<cityArrayList.size();i++){
                               if(cityId.equals(cityArrayList.get(i).getId())){
                                   cityId = cityArrayList.get(i).getId();
                                   //binding.etState.setText(cityArrayList.get(i).getName());
                                  // binding.etTown.setText(result.getCity());
                                   binding.etTown.setText(cityArrayList.get(i).getName());
                               }
                           }



                       }

                    } else if (object.optString("status").equals("0")) {
                        cityArrayList.clear();
                        CityNotAvailableDialog(getString(R.string.alert),getString(R.string.city_not_available_on_this_state));

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

    private void getAllCity11(String stateId) {
        // DataManager.getInstance().showProgressMessage(getActivity(), getString(R.string.please_wait));
        Map<String,String> headerMap = new HashMap<>();
        headerMap.put("Authorization","Bearer " +PreferenceConnector.readString(getActivity(), PreferenceConnector.access_token,""));
        headerMap.put("Accept","application/json");

        HashMap<String, String> map = new HashMap<>();
        map.put("state_id", stateId);


        Call<ResponseBody> chatCount = apiInterface.getAllCity(map);
        chatCount.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                DataManager.getInstance().hideProgressMessage();
                try {
                    String responseData = response.body() != null ? response.body().string() : "";
                    JSONObject object = new JSONObject(responseData);
                    Log.e(TAG, "Get All city RESPONSE" + object);

                    if (object.optString("status").equals("1")) {
                        CityModel cityModel = new Gson().fromJson(responseData, CityModel.class);
                        cityArrayList.clear();
                        cityArrayList.addAll(cityModel.getResult());
                        cityId = cityArrayList.get(0).getId();
                        binding.etTown.setText(cityArrayList.get(0).getName());
                     //   checkCityExistence(cityArrayList.get(0).getName());
                        checkCityExistence(cityArrayList.get(0).getId());



                    } else if (object.optString("status").equals("0")) {
                        cityArrayList.clear();
                        CityNotAvailableDialog(getString(R.string.alert),getString(R.string.city_not_available_on_this_state));

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



    private void showDropDownCountry(View v, TextView textView, List<CountryModel.Result> stringList) {
        PopupMenu popupMenu = new PopupMenu(getActivity(), v);
        for (int i = 0; i < stringList.size(); i++) {
            popupMenu.getMenu().add(stringList.get(i).getName());
        }
        popupMenu.setOnMenuItemClickListener(menuItem -> {
            textView.setText(menuItem.getTitle());
            for (int i = 0; i < stringList.size(); i++) {
                if(stringList.get(i).getName().equalsIgnoreCase(menuItem.getTitle().toString())) {
                    countryId = stringList.get(i).getId();

                }
            }
            return true;
        });
        popupMenu.show();
    }


    private void showDropDownState(View v, TextView textView, List<StateModel.Result> stringList) {
        PopupMenu popupMenu = new PopupMenu(getActivity(), v);
        for (int i = 0; i < stringList.size(); i++) {
            popupMenu.getMenu().add(stringList.get(i).getName());
        }
        popupMenu.setOnMenuItemClickListener(menuItem -> {
            textView.setText(menuItem.getTitle());
            for (int i = 0; i < stringList.size(); i++) {
                if(stringList.get(i).getName().equalsIgnoreCase(menuItem.getTitle().toString())) {
                    stateId = stringList.get(i).getId();
                    getAllCity11(stateId);
                }
            }
            return true;
        });
        popupMenu.show();
    }



    private void showDropDownCity(View v, TextView textView, List<CityModel.Result> stringList) {
        PopupMenu popupMenu = new PopupMenu(getActivity(), v);
        for (int i = 0; i < stringList.size(); i++) {
            popupMenu.getMenu().add(stringList.get(i).getName());
        }
        popupMenu.setOnMenuItemClickListener(menuItem -> {
            textView.setText(menuItem.getTitle());
            for (int i = 0; i < stringList.size(); i++) {
                if(stringList.get(i).getName().equalsIgnoreCase(menuItem.getTitle().toString())) {
                    cityId = stringList.get(i).getId();
                   // checkCityExistence(stringList.get(i).getName());
                    checkCityExistence(stringList.get(i).getId());

                }
            }
            return true;
        });
        popupMenu.show();
    }


    private void checkCityExistence(String city) {
        //binding.loader.setVisibility(View.VISIBLE);

        if (city.isEmpty()) {
            // Don't enable the next EditText if email is empty
            return;
        }

        Map<String, String> map = new HashMap<>();
        map.put("city", city);
        map.put("country", countryId);
        map.put("state", stateId);
        map.put("user_id", PreferenceConnector.readString(getActivity(), PreferenceConnector.User_id, ""));
        Log.e("MapMap", "Check city Exit Request" + map);

        Call<ResponseBody> loginCall = apiInterface.checkCityExitApi(map);
        loginCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                DataManager.getInstance().hideProgressMessage();
                // binding.loader.setVisibility(View.GONE);
                try {
                    String responseData = response.body() != null ? response.body().string() : "";
                    JSONObject object = new JSONObject(responseData);
                    Log.e("Add Address", "Check email Exit RESPONSE" + object);
                    if (object.optString("status").equals("1")) {
                        //emailExit = true;
                    } else if (object.optString("status").equals("0")) {
                        // emailExit = false;
                     //   binding.etTown.setError(getString(R.string.this_city_is_not_served));
                        // binding.email.setFocusable(true);
                      //  Toast.makeText(requireActivity(), getString(R.string.this_city_is_not_served), Toast.LENGTH_SHORT).show();

                        ShowCityServeDialog(getString(R.string.alert),getString(R.string.this_city_is_not_served));

                    }


                } catch (Exception e) {
                    Log.e("error>>>>", "" + e);
                    DataManager.getInstance().hideProgressMessage();
                    e.printStackTrace();
                }


            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                call.cancel();
                Toast.makeText(requireActivity(), "Network Error !!!!", Toast.LENGTH_SHORT).show();
                //  binding.loader.setVisibility(View.GONE);
                DataManager.getInstance().hideProgressMessage();
            }
        });
    }



    private void ShowCityServeDialog(String title,String msg){
        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
        builder.setTitle(title)
                .setMessage(msg)
                .setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }) .show();
              /*  .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })*/
        // Show the dialog
    }


    private void CityNotAvailableDialog(String title,String msg){
        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
        builder.setTitle(title)
                .setMessage(msg)
                .setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }) .show();
              /*  .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })*/
        // Show the dialog
    }


}