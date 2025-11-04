package com.example.waterrefilldraftv1.Customer.utils;

import android.content.Context;
import android.content.SharedPreferences;
import com.example.waterrefilldraftv1.Customer.models.User;
import com.google.gson.Gson;

public class SessionStore {
    private final SharedPreferences prefs;
    private final Gson gson = new Gson();

    public SessionStore(Context context) {
        this.prefs = context.getSharedPreferences("session", Context.MODE_PRIVATE);
    }

    public void saveToken(String token) {
        prefs.edit().putString("token", token).apply();
    }

    public String getToken() {
        return prefs.getString("token", null);
    }

    public void saveUser(User user) {
        prefs.edit().putString("user", gson.toJson(user)).apply();
    }

    public User getUser() {
        String j = prefs.getString("user", null);
        return j == null ? null : gson.fromJson(j, User.class);
    }

    public void clear() {
        prefs.edit().clear().apply();
    }
}




