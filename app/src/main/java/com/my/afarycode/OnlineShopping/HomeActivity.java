package com.my.afarycode.OnlineShopping;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import com.my.afarycode.OnlineShopping.fragment.HomeFragment;
import com.my.afarycode.OnlineShopping.fragment.MyBookingFragment;
import com.my.afarycode.OnlineShopping.fragment.MyProfileFragment;
import com.my.afarycode.OnlineShopping.fragment.WalletFragment;
import com.my.afarycode.R;
import com.my.afarycode.databinding.ActivityHomeBinding;
import com.my.afarycode.ratrofit.AfaryCode;
import com.my.afarycode.ratrofit.ApiClient;
import cn.pedant.SweetAlert.SweetAlertDialog;

public class HomeActivity extends AppCompatActivity {

    ActivityHomeBinding binding;
    Fragment fragment;
    private long backPressedTime;
    private Toast backToast;
    FragmentManager fragmentManager = getSupportFragmentManager();
    private AfaryCode apiInterface;
    private String status;



    BroadcastReceiver OrderStatusReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getStringExtra("status") != null) {
                try {
                    if ("Order Accepted".equals(intent.getStringExtra("status")) || "Order Rejected".equalsIgnoreCase(intent.getStringExtra("status"))) {
                        AlertOrderStatus(intent.getStringExtra("msg"));
                        Log.e("order msg====",intent.getStringExtra("msg"));
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
      /*  if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            status = extras.getString("status");
        }*/

       /* if (status.equals("accept")) {
            new SweetAlertDialog(this, SweetAlertDialog.SUCCESS_TYPE)
                    .setTitleText("Request Accepted !")
                    .setContentText("Your Order Have  SuccessFully Accepted!!!")
                    .show();
        }*/

        apiInterface = ApiClient.getClient(this).create(AfaryCode.class);

        binding.RRHome.setOnClickListener(v -> {
            fragment = new HomeFragment();
            loadFragment(fragment);

        });

        binding.RRMyBooking.setOnClickListener(v -> {

          //  fragment = new MyBookingFragment();
           // loadFragment(fragment);

        });

        binding.RRProfile.setOnClickListener(v -> {
            fragment = new MyProfileFragment();
            loadFragment(fragment);
        });

        binding.RRwallet.setOnClickListener(v -> {
            fragment = new WalletFragment();
            loadFragment(fragment);
        });

        fragment = new HomeFragment();
        loadFragment(fragment);
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
        registerReceiver(OrderStatusReceiver, new IntentFilter("check_status"));

    }



}