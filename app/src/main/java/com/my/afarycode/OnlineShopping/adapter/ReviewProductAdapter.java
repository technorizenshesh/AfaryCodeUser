package com.my.afarycode.OnlineShopping.adapter;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.my.afarycode.OnlineShopping.Model.HomeOfferModel;
import com.my.afarycode.OnlineShopping.Model.ShoppingProductModal;
import com.my.afarycode.R;
import com.my.afarycode.databinding.ItemReviewItemBinding;

import java.util.ArrayList;

public class ReviewProductAdapter extends RecyclerView.Adapter<ReviewProductAdapter.MyViewHolder> {

    private Context mContext;
    private ArrayList<ShoppingProductModal.Result.review> modelList;

    public ReviewProductAdapter(Context context, ArrayList<ShoppingProductModal.Result.review> modelList) {
        this.mContext = context;
        this.modelList = modelList;
    }


    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        ItemReviewItemBinding binding = DataBindingUtil.inflate(LayoutInflater.from(mContext),R.layout.item_review_item,viewGroup,false);
        return new MyViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        //Here you can fill your row view
           holder.binding.userName.setText(modelList.get(position).getUserName());
        holder.binding.comments.setText(modelList.get(position).getProductReview());
        holder.binding.ratingBar.setRating(Float.parseFloat(modelList.get(position).getProductRating()));
        Glide.with(mContext).load(modelList.get(position).getImage()).placeholder(R.drawable.user_default)
                .override(70,70).into(holder.binding.ivPic);
        }




    @Override
    public int getItemCount() {

        return modelList.size();
    }





    public class MyViewHolder extends RecyclerView.ViewHolder {

        ItemReviewItemBinding binding;
        public MyViewHolder(final ItemReviewItemBinding itemView) {
            super(itemView.getRoot());
           binding = itemView;

        }
    }


}

