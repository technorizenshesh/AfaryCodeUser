package com.my.afarycode.OnlineShopping.adapter;

import android.content.Context;
import android.content.Intent;
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
import com.my.afarycode.OnlineShopping.constant.PreferenceConnector;
import com.my.afarycode.R;
import com.my.afarycode.databinding.ItemProducts2Binding;
import com.my.afarycode.databinding.ItemProductsBinding;

import java.util.ArrayList;

public class ProductAdapter2 extends RecyclerView.Adapter<ProductAdapter2.MyViewHolder> {
    Context context;
    ArrayList<ProductItemModel.Result> arrayList;
    private AllShopOnlineActivity fragment;
    private int limit = 0;

    public ProductAdapter2(Context context, ArrayList<ProductItemModel.Result> arrayList,int limit) {
        this.context = context;
        this.arrayList = arrayList;
        this.limit = limit;

    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemProducts2Binding binding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.item_products2,parent,false);
        return new MyViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.binding.tvProductName.setText(arrayList.get(position).getProductName());
        holder.binding.tvBrandNAme.setText(arrayList.get(position).getProductBrand());
        holder.binding.tvProductPrice.setText(arrayList.get(position).getLocalCurrency()+arrayList.get(position).getLocalPrice());
        if(!arrayList.get(position).getProductImages().contains("/")) Glide.with(context).load("http://technorizen.com/afarycodewebsite/uploads/product/"+arrayList.get(position).getProductImages()).into(holder.binding.img);
         else Glide.with(context).load(arrayList.get(position).getProductImages()).into(holder.binding.img);
        holder.binding.img.setOnClickListener(v -> {
            PreferenceConnector.writeString(context,PreferenceConnector.Cat_id,arrayList.get(position).getCatId());
            context.startActivity(new Intent(context, ProductDetailAct.class)
                    .putExtra("product_id",arrayList.get(position).getProId())
                    .putExtra("restaurant_id",arrayList.get(position).getRestaurantId())
                    .putExtra("productPrice",arrayList.get(position).getLocalPrice()));
        });


        if(arrayList.get(position).getDeliveryCharges().equals("1")) {
            holder.binding.ivDeliveryType.setVisibility(View.VISIBLE);
        }
        else {
            holder.binding.ivDeliveryType.setVisibility(View.GONE);
        }




    }

    @Override
    public int getItemCount() {
       // if(arrayList.size()>limit) return limit;
     //   else return arrayList.size();

        return arrayList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        ItemProducts2Binding binding;
        public MyViewHolder(@NonNull ItemProducts2Binding itemView) {
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
