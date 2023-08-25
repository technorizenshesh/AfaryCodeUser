package com.my.afarycode;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_splash);

        FirebaseApp.initializeApp(this);

        finds();

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
}