package com.my.afarycode.OnlineShopping.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.my.afarycode.OnlineShopping.Model.ShoppingProductModal;
import com.my.afarycode.R;
import com.my.afarycode.databinding.ItemInnerBinding;

import java.util.ArrayList;


public class InnerAttributeAdapter extends RecyclerView.Adapter<InnerAttributeAdapter.MyViewHolder> {
       Context context;
       ArrayList<ShoppingProductModal.Result.ValidateName.AttributeName> arrayList;


    public InnerAttributeAdapter(Context context, ArrayList<ShoppingProductModal.Result.ValidateName.AttributeName> arrayList) {
        this.context = context;
        this.arrayList = arrayList;
    }




    @NonNull
        @Override
        public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemInnerBinding binding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.item_inner,parent,false);
            return new MyViewHolder(binding);
        }

        @Override
        public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
            holder.binding.tvInnerName.setText(arrayList.get(position).getName());
        }

        @Override
        public int getItemCount() {
            return arrayList.size();
        }

        public class MyViewHolder extends RecyclerView.ViewHolder {
            ItemInnerBinding binding;
            public MyViewHolder(@NonNull ItemInnerBinding itemView) {
                super(itemView.getRoot());
                binding = itemView;
            }
        }
    }


