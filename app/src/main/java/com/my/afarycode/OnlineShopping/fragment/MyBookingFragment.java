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
import androidx.recyclerview.widget.LinearLayoutManager;

import com.my.afarycode.OnlineShopping.Model.HomeShopeProductModel;
import com.my.afarycode.OnlineShopping.MyBookingDetails;
import com.my.afarycode.OnlineShopping.UpdateProfile;
import com.my.afarycode.OnlineShopping.adapter.MyBookingAdapter;
import com.my.afarycode.R;
import com.my.afarycode.databinding.FragmentMyBookingBinding;
import com.my.afarycode.databinding.FragmentMyprofileBinding;

import java.util.ArrayList;


public class MyBookingFragment extends Fragment {

    FragmentMyBookingBinding binding;

    Fragment fragment;

    MyBookingAdapter mAdapter;
    private ArrayList<HomeShopeProductModel> modelList = new ArrayList<>();

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_my_booking, container, false);

        binding.RRback.setOnClickListener(v ->{
            getFragmentManager().popBackStack();
        });

        setAdapter();

        return binding.getRoot();

    }

    private void setAdapter() {

        modelList.add(new HomeShopeProductModel("Reliance Fresh",R.drawable.shert));
        modelList.add(new HomeShopeProductModel("Reliance Fresh",R.drawable.img3));

        mAdapter = new MyBookingAdapter(getActivity(),modelList);
        binding.recycler.setHasFixedSize(true);
        // use a linear layout manager
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        binding.recycler.setLayoutManager(linearLayoutManager);
        binding.recycler.setAdapter(mAdapter);

        mAdapter.SetOnItemClickListener(new MyBookingAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position, HomeShopeProductModel model) {


                fragment = new MyBookingDetails();
                loadFragment(fragment);

               // startActivity(new Intent(getActivity(),MyBookingDetails.class));

            }
        });
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