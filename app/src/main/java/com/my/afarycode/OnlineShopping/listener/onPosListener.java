package com.my.afarycode.OnlineShopping.listener;

import android.app.Dialog;

public interface onPosListener {
    void onPos(int position);
    void onPos(int position,String Type);
    void onPos(int position, String Type, Dialog dialog);

}
