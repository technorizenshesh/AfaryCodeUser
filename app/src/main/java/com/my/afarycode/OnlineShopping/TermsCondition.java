package com.my.afarycode.OnlineShopping;

import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.text.Html;
import android.util.Log;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.RelativeLayout;

import com.my.afarycode.OnlineShopping.constant.PreferenceConnector;
import com.my.afarycode.OnlineShopping.helper.DataManager;
import com.my.afarycode.R;
import com.my.afarycode.databinding.ActivityPrivacyPolicyBinding;
import com.my.afarycode.databinding.ActivityTermsConditionBinding;
import com.my.afarycode.ratrofit.AfaryCode;
import com.my.afarycode.ratrofit.ApiClient;
import com.my.afarycode.ratrofit.Constant;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import ir.alirezabdn.wp7progress.WP10ProgressBar;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class TermsCondition extends AppCompatActivity {
    public String TAG = "TermsCondition";
    WebView TermsWV;
    String Url;
    RelativeLayout remember_layout, NavigationUpIM;
    boolean loadingFinished = true;
    boolean redirect = false;
    private WP10ProgressBar loader_page;
    private AfaryCode apiInterface;

    ActivityTermsConditionBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_terms_condition);
        apiInterface = ApiClient.getClient(TermsCondition.this).create(AfaryCode.class);
        loader_page = findViewById(R.id.loader_page);
        loader_page.showProgressBar();

        initComp();

        Url = Constant.TERMS_AND_CONDITIONS;
        TermsWV.getSettings().setLoadsImagesAutomatically(true);
        TermsWV.getSettings().setJavaScriptEnabled(true);
        TermsWV.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        TermsWV.setWebViewClient(new HelloWebViewClient());
        TermsWV.getSettings().setDomStorageEnabled(true);
        TermsWV.getSettings().setAppCacheEnabled(true);
        TermsWV.getSettings().setLoadsImagesAutomatically(true);
        TermsWV.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
        TermsWV.getSettings().setRenderPriority(WebSettings.RenderPriority.HIGH);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            TermsWV.getSettings().setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
            //TermsWV.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        }

        TermsWV.loadUrl(Url);
        TermsWV.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String urlNewString) {
                if (!loadingFinished) {
                    redirect = true;
                }

                loadingFinished = false;
                view.loadUrl(urlNewString);
                loader_page.showProgressBar();
                return true;
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap facIcon) {
                loadingFinished = false;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                if (!redirect) {
                    loadingFinished = true;
                }

                if (loadingFinished && !redirect) {
                    //remember_layout.setVisibility(View.VISIBLE);
                    loader_page.hideProgressBar();
                    //HIDE LOADING IT HAS FINISHED
                } else {
                    redirect = false;
                }

            }
        });
        getTermsConditions();

    }

    @Override
    public void onBackPressed() {
        finish();
    }

    private void initComp() {

        loader_page = findViewById(R.id.loader_page);
        TermsWV = findViewById(R.id.TermsWV);
        NavigationUpIM = findViewById(R.id.NavigationUpIM);

        NavigationUpIM.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private class HelloWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }
    }

    public void getTermsConditions() {
        DataManager.getInstance().showProgressMessage(TermsCondition.this, "Please wait...");
        Call<ResponseBody> loginCall = apiInterface.getTermsConditions();

        loginCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call,
                                   Response<ResponseBody> response) {
                DataManager.getInstance().hideProgressMessage();

                try {
                    String responseString = response.body().string();
                    JSONObject jsonObject = new JSONObject(responseString);
                    Log.e(TAG,"get terms conditions Response = " + responseString);
                    if(jsonObject.getString("status").equals("1")) {
                        JSONObject jsonObject11 = jsonObject.getJSONObject("result");
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                            binding.tvDetails.setText(Html.fromHtml(jsonObject11.getString("details"), Html.FROM_HTML_MODE_COMPACT));
                        } else {
                            binding.tvDetails.setText(Html.fromHtml(jsonObject11.getString("details")));
                        }
                    } else {
                        // binding.tvNotFound.setVisibility(View.VISIBLE);

                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                call.cancel();
                DataManager.getInstance().hideProgressMessage();
            }
        });

    }


}
