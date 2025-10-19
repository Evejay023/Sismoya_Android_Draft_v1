package com.example.waterrefilldraftv1.Customer.network;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import java.security.cert.CertificateException;
import java.util.concurrent.TimeUnit;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

public class RetrofitClient {
    private static final String TAG = "RetrofitClient";
    private static final String BASE_URL = "https://sismoya.bsit3b.site/api/";
    private static Retrofit retrofit;

    public static Retrofit getInstance() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(createOkHttpClient())
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }

    private static OkHttpClient createOkHttpClient() {
        // Logging interceptor
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor(message ->
                Log.d(TAG, "OkHttp: " + message)
        );
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient.Builder builder = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .addInterceptor(chain -> {
                    Request original = chain.request();

                    // Log the request
                    Log.d(TAG, "Request URL: " + original.url());
                    Log.d(TAG, "Request Method: " + original.method());

                    // Attach token from SharedPreferences if present
                    String token = getToken();
                    Request.Builder requestBuilder = original.newBuilder();

                    if (token != null && !token.isEmpty()) {
                        requestBuilder.addHeader("Authorization", "Bearer " + token);
                        Log.d(TAG, "Added Authorization header");
                    }

                    // Add other headers
                    requestBuilder
                            .addHeader("Accept", "application/json")
                            .addHeader("Content-Type", "application/json");

                    Request request = requestBuilder.build();

                    try {
                        okhttp3.Response response = chain.proceed(request);
                        Log.d(TAG, "Response Code: " + response.code());
                        return response;
                    } catch (Exception e) {
                        Log.e(TAG, "Network Error: " + e.getMessage(), e);
                        throw e;
                    }
                })
                .addInterceptor(logging);

        // For development/testing: Trust all certificates
        // WARNING: Remove this in production or use proper certificate pinning
        try {
            final TrustManager[] trustAllCerts = new TrustManager[]{
                    new X509TrustManager() {
                        @Override
                        public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException {
                        }

                        @Override
                        public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException {
                        }

                        @Override
                        public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                            return new java.security.cert.X509Certificate[]{};
                        }
                    }
            };

            final SSLContext sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, trustAllCerts, new java.security.SecureRandom());
            final SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();

            builder.sslSocketFactory(sslSocketFactory, (X509TrustManager) trustAllCerts[0]);
            builder.hostnameVerifier(new HostnameVerifier() {
                @Override
                public boolean verify(String hostname, SSLSession session) {
                    Log.d(TAG, "Hostname verification: " + hostname);
                    return true; // Trust all hostnames for development
                }
            });
        } catch (Exception e) {
            Log.e(TAG, "Error setting up SSL: " + e.getMessage(), e);
        }

        return builder.build();
    }

    private static String getToken() {
        try {
            Class<?> cl = Class.forName("android.app.AppGlobals");
            android.app.Application app = (android.app.Application) cl.getMethod("getInitialApplication").invoke(null);
            if (app != null) {
                SharedPreferences prefs = app.getSharedPreferences("session", Context.MODE_PRIVATE);
                return prefs.getString("token", null);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error getting token: " + e.getMessage());
        }
        return null;
    }

    public static Retrofit getClient() {
        return getInstance();
    }

}