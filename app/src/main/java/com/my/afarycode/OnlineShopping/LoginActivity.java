package com.my.afarycode.OnlineShopping;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.databinding.DataBindingUtil;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.gson.Gson;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.my.afarycode.OnlineShopping.Model.LoginModel;
import com.my.afarycode.OnlineShopping.bottomsheet.WebViewBottomSheet;
import com.my.afarycode.OnlineShopping.constant.PreferenceConnector;
import com.my.afarycode.OnlineShopping.helper.CountryCodes;
import com.my.afarycode.OnlineShopping.helper.DataManager;
import com.my.afarycode.OnlineShopping.listener.AskListener;
import com.my.afarycode.R;
import com.my.afarycode.databinding.ActivityLoginBinding;
import com.my.afarycode.ratrofit.AfaryCode;
import com.my.afarycode.ratrofit.ApiClient;
import com.my.afarycode.ratrofit.Constant;

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

public class LoginActivity extends AppCompatActivity implements AskListener {

    ActivityLoginBinding binding;
    private AfaryCode apiInterface;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_login);
        apiInterface = ApiClient.getClient(this).create(AfaryCode.class);

    /*    if(getIntent()!=null){
            if(getIntent().getStringExtra("from").equalsIgnoreCase("notification")){
                AlertDialogAdminStatus(getIntent().getStringExtra("msg"),getIntent().getStringExtra("msg"));
            }
        }*/


        binding.txtRegister.setOnClickListener(v -> {
            //startActivity(new Intent(LoginActivity.this, SignUpActivity.class));

            startActivity(new Intent(this, AskAct.class));
            finish();
        });

        SetupUI();

        binding.txtForogtPassword.setOnClickListener(v -> {
            startActivity(new Intent(LoginActivity.this, ForgotPassword.class));
        });


        binding.txtTerms.setOnClickListener(v ->
                // startActivity(new Intent(LoginActivity.this, WebViewAct.class)
                //          .putExtra("url", Constant.TERMS_AND_CONDITIONS)
                //        .putExtra("title",getString(R.string.terms_and_conditions))));
                new WebViewBottomSheet(Constant.TERMS_AND_CONDITIONS, getString(R.string.terms_and_conditions)).callBack(this::ask).show(getSupportFragmentManager(), ""));


    }

    private void SetupUI() {
        setCountryCodeFromLocation();



        Intent intent = getIntent();
        if (intent != null) {
            String type = intent.getStringExtra("type");
            if (type.equalsIgnoreCase("Become a Customer")) {
                binding.tvTitle.setVisibility(View.VISIBLE);
            } else binding.tvTitle.setVisibility(View.GONE);

        } else {
            Log.e("LoginActivity", "Intent is null");

        }


        binding.RRLogin.setOnClickListener(v -> {
            if (binding.edtEmail.getText().toString().trim().isEmpty()) {
                binding.edtEmail.setError(getString(R.string.can_not_be_empty));
                Toast.makeText(LoginActivity.this, getString(R.string.please_enter_valied_email), Toast.LENGTH_SHORT).show();
            } else if (binding.password.getText().toString().trim().isEmpty()) {
                binding.password.setError(getString(R.string.can_not_be_empty));
                Toast.makeText(LoginActivity.this, getString(R.string.please_enter_password), Toast.LENGTH_SHORT).show();
            } else {
                LoginAPi();
            }
        });
    }

    private void LoginAPi() {
        DataManager.getInstance().showProgressMessage(LoginActivity.this, getString(R.string.please_wait));
        Map<String, String> map = new HashMap<>();
        map.put("email", binding.edtEmail.getText().toString());
        map.put("password", binding.password.getText().toString());
        map.put("lat", "");
        map.put("lon", "");
        map.put("register_id", PreferenceConnector.readString(LoginActivity.this, PreferenceConnector.Firebash_Token, ""));
        map.put("type", "User");
        map.put("country_code", binding.ccp.getSelectedCountryCode() + "");

        Log.e("MapMap", "LOGIN REQUEST" + map);
        Call<ResponseBody> loginCall = apiInterface.login(map);

        loginCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                DataManager.getInstance().hideProgressMessage();
                try {

                    String stringResponse = response.body().string();
                    JSONObject jsonObject = new JSONObject(stringResponse);
                    Log.e("MapMap", "LOGIN RESPONSE" + stringResponse);
                    if (jsonObject.getString("status").equals("1")) {
                        LoginModel data = new Gson().fromJson(stringResponse, LoginModel.class);
                        String user_id = data.result.id;
                        String moble_no = data.result.mobile;
                        String firstName = data.result.userName;
                        String email1 = data.result.email;
                        String password = data.result.password;

                        String username = data.result.userName;
                        String img = data.result.image;
                        String token = data.result.getAccessToken();


                        Toast.makeText(LoginActivity.this, getString(R.string.login_successfully), Toast.LENGTH_SHORT).show();


                        PreferenceConnector.writeString(LoginActivity.this, PreferenceConnector.LoginStatus, "true");
                        PreferenceConnector.writeString(LoginActivity.this, PreferenceConnector.User_id, user_id);
                        PreferenceConnector.writeString(LoginActivity.this, PreferenceConnector.User_email, email1);
                        PreferenceConnector.writeString(LoginActivity.this, PreferenceConnector.User_Mobile, moble_no);
                        PreferenceConnector.writeString(LoginActivity.this, PreferenceConnector.Password, password);
                        PreferenceConnector.writeString(LoginActivity.this, PreferenceConnector.User_First_name, firstName);
                        PreferenceConnector.writeString(LoginActivity.this, PreferenceConnector.User_name, username);
                        PreferenceConnector.writeString(LoginActivity.this, PreferenceConnector.User_img, img);
                        PreferenceConnector.writeString(LoginActivity.this, PreferenceConnector.access_token, token);
                        PreferenceConnector.writeString(LoginActivity.this, PreferenceConnector.FROM, "splash");
                        PreferenceConnector.writeString(LoginActivity.this, PreferenceConnector.Register_id, data.getResult().getRegisterId());
                        // PreferenceConnector.writeString(LoginActivity.this, PreferenceConnector.LANGUAGE, data.getResult().get);


                        startActivity(new Intent(LoginActivity.this, HomeActivity.class)
                                .putExtra("user_id", user_id)
                                .putExtra("status", "login").addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP));
                        finish();


                    } else if (jsonObject.getString("status").equals("0")) {
                        Toast.makeText(LoginActivity.this, jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                    }


/*
                    else if (jsonObject.getString("status").equals("10")) {
                        LoginModel data = new Gson().fromJson(stringResponse, LoginModel.class);
                        String user_id = data.result.id;
                        String moble_no = data.result.mobile;
                        String firstName = data.result.userName;
                        String email1 = data.result.email;
                        String password = data.result.password;

                        String username = data.result.userName;
                        String img = data.result.image;
                        String token = data.result.getAccessToken();
                        String countryCode =  data.result.getCountryCode();
                        String countryCode =  data.result.getCountryCode();


                        //Toast.makeText(LoginActivity.this, getString(R.string.login_successfully), Toast.LENGTH_SHORT).show();
                       // PreferenceConnector.writeString(LoginActivity.this, PreferenceConnector.LoginStatus, "true");
                        PreferenceConnector.writeString(LoginActivity.this, PreferenceConnector.User_id, user_id);
                        PreferenceConnector.writeString(LoginActivity.this, PreferenceConnector.User_email, email1);
                        PreferenceConnector.writeString(LoginActivity.this, PreferenceConnector.User_Mobile, moble_no);
                        PreferenceConnector.writeString(LoginActivity.this, PreferenceConnector.Password, password);
                        PreferenceConnector.writeString(LoginActivity.this, PreferenceConnector.User_First_name, firstName);
                        PreferenceConnector.writeString(LoginActivity.this, PreferenceConnector.User_name, username);
                        PreferenceConnector.writeString(LoginActivity.this, PreferenceConnector.User_img, img);
                        PreferenceConnector.writeString(LoginActivity.this, PreferenceConnector.access_token, token);
                        PreferenceConnector.writeString(LoginActivity.this, PreferenceConnector.FROM, "splash");
                        PreferenceConnector.writeString(LoginActivity.this, PreferenceConnector.Register_id, data.getResult().getRegisterId());
                        // PreferenceConnector.writeString(LoginActivity.this, PreferenceConnector.LANGUAGE, data.getResult().get);

                        startActivity(new Intent(LoginActivity.this, VerificationScreen.class)
                                .putExtra("user_name",binding.userName.getText().toString())
                                .putExtra("name",name)
                                .putExtra("email",email1)
                                .putExtra("password",password)
                                .putExtra("mobile",moble_no)
                                .putExtra("country_code",countryCode)
                                .putExtra("country","")
                                .putExtra("language",data.getResult().getLang)
                                .putExtra("type","User")
                                .putExtra("register_id",data.getResult().getRegisterId());
                        finish();

                    }
*/


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
    public void ask(String value, String status) {

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


