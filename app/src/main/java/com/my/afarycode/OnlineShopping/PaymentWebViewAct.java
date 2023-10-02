package com.my.afarycode.OnlineShopping;

import android.os.Bundle;
import android.util.Log;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.my.afarycode.R;
import com.my.afarycode.databinding.ActivityPaymentViewBinding;

public class PaymentWebViewAct extends AppCompatActivity {
    ActivityPaymentViewBinding binding;
    String url="";
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_payment_view);
        initViews();

    }

    private void initViews() {
        if (getIntent() != null) {
            url = getIntent().getStringExtra("url");

        }

        setUrlView();
    }

    private void setUrlView() {
        binding.webView.getSettings().setJavaScriptEnabled(true);
        binding.webView.getSettings().setLoadWithOverviewMode(true);
        binding.webView.getSettings().setDomStorageEnabled(true);
        binding.webView.getSettings().setBuiltInZoomControls(true);
        binding.webView.getSettings().setUseWideViewPort(true);
        binding.webView.setWebViewClient(new WebViewClient(){
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                Log.e("url===",url);
            }
        });
        binding.webView.loadUrl(url);
    }
}
