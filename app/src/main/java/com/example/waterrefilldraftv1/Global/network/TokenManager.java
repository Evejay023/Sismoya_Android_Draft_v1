package com.example.waterrefilldraftv1.Global.network;

import android.content.Context;
import android.content.SharedPreferences;

public class TokenManager {
    private static final String PREFS_NAME = "MyAppPrefs";
    private static final String KEY_TOKEN = "token";

    private static Context appContext;

    public static void init(Context context) {
        if (context != null) {
            appContext = context.getApplicationContext();
        }
    }

    public static Context getAppContext() {
        return appContext;
    }

    public static String getToken(Context context) {
        SharedPreferences sp = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return sp.getString(KEY_TOKEN, null);
    }

    public static String getToken() {
        if (appContext == null) return null;
        return getToken(appContext);
    }

    public static void saveToken(Context context, String token) {
        SharedPreferences sp = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        sp.edit().putString(KEY_TOKEN, token).apply();
    }

    public static void clearToken(Context context) {
        SharedPreferences sp = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        sp.edit().remove(KEY_TOKEN).apply();
    }

    public static void clearToken() {
        if (appContext != null) {
            clearToken(appContext);
        }
    }

}
