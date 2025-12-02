package com.example.eventmanager.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.eventmanager.api.response.UserData;
import com.google.gson.Gson;

public class SessionManager {
    
    private static final String PREF_NAME = "EventManagerPrefs";
    private static final String KEY_ACCESS_TOKEN = "access_token";
    private static final String KEY_REFRESH_TOKEN = "refresh_token";
    private static final String KEY_USER_DATA = "user_data";
    private static final String KEY_IS_LOGGED_IN = "is_logged_in";
    
    private SharedPreferences prefs;
    private SharedPreferences.Editor editor;
    private Gson gson;
    
    public SessionManager(Context context) {
        prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = prefs.edit();
        gson = new Gson();
    }
    
    // Save login session
    public void saveLoginSession(String accessToken, String refreshToken, UserData user) {
        editor.putString(KEY_ACCESS_TOKEN, accessToken);
        editor.putString(KEY_REFRESH_TOKEN, refreshToken);
        editor.putString(KEY_USER_DATA, gson.toJson(user));
        editor.putBoolean(KEY_IS_LOGGED_IN, true);
        editor.apply();
    }
    
    // Check if user is logged in
    public boolean isLoggedIn() {
        return prefs.getBoolean(KEY_IS_LOGGED_IN, false);
    }
    
    // Get access token
    public String getAccessToken() {
        return prefs.getString(KEY_ACCESS_TOKEN, null);
    }
    
    // Get refresh token
    public String getRefreshToken() {
        return prefs.getString(KEY_REFRESH_TOKEN, null);
    }
    
    // Get current user
    public UserData getCurrentUser() {
        String userJson = prefs.getString(KEY_USER_DATA, null);
        if (userJson != null) {
            return gson.fromJson(userJson, UserData.class);
        }
        return null;
    }
    
    // Check if current user is admin
    public boolean isAdmin() {
        UserData user = getCurrentUser();
        return user != null && user.isAdmin();
    }
    
    // Get user role
    public String getUserRole() {
        UserData user = getCurrentUser();
        return user != null ? user.getRole() : null;
    }
    
    // Get user name
    public String getUserName() {
        UserData user = getCurrentUser();
        return user != null ? user.getName() : null;
    }
    
    // Get user email
    public String getUserEmail() {
        UserData user = getCurrentUser();
        return user != null ? user.getEmail() : null;
    }
    
    // Update user data
    public void updateUserData(UserData user) {
        editor.putString(KEY_USER_DATA, gson.toJson(user));
        editor.apply();
    }
    
    // Logout - clear all session data
    public void logout() {
        editor.clear();
        editor.apply();
    }
}

