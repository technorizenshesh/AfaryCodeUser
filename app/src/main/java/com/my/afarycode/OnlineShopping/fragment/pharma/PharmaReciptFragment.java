package com.my.afarycode.OnlineShopping.fragment.pharma;

import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;

import com.my.afarycode.OnlineShopping.CardActivity;
import com.my.afarycode.OnlineShopping.servercommunication.GPSTracker;
import com.my.afarycode.R;
import com.my.afarycode.databinding.PharmasisitOrderBinding;

import java.io.IOException;
import java.util.List;
import java.util.Locale;


public class PharmaReciptFragment extends Fragment {

    PharmasisitOrderBinding binding;
    private CardActivity fragment1;
    private GPSTracker gpsTracker;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.pharmasisit_order, container, false);

        gpsTracker = new GPSTracker(getActivity());
        double lat = gpsTracker.getLatitude();
        double lon = gpsTracker.getLongitude();

        Geocoder geocoder = new Geocoder(getContext(), Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(lat, lon, 1);
            Log.e("addresses>>>", "" + addresses.get(0).getAddressLine(0));
            binding.address.setText("" + addresses.get(0).getAddressLine(0));

        } catch (IOException e) {
            e.printStackTrace();
        }

        binding.paynow.setOnClickListener(v -> {
            fragment1 = new CardActivity();
            Bundle mBundle = new Bundle();
            mBundle.putString("cat_id", "");
            loadFragment(fragment1, mBundle);

        });

        return binding.getRoot();
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