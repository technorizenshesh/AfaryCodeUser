package com.my.afarycode.OnlineShopping;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.my.afarycode.OnlineShopping.myorder.MyOrderScreen;
import com.my.afarycode.R;
import com.my.afarycode.databinding.ActivityMyprofileBinding;

public class MyprofileActivity extends AppCompatActivity {

    ActivityMyprofileBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_myprofile);

        binding.RRback.setOnClickListener(v -> {
            onBackPressed();
        });


        binding.txtMyOrder.setOnClickListener(v -> {
            startActivity(new Intent(MyprofileActivity.this, MyOrderScreen.class));

        });

        binding.order.setOnClickListener(v -> {
            Toast.makeText(this, "Success123!!!!", Toast.LENGTH_SHORT).show();
            //startActivity(new Intent(MyprofileActivity.this, MyOrderScreen.class));

        });



        binding.logout123.setOnClickListener(v -> {

            Toast.makeText(this, "Success!!!!", Toast.LENGTH_SHORT).show();
        /*    startActivity(new Intent(MyprofileActivity.this, LoginActivity.class));
            PreferenceConnector.writeString(MyprofileActivity.this, PreferenceConnector.LoginStatus, "false");
            finish();*/
        });

        binding.txtWishList.setOnClickListener(v -> {

            startActivity(new Intent(MyprofileActivity.this, WishListActivity.class));

        });

        binding.txtChangePassword.setOnClickListener(v -> {

            startActivity(new Intent(MyprofileActivity.this, ChangePassword.class));

        });

        binding.txtUpdate.setOnClickListener(v -> {

            startActivity(new Intent(MyprofileActivity.this, UpdateProfile.class));

        });

        binding.txtPrivacy.setOnClickListener(v -> {

            startActivity(new Intent(MyprofileActivity.this, PrivacyPolicy.class));

        });

        binding.txtTerms.setOnClickListener(v -> {
            startActivity(new Intent(MyprofileActivity.this, TermsCondition.class));
        });
    }
}