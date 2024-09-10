package com.my.afarycode.OnlineShopping.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.my.afarycode.OnlineShopping.AllShopOnlineActivity;
import com.my.afarycode.OnlineShopping.Model.GetNotificationModal;
import com.my.afarycode.OnlineShopping.WishListActivity;
import com.my.afarycode.R;
import com.my.afarycode.databinding.ItemNotificationBinding;
import com.my.afarycode.ratrofit.AfaryCode;
import com.my.afarycode.ratrofit.ApiClient;

import java.util.ArrayList;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.MyViewHolder> {

    private final AfaryCode apiInterface;
    private ArrayList<GetNotificationModal.Result> all_category_subcategory;
    private final Context activity;
    private AllShopOnlineActivity fragment;
    private WishListActivity fragment1;

    public NotificationAdapter(Context a, ArrayList<GetNotificationModal.Result> all_category_subcategory) {
        this.activity = a;
        this.all_category_subcategory = all_category_subcategory;

        apiInterface = ApiClient.getClient(activity).create(AfaryCode.class);
    }

    @Override
    public NotificationAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        ItemNotificationBinding progressAdapterBinding = DataBindingUtil.inflate(LayoutInflater.from(activity)
                , R.layout.item_notification, parent, false);

        return new MyViewHolder(progressAdapterBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, final int position) {
        holder.progressAdapterBinding.tvText.setText(all_category_subcategory.get(position).getMessage());
      //  holder.progressAdapterBinding.notificationTitle.setText(all_category_subcategory.get(position).message);
      //  holder.progressAdapterBinding.time.setText(all_category_subcategory.get(position).dateTime);
    }


    @Override
    public int getItemCount() {
        return all_category_subcategory.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        ItemNotificationBinding progressAdapterBinding;

        public MyViewHolder(ItemNotificationBinding itemView) {
            super(itemView.getRoot());
            progressAdapterBinding = itemView;
        }
    }
}

