package com.my.afarycode.OnlineShopping.Model;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class DeliveryAgencyModel {

    @SerializedName("result")
    @Expose
    private List<Result> result;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("status")
    @Expose
    private String status;

    public List<Result> getResult() {
        return result;
    }

    public void setResult(List<Result> result) {
        this.result = result;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }


    public class Result {

        @SerializedName("id")
        @Expose
        private String id;
        @SerializedName("name")
        @Expose
        private String name;
        @SerializedName("status")
        @Expose
        private String status;
        @SerializedName("date_time")
        @Expose
        private String dateTime;

        @SerializedName("price")
        @Expose
        private String price;

        @SerializedName("image")
        @Expose
        private String image;

        @SerializedName("chk")
        @Expose
        private boolean chk = false;


        @SerializedName("type")
        @Expose
        private String deliveryMethod;


        @SerializedName("country_id")
        @Expose
        private String countryId;

        @SerializedName("local_currency")
        @Expose
        private String localCurrency;

        @SerializedName("show_currency_code")
        @Expose
        private String showCurrencyCode;

        @SerializedName("local_price")
        @Expose
        private String localPrice;


        public String getCountryId() {
            return countryId;
        }

        public void setCountryId(String countryId) {
            this.countryId = countryId;
        }

        public String getLocalCurrency() {
            return localCurrency;
        }

        public void setLocalCurrency(String localCurrency) {
            this.localCurrency = localCurrency;
        }

        public String getShowCurrencyCode() {
            return showCurrencyCode;
        }

        public void setShowCurrencyCode(String showCurrencyCode) {
            this.showCurrencyCode = showCurrencyCode;
        }

        public String getLocalPrice() {
            return localPrice;
        }

        public void setLocalPrice(String localPrice) {
            this.localPrice = localPrice;
        }

        public String getDeliveryMethod() {
            return deliveryMethod;
        }

        public void setDeliveryMethod(String deliveryMethod) {
            this.deliveryMethod = deliveryMethod;
        }

        public String getImage() {
            return image;
        }

        public void setImage(String image) {
            this.image = image;
        }

        public boolean isChk() {
            return chk;
        }

        public void setChk(boolean chk) {
            this.chk = chk;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getDateTime() {
            return dateTime;
        }

        public void setDateTime(String dateTime) {
            this.dateTime = dateTime;
        }

        public String getPrice() {
            return price;
        }

        public void setPrice(String price) {
            this.price = price;
        }
    }

}

