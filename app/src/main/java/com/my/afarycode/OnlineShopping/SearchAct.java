/*
package com.my.afarycode.OnlineShopping;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.databinding.DataBindingUtil;

import com.google.gson.Gson;
import com.my.afarycode.OnlineShopping.Model.ProductItemModel;
import com.my.afarycode.OnlineShopping.activity.CheckOutDeliveryAct;
import com.my.afarycode.OnlineShopping.adapter.AdapterSearch;
import com.my.afarycode.OnlineShopping.constant.PreferenceConnector;
import com.my.afarycode.OnlineShopping.helper.DataManager;
import com.my.afarycode.OnlineShopping.listener.onItemClickListener;
import com.my.afarycode.R;
import com.my.afarycode.Splash;
import com.my.afarycode.databinding.ActivitySearchBinding;
import com.my.afarycode.ratrofit.AfaryCode;
import com.my.afarycode.ratrofit.ApiClient;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SearchAct extends AppCompatActivity implements onItemClickListener {
    public String TAG = "SearchAct";
    AfaryCode apiInterface;
    Context context = SearchAct.this;
    private ArrayList<ProductItemModel.Result> arrayList = new ArrayList<>();
    private AdapterSearch adapter;
    private ActivitySearchBinding binding;
    private String queryString = "",subCatId="",catId="";
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_search);
        apiInterface = ApiClient.getClient(context).create(AfaryCode.class);
        BindView();
    }

    private void BindView() {
        //   getSupportActionBar().setDisplayHomeAsUpEnabled(true);


     */
/*   binding.search.setIconified(false);
        binding.search.setQueryHint(getString(R.string.search_here));
        adapter = new AdapterSearch(this, arrayList, this);
        binding.recyList.setAdapter(adapter);
        binding.swiperRefresh.setOnRefreshListener(this::getProduct);
        binding.search.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                finish();
                return false;
            }
        });
        binding.search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                queryString = s;
                getProduct();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                queryString = s;
                getProduct();
                return false;
            }
        });*//*






    }

    private void getProduct() {
        binding.swiperRefresh.setRefreshing(true);
        Map<String,String> headerMap = new HashMap<>();
        headerMap.put("Authorization","Bearer " + PreferenceConnector.readString(SearchAct.this, PreferenceConnector.access_token,""));
        headerMap.put("Accept","application/json");

        HashMap<String,String> param = new HashMap<>();
        param.put("title",queryString);
        param.put("user_id", PreferenceConnector.readString(SearchAct.this, PreferenceConnector.User_id, ""));

        param.put("register_id", PreferenceConnector.readString(SearchAct.this, PreferenceConnector.Register_id, ""));

        Call<ResponseBody> call = apiInterface.searchProduct(headerMap,param);
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    binding.swiperRefresh.setRefreshing(false);

                    try {
                        String responseString = response.body().string();
                        JSONObject jsonObject = new JSONObject(responseString);
                        Log.e(TAG,"getProduct Search Response = " + responseString);
                        if(jsonObject.getString("status").equals("1")) {
                            ProductItemModel model = new Gson().fromJson(responseString, ProductItemModel.class);
                            arrayList.clear();
                            arrayList.addAll(model.getResult());
                            adapter.notifyDataSetChanged();
                            binding.tvNotFound.setVisibility(View.GONE);

                        }

                        else if (jsonObject.getString("status").equals("5")) {
                            PreferenceConnector.writeString(SearchAct.this, PreferenceConnector.LoginStatus, "false");
                            startActivity(new Intent(SearchAct.this, Splash.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP));
                            finish();

                        }



                        else {
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
                    binding.swiperRefresh.setRefreshing(false);
                }
            });
        }




    @Override
    public void onItem(int position) {

        if(arrayList.get(position).getCountryId().equalsIgnoreCase(PreferenceConnector.readString(SearchAct.this, PreferenceConnector.countryId,"")))
            PreferenceConnector.writeString(SearchAct.this,PreferenceConnector.filterType,"Domestic");
      else PreferenceConnector.writeString(SearchAct.this,PreferenceConnector.filterType,"International");

      startActivity(new Intent(context, ProductListAct.class)
                .putExtra("title",arrayList.get(position).getProductName())
                .putExtra("countryId",arrayList.get(position).getCountryId())
                .putExtra("by_screen","Search"));
    }
}
*/
