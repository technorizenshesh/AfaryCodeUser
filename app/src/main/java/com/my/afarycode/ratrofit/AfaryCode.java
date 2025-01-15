package com.my.afarycode.ratrofit;

import com.my.afarycode.OnlineShopping.Model.AddAvailable;
import com.my.afarycode.OnlineShopping.Model.AddWalletModal;
import com.my.afarycode.OnlineShopping.Model.Add_Address_Modal;
import com.my.afarycode.OnlineShopping.Model.Add_To_Cart_Modal;
import com.my.afarycode.OnlineShopping.Model.Add_Wish_To_Cart_Modal;
import com.my.afarycode.OnlineShopping.Model.Add_to_Wish;
import com.my.afarycode.OnlineShopping.Model.BannerModal1;
import com.my.afarycode.OnlineShopping.Model.CartModal;
import com.my.afarycode.OnlineShopping.Model.CategoryModal;
import com.my.afarycode.OnlineShopping.Model.CheckOtp;
import com.my.afarycode.OnlineShopping.Model.DeleteCartModal;
import com.my.afarycode.OnlineShopping.Model.ForgotPasswordModal;
import com.my.afarycode.OnlineShopping.Model.GetNotificationModal;
import com.my.afarycode.OnlineShopping.Model.GetProfileModal;
import com.my.afarycode.OnlineShopping.Model.GetRestorentsModal;
import com.my.afarycode.OnlineShopping.Model.GetShopingCategoryModal;
import com.my.afarycode.OnlineShopping.Model.GetTransferDetails;
import com.my.afarycode.OnlineShopping.Model.GetViewModal;
import com.my.afarycode.OnlineShopping.Model.GetWishListModal;
import com.my.afarycode.OnlineShopping.Model.Get_Transaction_Modal;
import com.my.afarycode.OnlineShopping.Model.LoginModel;
import com.my.afarycode.OnlineShopping.Model.PaymentModal;
import com.my.afarycode.OnlineShopping.Model.ProductItemModel;
import com.my.afarycode.OnlineShopping.Model.ReviewModal;
import com.my.afarycode.OnlineShopping.Model.ShoppingProductModal;
import com.my.afarycode.OnlineShopping.Model.ShoppingStoreDetailsModal;
import com.my.afarycode.OnlineShopping.Model.SignupModel;
import com.my.afarycode.OnlineShopping.Model.TransferMoneyModal;
import com.my.afarycode.OnlineShopping.Model.UpdateCartModal;
import com.my.afarycode.OnlineShopping.Model.UpdateProfileModal;
import com.my.afarycode.OnlineShopping.Model.WithDrawalMoney;

import java.util.Map;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.HeaderMap;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;

