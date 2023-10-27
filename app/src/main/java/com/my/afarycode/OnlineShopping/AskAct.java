package com.my.afarycode.OnlineShopping;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.my.afarycode.OnlineShopping.bottomsheet.AskBottomSheet;
import com.my.afarycode.OnlineShopping.listener.AskListener;
import com.my.afarycode.R;
import com.my.afarycode.databinding.ActivityAskBinding;


public class AskAct extends AppCompatActivity implements AskListener {
    ActivityAskBinding binding;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_ask);
        initViews();
    }

    private void initViews() {
        new AskBottomSheet().callBack(this::ask).show(getSupportFragmentManager(),"");

    }

    @Override
    public void ask(String value,String status) {
        if(value.equalsIgnoreCase("No")) {
            startActivity(new Intent(AskAct.this, WellcomeScreen.class));
            finish();
        } else {
            startActivity(new Intent(AskAct.this, LoginActivity.class)
                    .putExtra("type","Become a Customer"));
            finish();
        }
    }
}
