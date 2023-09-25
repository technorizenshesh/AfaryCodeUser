package com.my.afarycode.OnlineShopping.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.my.afarycode.OnlineShopping.Model.DeliveryTypeModel;
import com.my.afarycode.OnlineShopping.Model.LocationModel;
import com.my.afarycode.OnlineShopping.listener.onPosListener;
import com.my.afarycode.R;
import com.my.afarycode.databinding.ItemLocationBinding;
import com.my.afarycode.databinding.ItemTypeBinding;

import java.util.ArrayList;

public class DeliveryTypeAdapter extends RecyclerView.Adapter<DeliveryTypeAdapter.MyViewHolder> {
    Context context;
    ArrayList<DeliveryTypeModel.Result> arrayList;
    onPosListener listener;
    private int lastSelectedPosition = -1;

    public DeliveryTypeAdapter(Context context, ArrayList<DeliveryTypeModel.Result> arrayList, onPosListener listener) {
        this.context = context;
        this.arrayList = arrayList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemTypeBinding binding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.item_type, parent, false);
        return new MyViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.binding.rdMyHome.setText(arrayList.get(position).getName());
        if(arrayList.get(position).isChk()== true) holder.binding.rdMyHome.setChecked(true);
        else holder.binding.rdMyHome.setChecked(false);




        holder.binding.rdMyHome.setOnClickListener(v -> {
          /* for (int i=0;i<arrayList.size();i++){
                arrayList.get(i).setChk(false);
            }
            arrayList.get(position).setChk(true);*/
              listener.onPos(position,arrayList.get(position).getName());
            //     lastSelectedPosition = getAdapterPosition();
           // notifyDataSetChanged();
        });

    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        ItemTypeBinding binding;

        public MyViewHolder(@NonNull ItemTypeBinding itemView) {
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



        }
    }



}