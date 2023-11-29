package com.my.afarycode.OnlineShopping.orderdetails;

public interface ItemOrderListener {
    void onItem(int position,OrderDetailsModel.Result.Product product);
}
