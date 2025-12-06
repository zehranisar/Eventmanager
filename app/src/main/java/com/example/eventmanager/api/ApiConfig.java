package com.example.eventmanager.api;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.util.Log;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import java.util.concurrent.TimeUnit;

public class ApiConfig {
    
    private static final String TAG = "ApiConfig";
    private static final String PREF_NAME = "EventManagerPrefs";
    private static final String PREF_SERVER_IP = "server_ip";
    private static final String PREF_SERVER_PORT = "server_port";
    
    // Default configuration
    private static final String DEFAULT_PORT = "8000";
    
    // Connection methods:
    // 1. For Android Emulator: Use "10.0.2.2" (maps to host machine's localhost)
    // 2. For Real Device (USB): Use "127.0.0.1" + run: adb reverse tcp:8000 tcp:8000
    // 3. For Real Device (WiFi): Use your computer's IP address (e.g., "192.168.1.100")
    
    private static Retrofit retrofit = null;
    private static ApiService apiService = null;
    
    /**
     * Get the server IP address based on device type and saved preferences
     */
    private static String getServerIp(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        String savedIp = prefs.getString(PREF_SERVER_IP, null);
        
        if (savedIp != null && !savedIp.isEmpty()) {
            Log.d(TAG, "Using saved server IP: " + savedIp);
            return savedIp;
        }
        
        // Auto-detect based on device type
        if (isEmulator()) {
            // Android Emulator - use special IP that maps to host machine
            String emulatorIp = "10.0.2.2";
            Log.d(TAG, "Emulator detected, using: " + emulatorIp);
            return emulatorIp;
        } else {
            // Real device - default to localhost (requires ADB reverse)
            // User should set their computer's IP address for WiFi connection
            String defaultIp = "127.0.0.1";
            Log.d(TAG, "Real device detected, using: " + defaultIp + " (Set your computer's IP for WiFi)");
            return defaultIp;
        }
    }
    
    /**
     * Get the server port from preferences or use default
     */
    private static String getServerPort(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        return prefs.getString(PREF_SERVER_PORT, DEFAULT_PORT);
    }
    
    /**
     * Build the base URL dynamically
     */
    private static String getBaseUrl(Context context) {
        String ip = getServerIp(context);
        String port = getServerPort(context);
        String baseUrl = "http://" + ip + ":" + port + "/api/";
        Log.d(TAG, "Base URL: " + baseUrl);
        return baseUrl;
    }
    
    /**
     * Check if running on Android Emulator
     */
    private static boolean isEmulator() {
        return Build.FINGERPRINT.startsWith("generic")
                || Build.FINGERPRINT.startsWith("unknown")
                || Build.MODEL.contains("google_sdk")
                || Build.MODEL.contains("Emulator")
                || Build.MODEL.contains("Android SDK built for x86")
                || Build.MANUFACTURER.contains("Genymotion")
                || (Build.BRAND.startsWith("generic") && Build.DEVICE.startsWith("generic"))
                || "google_sdk".equals(Build.PRODUCT);
    }
    
    public static Retrofit getRetrofit(Context context) {
        if (retrofit == null) {
            String baseUrl = getBaseUrl(context);
            Log.d(TAG, "Initializing Retrofit with URL: " + baseUrl);
            
            // Logging interceptor for debugging
            HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor(message -> {
                Log.d(TAG, message);
            });
            loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
            
            // Auth interceptor to add token to requests
            Interceptor authInterceptor = chain -> {
                Request original = chain.request();
                
                // Get token from SharedPreferences
                SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
                String token = prefs.getString("access_token", null);
                
                Request.Builder requestBuilder = original.newBuilder();
                
                if (token != null && !token.isEmpty()) {
                    requestBuilder.addHeader("Authorization", "Bearer " + token);
                }
                
                requestBuilder.addHeader("Content-Type", "application/json");
                
                return chain.proceed(requestBuilder.build());
            };
            
            OkHttpClient client = new OkHttpClient.Builder()
                    .addInterceptor(loggingInterceptor)
                    .addInterceptor(authInterceptor)
                    .connectTimeout(30, TimeUnit.SECONDS)  // Increased timeout for USB connection
                    .readTimeout(30, TimeUnit.SECONDS)
                    .writeTimeout(30, TimeUnit.SECONDS)
                    .retryOnConnectionFailure(true)  // Retry on connection failure
                    .build();
            
            retrofit = new Retrofit.Builder()
                    .baseUrl(baseUrl)
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }
    
    /**
     * Set server IP address (for WiFi connection on real device)
     * Example: setServerIp(context, "192.168.1.100")
     */
    public static void setServerIp(Context context, String ip) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(PREF_SERVER_IP, ip);
        editor.apply();
        
        // Reset retrofit to use new IP
        retrofit = null;
        apiService = null;
        
        Log.d(TAG, "Server IP updated to: " + ip);
    }
    
    /**
     * Set server port
     */
    public static void setServerPort(Context context, String port) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(PREF_SERVER_PORT, port);
        editor.apply();
        
        // Reset retrofit to use new port
        retrofit = null;
        apiService = null;
        
        Log.d(TAG, "Server port updated to: " + port);
    }
    
    /**
     * Get current server IP
     */
    public static String getCurrentServerIp(Context context) {
        return getServerIp(context);
    }
    
    /**
     * Get current server port
     */
    public static String getCurrentServerPort(Context context) {
        return getServerPort(context);
    }
    
    /**
     * Reset to default configuration
     */
    public static void resetToDefault(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.remove(PREF_SERVER_IP);
        editor.remove(PREF_SERVER_PORT);
        editor.apply();
        
        // Reset retrofit
        retrofit = null;
        apiService = null;
        
        Log.d(TAG, "Reset to default configuration");
    }
    
    public static ApiService getApiService(Context context) {
        if (apiService == null) {
            apiService = getRetrofit(context).create(ApiService.class);
        }
        return apiService;
    }
    
    // Save tokens to SharedPreferences
    public static void saveTokens(Context context, String accessToken, String refreshToken) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("access_token", accessToken);
        editor.putString("refresh_token", refreshToken);
        editor.apply();
    }
    
    // Get access token
    public static String getAccessToken(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        return prefs.getString("access_token", null);
    }
    
    // Clear tokens (logout)
    public static void clearTokens(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.remove("access_token");
        editor.remove("refresh_token");
        editor.apply();
        
        // Reset retrofit to force new instance
        retrofit = null;
        apiService = null;
    }
    
    // Check if user is logged in
    public static boolean isLoggedIn(Context context) {
        return getAccessToken(context) != null;
    }
    
    // Get current base URL (for debugging)
    public static String getBaseUrlString(Context context) {
        return getBaseUrl(context);
    }
}
