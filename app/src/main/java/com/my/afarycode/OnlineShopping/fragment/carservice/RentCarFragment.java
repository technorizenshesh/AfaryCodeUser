package com.my.afarycode.OnlineShopping.fragment.carservice;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.my.afarycode.OnlineShopping.Model.HomeShopeProductModel;
import com.my.afarycode.R;
import com.my.afarycode.databinding.RentCarFragmentBinding;

import java.util.ArrayList;


public class RentCarFragment extends Fragment {

    RentCarFragmentBinding binding;
    private ArrayList<HomeShopeProductModel> modelList = new ArrayList<>();

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = DataBindingUtil.inflate(inflater, R.layout.rent_car_fragment, container, false);

        return binding.getRoot();
    }
}