package com.my.afarycode.OnlineShopping.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class CategoryModal {

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

        @SerializedName("icon")
        @Expose
        public String icon;
        @SerializedName("icon_1")
        @Expose
        public String icon1;

        @SerializedName("id")
        @Expose
        public String id;
        @SerializedName("name")
        @Expose
        public String categoryName;

        @SerializedName("name_fr")
        @Expose
        public String nameFr;

        public String getIcon() {
            return icon;
        }

        public void setIcon(String icon) {
            this.icon = icon;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getCategoryName() {
            return categoryName;
        }

        public void setCategoryName(String categoryName) {
            this.categoryName = categoryName;
        }

        public String getIcon1() {
            return icon1;
        }

        public void setIcon1(String icon1) {
            this.icon1 = icon1;
        }

        public String getNameFr() {
            return nameFr;
        }

        public void setNameFr(String nameFr) {
            this.nameFr = nameFr;
        }
    }

}