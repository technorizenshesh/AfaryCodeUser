package com.my.afarycode.OnlineShopping.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;



public class CartModal {

    @SerializedName("result")
    @Expose
    public List<Result> result = null;
    @SerializedName("message")
    @Expose
    public String message;
    @SerializedName("status")
    @Expose
    public String status;
    @SerializedName("total_amount")
    @Expose
    public Integer totalAmount;
    @SerializedName("platform_fees")
    @Expose
    public String platformFees;
    @SerializedName("tax_n1")
    @Expose
    public String taxN1;
    @SerializedName("tax_n2")
    @Expose
    public String taxN2;

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

    public Integer getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(Integer totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getPlatformFees() {
        return platformFees;
    }

    public void setPlatformFees(String platformFees) {
        this.platformFees = platformFees;
    }

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
    public class Result {

        @SerializedName("id")
        @Expose
        public String id;

        @SerializedName("pro_id")
        @Expose
        public String proId;
        @SerializedName("category_id")
        @Expose
        public String categoryId;
        @SerializedName("restaurant_id")
        @Expose
        public String restaurantId;
        @SerializedName("product_name")
        @Expose
        public String productName;
        @SerializedName("product_images")
        @Expose
        public String productImages;
        @SerializedName("product_details")
        @Expose
        public String productDetails;
        @SerializedName("Product_brand")
        @Expose
        public String productBrand;
        @SerializedName("product_qut")
        @Expose
        public String productQut;
        @SerializedName("product_price")
        @Expose
        public String productPrice;
        @SerializedName("platform_fees")
        @Expose
        public String platformFees;
        @SerializedName("tax_n1")
        @Expose
        public String taxN1;
        @SerializedName("tax_n2")
        @Expose
        public String taxN2;
        @SerializedName("product_color")
        @Expose
        public String productColor;
        @SerializedName("status")
        @Expose
        public String status;
        @SerializedName("date")
        @Expose
        public String date;
        @SerializedName("total_amount")
        @Expose
        public Integer totalAmount;
        @SerializedName("quantity")
        @Expose
        public String quantity;
        @SerializedName("item_id")
        @Expose
        public String itemId;
        @SerializedName("shop_id")
        @Expose
        public String shopId;
        @SerializedName("cart_id")
        @Expose
        public String cartId;
        @SerializedName("product_image")
        @Expose
        public String productImage;

        @SerializedName("item_amount")
        @Expose
        public String itemAmount;

        public String getItemAmount() {
            return itemAmount;
        }

        public void setItemAmount(String itemAmount) {
            this.itemAmount = itemAmount;
        }

        @SerializedName("wish_list")
        @Expose
        public String wishList;

        public String getWishList() {
            return wishList;
        }

        @SerializedName("delivery_charges")
        @Expose
        private String deliveryCharges;

        public String getDeliveryCharges() {
            return deliveryCharges;
        }

        public void setDeliveryCharges(String deliveryCharges) {
            this.deliveryCharges = deliveryCharges;
        }

        public void setWishList(String wishList) {
            this.wishList = wishList;
        }

        public String getProId() {
            return proId;
        }

        public void setProId(String proId) {
            this.proId = proId;
        }

        public String getCategoryId() {
            return categoryId;
        }

        public void setCategoryId(String categoryId) {
            this.categoryId = categoryId;
        }

        public String getRestaurantId() {
            return restaurantId;
        }

        public void setRestaurantId(String restaurantId) {
            this.restaurantId = restaurantId;
        }

        public String getProductName() {
            return productName;
        }

        public void setProductName(String productName) {
            this.productName = productName;
        }

        public String getProductImages() {
            return productImages;
        }

        public void setProductImages(String productImages) {
            this.productImages = productImages;
        }

        public String getProductDetails() {
            return productDetails;
        }

        public void setProductDetails(String productDetails) {
            this.productDetails = productDetails;
        }

        public String getProductBrand() {
            return productBrand;
        }

        public void setProductBrand(String productBrand) {
            this.productBrand = productBrand;
        }

        public String getProductQut() {
            return productQut;
        }

        public void setProductQut(String productQut) {
            this.productQut = productQut;
        }

        public String getProductPrice() {
            return productPrice;
        }

        public void setProductPrice(String productPrice) {
            this.productPrice = productPrice;
        }

        public String getPlatformFees() {
            return platformFees;
        }

        public void setPlatformFees(String platformFees) {
            this.platformFees = platformFees;
        }

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

        public String getProductColor() {
            return productColor;
        }

        public void setProductColor(String productColor) {
            this.productColor = productColor;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getDate() {
            return date;
        }

        public void setDate(String date) {
            this.date = date;
        }

        public Integer getTotalAmount() {
            return totalAmount;
        }

        public void setTotalAmount(Integer totalAmount) {
            this.totalAmount = totalAmount;
        }

        public String getQuantity() {
            return quantity;
        }

        public void setQuantity(String quantity) {
            this.quantity = quantity;
        }

        public String getItemId() {
            return itemId;
        }

        public void setItemId(String itemId) {
            this.itemId = itemId;
        }

        public String getShopId() {
            return shopId;
        }

        public void setShopId(String shopId) {
            this.shopId = shopId;
        }

        public String getCartId() {
            return cartId;
        }

        public void setCartId(String cartId) {
            this.cartId = cartId;
        }

        public String getProductImage() {
            return productImage;
        }

        public void setProductImage(String productImage) {
            this.productImage = productImage;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }
    }
}