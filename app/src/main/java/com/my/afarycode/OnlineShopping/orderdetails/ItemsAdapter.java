package com.my.afarycode.OnlineShopping.orderdetails;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;


import com.my.afarycode.R;
import com.my.afarycode.databinding.ItemDetailsBinding;

import java.util.ArrayList;

public class ItemsAdapter extends RecyclerView.Adapter<ItemsAdapter.MyViewHolder> {
    Context context;
    ArrayList<OrderDetailsModel.Result.Product> arrayList;


    public ItemsAdapter(Context context, ArrayList<OrderDetailsModel.Result.Product> arrayList) {
        this.context = context;
        this.arrayList = arrayList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemDetailsBinding binding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.item_details,parent,false);
        return new MyViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.binding.tvProductName.setText(arrayList.get(position).getProductName());
      //  holder.binding.tvProductPrice.setText(arrayList.get(position).getQuantity() + " X " + arrayList.get(position).getProductPrice() );
     //   holder.binding.tvProductTotal.setText(Integer.parseInt(arrayList.get(position).getQuantity()) * Integer.parseInt(arrayList.get(position).getProductPrice())+"");

        holder.binding.tvProductPrice.setText(arrayList.get(position).getQuantity() + " X " + arrayList.get(position).getPrice() );
        holder.binding.tvProductTotal.setText(Integer.parseInt(arrayList.get(position).getQuantity()) * Integer.parseInt(arrayList.get(position).getPrice())+"");

    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        ItemDetailsBinding binding;
        public MyViewHolder(@NonNull ItemDetailsBinding itemView) {
            super(itemView.getRoot());
            binding = itemView;
        }
    }
}