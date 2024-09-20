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
import com.my.afarycode.R;
import com.my.afarycode.databinding.ItemInnerBinding;

import java.util.ArrayList;


public class InnerAttributeAdapter extends RecyclerView.Adapter<InnerAttributeAdapter.MyViewHolder> {
       Context context;
       ArrayList<ShoppingProductModal.Result.ValidateName.AttributeName> arrayList;
       InnerClickListener listener;
       int position;
    public InnerAttributeAdapter(Context context, ArrayList<ShoppingProductModal.Result.ValidateName.AttributeName> arrayList,int position,InnerClickListener listener) {
        this.context = context;
        this.arrayList = arrayList;
        this.position = position;
        this.listener = listener;
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

            if(arrayList.get(position).isChk()==true) holder.binding.tvInnerName.setTextColor(context.getColor(R.color.red));
            else holder.binding.tvInnerName.setTextColor(context.getColor(R.color.black));


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

                binding.tvInnerName.setOnClickListener(view -> {
                    for(int i =0;i<arrayList.size();i++){
                        arrayList.get(i).setChk(false);
                    }
                    arrayList.get(getAdapterPosition()).setChk(true);
                    notifyDataSetChanged();
                  //  listener.innerClick(position,arrayList.get(position).getName(),getAdapterPosition());
                });

            }
        }
    }


