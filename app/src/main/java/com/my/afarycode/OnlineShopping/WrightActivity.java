package com.my.afarycode.OnlineShopping;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;
import com.my.afarycode.OnlineShopping.Model.ReviewModal;
import com.my.afarycode.OnlineShopping.Model.SignupModel;
import com.my.afarycode.OnlineShopping.activity.CheckOutDeliveryAct;
import com.my.afarycode.OnlineShopping.constant.PreferenceConnector;
import com.my.afarycode.OnlineShopping.helper.DataManager;
import com.my.afarycode.R;
import com.my.afarycode.Splash;
import com.my.afarycode.databinding.ActivityWrightBinding;
import com.my.afarycode.ratrofit.AfaryCode;
import com.my.afarycode.ratrofit.ApiClient;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class WrightActivity extends AppCompatActivity {

    ActivityWrightBinding binding;
    private String product_id;
    private String restaurant_id;
    private AfaryCode apiInterface;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_wright);
        apiInterface = ApiClient.getClient(this).create(AfaryCode.class);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();

        if (bundle != null) {

            product_id = bundle.getString("product_id");
            restaurant_id = bundle.getString("restaurant_id");
        }

        SetupUI();

        binding.RRback.setOnClickListener(v -> {
            onBackPressed();
        });

        binding.txtSKip.setOnClickListener(v -> {
            finish();
        });
    }

    private void SetupUI() {
        binding.addReview.setOnClickListener(v -> {
            if (binding.addComments.getText().toString().trim().isEmpty()) {
                binding.addComments.setError("Field cannot be empty");

                Toast.makeText(WrightActivity.this, "please add comments!!!!", Toast.LENGTH_SHORT).show();

            } else {
                AddReviewAPi();
            }
        });

    }

    private void AddReviewAPi() {

        DataManager.getInstance().showProgressMessage(WrightActivity.this, "Please wait...");
        Map<String,String> headerMap = new HashMap<>();
        headerMap.put("Authorization","Bearer " +PreferenceConnector.readString(WrightActivity.this, PreferenceConnector.access_token,""));
        headerMap.put("Accept","application/json");
        Map<String, String> map = new HashMap<>();
        map.put("pro_id", product_id);
        map.put("rate", "2");
        map.put("comment", binding.addComments.getText().toString());
        map.put("user_id", PreferenceConnector.readString(this, PreferenceConnector.User_id, ""));
        map.put("restaurant_id", restaurant_id);
        map.put("register_id", PreferenceConnector.readString(WrightActivity.this, PreferenceConnector.Register_id, ""));

        Log.e("MapMap", "Review REQUEST" + map);
        Call<ReviewModal> reviewModalCall = apiInterface.add_rating(headerMap,map);

        reviewModalCall.enqueue(new Callback<ReviewModal>() {
            @Override
            public void onResponse(Call<ReviewModal> call, Response<ReviewModal> response) {
                DataManager.getInstance().hideProgressMessage();

                try {
                    ReviewModal data = response.body();
                    String dataResponse = new Gson().toJson(response.body());
                    Log.e("MapMap", "LOGIN RESPONSE" + dataResponse);

                    if (data.status.equals("1")) {

                        Toast.makeText(WrightActivity.this, "SuccessFully Add Your Review !",
                                Toast.LENGTH_SHORT).show();
                        finish();

                    } else if (data.status.equals("0")) {
                        Toast.makeText(WrightActivity.this, data.message, Toast.LENGTH_SHORT).show();
                    }

                    else if (data.status.equals("5")) {
                        PreferenceConnector.writeString(WrightActivity.this, PreferenceConnector.LoginStatus, "false");
                        startActivity(new Intent(WrightActivity.this, Splash.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP));
                        finish();
                    }



                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ReviewModal> call, Throwable t) {
                call.cancel();
                DataManager.getInstance().hideProgressMessage();
            }
        });

    }
}