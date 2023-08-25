package com.my.afarycode.OnlineShopping.fragment;

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
import com.my.afarycode.OnlineShopping.adapter.MyAddAddressAdapter;
import com.my.afarycode.OnlineShopping.adapter.MyBookingAdapter;
import com.my.afarycode.R;
import com.my.afarycode.databinding.FragmentMyAddressBinding;
import com.my.afarycode.databinding.FragmentMyBookingBinding;

import java.util.ArrayList;


public class MyAddressFragment extends Fragment {

    FragmentMyAddressBinding binding;

    Fragment fragment;

    MyAddAddressAdapter mAdapter;
    private ArrayList<HomeShopeProductModel> modelList = new ArrayList<>();

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_my_address, container, false);

        binding.RRback.setOnClickListener(v ->{
            getFragmentManager().popBackStack();
        });

        setAdapter();

        GetAddressListAPI();

        return binding.getRoot();

    }

    private void GetAddressListAPI() {



    }

    private void setAdapter() {

        modelList.add(new HomeShopeProductModel("Home Address.",R.drawable.shert));
        modelList.add(new HomeShopeProductModel("Work Address.",R.drawable.img3));
        modelList.add(new HomeShopeProductModel("Pick From Store.",R.drawable.img3));

        mAdapter = new MyAddAddressAdapter(getActivity(),modelList);
        binding.recycler.setHasFixedSize(true);
        // use a linear layout manager
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        binding.recycler.setLayoutManager(linearLayoutManager);
        binding.recycler.setAdapter(mAdapter);

        mAdapter.SetOnItemClickListener(new MyAddAddressAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position, HomeShopeProductModel model) {



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