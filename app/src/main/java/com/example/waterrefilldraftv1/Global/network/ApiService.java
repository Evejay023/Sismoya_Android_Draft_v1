package com.example.waterrefilldraftv1.Global.network;

import com.example.waterrefilldraftv1.Customer.models.AddressResponse;
import com.example.waterrefilldraftv1.Customer.models.OrderRequest;
import com.example.waterrefilldraftv1.Customer.models.OrderStats;
import com.example.waterrefilldraftv1.Customer.models.OrderOverview;
import com.example.waterrefilldraftv1.Customer.models.RegisterRequest;
import com.example.waterrefilldraftv1.Login_Customer_and_Riders.LoginRequest;
import com.example.waterrefilldraftv1.Customer.models.LoginResponse;
import com.example.waterrefilldraftv1.Customer.models.ForgotPasswordRequest;
import com.example.waterrefilldraftv1.Customer.models.VerifyCodeRequest;
import com.example.waterrefilldraftv1.Login_Customer_and_Riders_model.ResetPasswordRequest;
import com.example.waterrefilldraftv1.Customer.models.User;
import com.example.waterrefilldraftv1.Customer.models.Address;
import com.example.waterrefilldraftv1.Customer.models.ProductDto;
import com.example.waterrefilldraftv1.Customer.models.CartItem;
import com.example.waterrefilldraftv1.Customer.models.ServerCartItem;
import com.example.waterrefilldraftv1.Riders.models.CompletedOrderModel;
import com.example.waterrefilldraftv1.Riders.models.PickupOrder;
import com.example.waterrefilldraftv1.Riders.models.RiderOrdersResponse;

import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.Url;

public interface ApiService {

    // ======================================================
    // ✅ ACCOUNT / PROFILE MANAGEMENT
    // ======================================================

    @GET("profile")
    Call<ApiResponse> getProfile(@Header("Authorization") String token);

    @PUT("update-profile")
    Call<ApiResponse> updateProfile(@Header("Authorization") String token, @Body User user);

    @PUT("update-profile")
    Call<ApiResponse> updateProfilePartial(@Header("Authorization") String token, @Body Map<String, String> body);

    @PUT("change-password")
    Call<ApiResponse> changePassword(@Header("Authorization") String token, @Body Map<String, String> body);


    // ======================================================
    // ✅ AUTHENTICATION
    // ======================================================

    @POST("login")
    Call<LoginResponse> login(@Body LoginRequest request);

    @POST("register")
    Call<ApiResponse> register(@Body User user);

    @POST("register")
    Call<ApiResponse> registerUser(@Body RegisterRequest request);

    @POST("forgot-password")
    Call<ApiResponse> forgotPassword(@Body ForgotPasswordRequest request);

    @POST("verify-reset-code")
    Call<ApiResponse> verifyCode(@Body VerifyCodeRequest request);

    @POST("reset-password")
    Call<ApiResponse> resetPassword(@Body ResetPasswordRequest request);


    // ======================================================
    // ✅ PRODUCTS
    // ======================================================

    @GET("gallons")
    Call<List<ProductDto>> getGallons();

    @GET("gallons/{id}")
    Call<ProductDto> getGallon(@Path("id") int id);


    // ======================================================
    // ✅ ADDRESSES
    // ======================================================

    @GET("addresses")
    Call<AddressResponse> getAddresses(@Header("Authorization") String token);

    @POST("addresses")
    Call<ApiResponse> createAddress(@Header("Authorization") String token, @Body Address body);

    @PUT("addresses/{id}")
    Call<ApiResponse> updateAddress(@Header("Authorization") String token, @Path("id") int id, @Body Address body);

    @DELETE("addresses/{id}")
    Call<ApiResponse> deleteAddress(@Header("Authorization") String token, @Path("id") int id);

    @POST("addresses/{id}/default")
    Call<ApiResponse> setDefaultAddress(@Header("Authorization") String token, @Path("id") int id);


    // ======================================================
    // ✅ ORDERS
    // ======================================================

    @POST("orders")
    Call<ApiResponse> placeOrder(@Header("Authorization") String token, @Body OrderRequest request);

    @GET("orders/stats")
    Call<OrderStats> getOrderStats(@Header("Authorization") String token, @Query("order_id") String orderId);

    @GET("orders/latest")
    Call<List<OrderOverview>> getLatestOrders(@Header("Authorization") String token, @Query("order_id") String orderId);


    // ======================================================
    // ✅ CART
    // ======================================================

    @GET("cartItems")
    Call<List<ServerCartItem>> getCartItems(@Header("Authorization") String token);

    @POST("cartItems")
    Call<ApiResponse> addToCart(@Header("Authorization") String token, @Body Map<String, Object> body);

    @DELETE("cartItems")
    Call<ApiResponse> removeFromCart(@Header("Authorization") String token, @Body Map<String, List<Integer>> body);

    @PUT("cartItems/decrease")
    Call<ApiResponse> decreaseCartItem(@Header("Authorization") String token, @Body Map<String, Integer> body);

    @PUT("cartItems/increase")
    Call<ApiResponse> increaseCartItem(@Header("Authorization") String token, @Body Map<String, Integer> body);


    // ======================================================
    // ✅ OPTIONAL DYNAMIC URLS
    // ======================================================

    @GET
    Call<List<ProductDto>> getCustomerContainerJson(@Url String fullUrl);

    @GET
    Call<List<CartItem>> getCustomerCartJson(@Url String fullUrl);


    // ======================================================
    // ✅ RIDER ORDERS (Pick-up & Deliver)
    // ======================================================

    // ======================================================
// ✅ RIDER ORDERS (Pick-up & Deliver)
// ======================================================

    /** Fetch ALL rider orders (to_pickup + to_deliver) */
    @GET("rider/orders")
    @Headers("Cache-Control: no-cache, no-store, must-revalidate")
    Call<RiderOrdersResponse> getRiderOrders(@Header("Authorization") String token);


    /** Update order status */
    @PUT("rider/orders/{id}/update-status")
    @Headers("Cache-Control: no-cache, no-store, must-revalidate")
    Call<ApiResponse> updateRiderOrderStatus(
            @Header("Authorization") String token,
            @Path("id") int orderId,
            @Body Map<String, String> body
    );

    /** Fetch ONLY to_pick orders */
    @GET("rider/orders/to_pick")
    @Headers("Cache-Control: no-cache, no-store, must-revalidate")
    Call<ApiResponse> getToPickOrders(@Header("Authorization") String token);


    /** Delivery history list */
    @GET("rider/delivery-history")
    @Headers("Cache-Control: no-cache, no-store, must-revalidate")
    Call<ApiResponse> getDeliveryHistory(@Header("Authorization") String token);

    /** (Optional) Completed model version */
    @GET("rider/delivery-history")
    @Headers("Cache-Control: no-cache, no-store, must-revalidate")
    Call<List<CompletedOrderModel>> getDeliveredOrders();




}
