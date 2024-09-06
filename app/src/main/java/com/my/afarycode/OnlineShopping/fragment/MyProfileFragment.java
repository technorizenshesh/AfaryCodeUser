package com.my.afarycode.OnlineShopping.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.my.afarycode.OnlineShopping.ChangePassword;
import com.my.afarycode.OnlineShopping.OrderHistoryScreen;
import com.my.afarycode.OnlineShopping.helper.DataManager;
import com.my.afarycode.OnlineShopping.myorder.MyOrderScreen;
import com.my.afarycode.OnlineShopping.PrivacyPolicy;
import com.my.afarycode.OnlineShopping.TermsCondition;
import com.my.afarycode.OnlineShopping.UpdateProfile;
import com.my.afarycode.OnlineShopping.WishListActivity;
import com.my.afarycode.OnlineShopping.constant.PreferenceConnector;
import com.my.afarycode.R;
import com.my.afarycode.Splash;
import com.my.afarycode.databinding.FragmentMyprofileBinding;
import com.my.afarycode.ratrofit.AfaryCode;
import com.my.afarycode.ratrofit.ApiClient;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class MyProfileFragment extends Fragment {
public String TAG ="MyProfileFragment";
    FragmentMyprofileBinding binding;
    Fragment fragment;



    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_myprofile, container, false);


        showLang(PreferenceConnector.readString(requireActivity(), PreferenceConnector.LANGUAGE, ""));

        binding.RRback.setOnClickListener(v -> {
            getFragmentManager().popBackStack();
        });


        binding.txtMyOrder.setOnClickListener(v -> {
            startActivity(new Intent(getActivity(), OrderHistoryScreen.class));
        });


        binding.RRChangeLanguage.setOnClickListener(v -> {
            showLanguageChangeDialog();
        });






        binding.RRLogout.setOnClickListener(v -> {
          //  Logout(PreferenceConnector.readString(getActivity(),PreferenceConnector.User_id,""),getActivity());
            showLogoutDialog();
        });

        binding.txtWishList.setOnClickListener(v -> {

            fragment = new WishListActivity();
            loadFragment(fragment);


        });

        binding.txtChangePassword.setOnClickListener(v -> {
            fragment = new ChangePassword();
            loadFragment(fragment);

        });

        binding.txtUpdate.setOnClickListener(v -> {
            fragment = new UpdateProfile();
            loadFragment(fragment);

        });


        binding.txtOnlineOrder.setOnClickListener(v -> {
            startActivity(new Intent(getActivity(), MyOrderScreen.class));

        });


        binding.txtPrivacy.setOnClickListener(v -> {
            startActivity(new Intent(getActivity(), PrivacyPolicy.class));

        });




        binding.txtTerms.setOnClickListener(v -> {
            startActivity(new Intent(getActivity(), TermsCondition.class));
        });


        return binding.getRoot();

    }

    private void showLang(String language) {
        if(language.equalsIgnoreCase("en"))
        binding.txtLang.setText(getString(R.string.english));
        else if(language.equalsIgnoreCase("fr")) binding.txtLang.setText(getString(R.string.french));
        else binding.txtLang.setText(getString(R.string.english));

    }


    public boolean loadFragment(Fragment fragment) {
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        if (fragment != null) {
            fragmentManager.beginTransaction()
                    .addToBackStack("Home")
                    .replace(R.id.fragment_homeContainer, fragment)//, tag)
                    .commit();
            return true;
        }
        return false;
    }


    private void showLanguageChangeDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
        builder.setTitle(R.string.change_language)
                .setItems(new CharSequence[]{
                        getString(R.string.english),
                        getString(R.string.french)
                }, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                // English
                                changeLocale("en");
                                break;
                            case 1:
                                // French
                                changeLocale("fr");
                                break;
                        }
                    }
                });
        builder.create().show();
    }


    private void changeLocale(String en) {
        updateResources(requireActivity(),en);
        PreferenceConnector.writeString(requireActivity(), PreferenceConnector.LANGUAGE, en);
        updateLanguage(PreferenceConnector.readString(getActivity(),PreferenceConnector.User_id,""),en,requireActivity());

    }


    private void updateResources(Context wellcomeScreen, String en) {
        Locale locale = new Locale(en);
        Locale.setDefault(locale);
        Resources resources = wellcomeScreen.getResources();
        Configuration configuration = resources.getConfiguration();
        configuration.locale = locale;
        resources.updateConfiguration(configuration, resources.getDisplayMetrics());

    }


    public  void updateLanguage(String id,String language, Context context) {
        DataManager.getInstance().showProgressMessage(getActivity(),getString(R.string.please_wait));
        AfaryCode apiInterface = ApiClient.getClient(context.getApplicationContext()).create(AfaryCode.class);
        Map<String, String> map = new HashMap<>();
        map.put("user_id",id);
        map.put("language",language);
        Log.e(TAG,"Update Language Request "+map);
        Call<ResponseBody> loginCall = apiInterface.updateLanguageApi(map);
        loginCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                DataManager.getInstance().hideProgressMessage();
                try {
                    String stringResponse = response.body().string();
                    JSONObject jsonObject = new JSONObject(stringResponse);
                    if(jsonObject.getString("status").equals("1")){
                        showLang(language);
                        Intent intent = new Intent(requireActivity(), Splash.class);
                        startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK |
                                Intent.FLAG_ACTIVITY_NEW_TASK));
                    }
                    else if(jsonObject.getString("status").equals("0")){
                        //App.showToast(context,"data not available", Toast.LENGTH_SHORT);
                    }
                    else if (jsonObject.getString("status").equals("5")) {
                        logttt();
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

    public  void Logout(String id, Context context) {
        DataManager.getInstance().showProgressMessage(getActivity(),getString(R.string.please_wait));

        AfaryCode apiInterface = ApiClient.getClient(context.getApplicationContext()).create(AfaryCode.class);
        Map<String, String> map = new HashMap<>();
        map.put("user_id",id);
        map.put("register_id", PreferenceConnector.readString(getActivity(), PreferenceConnector.Register_id, ""));
        Log.e(TAG,"User Logout Request "+map);
        Call<ResponseBody> loginCall = apiInterface.logoutApi(map);
        loginCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                DataManager.getInstance().hideProgressMessage();
                try {
                    String stringResponse = response.body().string();
                    JSONObject jsonObject = new JSONObject(stringResponse);

                    if(jsonObject.getString("status").equals("1")){
                        logttt();
                    }
                    else if(jsonObject.getString("status").equals("0")){
                        //App.showToast(context,"data not available", Toast.LENGTH_SHORT);
                    }
                    else if (jsonObject.getString("status").equals("5")) {
                        logttt();
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

    private  void logttt() {

    }


    private void showLogoutDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
        builder.setTitle(getString(R.string.logout))
                .setMessage(getString(R.string.are_you_sure_logout))
                .setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        PreferenceConnector.writeString(getActivity(), PreferenceConnector.LoginStatus, "false");
                        startActivity(new Intent(getActivity(), Splash.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP));
                        getActivity().finish();
                    }
                })
                .setNegativeButton(getString(R.string.no), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Handle cancel action
                        dialog.dismiss();
                    }
                });

        AlertDialog dialog = builder.create();
        dialog.show();
    }



}