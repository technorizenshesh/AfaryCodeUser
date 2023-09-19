package com.my.afarycode;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.databinding.DataBindingUtil;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.google.firebase.FirebaseApp;
import com.google.firebase.messaging.FirebaseMessaging;
import com.my.afarycode.OnlineShopping.HomeActivity;
import com.my.afarycode.OnlineShopping.LoginActivity;
import com.my.afarycode.OnlineShopping.WellcomeScreen;
import com.my.afarycode.OnlineShopping.constant.PreferenceConnector;
import com.my.afarycode.databinding.ActivitySplashBinding;

import static android.content.ContentValues.TAG;

public class Splash extends AppCompatActivity {

    ActivitySplashBinding binding;
    private String token;
    int PERMISSION_ID = 44;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_splash);

        FirebaseApp.initializeApp(this);




        if (checkPermissions()) {
            if (isLocationEnabled()) {  finds();}
            else {
                Toast.makeText(Splash.this, "Turn on location", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);

            }
        } else {
            requestPermissions();
        }


    }

    private void finds() {

        FirebaseMessaging.getInstance().getToken().addOnSuccessListener(token -> {
            Log.e("token>>>>>", token);
            PreferenceConnector.writeString(Splash.this, PreferenceConnector.Firebash_Token, token);

        });


        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                if (PreferenceConnector.readString(Splash.this,
                        PreferenceConnector.LoginStatus, "").equals("true")) {
                    PreferenceConnector.writeString(Splash.this, PreferenceConnector.FROM, "splash");
                    Intent intent = new Intent(Splash.this, HomeActivity.class)
                            .putExtra("status", "login2");
                    startActivity(intent);
                    finish();
                } else {
                 //   Intent intent = new Intent(Splash.this, WellcomeScreen.class);
                    startActivity(new Intent(Splash.this, LoginActivity.class)
                            .putExtra("type",""));
                    finish();
                }
            }
        }, 3000);
    }


    private boolean checkPermissions() {
        if (ActivityCompat.checkSelfPermission(Splash.this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(Splash.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        return false;
    }


    private void requestPermissions() {
        ActivityCompat.requestPermissions(
                Splash.this,
                new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION},
                PERMISSION_ID
        );
    }

    private boolean isLocationEnabled() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
                LocationManager.NETWORK_PROVIDER
        );
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == PERMISSION_ID) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                finds();
            }
        }
    }




}