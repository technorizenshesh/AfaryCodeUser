package com.my.afarycode.OnlineShopping;

import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.my.afarycode.OnlineShopping.Model.SliderData;
import com.my.afarycode.OnlineShopping.adapter.SliderAdapterExample;
import com.my.afarycode.OnlineShopping.adapter.SliderTextAdapter;
import com.my.afarycode.R;
import com.my.afarycode.databinding.ActivityWellcomeScreenBinding;
import com.smarteist.autoimageslider.IndicatorView.animation.type.IndicatorAnimationType;
import com.smarteist.autoimageslider.SliderAnimations;
import com.smarteist.autoimageslider.SliderView;

import java.util.ArrayList;
import java.util.Locale;

public class WellcomeScreen extends AppCompatActivity {

    ActivityWellcomeScreenBinding binding;
    private Object item;

    ArrayList<SliderData>arrayList =new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_wellcome_screen);

        String[] country = {"English", "Français"};

        arrayList.add(new SliderData(getString(R.string.book_your_nearby_provider),"Lorem ipsum dolor sit amet, consectetuer adipiscing elit. Aenean commodo ligula eget dolor. Aenean massa. Cum sociis natoque penatibus et magnis dis parturient"));
        arrayList.add(new SliderData(getString(R.string.book_your_nearby_provider),"Lorem ipsum dolor sit amet, consectetuer adipiscing elit. Aenean commodo ligula eget dolor. Aenean massa. Cum sociis natoque penatibus et magnis dis parturient"));
        arrayList.add(new SliderData(getString(R.string.book_your_nearby_provider),"Lorem ipsum dolor sit amet, consectetuer adipiscing elit. Aenean commodo ligula eget dolor. Aenean massa. Cum sociis natoque penatibus et magnis dis parturient"));
        arrayList.add(new SliderData(getString(R.string.book_your_nearby_provider),"Lorem ipsum dolor sit amet, consectetuer adipiscing elit. Aenean commodo ligula eget dolor. Aenean massa. Cum sociis natoque penatibus et magnis dis parturient"));

        ArrayAdapter aa = new ArrayAdapter(this, android.R.layout.simple_spinner_item, country);
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.langougeChange.setAdapter(aa);


        binding.langougeChange.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                item = parent.getSelectedItem();
                if (item.equals("English")) {

                    updateResources(WellcomeScreen.this, "en");


                } else if (item.equals("Français")) {
                    updateResources(WellcomeScreen.this, "fr");
                }

            }

            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        binding.RRSignIn.setOnClickListener(v -> {

            startActivity(new Intent(WellcomeScreen.this, LoginActivity.class) .putExtra("type",""));
            finish();

        });

        binding.RRSignUp.setOnClickListener(v -> {
            startActivity(new Intent(WellcomeScreen.this, SignUpActivity.class));
           finish();
        });


      SliderTextAdapter adapter1 = new SliderTextAdapter(WellcomeScreen.this, arrayList);
        binding.imageSlider.setSliderAdapter(adapter1);
        binding.imageSlider.setIndicatorAnimation(IndicatorAnimationType.WORM); //set indicator animation by using SliderLayout.IndicatorAnimations. :WORM or THIN_WORM or COLOR or DROP or FILL or NONE or SCALE or SCALE_DOWN or SLIDE and SWAP!!
        binding.imageSlider.setSliderTransformAnimation(SliderAnimations.SIMPLETRANSFORMATION);
        binding.imageSlider.setAutoCycleDirection(SliderView.AUTO_CYCLE_DIRECTION_BACK_AND_FORTH);
        //     binding.imageSlider.setIndicatorSelectedColor(R.color.colorPrimary);
        //      binding.imageSlider.setIndicatorUnselectedColor(Color.GRAY);
        binding.imageSlider.setScrollTimeInSec(3);
        binding.imageSlider.setAutoCycle(true);
        binding.imageSlider.startAutoCycle();


    }

    private void updateResources(WellcomeScreen wellcomeScreen, String en) {
        Locale locale = new Locale(en);
        Locale.setDefault(locale);
        Resources resources = wellcomeScreen.getResources();
        Configuration configuration = resources.getConfiguration();
        configuration.locale = locale;
        resources.updateConfiguration(configuration, resources.getDisplayMetrics());

    }
}