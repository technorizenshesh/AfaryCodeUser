package com.my.afarycode.OnlineShopping;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.databinding.DataBindingUtil;

import com.google.gson.Gson;
import com.my.afarycode.OnlineShopping.Model.HomeShopeProductModel;
import com.my.afarycode.OnlineShopping.Model.ProductItemModel;
import com.my.afarycode.OnlineShopping.activity.CheckOutDeliveryAct;
import com.my.afarycode.OnlineShopping.adapter.ProductAdapter;
import com.my.afarycode.OnlineShopping.adapter.SearchProductAdapter;
import com.my.afarycode.OnlineShopping.constant.PreferenceConnector;
import com.my.afarycode.OnlineShopping.helper.DataManager;
import com.my.afarycode.OnlineShopping.helper.NetworkAvailablity;
import com.my.afarycode.R;
import com.my.afarycode.Splash;
import com.my.afarycode.databinding.ActivityProductListBinding;
import com.my.afarycode.ratrofit.AfaryCode;
import com.my.afarycode.ratrofit.ApiClient;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProductListAct extends AppCompatActivity {
    public String TAG = "ProductListAct";
    ProductAdapter adapter;
    Context context = ProductListAct.this;
    ActivityProductListBinding binding;
    private ArrayList<ProductItemModel.Result> arrayList;

    String byCatId= "",byScreen="",title="";
    private AfaryCode apiInterface;
    String queryString="",filterType="All Country",countryId="";
    ArrayList<String>filterList =new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       binding =  DataBindingUtil.setContentView(this,R.layout.activity_product_list);
       apiInterface = ApiClient.getClient(context).create(AfaryCode.class);
        initViews();
    }

    private void initViews() {

       if(getIntent()!=null){
          // byScreen = getIntent().getStringExtra("by_screen");
          // byCatId = getIntent().getStringExtra("byCatId");
           title =  getIntent().getStringExtra("title");
           countryId = getIntent().getStringExtra("countryId");
           queryString = title;
           if(!PreferenceConnector.readString(ProductListAct.this, PreferenceConnector.filterType,"").equalsIgnoreCase(""))
            binding.tvFilter.setText(PreferenceConnector.readString(ProductListAct.this, PreferenceConnector.filterType,""));

           //binding.tvTitle.setText(title);
       }
        filterList.clear();
        filterList.add("All Country");
        filterList.add("Domestic");
        filterList.add("International");


        arrayList = new ArrayList<>();
       
        adapter = new ProductAdapter(context,arrayList);
        binding.recyclerResult.setAdapter(adapter);

        binding.search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                queryString = s;
               if(NetworkAvailablity.checkNetworkStatus(ProductListAct.this)) getProductBySearchId(queryString,filterType,countryId);
               else Toast.makeText(ProductListAct.this, getString(R.string.network_failure), Toast.LENGTH_SHORT).show();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                queryString = s;
                if(NetworkAvailablity.checkNetworkStatus(ProductListAct.this)) getProductBySearchId(queryString,filterType,countryId);
                else Toast.makeText(ProductListAct.this, getString(R.string.network_failure), Toast.LENGTH_SHORT).show();
                return false;
            }
        });


        binding.swiperRefresh.setOnRefreshListener(this::getProductItems);
        binding.search.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                finish();
                return false;
            }
        });


        binding.tvFilter.setOnClickListener(view -> showDropDownFilterList(view,binding.tvFilter,filterList));


     //  if(byScreen.equalsIgnoreCase("Shopping")) getProductById(byCatId);
     //  else getProductBySearchId(byCatId);

        if(NetworkAvailablity.checkNetworkStatus(ProductListAct.this)) getProductBySearchId(queryString,filterType,countryId);
        else Toast.makeText(ProductListAct.this, getString(R.string.network_failure), Toast.LENGTH_SHORT).show();
    }

    private void getProductItems() {
        if(NetworkAvailablity.checkNetworkStatus(ProductListAct.this)) getProductBySearchId(queryString,filterType,countryId);
        else Toast.makeText(ProductListAct.this, getString(R.string.network_failure), Toast.LENGTH_SHORT).show();
    }



/*
    private void getProductBySearchId(String id) {
        DataManager.getInstance().showProgressMessage((Activity) context, getString(R.string.please_wait));
        Map<String,String> headerMap = new HashMap<>();
        headerMap.put("Authorization","Bearer " +PreferenceConnector.readString(context, PreferenceConnector.access_token,""));
        headerMap.put("Accept","application/json");

        HashMap<String,String> param = new HashMap<>();
        param.put("sub_id",id);
        Call<ResponseBody> call = apiInterface.getProductBySearchIdItem(headerMap,param);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                DataManager.getInstance().hideProgressMessage();
                try {
                    String responseString = response.body().string();
                    JSONObject jsonObject = new JSONObject(responseString);
                    Log.e(TAG,"getProduct Search Item Response = " + responseString);
                    if(jsonObject.getString("status").equals("1")) {
                        ProductItemModel model = new Gson().fromJson(responseString, ProductItemModel.class);
                        arrayList.clear();
                        arrayList.addAll(model.getResult());
                        adapter.notifyDataSetChanged();
                        binding.tvNotFound.setVisibility(View.GONE);

                    } else {
                        arrayList.clear();
                        adapter.notifyDataSetChanged();
                        binding.tvNotFound.setVisibility(View.VISIBLE);

                    }

                } catch (Exception e) {
                    // Toast.makeText(mContext, "Exception = " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.e("Exception","Exception = " + e.getMessage());
                }

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                DataManager.getInstance().hideProgressMessage();
            }
        });
    }
*/

