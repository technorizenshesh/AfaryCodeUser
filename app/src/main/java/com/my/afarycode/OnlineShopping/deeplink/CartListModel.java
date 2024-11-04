package com.my.afarycode.OnlineShopping.deeplink;

public class CartListModel {
    String name,image,price,qty,currency;

    public CartListModel(String name, String image, String price, String qty,String currency) {
        this.name = name;
        this.image = image;
        this.price = price;
        this.qty = qty;
        this.currency = currency;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getQty() {
        return qty;
    }

    public void setQty(String qty) {
        this.qty = qty;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }
}

