package com.example.eventmanager.api.response;

import com.google.gson.annotations.SerializedName;

public class DashboardResponse extends BaseResponse {
    
    @SerializedName("stats")
    private DashboardStats stats;
    
    @SerializedName("user")
    private UserData user;
    
    public DashboardStats getStats() { return stats; }
    public void setStats(DashboardStats stats) { this.stats = stats; }
    
    public UserData getUser() { return user; }
    public void setUser(UserData user) { this.user = user; }
    
    public static class DashboardStats {
        @SerializedName("total_events")
        private int totalEvents;
        
        @SerializedName("my_registrations")
        private int myRegistrations;
        
        @SerializedName("my_reminders")
        private int myReminders;
        
        @SerializedName("events_created")
        private int eventsCreated; // Admin only
        
        @SerializedName("total_users")
        private int totalUsers; // Admin only
        
        // Getters
        public int getTotalEvents() { return totalEvents; }
        public int getMyRegistrations() { return myRegistrations; }
        public int getMyReminders() { return myReminders; }
        public int getEventsCreated() { return eventsCreated; }
        public int getTotalUsers() { return totalUsers; }
    }
}

