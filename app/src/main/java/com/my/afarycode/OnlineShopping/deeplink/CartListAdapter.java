package com.my.afarycode.OnlineShopping.deeplink;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.my.afarycode.R;
import com.my.afarycode.databinding.ItemCartListBinding;
import java.util.ArrayList;




public class CartListAdapter extends RecyclerView.Adapter<CartListAdapter.MyViewHolder> {

    private ArrayList<CartListModel> arrayList;
    private final Context context;


    public CartListAdapter(Context context, ArrayList<CartListModel> arrayList ) {
        this.context = context;
        this.arrayList = arrayList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        ItemCartListBinding binding = DataBindingUtil.inflate(LayoutInflater.from(context)
                , R.layout.item_cart_list, parent, false);

        return new MyViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, final int position) {
            holder.binding.tvProductName.setText(arrayList.get(position).getName());
            Glide.with(context).load(arrayList.get(position).getImage()).into(holder.binding.img1);
            holder.binding.tvProductQty.setText(arrayList.get(position).getQty());
               holder.binding.tvProductPrice.setText(arrayList.get(position).getCurrency() + arrayList.get(position).getPrice());

            holder.binding.tvProductTotal.setText(arrayList.get(position).getCurrency() + Integer.valueOf(arrayList.get(position).getPrice())
                    * Integer.valueOf(arrayList.get(position).getQty()));

    }



    @Override
    public int getItemCount() {

        return arrayList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        ItemCartListBinding binding;

        public MyViewHolder(ItemCartListBinding itemView) {
            super(itemView.getRoot());
            binding = itemView;
        }
    }


}
