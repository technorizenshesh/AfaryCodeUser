package com.my.afarycode.OnlineShopping.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;


import com.my.afarycode.OnlineShopping.HomeShoppingOnlineScreen;
import com.my.afarycode.OnlineShopping.Model.CategoryModal;
import com.my.afarycode.OnlineShopping.Model.GetShopingCategoryModal;
import com.my.afarycode.OnlineShopping.ProductListAct;
import com.my.afarycode.OnlineShopping.listener.onItemClickListener;
import com.my.afarycode.R;
import com.my.afarycode.databinding.GetShopingCategoryBinding;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class SubCategoryAdapter extends RecyclerView.Adapter<SubCategoryAdapter.MyViewHolder> {

    private ArrayList<GetShopingCategoryModal.Result> all_category_subcategory;
    private final Context activity;
    private HomeShoppingOnlineScreen fragment;
    onItemClickListener listener;
    public SubCategoryAdapter(Context a, ArrayList<GetShopingCategoryModal.Result> all_category_subcategory, onItemClickListener listener) {
        this.activity = a;
        this.all_category_subcategory = all_category_subcategory;
        this.listener = listener;
        // adapterCallback = ((CategoryAdapter.AdapterCallback1) activity);
    }

    @Override
    public SubCategoryAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        GetShopingCategoryBinding progressAdapterBinding = DataBindingUtil.inflate(LayoutInflater.from(activity)
                , R.layout.get_shoping_category, parent, false);

        return new MyViewHolder(progressAdapterBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, final int position) {

        holder.progressAdapterBinding.name.setText(all_category_subcategory.get(position).subCatName);
        Picasso.get().load(all_category_subcategory.get(position).icon).into(holder.progressAdapterBinding.imageIcon);

      /*  holder.progressAdapterBinding.LLShopOnline.setOnClickListener(v -> {

            Toast.makeText(activity, ""+all_category_subcategory.get(position).id, Toast.LENGTH_SHORT).show();

            if (all_category_subcategory.get(position).id.equals("1")) {
                fragment = new HomeShoppingOnlineScreen();
                loadFragment(fragment);
            }
        });*/

        holder.progressAdapterBinding.llMain.setOnClickListener(v -> {
          //  activity.startActivity(new Intent(activity, ProductListAct.class)
             //       .putExtra("title",all_category_subcategory.get(position).getSubCatName())
              //      .putExtra("byCatId",all_category_subcategory.get(position).id)
              //      .putExtra("by_screen","Shopping"));

            listener.onItem(position);
        });
    }

    @Override
    public int getItemCount() {
        return all_category_subcategory.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        GetShopingCategoryBinding progressAdapterBinding;

        public MyViewHolder(GetShopingCategoryBinding itemView) {
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

