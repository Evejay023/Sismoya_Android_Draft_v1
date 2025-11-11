package com.example.waterrefilldraftv1.Riders.Utils;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.widget.Toast;

import com.example.waterrefilldraftv1.Global.network.TokenManager;
import com.example.waterrefilldraftv1.Login_Customer_and_Riders.LoginActivity;
import com.example.waterrefilldraftv1.Customer.utils.SessionStore;
import com.example.waterrefilldraftv1.Riders.models.Rider;

public class RiderAuthHelper {

    // ✅ ADD: Track if user has logged in at least once
    public static void setUserHasLoggedInBefore(Context context, boolean value) {
        SharedPreferences prefs = context.getSharedPreferences("AppPrefs", Context.MODE_PRIVATE);
        prefs.edit().putBoolean("user_has_logged_in_before", value).apply();
    }

    public static boolean hasUserLoggedInBefore(Context context) {
        SharedPreferences prefs = context.getSharedPreferences("AppPrefs", Context.MODE_PRIVATE);
        return prefs.getBoolean("user_has_logged_in_before", false);
    }

    public static void logoutRider(Context context) {
        // ✅ SET FLAG: Direct to login on next app start
        setDirectToLoginFlag(context, true);

        // Clear all session data (but keep Remember Me credentials)
        clearAllSessionData(context);

        Toast.makeText(context, "Logged out successfully", Toast.LENGTH_SHORT).show();

        // Navigate to login
        Intent intent = new Intent(context, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        context.startActivity(intent);

        // If called from Activity, finish it
        if (context instanceof android.app.Activity) {
            ((android.app.Activity) context).finish();
        }
    }

    // ✅ NEW: Set flag for direct login
    private static void setDirectToLoginFlag(Context context, boolean value) {
        SharedPreferences prefs = context.getSharedPreferences("AppPrefs", Context.MODE_PRIVATE);
        prefs.edit().putBoolean("direct_to_login", value).apply();
    }

    // ✅ NEW: Check if we should go directly to login
    public static boolean shouldDirectToLogin(Context context) {
        SharedPreferences prefs = context.getSharedPreferences("AppPrefs", Context.MODE_PRIVATE);
        return prefs.getBoolean("direct_to_login", false);
    }

    // ✅ NEW: Clear the flag (after using it)
    public static void clearDirectToLoginFlag(Context context) {
        SharedPreferences prefs = context.getSharedPreferences("AppPrefs", Context.MODE_PRIVATE);
        prefs.edit().putBoolean("direct_to_login", false).apply();
    }

    public static void clearAllSessionData(Context context) {
        // 1. Clear TokenManager
        TokenManager.clearToken();
        TokenManager.clearToken(context);

        // 2. Clear SessionStore (Customer session)
        SessionStore customerSession = new SessionStore(context);
        customerSession.clear();

        // 3. Clear RiderSessionStore
        RiderSessionStore riderSessionStore = new RiderSessionStore(context);
        riderSessionStore.clear();

        // 4. Clear any other SharedPreferences used by rider
        SharedPreferences riderPrefs = context.getSharedPreferences("RiderPrefs", Context.MODE_PRIVATE);
        riderPrefs.edit().clear().apply();

        // 5. Clear global app preferences if any
        SharedPreferences globalPrefs = context.getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE);
        globalPrefs.edit().clear().apply();
    }

    public static boolean isRiderLoggedIn(Context context) {
        // Check if token exists
        String token = TokenManager.getToken(context);
        if (token == null || token.isEmpty()) {
            return false;
        }

        // Check if rider data exists
        RiderSessionStore riderSessionStore = new RiderSessionStore(context);
        Rider rider = riderSessionStore.getRider();

        return rider != null && "rider".equalsIgnoreCase(rider.getRole());
    }
}