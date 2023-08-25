package com.my.afarycode.OnlineShopping.fragment.pharma;

import android.app.AlertDialog;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;

import com.my.afarycode.OnlineShopping.Model.HomeShopeProductModel;
import com.my.afarycode.OnlineShopping.fragment.HomeFragment;
import com.my.afarycode.OnlineShopping.servercommunication.GPSTracker;
import com.my.afarycode.R;
import com.my.afarycode.databinding.PharmicyFragmentBinding;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


public class PharmicyFragment extends Fragment {

    PharmicyFragmentBinding binding;
    private ArrayList<HomeShopeProductModel> modelList = new ArrayList<>();
    private GPSTracker gpsTracker;
    private View close;
    private LinearLayout pharmicy_list;
    private AlertDialog alertDialog;
    private PharmaDetailsFragment fragment1;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.pharmicy_fragment, container, false);

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

        SetupUI();

        binding.RRback.setOnClickListener(v -> {
            getFragmentManager().popBackStack();
        });

        return binding.getRoot();
    }

    private void SetupUI() {

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getContext());
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.alert_label_editor, null);
        close = dialogView.findViewById(R.id.close);
        pharmicy_list = dialogView.findViewById(R.id.pharmicy_list);

        close.setOnClickListener(v -> {
            alertDialog.dismiss();
        });

        pharmicy_list.setOnClickListener(v -> {
            fragment1 = new PharmaDetailsFragment();
            Bundle mBundle = new Bundle();
            mBundle.putString("cat_id", "");
            loadFragment(fragment1, mBundle);
            alertDialog.dismiss();
        });


        dialogBuilder.setView(dialogView);
        alertDialog = dialogBuilder.create();
        alertDialog.show();

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