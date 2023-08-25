package com.my.afarycode.OnlineShopping.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.my.afarycode.OnlineShopping.Model.HomeShopeProductModel;
import com.my.afarycode.R;

import java.util.ArrayList;

public class MyAddAddressAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    private Context mContext;
    private ArrayList<HomeShopeProductModel> modelList;
    private OnItemClickListener mItemClickListener;

    public MyAddAddressAdapter(Context context, ArrayList<HomeShopeProductModel> modelList) {
        this.mContext = context;
        this.modelList = modelList;
    }

    public void updateList(ArrayList<HomeShopeProductModel> modelList) {
        this.modelList = modelList;
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_my_address, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        //Here you can fill your row view
        if (holder instanceof ViewHolder) {
            final HomeShopeProductModel model = getItem(position);
            final ViewHolder genericViewHolder = (ViewHolder) holder;


          genericViewHolder.txtAddress.setText(model.getName());

        }

    }


    @Override
    public int getItemCount() {

        return modelList.size();
    }

    public void SetOnItemClickListener(final OnItemClickListener mItemClickListener) {
        this.mItemClickListener = mItemClickListener;
    }

    private HomeShopeProductModel getItem(int position) {
        return modelList.get(position);
    }


    public interface OnItemClickListener {

        void onItemClick(View view, int position, HomeShopeProductModel model);

    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView txtAddress;

        public ViewHolder(final View itemView) {
            super(itemView);

         this.txtAddress=itemView.findViewById(R.id.txtAddress);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    mItemClickListener.onItemClick(itemView, getAdapterPosition(), modelList.get(getAdapterPosition()));

                }
            });
        }
    }


}

