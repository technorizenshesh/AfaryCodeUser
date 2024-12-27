package com.my.afarycode.OnlineShopping.chat;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.google.gson.Gson;
import com.my.afarycode.OnlineShopping.Model.GetProfileModal;
import com.my.afarycode.OnlineShopping.constant.PreferenceConnector;
import com.my.afarycode.OnlineShopping.helper.DataManager;
import com.my.afarycode.OnlineShopping.helper.NetworkAvailablity;
import com.my.afarycode.OnlineShopping.myorder.OrderModel;
import com.my.afarycode.OnlineShopping.orderdetails.OrderDetailsAct;
import com.my.afarycode.R;
import com.my.afarycode.Splash;
import com.my.afarycode.databinding.ActivityChatListBinding;
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

public class ChatListAct extends AppCompatActivity implements ChatOnListener{
    public String TAG ="ChatListAct";
    ActivityChatListBinding binding;
    AfaryCode apiInterface;
    ChatListAdapter adapter;
    ArrayList<ChatListModel.Result>arrayList;
    String userName="",userId="",userImg="";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_chat_list);
        apiInterface = ApiClient.getClient(this).create(AfaryCode.class);
        initViews();

    }

    private void initViews() {

        arrayList = new ArrayList<>();

        adapter = new ChatListAdapter(ChatListAct.this,arrayList,ChatListAct.this);
        binding.rvChatList.setAdapter(adapter);

        binding.backNavigation.setOnClickListener(view -> finish());

        if(NetworkAvailablity.checkNetworkStatus(ChatListAct.this))  GetProfileAPI();
        else Toast.makeText(ChatListAct.this, getString(R.string.network_failure), Toast.LENGTH_SHORT).show();

        if(NetworkAvailablity.checkNetworkStatus(ChatListAct.this)) getAllSeller();
        else Toast.makeText(ChatListAct.this, getString(R.string.network_failure), Toast.LENGTH_SHORT).show();




    }


    private void getAllSeller( ) {
        DataManager.getInstance().showProgressMessage(ChatListAct.this,"Please wait...");

        Map<String, String> map = new HashMap<>();
        Map<String,String> headerMap = new HashMap<>();
        headerMap.put("Authorization","Bearer " + PreferenceConnector.readString(ChatListAct.this, PreferenceConnector.access_token,""));
        headerMap.put("Accept","application/json");


        map.put("user_id", PreferenceConnector.readString(ChatListAct.this, PreferenceConnector.User_id, ""));
        map.put("country_id", PreferenceConnector.readString(ChatListAct.this, PreferenceConnector.countryId, ""));
        map.put("register_id", PreferenceConnector.readString(ChatListAct.this, PreferenceConnector.Register_id, ""));

        Log.e(TAG, "Chat msg Request :" + map);
        Call<ResponseBody> loginCall = apiInterface.getSellerChatListApi(headerMap,map);
        loginCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                DataManager.getInstance().hideProgressMessage();
                try {
                    String responseData = response.body() != null ? response.body().string() : "";
                    JSONObject object = new JSONObject(responseData);
                    Log.e(TAG, "Chat msg Response :" + object);
                    if (object.optString("status").equals("1")) {
                        ChatListModel model = new Gson().fromJson(responseData,ChatListModel.class);
                        arrayList.clear();
                        arrayList.addAll(model.getResult());
                        adapter.notifyDataSetChanged();

                    } else if (object.optString("status").equals("0")) {
                        arrayList.clear();
                        adapter.notifyDataSetChanged();

                        // Toast.makeText(OrderDetailsAct.this, data.message /*getString(R.string.wrong_username_password)*/, Toast.LENGTH_SHORT).show();
                    }

                    else if (object.getString("status").equals("5")) {
                        PreferenceConnector.writeString(ChatListAct.this, PreferenceConnector.LoginStatus, "false");
                        startActivity(new Intent(ChatListAct.this, Splash.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP));
                        finish();
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
    public void onChat(int Position, ChatListModel.Result result) {
            startActivity(new Intent(this, ChatAct.class)
                    .putExtra("UserId", result.getId())
                    .putExtra("UserName",result.getUserName())
                    .putExtra("UserImage",result.getImage())
                    .putExtra("id",PreferenceConnector.readString(ChatListAct.this,PreferenceConnector.User_id,""))
                    .putExtra("name",userName)
                    .putExtra("img",userImg));

    }


    private void GetProfileAPI() {
        Map<String, String> map = new HashMap<>();
        map.put("user_id", PreferenceConnector.readString(ChatListAct.this, PreferenceConnector.User_id, ""));
        map.put("register_id", PreferenceConnector.readString(ChatListAct.this, PreferenceConnector.Register_id, ""));
        map.put("country_id",PreferenceConnector.readString(ChatListAct.this, PreferenceConnector.countryId, ""));

        Call<GetProfileModal> loginCall = apiInterface.get_profile(map);

        loginCall.enqueue(new Callback<GetProfileModal>() {
            @Override
            public void onResponse(Call<GetProfileModal> call,
                                   Response<GetProfileModal> response) {

                DataManager.getInstance().hideProgressMessage();

                try {

                    GetProfileModal data = response.body();
                    String dataResponse = new Gson().toJson(response.body());

                    Log.e("MapMap", "GET RESPONSE" + dataResponse);

                    if (data.status.equals("1")) {

                        userName = data.getResult().userName;
                        userImg = data.getResult().image;
                        userId = data.getResult().id;



                    } else if (data.status.equals("0")) {
                        // Toast.makeText(OrderDetailsAct.this, data.message /*getString(R.string.wrong_username_password)*/, Toast.LENGTH_SHORT).show();
                    }

                    else if (data.status.equals("5")) {
                        PreferenceConnector.writeString(ChatListAct.this, PreferenceConnector.LoginStatus, "false");
                        startActivity(new Intent(ChatListAct.this, Splash.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP));
                        finish();
                    }


                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<GetProfileModal> call, Throwable t) {
                call.cancel();
                DataManager.getInstance().hideProgressMessage();
            }
        });
    }

}
