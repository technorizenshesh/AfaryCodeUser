package com.my.afarycode.OnlineShopping;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.my.afarycode.OnlineShopping.Model.GetProfileModal;
import com.my.afarycode.OnlineShopping.chat.ChatListAct;
import com.my.afarycode.OnlineShopping.constant.PreferenceConnector;
import com.my.afarycode.OnlineShopping.fragment.HomeFragment;
import com.my.afarycode.OnlineShopping.fragment.MyBookingFragment;
import com.my.afarycode.OnlineShopping.fragment.MyProfileFragment;
import com.my.afarycode.OnlineShopping.fragment.WalletFragment;
import com.my.afarycode.OnlineShopping.helper.DataManager;
import com.my.afarycode.OnlineShopping.ratereview.RateReviewAct;
import com.my.afarycode.R;
import com.my.afarycode.Splash;
import com.my.afarycode.databinding.ActivityHomeBinding;
import com.my.afarycode.ratrofit.AfaryCode;
import com.my.afarycode.ratrofit.ApiClient;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import cn.pedant.SweetAlert.SweetAlertDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeActivity extends AppCompatActivity {

    ActivityHomeBinding binding;
    Fragment fragment;
    private long backPressedTime;
    private Toast backToast;
    FragmentManager fragmentManager = getSupportFragmentManager();
    private AfaryCode apiInterface;
    private String status="",msg="",orderId="";



    BroadcastReceiver OrderStatusReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getStringExtra("status") != null) {
                Log.e("msg====",intent.getStringExtra("msg"));
                try {
                    if ("Order Accepted".equals(intent.getStringExtra("status")) || "Order Rejected".equalsIgnoreCase(intent.getStringExtra("status"))) {
                        AlertOrderStatus(intent.getStringExtra("msg"));
                        Log.e("order msg====",intent.getStringExtra("msg"));
                    }

                    else if("You have been logged out because you have logged in on another device".equals(intent.getStringExtra("msg")))
                    {
                        Log.e("Logout msg====",intent.getStringExtra("msg"));
                        PreferenceConnector.writeString(HomeActivity.this, PreferenceConnector.LoginStatus, "false");
                        startActivity(new Intent(HomeActivity.this, Splash.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP));
                        finish();


                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }


    };

    private void AlertOrderStatus(String msg) {

            AlertDialog.Builder builder = new AlertDialog.Builder(HomeActivity.this);
            builder.setMessage(msg)
                    .setCancelable(false)
                    .setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });

            AlertDialog alertDialog = builder.create();
            alertDialog.show();



    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_home);

           if(getIntent()!=null) {
               status = getIntent().getStringExtra("status");
               msg = getIntent().getStringExtra("msg");
                 if(status.equals("openPaymentDialog")) createAndShowDialog(HomeActivity.this,msg);
                 else if(status.equals("orderCompleteDialog")){
                     orderId = getIntent().getStringExtra("order_id");
                     createAndShowDialog(HomeActivity.this,msg);
                 }
/*
                 else if(status.equals("You have been logged out because you have logged in on another device")){
                     PreferenceConnector.writeString(HomeActivity.this, PreferenceConnector.LoginStatus, "false");
                     startActivity(new Intent(HomeActivity.this, Splash.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP));
                     finish();

                 }
*/
           }




        apiInterface = ApiClient.getClient(this).create(AfaryCode.class);

        binding.RRHome.setOnClickListener(v -> {
            fragment = new HomeFragment();
            loadFragment(fragment);

        });

        binding.RRChat.setOnClickListener(v -> {

          //  fragment = new MyBookingFragment();
           // loadFragment(fragment);

            startActivity(new Intent(HomeActivity.this, ChatListAct.class));

        });

        binding.RRProfile.setOnClickListener(v -> {
            fragment = new MyProfileFragment();
            loadFragment(fragment);
        });

        binding.RRwallet.setOnClickListener(v -> {
            fragment = new WalletFragment();
            loadFragment(fragment);
        });

       // fragment = new HomeFragment();
      //  loadFragment(fragment);
        GetProfile();
    }


    public boolean loadFragment(Fragment fragment) {
        if (fragment != null) {
            fragmentManager.beginTransaction()
                    .addToBackStack("Home")
                    .replace(R.id.fragment_homeContainer, fragment)
                    .commit();
            return true;
        }
        return false;
    }



    private void changeLocale(String en) {
        updateResources(HomeActivity.this,en);
        PreferenceConnector.writeString(HomeActivity.this, PreferenceConnector.LANGUAGE, en);
         fragment = new HomeFragment();
          loadFragment(fragment);
    }


    private void updateResources(Context wellcomeScreen, String en) {
        Locale locale = new Locale(en);
        Locale.setDefault(locale);
        Resources resources = wellcomeScreen.getResources();
        Configuration configuration = resources.getConfiguration();
        configuration.locale = locale;
        resources.updateConfiguration(configuration, resources.getDisplayMetrics());

    }


    @Override
    public void onBackPressed() {
        if (fragmentManager.getBackStackEntryCount() > 1) {
            fragmentManager.popBackStack();
        } else if (fragmentManager.getBackStackEntryCount() == 1) {
            if (backPressedTime + 2000 > System.currentTimeMillis()) {
                finish();
                return;
            } else {
                backToast = Toast.makeText(HomeActivity.this, "Press once again to exit", Toast.LENGTH_SHORT);
                backToast.show();
            }
            backPressedTime = System.currentTimeMillis();
        }
    }


    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(OrderStatusReceiver);
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            registerReceiver(OrderStatusReceiver, new IntentFilter("check_status"),Context.RECEIVER_EXPORTED);
        }
        else {
            registerReceiver(OrderStatusReceiver, new IntentFilter("check_status"));
        }
    }



    public void createAndShowDialog(Context context,String msg) {
       // Dialog dialog = new Dialog(context, R.style.FullScreenDialog);

        Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.layout_fullscreen_dialog);
        dialog.setCanceledOnTouchOutside(true);

        TextView tv = dialog.findViewById(R.id.tvMsg);
        RelativeLayout btnOk = dialog.findViewById(R.id.btnOk);

        tv.setText(msg);
        btnOk.setOnClickListener(view -> {
            if(status.equals("")){
                startActivity(new Intent(HomeActivity.this, RateReviewAct.class)
                        .putExtra("order_id",orderId));
            }

            dialog.dismiss();

        });

        WindowManager.LayoutParams layoutParams = dialog.getWindow().getAttributes();
        dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
        dialog.getWindow().setAttributes(layoutParams);
        dialog.show();
    }



    private void GetProfile() {
        DataManager.getInstance().showProgressMessage(HomeActivity.this, getString(R.string.please_wait));
        Map<String, String> map = new HashMap<>();
        map.put("user_id", PreferenceConnector.readString(HomeActivity.this, PreferenceConnector.User_id, ""));
        map.put("register_id", PreferenceConnector.readString(HomeActivity.this, PreferenceConnector.Register_id, ""));
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
                        if(data.getResult().getLanguage().equalsIgnoreCase("en")) changeLocale("en");
                        else if(data.getResult().getLanguage().equalsIgnoreCase("fr")) changeLocale("fr");
                        else changeLocale("en");

                    } else if (data.status.equals("0")) {
                        Toast.makeText(HomeActivity.this, data.message /*getString(R.string.wrong_username_password)*/, Toast.LENGTH_SHORT).show();
                    }

                    else if (data.status.equals("5")) {

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