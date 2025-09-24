package com.example.waterrefilldraftv1.network;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.waterrefilldraftv1.models.ApiResponse;
import com.example.waterrefilldraftv1.models.LoginResponse;
import com.example.waterrefilldraftv1.models.User;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class NetworkManager {

    private final Context context;
    private RequestQueue requestQueue;
    private static final String TAG = "NetworkManager";

    private static final String BASE_URL = "http://192.168.1.20/sismoya";   // replace with your API base URL

    public NetworkManager(Context context) {
        this.context = context;
        requestQueue = Volley.newRequestQueue(context);
    }

    // Generic callback
    public interface ApiCallback<T> {
        void onSuccess(T response);
        void onError(String error);
    }

    // ================== LOGIN ==================
    public void loginUser(String userInput, String password, ApiCallback<LoginResponse> callback) {
        if (userInput == null || userInput.isEmpty() || password == null || password.isEmpty()) {
            callback.onError("Username/email and password cannot be empty");
            return;
        }

        JSONObject body = new JSONObject();
        try {
            body.put("identifier", userInput); // new field expected by server
            body.put("password", password);
        } catch (JSONException e) {
            callback.onError("JSON error: " + e.getMessage());
            return;
        }

        String url = BASE_URL + "/login";

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, body,
                response -> {
                    try {
                        boolean success = !response.getBoolean("error");
                        String message = response.getString("message");

                        User user = null;
                        if (response.has("user") && !response.isNull("user")) {
                            JSONObject userObj = response.getJSONObject("user");
                            user = new User(
                                    userObj.getInt("user_id"),
                                    userObj.getString("first_name"),
                                    userObj.getString("last_name"),
                                    userObj.getString("email"),
                                    userObj.getString("contact_no"),
                                    userObj.getString("username")
                            );
                        }

                        callback.onSuccess(new LoginResponse(success, message, user));
                    } catch (JSONException e) {
                        callback.onError("JSON parsing error: " + e.getMessage());
                    }
                },
                error -> callback.onError(getVolleyError(error))
        ) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json");
                return headers;
            }
        };

        Log.d(TAG, "Login JSON: " + body.toString());
        Log.d(TAG, "Login URL: " + url);

        requestQueue.add(request);
    }





    // ================== REGISTER ==================
    public void registerUser(User newUser, ApiCallback<ApiResponse> callback) {
        JSONObject body = new JSONObject();
        try {
            body.put("first_name", newUser.getFirstName());
            body.put("last_name", newUser.getLastName());
            body.put("email", newUser.getEmail());
            body.put("contact_no", newUser.getContactNo());
            body.put("username", newUser.getUsername());
            body.put("password", newUser.getPassword());
            body.put("role", newUser.getRole());
        } catch (JSONException e) {
            callback.onError("JSON error: " + e.getMessage());
            return;
        }

        String url = BASE_URL + "/register";
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, body,
                response -> {
                    try {
                        boolean success = !response.getBoolean("error");
                        String message = response.getString("message");
                        callback.onSuccess(new ApiResponse(success, message));
                    } catch (JSONException e) {
                        callback.onError("JSON parsing error: " + e.getMessage());
                    }
                },
                error -> callback.onError(getVolleyError(error))
        );

        requestQueue.add(request);
    }

    // ================== PASSWORD RESET ==================
    public void requestPasswordReset(String email, ApiCallback<ApiResponse> callback) {
        JSONObject body = new JSONObject();
        try {
            body.put("email", email);
        } catch (JSONException e) {
            callback.onError("JSON error: " + e.getMessage());
            return;
        }

        String url = BASE_URL + "/forgot_password";
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, body,
                response -> {
                    try {
                        boolean success = !response.getBoolean("error");
                        String message = response.getString("message");
                        callback.onSuccess(new ApiResponse(success, message));
                    } catch (JSONException e) {
                        callback.onError("JSON parsing error: " + e.getMessage());
                    }
                },
                error -> callback.onError(getVolleyError(error))
        );

        requestQueue.add(request);
    }

    public void verifyCode(String email, String code, ApiCallback<ApiResponse> callback) {
        JSONObject body = new JSONObject();
        try {
            body.put("email", email);
            body.put("code", code);
        } catch (JSONException e) {
            callback.onError("JSON error: " + e.getMessage());
            return;
        }

        String url = BASE_URL + "/verify_code";
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, body,
                response -> {
                    try {
                        boolean success = !response.getBoolean("error");
                        String message = response.getString("message");
                        callback.onSuccess(new ApiResponse(success, message));
                    } catch (JSONException e) {
                        callback.onError("JSON parsing error: " + e.getMessage());
                    }
                },
                error -> callback.onError(getVolleyError(error))
        );

        requestQueue.add(request);
    }

    public void resetPassword(String email, String code, String newPassword, ApiCallback<ApiResponse> callback) {
        JSONObject body = new JSONObject();
        try {
            body.put("email", email);
            body.put("code", code);
            body.put("new_password", newPassword);
        } catch (JSONException e) {
            callback.onError("JSON error: " + e.getMessage());
            return;
        }

        String url = BASE_URL + "/reset_password";
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, body,
                response -> {
                    try {
                        boolean success = !response.getBoolean("error");
                        String message = response.getString("message");
                        callback.onSuccess(new ApiResponse(success, message));
                    } catch (JSONException e) {
                        callback.onError("JSON parsing error: " + e.getMessage());
                    }
                },
                error -> callback.onError(getVolleyError(error))
        );

        requestQueue.add(request);
    }

    // ================== CLEANUP ==================
    public void shutdown() {
        if (requestQueue != null) {
            requestQueue.stop();
        }
    }

    private String getVolleyError(VolleyError error) {
        if (error.networkResponse != null) {
            return "Error " + error.networkResponse.statusCode;
        } else {
            return error.getMessage() != null ? error.getMessage() : "Network error";
        }
    }
}
