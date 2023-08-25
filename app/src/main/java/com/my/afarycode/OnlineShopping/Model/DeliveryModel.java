package com.my.afarycode.OnlineShopping.Model;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class DeliveryModel {

    @SerializedName("result")
    @Expose
    private List<Result> result = null;
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




    public class Result implements Comparable<Result>{

        @SerializedName("id")
        @Expose
        private String id;
        @SerializedName("sections")
        @Expose
        private String sections;
        @SerializedName("taxes_first")
        @Expose
        private String taxesFirst;
        @SerializedName("taxes_second")
        @Expose
        private String taxesSecond;
        @SerializedName("platform_fees")
        @Expose
        private String platformFees;
        @SerializedName("commission")
        @Expose
        private String commission;
        @SerializedName("delivery_fees")
        @Expose
        private String deliveryFees;

        @SerializedName("km")
        @Expose
        private String km;

        @SerializedName("delivery_charges")
        @Expose
        private String deliveryCharges;


        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getSections() {
            return sections;
        }

        public void setSections(String sections) {
            this.sections = sections;
        }

        public String getTaxesFirst() {
            return taxesFirst;
        }

        public void setTaxesFirst(String taxesFirst) {
            this.taxesFirst = taxesFirst;
        }

        public String getTaxesSecond() {
            return taxesSecond;
        }

        public void setTaxesSecond(String taxesSecond) {
            this.taxesSecond = taxesSecond;
        }

        public String getPlatformFees() {
            return platformFees;
        }

        public void setPlatformFees(String platformFees) {
            this.platformFees = platformFees;
        }

        public String getCommission() {
            return commission;
        }

        public void setCommission(String commission) {
            this.commission = commission;
        }

        public String getDeliveryFees() {
            return deliveryFees;
        }

        public void setDeliveryFees(String deliveryFees) {
            this.deliveryFees = deliveryFees;
        }

        public String getKm() {
            return km;
        }

        public void setKm(String km) {
            this.km = km;
        }

        public String getDeliveryCharges() {
            return deliveryCharges;
        }

        public void setDeliveryCharges(String deliveryCharges) {
            this.deliveryCharges = deliveryCharges;
        }

        @Override
        public int compareTo(Result o) {
            if (Integer.parseInt(this.getKm()) > Integer.parseInt(o.getKm())) {
                return 1;
            } else if (Integer.parseInt(this.getKm()) < Integer.parseInt(o.getKm())) {
                return -1;
            }
            return 0;
        }     }






}

