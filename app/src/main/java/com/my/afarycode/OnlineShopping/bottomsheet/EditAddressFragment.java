package com.my.afarycode.OnlineShopping.bottomsheet;

import static android.app.Activity.RESULT_OK;

import android.app.Dialog;
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
import com.my.afarycode.OnlineShopping.Model.CountryModel;
import com.my.afarycode.OnlineShopping.Model.LocationModel;
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
    String address = "", city = "",addressType="",countryId="";
    addAddressListener listener;
    LocationModel.Result result;

    ArrayList<CountryModel.Result> countryArrayList;


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

        binding.tvAddressTitle.setText("UPDATE BILLING ADDRESS");

        countryArrayList = new ArrayList<>();

        binding.etTitle.setText(result.getAddressName());
        binding.etFname.setText(result.getFirstName());
        binding.etLname.setText(result.getLastName());
        binding.etEmail.setText(result.getEmail());
        binding.etMobile.setText(result.getPhone());
        tvCompleteadd.setText(result.getAddress());
        binding.etCountry.setText(result.getCountryName());
        binding.etPostCode.setText(result.getPostcode());
        binding.etTown.setText(result.getCity());
        address = result.getAddress();
        countryId = result.getCountry();

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


        binding.etCountry.setOnClickListener(v -> {
            if (countryArrayList.size() > 0)
                showDropDownCountry(v, binding.etCountry, countryArrayList);
        });

        getAllCountry();

    }

    private void validation() {


        if(binding.etFname.getText().toString().equals("")){
            Toast.makeText(getActivity(), getString(R.string.please_enter_first_name), Toast.LENGTH_SHORT).show();
        }

        else if(binding.etLname.getText().toString().equals("")){
            Toast.makeText(getActivity(), getString(R.string.please_enter_last_name), Toast.LENGTH_SHORT).show();
        }
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
                    tvArea.setVisibility(View.VISIBLE);
                    tv1.setVisibility(View.VISIBLE);
                    v1.setVisibility(View.VISIBLE);
                    tvArea.setText(city);
                    binding.tvCompleteadd.setText(place.getAddress());
                    latitude = place.getLatLng().latitude;
                    longitude = place.getLatLng().longitude;

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
        map.put("zip", binding.etPostCode.getText().toString());
        map.put("city", binding.etTown.getText().toString());
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


}