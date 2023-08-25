package com.my.afarycode.OnlineShopping.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.my.afarycode.OnlineShopping.Model.CountryModel;
import com.my.afarycode.OnlineShopping.bottomsheet.CountryBottomSheet;
import com.my.afarycode.OnlineShopping.listener.onPositionClickListener;
import com.my.afarycode.R;
import com.my.afarycode.databinding.ItemCountryBinding;

import java.util.ArrayList;

public class CountryAdapter extends RecyclerView.Adapter<CountryAdapter.MyViewHolder> {
    Context context;
    ArrayList<CountryModel.Result>arrayList;
    onPositionClickListener listener;

    public CountryAdapter(Context context, ArrayList<CountryModel.Result> arrayList, onPositionClickListener listener) {
        this.context = context;
        this.arrayList = arrayList;
        this.listener = listener;
    }



    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemCountryBinding binding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.item_country, parent, false);
        return new MyViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
       holder.binding.tvName.setText(arrayList.get(position).getName());
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        ItemCountryBinding binding;
        public MyViewHolder(@NonNull ItemCountryBinding itemView) {
            super(itemView.getRoot());
            binding = itemView;

            binding.tvName.setOnClickListener(v -> {
                listener.onPosition(getAdapterPosition(),arrayList.get(getAdapterPosition()).getName(),arrayList.get(getAdapterPosition()).getId());
            });
        }
    }

    public void filterList(ArrayList<CountryModel.Result> filterlist) {
        if(filterlist.size()==0)
            CountryBottomSheet.tvNotFound.setVisibility(View.VISIBLE);
        else
            CountryBottomSheet.tvNotFound.setVisibility(View.GONE);
        arrayList = filterlist;
        notifyDataSetChanged();
    }

}
