package com.my.afarycode.OnlineShopping;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.my.afarycode.OnlineShopping.Model.HomeShopeProductModel;
import com.my.afarycode.OnlineShopping.adapter.MyBookingAdapter;
import com.my.afarycode.R;
import com.my.afarycode.databinding.ActivityMyBookingBinding;

import java.util.ArrayList;

public class MyBookingActivity extends AppCompatActivity {

    ActivityMyBookingBinding binding;
    MyBookingAdapter mAdapter;
    private ArrayList<HomeShopeProductModel> modelList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_my_booking);

        binding.RRback.setOnClickListener(v -> {

            onBackPressed();

        });

        setAdapter();
    }

    private void setAdapter() {

        modelList.add(new HomeShopeProductModel("Reliance Fresh", R.drawable.shert));
        modelList.add(new HomeShopeProductModel("Reliance Fresh", R.drawable.img3));

        mAdapter = new MyBookingAdapter(MyBookingActivity.this, modelList);
        binding.recycler.setHasFixedSize(true);


        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(MyBookingActivity.this);
        binding.recycler.setLayoutManager(linearLayoutManager);
        binding.recycler.setAdapter(mAdapter);

        mAdapter.SetOnItemClickListener(new MyBookingAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position, HomeShopeProductModel model) {
                startActivity(new Intent(MyBookingActivity.this, MyBookingDetails.class));
            }
        });
    }
}