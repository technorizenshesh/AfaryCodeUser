package com.my.afarycode.OnlineShopping;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.my.afarycode.OnlineShopping.constant.PreferenceConnector;
import com.my.afarycode.OnlineShopping.deeplink.PaymentByAnotherAct;
import com.my.afarycode.R;
import com.my.afarycode.databinding.ActivityPaymentViewBinding;

public class PaymentWebViewAct extends AppCompatActivity {
    ActivityPaymentViewBinding binding;
    String url="",ref="";
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_payment_view);
        initViews();

    }

    private void initViews() {
        if (getIntent() != null) {
            url = getIntent().getStringExtra("url");
            ref = getIntent().getStringExtra("ref");

        }

        setUrlView();
    }

    private void setUrlView() {
        binding.webView.getSettings().setJavaScriptEnabled(true);
        binding.webView.getSettings().setLoadWithOverviewMode(true);
        binding.webView.getSettings().setDomStorageEnabled(true);
        binding.webView.getSettings().setBuiltInZoomControls(true);
        binding.webView.getSettings().setUseWideViewPort(true);


        binding.webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                // Check for success or failure URL patterns
                String url = request.getUrl().toString();
                Log.e("url===",url);
                if (url.contains("payment_success")) {
                    // Handle payment success

                 if(PreferenceConnector.readString(PaymentWebViewAct.this,PreferenceConnector.PaymentType,"").equals("Booking")) {
                     startActivity(new Intent(PaymentWebViewAct.this, CheckPaymentStatusAct.class)
                             .putExtra("paymentBy", "User"));
                     finish();
                 }
                 else if(PreferenceConnector.readString(PaymentWebViewAct.this,PreferenceConnector.PaymentType,"").equals("InvoiceWallet")){
                     startActivity(new Intent(PaymentWebViewAct.this, CheckPaymentStatusAct.class)
                             .putExtra("paymentBy", "Transfer"));
                     finish();
                 }

                 else {
                     startActivity(new Intent(PaymentWebViewAct.this, CheckPaymentStatusAct.class)
                             .putExtra("paymentBy","anotherUser"));
                     finish();
                 }
                    return true; // Don't load the URL
                } else if (url.contains("payment_failure")) {
                    // Handle payment failure
                    Toast.makeText(PaymentWebViewAct.this, "Payment Failed!", Toast.LENGTH_SHORT).show();
                    finish();
                    return true;
                }

                return false; // Let the WebView handle other URLs
            }
        });



       /* binding.webView.setWebViewClient(new WebViewClient(){
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                Log.e("url===",url);
            }
        });*/
        binding.webView.loadUrl(url);
    }
}
