package com.my.afarycode.OnlineShopping.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.my.afarycode.OnlineShopping.Model.SliderData;
import com.my.afarycode.R;
import com.smarteist.autoimageslider.SliderViewAdapter;

import java.util.ArrayList;
import java.util.List;

public class SliderTextAdapter extends
        SliderViewAdapter<SliderTextAdapter.SliderAdapterVH> {

    private Context context;
    private List<SliderData> mSliderItems ;

    public SliderTextAdapter(Context context, ArrayList<SliderData> get_result1) {
        this.context = context;
        this.mSliderItems = get_result1;
    }




    @Override
    public SliderAdapterVH onCreateViewHolder(ViewGroup parent) {
        View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.image_slider_text, null);
        return new SliderAdapterVH(inflate);
    }

    @Override
    public void onBindViewHolder(SliderAdapterVH viewHolder, final int position) {
        viewHolder.tvTitle.setText(mSliderItems.get(position).getTitle());
        viewHolder.tvDescription.setText(mSliderItems.get(position).getDescription());


    }

    @Override
    public int getCount() {
        //slider view count could be dynamic size
        return mSliderItems.size();
    }

    class SliderAdapterVH extends SliderViewAdapter.ViewHolder {

        View itemView;

        TextView textViewDescription;
        TextView tvTitle,tvDescription;

        public SliderAdapterVH(View itemView) {
            super(itemView);
        //    imageViewBackground = itemView.findViewById(R.id.iv_auto_image_slider);
         //   imageGifContainer = itemView.findViewById(R.id.iv_gif_container);
            textViewDescription = itemView.findViewById(R.id.tv_auto_image_slider);


            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvDescription = itemView.findViewById(R.id.tvDescription);

            this.itemView = itemView;
        }
    }

}