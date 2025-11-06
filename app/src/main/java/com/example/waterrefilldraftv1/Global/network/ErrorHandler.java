package com.example.waterrefilldraftv1.Global.network;

import android.util.Log;
import java.io.IOException;
import retrofit2.Response;

public class ErrorHandler {
    private static final String TAG = "ErrorHandler";

    public static String handleNetworkError(Throwable t) {
        Log.e(TAG, "Network error", t);

        if (t instanceof java.net.UnknownHostException || t instanceof java.net.ConnectException) {
            return "No internet connection. Please check your network and try again.";
        } else if (t instanceof java.net.SocketTimeoutException) {
            return "Connection timeout. Please try again.";
        } else if (t instanceof javax.net.ssl.SSLHandshakeException) {
            return "Security error. Please check your connection and try again.";
        } else if (t instanceof IOException) {
            return "Network error. Please check your connection.";
        } else {
            return "An error occurred. Please try again.";
        }
    }

    public static String handleApiError(Response<?> response) {
        try {
            String errorMessage = "Request failed";

            if (response.errorBody() != null) {
                String errorBody = response.errorBody().string();
                Log.d(TAG, "Raw error response: " + errorBody);

                // Try to extract message from JSON error response using more robust parsing
                errorMessage = extractJsonField(errorBody, "message");
                if (errorMessage.equals(errorBody)) { // If not found, try "error" field
                    errorMessage = extractJsonField(errorBody, "error");
                }

                // If still no specific message, use the raw body (truncated if too long)
                if (errorMessage.equals(errorBody) && errorBody.length() > 100) {
                    errorMessage = errorBody.substring(0, 100) + "...";
                }
            }

            // Enhance with status code context
            switch (response.code()) {
                case 401:
                    if (errorMessage.toLowerCase().contains("login") ||
                            errorMessage.toLowerCase().contains("credential")) {
                        return errorMessage; // Use server message if it's about credentials
                    }
                    return "Invalid username/email or password. Please check your credentials.";
                case 404:
                    return "Account not found. Please check your credentials.";
                case 500:
                    return "Server error. Please try again later.";
                case 422:
                    return "Invalid input data. Please check your information.";
                default:
                    return errorMessage + " (Error " + response.code() + ")";
            }

        } catch (Exception e) {
            Log.w(TAG, "Error parsing error response", e);
            // Fallback to status code based messages
            switch (response.code()) {
                case 401:
                    return "Invalid username/email or password.";
                case 404:
                    return "Account not found.";
                case 500:
                    return "Server error. Please try again later.";
                default:
                    return "Request failed. Please try again.";
            }
        }
    }

    private static String extractJsonField(String json, String fieldName) {
        try {
            String searchPattern = "\"" + fieldName + "\":\"";
            int fieldIndex = json.indexOf(searchPattern);
            if (fieldIndex != -1) {
                int start = fieldIndex + searchPattern.length();
                int end = json.indexOf("\"", start);
                if (end > start) {
                    return json.substring(start, end);
                }
            }
        } catch (Exception e) {
            Log.w(TAG, "Error extracting JSON field: " + fieldName, e);
        }
        return json; // Return original if not found
    }
}