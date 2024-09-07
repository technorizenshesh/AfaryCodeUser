package com.my.afarycode.OnlineShopping.adapter;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
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

import com.my.afarycode.OnlineShopping.HomeShoppingOnlineScreen;
import com.my.afarycode.OnlineShopping.Model.CategoryModal;
import com.my.afarycode.OnlineShopping.ProductDetailAct;
import com.my.afarycode.OnlineShopping.constant.PreferenceConnector;
import com.my.afarycode.OnlineShopping.fragment.carservice.CarServiceFragment;
import com.my.afarycode.OnlineShopping.fragment.pharma.PharmicyFragment;
import com.my.afarycode.R;
import com.my.afarycode.databinding.CategoryListAdapterBinding;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.MyViewHolder> {

    private ArrayList<CategoryModal.Result> all_category_subcategory;
    private final Context activity;
    private HomeShoppingOnlineScreen fragment;
    private PharmicyFragment fragment1;
    private CarServiceFragment fragment2;

    public CategoryAdapter(Context a, ArrayList<CategoryModal.Result> all_category_subcategory) {
        this.activity = a;
        this.all_category_subcategory = all_category_subcategory;
    }

    @Override
    public CategoryAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        CategoryListAdapterBinding progressAdapterBinding = DataBindingUtil.inflate(LayoutInflater.from(activity)
                , R.layout.category_list_adapter, parent, false);
        return new MyViewHolder(progressAdapterBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, final int position) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            if( PreferenceConnector.readString(activity, PreferenceConnector.LANGUAGE, "").equalsIgnoreCase("en"))  {
                holder.progressAdapterBinding.categoryName.setText(all_category_subcategory.get(position).getCategoryName());
            }
            else if(PreferenceConnector.readString(activity, PreferenceConnector.LANGUAGE, "").equalsIgnoreCase("fr")){
                holder.progressAdapterBinding.categoryName.setText(Html.fromHtml(all_category_subcategory.get(position).getNameFr(), Html.FROM_HTML_MODE_LEGACY));
            }
            else {
                holder.progressAdapterBinding.categoryName.setText(all_category_subcategory.get(position).getCategoryName());
            }


        } else {
            if (PreferenceConnector.readString(activity, PreferenceConnector.LANGUAGE, "").equalsIgnoreCase("en")) {
                holder.progressAdapterBinding.categoryName.setText(all_category_subcategory.get(position).getCategoryName());

            } else if (PreferenceConnector.readString(activity, PreferenceConnector.LANGUAGE, "").equalsIgnoreCase("fr")) {
                holder.progressAdapterBinding.categoryName.setText(all_category_subcategory.get(position).getNameFr());

            } else {
                holder.progressAdapterBinding.categoryName.setText(all_category_subcategory.get(position).getCategoryName());

            }

        }






        Picasso.get().load(all_category_subcategory.get(position).getIcon1()).into(holder.progressAdapterBinding.icon);

        holder.progressAdapterBinding.LLShopOnline.setOnClickListener(v -> {

            Log.e("======",all_category_subcategory.size()+"");
            if (all_category_subcategory.get(position).categoryName.equals("online store")) {
                fragment = new HomeShoppingOnlineScreen();
                Bundle mBundle = new Bundle();
                mBundle.putString("cat_id", all_category_subcategory.get(position).id);
                PreferenceConnector.writeString(activity, PreferenceConnector.Cat_id, all_category_subcategory.get(position).id);
                loadFragment(fragment, mBundle);

            } else if (all_category_subcategory.get(position).categoryName.equals("restaurant")) {
              //  fragment = new HomeShoppingOnlineScreen();
             /*   Bundle mBundle = new Bundle();
                mBundle.putString("cat_id", all_category_subcategory.get(position).id);
                PreferenceConnector.writeString(activity, PreferenceConnector.Cat_id, all_category_subcategory.get(position).id);
                loadFragment(fragment, mBundle);*/

            } else if (all_category_subcategory.get(position).categoryName.equals("pharmacy")) {
              /*  fragment1 = new PharmicyFragment();
                Bundle mBundle = new Bundle();
                mBundle.putString("cat_id", all_category_subcategory.get(position).id);
                PreferenceConnector.writeString(activity, PreferenceConnector.Cat_id, all_category_subcategory.get(position).id);
                loadFragment(fragment1, mBundle);*/

            } else if (all_category_subcategory.get(position).categoryName.equals("car services")) {
              /*  fragment2 = new CarServiceFragment();
                Bundle mBundle = new Bundle();
                mBundle.putString("cat_id", all_category_subcategory.get(position).id);
                PreferenceConnector.writeString(activity, PreferenceConnector.Cat_id, all_category_subcategory.get(position).id);
                loadFragment(fragment2, mBundle);*/

            } else {

            }

        });
    }

    @Override
    public int getItemCount() {
        return all_category_subcategory.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        CategoryListAdapterBinding progressAdapterBinding;

        public MyViewHolder(CategoryListAdapterBinding itemView) {
            super(itemView.getRoot());
            progressAdapterBinding = itemView;
        }
    }

    public boolean loadFragment(Fragment fragment, Bundle mBundle) {
        FragmentManager fragmentManager = ((FragmentActivity) activity)
                .getSupportFragmentManager();
        fragment.setArguments(mBundle);

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

