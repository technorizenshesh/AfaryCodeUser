package com.my.afarycode;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.databinding.DataBindingUtil;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
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
import com.my.afarycode.OnlineShopping.VerificationScreen;
import com.my.afarycode.OnlineShopping.WellcomeScreen;
import com.my.afarycode.OnlineShopping.constant.PreferenceConnector;
import com.my.afarycode.OnlineShopping.fragment.HomeFragment;
import com.my.afarycode.databinding.ActivitySplashBinding;

import static android.content.ContentValues.TAG;

import java.util.Locale;

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

        Intent intent11 = getIntent();
        if (intent11 != null) {
            String from = intent11.getStringExtra("from");
            String msg = intent11.getStringExtra("msg");
            String title = intent11.getStringExtra("title");

            if ("notification".equalsIgnoreCase(from)) {
                if (msg != null) {
                    AlertDialogAdminStatus(title, msg);
                } else {
                    // Handle the case where 'msg' is null
                    Log.e("MainActivity", "Message is null");
                    // Optionally, you might show a default message or take some other action
                }
            }
            else {
                Log.e("SplashActivity", "Notification is null");

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
        } else {
            // Handle the case where 'intent' is null
            Log.e("SplashActivity", "Intent is null");
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





    }

    private void finds() {

        FirebaseMessaging.getInstance().getToken().addOnSuccessListener(token -> {
            Log.e("token>>>>>", token);
            PreferenceConnector.writeString(Splash.this, PreferenceConnector.Firebash_Token, token);

        });



        String lang = PreferenceConnector.readString(Splash.this, PreferenceConnector.LANGUAGE, "");
        Log.e("Language====",lang);
        switch (lang) {
            case "en":
                changeLocale("en");
                break;
            case "fr":
                changeLocale("fr");
                break;
            default:
                changeLocale("en");
                break;

        }


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
                            .putExtra("type","").addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP));
                    finish();

                  /*  startActivity(new Intent(Splash.this, VerificationScreen.class)
                            .putExtra("type",""));
                    finish();*/
                }
            }
        }, 3000);



    }


    private boolean checkPermissions() {
        if (ActivityCompat.checkSelfPermission(Splash.this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(Splash.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
        ActivityCompat.checkSelfPermission(Splash.this, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED
        ) {
            return true;
        }
        return false;
    }


    private void requestPermissions() {
        ActivityCompat.requestPermissions(
                Splash.this,
                new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.POST_NOTIFICATIONS},
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

    private void changeLocale(String en) {
        updateResources(Splash.this,en);
        PreferenceConnector.writeString(Splash.this, PreferenceConnector.LANGUAGE, en);
    }


    private void updateResources(Context wellcomeScreen, String en) {
        Locale locale = new Locale(en);
        Locale.setDefault(locale);
        Resources resources = wellcomeScreen.getResources();
        Configuration configuration = resources.getConfiguration();
        configuration.locale = locale;
        resources.updateConfiguration(configuration, resources.getDisplayMetrics());

    }

    private void AlertDialogAdminStatus(String title,String msg) {
        AlertDialog.Builder builder = new AlertDialog.Builder(Splash.this);
        builder //.setTitle(getString(R.string.password_change_by_admin))
                .setTitle(title)
                .setMessage(msg)
                .setCancelable(false)
                .setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
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
                });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();



    }
}