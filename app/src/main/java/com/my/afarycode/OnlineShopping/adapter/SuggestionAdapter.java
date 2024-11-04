package com.my.afarycode.OnlineShopping.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.my.afarycode.OnlineShopping.Model.ProductItemModel;
import com.my.afarycode.OnlineShopping.Model.SuggestProductModel;
import com.my.afarycode.R;

import java.util.List;




public class SuggestionAdapter extends ArrayAdapter<ProductItemModel.Result> {
    private List<ProductItemModel.Result> arrayList;

    public SuggestionAdapter(Context context, List<ProductItemModel.Result> arrayList) {
        super(context, 0,arrayList);
        this.arrayList = arrayList;

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
      /*  SuggestProductModel.Result place = arrayList.get(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_search, parent, false);
        }

        TextView textView = convertView.findViewById(R.id.tvProductName);
        assert place != null;
        textView.setText(place.getProductName());

        return convertView;*/




        if (position < 0 || position >= arrayList.size()) {
            return convertView; // Return an empty view or handle as needed
        }

        ProductItemModel.Result place = arrayList.get(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_search, parent, false);
        }

        TextView textView = convertView.findViewById(R.id.tvProductName);
        assert place != null; // Ensure place is not null
        textView.setText(place.getProductName());


        return convertView;

    }

    @Override
    public int getCount() {
        return arrayList.size();
    }
}
