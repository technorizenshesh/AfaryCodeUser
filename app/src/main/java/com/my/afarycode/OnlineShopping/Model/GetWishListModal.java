package com.my.afarycode.OnlineShopping.Model;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class GetWishListModal {

    @SerializedName("result")
    @Expose
    private List<Result> result;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("total_amount")
    @Expose
    private Object totalAmount;
    @SerializedName("platform_fees")
    @Expose
    private String platformFees;
    @SerializedName("tax_n1")
    @Expose
    private String taxN1;
    @SerializedName("tax_n2")
    @Expose
    private String taxN2;

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

    public Object getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(Object totalAmount) {
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
        private String id;
        @SerializedName("product_id")
        @Expose
        private String productId;
        @SerializedName("user_id")
        @Expose
        private String userId;
        @SerializedName("date_time")
        @Expose
        private String dateTime;
        @SerializedName("product_details")
        @Expose
        private ProductDetails productDetails;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getProductId() {
            return productId;
        }

        public void setProductId(String productId) {
            this.productId = productId;
        }

        public String getUserId() {
            return userId;
        }

        public void setUserId(String userId) {
            this.userId = userId;
        }

        public String getDateTime() {
            return dateTime;
        }

        public void setDateTime(String dateTime) {
            this.dateTime = dateTime;
        }

        public ProductDetails getProductDetails() {
            return productDetails;
        }

        public void setProductDetails(ProductDetails productDetails) {
            this.productDetails = productDetails;
        }


        public class ProductDetails {

            @SerializedName("pro_id")
            @Expose
            private String proId;
            @SerializedName("cat_id")
            @Expose
            private String catId;
            @SerializedName("category_id")
            @Expose
            private String categoryId;
            @SerializedName("sub_cat")
            @Expose
            private String subCat;
            @SerializedName("restaurant_id")
            @Expose
            private String restaurantId;
            @SerializedName("seller_id")
            @Expose
            private String sellerId;
            @SerializedName("product_name")
            @Expose
            private String productName;
            @SerializedName("product_images")
            @Expose
            private String productImages;
            @SerializedName("image_1")
            @Expose
            private String image1;
            @SerializedName("image_2")
            @Expose
            private String image2;
            @SerializedName("image_3")
            @Expose
            private String image3;
            @SerializedName("product_details")
            @Expose
            private String productDetails;
            @SerializedName("addtional_detial")
            @Expose
            private String addtionalDetial;
            @SerializedName("Product_brand")
            @Expose
            private String productBrand;
            @SerializedName("product_qut")
            @Expose
            private String productQut;
            @SerializedName("product_price")
            @Expose
            private String productPrice;
            @SerializedName("pro_off")
            @Expose
            private String proOff;
            @SerializedName("interested")
            @Expose
            private String interested;
            @SerializedName("reviews")
            @Expose
            private String reviews;
            @SerializedName("sku")
            @Expose
            private String sku;
            @SerializedName("size")
            @Expose
            private String size;
            @SerializedName("stock")
            @Expose
            private String stock;
            @SerializedName("tax_n1")
            @Expose
            private String taxN1;
            @SerializedName("tax_n2")
            @Expose
            private String taxN2;
            @SerializedName("product_color")
            @Expose
            private String productColor;
            @SerializedName("status")
            @Expose
            private String status;
            @SerializedName("status_block")
            @Expose
            private String statusBlock;
            @SerializedName("block_resion")
            @Expose
            private String blockResion;
            @SerializedName("national_international")
            @Expose
            private String nationalInternational;
            @SerializedName("delivery_charges")
            @Expose
            private String deliveryCharges;
            @SerializedName("date")
            @Expose
            private String date;
            @SerializedName("cat_new")
            @Expose
            private String catNew;
            @SerializedName("new_qut")
            @Expose
            private String newQut;
            @SerializedName("notif")
            @Expose
            private String notif;
            @SerializedName("approval")
            @Expose
            private String approval;
            @SerializedName("country_id")
            @Expose
            private String countryId;
            @SerializedName("delete_status")
            @Expose
            private String deleteStatus;

            @SerializedName("currency")
            @Expose
            private String currency;

            public String getCurrency() {
                return currency;
            }

            public void setCurrency(String currency) {
                this.currency = currency;
            }

            public String getProId() {
                return proId;
            }

            public void setProId(String proId) {
                this.proId = proId;
            }

            public String getCatId() {
                return catId;
            }

            public void setCatId(String catId) {
                this.catId = catId;
            }

            public String getCategoryId() {
                return categoryId;
            }

            public void setCategoryId(String categoryId) {
                this.categoryId = categoryId;
            }

            public String getSubCat() {
                return subCat;
            }

            public void setSubCat(String subCat) {
                this.subCat = subCat;
            }

            public String getRestaurantId() {
                return restaurantId;
            }

            public void setRestaurantId(String restaurantId) {
                this.restaurantId = restaurantId;
            }

            public String getSellerId() {
                return sellerId;
            }

            public void setSellerId(String sellerId) {
                this.sellerId = sellerId;
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

            public String getProductDetails() {
                return productDetails;
            }

            public void setProductDetails(String productDetails) {
                this.productDetails = productDetails;
            }

            public String getAddtionalDetial() {
                return addtionalDetial;
            }

            public void setAddtionalDetial(String addtionalDetial) {
                this.addtionalDetial = addtionalDetial;
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

            public String getProOff() {
                return proOff;
            }

            public void setProOff(String proOff) {
                this.proOff = proOff;
            }

            public String getInterested() {
                return interested;
            }

            public void setInterested(String interested) {
                this.interested = interested;
            }

            public String getReviews() {
                return reviews;
            }

            public void setReviews(String reviews) {
                this.reviews = reviews;
            }

            public String getSku() {
                return sku;
            }

            public void setSku(String sku) {
                this.sku = sku;
            }

            public String getSize() {
                return size;
            }

            public void setSize(String size) {
                this.size = size;
            }

            public String getStock() {
                return stock;
            }

            public void setStock(String stock) {
                this.stock = stock;
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

            public String getStatusBlock() {
                return statusBlock;
            }

            public void setStatusBlock(String statusBlock) {
                this.statusBlock = statusBlock;
            }

            public String getBlockResion() {
                return blockResion;
            }

            public void setBlockResion(String blockResion) {
                this.blockResion = blockResion;
            }

            public String getNationalInternational() {
                return nationalInternational;
            }

            public void setNationalInternational(String nationalInternational) {
                this.nationalInternational = nationalInternational;
            }

            public String getDeliveryCharges() {
                return deliveryCharges;
            }

            public void setDeliveryCharges(String deliveryCharges) {
                this.deliveryCharges = deliveryCharges;
            }

            public String getDate() {
                return date;
            }

            public void setDate(String date) {
                this.date = date;
            }

            public String getCatNew() {
                return catNew;
            }

            public void setCatNew(String catNew) {
                this.catNew = catNew;
            }

            public String getNewQut() {
                return newQut;
            }

            public void setNewQut(String newQut) {
                this.newQut = newQut;
            }

            public String getNotif() {
                return notif;
            }

            public void setNotif(String notif) {
                this.notif = notif;
            }

            public String getApproval() {
                return approval;
            }

            public void setApproval(String approval) {
                this.approval = approval;
            }

            public String getCountryId() {
                return countryId;
            }

            public void setCountryId(String countryId) {
                this.countryId = countryId;
            }

            public String getDeleteStatus() {
                return deleteStatus;
            }

            public void setDeleteStatus(String deleteStatus) {
                this.deleteStatus = deleteStatus;
            }

        }

    }

}



