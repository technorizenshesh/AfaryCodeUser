package com.my.afarycode.OnlineShopping;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.app.ActivityCompat;
import androidx.databinding.DataBindingUtil;

import android.Manifest;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.gson.Gson;
import com.hbb20.CountryCodePicker;
import com.my.afarycode.OnlineShopping.Model.GetProfileModal;
import com.my.afarycode.OnlineShopping.constant.PreferenceConnector;
import com.my.afarycode.OnlineShopping.deeplink.PaymentByAnotherAct;
import com.my.afarycode.OnlineShopping.helper.CountryCodes;
import com.my.afarycode.OnlineShopping.helper.DataManager;
import com.my.afarycode.OnlineShopping.helper.NetworkAvailablity;
import com.my.afarycode.OnlineShopping.myorder.MyOrderScreen;
import com.my.afarycode.R;
import com.my.afarycode.Splash;
import com.my.afarycode.databinding.ActivityCheckOutPaymentBinding;
import com.my.afarycode.ratrofit.AfaryCode;
import com.my.afarycode.ratrofit.ApiClient;

import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CheckOutPayment extends AppCompatActivity {
    public String TAG = "CheckOutPayment";
    ActivityCheckOutPaymentBinding binding;
    // private ArrayList<String> cart_id_string = new ArrayList<>();
    private String strList = "", cart_id_string = "";
    private String deliveryYesNo = "", totalPriceToToPay = "", deliveryCharge = "", platFormsFees = "", taxN1 = "", taxN2 = "", sendToServer = "", anotherPersonId = "", insertDeliveryId = "";
    private AfaryCode apiInterface;
    GetProfileModal data;
    CountryCodePicker ccp;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    String orderType = "";

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
                sendToServer = extras.getString("sendToServer");
                deliveryYesNo = extras.getString("selfCollect");
                insertDeliveryId = extras.getString("insertDeliveryId");
                orderType = extras.getString("orderType");


                if (deliveryYesNo.equalsIgnoreCase("Yes")) {
                    binding.rl33.setVisibility(View.GONE);
                    binding.rl22.setVisibility(View.VISIBLE);
                    binding.rl11.setVisibility(View.GONE);

                } else {
                    binding.rl33.setVisibility(View.VISIBLE);
                    binding.rl22.setVisibility(View.GONE);
                    binding.rl11.setVisibility(View.VISIBLE);

                }

                if (orderType.equalsIgnoreCase("INTERNATIONAL")) {
                    binding.rl33.setVisibility(View.GONE);
                    binding.rl22.setVisibility(View.VISIBLE);
                    binding.rl11.setVisibility(View.GONE);

                }


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
           /* String refNumber = generateReferenceNumber();
            String ll =   "https://technorizen.com/afarycodewebsite/home/redirectwebpvit?tel_marchand=074272732&operateur=VM" + "&montant=105" +*//* totalPriceToToPay+*//*  "&ref=" + refNumber + "&user_id=" + PreferenceConnector.readString(CheckOutPayment.this, PreferenceConnector.User_id, "")
                    + "&user_number=" + data.getResult().getMobile()+"&redirect=https://technorizen.com/afarycodewebsite/";*/

         /*   PreferenceConnector.writeString(CheckOutPayment.this,PreferenceConnector.transId,refNumber);
            PreferenceConnector.writeString(CheckOutPayment.this,PreferenceConnector.serviceType,PreferenceConnector.Booking);
            PreferenceConnector.writeString(CheckOutPayment.this,PreferenceConnector.ShareUserId,"");
            PreferenceConnector.writeString(CheckOutPayment.this,PreferenceConnector.PaymentType,"Booking");
            Log.e("url===",ll);
           startActivity(new Intent(CheckOutPayment.this, PaymentWebViewAct.class)
                   .putExtra("url",ll)
                   .putExtra("ref",refNumber));*/
           if(NetworkAvailablity.checkNetworkStatus(CheckOutPayment.this)) callCardPayment("", data.getResult().getMobile(), "Card");
           else Toast.makeText(CheckOutPayment.this, getString(R.string.network_failure), Toast.LENGTH_SHORT).show();
        });

        binding.llTransfer11.setOnClickListener(v -> {
          /*  // PaymentAPI("VM", strList);
            String refNumber = generateReferenceNumber();
            String ll =   "https://technorizen.com/afarycodewebsite/home/redirectwebpvit?tel_marchand=074272732&operateur=VM" + "&montant=105" + *//*totalPriceToToPay +*//* "&ref=" + refNumber + "&user_id=" + PreferenceConnector.readString(CheckOutPayment.this, PreferenceConnector.User_id, "")
                    + "&user_number=" + data.getResult().getMobile()+"&redirect=https://technorizen.com/afarycodewebsite/";
            Log.e("url===",ll);*/

            if(NetworkAvailablity.checkNetworkStatus(CheckOutPayment.this)) callCardPayment("", data.getResult().getMobile(), "Card");
            else Toast.makeText(CheckOutPayment.this, getString(R.string.network_failure), Toast.LENGTH_SHORT).show();

        });


        binding.llMoov.setOnClickListener(v -> {
            dialogMoov("MC", strList);

        });

        binding.llAirtel.setOnClickListener(v -> {
            dialogAirtel("AM", strList);
        });

        binding.llCod.setOnClickListener(v -> {
            dialogAlert();
        });

        binding.llWallet.setOnClickListener(v -> {
            if (Double.parseDouble(data.getResult().getWallet()) >= Double.parseDouble(totalPriceToToPay))
            {

                if(NetworkAvailablity.checkNetworkStatus(CheckOutPayment.this))  PaymentAPI("", "", data.getResult().getMobile(), "Wallet");
                else Toast.makeText(CheckOutPayment.this, getString(R.string.network_failure), Toast.LENGTH_SHORT).show();

            }
            else
                Toast.makeText(this, getString(R.string.low_wallet_balance), Toast.LENGTH_SHORT).show();
        });


        binding.llWallet11.setOnClickListener(v -> {
            if (Double.parseDouble(data.getResult().getWallet()) >= Double.parseDouble(totalPriceToToPay))
            {
                if(NetworkAvailablity.checkNetworkStatus(CheckOutPayment.this)) PaymentAPI("", "", data.getResult().getMobile(), "Wallet");
                else Toast.makeText(CheckOutPayment.this, getString(R.string.network_failure), Toast.LENGTH_SHORT).show();
            }
            else
                Toast.makeText(this, getString(R.string.low_wallet_balance), Toast.LENGTH_SHORT).show();
        });


        binding.btnSendAnother.setOnClickListener(v -> {
            dialogSendAnotherPerson();
        });


        if(NetworkAvailablity.checkNetworkStatus(CheckOutPayment.this)) GetProfile();
        else Toast.makeText(CheckOutPayment.this, getString(R.string.network_failure), Toast.LENGTH_SHORT).show();


    }


    private void callCardPayment(String strList, String number, String paymentType) {
        //binding.loader.setVisibility(View.VISIBLE);
        DataManager.getInstance().showProgressMessage(CheckOutPayment.this, getString(R.string.please_wait));
        Map<String, String> headerMap = new HashMap<>();
        headerMap.put("Authorization", "Bearer " + PreferenceConnector.readString(CheckOutPayment.this, PreferenceConnector.access_token, ""));
        headerMap.put("Accept", "application/json");


        Map<String, String> map = new HashMap<>();
        map.put("user_id", PreferenceConnector.readString(CheckOutPayment.this, PreferenceConnector.User_id, ""));
        map.put("amount", /*"105"*/totalPriceToToPay);

        map.put("delivery_charge", deliveryCharge);
        map.put("platFormsFees", platFormsFees);
        map.put("taxN1", taxN1);
        map.put("taxN2", taxN2);
        map.put("operateur", "");
        map.put("cart_id", strList);
        map.put("num_marchand", "");
        map.put("type", "USER");
        map.put("user_number", number);
        map.put("register_id", PreferenceConnector.readString(CheckOutPayment.this, PreferenceConnector.Register_id, ""));
        map.put("address_id", PreferenceConnector.readString(CheckOutPayment.this, PreferenceConnector.ADDRESS_ID, ""));
        map.put("payment_type", paymentType);
        map.put("sub_orderdata", sendToServer);
        map.put("datetime", DataManager.getCurrent());
        map.put("self_collect", deliveryYesNo);
        map.put("delivery_type", orderType);


        Log.e("MapMap", "payment_params" + map);

        Call<ResponseBody> loginCall = apiInterface.cardPaymentApi(headerMap, map);
        loginCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                DataManager.getInstance().hideProgressMessage();
                // binding.loader.setVisibility(View.GONE);
                try {
                    String responseData = response.body() != null ? response.body().string() : "";
                    JSONObject object = new JSONObject(responseData);
                    Log.e(TAG, "Card Payment RESPONSE" + object);
                    if (object.optString("status").equals("1")) {
                        PreferenceConnector.writeString(CheckOutPayment.this, PreferenceConnector.transId, object.getJSONObject("ressult").getString("reference"));
                        PreferenceConnector.writeString(CheckOutPayment.this, PreferenceConnector.serviceType, PreferenceConnector.Booking);
                        PreferenceConnector.writeString(CheckOutPayment.this, PreferenceConnector.ShareUserId, "");
                        PreferenceConnector.writeString(CheckOutPayment.this, PreferenceConnector.PaymentType, "Booking");

                        PreferenceConnector.writeString(CheckOutPayment.this, PreferenceConnector.PaymentOrderId,  object.getJSONObject("ressult").getString("order_id"));

                        startActivity(new Intent(CheckOutPayment.this, PaymentWebViewAct.class)
                                .putExtra("url", object.getJSONObject("ressult").getString("webviewurl"))
                                .putExtra("ref", object.getJSONObject("ressult").getString("reference")));
                    } else if (object.optString("status").equals("0")) {
                        //binding.loader.setVisibility(View.GONE);
                        Toast.makeText(CheckOutPayment.this, object.getString("message"), Toast.LENGTH_SHORT).show();

                    } else if (object.optString("status").equals("5")) {
                        PreferenceConnector.writeString(CheckOutPayment.this, PreferenceConnector.LoginStatus, "false");
                        startActivity(new Intent(CheckOutPayment.this, Splash.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP));
                        finish();
                    }


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

    public static String generateReferenceNumber() {
        // Get current date in DDMMYYYY format
        SimpleDateFormat dateFormat = new SimpleDateFormat("ddMMyyyy");
        String datePart = dateFormat.format(new Date());

        // Generate two random letters
        String lettersPart = generateRandomLetters(2);

        // Combine parts to create the reference number
        return "Ref" + datePart + lettersPart;
    }

    private static String generateRandomLetters(int length) {
        StringBuilder letters = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < length; i++) {
            // Generate a random letter between 'a' and 'z'
            char randomChar = (char) ('a' + random.nextInt(26));
            letters.append(randomChar);
        }
        return letters.toString();
    }


    private void GetProfile() {
        DataManager.getInstance().showProgressMessage(CheckOutPayment.this, getString(R.string.please_wait));
        Map<String, String> map = new HashMap<>();
        map.put("user_id", PreferenceConnector.readString(CheckOutPayment.this, PreferenceConnector.User_id, ""));
        map.put("register_id", PreferenceConnector.readString(CheckOutPayment.this, PreferenceConnector.Register_id, ""));
        map.put("country_id", PreferenceConnector.readString(CheckOutPayment.this, PreferenceConnector.countryId, ""));

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

                        if (data.getResult().getCountry().equals("79")) {
                            binding.rlMblMoney.setVisibility(View.VISIBLE);
                        } else {
                            binding.rlMblMoney.setVisibility(View.GONE);

                        }

                    } else if (data.status.equals("0")) {
                    } else if (data.status.equals("5")) {
                        PreferenceConnector.writeString(CheckOutPayment.this, PreferenceConnector.LoginStatus, "false");
                        startActivity(new Intent(CheckOutPayment.this, Splash.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP));
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


    private void dialogAlert() {
        AlertDialog.Builder builder = new AlertDialog.Builder(CheckOutPayment.this);
        builder.setMessage(getString(R.string.dear_customer)+"\n"
                       + getString(R.string.you_have_requested_delivery_with_cash_on_delivery_one_of_our)+ " \n"
                       + getString(R.string.salespeople_may_call_you) +"\n"
                       + getString(R.string.do_you_confirm_this_number) + data.getResult().mobile)
                .setCancelable(false)
                .setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                       if(NetworkAvailablity.checkNetworkStatus(CheckOutPayment.this)) PaymentAPI("VM", strList, data.getResult().mobile, "Cash");
                       else Toast.makeText(CheckOutPayment.this, getString(R.string.network_failure), Toast.LENGTH_SHORT).show();
                    }
                }).setNegativeButton(getString(R.string.no), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        dialogNumber("VM", "");
                    }
                });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }


    private void PaymentAPI(String operateur, String strList, String number, String paymentType) {
        //binding.loader.setVisibility(View.VISIBLE);
        DataManager.getInstance().showProgressMessage(CheckOutPayment.this, getString(R.string.please_wait));
        Map<String, String> headerMap = new HashMap<>();
        headerMap.put("Authorization", "Bearer " + PreferenceConnector.readString(CheckOutPayment.this, PreferenceConnector.access_token, ""));
        headerMap.put("Accept", "application/json");


        Map<String, String> map = new HashMap<>();
        map.put("user_id", PreferenceConnector.readString(CheckOutPayment.this, PreferenceConnector.User_id, ""));
        map.put("amount", /*"105"*/totalPriceToToPay);

        map.put("delivery_charge", deliveryCharge);
        map.put("platFormsFees", platFormsFees);
        map.put("taxN1", taxN1);
        map.put("taxN2", taxN2);
        map.put("operateur", operateur);
        map.put("cart_id", strList);
        if (operateur.equals("MC")) map.put("num_marchand", "060110217");
        else if (operateur.equals("AM")) map.put("num_marchand", "074272732");
        else if (operateur.equals("VM")) map.put("num_marchand", "074272732");
        else if (operateur.equals("")) map.put("num_marchand", "");
        map.put("type", "USER");
        map.put("user_number", number);
        map.put("register_id", PreferenceConnector.readString(CheckOutPayment.this, PreferenceConnector.Register_id, ""));
        map.put("address_id", PreferenceConnector.readString(CheckOutPayment.this, PreferenceConnector.ADDRESS_ID, ""));

        map.put("payment_type", paymentType);
        map.put("sub_orderdata", sendToServer);
        map.put("datetime", DataManager.getCurrent());
        map.put("self_collect", deliveryYesNo);
        map.put("delivery_type", orderType);


        Log.e("MapMap", "payment_params" + map);

        Call<ResponseBody> loginCall = apiInterface.payment(headerMap, map);
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
                        if (paymentType.equals("Wallet") || paymentType.equals("Cash")) {
                            Toast.makeText(CheckOutPayment.this, object.getString("message"), Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(CheckOutPayment.this, HomeActivity.class)
                                    .putExtra("status", "")
                                    .putExtra("msg", "")
                                    .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP));
                            finish();
                        } else {
                            PreferenceConnector.writeString(CheckOutPayment.this, PreferenceConnector.transId, object.getJSONObject("ressult").getString("reference"));
                            PreferenceConnector.writeString(CheckOutPayment.this, PreferenceConnector.serviceType, PreferenceConnector.Booking);
                            PreferenceConnector.writeString(CheckOutPayment.this, PreferenceConnector.ShareUserId, "");
                            PreferenceConnector.writeString(CheckOutPayment.this, PreferenceConnector.PaymentType, "Booking");
                            PreferenceConnector.writeString(CheckOutPayment.this, PreferenceConnector.PaymentOrderId,  object.getJSONObject("ressult").getString("order_id"));

                            startActivity(new Intent(CheckOutPayment.this, CheckPaymentStatusAct.class)
                                    .putExtra("paymentBy", "User"));

                        }
                    } else if (object.optString("status").equals("0")) {
                        //binding.loader.setVisibility(View.GONE);
                        Toast.makeText(CheckOutPayment.this, object.getString("message"), Toast.LENGTH_SHORT).show();
                    } else if (object.optString("status").equals("5")) {
                        PreferenceConnector.writeString(CheckOutPayment.this, PreferenceConnector.LoginStatus, "false");
                        startActivity(new Intent(CheckOutPayment.this, Splash.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP));
                        finish();
                    }


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


    public void dialogNumber(String operator, String strList) {
        Dialog mDialog = new Dialog(CheckOutPayment.this);
        mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mDialog.setContentView(R.layout.dialog_number);
        mDialog.setCancelable(false);
        mDialog.setCanceledOnTouchOutside(false);

        EditText edNumber = mDialog.findViewById(R.id.edNumber);
        AppCompatButton btnBack = mDialog.findViewById(R.id.btnBack);
        AppCompatButton btnPayNow = mDialog.findViewById(R.id.btnPayNow);
        CountryCodePicker ccp = mDialog.findViewById(R.id.ccp);


        btnBack.setOnClickListener(v -> {
            mDialog.dismiss();

        });

        btnPayNow.setOnClickListener(v -> {
            if (edNumber.getText().toString().equals(""))
                Toast.makeText(CheckOutPayment.this, getString(R.string.please_enter_number), Toast.LENGTH_SHORT).show();

            else {
                mDialog.dismiss();

                if(NetworkAvailablity.checkNetworkStatus(CheckOutPayment.this)) changeUserNumber("VM",edNumber.getText().toString(),ccp.getSelectedCountryCode());
                else Toast.makeText(CheckOutPayment.this, getString(R.string.network_failure), Toast.LENGTH_SHORT).show();




            }

        });
        mDialog.show();

    }


    public void dialogAirtel(String operator, String strList) {
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
            if (edNumber.getText().toString().equals(""))
                Toast.makeText(CheckOutPayment.this, getString(R.string.please_enter_number), Toast.LENGTH_SHORT).show();

            else {
                mDialog.dismiss();

                if(NetworkAvailablity.checkNetworkStatus(CheckOutPayment.this)) PaymentAPI(operator, cart_id_string, edNumber.getText().toString(), "Online");
                else Toast.makeText(CheckOutPayment.this, getString(R.string.network_failure), Toast.LENGTH_SHORT).show();
            }

        });
        mDialog.show();

    }

    public void dialogMoov(String operator, String strList) {
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
            if (edNumber.getText().toString().equals(""))
                Toast.makeText(CheckOutPayment.this, getString(R.string.please_enter_number), Toast.LENGTH_SHORT).show();

            else {
                mDialog.dismiss();

                if(NetworkAvailablity.checkNetworkStatus(CheckOutPayment.this)) PaymentAPI(operator, cart_id_string, edNumber.getText().toString(), "Online");
                else Toast.makeText(CheckOutPayment.this, getString(R.string.network_failure), Toast.LENGTH_SHORT).show();
            }

        });

        mDialog.show();
    }


    public void dialogSendAnotherPerson() {
        Dialog mDialog = new Dialog(CheckOutPayment.this);
        mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mDialog.setContentView(R.layout.dialog_send_another_person);
        mDialog.setCancelable(false);
        mDialog.setCanceledOnTouchOutside(false);

        EditText edNumber = mDialog.findViewById(R.id.edNumber);
        AppCompatButton btnCancel = mDialog.findViewById(R.id.btnCancel);
        AppCompatButton btnSend = mDialog.findViewById(R.id.btnSend);
        ccp = mDialog.findViewById(R.id.ccp);

        setCountryCodeFromLocation(ccp);



/*
        edNumber.addTextChangedListener(new TextWatcher() {
                                            @Override
                                            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                                            }

                                            @Override
                                            public void onTextChanged(CharSequence s, int start, int before, int count) {
                                                // Check if the input is numeric
                                                if (s.length()==8 */
        /*|| s.length()==10*//*
) {
                                                    checkUserExit(ccp.getSelectedCountryCode()+"-"+edNumber.getText().toString());
                                                }

                                            }

                                            @Override
                                            public void afterTextChanged(Editable s) {
                                            }
                                        });
*/


        btnCancel.setOnClickListener(v -> {
            mDialog.dismiss();

        });

        btnSend.setOnClickListener(v -> {
            if (edNumber.getText().toString().equals(""))
                Toast.makeText(CheckOutPayment.this, getString(R.string.enter_email_number), Toast.LENGTH_SHORT).show();

            else {
                mDialog.dismiss();
                //  if(!anotherPersonId.equalsIgnoreCase(""))
                String number ="";
                String type="";
                 if(isValidPhoneNumber(edNumber.getText().toString())){
                     number = ccp.getSelectedCountryCode() + "-" + edNumber.getText().toString();
                     type ="mobile";
                 }
                 else if(isValidEmail(edNumber.getText().toString())){
                     number =  edNumber.getText().toString();
                     type="email";
                 }

             if(NetworkAvailablity.checkNetworkStatus(CheckOutPayment.this))   checkUserExit(number,type);
             else Toast.makeText(CheckOutPayment.this, getString(R.string.network_failure), Toast.LENGTH_SHORT).show();


            }

        });
        mDialog.show();

    }

    private boolean isValidEmail(String email) {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }


    private boolean isValidPhoneNumber(String phoneNumber) {
        // Phone number validation using regex
        // Example pattern for a 10-digit phone number (adjust based on your requirements)
        return phoneNumber.matches("^(\\+\\d{1,3}[- ]?)?\\(?\\d{1,4}\\)?[- ]?\\d{1,4}[- ]?\\d{1,4}$");
    }


    private void checkUserExit(String number,String type) {
        //binding.loader.setVisibility(View.VISIBLE);
        DataManager.getInstance().showProgressMessage(CheckOutPayment.this, getString(R.string.please_wait));
        Map<String, String> headerMap = new HashMap<>();
        headerMap.put("Authorization", "Bearer " + PreferenceConnector.readString(CheckOutPayment.this, PreferenceConnector.access_token, ""));
        headerMap.put("Accept", "application/json");


        Map<String, String> map = new HashMap<>();
        map.put("user_id", PreferenceConnector.readString(CheckOutPayment.this, PreferenceConnector.User_id, ""));
        map.put("identity", number);
        map.put("type_by", type);


        Log.e("MapMap", "Check user Exit Request" + map);

        Call<ResponseBody> loginCall = apiInterface.checkUserExitApi(headerMap, map);
        loginCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                DataManager.getInstance().hideProgressMessage();
                // binding.loader.setVisibility(View.GONE);
                try {
                    String responseData = response.body() != null ? response.body().string() : "";
                    JSONObject object = new JSONObject(responseData);
                    Log.e(TAG, "Check user Exit RESPONSE" + object);
                    if (object.optString("status").equals("1")) {
                        anotherPersonId = object.getJSONObject("data").getString("id");
                        if(NetworkAvailablity.checkNetworkStatus(CheckOutPayment.this))  sendLinkAnotherPerson(anotherPersonId,number,type);
                        else Toast.makeText(CheckOutPayment.this, getString(R.string.network_failure), Toast.LENGTH_SHORT).show();
                    } else if (object.optString("status").equals("0")) {
                        //binding.loader.setVisibility(View.GONE);
                       // Toast.makeText(CheckOutPayment.this, object.getString("message"), Toast.LENGTH_SHORT).show();
                        dialogUserNotExit(number,type);


                    } else if (object.optString("status").equals("5")) {
                        PreferenceConnector.writeString(CheckOutPayment.this, PreferenceConnector.LoginStatus, "false");
                        startActivity(new Intent(CheckOutPayment.this, Splash.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP));
                        finish();
                    }


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


    public String createDeepLink(String paymentId) {
        return "https://www.afaycode.com/payment?paymentId="; /*+ paymentId;*/
    }



    private void dialogUserNotExit(String number, String type) {
        AlertDialog.Builder builder = new AlertDialog.Builder(CheckOutPayment.this);
        builder.setMessage(getString(R.string.this_user_is_not_yet_affiliated_with_afary_code_a_link_will_be_sent_to_him_by_sms_so_that_he_can_make_the_payment))
                .setCancelable(false)
                .setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        sendPaymentLinkAnotherPerson(number,type);
                    }
                }).setNegativeButton(getString(R.string.skip), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }



