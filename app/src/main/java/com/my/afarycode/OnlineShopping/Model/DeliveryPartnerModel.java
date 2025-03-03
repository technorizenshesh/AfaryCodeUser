package com.my.afarycode.OnlineShopping.Model;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class DeliveryPartnerModel {

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
        @SerializedName("intercity_partner_id")
        @Expose
        private String intercityPartnerId;
        @SerializedName("city_id_start")
        @Expose
        private String cityIdStart;
        @SerializedName("city_id_destination")
        @Expose
        private String cityIdDestination;
        @SerializedName("status")
        @Expose
        private String status;
        @SerializedName("price")
        @Expose
        private String price;
        @SerializedName("country_id")
        @Expose
        private String countryId;
        @SerializedName("state_id")
        @Expose
        private String stateId;
        @SerializedName("city_start")
        @Expose
        private String cityStart;
        @SerializedName("city_end")
        @Expose
        private String cityEnd;
        @SerializedName("intercity_partner")
        @Expose
        private String intercityPartner;
        @SerializedName("intercity_partner_image")
        @Expose
        private String intercityPartnerImage;


        private boolean chk =false;

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

        public String getIntercityPartnerId() {
            return intercityPartnerId;
        }

        public void setIntercityPartnerId(String intercityPartnerId) {
            this.intercityPartnerId = intercityPartnerId;
        }

        public String getCityIdStart() {
            return cityIdStart;
        }

        public void setCityIdStart(String cityIdStart) {
            this.cityIdStart = cityIdStart;
        }

        public String getCityIdDestination() {
            return cityIdDestination;
        }

        public void setCityIdDestination(String cityIdDestination) {
            this.cityIdDestination = cityIdDestination;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getPrice() {
            return price;
        }

        public void setPrice(String price) {
            this.price = price;
        }

        public String getCountryId() {
            return countryId;
        }

        public void setCountryId(String countryId) {
            this.countryId = countryId;
        }

        public String getStateId() {
            return stateId;
        }

        public void setStateId(String stateId) {
            this.stateId = stateId;
        }

        public String getCityStart() {
            return cityStart;
        }

        public void setCityStart(String cityStart) {
            this.cityStart = cityStart;
        }

        public String getCityEnd() {
            return cityEnd;
        }

        public void setCityEnd(String cityEnd) {
            this.cityEnd = cityEnd;
        }

        public String getIntercityPartner() {
            return intercityPartner;
        }

        public void setIntercityPartner(String intercityPartner) {
            this.intercityPartner = intercityPartner;
        }

        public String getIntercityPartnerImage() {
            return intercityPartnerImage;
        }

        public void setIntercityPartnerImage(String intercityPartnerImage) {
            this.intercityPartnerImage = intercityPartnerImage;
        }

    }

}


