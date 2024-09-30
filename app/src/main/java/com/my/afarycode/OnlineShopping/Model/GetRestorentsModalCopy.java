package com.my.afarycode.OnlineShopping.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class GetRestorentsModalCopy {

    @SerializedName("result")
    @Expose
    private List<GetRestorentsModalCopy.Result> result;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("status")
    @Expose
    private String status;

    public List<GetRestorentsModalCopy.Result> getResult() {
        return result;
    }

    public void setResult(List<GetRestorentsModalCopy.Result> result) {
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

        @SerializedName("restaurant_id")
        @Expose
        private String id;

        @SerializedName("product_name")
        @Expose
        private String name;
        @SerializedName("product_images")
        @Expose
        private String image1;

        @SerializedName("country_name")
        @Expose
        private String countryName;

        @SerializedName("state_name")
        @Expose
        private String stateName;

        @SerializedName("city_name")
        @Expose
        private String cityName;

        @SerializedName("shop_details")
        @Expose
        private ShopDetails shopDetails;



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

        public String getImage1() {
            return image1;
        }

        public void setImage1(String image1) {
            this.image1 = image1;
        }

        public String getCountryName() {
            return countryName;
        }

        public void setCountryName(String countryName) {
            this.countryName = countryName;
        }

        public String getStateName() {
            return stateName;
        }

        public void setStateName(String stateName) {
            this.stateName = stateName;
        }

        public String getCityName() {
            return cityName;
        }

        public void setCityName(String cityName) {
            this.cityName = cityName;
        }

        public ShopDetails getShopDetails() {
            return shopDetails;
        }

        public void setShopDetails(ShopDetails shopDetails) {
            this.shopDetails = shopDetails;
        }

        public class ShopDetails {


            @SerializedName("id")
            @Expose
            private String id;

            @SerializedName("name")
            @Expose
            private String name;

            @SerializedName("image_1")
            @Expose
            private String image1;
            @SerializedName("image2")
            @Expose
            private String image2;

            @SerializedName("image_3")
            @Expose
            private String image3;

            @SerializedName("address")
            @Expose
            private String address;

            @SerializedName("rating")
            @Expose
            private String rating;

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

            public String getImage1() {
                return image1;
            }

            public void setImage1(String image1) {
                this.image1 = image1;
            }

            public String getImage2() {
                return image2;
            }

            public void setImage2(String image2) {
                this.image2 = image2;
            }

            public String getImage3() {
                return image3;
            }

            public void setImage3(String image3) {
                this.image3 = image3;
            }

            public String getAddress() {
                return address;
            }

            public void setAddress(String address) {
                this.address = address;
            }

            public String getRating() {
                return rating;
            }

            public void setRating(String rating) {
                this.rating = rating;
            }
        }

    }

}



