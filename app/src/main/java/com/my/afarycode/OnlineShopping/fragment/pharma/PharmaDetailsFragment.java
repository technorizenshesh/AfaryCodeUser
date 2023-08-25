package com.my.afarycode.OnlineShopping.fragment.pharma;

import android.app.AlertDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;

import com.my.afarycode.OnlineShopping.Model.HomeShopeProductModel;
import com.my.afarycode.R;
import com.my.afarycode.databinding.PharmasistDetailsBinding;

import java.util.ArrayList;


public class PharmaDetailsFragment extends Fragment {
    PharmasistDetailsBinding binding;
    private ArrayList<HomeShopeProductModel> modelList = new ArrayList<>();
    private AlertDialog alertDialog;
    private View close;
    private PharmaReciptFragment fragment1;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = DataBindingUtil.inflate(inflater, R.layout.pharmasist_details, container, false);

        binding.orderConfirmation.setOnClickListener(v -> {
            fragment1 = new PharmaReciptFragment();
            Bundle mBundle = new Bundle();
            mBundle.putString("cat_id", "");
            loadFragment(fragment1, mBundle);
            alertDialog.dismiss();
        });

        binding.addNewM.setOnClickListener(v -> {
            SetupUI();

        });

        return binding.getRoot();
    }


    public boolean loadFragment(Fragment fragment, Bundle mBundle) {
        FragmentManager fragmentManager = ((FragmentActivity) getActivity())
                .getSupportFragmentManager();
        fragment.setArguments(mBundle);
        if (fragment != null) {
            fragmentManager.beginTransaction()
                    .addToBackStack("Home")
                    .replace(R.id.fragment_homeContainer, fragment)
                    .commit();
            return true;
        }
        return false;
    }

     private void SetupUI() {

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getContext());
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.add_new_madicine, null);


      /*  close = dialogView.findViewById(R.id.close);

        close.setOnClickListener(v -> {
            alertDialog.dismiss();
        });*/

        dialogBuilder.setView(dialogView);
        alertDialog = dialogBuilder.create();
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        alertDialog.show();

    }

}