package com.my.afarycode.OnlineShopping.Model;
import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class OrderHistoryModel {

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

        @SerializedName("taxN1")
        @Expose
        private String taxN1;
        @SerializedName("taxN2")
        @Expose
        private String taxN2;
        @SerializedName("delivery_charges")
        @Expose
        private String deliveryCharges;
        @SerializedName("platFormsFees")
        @Expose
        private String platFormsFees;
        @SerializedName("order_id")
        @Expose
        private String orderId;
        @SerializedName("status")
        @Expose
        private String status;
        @SerializedName("total")
        @Expose
        private String total;
        @SerializedName("total_text")
        @Expose
        private String totalText;
        @SerializedName("product_images")
        @Expose
        private List<ProductImage> productImages;

        public String getTaxN1() {
            return taxN1;
        }

        public void setTaxN1(String taxN1) {
            this.taxN1 = taxN1;
        }

        public String getTaxN2() {
            return taxN2;
        }

        public void setTaxN2(String taxN2) {
            this.taxN2 = taxN2;
        }

        public String getDeliveryCharges() {
            return deliveryCharges;
        }

        public void setDeliveryCharges(String deliveryCharges) {
            this.deliveryCharges = deliveryCharges;
        }

        public String getPlatFormsFees() {
            return platFormsFees;
        }

        public void setPlatFormsFees(String platFormsFees) {
            this.platFormsFees = platFormsFees;
        }

        public String getOrderId() {
            return orderId;
        }

        public void setOrderId(String orderId) {
            this.orderId = orderId;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getTotal() {
            return total;
        }

        public void setTotal(String total) {
            this.total = total;
        }

        public String getTotalText() {
            return totalText;
        }

        public void setTotalText(String totalText) {
            this.totalText = totalText;
        }

        public List<ProductImage> getProductImages() {
            return productImages;
        }

        public void setProductImages(List<ProductImage> productImages) {
            this.productImages = productImages;
        }

        public class ProductImage {

            @SerializedName("product_images")
            @Expose
            private String productImages;

            public String getProductImages() {
                return productImages;
            }

            public void setProductImages(String productImages) {
                this.productImages = productImages;
            }

        }


    }

}


