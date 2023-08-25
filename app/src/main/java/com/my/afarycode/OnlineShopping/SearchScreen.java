package com.my.afarycode.OnlineShopping;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.my.afarycode.OnlineShopping.Model.HomeShopeProductModel;
import com.my.afarycode.OnlineShopping.adapter.SearchProductAdapter;
import com.my.afarycode.OnlineShopping.adapter.ShopOnlineAdapter;
import com.my.afarycode.R;
import com.my.afarycode.databinding.ActivitySearchScreenBinding;

import java.util.ArrayList;

public class SearchScreen extends Fragment {

    SearchProductAdapter mAdapter;
    private ArrayList<HomeShopeProductModel> modelList = new ArrayList<>();

    ActivitySearchScreenBinding binding;
    Fragment fragment;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding=DataBindingUtil.inflate(inflater,R.layout.activity_search_screen,container,false);

        setAdapter();

        return binding.getRoot();
    }

    private void setAdapter() {

        modelList.add(new HomeShopeProductModel("Reliance Fresh",R.drawable.unplus));
        modelList.add(new HomeShopeProductModel("Reliance Fresh",R.drawable.unplus_second));
        modelList.add(new HomeShopeProductModel("Reliance Fresh",R.drawable.unplus));
        modelList.add(new HomeShopeProductModel("Reliance Fresh",R.drawable.unplus_second));
        modelList.add(new HomeShopeProductModel("Reliance Fresh",R.drawable.unplus_second));
        modelList.add(new HomeShopeProductModel("Reliance Fresh",R.drawable.c));


        mAdapter = new SearchProductAdapter(getActivity(),modelList);
        binding.recyclerResult.setHasFixedSize(true);
        // use a linear layout manager
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        binding.recyclerResult.setLayoutManager(new GridLayoutManager(getActivity(),2));
        binding.recyclerResult.setAdapter(mAdapter);

        mAdapter.SetOnItemClickListener(new SearchProductAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position, HomeShopeProductModel model) {


                fragment = new ShoppingProductDetail();
                loadFragment(fragment);

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