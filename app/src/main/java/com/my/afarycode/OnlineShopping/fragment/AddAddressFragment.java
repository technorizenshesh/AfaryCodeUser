package com.my.afarycode.OnlineShopping.fragment;

import static android.app.Activity.RESULT_OK;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
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
import com.my.afarycode.OnlineShopping.Model.StateModel;
import com.my.afarycode.OnlineShopping.activity.CardAct;
import com.my.afarycode.OnlineShopping.activity.CheckOutDeliveryAct;
import com.my.afarycode.OnlineShopping.constant.PreferenceConnector;
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

public class AddAddressFragment extends BottomSheetDialogFragment {
    public String TAG = "AddAddressFragment";
    BottomSheetDialog dialog;
    private BottomSheetBehavior<View> mBehavior;
    FragmentAddAddressBinding binding;
    public static TextView tvArea, tv1,tvCompleteadd;
    public static View v1;
    double latitude = 0.0, longitude = 0.0;
    int AUTOCOMPLETE_REQUEST_CODE_ADDRESS = 101;
    AfaryCode apiInterface;
    String address = "", city = "",addressType="",title="",categoryId="",countryId="",stateId="";
    addAddressListener listener;

    ArrayList<CountryModel.Result> countryArrayList;

    ArrayList<StateModel.Result> stateArrayList;



    public AddAddressFragment(String title,String categoryId) {
        this.title = title;
        this.categoryId = categoryId;
    }

