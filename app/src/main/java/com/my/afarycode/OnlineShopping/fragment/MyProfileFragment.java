package com.my.afarycode.OnlineShopping.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.my.afarycode.OnlineShopping.ChangePassword;
import com.my.afarycode.OnlineShopping.myorder.MyOrderScreen;
import com.my.afarycode.OnlineShopping.PrivacyPolicy;
import com.my.afarycode.OnlineShopping.TermsCondition;
import com.my.afarycode.OnlineShopping.UpdateProfile;
import com.my.afarycode.OnlineShopping.WishListActivity;
import com.my.afarycode.OnlineShopping.constant.PreferenceConnector;
import com.my.afarycode.R;
import com.my.afarycode.Splash;
import com.my.afarycode.databinding.FragmentMyprofileBinding;


public class MyProfileFragment extends Fragment {

    FragmentMyprofileBinding binding;
    Fragment fragment;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_myprofile, container, false);

        binding.RRback.setOnClickListener(v -> {
            getFragmentManager().popBackStack();
        });


        binding.txtMyOrder.setOnClickListener(v -> {

            startActivity(new Intent(getActivity(), MyOrderScreen.class));

        });


        binding.RRLogout.setOnClickListener(v -> {
            startActivity(new Intent(getContext(), Splash.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP));
            PreferenceConnector.writeString(getContext(), PreferenceConnector.LoginStatus, "false");
            getActivity().finish();
        });

        binding.txtWishList.setOnClickListener(v -> {

            fragment = new WishListActivity();
            loadFragment(fragment);


        });

        binding.txtChangePassword.setOnClickListener(v -> {
            fragment = new ChangePassword();
            loadFragment(fragment);

        });

        binding.txtUpdate.setOnClickListener(v -> {
            fragment = new UpdateProfile();
            loadFragment(fragment);

        });


        binding.txtOnlineOrder.setOnClickListener(v -> {
            startActivity(new Intent(getActivity(), MyOrderScreen.class));

        });


        binding.txtPrivacy.setOnClickListener(v -> {
            startActivity(new Intent(getActivity(), PrivacyPolicy.class));

        });




        binding.txtTerms.setOnClickListener(v -> {
            startActivity(new Intent(getActivity(), TermsCondition.class));
        });


        return binding.getRoot();

    }


    public boolean loadFragment(Fragment fragment) {
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        if (fragment != null) {
            fragmentManager.beginTransaction()
                    .addToBackStack("Home")
                    .replace(R.id.fragment_homeContainer, fragment)//, tag)
                    .commit();
            return true;
        }
        return false;
    }

}