package com.my.afarycode.OnlineShopping.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.my.afarycode.OnlineShopping.Model.CountryModel;
import com.my.afarycode.OnlineShopping.Model.ProductItemModel;
import com.my.afarycode.OnlineShopping.Model.SearchModel;
import com.my.afarycode.OnlineShopping.listener.onItemClickListener;
import com.my.afarycode.R;
import com.my.afarycode.databinding.ItemSearchBinding;

import java.util.ArrayList;

public class AdapterSearch extends RecyclerView.Adapter<AdapterSearch.MyViewHolder> {
private onItemClickListener listener;
        Context context;
        ArrayList<ProductItemModel.Result> arrayList;

public AdapterSearch(Context context, ArrayList<ProductItemModel.Result> arrayList, onItemClickListener listener) {
        this.context = context;
        this.arrayList = arrayList;
        this.listener = listener;
        }

@NonNull
@Override
public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemSearchBinding binding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.item_search,parent,false);
        return new MyViewHolder(binding);
        }

@Override
public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        holder.binding.tvProductName.setText(arrayList.get(position).getProductName());

        }

@Override
public int getItemCount() {
        return arrayList.size();
        }

class MyViewHolder extends RecyclerView.ViewHolder {
    ItemSearchBinding binding;
    public MyViewHolder(@NonNull ItemSearchBinding itemView) {
        super(itemView.getRoot());
        binding = itemView;

        binding.llMain.setOnClickListener(v -> listener.onItem(getAdapterPosition()));
    }
}



}
