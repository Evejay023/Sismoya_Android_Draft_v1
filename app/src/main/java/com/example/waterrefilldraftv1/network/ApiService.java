    package com.example.waterrefilldraftv1.network;

    import com.example.waterrefilldraftv1.models.ApiResponse;
    import com.example.waterrefilldraftv1.models.LoginRequest;
    import com.example.waterrefilldraftv1.models.LoginResponse;
    import com.example.waterrefilldraftv1.models.ForgotPasswordRequest;
    import com.example.waterrefilldraftv1.models.VerifyCodeRequest;
    import com.example.waterrefilldraftv1.models.ResetPasswordRequest;
    import com.example.waterrefilldraftv1.models.User;
    import com.example.waterrefilldraftv1.models.Address;
    import com.example.waterrefilldraftv1.models.ProductDto;

    import retrofit2.Call;
    import retrofit2.http.Body;
    import retrofit2.http.POST;
    import retrofit2.http.GET;
    import retrofit2.http.PUT;
    import retrofit2.http.DELETE;
    import retrofit2.http.Path;

    public interface ApiService {

        // Login uses JSON body { "identifier": "...", "password": "..." }
        @POST("login")
        Call<LoginResponse> login(@Body LoginRequest request);

        // Register expects user fields; existing code sends JSON with first_name, last_name, etc.
        @POST("register")
        Call<ApiResponse> register(@Body User user);

        // Forgot password (send email)
        @POST("forgot_password")
        Call<ApiResponse> forgotPassword(@Body ForgotPasswordRequest request);

        // Verify code (send email + code)
        @POST("verify_code")
        Call<ApiResponse> verifyCode(@Body VerifyCodeRequest request);

        // Reset password (send email + code + password + confirm_password)
        @POST("reset_password")
        Call<ApiResponse> resetPassword(@Body ResetPasswordRequest request);

        // ================= Additional endpoints based on PHP backend =================

        // Gallons / Products
        @GET("gallons")
        Call<java.util.List<ProductDto>> getGallons();

        @GET("gallons/{id}")
        Call<ProductDto> getGallon(@Path("id") int id);

        // Addresses (requires Authorization header on client)
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

        // Orders
        @POST("orders")
        Call<ApiResponse> placeOrder(@Body com.example.waterrefilldraftv1.models.OrderRequest request);
    }
