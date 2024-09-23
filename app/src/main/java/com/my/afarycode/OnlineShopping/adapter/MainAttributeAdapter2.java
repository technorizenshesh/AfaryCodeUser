package com.my.afarycode.OnlineShopping.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.my.afarycode.OnlineShopping.Model.ShoppingProductModal;
import com.my.afarycode.OnlineShopping.ProductListAct;
import com.my.afarycode.OnlineShopping.listener.InnerClickListener;
import com.my.afarycode.OnlineShopping.listener.MainClickListener;
import com.my.afarycode.R;
import com.my.afarycode.databinding.ItemMainAttributeBinding;
import com.my.afarycode.databinding.ItemMainBinding;

import java.util.ArrayList;
import java.util.List;


public class MainAttributeAdapter2 extends RecyclerView.Adapter<MainAttributeAdapter2.MyViewHolder> implements InnerClickListener {

    Context context;
    ArrayList<ShoppingProductModal.Result.ValidateName> arrayList;
    MainClickListener listener;

    public MainAttributeAdapter2(Context context, ArrayList<ShoppingProductModal.Result.ValidateName> arrayList,MainClickListener listener) {
        this.context = context;
        this.arrayList = arrayList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemMainAttributeBinding binding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.item_main_attribute,parent,false);
        return new MyViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        holder.binding.tvAttributeName.setText(arrayList.get(position).getName());

        holder.binding.tvAttribute.setOnClickListener(v->{
            showDropDownAttribute(v,holder.binding.tvAttribute,arrayList.get(position).getName(),position,arrayList.get(position).getAttributeName());
        });

     //   holder.binding.rvInner.setAdapter(new InnerAttributeAdapter(context, (ArrayList<ShoppingProductModal.Result.ValidateName.AttributeName>) arrayList.get(position).getAttributeName(),position,MainAttributeAdapter2.this));
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
        ItemMainAttributeBinding binding;
        public MyViewHolder(@NonNull ItemMainAttributeBinding itemView) {
            super(itemView.getRoot());
            binding = itemView;
        }

    }

    private void showDropDownAttribute(View v, TextView textView,String mainName,int mainPosition, List<ShoppingProductModal.Result.ValidateName.AttributeName> stringList) {
        PopupMenu popupMenu = new PopupMenu(context, v);
        for (int i = 0; i < stringList.size(); i++) {
            popupMenu.getMenu().add(stringList.get(i).getName());
        }
        popupMenu.setOnMenuItemClickListener(menuItem -> {

            for (int i = 0; i < stringList.size(); i++) {
                if (stringList.get(i).getName().equalsIgnoreCase(menuItem.getTitle().toString())) {
                   // filterType = stringList.get(i).getId();
                    textView.setText(menuItem.getTitle());
                    //  listener.onExpense(filterText);
                    listener.mainClick(mainName,stringList.get(i).getName(),mainPosition,i);


                }
            }

            return true;
        });
        popupMenu.show();
    }

}

