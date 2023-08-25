package com.my.afarycode.OnlineShopping.adapter;

import android.content.Context;
import android.content.Intent;
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
import com.my.afarycode.OnlineShopping.Model.ProductItemModel;
import com.my.afarycode.OnlineShopping.ProductDetailAct;
import com.my.afarycode.OnlineShopping.ShoppingProductDetail;
import com.my.afarycode.R;
import com.my.afarycode.databinding.ItemProductsBinding;

import java.util.ArrayList;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.MyViewHolder> {
    Context context;
    ArrayList<ProductItemModel.Result>arrayList;
    private Fragment fragment;

    public ProductAdapter(Context context, ArrayList<ProductItemModel.Result> arrayList) {
        this.context = context;
        this.arrayList = arrayList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemProductsBinding binding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.item_products,parent,false);
        return new MyViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.binding.tvProductName.setText(arrayList.get(position).getProductName());
        holder.binding.tvBrandNAme.setText(arrayList.get(position).getProductBrand());
        holder.binding.tvProductPrice.setText("Rs."+arrayList.get(position).getProductPrice());
        Glide.with(context).load(arrayList.get(position).getImage()).into(holder.binding.img);




        holder.binding.img.setOnClickListener(v -> {
                context.startActivity(new Intent(context, ProductDetailAct.class)
              .putExtra("product_id",arrayList.get(position).getProId())
                   .putExtra("restaurant_id",arrayList.get(position).getRestaurantId())
                   .putExtra("productPrice",arrayList.get(position).getProductPrice()));

         /*          Bundle bundle = new Bundle();
            bundle.putString("product_id", arrayList.get(position).getProId());
            bundle.putString("restaurant_id", arrayList.get(position).getRestaurantId());
            bundle.putString("productPrice", arrayList.get(position).getProductPrice());

            fragment = new ShoppingProductDetail();
            fragment.setArguments(bundle);
            loadFragment(fragment);*/
        });

        if(arrayList.get(position).getDeliveryCharges().equalsIgnoreCase("0")){
            holder.binding.tvDelivery.setVisibility(View.VISIBLE);
        } else holder.binding.tvDelivery.setVisibility(View.GONE);


    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        ItemProductsBinding binding;
        public MyViewHolder(@NonNull ItemProductsBinding itemView) {
            super(itemView.getRoot());
            binding = itemView;
        }
    }

    public boolean loadFragment(Fragment fragment) {
        FragmentManager fragmentManager = ((FragmentActivity) context)
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
