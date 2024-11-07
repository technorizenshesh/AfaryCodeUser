package com.my.afarycode.OnlineShopping.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
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
import com.my.afarycode.OnlineShopping.HomeShoppingOnlineScreen;
import com.my.afarycode.OnlineShopping.Model.Add_Wish_To_Cart_Modal;
import com.my.afarycode.OnlineShopping.Model.DeleteCartModal;
import com.my.afarycode.OnlineShopping.Model.GetRestorentsModal;
import com.my.afarycode.OnlineShopping.Model.GetWishListModal;
import com.my.afarycode.OnlineShopping.WishListActivity;
import com.my.afarycode.OnlineShopping.activity.CardAct;
import com.my.afarycode.OnlineShopping.activity.CheckOutDeliveryAct;
import com.my.afarycode.OnlineShopping.constant.PreferenceConnector;
import com.my.afarycode.OnlineShopping.helper.DataManager;
import com.my.afarycode.R;
import com.my.afarycode.Splash;
import com.my.afarycode.databinding.ItemWishListBinding;
import com.my.afarycode.databinding.NearMyHomeBinding;
import com.my.afarycode.ratrofit.AfaryCode;
import com.my.afarycode.ratrofit.ApiClient;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MyWishListAdapter extends
        RecyclerView.Adapter<MyWishListAdapter.MyViewHolder> {

    private final AfaryCode apiInterface;
    private ArrayList<GetWishListModal.Result> all_category_subcategory;
    private final Context activity;
    private AllShopOnlineActivity fragment;
    private WishListActivity fragment1;

    public MyWishListAdapter(Context a, ArrayList<GetWishListModal.Result> all_category_subcategory) {
        this.activity = a;
        this.all_category_subcategory = all_category_subcategory;
        apiInterface = ApiClient.getClient(activity).create(AfaryCode.class);
    }

    @Override
    public MyWishListAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        ItemWishListBinding progressAdapterBinding = DataBindingUtil.inflate(LayoutInflater.from(activity)
                , R.layout.item_wish_list, parent, false);

        return new MyViewHolder(progressAdapterBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, final int position) {

        holder.progressAdapterBinding.productName.setText(all_category_subcategory.get(position).getProductDetails().getProductName());
        Picasso.get().load(all_category_subcategory.get(position).getProductDetails().getProductImages()).into(holder.progressAdapterBinding.imgeProduct);
        holder.progressAdapterBinding.txtPrice.setText(all_category_subcategory.get(position).getProductDetails().getLocalCurrency() + all_category_subcategory.get(position).getProductDetails().getLocalPrice());

        holder.progressAdapterBinding.addToCart.setOnClickListener(v -> {
            AddCartToWishListAPI(all_category_subcategory.get(position).getId());
        });
    }

    private void AddCartToWishListAPI(String wis_id) {

        DataManager.getInstance().showProgressMessage((Activity) activity,activity.getString(R.string.please_wait));
        Map<String,String> headerMap = new HashMap<>();
        headerMap.put("Authorization","Bearer " +PreferenceConnector.readString(activity, PreferenceConnector.access_token,""));
        headerMap.put("Accept","application/json");

        Map<String, String> map = new HashMap<>();
        map.put("user_id", PreferenceConnector.readString(activity, PreferenceConnector.User_id, ""));
        map.put("register_id", PreferenceConnector.readString(activity, PreferenceConnector.Register_id, ""));

        map.put("wish_id", wis_id);

        Log.e("MapMap", "EXERSICE LIST" + map);

        Call<ResponseBody> loginCall = apiInterface.wishlist_to_cart(headerMap,map);

        loginCall.enqueue(new Callback<ResponseBody>() {

            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                DataManager.getInstance().hideProgressMessage();

                try {
                    Log.e("response===", response.body().toString());
                    String stringResponse = response.body().string();
                    JSONObject jsonObject = new JSONObject(stringResponse);
                    if (jsonObject.getString("status").toString().equals("1")) {
                        notifyDataSetChanged();
                        fragment1 = new WishListActivity();
                        loadFragment(fragment1);
                        Toast.makeText(activity, "Add  cart item ", Toast.LENGTH_SHORT).show();

                    } else if (jsonObject.getString("status").equals("0")) {
                        Toast.makeText(activity, jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                    }

                    else if (jsonObject.getString("message").equals("5")) {
                        // Toast.makeText(getContext(), "No Data Found !!!!", Toast.LENGTH_SHORT).show();
                        PreferenceConnector.writeString(activity, PreferenceConnector.LoginStatus, "false");
                        activity.startActivity(new Intent(activity, Splash.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP));
                       ((Activity) activity).finish();

                    }


                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                call.cancel();
                DataManager.getInstance().hideProgressMessage();
            }
        });
    }

    @Override
    public int getItemCount() {
        return all_category_subcategory.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        ItemWishListBinding progressAdapterBinding;

        public MyViewHolder(ItemWishListBinding itemView) {
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
                    .replace(R.id.fragment_homeContainer, fragment)//, tag)
                    .commit();
            return true;
        }
        return false;
    }
}

