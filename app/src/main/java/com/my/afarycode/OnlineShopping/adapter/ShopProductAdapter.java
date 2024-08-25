package com.my.afarycode.OnlineShopping.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.my.afarycode.OnlineShopping.Model.ShopDetailModel;
import com.my.afarycode.R;
import com.my.afarycode.databinding.ItemProductBinding;

import java.util.ArrayList;

public class ShopProductAdapter extends RecyclerView.Adapter<ShopProductAdapter.MyViewHolder> {
    Context context;
    ArrayList<ShopDetailModel.Result.Product> arrayList;
    String currency="";
    public ShopProductAdapter(Context context,ArrayList<ShopDetailModel.Result.Product>arrayList,String currency) {
        this.context = context;
        this.arrayList = arrayList;
        this.currency = currency;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemProductBinding binding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.item_product,parent,false);
        return new MyViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.binding.tvProduct.setText(arrayList.get(position).getProductName());
        holder.binding.tvProductPrice.setText(currency+arrayList.get(position).getProductPrice());
        Glide.with(context).load(arrayList.get(position).getImage1())
                .override(250,250).into(holder.binding.ivImg);

        if(arrayList.get(position).getDeliveryCharges().equals("0")) holder.binding.tvFeeType.setVisibility(View.VISIBLE);
        else holder.binding.tvFeeType.setVisibility(View.GONE);

        if(arrayList.get(position).getApproval().equals("1")) holder.binding.tvProductApproval.setVisibility(View.GONE);
        else holder.binding.tvProductApproval.setVisibility(View.VISIBLE);

       /* if(arrayList.get(position).getStatus().equals("1")) {
            holder.binding.tvStatus.setTextColor(context.getColor(R.color.colorGreen));
            holder.binding.tvStatus.setText(context.getString(R.string.active));
        }
        else {
            holder.binding.tvStatus.setTextColor(context.getColor(R.color.red));
            holder.binding.tvStatus.setText(context.getString(R.string.deactive));        }*/

        if(arrayList.get(position).getDeliveryCharges().equalsIgnoreCase("0"))
            holder.binding.ivDeliveryType.setVisibility(View.VISIBLE);
        else holder.binding.ivDeliveryType.setVisibility(View.GONE);

    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        ItemProductBinding binding;
        public MyViewHolder(@NonNull ItemProductBinding itemView) {
            super(itemView.getRoot());
            binding = itemView;

        }
    }
}
