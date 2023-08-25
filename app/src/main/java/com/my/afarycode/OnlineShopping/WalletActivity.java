package com.my.afarycode.OnlineShopping;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.my.afarycode.OnlineShopping.Model.HomeShopeProductModel;
import com.my.afarycode.OnlineShopping.adapter.MyBookingAdapter;
import com.my.afarycode.OnlineShopping.adapter.WalletAdapter;
import com.my.afarycode.R;
import com.my.afarycode.databinding.ActivityWalletBinding;

import java.util.ArrayList;

public class WalletActivity extends AppCompatActivity {

    ActivityWalletBinding binding;

    WalletAdapter mAdapter;
    private ArrayList<HomeShopeProductModel> modelList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding= DataBindingUtil.setContentView(this,R.layout.activity_wallet);

        binding.RRback.setOnClickListener(v -> {

            onBackPressed();
        });

        setAdapter();
    }


    private void setAdapter() {

        modelList.add(new HomeShopeProductModel("Reliance Fresh",R.drawable.shert));
        modelList.add(new HomeShopeProductModel("Reliance Fresh",R.drawable.img3));

   /*     mAdapter = new WalletAdapter(WalletActivity.this,modelList);
        binding.recyclerWallet.setHasFixedSize(true);*/
        // use a linear layout manager
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(WalletActivity.this);
        binding.recyclerWallet.setLayoutManager(linearLayoutManager);
        binding.recyclerWallet.setAdapter(mAdapter);

    }
}