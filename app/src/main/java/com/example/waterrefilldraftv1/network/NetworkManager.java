package com.example.waterrefilldraftv1.network;

import android.content.Context;
import android.util.Log;

import com.example.waterrefilldraftv1.models.ApiResponse;
import com.example.waterrefilldraftv1.models.LoginRequest;
import com.example.waterrefilldraftv1.models.LoginResponse;
import com.example.waterrefilldraftv1.models.ForgotPasswordRequest;
import com.example.waterrefilldraftv1.models.VerifyCodeRequest;
import com.example.waterrefilldraftv1.models.ResetPasswordRequest;
import com.example.waterrefilldraftv1.models.User;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NetworkManager {
    private static final String TAG = "NetworkManager";
    private final ApiService apiService;
    private final Context context; // optional, kept for compatibility

    public interface ApiCallback<T> {
        void onSuccess(T response);
        void onError(String error);
    }

    public NetworkManager(Context context) {
        this.context = context;
        this.apiService = RetrofitClient.getInstance().create(ApiService.class);
    }

    // Overload kept for backward compatibility if some code passes User object
    public void loginUser(User user, ApiCallback<LoginResponse> callback) {
        // If your existing code constructs a User for login, convert to LoginRequest
        String identifier = user.getEmail() != null && !user.getEmail().isEmpty() ?
                user.getEmail() : user.getUsername();
        loginUser(identifier, user.getPassword(), callback);
    }

    // Primary login method used by UI (identifier = username or email)
    public void loginUser(String identifier, String password, ApiCallback<LoginResponse> callback) {
        LoginRequest req = new LoginRequest(identifier, password);
        apiService.login(req).enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    String msg = "Login failed";
                    try {
                        if (response.errorBody() != null) msg = response.errorBody().string();
                    } catch (Exception e) { /* ignore */ }
                    Log.w(TAG, "login onResponse not successful: " + msg);
                    callback.onError("Login failed: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                Log.e(TAG, "Login error", t);
                callback.onError(t.getMessage() == null ? "Network error" : t.getMessage());
            }
        });
    }

    // Register (keeps existing signature)
    public void registerUser(User user, ApiCallback<ApiResponse> callback) {
        apiService.register(user).enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError("Register failed: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                callback.onError(t.getMessage() == null ? "Network error" : t.getMessage());
            }
        });
    }

    // Forgot password (email)
    public void requestPasswordReset(String email, ApiCallback<ApiResponse> callback) {
        ForgotPasswordRequest req = new ForgotPasswordRequest(email);
        apiService.forgotPassword(req).enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError("Request failed: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                callback.onError(t.getMessage() == null ? "Network error" : t.getMessage());
            }
        });
    }

    // Verify code
    public void verifyCode(String email, String code, ApiCallback<ApiResponse> callback) {
        VerifyCodeRequest req = new VerifyCodeRequest(email, code);
        apiService.verifyCode(req).enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError("Verification failed: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                callback.onError(t.getMessage() == null ? "Network error" : t.getMessage());
            }
        });
    }

    // Reset password
    public void resetPassword(String email, String code, String newPassword, ApiCallback<ApiResponse> callback) {
        ResetPasswordRequest req = new ResetPasswordRequest(email, code, newPassword, newPassword);
        apiService.resetPassword(req).enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError("Reset failed: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                callback.onError(t.getMessage() == null ? "Network error" : t.getMessage());
            }
        });
    }

    // keep for compatibility with old code
    public void shutdown() {
        // nothing to shutdown for Retrofit, but method kept so UI calls won't crash
    }
}
