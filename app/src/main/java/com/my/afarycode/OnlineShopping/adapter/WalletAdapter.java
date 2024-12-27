package com.my.afarycode.OnlineShopping.adapter;

import android.content.Context;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.my.afarycode.OnlineShopping.AllShopOnlineActivity;
import com.my.afarycode.OnlineShopping.HomeShoppingOnlineScreen;
import com.my.afarycode.OnlineShopping.Model.GetRestorentsModal;
import com.my.afarycode.OnlineShopping.Model.GetTransferDetails;
import com.my.afarycode.OnlineShopping.Model.ShoppingProductModal;
import com.my.afarycode.OnlineShopping.Model.ShoppingStoreDetailsModal;
import com.my.afarycode.OnlineShopping.ShoppingProductDetail;
import com.my.afarycode.R;
import com.my.afarycode.databinding.ItemReviewItemBinding;
import com.my.afarycode.databinding.ItemWalletBinding;
import com.my.afarycode.databinding.NearMyHomeBinding;
import com.my.afarycode.databinding.ShoppingStoreAdapterBinding;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class WalletAdapter extends
        RecyclerView.Adapter<WalletAdapter.MyViewHolder> {

    private ArrayList<GetTransferDetails.Result> all_category_subcategory;
    private final Context activity;
    private ShoppingProductDetail fragment;

    public WalletAdapter(Context a, ArrayList<GetTransferDetails.Result> all_category_subcategory) {
        this.activity = a;
        this.all_category_subcategory = all_category_subcategory;
        // adapterCallback = ((CategoryAdapter.AdapterCallback1) activity);
    }

    @Override
    public WalletAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        ItemWalletBinding progressAdapterBinding = DataBindingUtil.inflate(LayoutInflater.from(activity)
                , R.layout.item_wallet, parent, false);

        return new MyViewHolder(progressAdapterBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, final int position) {

        holder.progressAdapterBinding.userName.setText(all_category_subcategory.get(position).getDatetime());
        holder.progressAdapterBinding.amount.setText(all_category_subcategory.get(position).getShowCurrencyCode() + all_category_subcategory.get(position).getLocalPrice());
        holder.progressAdapterBinding.tvWalletBal.setText(all_category_subcategory.get(position).getShowCurrencyCode() + all_category_subcategory.get(position).getWalletLocalPrice());


        holder.progressAdapterBinding.tvTransId.setText(Html.fromHtml("<font color='#000'>" + "<b>" + activity.getString(R.string.transaction_id1) + " :" + "</b>" + all_category_subcategory.get(position).getTransactionId() + "</font>"));
        holder.progressAdapterBinding.tvRefInfo.setText(Html.fromHtml("<font color='#000'>" + "<b>" + activity.getString(R.string.refe_info) + " :" + "</b>" + all_category_subcategory.get(position).getReferenceInfo() + "</font>"));
        holder.progressAdapterBinding.tvTemps.setText(Html.fromHtml("<font color='#000'>" + "<b>" + activity.getString(R.string.tamps) + " :" + "</b>" + all_category_subcategory.get(position).getDatetime() + "</font>"));
        holder.progressAdapterBinding.tvAmount.setText(Html.fromHtml("<font color='#000'>" + "<b>" + activity.getString(R.string.amount) + " : " + "</b>" + all_category_subcategory.get(position).getShowCurrencyCode() + all_category_subcategory.get(position).getLocalPrice() + "</font>"));


        if (all_category_subcategory.get(position).getTransactionType().equalsIgnoreCase("Wallet")) {
            holder.progressAdapterBinding.tvProductType.setText(Html.fromHtml("<font color='#000'>" + "<b>" + activity.getString(R.string.description) + " : " + activity.getString(R.string.purchase) + "</b>" + "</font>"));
            holder.progressAdapterBinding.tvTransactonType.setText(Html.fromHtml("<font color='#000'>" + "<b>" + activity.getString(R.string.transaction_type) + " : " + "</b>" + activity.getString(R.string.wallet) + "</font>"));
            holder.progressAdapterBinding.ivType.setImageResource(R.drawable.ic_payment);


        } else if (all_category_subcategory.get(position).getTransactionType().equalsIgnoreCase("Booking")) {
            if (all_category_subcategory.get(position).getOperateur().equalsIgnoreCase("AM")) {
                holder.progressAdapterBinding.tvTransactonType.setText(Html.fromHtml("<font color='#000'>" + "<b>" + activity.getString(R.string.transaction_type) + " : " + "</b>" + activity.getString(R.string.airtel_money_number) + " " + all_category_subcategory.get(position).getClient() + "</font>"));

            } else if (all_category_subcategory.get(position).getOperateur().equalsIgnoreCase("MC")) {
                holder.progressAdapterBinding.tvTransactonType.setText(Html.fromHtml("<font color='#000'>" + "<b>" + activity.getString(R.string.transaction_type) + " : " + "</b>" + activity.getString(R.string.moov_money_number) + " " + all_category_subcategory.get(position).getClient() + "</font>"));
            } else {
                holder.progressAdapterBinding.tvTransactonType.setText(Html.fromHtml("<font color='#000'>" + "<b>" + activity.getString(R.string.transaction_type) + " : " + "</b>" + activity.getString(R.string.wallet) + "</font>"));
            }

            holder.progressAdapterBinding.userName2.setText(activity.getString(R.string.to) + " " + all_category_subcategory.get(position).getReferenceInfo());

            holder.progressAdapterBinding.tvProductType.setText(Html.fromHtml("<font color='#000'>" + "<b>" + activity.getString(R.string.description) + " : " + activity.getString(R.string.purchase) + "</b>" + "</font>"));
            holder.progressAdapterBinding.tvTransId.setText(Html.fromHtml("<font color='#000'>" + "<b>" + activity.getString(R.string.transaction_id1) + " : " + "</b>" + all_category_subcategory.get(position).getTransactionId() + "</font>"));
            holder.progressAdapterBinding.tvRefInfo.setText(Html.fromHtml("<font color='#000'>" + "<b>" + activity.getString(R.string.refe_info) + " : " + "</b>" + all_category_subcategory.get(position).getReferenceInfo() + "</font>"));
            holder.progressAdapterBinding.ivType.setImageResource(R.drawable.ic_payment);


        } else if (all_category_subcategory.get(position).getTransactionType().equalsIgnoreCase("AddMoney")) {
            holder.progressAdapterBinding.ivType.setImageResource(R.drawable.received);
            holder.progressAdapterBinding.userName2.setText(activity.getString(R.string.from) + " " + all_category_subcategory.get(position).getTransactionBy());


            if (all_category_subcategory.get(position).getOperateur().equalsIgnoreCase("AM"))
                holder.progressAdapterBinding.tvTransactonType.setText(Html.fromHtml("<font color='#000'>" + "<b>" + activity.getString(R.string.transaction_type) + " : " + "</b>" + activity.getString(R.string.airtel_money_number) + " " + all_category_subcategory.get(position).getClient() + "</font>"));

            else if (all_category_subcategory.get(position).getOperateur().equalsIgnoreCase("MC"))
                holder.progressAdapterBinding.tvTransactonType.setText(Html.fromHtml("<font color='#000'>" + "<b>" + activity.getString(R.string.transaction_type) + " : " + "</b>" + activity.getString(R.string.moov_money_number) + " " + all_category_subcategory.get(position).getClient() + "</font>"));


            holder.progressAdapterBinding.tvProductType.setText(Html.fromHtml("<font color='#000'>" + "<b>" + activity.getString(R.string.description) + " : " + activity.getString(R.string.recharge) + "</b>" + "</font>"));
            holder.progressAdapterBinding.rlOne.setVisibility(View.VISIBLE);
            holder.progressAdapterBinding.rlTwo.setVisibility(View.GONE);


        } else if (all_category_subcategory.get(position).getTransactionType().equalsIgnoreCase("Withdraw")) {
            holder.progressAdapterBinding.ivType.setImageResource(R.drawable.withdraw);
            holder.progressAdapterBinding.userName2.setText(activity.getString(R.string.to) + " " + " " + activity.getString(R.string.withdraw_money_));
            holder.progressAdapterBinding.tvProductType.setText(Html.fromHtml("<font color='#000'>" + "<b>" + activity.getString(R.string.description) + " : " + activity.getString(R.string.withdraw) + "</b>" + "</font>"));
            holder.progressAdapterBinding.tvTransactonType.setText(Html.fromHtml("<font color='#000'>" + "<b>" + activity.getString(R.string.transaction_type) + " : " + "</b>" + activity.getString(R.string.wallet) + "</font>"));
            //    holder.progressAdapterBinding.userName2.setText("To " + all_category_subcategory.get(position).getReferenceInfo());

            //  holder.progressAdapterBinding.rlOne.setVisibility(View.VISIBLE);
            //  holder.progressAdapterBinding.rlTwo.setVisibility(View.GONE);


        } else if (all_category_subcategory.get(position).getTransactionType().equalsIgnoreCase("TransferMoney")) {
            holder.progressAdapterBinding.ivType.setImageResource(R.drawable.withdraw);
            holder.progressAdapterBinding.userName2.setText(activity.getString(R.string.to) + " " + all_category_subcategory.get(position).getReferenceInfo());
            holder.progressAdapterBinding.tvProductType.setText(Html.fromHtml("<font color='#000'>" + "<b>" + activity.getString(R.string.description) + " : " + activity.getString(R.string.transfer) + "</b>" + "</font>"));
            holder.progressAdapterBinding.tvTransactonType.setText(Html.fromHtml("<font color='#000'>" + "<b>" + "Transaction type :" + "</b>" + activity.getString(R.string.wallet) + " " + "</font>"));
            // holder.progressAdapterBinding.userName2.setText("To " + all_category_subcategory.get(position).getReferenceInfo());

            //  holder.progressAdapterBinding.rlOne.setVisibility(View.VISIBLE);
            //  holder.progressAdapterBinding.rlTwo.setVisibility(View.GONE);


        } else if (all_category_subcategory.get(position).getInterfaceid().equalsIgnoreCase("Refund")) {
            holder.progressAdapterBinding.ivType.setImageResource(R.drawable.received);
            holder.progressAdapterBinding.userName2.setText(activity.getString(R.string.received) + " " + all_category_subcategory.get(position).getDescription());
            holder.progressAdapterBinding.tvTransactonType.setText(Html.fromHtml("<font color='#000'>" + "<b>" + activity.getString(R.string.transaction_type) + " : " + "</b>" + all_category_subcategory.get(position).getDescription() + "</font>"));
            holder.progressAdapterBinding.tvRefInfo.setText(Html.fromHtml("<font color='#000'>" + "<b>" + activity.getString(R.string.refe_info) + " :" + "</b>"
                    + activity.getString(R.string.your_order_cancellation_amount_refund) + " "
                    + all_category_subcategory.get(position).getShowCurrencyCode()
                    + all_category_subcategory.get(position).getLocalPrice() + " "
                    + activity.getString(R.string.successfully_done_for_order_id) + " "
                    + all_category_subcategory.get(position).getOrderId()+ "</font>"));


            holder.progressAdapterBinding.tvProductType.setText(Html.fromHtml("<font color='#000'>" + "<b>" + activity.getString(R.string.description) + " : " + activity.getString(R.string.refund) + "</b>" + "</font>"));
            holder.progressAdapterBinding.rlOne.setVisibility(View.VISIBLE);
            holder.progressAdapterBinding.rlTwo.setVisibility(View.GONE);


        }


        if (all_category_subcategory.get(position).isChk() == true) {

            holder.progressAdapterBinding.ivExpen.setImageResource(R.drawable.ic_expen_minus);
            holder.progressAdapterBinding.rlDetail.setVisibility(View.GONE);
            holder.progressAdapterBinding.rlTwo.setVisibility(View.VISIBLE);

        } else {

            holder.progressAdapterBinding.ivExpen.setImageResource(R.drawable.ic_expen_plus);
            holder.progressAdapterBinding.rlDetail.setVisibility(View.VISIBLE);
            holder.progressAdapterBinding.rlTwo.setVisibility(View.GONE);

        }

    }

    @Override
    public int getItemCount() {
        return all_category_subcategory.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        ItemWalletBinding progressAdapterBinding;

        public MyViewHolder(ItemWalletBinding itemView) {
            super(itemView.getRoot());
            progressAdapterBinding = itemView;

            progressAdapterBinding.ivExpen.setOnClickListener(view -> {
                if (all_category_subcategory.get(getAdapterPosition()).isChk() == false)
                    all_category_subcategory.get(getAdapterPosition()).setChk(true);
                else all_category_subcategory.get(getAdapterPosition()).setChk(false);
                notifyDataSetChanged();

            });

            progressAdapterBinding.tvTransId.setOnClickListener(view -> setClipboard(activity, all_category_subcategory.get(getAdapterPosition()).getTransactionId()));

        }
    }


    private void setClipboard(Context context, String text) {
        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.HONEYCOMB) {
            android.text.ClipboardManager clipboard = (android.text.ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
            clipboard.setText(text);
        } else {
            android.content.ClipboardManager clipboard = (android.content.ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
            android.content.ClipData clip = android.content.ClipData.newPlainText("Copied Text", text);
            clipboard.setPrimaryClip(clip);
        }

        Toast.makeText(activity, "copy...", Toast.LENGTH_SHORT).show();
    }

}

