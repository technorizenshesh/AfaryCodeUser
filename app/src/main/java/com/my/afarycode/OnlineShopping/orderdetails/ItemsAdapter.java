package com.my.afarycode.OnlineShopping.orderdetails;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
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
    ItemOrderListener listener;

    public ItemsAdapter(Context context, ArrayList<OrderDetailsModel.Result.Product> arrayList,ItemOrderListener listener) {
        this.context = context;
        this.arrayList = arrayList;
        this.listener = listener;
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

     //   holder.binding.tvProductPrice.setText(arrayList.get(position).getQuantity() + " X " + arrayList.get(position).getPrice() );
     //   holder.binding.tvProductTotal.setText(Integer.parseInt(arrayList.get(position).getQuantity()) * Double.parseDouble(arrayList.get(position).getPrice())+"");

        holder.binding.tvProductPrice.setText(arrayList.get(position).getQuantity() + " X " + arrayList.get(position).getLocalPrice() );
        holder.binding.tvProductTotal.setText(Integer.parseInt(arrayList.get(position).getQuantity()) * Integer.parseInt(arrayList.get(position).getLocalPrice())+"");


        if(arrayList.get(position).getStatus().equals("Cancelled")){
            holder.binding.ivItem.setVisibility(View.GONE);
            holder.binding.tvCancelled.setVisibility(View.VISIBLE);
        }
        else  if(arrayList.get(position).getStatus().equals("Completed")){
            holder.binding.ivItem.setVisibility(View.GONE);
            holder.binding.tvCancelled.setVisibility(View.GONE);
        }

        else  if(arrayList.get(position).getStatus().equals("Cancelled_by_user")){
            holder.binding.ivItem.setVisibility(View.GONE);
            holder.binding.tvCancelled.setVisibility(View.VISIBLE);
        }

        else  if(arrayList.get(position).getStatus().equals("PickedUp")){
            holder.binding.ivItem.setVisibility(View.GONE);
            holder.binding.tvCancelled.setVisibility(View.GONE);
        }


        else {
            holder.binding.ivItem.setVisibility(View.VISIBLE);
            holder.binding.tvCancelled.setVisibility(View.GONE);
        }


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

            binding.ivItem.setOnClickListener(view -> listener.onItem(getAdapterPosition(),arrayList.get(getAdapterPosition())));
        }
    }
}