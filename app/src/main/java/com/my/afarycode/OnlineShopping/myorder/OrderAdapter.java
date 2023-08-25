package com.my.afarycode.OnlineShopping.myorder;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;


import com.bumptech.glide.Glide;
import com.my.afarycode.OnlineShopping.orderdetails.OrderDetailsAct;
import com.my.afarycode.R;
import com.my.afarycode.databinding.ItemOrderBinding;

import java.util.ArrayList;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.MyViewHolder> {
    Context context;
    ArrayList<OrderModel.Result>arrayList;
    OrderListener listener;

    public OrderAdapter(Context context, ArrayList<OrderModel.Result> arrayList, OrderListener listener) {
        this.context = context;
        this.arrayList = arrayList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemOrderBinding binding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.item_order,parent,false);
        return new MyViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.binding.orderId.setText(arrayList.get(position).getOrderId());
        holder.binding.orderPrice.setText("Rs"+arrayList.get(position).getTotalText());
        if(arrayList.get(position).getProductImageList().size() == 1){
            holder.binding.llOne.setVisibility(View.VISIBLE);
            holder.binding.llTwo.setVisibility(View.GONE);
            holder.binding.llThree.setVisibility(View.GONE);
            holder.binding.llFour.setVisibility(View.GONE);
            Glide.with(context).load(arrayList.get(position).getProductImageList().get(0).getImage()).into(holder.binding.productImage);

        }

       else if(arrayList.get(position).getProductImageList().size() == 2){
            holder.binding.llOne.setVisibility(View.GONE);
            holder.binding.llTwo.setVisibility(View.VISIBLE);
            holder.binding.llThree.setVisibility(View.GONE);
            holder.binding.llFour.setVisibility(View.GONE);
            Glide.with(context).load(arrayList.get(position).getProductImageList().get(0).getImage()).into(holder.binding.productImageTw01);
            Glide.with(context).load(arrayList.get(position).getProductImageList().get(1).getImage()).into(holder.binding.productImageTwo2);

       }

        else if(arrayList.get(position).getProductImageList().size() == 3){
            holder.binding.llOne.setVisibility(View.GONE);
            holder.binding.llTwo.setVisibility(View.GONE);
            holder.binding.llThree.setVisibility(View.VISIBLE);
            holder.binding.llFour.setVisibility(View.GONE);
            Glide.with(context).load(arrayList.get(position).getProductImageList().get(0).getImage()).into(holder.binding.productImageThree1);
            Glide.with(context).load(arrayList.get(position).getProductImageList().get(1).getImage()).into(holder.binding.productImageThree2);
            Glide.with(context).load(arrayList.get(position).getProductImageList().get(2).getImage()).into(holder.binding.productImageThree3);

        }

        else if(arrayList.get(position).getProductImageList().size() <= 4){
            holder.binding.llOne.setVisibility(View.GONE);
            holder.binding.llTwo.setVisibility(View.GONE);
            holder.binding.llThree.setVisibility(View.GONE);
            holder.binding.llFour.setVisibility(View.VISIBLE);
            Glide.with(context).load(arrayList.get(position).getProductImageList().get(0).getImage()).into(holder.binding.productImageFour1);
            Glide.with(context).load(arrayList.get(position).getProductImageList().get(1).getImage()).into(holder.binding.productImageFour2);
            Glide.with(context).load(arrayList.get(position).getProductImageList().get(2).getImage()).into(holder.binding.productImageFour3);
            holder.binding.tvImgCount.setText("+"+(arrayList.get(position).getProductImageList().size()-3));

        }


        if(arrayList.get(position).getStatus().equals("Pending")){

            holder.binding.orderStatus.setVisibility(View.GONE);

        }

       else if(arrayList.get(position).getStatus().equals("Accept")){

            holder.binding.orderStatus.setVisibility(View.VISIBLE);
            holder.binding.orderStatus.setText(arrayList.get(position).getStatus());

            holder.binding.orderStatus.setBackgroundColor(context.getColor(R.color.colorGreen));

        }


        else if(arrayList.get(position).getStatus().equals("Decline")){

            holder.binding.orderStatus.setVisibility(View.VISIBLE);
            holder.binding.orderStatus.setText(arrayList.get(position).getStatus());
            holder.binding.orderStatus.setBackgroundColor(context.getColor(R.color.red));

        }


        else {

            holder.binding.orderStatus.setVisibility(View.VISIBLE);
            holder.binding.orderStatus.setText(arrayList.get(position).getStatus());

            holder.binding.orderStatus.setBackgroundColor(context.getColor(R.color.colorGreen));

        }


    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        ItemOrderBinding binding;
        public MyViewHolder(@NonNull ItemOrderBinding itemView) {
            super(itemView.getRoot());
            binding = itemView;



            binding.linearText.setOnClickListener(v ->
            {
                context.startActivity(new Intent(context, OrderDetailsAct.class)
                            .putExtra("id",arrayList.get(getAdapterPosition()).getOrderId()+""));
            });




        }
    }
}
