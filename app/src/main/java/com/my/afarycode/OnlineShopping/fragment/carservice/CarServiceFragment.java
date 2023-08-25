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
import com.my.afarycode.R;
import com.my.afarycode.databinding.CarServiceBinding;


public class CarServiceFragment extends Fragment {

    CarServiceBinding binding;
    private RentCarFragment fragment1;
    private BuyCar fragment;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.car_service, container, false);

        SetupUI();

        binding.RRback.setOnClickListener(v -> {
            getFragmentManager().popBackStack();
        });

        return binding.getRoot();
    }

    private void SetupUI() {

        binding.buyCar.setOnClickListener(v -> {
            fragment = new BuyCar();
            Bundle mBundle = new Bundle();
            mBundle.putString("cat_id", "");
            loadFragment(fragment, mBundle);

        });

        binding.rentCar.setOnClickListener(v -> {
            fragment1 = new RentCarFragment();
            Bundle mBundle = new Bundle();
            mBundle.putString("cat_id", "");
            loadFragment(fragment1, mBundle);

        });

        binding.callTaxi.setOnClickListener(v -> {
            fragment1 = new RentCarFragment();
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