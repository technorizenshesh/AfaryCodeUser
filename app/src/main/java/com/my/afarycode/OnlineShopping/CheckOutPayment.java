package com.my.afarycode.OnlineShopping;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.databinding.DataBindingUtil;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.Gson;
import com.my.afarycode.OnlineShopping.Model.GetProfileModal;
import com.my.afarycode.OnlineShopping.constant.PreferenceConnector;
import com.my.afarycode.OnlineShopping.helper.DataManager;
import com.my.afarycode.OnlineShopping.myorder.MyOrderScreen;
import com.my.afarycode.R;
import com.my.afarycode.databinding.ActivityCheckOutPaymentBinding;
import com.my.afarycode.ratrofit.AfaryCode;
import com.my.afarycode.ratrofit.ApiClient;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CheckOutPayment extends AppCompatActivity {
    public String TAG = "CheckOutPayment";
    ActivityCheckOutPaymentBinding binding;
   // private ArrayList<String> cart_id_string = new ArrayList<>();
    private String strList = "",cart_id_string="";
    private String totalPriceToToPay = "",deliveryCharge="",platFormsFees="",taxN1="",taxN2="";
    private AfaryCode apiInterface;
    GetProfileModal data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_check_out_payment);
        apiInterface = ApiClient.getClient(this).create(AfaryCode.class);

        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();

            if (extras == null) {
                cart_id_string = null;

            } else {
                //cart_id_string = extras.getStringArrayList("cart_id_array");
                cart_id_string = extras.getString("cart_id_array");
                totalPriceToToPay = extras.getString("totalPriceToToPay");

                deliveryCharge = extras.getString("deliveryCharge");
                platFormsFees = extras.getString("platFormsFees");
                taxN1 = extras.getString("taxN1");
                taxN2 = extras.getString("taxN2");




            }

        } else {
            /*newString= (String) savedInstanceState.getParcelableArrayList("cart_id_array");*/
        }

        Log.e("cart_id_array>>>>", "" + cart_id_string);

        //strList = cart_id_string.toString();

        //strList = strList.replace("[", "")
         //       .replace("]", "")
         //       .replace(" ", "");

        Log.e("strList>>>", strList);

        //cart_id_array

        binding.RRback.setOnClickListener(v -> {
            onBackPressed();
        });

        binding.llDone.setOnClickListener(v -> {
            startActivity(new Intent(CheckOutPayment.this, SuccessScreen.class));
            finish();
        });

        binding.llTransfer.setOnClickListener(v -> {
           // PaymentAPI("VM", strList);
         String ll =   "https://technorizen.com/afarycodewebsite/home/redirectwebpvit?tel_marchand=074272732&montant=105&ref=Ref13082020ab&user_id=" + PreferenceConnector.readString(CheckOutPayment.this, PreferenceConnector.User_id, "")
            + "&user_number=" + data.getResult().getMobile()+"&redirect=https://technorizen.com/afarycodewebsite/";

           startActivity(new Intent(CheckOutPayment.this, PaymentWebViewAct.class)
                   .putExtra("url",ll));
        });

        binding.llMoov.setOnClickListener(v -> {
            dialogMoov("MC",strList);

        });

        binding.llAirtel.setOnClickListener(v -> {
            dialogAirtel("AM",strList);
        });

        binding.llCod.setOnClickListener(v -> {
           dialogAlert();
        });

        binding.llWallet.setOnClickListener(v -> {
           if(Double.parseDouble(data.getResult().getWallet()) >= Double.parseDouble(totalPriceToToPay))
            PaymentAPI("","",data.getResult().getMobile(),"Wallet");
           else Toast.makeText(this, "Low wallet balance please recharge your wallet.", Toast.LENGTH_SHORT).show();
        });




        GetProfile();
    }


    private void GetProfile() {
        DataManager.getInstance().showProgressMessage(CheckOutPayment.this, "Please wait...");
        Map<String, String> map = new HashMap<>();
        map.put("user_id", PreferenceConnector.readString(CheckOutPayment.this, PreferenceConnector.User_id, ""));
        Call<GetProfileModal> loginCall = apiInterface.get_profile(map);

        loginCall.enqueue(new Callback<GetProfileModal>() {
            @Override
            public void onResponse(Call<GetProfileModal> call,
                                   Response<GetProfileModal> response) {

                DataManager.getInstance().hideProgressMessage();

                try {

                     data = response.body();
                    String dataResponse = new Gson().toJson(response.body());

                    Log.e("MapMap", "GET RESPONSE" + dataResponse);

                    if (data.status.equals("1")) {

                    } else if (data.status.equals("0")) {
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





    private void dialogAlert() {
        AlertDialog.Builder builder = new AlertDialog.Builder(CheckOutPayment.this);
        builder.setMessage("Dear Customer,\n" +
                        "You have requested delivery with cash on delivery. One of our \n" +
                        "salespeople may call you.\n" +
                        "Do you confirm this number…" + data.getResult().mobile )
                .setCancelable(false)
                .setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        PaymentAPI("VM", strList,data.getResult().mobile,"Cash");

                    }
                }).setNegativeButton(getString(R.string.no), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        dialogNumber("VM","");
                    }
                });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }



    private void PaymentAPI(String operateur, String strList,String number,String paymentType) {
        //binding.loader.setVisibility(View.VISIBLE);
        DataManager.getInstance().showProgressMessage(CheckOutPayment.this, "Please wait...");
        Map<String,String> headerMap = new HashMap<>();
        headerMap.put("Authorization","Bearer " +PreferenceConnector.readString(CheckOutPayment.this, PreferenceConnector.access_token,""));
        headerMap.put("Accept","application/json");


        Map<String, String> map = new HashMap<>();
        map.put("user_id", PreferenceConnector.readString(CheckOutPayment.this, PreferenceConnector.User_id, ""));
        map.put("amount", /*"105"*/totalPriceToToPay);

        map.put("delivery_charge", deliveryCharge);
        map.put("platFormsFees", platFormsFees);
        map.put("taxN1", taxN1);
        map.put("taxN2", taxN2);
        map.put("operateur", operateur);
        map.put("cart_id", strList);
        if(operateur.equals("MC"))  map.put("num_marchand", "060110217");
        else if(operateur.equals("AM")) map.put("num_marchand", "074272732");
        else if(operateur.equals("VM")) map.put("num_marchand", "074272732");
        else if(operateur.equals(""))map.put("num_marchand", "");
        map.put("type", "USER");
        map.put("user_number",number);
        map.put("register_id", PreferenceConnector.readString(CheckOutPayment.this, PreferenceConnector.Firebash_Token, ""));
        map.put("address_id", PreferenceConnector.readString(CheckOutPayment.this, PreferenceConnector.ADDRESS_ID, ""));

        map.put("payment_type",paymentType);

        Log.e("MapMap", "payment_params" + map);

        Call<ResponseBody> loginCall = apiInterface.payment(headerMap,map);
        loginCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                DataManager.getInstance().hideProgressMessage();
               // binding.loader.setVisibility(View.GONE);
                try {
                    String responseData = response.body() != null ? response.body().string() : "";
                    JSONObject object = new JSONObject(responseData);
                    Log.e(TAG, "Payment RESPONSE" + object);
                    if (object.optString("status").equals("1")) {
                      //  PaymentModal data = new Gson().fromJson(responseData, PaymentModal.class);
                       // binding.loader.setVisibility(View.GONE);
                         if(paymentType.equals("Wallet") || paymentType.equals("Cash")) {
                             Toast.makeText(CheckOutPayment.this, object.getString("message"), Toast.LENGTH_SHORT).show();
                             startActivity(new Intent(CheckOutPayment.this, MyOrderScreen.class)
                                     .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP));
                             finish();
                         }
                         else {
                             PreferenceConnector.writeString(CheckOutPayment.this,PreferenceConnector.transId,object.getJSONObject("ressult").getString("reference"));
                             PreferenceConnector.writeString(CheckOutPayment.this,PreferenceConnector.serviceType,PreferenceConnector.Booking);

                             startActivity(new Intent(CheckOutPayment.this, CheckPaymentStatusAct.class));

                         }
                    } else if (object.optString("status").equals("0")) {
                        //binding.loader.setVisibility(View.GONE);
                        Toast.makeText(CheckOutPayment.this, object.getString("message"), Toast.LENGTH_SHORT).show();                    }


                } catch (Exception e) {
                    Log.e("error>>>>", "" + e);
                    binding.loader.setVisibility(View.GONE);
                    e.printStackTrace();
                }






            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                call.cancel();
                Toast.makeText(CheckOutPayment.this, "Network Error !!!!", Toast.LENGTH_SHORT).show();
              //  binding.loader.setVisibility(View.GONE);
                DataManager.getInstance().hideProgressMessage();
            }
        });
    }



    public void dialogNumber(String operator,String strList){
        Dialog mDialog = new Dialog(CheckOutPayment.this);
        mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mDialog.setContentView(R.layout.dialog_number);
        mDialog.setCancelable(false);
        mDialog.setCanceledOnTouchOutside(false);

        EditText edNumber = mDialog.findViewById(R.id.edNumber);
        AppCompatButton btnBack = mDialog.findViewById(R.id.btnBack);
        AppCompatButton btnPayNow = mDialog.findViewById(R.id.btnPayNow);

        btnBack.setOnClickListener(v -> {
            mDialog.dismiss();

        });

        btnPayNow.setOnClickListener(v -> {
            if(edNumber.getText().toString().equals(""))
                Toast.makeText(CheckOutPayment.this, "Please enter number", Toast.LENGTH_SHORT).show();

            else {
                mDialog.dismiss();
                PaymentAPI(operator, cart_id_string,edNumber.getText().toString(),"Cash");
            }

        });
        mDialog.show();

    }




    public void dialogAirtel(String operator,String strList){
        Dialog mDialog = new Dialog(CheckOutPayment.this);
        mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mDialog.setContentView(R.layout.dialog_airtel);
        mDialog.setCancelable(false);
        mDialog.setCanceledOnTouchOutside(false);

        EditText edNumber = mDialog.findViewById(R.id.edNumber);
        AppCompatButton btnBack = mDialog.findViewById(R.id.btnBack);
        AppCompatButton btnPayNow = mDialog.findViewById(R.id.btnPayNow);

        btnBack.setOnClickListener(v -> {
            mDialog.dismiss();

        });

        btnPayNow.setOnClickListener(v -> {
           if(edNumber.getText().toString().equals(""))
               Toast.makeText(CheckOutPayment.this, "Please enter number", Toast.LENGTH_SHORT).show();

         else {
               mDialog.dismiss();
               PaymentAPI(operator, cart_id_string,edNumber.getText().toString(),"Online");
           }

        });
        mDialog.show();

    }

    public void dialogMoov(String operator,String strList){
        Dialog mDialog = new Dialog(CheckOutPayment.this);
        mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mDialog.setContentView(R.layout.dialog_moov);
        mDialog.setCancelable(false);
        mDialog.setCanceledOnTouchOutside(false);

        EditText edNumber = mDialog.findViewById(R.id.edNumber);
        AppCompatButton btnBack = mDialog.findViewById(R.id.btnBack);
        AppCompatButton btnPayNow = mDialog.findViewById(R.id.btnPayNow);

        btnBack.setOnClickListener(v -> {
            mDialog.dismiss();

        });

        btnPayNow.setOnClickListener(v -> {
            if(edNumber.getText().toString().equals(""))
                Toast.makeText(CheckOutPayment.this, "Please enter number", Toast.LENGTH_SHORT).show();

            else {
                mDialog.dismiss();
                PaymentAPI(operator, cart_id_string,edNumber.getText().toString(),"Online");
            }

        });

        mDialog.show();
    }


}