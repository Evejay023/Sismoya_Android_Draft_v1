    package com.example.waterrefilldraftv1.Global.network;

    import android.content.Context;
    import android.util.Log;

    import com.example.waterrefilldraftv1.Login_Customer_and_Riders.LoginRequest;
    import com.example.waterrefilldraftv1.Customer.models.LoginResponse;
    import com.example.waterrefilldraftv1.Customer.models.ForgotPasswordRequest;
    import com.example.waterrefilldraftv1.Customer.models.VerifyCodeRequest;
    import com.example.waterrefilldraftv1.Login_Customer_and_Riders_model.ResetPasswordRequest;
    import com.example.waterrefilldraftv1.Customer.models.User;
    import com.example.waterrefilldraftv1.Customer.models.Address;
    import com.example.waterrefilldraftv1.Customer.models.ProductDto;
    import com.example.waterrefilldraftv1.Customer.models.OrderRequest;

    import java.util.HashMap;
    import java.util.Map;

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
            String token = TokenManager.getToken(context); // fetch token

            apiService.getAddresses("Bearer " + token).enqueue(new Callback<com.example.waterrefilldraftv1.Customer.models.AddressResponse>() {
                @Override
                public void onResponse(Call<com.example.waterrefilldraftv1.Customer.models.AddressResponse> call, Response<com.example.waterrefilldraftv1.Customer.models.AddressResponse> response) {
                    if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                        callback.onSuccess(response.body().getAddresses());
                    } else {
                        String errorMsg = "Failed to load addresses";
                        if (response.errorBody() != null) {
                            try {
                                errorMsg = response.errorBody().string();
                            } catch (Exception ignored) {}
                        }
                        Log.w(TAG, "getAddresses onResponse not successful: " + errorMsg);
                        callback.onError(errorMsg);
                    }
                }

                @Override
                public void onFailure(Call<com.example.waterrefilldraftv1.Customer.models.AddressResponse> call, Throwable t) {
                    callback.onError(t.getMessage() == null ? "Network error" : t.getMessage());
                }
            });
        }


        public void createAddress(Address address, ApiCallback<ApiResponse> callback) {
            String token = TokenManager.getToken(context);
            apiService.createAddress("Bearer " + token, address).enqueue(new Callback<ApiResponse>() {
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
            String token = TokenManager.getToken(context);
            apiService.updateAddress("Bearer " + token, id, address).enqueue(new Callback<ApiResponse>() {
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
            String token = TokenManager.getToken(context);
            apiService.deleteAddress("Bearer " + token, id).enqueue(new Callback<ApiResponse>() {
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
            String token = TokenManager.getToken(context);
            apiService.setDefaultAddress("Bearer " + token, id).enqueue(new Callback<ApiResponse>() {
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
            String token = TokenManager.getToken(context); // ✅ Retrieve token
            if (token == null) {
                callback.onError("Token missing. Please log in again.");
                return;
            }

            apiService.placeOrder("Bearer " + token, request).enqueue(new Callback<ApiResponse>() {
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

        // ✅ Fetch user profile
        public void getProfile(ApiCallback<ApiResponse> callback) {
            String token = TokenManager.getToken(context);
            if (token == null) {
                callback.onError("Token missing. Please log in again.");
                return;
            }

            apiService.getProfile("Bearer " + token).enqueue(new Callback<ApiResponse>() {
                @Override
                public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        callback.onSuccess(response.body());
                    } else {
                        callback.onError("Failed to fetch profile: " + response.message());
                    }
                }

                @Override
                public void onFailure(Call<ApiResponse> call, Throwable t) {
                    callback.onError(t.getMessage() == null ? "Network error" : t.getMessage());
                }
            });
        }

        // ✅ Update profile
        public void updateProfile(User user, ApiCallback<ApiResponse> callback) {
            String token = TokenManager.getToken(context);
            if (token == null) {
                callback.onError("Token missing. Please log in again.");
                return;
            }

            apiService.updateProfile("Bearer " + token, user).enqueue(new Callback<ApiResponse>() {
                @Override
                public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        callback.onSuccess(response.body());
                    } else {
                        callback.onError("Profile update failed: " + response.message());
                    }
                }

                @Override
                public void onFailure(Call<ApiResponse> call, Throwable t) {
                    callback.onError(t.getMessage() == null ? "Network error" : t.getMessage());
                }
            });
        }

        // ✅ Change Password
        public void changePassword(Map<String, String> body, ApiCallback<ApiResponse> callback) {
            String token = TokenManager.getToken(context);
            if (token == null) {
                callback.onError("Token missing. Please log in again.");
                return;
            }

            apiService.changePassword("Bearer " + token, body).enqueue(new retrofit2.Callback<ApiResponse>() {
                @Override
                public void onResponse(retrofit2.Call<ApiResponse> call, retrofit2.Response<ApiResponse> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        callback.onSuccess(response.body());
                    } else {
                        callback.onError("Change password failed: " + response.message());
                    }
                }

                @Override
                public void onFailure(retrofit2.Call<ApiResponse> call, Throwable t) {
                    callback.onError(t.getMessage() == null ? "Network error" : t.getMessage());
                }
            });
        }


        // ✅ Update Rider profile (partial update)
        public void updateRiderProfile(Map<String, String> body, ApiCallback<ApiResponse> callback) {
            String token = TokenManager.getToken(context);
            if (token == null) {
                callback.onError("Token missing. Please log in again.");
                return;
            }

            apiService.updateProfilePartial("Bearer " + token, body).enqueue(new Callback<ApiResponse>() {
                @Override
                public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                    if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                        callback.onSuccess(response.body());
                    } else {
                        String msg = (response.body() != null)
                                ? response.body().getMessage()
                                : "Update failed.";
                        callback.onError(msg);
                    }
                }

                @Override
                public void onFailure(Call<ApiResponse> call, Throwable t) {
                    callback.onError(t.getMessage() == null ? "Network error" : t.getMessage());
                }
            });
        }


        // ✅ Fetch user/rider profile


        public void changeRiderPassword(Map<String, String> body, ApiCallback<ApiResponse> callback) {
            String token = TokenManager.getToken(context);
            if (token == null) {
                callback.onError("Token missing. Please log in again.");
                return;
            }

            // ✅ Use the same endpoint as changePassword()
            apiService.changePassword("Bearer " + token, body)
                    .enqueue(new retrofit2.Callback<ApiResponse>() {
                        @Override
                        public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                            if (response.isSuccessful() && response.body() != null) {
                                callback.onSuccess(response.body());
                            } else {
                                callback.onError("Change password failed: " + response.message());
                            }
                        }

                        @Override
                        public void onFailure(Call<ApiResponse> call, Throwable t) {
                            callback.onError(t.getMessage() == null ? "Network error" : t.getMessage());
                        }
                    });
        }

        public void updateRiderOrderStatus(int orderId, String newStatus, ApiCallback<ApiResponse> callback) {
            String token = TokenManager.getToken(context);
            if (token == null) {
                callback.onError("Token missing. Please log in again.");
                return;
            }

            Map<String, String> body = new HashMap<>();
            body.put("newStatus", newStatus); // ✅ correct key

            apiService.updateRiderOrderStatus("Bearer " + token, orderId, body)
                    .enqueue(new Callback<ApiResponse>() {
                        @Override
                        public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                            if (response.isSuccessful() && response.body() != null) {
                                callback.onSuccess(response.body());
                            } else {
                                callback.onError("Failed to update status: " + response.message());
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
