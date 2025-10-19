package com.example.waterrefilldraftv1.Customer.network;

import android.content.Context;
import android.util.Log;

import com.example.waterrefilldraftv1.Customer.models.ApiResponse;
import com.example.waterrefilldraftv1.Login_Customer_and_Riders.LoginRequest;
import com.example.waterrefilldraftv1.Customer.models.LoginResponse;
import com.example.waterrefilldraftv1.Customer.models.ForgotPasswordRequest;
import com.example.waterrefilldraftv1.Customer.models.VerifyCodeRequest;
import com.example.waterrefilldraftv1.Customer.models.ResetPasswordRequest;
import com.example.waterrefilldraftv1.Customer.models.User;
import com.example.waterrefilldraftv1.Customer.models.Address;
import com.example.waterrefilldraftv1.Customer.models.ProductDto;
import com.example.waterrefilldraftv1.Customer.models.OrderRequest;

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

    // =================== Gallons / Products ===================
    public void fetchGallons(ApiCallback<java.util.List<ProductDto>> callback) {
        apiService.getGallons().enqueue(new Callback<java.util.List<ProductDto>>() {
            @Override
            public void onResponse(Call<java.util.List<ProductDto>> call, Response<java.util.List<ProductDto>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError("Failed to load products: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<java.util.List<ProductDto>> call, Throwable t) {
                callback.onError(t.getMessage() == null ? "Network error" : t.getMessage());
            }
        });
    }

    // =================== Addresses ===================
    public void getAddresses(ApiCallback<java.util.List<Address>> callback) {
        apiService.getAddresses().enqueue(new Callback<java.util.List<Address>>() {
            @Override
            public void onResponse(Call<java.util.List<Address>> call, Response<java.util.List<Address>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError("Failed to load addresses: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<java.util.List<Address>> call, Throwable t) {
                callback.onError(t.getMessage() == null ? "Network error" : t.getMessage());
            }
        });
    }

    public void createAddress(Address address, ApiCallback<ApiResponse> callback) {
        apiService.createAddress(address).enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError("Create address failed: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                callback.onError(t.getMessage() == null ? "Network error" : t.getMessage());
            }
        });
    }

    public void updateAddress(int id, Address address, ApiCallback<ApiResponse> callback) {
        apiService.updateAddress(id, address).enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError("Update address failed: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                callback.onError(t.getMessage() == null ? "Network error" : t.getMessage());
            }
        });
    }

    public void deleteAddress(int id, ApiCallback<ApiResponse> callback) {
        apiService.deleteAddress(id).enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError("Delete address failed: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                callback.onError(t.getMessage() == null ? "Network error" : t.getMessage());
            }
        });
    }

    public void setDefaultAddress(int id, ApiCallback<ApiResponse> callback) {
        apiService.setDefaultAddress(id).enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError("Set default failed: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                callback.onError(t.getMessage() == null ? "Network error" : t.getMessage());
            }
        });
    }

    // =================== Orders ===================
    public void placeOrder(OrderRequest request, ApiCallback<ApiResponse> callback) {
        apiService.placeOrder(request).enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError("Order failed: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                callback.onError(t.getMessage() == null ? "Network error" : t.getMessage());
            }
        });
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
