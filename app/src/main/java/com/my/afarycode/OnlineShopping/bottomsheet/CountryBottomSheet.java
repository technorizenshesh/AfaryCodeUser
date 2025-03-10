package com.my.afarycode.OnlineShopping.bottomsheet;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.widget.NestedScrollView;
import androidx.databinding.DataBindingUtil;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.gson.Gson;
import com.my.afarycode.OnlineShopping.Model.CountryModel;
import com.my.afarycode.OnlineShopping.Model.SearchModel;
import com.my.afarycode.OnlineShopping.activity.CardAct;
import com.my.afarycode.OnlineShopping.activity.CheckOutDeliveryAct;
import com.my.afarycode.OnlineShopping.adapter.CountryAdapter;
import com.my.afarycode.OnlineShopping.constant.PreferenceConnector;
import com.my.afarycode.OnlineShopping.helper.DataManager;
import com.my.afarycode.OnlineShopping.helper.NetworkAvailablity;
import com.my.afarycode.OnlineShopping.listener.SearchListener;
import com.my.afarycode.OnlineShopping.listener.onPositionClickListener;
import com.my.afarycode.R;
import com.my.afarycode.Splash;
import com.my.afarycode.databinding.FragmentCountrySheetBinding;
import com.my.afarycode.ratrofit.AfaryCode;
import com.my.afarycode.ratrofit.ApiClient;

