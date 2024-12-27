package com.my.afarycode.OnlineShopping.bottomsheet;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.my.afarycode.OnlineShopping.constant.PreferenceConnector;
import com.my.afarycode.OnlineShopping.fragment.BottomAddFragment;
import com.my.afarycode.OnlineShopping.fragment.TransferMOneyFragment;
import com.my.afarycode.OnlineShopping.helper.DataManager;
import com.my.afarycode.OnlineShopping.listener.AskListener;
import com.my.afarycode.R;
import com.my.afarycode.ratrofit.AfaryCode;
import com.my.afarycode.ratrofit.ApiClient;
import com.rilixtech.widget.countrycodepicker.CountryCodePicker;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;



public class TransferMoneyBottomSheet extends BottomSheetDialogFragment implements AskListener{

    Context context;
    private RelativeLayout btnFirst,btnSecond;
    AskListener listener;
    public TransferMoneyBottomSheet(Context context) {
        this.context = context;
    }


   public TransferMoneyBottomSheet callBack(AskListener listener) {
        this.listener = listener;
        return this;
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @SuppressLint("RestrictedApi")
    public void setupDialog(Dialog dialog, int style) {
        View contentView = View.inflate(getContext(), R.layout.fragment_transfer, (ViewGroup) null);

        btnFirst = contentView.findViewById(R.id.btnFirst);
        btnSecond = contentView.findViewById(R.id.btnSecond);

        btnFirst.setOnClickListener(v -> {
            new TransferMOneyFragment(getActivity()).callBack(this::ask).show(getActivity().getSupportFragmentManager(), "ModalBottomSheet");

        });

        btnSecond.setOnClickListener(v -> {
            new RequestMoneyBottomSheet(getActivity()).callBack(this::ask).show(getActivity().getSupportFragmentManager(), "ModalBottomSheet");

        });

        dialog.setContentView(contentView);

        ((View) contentView.getParent()).setBackgroundColor(getResources().getColor(android.R.color.transparent));

    }

    @Override
    public void ask(String value, String status) {
        listener.ask("","transfer");
        dismiss();
    }
}
