package com.my.afarycode.OnlineShopping;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.method.LinkMovementMethod;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;

import com.my.afarycode.OnlineShopping.constant.PreferenceConnector;
import com.my.afarycode.OnlineShopping.helper.MyService;
import com.my.afarycode.OnlineShopping.myorder.MyOrderScreen;
import com.my.afarycode.R;
import com.my.afarycode.databinding.ActivityPaymentStatusBinding;
import com.my.afarycode.ratrofit.AfaryCode;
import com.my.afarycode.ratrofit.ApiClient;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class CheckPaymentStatusAct extends AppCompatActivity {
    ActivityPaymentStatusBinding binding;
    String paymentBy="";
    private AfaryCode apiInterface;
    private String TAG ="CheckPaymentStatusAct";
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
                       if(paymentBy.equals("user")) {
                           startActivity(new Intent(CheckPaymentStatusAct.this, MyOrderScreen.class)
                                   .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP));
                           finish();
                       }
                       else {
                           Toast.makeText(CheckPaymentStatusAct.this, object.getString("message"), Toast.LENGTH_SHORT).show();
                           startActivity(new Intent(CheckPaymentStatusAct.this,HomeActivity.class)
                                   .putExtra("status","")
                                   .putExtra("msg","").addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP));
                           finish();
                       }
                    }


                    else if (object.getString("status").equals("3")) {
                            Log.e("Payment under processing ====",intent.getStringExtra("object")+"");


                        }


                    else if (object.getString("status").equals("9")) {
                        Log.e("Payment has not been completed ====",intent.getStringExtra("object")+"");
                        showAlertDialog();

                    }




                    else {
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


    private void showAlertDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Transaction Status")
                .setMessage("Your transaction has not been completed. Please try again or choose another method of payment.\n\nIf on the contrary this message appears while you have been debited, please contact the administrator here (link).")
                .setPositiveButton("Ok", null)
                .setNegativeButton("Cancel", null);

        AlertDialog dialog = builder.create();
        dialog.setOnShowListener(dialogInterface -> {
            TextView messageTextView = dialog.findViewById(android.R.id.message);
            if (messageTextView != null) {
                String message = "Your transaction has not been completed. Please try again or choose another method of payment.<br><br>" +
                        "If on the contrary this message appears while you have been debited, please contact the administrator <a href='#'>here</a>.";

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    messageTextView.setText(Html.fromHtml(message, Html.FROM_HTML_MODE_LEGACY));
                }
                messageTextView.setMovementMethod(LinkMovementMethod.getInstance());

                // Highlight the "here" text in red
                SpannableString spannableString = new SpannableString(messageTextView.getText());
                int start = message.indexOf("here");
                int end = start + "here".length();
                spannableString.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.red)), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

                messageTextView.setText(spannableString);

                // Handle the click on the link
                messageTextView.setOnClickListener(v -> {
                    showDialog();
                });
            }
        });

        dialog.show();
    }


    private void showDialog() {
        // Get the current date and time
        String currentDateTime = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(new Date());

        String message = "Just placed an order today (" + currentDateTime + "). My account has been debited but I do not have confirmation from the app. Can you see this right away?";

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Order Confirmation")
                .setMessage(message)
                .setPositiveButton("Send", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Handle the send action
                        // For example, send the message to the administrator or log the issue
                        sendToAdminMsg(message);
                    }
                })
                .setNegativeButton("Cancel", null);

        // Create and show the dialog
        AlertDialog dialog = builder.create();
        dialog.show();
    }



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_payment_status);
        apiInterface = ApiClient.getClient(this).create(AfaryCode.class);
        initViews();
    }





    private void initViews() {

        if(getIntent()!=null) paymentBy = getIntent().getStringExtra("paymentBy");

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


    public void sendToAdminMsg(String msg) {
        Map<String,String> headerMap = new HashMap<>();
        headerMap.put("Authorization","Bearer " + PreferenceConnector.readString(getApplicationContext(), PreferenceConnector.access_token,""));
        headerMap.put("Accept","application/json");
        Log.e("transactionId===","transactionId = " + PreferenceConnector.readString(getApplicationContext(),PreferenceConnector.transId,""));
        Map<String,String> map = new HashMap<>();
        map.put("user_id",PreferenceConnector.readString(getApplicationContext(),PreferenceConnector.User_id,""));
        map.put("reference",PreferenceConnector.readString(getApplicationContext(),PreferenceConnector.transId,""));
        map.put("user_message",msg);


        Log.e(TAG,"Send Admin Msg Request "+map);
        Call<ResponseBody> loginCall = apiInterface.sendAdminMsg(headerMap,map);
        loginCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    String responseData = response.body() != null ? response.body().string() : "";
                    JSONObject object = new JSONObject(responseData);
                    Log.e(TAG, "Send Admin Msg Response" + object);
                    startActivity(new Intent(CheckPaymentStatusAct.this,HomeActivity.class)
                            .putExtra("status","")
                            .putExtra("msg","").addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP));
                    finish();

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                call.cancel();
            }
        });


    }



}