import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CountryBottomSheet extends BottomSheetDialogFragment implements onPositionClickListener {

    public String TAG = "CountryBottomSheet";
    BottomSheetDialog dialog;
    FragmentCountrySheetBinding binding;
    AfaryCode apiInterface;
    private BottomSheetBehavior<View> mBehavior;
    public SearchListener listener;

    CountryAdapter adapter;

    ArrayList<CountryModel.Result> arrayList;
    String countryName = "";

    public static TextView tvNotFound;


    public CountryBottomSheet(String countryName) {
        this.countryName = countryName;
    }


    public CountryBottomSheet callBack(SearchListener listener) {
        this.listener = listener;
        return this;
    }


    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        dialog = (BottomSheetDialog) super.onCreateDialog(savedInstanceState);
        binding = DataBindingUtil.inflate(LayoutInflater.from(getActivity()), R.layout.fragment_country_sheet, null, false);
        dialog.setContentView(binding.getRoot());
        mBehavior = BottomSheetBehavior.from((View) binding.getRoot().getParent());
        mBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        apiInterface = ApiClient.getClient(getActivity()).create(AfaryCode.class);
        initBinding();


        binding.getRoot().getViewTreeObserver().addOnPreDrawListener(() -> {
            Rect r = new Rect();
            binding.getRoot().getWindowVisibleDisplayFrame(r);
            int screenHeight = binding.getRoot().getHeight();
            int keypadHeight = screenHeight - r.bottom;

            if (keypadHeight > 100) {
                // Keyboard is visible, adjust BottomSheet if necessary
                mBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);  // Optional: Collapse BottomSheet when keyboard shows
            } else {
                // Keyboard is hidden, set to expanded
                mBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
            }
            return true;
        });




        return dialog;
    }


    private boolean isKeyboardVisible(View rootView) {
        Rect rect = new Rect();
        rootView.getWindowVisibleDisplayFrame(rect);
        int screenHeight = rootView.getHeight();
        int keypadHeight = screenHeight - rect.bottom;
        return keypadHeight > screenHeight * 0.15; // Adjust based on your preference
    }

    // Method to manually show the keyboard
    public void showKeyboard(View view) {
        InputMethodManager imm = (InputMethodManager) requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT);
    }

    // Method to manually hide the keyboard
    public void hideKeyboard(View view) {
        InputMethodManager imm = (InputMethodManager) requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }





    private void initBinding() {

        arrayList = new ArrayList<>();

        tvNotFound = binding.tvNotFound;

        binding.tvLocation.setText(countryName);

        adapter = new CountryAdapter(getActivity(), arrayList, CountryBottomSheet.this);
        binding.rvServices.setAdapter(adapter);

        binding.ivBack.setOnClickListener(v -> dismiss());

        if (NetworkAvailablity.checkNetworkStatus(requireActivity())) getAllCountry();
        else
            Toast.makeText(requireActivity(), getString(R.string.network_failure), Toast.LENGTH_SHORT).show();


        binding.edSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence query, int start, int before, int count) {
                getFilterSearch(query.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }


    private void getAllCountry() {
        DataManager.getInstance().showProgressMessage(getActivity(), getString(R.string.please_wait));
        Map<String, String> headerMap = new HashMap<>();
        headerMap.put("Authorization", "Bearer " + PreferenceConnector.readString(getActivity(), PreferenceConnector.access_token, ""));
        headerMap.put("Accept", "application/json");
        Call<ResponseBody> chatCount = apiInterface.getAllCountry(headerMap);
        chatCount.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                DataManager.getInstance().hideProgressMessage();
                try {
                    String responseData = response.body() != null ? response.body().string() : "";
                    JSONObject object = new JSONObject(responseData);
                    Log.e(TAG, "Get All Country RESPONSE" + object);

                    if (object.optString("status").equals("1")) {
                        CountryModel data = new Gson().fromJson(responseData, CountryModel.class);
                        binding.tvNotFound.setVisibility(View.GONE);
                        arrayList.clear();
                        arrayList.addAll(data.getResult());
                        adapter.notifyDataSetChanged();

                    } else if (object.optString("status").equals("0")) {
                        binding.tvNotFound.setVisibility(View.VISIBLE);
                        arrayList.clear();
                        adapter.notifyDataSetChanged();

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

    @Override
    public void onPosition(int position, String name, String countryId) {
        // countryName = name
        dialogAddLocation(countryName, name, countryId);
    }

    private void dialogAddLocation(String countryName, String countryNameNew, String countryId) {
        Dialog mDialog = new Dialog(getActivity());
        mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mDialog.setContentView(R.layout.dialog_set_country);
        // mDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        TextView textView = mDialog.findViewById(R.id.tvTitle);
        TextView tvYes = mDialog.findViewById(R.id.tvYes);
        TextView tvBack = mDialog.findViewById(R.id.tvBack);


        textView.setText("Your current shopping location is " + countryName + "." + "Would you like to change it to " + countryNameNew + "?");
        WindowManager.LayoutParams lp = mDialog.getWindow().getAttributes();
        lp.dimAmount = 0.8f;
        mDialog.getWindow().setAttributes(lp);
        mDialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        mDialog.setCancelable(false);
        mDialog.setCanceledOnTouchOutside(false);


        tvBack.setOnClickListener(v -> {
            mDialog.dismiss();
            dialog.dismiss();

        });

        tvYes.setOnClickListener(v -> {
            mDialog.dismiss();

            if (NetworkAvailablity.checkNetworkStatus(requireActivity()))
                updateCountry(countryId, countryNameNew);
            else
                Toast.makeText(requireActivity(), getString(R.string.network_failure), Toast.LENGTH_SHORT).show();

        });


        mDialog.show();

    }


    public void getFilterSearch(String query) {
        try {
            query = query.toLowerCase();

            final ArrayList<CountryModel.Result> filteredList = new ArrayList<CountryModel.Result>();

            if (arrayList != null) {
                for (int i = 0; i < arrayList.size(); i++) {
                    String text = arrayList.get(i).getName().toLowerCase();
                    if (text.contains(query)) {
                        filteredList.add(arrayList.get(i));
                    }

                }
                adapter.filterList(filteredList);
                hideKeyboard();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void updateCountry(String countryId, String country) {
        DataManager.getInstance().showProgressMessage(getActivity(), getString(R.string.please_wait));
        Map<String, String> headerMap = new HashMap<>();
        headerMap.put("Authorization", "Bearer " + PreferenceConnector.readString(getActivity(), PreferenceConnector.access_token, ""));
        headerMap.put("Accept", "application/json");

        Map<String, String> map = new HashMap<>();
        map.put("user_id", PreferenceConnector.readString(getActivity(), PreferenceConnector.User_id, ""));
        map.put("country", countryId);
        map.put("register_id", PreferenceConnector.readString(getActivity(), PreferenceConnector.Register_id, ""));


        Call<ResponseBody> loginCall = apiInterface.updateCountryApi(headerMap, map);

        loginCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call,
                                   Response<ResponseBody> response) {
                DataManager.getInstance().hideProgressMessage();
                try {
                    String responseString = response.body().string();
                    JSONObject jsonObject = new JSONObject(responseString);
                    Log.e(TAG, "update country  Response = " + responseString);
                    if (jsonObject.getString("status").equals("1")) {
                        JSONObject jsonObject11 = jsonObject.getJSONObject("result");
                        listener.search(country);
                        dialog.dismiss();

                    } else if (jsonObject.getString("status").equals("5")) {
                        PreferenceConnector.writeString(getActivity(), PreferenceConnector.LoginStatus, "false");
                        startActivity(new Intent(getActivity(), Splash.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP));
                        getActivity().finish();
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


    public void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null && getView() != null) {
            imm.hideSoftInputFromWindow(getView().getWindowToken(), 0);
        }

    }

}