public interface AfaryCode {
    @FormUrlEncoded
    @POST("signup")
    Call<SignupModel> signup(@FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST("add_address")
    Call<Add_Address_Modal> add_address(@HeaderMap Map<String, String> auth, @FieldMap Map<String, String> params);


    @FormUrlEncoded
    @POST("add_money")
    Call<ResponseBody> add_money(@HeaderMap Map<String, String> auth, @FieldMap Map<String, String> params);


    @FormUrlEncoded
    @POST("add_money_card")
    Call<ResponseBody> addMoneyToWalletFromCard(@HeaderMap Map<String, String> auth, @FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST("transfer_money_wllet_to_wallet")
    Call<ResponseBody> transfer_money(@HeaderMap Map<String, String> auth, @FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST("submit_withdrawal_request")
    Call<ResponseBody> withdraw_money(@HeaderMap Map<String, String> auth, @FieldMap Map<String, String> params);

    //  login
    @FormUrlEncoded
    @POST("user_login")
    Call<ResponseBody> login(@FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST("check_otp")
    Call<CheckOtp> check_otp(@FieldMap Map<String, String> params);


    @FormUrlEncoded
    @POST("runPvit")
    Call<ResponseBody> payment(@HeaderMap Map<String, String> auth, @FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST("runPvitWebView")
    Call<ResponseBody> cardPaymentApi(@HeaderMap Map<String, String> auth, @FieldMap Map<String, String> params);


    @FormUrlEncoded
    @POST("runPvit_transfer_money")
    Call<ResponseBody> transferNumberMoney(@HeaderMap Map<String, String> auth, @FieldMap Map<String, String> params);


    @FormUrlEncoded
    @POST("runPvit_transfer_money")
    Call<ResponseBody> transferMoneyToWalletFromCard(@HeaderMap Map<String, String> auth, @FieldMap Map<String, String> params);


    @FormUrlEncoded
    @POST("change_password")
    Call<LoginModel> change_password(@FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST("forgot_password")
    Call<ForgotPasswordModal> forgot_password(@FieldMap Map<String, String> params);


 /* @FormUrlEncoded
  @POST("send_otp")
  Call<ResponseBody> sendOtpApi1(@FieldMap Map<String, String> params);

  @FormUrlEncoded
  @POST("verify_mobile_otp")
  Call<ResponseBody> verifyOtpApi1(@FieldMap Map<String, String> params);

*/

    @FormUrlEncoded
    @POST("send_otp_before_signup")
    Call<ResponseBody> sendOtpApi(@FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST("send_otp_before_signup_whatsup")
    Call<ResponseBody> sendWhatsAppOtpApi(@FieldMap Map<String, String> params);


    @FormUrlEncoded
    @POST("verify_mobile_otp_before_signup")
    Call<ResponseBody> verifyOtpApi(@FieldMap Map<String, String> params);


    @FormUrlEncoded
    @POST("forgot_password")
    Call<ResponseBody> forgot_passwordNew(@FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST("get_profile")
    Call<GetProfileModal> get_profile(@FieldMap Map<String, String> params);

   /* @FormUrlEncoded
    @POST("get_skills_client")
    Call<CategoryModal> get_category( @HeaderMap Map<String, String> auth,@FieldMap Map<String, String> params);*/

    @FormUrlEncoded
    @POST("get_home_category")
    Call<CategoryModal> get_category(@HeaderMap Map<String, String> auth, @FieldMap Map<String, String> params);


    @FormUrlEncoded
    @POST("get_subcat_users")
    Call<GetShopingCategoryModal> get_shopping_category(@HeaderMap Map<String, String> auth, @FieldMap Map<String, String> params);

/*    @POST("Login")
    Call<Object> logggg(@HeaderMap Map<String,String> header, @Body RequestBody body);*/

    @FormUrlEncoded
    @POST("get_shop_by_product")
    Call<ResponseBody> get_restaurant(@HeaderMap Map<String, String> auth, @FieldMap Map<String, String> params);


    @FormUrlEncoded
    @POST("get_shop_by_product_name")
    Call<ResponseBody> get_shop_by_product_name(@HeaderMap Map<String, String> auth, @FieldMap Map<String, String> params);



    @FormUrlEncoded
    @POST("add_rating")
    Call<ReviewModal> add_rating(@HeaderMap Map<String, String> auth, @FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST("get_restaurant_detail")
    Call<ShoppingStoreDetailsModal> get_restaurant_detail(@HeaderMap Map<String, String> auth, @FieldMap Map<String, String> params);


    @FormUrlEncoded
    @POST("get_wishlist")
    Call<ResponseBody> get_wish_list(@HeaderMap Map<String, String> auth, @FieldMap Map<String, String> params);


    @FormUrlEncoded
    @POST("get_notification_user")
    Call<ResponseBody> notifi_list(@HeaderMap Map<String, String> auth, @FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST("notifi_count_view")
    Call<GetViewModal> notifi_count_view(@HeaderMap Map<String, String> auth, @FieldMap Map<String, String> params);


    @FormUrlEncoded
    @POST("get_cart")
    Call<CartModal> get_cart(@HeaderMap Map<String, String> auth, @FieldMap Map<String, String> params);


    @FormUrlEncoded
    @POST("get_transaction")
    Call<Get_Transaction_Modal> get_transaction(@FieldMap Map<String, String> params);


    @FormUrlEncoded
    @POST("get_order_users")
    Call<ResponseBody> getAllOnlineOrderApi(@HeaderMap Map<String, String> auth, @FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST("get_order_detial_users")
    Call<ResponseBody> getDetailOnlineOrderApi(@HeaderMap Map<String, String> auth, @FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST("get_history_users")
    Call<ResponseBody> getHistoryOnlineOrderApi(@HeaderMap Map<String, String> auth, @FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST("update_cart")
    Call<UpdateCartModal> update_cart(@HeaderMap Map<String, String> auth, @FieldMap Map<String, String> params);


    @FormUrlEncoded
    @POST("delete_cart_data")
    Call<DeleteCartModal> delete_cart(@HeaderMap Map<String, String> auth, @FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST("wishlist_to_cart")
    Call<ResponseBody> wishlist_to_cart(@HeaderMap Map<String, String> auth, @FieldMap Map<String, String> params);


    @FormUrlEncoded
    @POST("get_wallet_history")
    Call<ResponseBody> get_transfer_money(@HeaderMap Map<String, String> auth, @FieldMap Map<String, String> params);


    @FormUrlEncoded
    @POST("available_balance")
    Call<AddAvailable> get_available(@HeaderMap Map<String, String> auth, @FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST("add_to_cart")
    Call<ResponseBody> add_to_cart(@HeaderMap Map<String, String> auth, @FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST("get_product_detail")
    Call<ShoppingProductModal> get_product_detail(@HeaderMap Map<String, String> auth, @FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST("get_slider")
    Call<BannerModal1> get_slider(@HeaderMap Map<String, String> auth, @FieldMap Map<String, String> params);


    @Multipart
    @POST("update_profile")
    Call<UpdateProfileModal> update_profile(
            @HeaderMap Map<String, String> auth,
            @Part("user_id") RequestBody user_id,
            @Part("name") RequestBody name,
            @Part("user_name") RequestBody user_name,
            @Part("email") RequestBody email,
            @Part("mobile") RequestBody title,
            @Part("address") RequestBody address,
            @Part("register_id") RequestBody register_id,

            @Part MultipartBody.Part file);


    @FormUrlEncoded
    @POST("get_item_search")
    Call<ResponseBody> getProductSearch(@HeaderMap Map<String, String> auth, @FieldMap Map<String, String> params);


    @FormUrlEncoded
    @POST("get_item")
    Call<ResponseBody> getProductItem(@HeaderMap Map<String, String> auth, @FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST("get_item")
    Call<ResponseBody> getProductBySearchIdItem(@HeaderMap Map<String, String> auth, @FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST("get_item_search")
    Call<ResponseBody> searchProduct(@HeaderMap Map<String, String> auth, @FieldMap Map<String, String> params);


    @FormUrlEncoded
    @POST("get_search_suggestions")
    Call<ResponseBody> searchSuggestionProduct(@HeaderMap Map<String, String> auth, @FieldMap Map<String, String> params);


    @FormUrlEncoded
    @POST("get_product_users")
    Call<ResponseBody> getAllProduct(@HeaderMap Map<String, String> auth, @FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST("get_product_by_category_location")
    Call<ResponseBody> getAllProductCatCountry(@HeaderMap Map<String, String> auth, @FieldMap Map<String, String> params);


    //////////////
    @FormUrlEncoded
    @POST("get_home_title")
    Call<ResponseBody> getHomeTitle(@HeaderMap Map<String, String> auth, @FieldMap Map<String, String> param);

    @POST("get_country")
    Call<ResponseBody> getAllCountry(@HeaderMap Map<String, String> auth);


    @FormUrlEncoded
    @POST("add_address")
    Call<ResponseBody> addAddress(@HeaderMap Map<String, String> auth, @FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST("get_address")
    Call<ResponseBody> getAddress(@HeaderMap Map<String, String> auth, @FieldMap Map<String, String> params);


    @FormUrlEncoded
    @POST("update_address")
    Call<ResponseBody> updateAddress(@HeaderMap Map<String, String> auth, @FieldMap Map<String, String> params);


    @FormUrlEncoded
    @POST("delete_address")
    Call<ResponseBody> deleteAddres(@HeaderMap Map<String, String> auth, @FieldMap Map<String, String> params);


    @FormUrlEncoded
    @POST("get_tax_km")
    Call<ResponseBody> getAllTax(@HeaderMap Map<String, String> auth, @FieldMap Map<String, String> params);


    @FormUrlEncoded
    @POST("get_tax_delivery_chareges")
    Call<ResponseBody> getAllTaxNew(@HeaderMap Map<String, String> auth, @FieldMap Map<String, String> params);


    @FormUrlEncoded
    @POST("insert_chat")
    Call<ResponseBody> sendNotification(@HeaderMap Map<String, String> auth, @FieldMap Map<String, String> params);


    @FormUrlEncoded
    @POST("update_country")
    Call<ResponseBody> updateCountryApi(@HeaderMap Map<String, String> auth, @FieldMap Map<String, String> params);


    @GET("privacy_policy")
    Call<ResponseBody> getPrivacyPolicy();

    @GET("terms_condition")
    Call<ResponseBody> getTermsConditions();

    @GET("terms_condition_for_sale")
    Call<ResponseBody> getUseConditions();


    @GET("get_address_category")
    Call<ResponseBody> getDelivery(@HeaderMap Map<String, String> auth, @Query("register_id") String registerId, @Query("user_id") String user_id);

    @FormUrlEncoded
    @POST("get_seller_shop_detail")
        // get_product
    Call<ResponseBody> get_shop_detail(@HeaderMap Map<String, String> auth, @FieldMap Map<String, String> params);


    @FormUrlEncoded
    @POST("add_to_wishlist")
    Call<ResponseBody> addToFavApi(@HeaderMap Map<String, String> auth, @FieldMap Map<String, String> params);


    @FormUrlEncoded
    @POST("get_delivery_partner")
    Call<ResponseBody> getDeliveryAgencyApi(@HeaderMap Map<String, String> auth, @FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST("payment_processing")
    Call<ResponseBody> checkPaymentStatusApi(@HeaderMap Map<String, String> auth, @FieldMap Map<String, String> params);


    @FormUrlEncoded
    @POST("order_cancel_by_user")
    Call<ResponseBody> cancelOrderByUserApi(@HeaderMap Map<String, String> auth, @FieldMap Map<String, String> params);


    @FormUrlEncoded
    @POST("logout")
    Call<ResponseBody> logoutApi(@FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST("insert_tax_delivery_chareges")
    Call<ResponseBody> sendTaxTServerApi(@HeaderMap Map<String, String> auth, @FieldMap Map<String, String> params);


    @FormUrlEncoded
    @POST("update_withdrawal_request")
    Call<ResponseBody> withdraw_money_update(@HeaderMap Map<String, String> auth, @FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST("yes_transfer_money_wllet_to_wallet")
    Call<ResponseBody> transfer_moneyFirstApi(@HeaderMap Map<String, String> auth, @FieldMap Map<String, String> params);


    @FormUrlEncoded
    @POST("money_transfer_request")
    Call<ResponseBody> transfer_money_request(@HeaderMap Map<String, String> auth, @FieldMap Map<String, String> params);


    @FormUrlEncoded
    @POST("send_money_transfer_request")
    Call<ResponseBody> transferMoneyRequestSend(@HeaderMap Map<String, String> auth, @FieldMap Map<String, String> params);


    @FormUrlEncoded
    @POST("get_driver_location")
    Call<ResponseBody> getDriverLocationApi(@HeaderMap Map<String, String> auth, @FieldMap Map<String, String> paramHashMap);


    @FormUrlEncoded
    @POST("add_product_review_rating")
    Call<ResponseBody> giveRateReviewApi(@HeaderMap Map<String, String> auth, @FieldMap Map<String, String> params);


    @FormUrlEncoded
    @POST("check_product_availability")
    Call<ResponseBody> checkProductAvailabilityApi(@HeaderMap Map<String, String> auth, @FieldMap Map<String, String> params);


    @FormUrlEncoded
    @POST("delete_user_order")
    Call<ResponseBody> deleteItemByUserApi(@HeaderMap Map<String, String> auth, @FieldMap Map<String, String> params);


    @FormUrlEncoded
    @POST("get_sellers")
    Call<ResponseBody> getSellerChatListApi(@HeaderMap Map<String, String> auth, @FieldMap Map<String, String> params);


    @FormUrlEncoded
    @POST("password_reset")
    Call<ResponseBody> sendAdminRequest(@FieldMap Map<String, String> params);


    @FormUrlEncoded
    @POST("change_password_user")
    Call<ResponseBody> changePasswordRepo(@HeaderMap Map<String, String> auth, @FieldMap Map<String, String> params);


    @FormUrlEncoded
    @POST("update_language")
    Call<ResponseBody> updateLanguageApi(@FieldMap Map<String, String> params);

    @GET("get_register_description")
    Call<ResponseBody> getTitleDes();


    @FormUrlEncoded
    @POST("get_notification_count_user")
    Call<ResponseBody> getNotificationCounterApi(@HeaderMap Map<String, String> auth, @FieldMap Map<String, String> params);


    @FormUrlEncoded
    @POST("")
    Call<ResponseBody> sendNumberOnServerApi(@FieldMap Map<String, String> params);


    @FormUrlEncoded
    @POST("check_user_exist_or_not_to_send_invoice")
    Call<ResponseBody> checkUserExitApi(@HeaderMap Map<String, String> auth, @FieldMap Map<String, String> params);


    @FormUrlEncoded
    @POST("generate_invoice")
    Call<ResponseBody> sendInvoiceAnotherApi(@HeaderMap Map<String, String> auth, @FieldMap Map<String, String> params);


    @FormUrlEncoded
    @POST("invoice_details")
    Call<ResponseBody> getInvoiceDataApi(@HeaderMap Map<String, String> auth, @FieldMap Map<String, String> params);


    @FormUrlEncoded
    @POST("send_message_to_admin")
    Call<ResponseBody> sendAdminMsg(@HeaderMap Map<String, String> auth, @FieldMap Map<String, String> params);


    @FormUrlEncoded
    @POST("reorder")
    Call<ResponseBody> reOrder(@HeaderMap Map<String, String> auth, @FieldMap Map<String, String> params);


    @FormUrlEncoded
    @POST("generate_invoice_recharge_wallet")
    Call<ResponseBody> sendWalletRechargeInvoiceAnotherApi(@HeaderMap Map<String, String> auth, @FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST("invoice_details_recharge_wallet")
    Call<ResponseBody> getWalletInvoiceDataApi(@HeaderMap Map<String, String> auth, @FieldMap Map<String, String> params);


    @FormUrlEncoded
    @POST("get_check_delivery_availability")
    Call<ResponseBody> getDeliveryAvailabilityApi(@HeaderMap Map<String, String> auth, @FieldMap Map<String, String> params);


    @FormUrlEncoded
    @POST("get_item_search_new")
    Call<ResponseBody> getProductSearchNew(@HeaderMap Map<String, String> auth, @FieldMap Map<String, String> params);


    @FormUrlEncoded
    @POST("update_mobile_number_on_order")
    Call<ResponseBody> changeUserNumberApi(@HeaderMap Map<String, String> auth, @FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST("update_payment_status_try_again")
    Call<ResponseBody> sendPaymentFailedInfoToServerApi(@HeaderMap Map<String, String> auth, @FieldMap Map<String, String> params);


    @FormUrlEncoded
    @POST("delivery_cancel_order")
    Call<ResponseBody> deliveryPersonInformOrderCancelApi( @FieldMap Map<String, String> params);





}