/*
    private void getProductById(String id) {
        DataManager.getInstance().showProgressMessage((Activity) context, getString(R.string.please_wait));
        Map<String,String> headerMap = new HashMap<>();
        headerMap.put("Authorization","Bearer " + PreferenceConnector.readString(context, PreferenceConnector.access_token,""));
        headerMap.put("Accept","application/json");

        HashMap<String,String> param = new HashMap<>();
        param.put("sub_id",id);
        Call<ResponseBody> call = apiInterface.getProductItem(headerMap,param);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                 DataManager.getInstance().hideProgressMessage();
                try {
                    String responseString = response.body().string();
                    JSONObject jsonObject = new JSONObject(responseString);
                    Log.e(TAG,"getProduct Item Response = " + responseString);
                    if(jsonObject.getString("status").equals("1")) {
                        ProductItemModel model = new Gson().fromJson(responseString, ProductItemModel.class);
                        arrayList.clear();
                        arrayList.addAll(model.getResult());
                        adapter.notifyDataSetChanged();
                        binding.tvNotFound.setVisibility(View.GONE);

                    } else {
                        arrayList.clear();
                        adapter.notifyDataSetChanged();
                        binding.tvNotFound.setVisibility(View.VISIBLE);

                    }

                } catch (Exception e) {
                    // Toast.makeText(mContext, "Exception = " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.e("Exception","Exception = " + e.getMessage());
                }

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                DataManager.getInstance().hideProgressMessage();
            }
        });
    }
*/


    private void getProductBySearchId(String searchText,String filterType,String countryId) {
      //  DataManager.getInstance().showProgressMessage((Activity) context, getString(R.string.please_wait));
        Map<String,String> headerMap = new HashMap<>();
        headerMap.put("Authorization","Bearer " + PreferenceConnector.readString(context, PreferenceConnector.access_token,""));
        headerMap.put("Accept","application/json");

        HashMap<String,String> param = new HashMap<>();
        param.put("title",searchText);
        param.put("user_id",PreferenceConnector.readString(ProductListAct.this, PreferenceConnector.User_id, ""));
        param.put("product_type",filterType);
        param.put("country_id",countryId);
        param.put("register_id", PreferenceConnector.readString(ProductListAct.this, PreferenceConnector.Register_id, ""));



        Call<ResponseBody> call = apiInterface.getProductSearch(headerMap,param);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                DataManager.getInstance().hideProgressMessage();
                try {
                    String responseString = response.body().string();
                    JSONObject jsonObject = new JSONObject(responseString);
                    Log.e(TAG,"getProduct Item Response = " + responseString);
                    if(jsonObject.getString("status").equals("1")) {
                        ProductItemModel model = new Gson().fromJson(responseString, ProductItemModel.class);
                        binding.tvResult.setVisibility(View.VISIBLE);
                        arrayList.clear();
                        arrayList.addAll(model.getResult());
                        adapter.notifyDataSetChanged();
                        binding.tvNotFound.setVisibility(View.GONE);
                        binding.tvResult.setText(arrayList.size()+ " Results");
                    }

                    else if (jsonObject.getString("status").equals("5")) {
                        PreferenceConnector.writeString(ProductListAct.this, PreferenceConnector.LoginStatus, "false");
                        startActivity(new Intent(ProductListAct.this, Splash.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP));
                        finish();

                    }


                    else {
                        arrayList.clear();
                        adapter.notifyDataSetChanged();
                        binding.tvNotFound.setVisibility(View.VISIBLE);
                        binding.tvResult.setVisibility(View.GONE);

                    }

                } catch (Exception e) {
                    // Toast.makeText(mContext, "Exception = " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.e("Exception","Exception = " + e.getMessage());
                }

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                DataManager.getInstance().hideProgressMessage();
            }
        });
    }


    @RequiresApi(api = Build.VERSION_CODES.HONEYCOMB)
    private void showDropDownFilterList(View v, TextView textView, List<String> stringList) {
        PopupMenu popupMenu = new PopupMenu(ProductListAct.this, v);
        for (int i = 0; i < stringList.size(); i++) {
            popupMenu.getMenu().add(stringList.get(i));
        }

        //popupMenu.getMenu().add(0,stringList.size()+1,0,R.string.add_new_category ).setIcon(R.drawable.ic_added);
        popupMenu.setOnMenuItemClickListener(menuItem -> {

            for (int i = 0; i < stringList.size(); i++) {
                if (stringList.get(i).equalsIgnoreCase(menuItem.getTitle().toString())) {
                    filterType = stringList.get(i);
                    textView.setText(menuItem.getTitle());
                    //  listener.onExpense(filterText);


                }
            }

            if(NetworkAvailablity.checkNetworkStatus(ProductListAct.this)) getProductBySearchId(queryString,filterType,countryId);
            else Toast.makeText(ProductListAct.this, getString(R.string.network_failure), Toast.LENGTH_SHORT).show();



            return true;
        });
        popupMenu.show();
    }








}
