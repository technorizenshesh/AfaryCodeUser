package com.my.afarycode.OnlineShopping;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.my.afarycode.R;
import com.my.afarycode.databinding.ActivityMyBookingDetailsBinding;

public class MyBookingDetails extends Fragment {

    ActivityMyBookingDetailsBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding=DataBindingUtil.inflate(inflater,R.layout.activity_my_booking_details,container,false);

        binding.RRback.setOnClickListener(v -> {

            getActivity().onBackPressed();

        });

        return binding.getRoot();

    }

}