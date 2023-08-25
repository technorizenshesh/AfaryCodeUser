package com.my.afarycode.OnlineShopping;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.Intent;
import android.os.Bundle;

import com.my.afarycode.R;
import com.my.afarycode.databinding.ActivitySuccessScreenBinding;

public class SuccessScreen extends AppCompatActivity {

    ActivitySuccessScreenBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding= DataBindingUtil.setContentView(this,R.layout.activity_success_screen);

        binding.RRHOme.setOnClickListener(v -> {
            startActivity(new Intent(SuccessScreen.this,HomeActivity.class)
                    .putExtra("status", "login1"));
            finish();
        });
    }
}