    package com.example.waterrefilldraftv1.network;

    import com.example.waterrefilldraftv1.models.ApiResponse;
    import com.example.waterrefilldraftv1.models.LoginRequest;
    import com.example.waterrefilldraftv1.models.LoginResponse;
    import com.example.waterrefilldraftv1.models.ForgotPasswordRequest;
    import com.example.waterrefilldraftv1.models.VerifyCodeRequest;
    import com.example.waterrefilldraftv1.models.ResetPasswordRequest;
    import com.example.waterrefilldraftv1.models.User;

    import retrofit2.Call;
    import retrofit2.http.Body;
    import retrofit2.http.POST;

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

        // Add more endpoints here (containers, products, orders...) as needed
    }