/*
    private void sharePaymentLink(String paymentId) {
        String link = createDeepLink(paymentId);
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, link);
        sendIntent.setType("text/plain");
        startActivity(Intent.createChooser(sendIntent, "Share payment link"));
    }
*/


    private void sendLinkAnotherPerson(String anotherPersonId,String number,String type) {
        //binding.loader.setVisibility(View.VISIBLE);
        DataManager.getInstance().showProgressMessage(CheckOutPayment.this, getString(R.string.please_wait));
        Map<String, String> headerMap = new HashMap<>();
        headerMap.put("Authorization", "Bearer " + PreferenceConnector.readString(CheckOutPayment.this, PreferenceConnector.access_token, ""));
        headerMap.put("Accept", "application/json");


        Map<String, String> map = new HashMap<>();
        map.put("user_id", PreferenceConnector.readString(CheckOutPayment.this, PreferenceConnector.User_id, ""));
        map.put("other_user_id", anotherPersonId);
        map.put("delivery_calculation", insertDeliveryId);
        map.put("payment_link", createDeepLink(""));
        map.put("type_by", type);
        map.put("value", number);

        Log.e("MapMap", "Send Invoice anotherPerson Request" + map);

        Call<ResponseBody> loginCall = apiInterface.sendInvoiceAnotherApi(headerMap, map);
        loginCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                DataManager.getInstance().hideProgressMessage();
                // binding.loader.setVisibility(View.GONE);
                try {
                    String responseData = response.body() != null ? response.body().string() : "";
                    JSONObject object = new JSONObject(responseData);
                    Log.e(TAG, "Send Invoice anotherPerson RESPONSE" + object);
                    if (object.optString("status").equals("1")) {
                        Toast.makeText(CheckOutPayment.this, getString(R.string.invoice_send_sucessfully), Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(CheckOutPayment.this, HomeActivity.class)
                                .putExtra("status", "")
                                .putExtra("msg", "").addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP));
                        finish();

                    } else if (object.optString("status").equals("0")) {
                        //binding.loader.setVisibility(View.GONE);
                        Toast.makeText(CheckOutPayment.this, object.getString("message"), Toast.LENGTH_SHORT).show();
                    } else if (object.optString("status").equals("5")) {
                        PreferenceConnector.writeString(CheckOutPayment.this, PreferenceConnector.LoginStatus, "false");
                        startActivity(new Intent(CheckOutPayment.this, Splash.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP));
                        finish();
                    }


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


    private void sendPaymentLinkAnotherPerson(String number,String type) {
        //binding.loader.setVisibility(View.VISIBLE);
        DataManager.getInstance().showProgressMessage(CheckOutPayment.this, getString(R.string.please_wait));
        Map<String, String> headerMap = new HashMap<>();
        headerMap.put("Authorization", "Bearer " + PreferenceConnector.readString(CheckOutPayment.this, PreferenceConnector.access_token, ""));
        headerMap.put("Accept", "application/json");


        Map<String, String> map = new HashMap<>();
        map.put("user_id", PreferenceConnector.readString(CheckOutPayment.this, PreferenceConnector.User_id, ""));
       // map.put("other_user_id", anotherPersonId);
        map.put("invoice_id", insertDeliveryId);
        map.put("type_by", type);
        map.put("value", number);

        Log.e("MapMap", "Send PaymentLink to anotherPerson Request" + map);

        Call<ResponseBody> loginCall = apiInterface.sendPaymentLinkToAnotherPersonApi( map);
        loginCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                DataManager.getInstance().hideProgressMessage();
                // binding.loader.setVisibility(View.GONE);
                try {
                    String responseData = response.body() != null ? response.body().string() : "";
                    JSONObject object = new JSONObject(responseData);
                    Log.e(TAG, "Send Payment anotherPerson RESPONSE" + object);
                    if (object.optString("status").equals("1")) {
                        Toast.makeText(CheckOutPayment.this, getString(R.string.invoice_send_sucessfully), Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(CheckOutPayment.this, HomeActivity.class)
                                .putExtra("status", "")
                                .putExtra("msg", "").addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP));
                        finish();

                    } else if (object.optString("status").equals("0")) {
                        //binding.loader.setVisibility(View.GONE);
                        Toast.makeText(CheckOutPayment.this, object.getString("message"), Toast.LENGTH_SHORT).show();
                    } else if (object.optString("status").equals("5")) {
                        PreferenceConnector.writeString(CheckOutPayment.this, PreferenceConnector.LoginStatus, "false");
                        startActivity(new Intent(CheckOutPayment.this, Splash.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP));
                        finish();
                    }


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






    private void setCountryCodeFromLocation(CountryCodePicker ccp) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        } else {
            try {
                FusedLocationProviderClient fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
                fusedLocationClient.getLastLocation().addOnSuccessListener(this, location -> {
                    if (location != null) {
                        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
                        try {
                            List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                            if (addresses != null && !addresses.isEmpty()) {
                                String countryCode = addresses.get(0).getCountryCode();
                                if (countryCode != null && !countryCode.isEmpty()) {
                                    Log.e("country code===", CountryCodes.getPhoneCode(countryCode) + "");
                                    ccp.setCountryForPhoneCode(/*getCountryPhoneCode(countryCode)*/
                                            CountryCodes.getPhoneCode(countryCode));
                                }
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                            Toast.makeText(this, "Error determining location", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                setCountryCodeFromLocation(ccp);
            }
        }
    }


    private void changeUserNumber(String operator, String number,String countryCode) {
        //binding.loader.setVisibility(View.VISIBLE);
        DataManager.getInstance().showProgressMessage(CheckOutPayment.this, getString(R.string.please_wait));
        Map<String, String> headerMap = new HashMap<>();
        headerMap.put("Authorization", "Bearer " + PreferenceConnector.readString(CheckOutPayment.this, PreferenceConnector.access_token, ""));
        headerMap.put("Accept", "application/json");


        Map<String, String> map = new HashMap<>();
        map.put("user_id", PreferenceConnector.readString(CheckOutPayment.this, PreferenceConnector.User_id, ""));
        map.put("mobile_number", number);
        map.put("country_code",countryCode);

        Log.e("MapMap", "Change user Number Request" + map);

        Call<ResponseBody> loginCall = apiInterface.changeUserNumberApi(headerMap, map);
        loginCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                DataManager.getInstance().hideProgressMessage();
                // binding.loader.setVisibility(View.GONE);
                try {
                    String responseData = response.body() != null ? response.body().string() : "";
                    JSONObject object = new JSONObject(responseData);
                    Log.e(TAG, "Change user Number RESPONSE" + object);
                    if (object.optString("status").equals("1")) {
                        if(NetworkAvailablity.checkNetworkStatus(CheckOutPayment.this)) PaymentAPI(operator, cart_id_string, number, "Cash");
                        else Toast.makeText(CheckOutPayment.this, getString(R.string.network_failure), Toast.LENGTH_SHORT).show();
                    } else if (object.optString("status").equals("0")) {
                        //binding.loader.setVisibility(View.GONE);
                        Toast.makeText(CheckOutPayment.this, object.getString("message"), Toast.LENGTH_SHORT).show();
                    } else if (object.optString("status").equals("5")) {
                        PreferenceConnector.writeString(CheckOutPayment.this, PreferenceConnector.LoginStatus, "false");
                        startActivity(new Intent(CheckOutPayment.this, Splash.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP));
                        finish();
                    }


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





}