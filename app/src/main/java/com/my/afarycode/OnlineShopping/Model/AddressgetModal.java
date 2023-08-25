package com.my.afarycode.OnlineShopping.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;


public class AddressgetModal {

    @SerializedName("result")
    @Expose
    public List<Result> result = null;
    @SerializedName("message")
    @Expose
    public String message;
    @SerializedName("status")
    @Expose
    public String status;

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
        public String id;
        @SerializedName("user_id")
        @Expose
        public String userId;
        @SerializedName("indicate_address_1")
        @Expose
        public String indicateAddress1;
        @SerializedName("indicate_address_2")
        @Expose
        public String indicateAddress2;
        @SerializedName("indicate_address_3")
        @Expose
        public String indicateAddress3;
        @SerializedName("location_1")
        @Expose
        public String location1;
        @SerializedName("location_2")
        @Expose
        public String location2;
        @SerializedName("location_3")
        @Expose
        public String location3;
        @SerializedName("city_1")
        @Expose
        public String city1;
        @SerializedName("city_2")
        @Expose
        public String city2;
        @SerializedName("city_3")
        @Expose
        public String city3;
        @SerializedName("phone")
        @Expose
        public String phone;
        @SerializedName("type")
        @Expose
        public String type;
        @SerializedName("date_time")
        @Expose
        public String dateTime;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getUserId() {
            return userId;
        }

        public void setUserId(String userId) {
            this.userId = userId;
        }

        public String getIndicateAddress1() {
            return indicateAddress1;
        }

        public void setIndicateAddress1(String indicateAddress1) {
            this.indicateAddress1 = indicateAddress1;
        }

        public String getIndicateAddress2() {
            return indicateAddress2;
        }

        public void setIndicateAddress2(String indicateAddress2) {
            this.indicateAddress2 = indicateAddress2;
        }

        public String getIndicateAddress3() {
            return indicateAddress3;
        }

        public void setIndicateAddress3(String indicateAddress3) {
            this.indicateAddress3 = indicateAddress3;
        }

        public String getLocation1() {
            return location1;
        }

        public void setLocation1(String location1) {
            this.location1 = location1;
        }

        public String getLocation2() {
            return location2;
        }

        public void setLocation2(String location2) {
            this.location2 = location2;
        }

        public String getLocation3() {
            return location3;
        }

        public void setLocation3(String location3) {
            this.location3 = location3;
        }

        public String getCity1() {
            return city1;
        }

        public void setCity1(String city1) {
            this.city1 = city1;
        }

        public String getCity2() {
            return city2;
        }

        public void setCity2(String city2) {
            this.city2 = city2;
        }

        public String getCity3() {
            return city3;
        }

        public void setCity3(String city3) {
            this.city3 = city3;
        }

        public String getPhone() {
            return phone;
        }

        public void setPhone(String phone) {
            this.phone = phone;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getDateTime() {
            return dateTime;
        }

        public void setDateTime(String dateTime) {
            this.dateTime = dateTime;
        }

    }
}