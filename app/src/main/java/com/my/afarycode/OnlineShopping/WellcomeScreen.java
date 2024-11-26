package com.my.afarycode.OnlineShopping;

import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.google.gson.Gson;
import com.my.afarycode.OnlineShopping.Model.CountryModelNew;
import com.my.afarycode.OnlineShopping.Model.SliderData;
import com.my.afarycode.OnlineShopping.adapter.SliderAdapterExample;
import com.my.afarycode.OnlineShopping.adapter.SliderTextAdapter;
import com.my.afarycode.OnlineShopping.constant.PreferenceConnector;
import com.my.afarycode.OnlineShopping.helper.DataManager;
import com.my.afarycode.R;
import com.my.afarycode.databinding.ActivityWellcomeScreenBinding;
import com.my.afarycode.ratrofit.AfaryCode;
import com.my.afarycode.ratrofit.ApiClient;
import com.my.afarycode.ratrofit.Constant;
import com.smarteist.autoimageslider.IndicatorView.animation.type.IndicatorAnimationType;
import com.smarteist.autoimageslider.SliderAnimations;
import com.smarteist.autoimageslider.SliderView;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class WellcomeScreen extends AppCompatActivity {

    ActivityWellcomeScreenBinding binding;
    private Object item;
    private AfaryCode apiInterface;
    ArrayList<SliderData.Result>arrayList =new ArrayList<>();
    String lang="";
    SliderTextAdapter adapter1;
    ArrayList<CountryModelNew> country = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_wellcome_screen);
        apiInterface = ApiClient.getClient(WellcomeScreen.this).create(AfaryCode.class);

        country.add(new CountryModelNew("1","English","Anglais"));
        country.add(new CountryModelNew("2","French","Français"));

        binding.RRSignIn.setOnClickListener(v -> {

            startActivity(new Intent(WellcomeScreen.this, LoginActivity.class) .putExtra("type",""));
            finish();

        });

        binding.RRSignUp.setOnClickListener(v -> {
            startActivity(new Intent(WellcomeScreen.this, SignUpActivity.class).putExtra("lang",lang));
           finish();
        });


        binding.tvLanguage.setOnClickListener(v -> {
            showDropLanguage(v,binding.tvLanguage,country);
        });

        getDescriptionTitle();
    }

    private void updateResources(WellcomeScreen wellcomeScreen, String en) {
        Locale locale = new Locale(en);
        Locale.setDefault(locale);
        Resources resources = wellcomeScreen.getResources();
        Configuration configuration = resources.getConfiguration();
        configuration.locale = locale;
        resources.updateConfiguration(configuration, resources.getDisplayMetrics());
        PreferenceConnector.writeString(WellcomeScreen.this, PreferenceConnector.LANGUAGE, en);
        adapter1.notifyDataSetChanged();
        Log.e("changalalal","=====" + "  " + en);
    }



    public void getDescriptionTitle() {
        DataManager.getInstance().showProgressMessage(WellcomeScreen.this, getString(R.string.please_wait));
        Call<ResponseBody> loginCall = apiInterface.getTitleDes();

        loginCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call,
                                   Response<ResponseBody> response) {
                DataManager.getInstance().hideProgressMessage();

                try {
                    String responseString = response.body().string();
                    JSONObject jsonObject = new JSONObject(responseString);
                    Log.e("WellcomeScreen","get title description Response = " + responseString);
                    if(jsonObject.getString("status").equals("1")) {
                        SliderData titleDesModel = new Gson().fromJson(responseString,SliderData.class);
                        arrayList.clear();
                        arrayList.addAll(titleDesModel.getResult());

                        adapter1 = new SliderTextAdapter(WellcomeScreen.this, arrayList);
                        binding.imageSlider.setSliderAdapter(adapter1);
                        binding.imageSlider.setIndicatorAnimation(IndicatorAnimationType.WORM); //set indicator animation by using SliderLayout.IndicatorAnimations. :WORM or THIN_WORM or COLOR or DROP or FILL or NONE or SCALE or SCALE_DOWN or SLIDE and SWAP!!
                        binding.imageSlider.setSliderTransformAnimation(SliderAnimations.SIMPLETRANSFORMATION);
                        binding.imageSlider.setAutoCycleDirection(SliderView.AUTO_CYCLE_DIRECTION_BACK_AND_FORTH);
                        //     binding.imageSlider.setIndicatorSelectedColor(R.color.colorPrimary);
                        //      binding.imageSlider.setIndicatorUnselectedColor(Color.GRAY);
                        binding.imageSlider.setScrollTimeInSec(3);
                        binding.imageSlider.setAutoCycle(true);
                        binding.imageSlider.startAutoCycle();

                        lang = "en";
                        binding.tvLanguage.setText(country.get(0).getName());

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



    private void showDropLanguage(View v, TextView textView, List<CountryModelNew> stringList) {
        PopupMenu popupMenu = new PopupMenu(WellcomeScreen.this, v);
        for (int i = 0; i < stringList.size(); i++) {
            popupMenu.getMenu().add(stringList.get(i).getName());
        }


        popupMenu.setOnMenuItemClickListener(menuItem -> {
            //   textView.setText(menuItem.getTitle());
            for (int i = 0; i < stringList.size(); i++) {
                if (stringList.get(i).getName().equalsIgnoreCase(menuItem.getTitle().toString())) {
                    if(stringList.get(i).getId().equalsIgnoreCase("1")) {
                        lang = "en"; //stringList.get(i).getName();
                        updateResources(WellcomeScreen.this, "en");
                        textView.setText(stringList.get(i).getName());
                        binding.tvText.setText("Next");

                    }
                    else {
                        lang = "fr";
                        textView.setText(stringList.get(i).getNameFr());
                        updateResources(WellcomeScreen.this, "fr");
                        binding.tvText.setText("Suivant");

                    }
                }
            }
            return true;
        });
        popupMenu.show();
    }



}