    public AddAddressFragment callBack(addAddressListener listener) {
        this.listener = listener;
        return this;
    }


    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        dialog = (BottomSheetDialog) super.onCreateDialog(savedInstanceState);
        binding = DataBindingUtil.inflate(LayoutInflater.from(getActivity()), R.layout.fragment_add_address, null, false);
        dialog.setContentView(binding.getRoot());
        dialog.setCanceledOnTouchOutside(false);
        apiInterface = ApiClient.getClient(getActivity()).create(AfaryCode.class);
        mBehavior = BottomSheetBehavior.from((View) binding.getRoot().getParent());
        mBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        if(title.equals("")) title = "New Address";
        initBinding();
        return dialog;
    }





    private void initBinding() {
        countryArrayList = new ArrayList<>();
        stateArrayList = new ArrayList<>();

        tvArea = dialog.findViewById(R.id.tvArea);
        tv1 = dialog.findViewById(R.id.tv1);
        v1 = dialog.findViewById(R.id.View1);
        tvCompleteadd = dialog.findViewById(R.id.tvCompleteadd);
        binding.etTitle.setText(title);
        if (!Places.isInitialized()) {
            Places.initialize(getActivity(), getString(R.string.place_api_key));
        }


        if(NetworkAvailablity.checkNetworkStatus(requireActivity())) getAllCountry();
        else Toast.makeText(requireActivity(), getString(R.string.network_failure), Toast.LENGTH_SHORT).show();




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
            if (!countryArrayList.isEmpty())
                showDropDownCountry(v, binding.etCountry, countryArrayList);
        });


        binding.etState.setOnClickListener(v -> {
            if (!stateArrayList.isEmpty())
                showDropDownState(v, binding.etState, stateArrayList);
        });




        mBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View view, int newState) {
                switch (newState) {
                    case BottomSheetBehavior.STATE_HIDDEN:{
                       // Toast.makeText(getActivity(), "STATE_HIDDEN Sheet", Toast.LENGTH_SHORT).show();
                        listener.onAddress("Hidden");
                        dismiss();
                    }
                        break;
                    case BottomSheetBehavior.STATE_EXPANDED: {
                       // btn_bottom_sheet.setText("Close Sheet");
                      //  Toast.makeText(getActivity(), "Close Sheet", Toast.LENGTH_SHORT).show();
                    }
                    break;
                    case BottomSheetBehavior.STATE_COLLAPSED: {
                     //   btn_bottom_sheet.setText("Expand Sheet");
                       // Toast.makeText(getActivity(), "Expand Sheet", Toast.LENGTH_SHORT).show();

                    }
                    break;
                    case BottomSheetBehavior.STATE_DRAGGING:{
                       // Toast.makeText(getActivity(), "STATE_DRAGGING Sheet", Toast.LENGTH_SHORT).show();

                    }

                        break;
                    case BottomSheetBehavior.STATE_SETTLING:{
                     //  Toast.makeText(getActivity(), "STATE_SETTLING Sheet", Toast.LENGTH_SHORT).show();

                    }
                        break;
                }
            }

            @Override
            public void onSlide(@NonNull View view, float v) {

            }
        });





        dialog.setOnKeyListener(new DialogInterface.OnKeyListener()
        {
            @Override
            public boolean onKey(android.content.DialogInterface dialog, int keyCode,android.view.KeyEvent event) {

                if ((keyCode ==  android.view.KeyEvent.KEYCODE_BACK))
                {
                    listener.onAddress("Hidden");
                    dismiss();
                  //  Toast.makeText(getActivity(), "Android Back press", Toast.LENGTH_SHORT).show();

                    //Hide your keyboard here!!!
                    return true; // pretend we've processed it

                }
                else
                    return false; // pass on to be processed as normal
            }
        });




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

        else if(/*binding.etCountry.getText().toString()*/countryId.equals("")){
            Toast.makeText(getActivity(), getString(R.string.please_enter_country), Toast.LENGTH_SHORT).show();
        }

        else if(binding.etPostCode.getText().toString().equals("")){
            Toast.makeText(getActivity(), getString(R.string.please_enter_postcode), Toast.LENGTH_SHORT).show();
        }

        else if(binding.etTown.getText().toString().equals("")){
            Toast.makeText(getActivity(), getString(R.string.please_enter_town), Toast.LENGTH_SHORT).show();
        }

       /* else if(deliveryType.equals("")){
            Toast.makeText(getActivity(), getString(R.string.please_select_address_type), Toast.LENGTH_SHORT).show();
        }*/
        else {
           if(NetworkAvailablity.checkNetworkStatus(requireActivity())) AddLocation();
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



    public void AddLocation(){
        DataManager.getInstance().showProgressMessage(getActivity(), getString(R.string.please_wait));
        Map<String,String> headerMap = new HashMap<>();
        headerMap.put("Authorization","Bearer " +PreferenceConnector.readString(getActivity(), PreferenceConnector.access_token,""));
      //  headerMap.put("Accept","application/json");

        Map<String, String> map = new HashMap<>();
        map.put("user_id", PreferenceConnector.readString(getActivity(), PreferenceConnector.User_id, ""));
        map.put("type", /*addressType*/ title);
        map.put("title_address",binding.etTitle.getText().toString());
        map.put("address_category",categoryId);
        map.put("fname",binding.etFname.getText().toString());
        map.put("lname",binding.etLname.getText().toString());
        map.put("email",binding.etEmail.getText().toString());
        map.put("phone", binding.etMobile.getText().toString());
        map.put("address",address);
        map.put("lat",latitude+"");
        map.put("lon",longitude+"");
        map.put("country",countryId /*binding.etCountry.getText().toString()*/);
        map.put("state",stateId /*binding.etCountry.getText().toString()*/);
        map.put("zip", binding.etPostCode.getText().toString());
        map.put("city", binding.etTown.getText().toString());
        map.put("register_id", PreferenceConnector.readString(getActivity(), PreferenceConnector.Register_id, ""));

        Log.e(TAG, "Add Location Request :" + map);
        Call<ResponseBody> loginCall = apiInterface.addAddress(headerMap,map);
        loginCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                DataManager.getInstance().hideProgressMessage();

                try {
                    String responseData = response.body() != null ? response.body().string() : "";
                    JSONObject object = new JSONObject(responseData);
                    Log.e(TAG, "Add Location RESPONSE" + object);
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
                        for(int i=0;i<=data.getResult().size();i++) {
                            if (data.getResult().get(i).getAvailability().equals("1")){
                                countryArrayList.add(data.getResult().get(i));
                            }
                        }


                      //  countryArrayList.addAll(data.getResult());
                       Log.e("available country list===",countryArrayList.size()+"");

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

                    if(NetworkAvailablity.checkNetworkStatus(requireActivity())) getAllState(countryId);
                    else Toast.makeText(requireActivity(), getString(R.string.network_failure), Toast.LENGTH_SHORT).show();
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

                }
            }
            return true;
        });
        popupMenu.show();
    }


}