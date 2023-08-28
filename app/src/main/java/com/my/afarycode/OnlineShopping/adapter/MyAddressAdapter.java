package com.my.afarycode.OnlineShopping.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.my.afarycode.OnlineShopping.Model.LocationModel;
import com.my.afarycode.OnlineShopping.listener.onPosListener;
import com.my.afarycode.R;
import com.my.afarycode.databinding.ItemAddressBinding;
import com.my.afarycode.databinding.ItemLocationBinding;

import java.util.ArrayList;


public class MyAddressAdapter extends RecyclerView.Adapter<MyAddressAdapter.MyViewHolder> {
    Context context;
    ArrayList<LocationModel.Result> arrayList;
    onPosListener listener;
    private int lastSelectedPosition = -1;

    public MyAddressAdapter(Context context, ArrayList<LocationModel.Result> arrayList, onPosListener listener) {
        this.context = context;
        this.arrayList = arrayList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemAddressBinding binding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.item_address, parent, false);
        return new MyViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.binding.tvAddress.setText(arrayList.get(position).getAddress());
        holder.binding.rdHome.setText(arrayList.get(position).getAddressName());


    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        ItemAddressBinding binding;

        public MyViewHolder(@NonNull ItemAddressBinding itemView) {
            super(itemView.getRoot());
            binding = itemView;

            binding.ivDelete.setOnClickListener(v -> listener.onPos(getAdapterPosition(),"Delete"));

            binding.ivEdit.setOnClickListener(v -> listener.onPos(getAdapterPosition(),"Edit"));

        }
    }



}
