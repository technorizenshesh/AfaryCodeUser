package com.my.afarycode.OnlineShopping.adapter;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.my.afarycode.OnlineShopping.AllShopOnlineActivity;
import com.my.afarycode.OnlineShopping.HomeShoppingOnlineScreen;
import com.my.afarycode.OnlineShopping.Model.GetRestorentsModal;
import com.my.afarycode.OnlineShopping.Model.ShoppingStoreDetailsModal;
import com.my.afarycode.OnlineShopping.ShoppingProductDetail;
import com.my.afarycode.R;
import com.my.afarycode.databinding.NearMyHomeBinding;
import com.my.afarycode.databinding.ShoppingStoreAdapterBinding;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class ShoppingStoreAdapter extends
        RecyclerView.Adapter<ShoppingStoreAdapter.MyViewHolder> {

    private ArrayList<ShoppingStoreDetailsModal.Result> all_category_subcategory;
    private final Context activity;
    private ShoppingProductDetail fragment;

    public ShoppingStoreAdapter(Context a, ArrayList<ShoppingStoreDetailsModal.Result> all_category_subcategory) {
        this.activity = a;
        this.all_category_subcategory = all_category_subcategory;
        // adapterCallback = ((CategoryAdapter.AdapterCallback1) activity);
    }

    @Override
    public ShoppingStoreAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        ShoppingStoreAdapterBinding progressAdapterBinding = DataBindingUtil.inflate(LayoutInflater.from(activity)
                , R.layout.shopping_store_adapter, parent, false);

        return new MyViewHolder(progressAdapterBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, final int position) {

        holder.progressAdapterBinding.productName.setText(all_category_subcategory.get(position).productName);
        holder.progressAdapterBinding.address.setText(all_category_subcategory.get(position).getDescription());


        if (!all_category_subcategory.get(position).image.equals("")) {
           // Picasso.get().load(all_category_subcategory.get(position).image).into(holder.progressAdapterBinding.img);
            Glide.with(activity).load(all_category_subcategory.get(position).image).into(holder.progressAdapterBinding.img);

        }

        holder.progressAdapterBinding.txtPrice.setText(all_category_subcategory.get(position).getLocalCurrency() + all_category_subcategory.get(position).getProductPrice());

        holder.progressAdapterBinding.categoryItem.setOnClickListener(v -> {

            Bundle bundle = new Bundle();
            bundle.putString("product_id", all_category_subcategory.get(position).proId);
            bundle.putString("restaurant_id", all_category_subcategory.get(position).restaurant_id);
            bundle.putString("productPrice", all_category_subcategory.get(position).productPrice);

            fragment = new ShoppingProductDetail();
            fragment.setArguments(bundle);
            loadFragment(fragment);

  /*          fragment = new ShoppingProductDetail();
            loadFragment(fragment);*/

            //Toast.makeText(activity, "" + all_category_subcategory.get(position).id, Toast.LENGTH_SHORT).show();

        });
    }

    @Override
    public int getItemCount() {
        return all_category_subcategory.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        ShoppingStoreAdapterBinding progressAdapterBinding;

        public MyViewHolder(ShoppingStoreAdapterBinding itemView) {
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

