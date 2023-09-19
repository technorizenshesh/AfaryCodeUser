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

import com.my.afarycode.OnlineShopping.AllShopOnlineActivity;
import com.my.afarycode.OnlineShopping.HomeShoppingOnlineScreen;
import com.my.afarycode.OnlineShopping.Model.GetRestorentsModal;
import com.my.afarycode.OnlineShopping.Model.GetTransferDetails;
import com.my.afarycode.OnlineShopping.Model.ShoppingProductModal;
import com.my.afarycode.OnlineShopping.Model.ShoppingStoreDetailsModal;
import com.my.afarycode.OnlineShopping.ShoppingProductDetail;
import com.my.afarycode.R;
import com.my.afarycode.databinding.ItemReviewItemBinding;
import com.my.afarycode.databinding.ItemWalletBinding;
import com.my.afarycode.databinding.NearMyHomeBinding;
import com.my.afarycode.databinding.ShoppingStoreAdapterBinding;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class WalletAdapter extends
        RecyclerView.Adapter<WalletAdapter.MyViewHolder> {

    private ArrayList<GetTransferDetails.Result> all_category_subcategory;
    private final Context activity;
    private ShoppingProductDetail fragment;

    public WalletAdapter(Context a, ArrayList<GetTransferDetails.Result> all_category_subcategory) {
        this.activity = a;
        this.all_category_subcategory = all_category_subcategory;
        // adapterCallback = ((CategoryAdapter.AdapterCallback1) activity);
    }

    @Override
    public WalletAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        ItemWalletBinding progressAdapterBinding = DataBindingUtil.inflate(LayoutInflater.from(activity)
                , R.layout.item_wallet, parent, false);

        return new MyViewHolder(progressAdapterBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, final int position) {

        holder.progressAdapterBinding.userName.setText(all_category_subcategory.get(position).getTransectionId());
        holder.progressAdapterBinding.amount.setText("$"+all_category_subcategory.get(position).amount);

    }

    @Override
    public int getItemCount() {
        return all_category_subcategory.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        ItemWalletBinding progressAdapterBinding;

        public MyViewHolder(ItemWalletBinding itemView) {
            super(itemView.getRoot());
            progressAdapterBinding = itemView;
        }
    }


}

