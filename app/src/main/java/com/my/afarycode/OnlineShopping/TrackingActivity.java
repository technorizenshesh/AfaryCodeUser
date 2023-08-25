package com.my.afarycode.OnlineShopping;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.os.Bundle;

import com.my.afarycode.R;
import com.my.afarycode.databinding.ActivityTrackingBinding;

public class TrackingActivity extends AppCompatActivity {

    ActivityTrackingBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding= DataBindingUtil.setContentView(this,R.layout.activity_tracking);

        binding.RRback.setOnClickListener(v -> {
            onBackPressed();
        });
    }
}