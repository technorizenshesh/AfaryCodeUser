package com.my.afarycode.OnlineShopping;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;

import com.my.afarycode.OnlineShopping.helper.MyService;
import com.my.afarycode.OnlineShopping.myorder.MyOrderScreen;
import com.my.afarycode.R;
import com.my.afarycode.databinding.ActivityPaymentStatusBinding;

import org.json.JSONObject;


public class CheckPaymentStatusAct extends AppCompatActivity {
    ActivityPaymentStatusBinding binding;


    BroadcastReceiver PaymentStatusReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.e("PaymentStatusReceiver ====","=====");
            try {
                JSONObject object = new JSONObject(intent.getStringExtra("object"));
                if(intent.getStringExtra("object")!= null) {
                     JSONObject result = object.getJSONObject("result");
                    Log.e("PaymentStatusReceiver ====",intent.getStringExtra("object")+"");

                    if(object.getString("status").equals("1")){
                        Toast.makeText(CheckPaymentStatusAct.this, object.getString("message"), Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(CheckPaymentStatusAct.this, MyOrderScreen.class)
                                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP));
                             finish();
                    }


                    else if (object.getString("status").equals("3")) {
                            Log.e("Payment under processing ====",intent.getStringExtra("object")+"");


                        } else {
                        Toast.makeText(CheckPaymentStatusAct.this, object.getString("message"), Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(CheckPaymentStatusAct.this,HomeActivity.class)
                                .putExtra("status","")
                                .putExtra("msg","").addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP));
                        finish();
                        }


                }


            }
            catch (Exception e){
                e.printStackTrace();
            }

        }
    };





    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_payment_status);
        initViews();
    }





    private void initViews() {


    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(PaymentStatusReceiver, new IntentFilter("check_payment_status"));
        startService(new Intent(CheckPaymentStatusAct.this, MyService.class));

    }


    @Override
    protected void onStop() {
        super.onStop();
        stopService(new Intent(CheckPaymentStatusAct.this, MyService.class));
        unregisterReceiver(PaymentStatusReceiver);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    //    stopService(new Intent(CheckPaymentStatusAct.this, MyService.class));
      //  unregisterReceiver(PaymentStatusReceiver);
    }

}
