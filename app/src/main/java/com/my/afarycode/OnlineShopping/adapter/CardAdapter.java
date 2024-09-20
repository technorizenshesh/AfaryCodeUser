package com.my.afarycode.OnlineShopping.adapter;

import android.app.Activity;
import android.util.Log;
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

import com.google.gson.Gson;
import com.my.afarycode.OnlineShopping.AllShopOnlineActivity;
import com.my.afarycode.OnlineShopping.CardActivity;
import com.my.afarycode.OnlineShopping.Model.Add_to_Wish;
import com.my.afarycode.OnlineShopping.Model.CartModal;
import com.my.afarycode.OnlineShopping.Model.DeleteCartModal;
import com.my.afarycode.OnlineShopping.Model.UpdateCartModal;
import com.my.afarycode.OnlineShopping.constant.PreferenceConnector;
import com.my.afarycode.OnlineShopping.helper.DataManager;
import com.my.afarycode.R;
import com.my.afarycode.databinding.ItemCardBinding;
import com.my.afarycode.listener.OnPositionListener;
import com.my.afarycode.ratrofit.AfaryCode;
import com.my.afarycode.ratrofit.ApiClient;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.my.afarycode.OnlineShopping.AllShopOnlineActivity.item_quantity;
import static com.my.afarycode.OnlineShopping.CheckOutScreen.plateform_fees;
import static com.my.afarycode.OnlineShopping.CheckOutScreen.subTotal;
import static com.my.afarycode.OnlineShopping.CheckOutScreen.totalPriceToToPay1;
import static com.my.afarycode.OnlineShopping.CheckOutScreen.total_price_to_to_pay;

public class CardAdapter extends RecyclerView.Adapter<CardAdapter.MyViewHolder> {

    private ArrayList<CartModal.Result> all_category_subcategory;
    private final Activity activity;
    private AllShopOnlineActivity fragment;
    private CardActivity fragment1;
    OnPositionListener listener;


    public CardAdapter(Activity a, ArrayList<CartModal.Result> all_category_subcategory, OnPositionListener listener) {
        this.activity = a;
        this.all_category_subcategory = all_category_subcategory;
        this.listener = listener;
    }

    @Override
    public CardAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        ItemCardBinding progressAdapterBinding = DataBindingUtil.inflate(LayoutInflater.from(activity)
                , R.layout.item_card, parent, false);

        return new MyViewHolder(progressAdapterBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, final int position) {
          if(all_category_subcategory.get(position)!=null) {
              holder.progressAdapterBinding.productName.setText(all_category_subcategory.get(position).productName);
              Picasso.get().load(all_category_subcategory.get(position).productImage).into(holder.progressAdapterBinding.img1);
              holder.progressAdapterBinding.itemQuantity.setText(all_category_subcategory.get(position).quantity);
              //   holder.progressAdapterBinding.price.setText("Rs. " + all_category_subcategory.get(position).productPrice);

              holder.progressAdapterBinding.price.setText("Rs. " + Integer.valueOf(all_category_subcategory.get(position).getItemAmount())
                      * Integer.valueOf(all_category_subcategory.get(position).quantity));

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

        holder.progressAdapterBinding.wishList.setOnClickListener(v -> {

           // AddToWIshListAPI(all_category_subcategory.get(position).cartId);

            listener.onPos(position,"Wishlist","");

        });

        holder.progressAdapterBinding.minus.setOnClickListener(v -> {

            int count = Integer.parseInt(String.valueOf(holder.progressAdapterBinding.itemQuantity.getText()));
            if (count > 1) {
                count--;
                holder.progressAdapterBinding.itemQuantity.setText(String.valueOf(count));
                holder.progressAdapterBinding.price.setText("Rs. " +
                        Integer.valueOf(all_category_subcategory.get(position).getQuantity())
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
        ItemCardBinding progressAdapterBinding;

        public MyViewHolder(ItemCardBinding itemView) {
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

