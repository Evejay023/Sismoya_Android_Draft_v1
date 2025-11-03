package com.example.waterrefilldraftv1.Global.network;

import android.content.Context;
import android.util.Log;

import java.security.cert.CertificateException;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Centralized Retrofit configuration for network requests.
 * Handles SSL, logging, headers, and authentication token.
 */
public class RetrofitClient {
    private static final String TAG = "RetrofitClient";
    private static final String BASE_URL = "https://sismoya.bsit3b.site/api/";

    // Toggle this off in production!
    private static final boolean TRUST_ALL_SSL = true;

    private static Retrofit retrofit;

    /**
     * Returns a singleton Retrofit instance.
     */
    public static Retrofit getInstance() {
        if (retrofit == null) {
            synchronized (RetrofitClient.class) {
                if (retrofit == null) {
                    retrofit = new Retrofit.Builder()
                            .baseUrl(BASE_URL)
                            .client(createOkHttpClient())
                            .addConverterFactory(GsonConverterFactory.create())
                            .build();
                }
            }
        }
        return retrofit;
    }

    /**
     * Provides an ApiService instance directly.
     */
    public static ApiService getApiService() {
        return getInstance().create(ApiService.class);
    }

    /**
     * Configures OkHttpClient with token interceptor, logging, SSL, and timeouts.
     */
    private static OkHttpClient createOkHttpClient() {
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor(
                message -> Log.d(TAG, "HTTP: " + message)
        );
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient.Builder builder = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .addInterceptor(chain -> {
                    Request original = chain.request();

                    Request.Builder requestBuilder = original.newBuilder()
                            .addHeader("Accept", "application/json")
                            .addHeader("Content-Type", "application/json");

                    Context context = TokenManager.getAppContext();
                    String token = (context != null) ? TokenManager.getToken(context) : null;

                    if (token != null && !token.isEmpty()) {
                        requestBuilder.addHeader("Authorization", "Bearer " + token);
                        Log.d(TAG, "✅ Token attached to request");
                    } else {
                        Log.w(TAG, "⚠️ No token available");
                    }

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

        // ✅ Disable caching globally
        builder.cache(null);

        if (TRUST_ALL_SSL) {
            setupUnsafeSSL(builder);
        }

        return builder.build();
    }


    /**
     * ⚠️ Development-only SSL configuration (trusts all certs).
     * Do NOT enable this in production builds.
     */
    private static void setupUnsafeSSL(OkHttpClient.Builder builder) {
        try {
            final TrustManager[] trustAllCerts = new TrustManager[]{
                    new X509TrustManager() {
                        @Override
                        public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException {}
                        @Override
                        public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException {}
                        @Override
                        public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                            return new java.security.cert.X509Certificate[]{};
                        }
                    }
            };

            SSLContext sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, trustAllCerts, new java.security.SecureRandom());
            SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();

            builder.sslSocketFactory(sslSocketFactory, (X509TrustManager) trustAllCerts[0]);
            builder.hostnameVerifier(new HostnameVerifier() {
                @Override
                public boolean verify(String hostname, SSLSession session) {
                    Log.d(TAG, "Bypassing hostname verification for: " + hostname);
                    return true;
                }
            });
        } catch (Exception e) {
            Log.e(TAG, "Error setting up SSL: " + e.getMessage(), e);
        }
    }

    /**
     * Backward-compatible method alias.
     */
    public static Retrofit getClient() {
        return getInstance();
    }
}
