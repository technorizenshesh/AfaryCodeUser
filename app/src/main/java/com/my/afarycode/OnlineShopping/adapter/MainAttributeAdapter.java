package com.my.afarycode.OnlineShopping.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.my.afarycode.OnlineShopping.Model.ShoppingProductModal;
import com.my.afarycode.OnlineShopping.listener.InnerClickListener;
import com.my.afarycode.OnlineShopping.listener.MainClickListener;
import com.my.afarycode.R;
import com.my.afarycode.databinding.ItemMainBinding;

import java.util.ArrayList;

public class MainAttributeAdapter extends RecyclerView.Adapter<MainAttributeAdapter.MyViewHolder> implements InnerClickListener {

    Context context;
    ArrayList<ShoppingProductModal.Result.ValidateName>arrayList;
    MainClickListener listener;

    public MainAttributeAdapter(Context context, ArrayList<ShoppingProductModal.Result.ValidateName> arrayList,MainClickListener listener) {
        this.context = context;
        this.arrayList = arrayList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemMainBinding binding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.item_main,parent,false);
        return new MyViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        holder.binding.tvmainName.setText(arrayList.get(position).getName());
        holder.binding.rvInner.setAdapter(new InnerAttributeAdapter(context, (ArrayList<ShoppingProductModal.Result.ValidateName.AttributeName>) arrayList.get(position).getAttributeName(),position,MainAttributeAdapter.this));
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    @Override
    public void innerClick(int position, String val,int innerPosition) {
          listener.mainClick(arrayList.get(position).getName(),val,position,innerPosition);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        ItemMainBinding binding;
        public MyViewHolder(@NonNull ItemMainBinding itemView) {
            super(itemView.getRoot());
            binding = itemView;
        }
    }
}
