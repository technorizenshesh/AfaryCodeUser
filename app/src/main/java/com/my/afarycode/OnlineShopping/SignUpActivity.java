package com.my.afarycode.OnlineShopping;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.databinding.DataBindingUtil;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.gson.Gson;
import com.my.afarycode.OnlineShopping.Model.SignupModel;
import com.my.afarycode.OnlineShopping.constant.PreferenceConnector;
import com.my.afarycode.OnlineShopping.helper.CountryCodes;
import com.my.afarycode.OnlineShopping.helper.DataManager;
import com.my.afarycode.R;
import com.my.afarycode.databinding.ActivitySignUpBinding;
import com.my.afarycode.ratrofit.AfaryCode;
import com.my.afarycode.ratrofit.ApiClient;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SignUpActivity extends AppCompatActivity {

    ActivitySignUpBinding binding;
    private AfaryCode apiInterface;
    private String code, lang = "";
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_sign_up);
        apiInterface = ApiClient.getClient(this).create(AfaryCode.class);

        SetupUi();

    }

    private void SetupUi() {
        setCountryCodeFromLocation();


        if (getIntent() != null) {
            lang = getIntent().getStringExtra("lang");
        }


        binding.RRSigUp.setOnClickListener(v -> {
                    code = binding.ccp.getSelectedCountryCode();
                    Log.e("code>>>", code);

                    if (binding.name.getText().toString().trim().isEmpty()) {
                        binding.name.setError(getString(R.string.can_not_be_empty));
                        Toast.makeText(SignUpActivity.this, getString(R.string.please_enter_name), Toast.LENGTH_SHORT).show();
                    } else if (binding.userName.getText().toString().trim().isEmpty()) {
                        binding.userName.setError(getString(R.string.can_not_be_empty));
                        Toast.makeText(SignUpActivity.this, getString(R.string.please_enter_user_name), Toast.LENGTH_SHORT).show();
                    } else if (binding.email.getText().toString().trim().isEmpty()) {
                        binding.email.setError(getString(R.string.can_not_be_empty));
                        Toast.makeText(SignUpActivity.this, getString(R.string.please_enter_email), Toast.LENGTH_SHORT).show();
                    } else if (binding.phone.getText().toString().trim().isEmpty()) {
                        binding.phone.setError(getString(R.string.can_not_be_empty));
                        Toast.makeText(SignUpActivity.this, getString(R.string.please_enter_mobile_number), Toast.LENGTH_SHORT).show();
                    } else if (binding.password.getText().toString().trim().isEmpty()) {
                        binding.password.setError(getString(R.string.can_not_be_empty));
                        Toast.makeText(SignUpActivity.this, getString(R.string.please_enter_password), Toast.LENGTH_SHORT).show();
                    } else if (!binding.password.getText().toString().equals(binding.confirmPassword.getText().toString())) {
                        binding.confirmPassword.setError(getString(R.string.password_should_be_same));

                    } else {
                        SignUpAPi();
                    }
                }
        );

        binding.txtLogin.setOnClickListener(v -> {
            startActivity(new Intent(SignUpActivity.this, LoginActivity.class).putExtra("type", ""));
        });

    }

    private void SignUpAPi() {

        DataManager.getInstance().showProgressMessage
                (SignUpActivity.this, getString(R.string.please_wait));

        Map<String, String> map = new HashMap<>();
        map.put("user_name", binding.userName.getText().toString());
        map.put("name", binding.name.getText().toString());
        map.put("email", binding.email.getText().toString().trim());
        map.put("password", binding.password.getText().toString());
        map.put("mobile", binding.phone.getText().toString());
        map.put("country_code", code);
        map.put("country", "");
        map.put("language", lang);

        map.put("register_id", PreferenceConnector.readString(SignUpActivity.this,
                PreferenceConnector.Firebash_Token, ""));

        map.put("type", "User");

        Log.e("MapMap", "LOGIN REQUEST" + map);

        Call<SignupModel> SignupCall = apiInterface.signup(map);

        SignupCall.enqueue(new Callback<SignupModel>() {
            @Override
            public void onResponse(Call<SignupModel> call, Response<SignupModel> response) {
                DataManager.getInstance().hideProgressMessage();

                try {
                    SignupModel data = response.body();
                    String dataResponse = new Gson().toJson(response.body());
                    Log.e("MapMap", "LOGIN RESPONSE" + dataResponse);

                    if (data.status.equals("1")) {

                        String user_id = data.result.id;
                        String moble_no = data.result.mobile;
                        String firstName = data.result.getName();
                        String email1 = data.result.email;
                        String password = data.result.password;
                        String otp = data.result.otp;
                        String lang = data.getResult().getLanguage();
                        String token = data.result.getAccessToken();
                        String username = data.result.userName;
                        String img = data.result.image;
                      //  String countryCode = data.result.image;
                        //  Toast.makeText(SignUpActivity.this, data.message, Toast.LENGTH_SHORT).show();


                        PreferenceConnector.writeString(SignUpActivity.this, PreferenceConnector.User_id, user_id);
                        PreferenceConnector.writeString(SignUpActivity.this, PreferenceConnector.User_email, email1);
                        PreferenceConnector.writeString(SignUpActivity.this, PreferenceConnector.User_Mobile, moble_no);
                        PreferenceConnector.writeString(SignUpActivity.this, PreferenceConnector.Password, password);
                        PreferenceConnector.writeString(SignUpActivity.this, PreferenceConnector.User_First_name, firstName);
                        PreferenceConnector.writeString(SignUpActivity.this, PreferenceConnector.User_name, username);
                        PreferenceConnector.writeString(SignUpActivity.this, PreferenceConnector.User_img, img);
                        PreferenceConnector.writeString(SignUpActivity.this, PreferenceConnector.access_token, token);
                        PreferenceConnector.writeString(SignUpActivity.this, PreferenceConnector.Register_id, data.getResult().getRegisterId());
                        PreferenceConnector.writeString(SignUpActivity.this, PreferenceConnector.LANGUAGE, lang);


                        startActivity(new Intent(SignUpActivity.this, VerificationScreen.class)
                                .putExtra("status", "")
                                .putExtra("msg", "")
                                .putExtra("user_id", user_id)
                                .putExtra("mobile", binding.phone.getText().toString())
                                .putExtra("countryCode", binding.ccp.getSelectedCountryCode())
                        );
                        finish();


                    } else if (data.status.equals("0")) {
                        Toast.makeText(SignUpActivity.this, data.message, Toast.LENGTH_SHORT).show();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<SignupModel> call, Throwable t) {
                call.cancel();
                DataManager.getInstance().hideProgressMessage();
                // Toast.makeText(SignUpActivity.this, "Email Already Exist", Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void setCountryCodeFromLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        } else {
            try {
                FusedLocationProviderClient fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
                fusedLocationClient.getLastLocation().addOnSuccessListener(this, location -> {
                    if (location != null) {
                        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
                        try {
                            List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                            if (addresses != null && !addresses.isEmpty()) {
                                String countryCode = addresses.get(0).getCountryCode();
                                if (countryCode != null && !countryCode.isEmpty()) {
                                    Log.e("country code===", CountryCodes.getPhoneCode(countryCode) + "");
                                    binding.ccp.setCountryForPhoneCode(/*getCountryPhoneCode(countryCode)*/
                                            CountryCodes.getPhoneCode(countryCode));
                                }
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                            Toast.makeText(this, "Error determining location", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }catch (Exception e){
                e.printStackTrace();
            }
        }

    }

    private int getCountryPhoneCode(String countryCode) {
        switch (countryCode) {
            case "IN": return 91; // India
            case "US": return 1;  // United States
            case "GA": return 241;

            // Add other countries as needed
            default: return 1; // Default to US if unknown
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                setCountryCodeFromLocation();
            }
        }
    }




}