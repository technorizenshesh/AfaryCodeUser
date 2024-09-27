package com.my.afarycode.OnlineShopping.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.my.afarycode.OnlineShopping.AllShopOnlineActivity;
import com.my.afarycode.OnlineShopping.CardActivity;
import com.my.afarycode.OnlineShopping.Model.CartModal;
import com.my.afarycode.R;
import com.my.afarycode.databinding.ItemCardBinding;
import com.my.afarycode.databinding.ItemCheckoutBinding;
import com.my.afarycode.listener.OnPositionListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class CheckoutAdapter extends RecyclerView.Adapter<CheckoutAdapter.MyViewHolder> {

    private ArrayList<CartModal.Result> all_category_subcategory;
    private final Context activity;
    private AllShopOnlineActivity fragment;
    private CardActivity fragment1;
    OnPositionListener listener;


    public CheckoutAdapter(Activity a, ArrayList<CartModal.Result> all_category_subcategory, OnPositionListener listener) {
        this.activity = a;
        this.all_category_subcategory = all_category_subcategory;
        this.listener = listener;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        ItemCheckoutBinding progressAdapterBinding = DataBindingUtil.inflate(LayoutInflater.from(activity)
                , R.layout.item_checkout, parent, false);

        return new MyViewHolder(progressAdapterBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, final int position) {
        if(all_category_subcategory.get(position)!=null) {
            holder.progressAdapterBinding.productName.setText(all_category_subcategory.get(position).productName);
          //  Picasso.get().load(all_category_subcategory.get(position).productImage).into(holder.progressAdapterBinding.img1);
            Glide.with(activity).load(all_category_subcategory.get(position).productImage).into(holder.progressAdapterBinding.img1);

            holder.progressAdapterBinding.itemQuantity.setText(all_category_subcategory.get(position).quantity);
            //   holder.progressAdapterBinding.price.setText("Rs. " + all_category_subcategory.get(position).productPrice);

            holder.progressAdapterBinding.price.setText("Rs. " + Integer.valueOf(all_category_subcategory.get(position).getItemAmount())
                    * Integer.valueOf(all_category_subcategory.get(position).quantity));


            if(all_category_subcategory.get(position).getWishList().equalsIgnoreCase("yes")){
                holder.progressAdapterBinding.wishAdd.setImageResource(R.drawable.ic_star_red);
            } else holder.progressAdapterBinding.wishAdd.setImageResource(R.drawable.star_icon);


            if(all_category_subcategory.get(position).getDeliveryCharges().equalsIgnoreCase("1"))
                holder.progressAdapterBinding.ivDeliveryType.setVisibility(View.VISIBLE);
            else holder.progressAdapterBinding.ivDeliveryType.setVisibility(View.GONE);


        }





        holder.progressAdapterBinding.plus.setOnClickListener(v -> {
            int count = Integer.parseInt(String.valueOf(holder.progressAdapterBinding.itemQuantity.getText()));
            count++;
            holder.progressAdapterBinding.itemQuantity.setText(String.valueOf(count));
            holder.progressAdapterBinding.price.setText("Rs. " + Integer.valueOf(all_category_subcategory.get(position).getItemAmount())
                    * Integer.valueOf(count));

            //  UpdateQuanityAPI(all_category_subcategory.get(position).cartId
            //          , all_category_subcategory.get(position).proId, count);

            listener.onPos(position,"Update",count+"");

        });

        holder.progressAdapterBinding.wishAdd.setOnClickListener(v -> {

            // AddToWIshListAPI(all_category_subcategory.get(position).cartId);

           if(all_category_subcategory.get(position).wishList.equalsIgnoreCase("yes")) listener.onPos(position,"Wishlist","no");
              else listener.onPos(position,"Wishlist","yes");
        });

        holder.progressAdapterBinding.minus.setOnClickListener(v -> {

            int count = Integer.parseInt(String.valueOf(holder.progressAdapterBinding.itemQuantity.getText()));
            if (count > 1) {
                count--;
                holder.progressAdapterBinding.itemQuantity.setText(String.valueOf(count));
                holder.progressAdapterBinding.price.setText("Rs. " +
                        Integer.valueOf(all_category_subcategory.get(position).getItemAmount())
                                * Integer.valueOf(count));

                //UpdateQuanityAPI(all_category_subcategory.get(position).cartId,
                //          all_category_subcategory.get(position).proId, count);

                listener.onPos(position,"Update",count+"");


            } else {
                Toast.makeText(activity, "Please Select Atleast one Item ", Toast.LENGTH_SHORT).show();
            }
        });

        holder.progressAdapterBinding.deleteItem.setOnClickListener(v -> {
            //DeleteAPI(all_category_subcategory.get(position).cartId);
            listener.onPos(position,"Delete","");

            //   notifyDataSetChanged();
        });
    }



    @Override
    public int getItemCount() {

        return all_category_subcategory.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        ItemCheckoutBinding progressAdapterBinding;

        public MyViewHolder(ItemCheckoutBinding itemView) {
            super(itemView.getRoot());
            progressAdapterBinding = itemView;
        }
    }


    public boolean loadFragment(Fragment fragment) {
        FragmentManager fragmentManager = ((FragmentActivity) activity)
                .getSupportFragmentManager();

        if (fragment != null) {
            fragmentManager.beginTransaction()
                    .addToBackStack("Home")
                    .replace(R.id.fragment_homeContainer, fragment)
                    .commit();
            return true;
        }
        return false;
    }
}