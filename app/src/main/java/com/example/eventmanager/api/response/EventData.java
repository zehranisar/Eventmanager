package com.example.eventmanager.api.response;

import com.google.gson.annotations.SerializedName;

public class EventData {
    
    @SerializedName("id")
    private int id;
    
    @SerializedName("title")
    private String title;
    
    @SerializedName("description")
    private String description;
    
    @SerializedName("date")
    private String date;
    
    @SerializedName("time")
    private String time;
    
    @SerializedName("location")
    private String location;
    
    @SerializedName("category")
    private String category;
    
    @SerializedName("created_by")
    private int createdBy;
    
    @SerializedName("created_by_name")
    private String createdByName;
    
    @SerializedName("max_participants")
    private int maxParticipants;
    
    @SerializedName("registered_count")
    private int registeredCount;
    
    @SerializedName("is_full")
    private boolean isFull;
    
    @SerializedName("is_active")
    private boolean isActive;
    
    @SerializedName("is_registered")
    private boolean isRegistered;
    
    @SerializedName("has_reminder")
    private boolean hasReminder;
    
    @SerializedName("created_at")
    private String createdAt;
    
    // Getters
    public int getId() { return id; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public String getDate() { return date; }
    public String getTime() { return time; }
    public String getLocation() { return location; }
    public String getCategory() { return category; }
    public int getCreatedBy() { return createdBy; }
    public String getCreatedByName() { return createdByName; }
    public int getMaxParticipants() { return maxParticipants; }
    public int getRegisteredCount() { return registeredCount; }
    public boolean isFull() { return isFull; }
    public boolean isActive() { return isActive; }
    public boolean isRegistered() { return isRegistered; }
    public boolean hasReminder() { return hasReminder; }
    public String getCreatedAt() { return createdAt; }
    
    // Setters
    public void setId(int id) { this.id = id; }
    public void setTitle(String title) { this.title = title; }
    public void setDescription(String description) { this.description = description; }
    public void setDate(String date) { this.date = date; }
    public void setTime(String time) { this.time = time; }
    public void setLocation(String location) { this.location = location; }
    public void setCategory(String category) { this.category = category; }
    public void setCreatedBy(int createdBy) { this.createdBy = createdBy; }
    public void setCreatedByName(String createdByName) { this.createdByName = createdByName; }
    public void setMaxParticipants(int maxParticipants) { this.maxParticipants = maxParticipants; }
    public void setRegisteredCount(int registeredCount) { this.registeredCount = registeredCount; }
    public void setFull(boolean full) { isFull = full; }
    public void setActive(boolean active) { isActive = active; }
    public void setRegistered(boolean registered) { isRegistered = registered; }
    public void setHasReminder(boolean hasReminder) { this.hasReminder = hasReminder; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
}

