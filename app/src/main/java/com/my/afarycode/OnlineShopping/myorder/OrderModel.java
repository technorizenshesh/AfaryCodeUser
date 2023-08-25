package com.my.afarycode.OnlineShopping.myorder;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class OrderModel {

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


        @SerializedName("order_id")
        @Expose
        private String orderId;

        @SerializedName("total")
        @Expose
        private String total;

        @SerializedName("total_text")
        @Expose
        private String totalText;

        @SerializedName("status")
        @Expose
        private String status;

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        @SerializedName("product_images")
        @Expose
        private List<productImage> productImageList;



        public class productImage {


            @SerializedName("product_images")
            @Expose
            private String productImages;

            public String getImage() {
                return productImages;
            }

            public void setImage(String productImages) {
                this.productImages = productImages;
            }
        }

        public String getTotalText() {
            return totalText;
        }

        public void setTotalText(String totalText) {
            this.totalText = totalText;
        }

        public String getOrderId() {
            return orderId;
        }

        public void setOrderId(String orderId) {
            this.orderId = orderId;
        }

        public String getTotal() {
            return total;
        }

        public void setTotal(String total) {
            this.total = total;
        }

        public List<productImage> getProductImageList() {
            return productImageList;
        }

        public void setShopImageList(List<productImage> productImageList) {
            this.productImageList = productImageList;
        }
    }



}