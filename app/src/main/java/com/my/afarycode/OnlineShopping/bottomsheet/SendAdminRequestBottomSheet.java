package com.my.afarycode.OnlineShopping.bottomsheet;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.my.afarycode.OnlineShopping.ForgotPassword;
import com.my.afarycode.OnlineShopping.SignUpActivity;
import com.my.afarycode.OnlineShopping.constant.PreferenceConnector;
import com.my.afarycode.OnlineShopping.helper.DataManager;
import com.my.afarycode.OnlineShopping.helper.NetworkAvailablity;
import com.my.afarycode.OnlineShopping.listener.AskListener;
import com.my.afarycode.R;
import com.my.afarycode.Splash;
import com.my.afarycode.databinding.FragmentSendAdminBinding;
import com.my.afarycode.ratrofit.AfaryCode;
import com.my.afarycode.ratrofit.ApiClient;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;












public class SendAdminRequestBottomSheet extends BottomSheetDialogFragment {
    Context context;
    BottomSheetDialog dialog;

    private AfaryCode apiInterface;
    FragmentSendAdminBinding binding;
    AskListener listener;
    private BottomSheetBehavior<View> mBehavior;
    public SendAdminRequestBottomSheet(Context context) {
        this.context = context;
    }


    public SendAdminRequestBottomSheet callBack(AskListener listener) {
        this.listener = listener;
        return this;
    }


    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @SuppressLint("RestrictedApi")
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        dialog = (BottomSheetDialog) super.onCreateDialog(savedInstanceState);
        binding = DataBindingUtil.inflate(LayoutInflater.from(getActivity()), R.layout.fragment_send_admin, null, false);
        dialog.setContentView(binding.getRoot());
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        mBehavior = BottomSheetBehavior.from((View) binding.getRoot().getParent());
        mBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        initBinding();
        return  dialog;
    }

    private void initBinding() {
        apiInterface = ApiClient.getClient(getActivity()).create(AfaryCode.class);

        binding.RRLogin.setOnClickListener(v -> {


            if (binding.edNumber.getText().toString().trim().isEmpty()) {
                binding.edNumber.setError(getString(R.string.can_not_be_empty));

                Toast.makeText(getActivity(), getString(R.string.please_enter_mobile_number), Toast.LENGTH_SHORT).show();

            } else {

                //  startActivity(new Intent(ForgotPassword.this, VerificationScreen.class)
                //      .putExtra("user_email", binding.edtmobile.getText().toString()));

                if(NetworkAvailablity.checkNetworkStatus(requireActivity()))    sendAdminRequestAPI(binding.ccp.getSelectedCountryCode()+"-"+binding.edNumber.getText().toString());
                else Toast.makeText(requireActivity(), getString(R.string.network_failure), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void sendAdminRequestAPI(String email) {
        DataManager.getInstance().showProgressMessage(getActivity(), getString(R.string.please_wait));

        Map<String,String> headerMap = new HashMap<>();
       // headerMap.put("Authorization","Bearer " + PreferenceConnector.readString(getActivity(), PreferenceConnector.access_token,""));
      //  headerMap.put("Accept","application/json");

        Map<String, String> map = new HashMap<>();
        map.put("email", email);
        map.put("user_type", "User");
        map.put("device_id", PreferenceConnector.readString(requireActivity(),
                PreferenceConnector.Firebash_Token, ""));

        Log.e("MapMap", "Send Admin Request" + map);
        Call<ResponseBody> SignupCall = apiInterface.sendAdminRequest(map);

        SignupCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                DataManager.getInstance().hideProgressMessage();

                try {
                    String responseData = response.body() != null ? response.body().string() : "";
                    JSONObject object = new JSONObject(responseData);
                    Log.e("MapMap", "Send Admin Response" + responseData);
                    if (object.optString("status").equals("1")) {
                        Toast.makeText(getContext(),getString(R.string.send_admin_request_successfull) , Toast.LENGTH_LONG).show();
                        listener.ask(responseData,"send");
                        dialog.dismiss();
                    } else if (object.optString("status").equals("0")) {
                       // listener.ask("","send");
                      //  dialog.dismiss();
                        Toast.makeText(getContext(), object.optString("message"), Toast.LENGTH_LONG).show();

                    }

                    else if (object.getString("status").equals("5")) {
                        PreferenceConnector.writeString(getActivity(), PreferenceConnector.LoginStatus, "false");
                        startActivity(new Intent(getActivity(), Splash.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP));
                        getActivity().finish();
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
