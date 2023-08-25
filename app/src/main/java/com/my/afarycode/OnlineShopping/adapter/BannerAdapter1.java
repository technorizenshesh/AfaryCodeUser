package com.my.afarycode.OnlineShopping.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.my.afarycode.OnlineShopping.Model.BannerModal1;
import com.my.afarycode.R;
import com.my.afarycode.databinding.ItemBannerBinding;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class BannerAdapter1 extends RecyclerView.Adapter<BannerAdapter1.MyViewHolder> {

    private ArrayList<BannerModal1.Result> all_category_subcategory;
    private final Context activity;

    public BannerAdapter1(Context a, ArrayList<BannerModal1.Result> all_category_subcategory) {
        this.activity = a;
        this.all_category_subcategory = all_category_subcategory;
    }

    @Override
    public BannerAdapter1.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ItemBannerBinding progressAdapterBinding = DataBindingUtil.inflate(LayoutInflater.from(activity)
                , R.layout.item_banner, parent, false);
        return new MyViewHolder(progressAdapterBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, final int position) {

        Picasso.get().load(all_category_subcategory.get(position).image).into(holder.progressAdapterBinding.img);
    }

    @Override
    public int getItemCount() {
        return all_category_subcategory.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        ItemBannerBinding progressAdapterBinding;

        public MyViewHolder(ItemBannerBinding itemView) {
            super(itemView.getRoot());
            progressAdapterBinding = itemView;
        }
    }

    public static interface AdapterCallback1 {
        void onMethodCallback1(ArrayList<String> arrayList);
    }
}

