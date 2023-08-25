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
import com.my.afarycode.databinding.ItemLocationBinding;

import java.util.ArrayList;

public class LocationAdapter extends RecyclerView.Adapter<LocationAdapter.MyViewHolder> {
    Context context;
    ArrayList<LocationModel.Result> arrayList;
    onPosListener listener;
    private int lastSelectedPosition = -1;

    public LocationAdapter(Context context, ArrayList<LocationModel.Result> arrayList, onPosListener listener) {
        this.context = context;
        this.arrayList = arrayList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemLocationBinding binding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.item_location, parent, false);
        return new MyViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.binding.tvAddress.setText(arrayList.get(position).getAddress());
        holder.binding.rdHome.setText(arrayList.get(position).getAddressName());
        if(arrayList.get(position).isChk()) holder.binding.rdHome.setChecked(arrayList.get(position).isChk());
        else holder.binding.rdHome.setChecked(arrayList.get(position).isChk());

    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        ItemLocationBinding binding;

        public MyViewHolder(@NonNull ItemLocationBinding itemView) {
            super(itemView.getRoot());
            binding = itemView;

           /* binding.layoutMain.setOnClickListener(v ->{
                for (int i=0;i<arrayList.size();i++){
                    arrayList.get(i).setChk(false);
                }
                arrayList.get(getAdapterPosition()).setChk(true);
                listener.onPos(getAdapterPosition());
                notifyDataSetChanged();

            } );*/

            binding.layoutMain.setOnClickListener(v -> {
                for (int i=0;i<arrayList.size();i++){
                    arrayList.get(i).setChk(false);
                }
                arrayList.get(getAdapterPosition()).setChk(true);
                listener.onPos(getAdapterPosition(),"Save");
                lastSelectedPosition = getAdapterPosition();
                notifyDataSetChanged();
            });




            binding.ivDelete.setOnClickListener(v -> listener.onPos(getAdapterPosition(),"Delete"));

            binding.ivEdit.setOnClickListener(v -> listener.onPos(getAdapterPosition(),"Edit"));

        }
    }

    public void changesOnSelection(boolean chk){
        for (int i=0;i<arrayList.size();i++){
            arrayList.get(i).setChk(false);
        }
        notifyDataSetChanged();
    }

}
