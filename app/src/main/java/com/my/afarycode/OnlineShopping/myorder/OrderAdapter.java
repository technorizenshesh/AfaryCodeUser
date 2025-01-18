package com.my.afarycode.OnlineShopping.myorder;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
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
        holder.binding.orderId.setText(context.getString(R.string.order_number)+ " "+arrayList.get(position).getOrderId());
      //  double total = Double.parseDouble(arrayList.get(position).getPrice())
     //           + Double.parseDouble(arrayList.get(position).getPlatFormsFees())
      //          + Double.parseDouble(arrayList.get(position).getDeliveryCharges())
      //          + Double.parseDouble(arrayList.get(position).getTaxN1())
     //           + Double.parseDouble(arrayList.get(position).getTaxN2());
        holder.binding.orderPrice.setText(context.getString(R.string.total_price)+ " "+"FCFA"+parseFrenchNumber(arrayList.get(position).getTotalAmount()));
        holder.binding.tvShopName.setText(arrayList.get(position).getProductList().get(0).getShopName());
        holder.binding.tvProductName.setText(context.getString(R.string.product_name1)+" "+arrayList.get(position).getProductList().get(0).getProductName());
        holder.binding.tvDateTime.setText(context.getString(R.string.order_time)+" "+arrayList.get(position).getDateTime());

        Log.e("order status====",arrayList.get(position).getStatus());

        if(arrayList.get(position).getProductList().size() == 1){
            holder.binding.llOne.setVisibility(View.VISIBLE);
            holder.binding.llTwo.setVisibility(View.GONE);
            holder.binding.llThree.setVisibility(View.GONE);
            holder.binding.llFour.setVisibility(View.GONE);
            Glide.with(context).load(arrayList.get(position).getProductList().get(0).getProductImages()).into(holder.binding.productImage);

        }

       else if(arrayList.get(position).getProductList().size() == 2){
            holder.binding.llOne.setVisibility(View.GONE);
            holder.binding.llTwo.setVisibility(View.VISIBLE);
            holder.binding.llThree.setVisibility(View.GONE);
            holder.binding.llFour.setVisibility(View.GONE);
            Glide.with(context).load(arrayList.get(position).getProductList().get(0).getProductImages()).into(holder.binding.productImageTw01);
            Glide.with(context).load(arrayList.get(position).getProductList().get(1).getProductImages()).into(holder.binding.productImageTwo2);

       }

        else if(arrayList.get(position).getProductList().size() == 3){
            holder.binding.llOne.setVisibility(View.GONE);
            holder.binding.llTwo.setVisibility(View.GONE);
            holder.binding.llThree.setVisibility(View.VISIBLE);
            holder.binding.llFour.setVisibility(View.GONE);
            Glide.with(context).load(arrayList.get(position).getProductList().get(0).getProductImages()).into(holder.binding.productImageThree1);
            Glide.with(context).load(arrayList.get(position).getProductList().get(1).getProductImages()).into(holder.binding.productImageThree2);
            Glide.with(context).load(arrayList.get(position).getProductList().get(2).getProductImages()).into(holder.binding.productImageThree3);

        }

        else if(arrayList.get(position).getProductList().size() <= 4){
            holder.binding.llOne.setVisibility(View.GONE);
            holder.binding.llTwo.setVisibility(View.GONE);
            holder.binding.llThree.setVisibility(View.GONE);
            holder.binding.llFour.setVisibility(View.VISIBLE);
            Glide.with(context).load(arrayList.get(position).getProductList().get(0).getProductImages()).into(holder.binding.productImageFour1);
            Glide.with(context).load(arrayList.get(position).getProductList().get(1).getProductImages()).into(holder.binding.productImageFour2);
            Glide.with(context).load(arrayList.get(position).getProductList().get(2).getProductImages()).into(holder.binding.productImageFour3);
            holder.binding.tvImgCount.setText("+"+(arrayList.get(position).getProductList().size()-3));

        }


        if(arrayList.get(position).getStatus().equals("Pending")){

            holder.binding.orderStatus.setVisibility(View.VISIBLE);
            holder.binding.orderStatus.setText(arrayList.get(position).getStatus());
            holder.binding.orderStatus.setBackgroundColor(context.getColor(R.color.blue_circle));
            holder.binding.btnCancel.setVisibility(View.GONE);
            holder.binding.mainCard.setCardBackgroundColor(context.getColor(R.color.white));

        }

       else if(arrayList.get(position).getStatus().equals("Accepted_by_admin")){
            holder.binding.orderStatus.setVisibility(View.VISIBLE);
            holder.binding.orderStatus.setText(context.getString(R.string.pending));
            holder.binding.orderStatus.setBackgroundColor(context.getColor(R.color.blue_circle));
            holder.binding.btnCancel.setVisibility(View.GONE);
            holder.binding.mainCard.setCardBackgroundColor(context.getColor(R.color.white));

        }

       else if(arrayList.get(position).getStatus().equals("Accepted")
        ){

            holder.binding.orderStatus.setVisibility(View.VISIBLE);
            holder.binding.orderStatus.setText(arrayList.get(position).getStatus());

            holder.binding.orderStatus.setBackgroundColor(context.getColor(R.color.colorGreen));
            holder.binding.btnCancel.setVisibility(View.GONE);
            holder.binding.mainCard.setCardBackgroundColor(context.getColor(R.color.white));

        }

        else if(arrayList.get(position).getStatus().equals("In_Transit")
        ){

            holder.binding.orderStatus.setVisibility(View.VISIBLE);
            holder.binding.orderStatus.setText(context.getString(R.string.transit));
            holder.binding.orderStatus.setBackgroundColor(context.getColor(R.color.colorGreen));
            holder.binding.btnCancel.setVisibility(View.GONE);
            holder.binding.mainCard.setCardBackgroundColor(context.getColor(R.color.white));

        }

        else if(arrayList.get(position).getStatus().equals("Cancelled")){
            holder.binding.orderStatus.setVisibility(View.VISIBLE);
            holder.binding.orderStatus.setText(arrayList.get(position).getStatus());
            holder.binding.orderStatus.setBackgroundColor(context.getColor(R.color.red));
            holder.binding.btnCancel.setVisibility(View.GONE);
            holder.binding.mainCard.setCardBackgroundColor(context.getColor(R.color.colorGray));

        }

        else if(arrayList.get(position).getStatus().equals("Cancelled_by_user")){

            holder.binding.orderStatus.setVisibility(View.VISIBLE);
            holder.binding.orderStatus.setText(context.getString(R.string.cancelled));
            holder.binding.orderStatus.setBackgroundColor(context.getColor(R.color.red));
            holder.binding.btnCancel.setVisibility(View.GONE);
            holder.binding.mainCard.setCardBackgroundColor(context.getColor(R.color.colorGray));

        }


        else if(arrayList.get(position).getStatus().equals("Reached_shipping_company")){
            holder.binding.btnCancel.setVisibility(View.GONE);
            holder.binding.orderStatus.setVisibility(View.VISIBLE);
            holder.binding.orderStatus.setText(context.getString(R.string.reached_to_shipping_company));
            holder.binding.orderStatus.setBackgroundColor(context.getColor(R.color.colorGreen));

        }


        else {
            holder.binding.btnCancel.setVisibility(View.GONE);
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

    private int parseFrenchNumber(String number) {
        // Remove the commas and parse to an integer
        String cleanedNumber = number.replace(",", "");
        return Integer.parseInt(cleanedNumber);
    }

}
