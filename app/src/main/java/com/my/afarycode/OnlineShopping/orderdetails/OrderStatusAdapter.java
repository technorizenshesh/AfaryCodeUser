package com.my.afarycode.OnlineShopping.orderdetails;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.my.afarycode.R;
import com.my.afarycode.databinding.ItemDeliveryStatusBinding;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class OrderStatusAdapter extends RecyclerView.Adapter<OrderStatusAdapter.MyViewHolder> {
    Context context;
    ArrayList<OrderDetailsModel.Result.DeliveryProgress> arrayList;

    public OrderStatusAdapter(Context context, ArrayList<OrderDetailsModel.Result.DeliveryProgress> arrayList) {
        this.context = context;
        this.arrayList = arrayList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemDeliveryStatusBinding binding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.item_delivery_status,parent,false);
        return new MyViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.binding.tvStatus.setText(arrayList.get(position).getObst_status());
        holder.binding.tvmsg.setText(arrayList.get(position).getObst_message());
        holder.binding.tvDateTime.setText(convertStringToDate(arrayList.get(position).getObst_created_at()) );

        if(arrayList.get(position).getObst_status().equals("PENDING")){
            holder.binding.tvStatus.setTextColor(context.getColor(R.color.teal_700));
            holder.binding.tvDateTime.setTextColor(context.getColor(R.color.teal_700));
            holder.binding.viewColor.setBackgroundColor(context.getColor(R.color.teal_700));

        }
        else  if(arrayList.get(position).getObst_status().equals("ACCEPT")){
            holder.binding.tvStatus.setTextColor(context.getColor(R.color.red));
            holder.binding.tvDateTime.setTextColor(context.getColor(R.color.red));
            holder.binding.viewColor.setBackgroundColor(context.getColor(R.color.red));
        }

        else  if(arrayList.get(position).getObst_status().equals("SHIPPING")){
            holder.binding.tvStatus.setTextColor(context.getColor(R.color.purple_700));
            holder.binding.tvDateTime.setTextColor(context.getColor(R.color.purple_700));
            holder.binding.viewColor.setBackgroundColor(context.getColor(R.color.purple_700));
        }

        else  if(arrayList.get(position).getObst_status().equals("DELIVERD")){
            holder.binding.tvStatus.setTextColor(context.getColor(R.color.colorGreen));
            holder.binding.tvDateTime.setTextColor(context.getColor(R.color.colorGreen));
            holder.binding.viewColor.setBackgroundColor(context.getColor(R.color.colorGreen));
        }


    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        ItemDeliveryStatusBinding binding;

        public MyViewHolder(@NonNull ItemDeliveryStatusBinding itemView) {
            super(itemView.getRoot());
            binding = itemView;
        }
    }

    public String convertStringToDate(String inputDate){
        String formattedDate="";
        try {
            String outputFormat = "dd MMM hh:mm a";

            // Define the input format
            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            // Parse the input date
            Date date = inputFormat.parse(inputDate);

            // Define the output format
            SimpleDateFormat outputFormatter = new SimpleDateFormat(outputFormat);
            // Format the date to the desired output
             formattedDate = outputFormatter.format(date);

            // Print the result
            System.out.println(formattedDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return formattedDate;
    }

}
