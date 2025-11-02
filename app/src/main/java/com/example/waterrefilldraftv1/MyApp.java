package com.example.waterrefilldraftv1;

import android.app.Application;
import com.example.waterrefilldraftv1.Global.network.TokenManager;

public class MyApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        // Initialize TokenManager globally (used in RetrofitClient)
        TokenManager.init(this);
    }
}
