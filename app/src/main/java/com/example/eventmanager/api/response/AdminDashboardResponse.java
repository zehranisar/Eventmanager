package com.example.eventmanager.api.response;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class AdminDashboardResponse extends BaseResponse {
    
    @SerializedName("stats")
    private DashboardStats stats;
    
    @SerializedName("event_registration_details")
    private List<EventRegistrationDetail> eventRegistrationDetails;
    
    @SerializedName("user_registration_details")
    private List<UserRegistrationDetail> userRegistrationDetails;
    
    @SerializedName("top_events")
    private List<TopEvent> topEvents;
    
    public DashboardStats getStats() {
        return stats;
    }
    
    public void setStats(DashboardStats stats) {
        this.stats = stats;
    }
    
    public List<EventRegistrationDetail> getEventRegistrationDetails() {
        return eventRegistrationDetails;
    }
    
    public void setEventRegistrationDetails(List<EventRegistrationDetail> eventRegistrationDetails) {
        this.eventRegistrationDetails = eventRegistrationDetails;
    }
    
    public List<UserRegistrationDetail> getUserRegistrationDetails() {
        return userRegistrationDetails;
    }
    
    public void setUserRegistrationDetails(List<UserRegistrationDetail> userRegistrationDetails) {
        this.userRegistrationDetails = userRegistrationDetails;
    }
    
    public List<TopEvent> getTopEvents() {
        return topEvents;
    }
    
    public void setTopEvents(List<TopEvent> topEvents) {
        this.topEvents = topEvents;
    }
    
    // Inner classes for nested data
    public static class DashboardStats {
        @SerializedName("total_users")
        private int totalUsers;
        
        @SerializedName("total_students")
        private int totalStudents;
        
        @SerializedName("total_admins")
        private int totalAdmins;
        
        @SerializedName("total_events")
        private int totalEvents;
        
        @SerializedName("active_events")
        private int activeEvents;
        
        @SerializedName("total_registrations")
        private int totalRegistrations;
        
        @SerializedName("recent_registrations")
        private int recentRegistrations;
        
        @SerializedName("recent_events")
        private int recentEvents;
        
        // Getters and setters
        public int getTotalUsers() { return totalUsers; }
        public void setTotalUsers(int totalUsers) { this.totalUsers = totalUsers; }
        
        public int getTotalStudents() { return totalStudents; }
        public void setTotalStudents(int totalStudents) { this.totalStudents = totalStudents; }
        
        public int getTotalAdmins() { return totalAdmins; }
        public void setTotalAdmins(int totalAdmins) { this.totalAdmins = totalAdmins; }
        
        public int getTotalEvents() { return totalEvents; }
        public void setTotalEvents(int totalEvents) { this.totalEvents = totalEvents; }
        
        public int getActiveEvents() { return activeEvents; }
        public void setActiveEvents(int activeEvents) { this.activeEvents = activeEvents; }
        
        public int getTotalRegistrations() { return totalRegistrations; }
        public void setTotalRegistrations(int totalRegistrations) { this.totalRegistrations = totalRegistrations; }
        
        public int getRecentRegistrations() { return recentRegistrations; }
        public void setRecentRegistrations(int recentRegistrations) { this.recentRegistrations = recentRegistrations; }
        
        public int getRecentEvents() { return recentEvents; }
        public void setRecentEvents(int recentEvents) { this.recentEvents = recentEvents; }
    }
    
    public static class EventRegistrationDetail {
        @SerializedName("event_id")
        private int eventId;
        
        @SerializedName("event_title")
        private String eventTitle;
        
        @SerializedName("event_date")
        private String eventDate;
        
        @SerializedName("event_time")
        private String eventTime;
        
        @SerializedName("event_location")
        private String eventLocation;
        
        @SerializedName("event_category")
        private String eventCategory;
        
        @SerializedName("registration_count")
        private int registrationCount;
        
        @SerializedName("max_participants")
        private int maxParticipants;
        
        @SerializedName("is_full")
        private boolean isFull;
        
        @SerializedName("is_active")
        private boolean isActive;
        
        @SerializedName("registrations")
        private List<RegistrationData> registrations;
        
        // Getters and setters
        public int getEventId() { return eventId; }
        public void setEventId(int eventId) { this.eventId = eventId; }
        
        public String getEventTitle() { return eventTitle; }
        public void setEventTitle(String eventTitle) { this.eventTitle = eventTitle; }
        
        public String getEventDate() { return eventDate; }
        public void setEventDate(String eventDate) { this.eventDate = eventDate; }
        
        public String getEventTime() { return eventTime; }
        public void setEventTime(String eventTime) { this.eventTime = eventTime; }
        
        public String getEventLocation() { return eventLocation; }
        public void setEventLocation(String eventLocation) { this.eventLocation = eventLocation; }
        
        public String getEventCategory() { return eventCategory; }
        public void setEventCategory(String eventCategory) { this.eventCategory = eventCategory; }
        
        public int getRegistrationCount() { return registrationCount; }
        public void setRegistrationCount(int registrationCount) { this.registrationCount = registrationCount; }
        
        public int getMaxParticipants() { return maxParticipants; }
        public void setMaxParticipants(int maxParticipants) { this.maxParticipants = maxParticipants; }
        
        public boolean isFull() { return isFull; }
        public void setFull(boolean full) { isFull = full; }
        
        public boolean isActive() { return isActive; }
        public void setActive(boolean active) { isActive = active; }
        
        public List<RegistrationData> getRegistrations() { return registrations; }
        public void setRegistrations(List<RegistrationData> registrations) { this.registrations = registrations; }
    }
    
    public static class UserRegistrationDetail {
        @SerializedName("user_id")
        private int userId;
        
        @SerializedName("user_name")
        private String userName;
        
        @SerializedName("user_email")
        private String userEmail;
        
        @SerializedName("user_role")
        private String userRole;
        
        @SerializedName("registration_count")
        private int registrationCount;
        
        @SerializedName("registrations")
        private List<RegistrationData> registrations;
        
        // Getters and setters
        public int getUserId() { return userId; }
        public void setUserId(int userId) { this.userId = userId; }
        
        public String getUserName() { return userName; }
        public void setUserName(String userName) { this.userName = userName; }
        
        public String getUserEmail() { return userEmail; }
        public void setUserEmail(String userEmail) { this.userEmail = userEmail; }
        
        public String getUserRole() { return userRole; }
        public void setUserRole(String userRole) { this.userRole = userRole; }
        
        public int getRegistrationCount() { return registrationCount; }
        public void setRegistrationCount(int registrationCount) { this.registrationCount = registrationCount; }
        
        public List<RegistrationData> getRegistrations() { return registrations; }
        public void setRegistrations(List<RegistrationData> registrations) { this.registrations = registrations; }
    }
    
    public static class RegistrationData {
        @SerializedName("id")
        private int id;
        
        @SerializedName("user_id")
        private int userId;
        
        @SerializedName("user_name")
        private String userName;
        
        @SerializedName("user_email")
        private String userEmail;
        
        @SerializedName("event_id")
        private int eventId;
        
        @SerializedName("event_title")
        private String eventTitle;
        
        @SerializedName("event_date")
        private String eventDate;
        
        @SerializedName("event_time")
        private String eventTime;
        
        @SerializedName("event_location")
        private String eventLocation;
        
        @SerializedName("name")
        private String name;
        
        @SerializedName("email")
        private String email;
        
        @SerializedName("phone")
        private String phone;
        
        @SerializedName("student_id")
        private String studentId;
        
        @SerializedName("registered_at")
        private String registeredAt;
        
        // Getters and setters
        public int getId() { return id; }
        public void setId(int id) { this.id = id; }
        
        public int getUserId() { return userId; }
        public void setUserId(int userId) { this.userId = userId; }
        
        public String getUserName() { return userName; }
        public void setUserName(String userName) { this.userName = userName; }
        
        public String getUserEmail() { return userEmail; }
        public void setUserEmail(String userEmail) { this.userEmail = userEmail; }
        
        public int getEventId() { return eventId; }
        public void setEventId(int eventId) { this.eventId = eventId; }
        
        public String getEventTitle() { return eventTitle; }
        public void setEventTitle(String eventTitle) { this.eventTitle = eventTitle; }
        
        public String getEventDate() { return eventDate; }
        public void setEventDate(String eventDate) { this.eventDate = eventDate; }
        
        public String getEventTime() { return eventTime; }
        public void setEventTime(String eventTime) { this.eventTime = eventTime; }
        
        public String getEventLocation() { return eventLocation; }
        public void setEventLocation(String eventLocation) { this.eventLocation = eventLocation; }
        
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        
        public String getPhone() { return phone; }
        public void setPhone(String phone) { this.phone = phone; }
        
        public String getStudentId() { return studentId; }
        public void setStudentId(String studentId) { this.studentId = studentId; }
        
        public String getRegisteredAt() { return registeredAt; }
        public void setRegisteredAt(String registeredAt) { this.registeredAt = registeredAt; }
    }
    
    public static class TopEvent {
        @SerializedName("event_id")
        private int eventId;
        
        @SerializedName("event_title")
        private String eventTitle;
        
        @SerializedName("event_date")
        private String eventDate;
        
        @SerializedName("registration_count")
        private int registrationCount;
        
        // Getters and setters
        public int getEventId() { return eventId; }
        public void setEventId(int eventId) { this.eventId = eventId; }
        
        public String getEventTitle() { return eventTitle; }
        public void setEventTitle(String eventTitle) { this.eventTitle = eventTitle; }
        
        public String getEventDate() { return eventDate; }
        public void setEventDate(String eventDate) { this.eventDate = eventDate; }
        
        public int getRegistrationCount() { return registrationCount; }
        public void setRegistrationCount(int registrationCount) { this.registrationCount = registrationCount; }
    }
}

