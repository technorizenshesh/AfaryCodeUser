package com.my.afarycode.OnlineShopping.chat;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;


import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.gson.Gson;
import com.my.afarycode.OnlineShopping.Model.GetProfileModal;
import com.my.afarycode.OnlineShopping.activity.CardAct;
import com.my.afarycode.OnlineShopping.activity.CheckOutDeliveryAct;
import com.my.afarycode.OnlineShopping.constant.PreferenceConnector;
import com.my.afarycode.OnlineShopping.helper.DataManager;
import com.my.afarycode.OnlineShopping.helper.NetworkAvailablity;
import com.my.afarycode.OnlineShopping.orderdetails.OrderDetailsAct;
import com.my.afarycode.R;
import com.my.afarycode.Splash;
import com.my.afarycode.databinding.ActivityChatBinding;
import com.my.afarycode.ratrofit.AfaryCode;
import com.my.afarycode.ratrofit.ApiClient;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChatAct extends AppCompatActivity {
    public String TAG = "ChatAct";
    ActivityChatBinding binding;
    DatabaseReference reference1;
    String request_id="",userId="", user_name, user_image, receiverId = "", receiverName = "", receiverImage = "";
    private AfaryCode apiInterface;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_chat);
        apiInterface = ApiClient.getClient(this).create(AfaryCode.class);



        initializeViews();
        if (getIntent() != null) {
            user_name = getIntent().getStringExtra("name");
            user_image = getIntent().getStringExtra("img") ;
            userId = getIntent().getStringExtra("id") ;
            setUserInfo(getIntent().getStringExtra("UserId"));
            receiverId = getIntent().getStringExtra("UserId");
            receiverName = getIntent().getStringExtra("UserName");
            receiverImage = getIntent().getStringExtra("UserImage");
           // request_id = getIntent().getStringExtra("request_id");


        }

    }

    private void initializeViews() {
        binding.backNavigation.setOnClickListener(v -> finish());
        binding.ChatLayout.imgSendIcon.setOnClickListener(v -> {
            if (binding.ChatLayout.tvMessage.getText().toString().length() > 0) {
                String messageText = binding.ChatLayout.tvMessage.getText().toString().trim();
                if (!messageText.equals("")) {
                    Map<String, String> map = new HashMap<String, String>();
                    map.put("message", DataManager.getInstance().toBase64(messageText));
                    map.put("user", user_name);
                    map.put("date", DataManager.getInstance().getCurrent());
                    map.put("msg_type", "1");
                    map.put("sender_id",userId);
                    Log.e("send mdg===", map.toString());
                    reference1.push().setValue(map);

                  // if (NetworkReceiver.isConnected())

                    if(NetworkAvailablity.checkNetworkStatus(ChatAct.this)) sendPushNotifications(messageText);
                    else Toast.makeText(ChatAct.this, getString(R.string.network_failure), Toast.LENGTH_SHORT).show();

                  //  else
                  //      Toast.makeText(this, getString(R.string.network_failure), Toast.LENGTH_SHORT).show();*/
                }
                binding.ChatLayout.tvMessage.setText("");
            } else {
                binding.ChatLayout.tvMessage.setError("Field is Blank");
            }
        });

        disablesend_button();

        binding.ChatLayout.tvMessage.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0) {
                    binding.ChatLayout.imgSendIcon.setEnabled(true);
                    binding.ChatLayout.imgSendIcon.setAlpha((float) 1.0);
                } else {
                    disablesend_button();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    private void sendPushNotifications(String messageText) {
        Map<String, String> map = new HashMap<>();
        Map<String,String> headerMap = new HashMap<>();
        headerMap.put("Authorization","Bearer " +PreferenceConnector.readString(ChatAct.this, PreferenceConnector.access_token,""));
        headerMap.put("Accept","application/json");

        map.put("user_id", PreferenceConnector.readString(ChatAct.this, PreferenceConnector.User_id, ""));
        map.put("sender_id", PreferenceConnector.readString(ChatAct.this, PreferenceConnector.User_id, ""));
        map.put("receiver_id",receiverId);
        map.put("chat_message",messageText);
        map.put("register_id", PreferenceConnector.readString(ChatAct.this, PreferenceConnector.Register_id, ""));

        Log.e(TAG, "Chat msg Request :" + map);
        Call<ResponseBody> loginCall = apiInterface.sendNotification(headerMap,map);
        loginCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                DataManager.getInstance().hideProgressMessage();
                try {
                    String responseData = response.body() != null ? response.body().string() : "";
                    JSONObject object = new JSONObject(responseData);
                    Log.e(TAG, "Chat msg Response :" + object);
                    if (object.optString("status").equals("1")) {

                    } else if (object.optString("status").equals("0")) {
                        // Toast.makeText(OrderDetailsAct.this, data.message /*getString(R.string.wrong_username_password)*/, Toast.LENGTH_SHORT).show();
                    }

                    else if (object.getString("status").equals("5")) {
                        PreferenceConnector.writeString(ChatAct.this, PreferenceConnector.LoginStatus, "false");
                        startActivity(new Intent(ChatAct.this, Splash.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP));
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

    private void disablesend_button() {
        binding.ChatLayout.imgSendIcon.setEnabled(false);
        binding.ChatLayout.imgSendIcon.setAlpha((float) 0.3);
    }


    private void setUserInfo(String pid) {

        String self_user = userId;
        // String other_user = user_id;
        String other_user = pid;
        if (Double.parseDouble(self_user) > Double.parseDouble(other_user)) {
            reference1 = FirebaseDatabase.getInstance()
                    .getReferenceFromUrl("https://decoded-reducer-294611.firebaseio.com/" + "messages" + "_" + self_user + "_" + other_user);

                 //   .getReferenceFromUrl("https://afarycodeseller-default-rtdb.firebaseio.com/" + "messages" + "_" + self_user + "_" + other_user);
        } else {
            reference1 = FirebaseDatabase.getInstance()
                    .getReferenceFromUrl("https://decoded-reducer-294611.firebaseio.com/" + "messages" + "_" + other_user + "_" + self_user);

                  //  .getReferenceFromUrl("https://afarycodeseller-default-rtdb.firebaseio.com/" + "messages" + "_" + other_user + "_" + self_user);
        }
        reference1.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                //    Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();
                GenericTypeIndicator<Map<String, String>> genericTypeIndicator = new GenericTypeIndicator<Map<String, String>>() {};
                Map<String, String> map = dataSnapshot.getValue(genericTypeIndicator);
                String message = map.get("message").toString();
                Log.e("ttttt",map+"");

                if (map != null) {
                    String sender_id = map.get("sender_id").toString();
                    String userName = map.get("user").toString();
                    String time = map.get("date").toString();
                    String msg_type = map.get("msg_type").toString();
                    if (userName.equals(user_name)) {
                        addMessageBox(message, 1, time, msg_type, "");
                    } else {
                        addMessageBox(message, 2, time, msg_type, "");
                    }
                }
                Log.e("mSgChat", "onChildAdded: " + message);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }



    public void addMessageBox(final String message, int type, String date, String msg_type, final String url) {
        TextView tv_time = null, tv_message = null,tvName;
        RelativeLayout relative_img_message = null;
        ImageView img_message = null;
        View view = null;

        if (type == 1) {
            view = LayoutInflater.from(getApplicationContext()).inflate(R.layout.layout_chat_white_bg, null);
            tv_time = view.findViewById(R.id.tv_time);
            tv_message = view.findViewById(R.id.tv_message);
            relative_img_message = view.findViewById(R.id.relative_img_message);
            img_message = view.findViewById(R.id.ivImg);
            tvName = view.findViewById(R.id.tvName);
            tvName.setText(user_name);
            tv_time.setText(DataManager.getInstance().getCurrent());//DataManager.getInstance().dateformatenew(date)
            Glide.with(getApplicationContext())
                    .load(user_image)
                    .apply(new RequestOptions().placeholder(R.drawable.user_default))
                    .into(img_message);
            tv_message.setText(DataManager.getInstance().fromBase64(message));

         /*   if (msg_type.equalsIgnoreCase("1")) {
                tv_message.setVisibility(View.VISIBLE);
                relative_img_message.setVisibility(View.GONE);
                tv_message.setText(DataManager.getInstance().fromBase64(message));
            } else {
                tv_message.setVisibility(View.GONE);
                relative_img_message.setVisibility(View.GONE);
                final View finalView = view;

            }*/
        } else {
            view = LayoutInflater.from(getApplicationContext()).inflate(R.layout.layout_chat_left_bg, null);
            tv_time = view.findViewById(R.id.tv_time1);
            tv_message = view.findViewById(R.id.tv_message1);
            relative_img_message = view.findViewById(R.id.relative_img_message1);
            img_message = view.findViewById(R.id.ivImg1);
            tv_time.setText(DataManager.getInstance().getCurrent());//DataManager.getInstance().dateformatenew(date)
            tvName = view.findViewById(R.id.tvName1);
            tvName.setText(receiverName);
            Glide.with(getApplicationContext())
                    .load(receiverImage)
                    .apply(new RequestOptions().placeholder(R.drawable.user_default))
                    .into(img_message);
            tv_message.setText(DataManager.getInstance().fromBase64(message));
          /*  if (msg_type.equalsIgnoreCase("1")) {
                tv_message.setVisibility(View.VISIBLE);
                relative_img_message.setVisibility(View.GONE);
                tv_message.setText(DataManager.getInstance().fromBase64(message));
            } else {
                tv_message.setVisibility(View.GONE);
                relative_img_message.setVisibility(View.VISIBLE);

                final View finalView1 = view;

            }*/
        }
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lp.setMargins(0, 0, 0, 10);
        view.setLayoutParams(lp);

        binding.layout1.addView(view);
        binding.scrollView.post(new Runnable() {
            @Override
            public void run() {
                binding.scrollView.fullScroll(View.FOCUS_DOWN);
            }
        });
    }


}
