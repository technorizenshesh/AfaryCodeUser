package com.my.afarycode.OnlineShopping;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
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
import com.my.afarycode.OnlineShopping.helper.NetworkAvailablity;
import com.my.afarycode.R;
import com.my.afarycode.Splash;
import com.my.afarycode.databinding.ActivitySignUpBinding;
import com.my.afarycode.ratrofit.AfaryCode;
import com.my.afarycode.ratrofit.ApiClient;

import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SignUpActivity extends AppCompatActivity {

    ActivitySignUpBinding binding;
    private AfaryCode apiInterface;
    private String code, lang = "";
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    public static String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
    private boolean emailExit=false;
    private boolean  numberExit=false;

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
            Log.e("selected lang====",lang);
        }


        binding.RRSigUp.setOnClickListener(v -> {
                    code = binding.ccp.getSelectedCountryCode();
                    Log.e("code>>>", code);

                    if (binding.name.getText().toString().trim().isEmpty()) {
                        binding.name.setError(getString(R.string.can_not_be_empty));
                        binding.name.setFocusable(true);
                        Toast.makeText(SignUpActivity.this, getString(R.string.please_enter_name), Toast.LENGTH_SHORT).show();
                    } else if (binding.userName.getText().toString().trim().isEmpty()) {
                        binding.userName.setError(getString(R.string.can_not_be_empty));
                        binding.userName.setFocusable(true);
                        Toast.makeText(SignUpActivity.this, getString(R.string.please_enter_user_name), Toast.LENGTH_SHORT).show();
                    } else if (binding.email.getText().toString().trim().isEmpty()) {
                        binding.email.setError(getString(R.string.can_not_be_empty));
                        binding.email.setFocusable(true);
                        Toast.makeText(SignUpActivity.this, getString(R.string.please_enter_email), Toast.LENGTH_SHORT).show();
                    }
                    else if (!binding.email.getText().toString().trim().matches(emailPattern)) {
                        binding.email.setError(getString(R.string.wrong_email));
                        binding.email.setFocusable(true);
                        Toast.makeText(SignUpActivity.this, getString(R.string.wrong_email), Toast.LENGTH_SHORT).show();
                    }

                    else if (!emailExit) {
                        binding.email.setError(getString(R.string.email_already_exits));
                        binding.email.setFocusable(true);
                        Toast.makeText(SignUpActivity.this, getString(R.string.email_already_exits), Toast.LENGTH_SHORT).show();
                    }


                    else if (binding.phone.getText().toString().trim().isEmpty()) {
                        binding.phone.setError(getString(R.string.can_not_be_empty));
                        binding.phone.setFocusable(true);
                        Toast.makeText(SignUpActivity.this, getString(R.string.please_enter_mobile_number), Toast.LENGTH_SHORT).show();
                    }

                    else if (!numberExit) {
                        binding.phone.setError(getString(R.string.mobile_number_already_exits));
                        binding.phone.setFocusable(true);
                        Toast.makeText(SignUpActivity.this, getString(R.string.mobile_number_already_exits), Toast.LENGTH_SHORT).show();
                    }

                    else if (binding.password.getText().toString().trim().isEmpty()) {
                        binding.password.setError(getString(R.string.can_not_be_empty));
                        binding.password.setFocusable(true);
                        Toast.makeText(SignUpActivity.this, getString(R.string.please_enter_password), Toast.LENGTH_SHORT).show();
                    } else if (!binding.password.getText().toString().equals(binding.confirmPassword.getText().toString())) {
                        binding.confirmPassword.setError(getString(R.string.password_should_be_same));
                        binding.confirmPassword.setFocusable(true);
                    } else {
                        startActivity(new Intent(SignUpActivity.this, VerificationScreen.class)
                                .putExtra("user_name",binding.userName.getText().toString())
                                .putExtra("name",binding.name.getText().toString())
                                .putExtra("email",binding.email.getText().toString().trim())
                                .putExtra("password",binding.password.getText().toString())
                                .putExtra("mobile",binding.phone.getText().toString())
                                .putExtra("country_code",code)
                                .putExtra("country","")
                                .putExtra("language",lang)
                                .putExtra("type","User")
                                .putExtra("register_id",PreferenceConnector.readString(SignUpActivity.this,
                                        PreferenceConnector.Firebash_Token, "")));
                    }
                }
        );

        binding.txtLogin.setOnClickListener(v -> {
            startActivity(new Intent(SignUpActivity.this, LoginActivity.class).putExtra("type", ""));
        });



        binding.email.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                // When the user finishes entering the email, call API to check if the email exists
                if (binding.email.getText().toString().trim().isEmpty()) {
                    binding.email.setError(getString(R.string.can_not_be_empty));
                    binding.email.setFocusable(true);
                    Toast.makeText(SignUpActivity.this, getString(R.string.please_enter_email), Toast.LENGTH_SHORT).show();
                }
                else if (!binding.email.getText().toString().trim().matches(emailPattern)) {
                    binding.email.setError(getString(R.string.wrong_email));
                    binding.email.setFocusable(true);
                    Toast.makeText(SignUpActivity.this, getString(R.string.wrong_email), Toast.LENGTH_SHORT).show();
                }
                else {
                    checkEmailExistence(binding.email.getText().toString());

                }
            }
        });

        binding.phone.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                // When the user finishes entering the phone number, call API to check if the number exists
                checkNumberExistence(binding.ccp.getSelectedCountryCode(), binding.phone.getText().toString());
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



    private void checkEmailExistence(String email) {
        //binding.loader.setVisibility(View.VISIBLE);

        if (email.isEmpty()) {
            // Don't enable the next EditText if email is empty
            return;
        }

        Map<String, String> map = new HashMap<>();
        map.put("email", email);
        Log.e("MapMap", "Check email Exit Request" + map);

        Call<ResponseBody> loginCall = apiInterface.checkEmailExitApi(map);
        loginCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                DataManager.getInstance().hideProgressMessage();
                // binding.loader.setVisibility(View.GONE);
                try {
                    String responseData = response.body() != null ? response.body().string() : "";
                    JSONObject object = new JSONObject(responseData);
                    Log.e("SignupActivity", "Check email Exit RESPONSE" + object);
                    if (object.optString("status").equals("1")) {
                        emailExit = true;
                    } else if (object.optString("status").equals("0")) {
                        emailExit = false;
                        binding.email.setError(getString(R.string.email_already_exits));
                        binding.email.setFocusable(true);
                        Toast.makeText(SignUpActivity.this, getString(R.string.email_already_exits), Toast.LENGTH_SHORT).show();
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
                Toast.makeText(SignUpActivity.this, "Network Error !!!!", Toast.LENGTH_SHORT).show();
                //  binding.loader.setVisibility(View.GONE);
                DataManager.getInstance().hideProgressMessage();
            }
        });
    }

    private void checkNumberExistence(String countryCode,String number) {
        //binding.loader.setVisibility(View.VISIBLE);

        if (number.isEmpty()) {
           // binding.phone.setEnabled(false); // Don't enable the next EditText if phone number is empty
            return;
        }

        Map<String, String> map = new HashMap<>();
        map.put("mobile",number);
        map.put("country_code",countryCode );
        Log.e("MapMap", "Check number Exit Request" + map);

        Call<ResponseBody> loginCall = apiInterface.checkNumberExitApi(map);
        loginCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                DataManager.getInstance().hideProgressMessage();
                // binding.loader.setVisibility(View.GONE);
                try {
                    String responseData = response.body() != null ? response.body().string() : "";
                    JSONObject object = new JSONObject(responseData);
                    Log.e("SignupActivity", "Check number Exit RESPONSE" + object);
                    if (object.optString("status").equals("1")) {
                        numberExit =true;

                    } else if (object.optString("status").equals("0")) {
                        numberExit =false;
                        binding.phone.setError(getString(R.string.mobile_number_already_exits));
                        binding.phone.setFocusable(true);
                        Toast.makeText(SignUpActivity.this, getString(R.string.mobile_number_already_exits), Toast.LENGTH_SHORT).show();
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
                Toast.makeText(SignUpActivity.this, "Network Error !!!!", Toast.LENGTH_SHORT).show();
                //  binding.loader.setVisibility(View.GONE);
                DataManager.getInstance().hideProgressMessage();
            }
        });
    }





}