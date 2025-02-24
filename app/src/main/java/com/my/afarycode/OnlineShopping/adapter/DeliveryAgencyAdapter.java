package com.my.afarycode.OnlineShopping.adapter;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.my.afarycode.OnlineShopping.Model.DeliveryAgencyModel;
import com.my.afarycode.OnlineShopping.Model.LocationModel;
import com.my.afarycode.OnlineShopping.listener.onPosListener;
import com.my.afarycode.R;
import com.my.afarycode.databinding.ItemDeliveryAgencyBinding;
import com.my.afarycode.databinding.ItemLocationBinding;

import java.util.ArrayList;

public class DeliveryAgencyAdapter extends RecyclerView.Adapter<DeliveryAgencyAdapter.MyViewHolder> {
    Context context;
    ArrayList<DeliveryAgencyModel.Result> arrayList;
    onPosListener listener;
    Dialog mDialog;
    private int lastSelectedPosition = -1;

    public DeliveryAgencyAdapter(Context context, ArrayList<DeliveryAgencyModel.Result> arrayList, onPosListener listener, Dialog mDialog) {
        this.context = context;
        this.arrayList = arrayList;
        this.listener = listener;
        this.mDialog = mDialog;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemDeliveryAgencyBinding binding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.item_delivery_agency, parent, false);
        return new MyViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.binding.tvPrice.setText(arrayList.get(position).getShowCurrencyCode()+arrayList.get(position).getLocalPrice());
        holder.binding.rdHome.setText(arrayList.get(position).getName());
       // ivImg;
        Glide.with(context).load(arrayList.get(position).getImage()).into(holder.binding.ivImg);
        if(arrayList.get(position).isChk()) holder.binding.rdHome.setChecked(arrayList.get(position).isChk());
        else holder.binding.rdHome.setChecked(arrayList.get(position).isChk());

    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        ItemDeliveryAgencyBinding binding;

        public MyViewHolder(@NonNull ItemDeliveryAgencyBinding itemView) {
            super(itemView.getRoot());
            binding = itemView;


            binding.layoutMain.setOnClickListener(v -> {
                for (int i=0;i<arrayList.size();i++){
                    arrayList.get(i).setChk(false);
                }
                arrayList.get(getAdapterPosition()).setChk(true);
                listener.onPos(getAdapterPosition(),"deliveryAgency",mDialog);
                lastSelectedPosition = getAdapterPosition();
                notifyDataSetChanged();
            });





        }
    }



}