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
    @Headers("Cache-Control: no-cache, no-store, must-revalidate")
    Call<ApiResponse> getProfile(@Header("Authorization") String token);

    @PUT("update-profile")
    @Headers("Cache-Control: no-cache, no-store, must-revalidate")
    Call<ApiResponse> updateProfile(@Header("Authorization") String token, @Body User user);

    @PUT("update-profile")
    @Headers("Cache-Control: no-cache, no-store, must-revalidate")
    Call<ApiResponse> updateProfilePartial(@Header("Authorization") String token, @Body Map<String, String> body);

    @PUT("change-password")
    @Headers("Cache-Control: no-cache, no-store, must-revalidate")
    Call<ApiResponse> changePassword(@Header("Authorization") String token, @Body Map<String, String> body);


    // ======================================================
    // ✅ AUTHENTICATION
    // ======================================================

    @POST("login")
    @Headers("Cache-Control: no-cache, no-store, must-revalidate")
    Call<LoginResponse> login(@Body LoginRequest request);

    @POST("register")
    @Headers("Cache-Control: no-cache, no-store, must-revalidate")
    Call<ApiResponse> register(@Body User user);

    @POST("register")
    @Headers("Cache-Control: no-cache, no-store, must-revalidate")
    Call<ApiResponse> registerUser(@Body RegisterRequest request);

    @POST("forgot-password")
    @Headers("Cache-Control: no-cache, no-store, must-revalidate")
    Call<ApiResponse> forgotPassword(@Body ForgotPasswordRequest request);

    @POST("verify-reset-code")
    @Headers("Cache-Control: no-cache, no-store, must-revalidate")
    Call<ApiResponse> verifyCode(@Body VerifyCodeRequest request);

    @POST("reset-password")
    @Headers("Cache-Control: no-cache, no-store, must-revalidate")
    Call<ApiResponse> resetPassword(@Body ResetPasswordRequest request);


    // ======================================================
    // ✅ PRODUCTS
    // ======================================================

    @GET("gallons")
    @Headers("Cache-Control: no-cache, no-store, must-revalidate")
    Call<List<ProductDto>> getGallons();

    @GET("gallons/{id}")
    @Headers("Cache-Control: no-cache, no-store, must-revalidate")
    Call<ProductDto> getGallon(@Path("id") int id);


    // ======================================================
    // ✅ ADDRESSES
    // ======================================================

    @GET("addresses")
    @Headers("Cache-Control: no-cache, no-store, must-revalidate")
    Call<AddressResponse> getAddresses(@Header("Authorization") String token);

    @POST("addresses")
    @Headers("Cache-Control: no-cache, no-store, must-revalidate")
    Call<ApiResponse> createAddress(@Header("Authorization") String token, @Body Address body);

    @PUT("addresses/{id}")
    @Headers("Cache-Control: no-cache, no-store, must-revalidate")
    Call<ApiResponse> updateAddress(@Header("Authorization") String token, @Path("id") int id, @Body Address body);

    @DELETE("addresses/{id}")
    @Headers("Cache-Control: no-cache, no-store, must-revalidate")
    Call<ApiResponse> deleteAddress(@Header("Authorization") String token, @Path("id") int id);

    @POST("addresses/{id}/default")
    @Headers("Cache-Control: no-cache, no-store, must-revalidate")
    Call<ApiResponse> setDefaultAddress(@Header("Authorization") String token, @Path("id") int id);


    // ======================================================
    // ✅ ORDERS
    // ======================================================

    @POST("orders")
    @Headers("Cache-Control: no-cache, no-store, must-revalidate")
    Call<ApiResponse> placeOrder(@Header("Authorization") String token, @Body OrderRequest request);

    @GET("orders/stats")
    @Headers("Cache-Control: no-cache, no-store, must-revalidate")
    Call<OrderStats> getOrderStats(@Header("Authorization") String token, @Query("order_id") String orderId);

    @GET("orders/latest")
    @Headers("Cache-Control: no-cache, no-store, must-revalidate")
    Call<List<OrderOverview>> getLatestOrders(@Header("Authorization") String token, @Query("order_id") String orderId);


    // ======================================================
    // ✅ CART
    // ======================================================

    @GET("cartItems")
    @Headers("Cache-Control: no-cache, no-store, must-revalidate")
    Call<List<ServerCartItem>> getCartItems(@Header("Authorization") String token);

    @POST("cartItems")
    @Headers("Cache-Control: no-cache, no-store, must-revalidate")
    Call<ApiResponse> addToCart(@Header("Authorization") String token, @Body Map<String, Object> body);

    @DELETE("cartItems")
    @Headers("Cache-Control: no-cache, no-store, must-revalidate")
    Call<ApiResponse> removeFromCart(@Header("Authorization") String token, @Body Map<String, List<Integer>> body);

    @PUT("cartItems/decrease")
    @Headers("Cache-Control: no-cache, no-store, must-revalidate")
    Call<ApiResponse> decreaseCartItem(@Header("Authorization") String token, @Body Map<String, Integer> body);

    @PUT("cartItems/increase")
    @Headers("Cache-Control: no-cache, no-store, must-revalidate")
    Call<ApiResponse> increaseCartItem(@Header("Authorization") String token, @Body Map<String, Integer> body);


    // ======================================================
    // ✅ OPTIONAL DYNAMIC URLS
    // ======================================================

    @GET
    @Headers("Cache-Control: no-cache, no-store, must-revalidate")
    Call<List<ProductDto>> getCustomerContainerJson(@Url String fullUrl);

    @GET
    @Headers("Cache-Control: no-cache, no-store, must-revalidate")
    Call<List<CartItem>> getCustomerCartJson(@Url String fullUrl);


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
            @Header("Authorization") String authHeader,
            @Path("id") String orderId,
            @Body Map<String, String> body
    );

    /** Fetch ONLY to_pick orders - FIXED: This endpoint might not exist in your backend */
    @GET("rider/orders/to_pick")
    @Headers("Cache-Control: no-cache, no-store, must-revalidate")
    Call<ApiResponse> getToPickOrders(@Header("Authorization") String token);

    /** Delivery history list - FIXED: Return proper response type */
    @GET("rider/delivery-history")
    @Headers("Cache-Control: no-cache, no-store, must-revalidate")
    Call<List<CompletedOrderModel>> getDeliveryHistory();

    /** Alternative delivery history with wrapper */
    @GET("rider/delivery-history")
    @Headers("Cache-Control: no-cache, no-store, must-revalidate")
    Call<ApiResponse> getDeliveryHistoryWithWrapper(@Header("Authorization") String token);

    // ======================================================
    // ✅ ADDITIONAL ENDPOINTS FROM YOUR BACKEND
    // ======================================================

    /** Get order by ID */
    @GET("orders/{id}")
    @Headers("Cache-Control: no-cache, no-store, must-revalidate")
    Call<ApiResponse> getOrderById(@Header("Authorization") String token, @Path("id") String orderId);

    /** Cancel order */
    @PUT("orders/{id}/cancel")
    @Headers("Cache-Control: no-cache, no-store, must-revalidate")
    Call<ApiResponse> cancelOrder(@Header("Authorization") String token, @Path("id") String orderId);

    /** Get orders by status */
    @GET("orders")
    @Headers("Cache-Control: no-cache, no-store, must-revalidate")
    Call<ApiResponse> getOrdersByStatus(@Header("Authorization") String token, @Query("status") String status);
}