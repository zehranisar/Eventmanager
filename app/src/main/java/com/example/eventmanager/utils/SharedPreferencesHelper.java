package com.example.eventmanager.utils;

import android.content.Context;
import android.content.SharedPreferences;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.example.eventmanager.models.User;
import com.example.eventmanager.models.Event;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class SharedPreferencesHelper {
    private static final String PREFS_NAME = "EventManagerPrefs";
    private static final String KEY_IS_LOGGED_IN = "is_logged_in";
    private static final String KEY_CURRENT_USER = "current_user";
    private static final String KEY_USERS = "users";
    private static final String KEY_EVENTS = "events";
    private static final String KEY_REGISTERED_EVENTS = "registered_events_";
    private static final String KEY_REMINDERS = "reminders_";
    private static final String KEY_OTP = "otp_";
    private static final String KEY_OTP_EMAIL = "otp_email";

    private SharedPreferences prefs;
    private Gson gson;

    public SharedPreferencesHelper(Context context) {
        prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        gson = new Gson();
    }

    // User authentication methods
    public void setLoggedIn(boolean isLoggedIn) {
        prefs.edit().putBoolean(KEY_IS_LOGGED_IN, isLoggedIn).apply();
    }

    public boolean isLoggedIn() {
        return prefs.getBoolean(KEY_IS_LOGGED_IN, false);
    }

    public void setCurrentUser(User user) {
        String userJson = gson.toJson(user);
        prefs.edit().putString(KEY_CURRENT_USER, userJson).apply();
    }

    public User getCurrentUser() {
        String userJson = prefs.getString(KEY_CURRENT_USER, null);
        if (userJson == null) return null;
        return gson.fromJson(userJson, User.class);
    }

    public void logout() {
        prefs.edit().remove(KEY_CURRENT_USER).putBoolean(KEY_IS_LOGGED_IN, false).apply();
    }

    // User registration methods
    public void registerUser(User user) {
        List<User> users = getAllUsers();
        if (users == null) {
            users = new ArrayList<>();
            // Create default admin on first registration
            initializeDefaultAdmin(users);
        }
        user.setId(String.valueOf(System.currentTimeMillis()));
        // Set default role as student if not set
        if (user.getRole() == null || user.getRole().isEmpty()) {
            user.setRole("student");
        }
        users.add(user);
        String usersJson = gson.toJson(users);
        prefs.edit().putString(KEY_USERS, usersJson).apply();
    }

    private void initializeDefaultAdmin(List<User> users) {
        // Check if admin already exists
        boolean adminExists = false;
        for (User user : users) {
            if (user.isAdmin()) {
                adminExists = true;
                break;
            }
        }
        
        // Create default admin if it doesn't exist
        if (!adminExists) {
            User admin = new User();
            admin.setId("admin_001");
            admin.setName("Admin");
            admin.setEmail("admin@university.edu");
            admin.setPassword("admin123");
            admin.setRole("admin");
            users.add(admin);
        }
    }

    public User loginUser(String email, String password) {
        List<User> users = getAllUsers();
        if (users == null) return null;

        for (User user : users) {
            if (user.getEmail().equals(email) && user.getPassword().equals(password)) {
                return user;
            }
        }
        return null;
    }

    public boolean userExists(String email) {
        List<User> users = getAllUsers();
        if (users == null) return false;

        for (User user : users) {
            if (user.getEmail().equals(email)) {
                return true;
            }
        }
        return false;
    }

    private List<User> getAllUsers() {
        String usersJson = prefs.getString(KEY_USERS, null);
        List<User> users;
        if (usersJson == null) {
            users = new ArrayList<>();
            // Initialize default admin
            initializeDefaultAdmin(users);
            String json = gson.toJson(users);
            prefs.edit().putString(KEY_USERS, json).apply();
        } else {
            Type type = new TypeToken<List<User>>(){}.getType();
            users = gson.fromJson(usersJson, type);
        }
        return users;
    }

    // Event methods
    public void saveEvents(List<Event> events) {
        String eventsJson = gson.toJson(events);
        prefs.edit().putString(KEY_EVENTS, eventsJson).apply();
    }

    public List<Event> getEvents() {
        String eventsJson = prefs.getString(KEY_EVENTS, null);
        if (eventsJson == null) {
            // Initialize with sample events
            return getSampleEvents();
        }
        Type type = new TypeToken<List<Event>>(){}.getType();
        return gson.fromJson(eventsJson, type);
    }

    public boolean deleteEvent(String eventId) {
        List<Event> events = getEvents();
        for (int i = 0; i < events.size(); i++) {
            if (events.get(i).getId().equals(eventId)) {
                events.remove(i);
                saveEvents(events);
                return true;
            }
        }
        return false;
    }

    private List<Event> getSampleEvents() {
        List<Event> events = new ArrayList<>();
        events.add(new Event("1", "Tech Conference 2024", "Annual technology conference featuring latest innovations", "2024-12-15", "09:00", "Main Auditorium", "Technology"));
        events.add(new Event("2", "Cultural Festival", "Celebrate diverse cultures with music, food, and performances", "2024-12-20", "14:00", "University Grounds", "Cultural"));
        events.add(new Event("3", "Career Fair", "Meet with top employers and explore career opportunities", "2024-12-25", "10:00", "Convention Center", "Career"));
        events.add(new Event("4", "Sports Day", "Inter-department sports competition", "2025-01-05", "08:00", "Sports Complex", "Sports"));
        events.add(new Event("5", "Workshop on AI", "Hands-on workshop on Artificial Intelligence and Machine Learning", "2025-01-10", "13:00", "Computer Lab 3", "Workshop"));
        saveEvents(events);
        return events;
    }

    // Registration tracking
    public void registerForEvent(String userId, String eventId) {
        List<String> registeredEvents = getRegisteredEvents(userId);
        if (!registeredEvents.contains(eventId)) {
            registeredEvents.add(eventId);
            String json = gson.toJson(registeredEvents);
            prefs.edit().putString(KEY_REGISTERED_EVENTS + userId, json).apply();
        }
    }

    public boolean isRegisteredForEvent(String userId, String eventId) {
        List<String> registeredEvents = getRegisteredEvents(userId);
        return registeredEvents.contains(eventId);
    }

    public List<String> getRegisteredEvents(String userId) {
        String json = prefs.getString(KEY_REGISTERED_EVENTS + userId, null);
        if (json == null) return new ArrayList<>();
        Type type = new TypeToken<List<String>>(){}.getType();
        return gson.fromJson(json, type);
    }

    // Reminder tracking
    public void setReminder(String userId, String eventId) {
        List<String> reminders = getReminders(userId);
        if (!reminders.contains(eventId)) {
            reminders.add(eventId);
            String json = gson.toJson(reminders);
            prefs.edit().putString(KEY_REMINDERS + userId, json).apply();
        }
    }

    public boolean hasReminder(String userId, String eventId) {
        List<String> reminders = getReminders(userId);
        return reminders.contains(eventId);
    }

    public List<String> getReminders(String userId) {
        String json = prefs.getString(KEY_REMINDERS + userId, null);
        if (json == null) return new ArrayList<>();
        Type type = new TypeToken<List<String>>(){}.getType();
        return gson.fromJson(json, type);
    }

    // Password reset methods
    public String generateOTP(String email) {
        // Static OTP for now (will be dynamic with backend)
        String otpString = "123456";
        
        // Store OTP and email
        prefs.edit().putString(KEY_OTP, otpString).apply();
        prefs.edit().putString(KEY_OTP_EMAIL, email).apply();
        
        return otpString;
    }

    public boolean validateOTP(String enteredOTP) {
        String storedOTP = prefs.getString(KEY_OTP, null);
        return storedOTP != null && storedOTP.equals(enteredOTP);
    }

    public String getOTPEmail() {
        return prefs.getString(KEY_OTP_EMAIL, null);
    }

    public boolean updatePassword(String email, String newPassword) {
        List<User> users = getAllUsers();
        if (users == null) return false;

        for (User user : users) {
            if (user.getEmail().equals(email)) {
                user.setPassword(newPassword);
                String usersJson = gson.toJson(users);
                prefs.edit().putString(KEY_USERS, usersJson).apply();
                // Clear OTP data
                prefs.edit().remove(KEY_OTP).remove(KEY_OTP_EMAIL).apply();
                return true;
            }
        }
        return false;
    }
}

