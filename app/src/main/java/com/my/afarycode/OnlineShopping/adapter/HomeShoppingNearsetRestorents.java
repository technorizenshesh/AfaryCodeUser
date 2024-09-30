package com.my.afarycode.OnlineShopping.adapter;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.my.afarycode.OnlineShopping.AllShopOnlineActivity;
import com.my.afarycode.OnlineShopping.Model.GetRestorentsModal;
import com.my.afarycode.OnlineShopping.Model.GetRestorentsModalCopy;
import com.my.afarycode.OnlineShopping.Model.ShopModel;
import com.my.afarycode.R;
import com.my.afarycode.databinding.NearMyHomeBinding;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class HomeShoppingNearsetRestorents extends
        RecyclerView.Adapter<HomeShoppingNearsetRestorents.MyViewHolder> {

    private List<ShopModel.Result> all_category_subcategory;
    List<ShopModel.Result> searchcategoryArrayList;
    private final Context activity;
    private AllShopOnlineActivity fragment;
    private int limit = 0;

    public HomeShoppingNearsetRestorents(Context a, List<ShopModel.Result> all_category_subcategory, int limit) {
        this.activity = a;
        this.all_category_subcategory = all_category_subcategory;
        this.limit = limit;

        this.searchcategoryArrayList = new ArrayList<>();
        searchcategoryArrayList.addAll(all_category_subcategory);
    }

    @Override
    public HomeShoppingNearsetRestorents.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        NearMyHomeBinding progressAdapterBinding = DataBindingUtil.inflate(LayoutInflater.from(activity)
                , R.layout.near_my_home, parent, false);

        return new MyViewHolder(progressAdapterBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, final int position) {

        holder.progressAdapterBinding.categoryName.setText(all_category_subcategory.get(position).getName());
        Glide.with(activity).load(all_category_subcategory.get(position).getImage1()).into(holder.progressAdapterBinding.img);
        holder.progressAdapterBinding.address.setText( all_category_subcategory.get(position).getAddress());


        if(!all_category_subcategory.get(position).getRating().equalsIgnoreCase(""))
        {
            holder.progressAdapterBinding.cardRate.setVisibility(View.VISIBLE);
            holder.progressAdapterBinding.tvRating.setText(all_category_subcategory.get(position).getRating());

        }
        else {
            holder.progressAdapterBinding.cardRate.setVisibility(View.GONE);

        }





        holder.progressAdapterBinding.categoryItem.setOnClickListener(v -> {

            Bundle bundle = new Bundle();
            bundle.putString("restorents_id", all_category_subcategory.get(position).getId());

            fragment = new AllShopOnlineActivity();
            fragment.setArguments(bundle);
            loadFragment(fragment);


        });
    }

    public void filter(String charText) {

        if (charText != null) {

            all_category_subcategory.clear();

            if (charText.isEmpty()) {

                all_category_subcategory.addAll(searchcategoryArrayList);

            } else {

                charText = charText.toLowerCase(Locale.getDefault());

                for (ShopModel.Result wp : searchcategoryArrayList) {

                    try {

                        if (wp.getName().toLowerCase().startsWith(charText)) {
                            all_category_subcategory.add(wp);
                        }

                    } catch (Exception e3) {

                        e3.printStackTrace();
                    }

                    notifyDataSetChanged();
                }

            }

            notifyDataSetChanged();
        }
    }

    @Override
    public int getItemCount() {
      //  if(all_category_subcategory.size()>limit) return limit;
     //   else return all_category_subcategory.size();

        return all_category_subcategory.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        NearMyHomeBinding progressAdapterBinding;

        public MyViewHolder(NearMyHomeBinding itemView) {
            super(itemView.getRoot());
            progressAdapterBinding = itemView;
        }
    }


    public boolean loadFragment(Fragment fragment) {
        FragmentManager fragmentManager = ((FragmentActivity) activity)
                .getSupportFragmentManager();

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

