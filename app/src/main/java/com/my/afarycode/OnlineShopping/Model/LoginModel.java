package com.my.afarycode.OnlineShopping.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class LoginModel {

    @SerializedName("result")
    @Expose
    public Result result;
    @SerializedName("message")
    @Expose
    public String message;
    @SerializedName("status")
    @Expose
    public String status;

    public Result getResult() {
        return result;
    }

    public void setResult(Result result) {
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
        @SerializedName("user_name")
        @Expose
        public String userName;
        @SerializedName("mobile")
        @Expose
        public String mobile;
        @SerializedName("moov_mobile_num")
        @Expose
        public String moovMobileNum;
        @SerializedName("airtel_mobile_num")
        @Expose
        public String airtelMobileNum;
        @SerializedName("email")
        @Expose
        public String email;
        @SerializedName("password")
        @Expose
        public String password;
        @SerializedName("type")
        @Expose
        public String type;
        @SerializedName("status")
        @Expose
        public String status;
        @SerializedName("image")
        @Expose
        public String image;
        @SerializedName("otp")
        @Expose
        public String otp;
        @SerializedName("dob")
        @Expose
        public String dob;
        @SerializedName("wallet")
        @Expose
        public String wallet;
        @SerializedName("social_id")
        @Expose
        public String socialId;
        @SerializedName("country_id")
        @Expose
        public String countryId;
        @SerializedName("register_id")
        @Expose
        public String registerId;
        @SerializedName("date_time")
        @Expose
        public String dateTime;
        @SerializedName("lat")
        @Expose
        public String lat;
        @SerializedName("lon")
        @Expose
        public String lon;
        @SerializedName("days")
        @Expose
        public String days;
        @SerializedName("open_time")
        @Expose
        public String openTime;
        @SerializedName("close_time")
        @Expose
        public String closeTime;
        @SerializedName("holidays_date")
        @Expose
        public String holidaysDate;
        @SerializedName("ios_register_id")
        @Expose
        public String iosRegisterId;
        @SerializedName("address")
        @Expose
        public String address;

        @SerializedName("access_token")
        @Expose
        private String accessToken;

        public String getAccessToken() {
            return accessToken;
        }

        public void setAccessToken(String accessToken) {
            this.accessToken = accessToken;
        }



        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getUserName() {
            return userName;
        }

        public void setUserName(String userName) {
            this.userName = userName;
        }

        public String getMobile() {
            return mobile;
        }

        public void setMobile(String mobile) {
            this.mobile = mobile;
        }

        public String getMoovMobileNum() {
            return moovMobileNum;
        }

        public void setMoovMobileNum(String moovMobileNum) {
            this.moovMobileNum = moovMobileNum;
        }

        public String getAirtelMobileNum() {
            return airtelMobileNum;
        }

        public void setAirtelMobileNum(String airtelMobileNum) {
            this.airtelMobileNum = airtelMobileNum;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getImage() {
            return image;
        }

        public void setImage(String image) {
            this.image = image;
        }

        public String getOtp() {
            return otp;
        }

        public void setOtp(String otp) {
            this.otp = otp;
        }

        public String getDob() {
            return dob;
        }

        public void setDob(String dob) {
            this.dob = dob;
        }

        public String getWallet() {
            return wallet;
        }

        public void setWallet(String wallet) {
            this.wallet = wallet;
        }

        public String getSocialId() {
            return socialId;
        }

        public void setSocialId(String socialId) {
            this.socialId = socialId;
        }

        public String getCountryId() {
            return countryId;
        }

        public void setCountryId(String countryId) {
            this.countryId = countryId;
        }

        public String getRegisterId() {
            return registerId;
        }

        public void setRegisterId(String registerId) {
            this.registerId = registerId;
        }

        public String getDateTime() {
            return dateTime;
        }

        public void setDateTime(String dateTime) {
            this.dateTime = dateTime;
        }

        public String getLat() {
            return lat;
        }

        public void setLat(String lat) {
            this.lat = lat;
        }

        public String getLon() {
            return lon;
        }

        public void setLon(String lon) {
            this.lon = lon;
        }

        public String getDays() {
            return days;
        }

        public void setDays(String days) {
            this.days = days;
        }

        public String getOpenTime() {
            return openTime;
        }

        public void setOpenTime(String openTime) {
            this.openTime = openTime;
        }

        public String getCloseTime() {
            return closeTime;
        }

        public void setCloseTime(String closeTime) {
            this.closeTime = closeTime;
        }

        public String getHolidaysDate() {
            return holidaysDate;
        }

        public void setHolidaysDate(String holidaysDate) {
            this.holidaysDate = holidaysDate;
        }

        public String getIosRegisterId() {
            return iosRegisterId;
        }

        public void setIosRegisterId(String iosRegisterId) {
            this.iosRegisterId = iosRegisterId;
        }

        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }

    }
}