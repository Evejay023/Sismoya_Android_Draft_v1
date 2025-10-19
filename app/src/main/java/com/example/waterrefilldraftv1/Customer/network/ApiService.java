package com.example.waterrefilldraftv1.Customer.network;

import com.example.waterrefilldraftv1.Customer.models.ApiResponse;
import com.example.waterrefilldraftv1.Customer.models.OrderRequest;
import com.example.waterrefilldraftv1.Customer.models.RegisterRequest;
import com.example.waterrefilldraftv1.Login_Customer_and_Riders.LoginRequest;
import com.example.waterrefilldraftv1.Customer.models.LoginResponse;
import com.example.waterrefilldraftv1.Customer.models.ForgotPasswordRequest;
import com.example.waterrefilldraftv1.Customer.models.VerifyCodeRequest;
import com.example.waterrefilldraftv1.Customer.models.ResetPasswordRequest;
import com.example.waterrefilldraftv1.Customer.models.User;
import com.example.waterrefilldraftv1.Customer.models.Address;
import com.example.waterrefilldraftv1.Customer.models.ProductDto;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.GET;
import retrofit2.http.PUT;
import retrofit2.http.DELETE;
import retrofit2.http.Path;
import retrofit2.http.Url;

public interface ApiService {

    // ================= AUTH =================

    @GET("profile")
    Call<ApiResponse> getProfile(@Header("Authorization") String token);



    // Login uses JSON body { "identifier": "...", "password": "..." }
    @POST("login")
    Call<LoginResponse> login(@Body LoginRequest request);

    // Register using RegisterRequest model
    @POST("register")
    Call<ApiResponse> registerUser(@Body RegisterRequest request);

    // Register using User model (for compatibility)
    @POST("register")
    Call<ApiResponse> register(@Body User user);

    // Forgot password (send email)
    @POST("forgot-password")
    Call<ApiResponse> forgotPassword(@Body ForgotPasswordRequest request);

    // Verify reset code
    @POST("verify-reset-code")
    Call<ApiResponse> verifyCode(@Body VerifyCodeRequest request);

    // Resend reset code (optional if we add later)
    @POST("resend-reset-code")
    Call<ApiResponse> resendCode(@Body VerifyCodeRequest request);

    // Reset password
    @POST("reset-password")
    Call<ApiResponse> resetPassword(@Body ResetPasswordRequest request);



    // ================= PRODUCTS =================
    @GET("gallons")
    Call<java.util.List<ProductDto>> getGallons();

    @GET("gallons/{id}")
    Call<ProductDto> getGallon(@Path("id") int id);


    // ================= ADDRESSES =================
    @GET("addresses")
    Call<java.util.List<Address>> getAddresses();

    @POST("addresses")
    Call<ApiResponse> createAddress(@Body Address body);

    @PUT("addresses/{id}")
    Call<ApiResponse> updateAddress(@Path("id") int id, @Body Address body);

    @DELETE("addresses/{id}")
    Call<ApiResponse> deleteAddress(@Path("id") int id);

    @POST("addresses/{id}/default")
    Call<ApiResponse> setDefaultAddress(@Path("id") int id);


    // ================= ORDERS =================
    @POST("orders")
    Call<ApiResponse> placeOrder(@Body OrderRequest request);

    // ================= CART =================
    @GET("cartItems")
    Call<java.util.List<com.example.waterrefilldraftv1.Customer.models.ServerCartItem>> getCartItems(@Header("Authorization") String token);

    @POST("cartItems")
    Call<ApiResponse> addToCart(@Header("Authorization") String token, @Body java.util.Map<String, Object> body);

    @DELETE("cartItems")
    Call<ApiResponse> removeFromCart(@Header("Authorization") String token, @Body java.util.Map<String, java.util.List<Integer>> body);

    @PUT("cartItems/decrease")
    Call<ApiResponse> decreaseCartItem(@Header("Authorization") String token, @Body java.util.Map<String, Integer> body);

    @PUT("cartItems/increase")
    Call<ApiResponse> increaseCartItem(@Header("Authorization") String token, @Body java.util.Map<String, Integer> body);

    // ================= ORDER STATS / LATEST =================
    @GET("orders/stats")
    Call<com.example.waterrefilldraftv1.Customer.models.ApiResponse> getOrderStats(@Header("Authorization") String token);

    @GET("orders/latest")
    Call<com.example.waterrefilldraftv1.Customer.models.ApiResponse> getLatestOrders(@Header("Authorization") String token);

    // ================= OPTIONAL FULL-URL ENDPOINTS (non-/api routes) =================
    // Use if backend serves JSON at public pages like customerContainer/customerCart
    @GET
    Call<java.util.List<ProductDto>> getCustomerContainerJson(@Url String fullUrl);

    @GET
    Call<java.util.List<com.example.waterrefilldraftv1.Customer.models.CartItem>> getCustomerCartJson(@Url String fullUrl);
}
