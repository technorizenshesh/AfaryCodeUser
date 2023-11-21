package com.my.afarycode.OnlineShopping.adapter;

import android.content.Context;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.my.afarycode.OnlineShopping.fragment.OrderCancelFragment;
import com.my.afarycode.OnlineShopping.fragment.OrderCompleteFragment;

public class OrderFragmentAdapter extends FragmentPagerAdapter {

    private Context myContext;
    int totalTabs;

    public OrderFragmentAdapter(Context context, FragmentManager fm, int totalTabs) {
        super(fm);
        myContext = context;
        this.totalTabs = totalTabs;
    }

    // this is for fragment tabs
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                OrderCompleteFragment orderCompleteFragment = new OrderCompleteFragment();
                return orderCompleteFragment;

            case 1:
                OrderCancelFragment orderCancelFragment = new OrderCancelFragment();
                return orderCancelFragment;



            default:
                return null;
        }
    }
    @Override
    public int getCount() {
        return totalTabs;
    }
}
