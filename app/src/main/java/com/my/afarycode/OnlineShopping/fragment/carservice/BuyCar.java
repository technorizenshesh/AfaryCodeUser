package com.my.afarycode.OnlineShopping.fragment.carservice;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;

import com.my.afarycode.OnlineShopping.Model.HomeShopeProductModel;
import com.my.afarycode.R;
import com.my.afarycode.databinding.BuyCarBinding;
import com.my.afarycode.databinding.RentCarFragmentBinding;

import java.util.ArrayList;


public class BuyCar extends Fragment {

    BuyCarBinding binding;
    private ArrayList<HomeShopeProductModel> modelList = new ArrayList<>();
    private PlatFormFeeFrag fragment1;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = DataBindingUtil.inflate(inflater, R.layout.buy_car, container, false);

        SetupUI();

        return binding.getRoot();
    }

    private void SetupUI() {
        binding.register.setOnClickListener(v -> {
            fragment1 = new PlatFormFeeFrag();
            Bundle mBundle = new Bundle();
            mBundle.putString("cat_id", "");
            loadFragment(fragment1, mBundle);
        });
    }

    public boolean loadFragment(Fragment fragment, Bundle mBundle) {
        FragmentManager fragmentManager = ((FragmentActivity) getActivity())
                .getSupportFragmentManager();
        fragment.setArguments(mBundle);
        if (fragment != null) {
            fragmentManager.beginTransaction()
                    .addToBackStack("Home")
                    .replace(R.id.fragment_homeContainer, fragment)
                    .commit();
            return true;
        }
        return false;
    }
}