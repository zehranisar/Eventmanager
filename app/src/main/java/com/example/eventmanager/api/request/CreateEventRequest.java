package com.example.eventmanager.api.request;

import com.google.gson.annotations.SerializedName;

public class CreateEventRequest {
    
    @SerializedName("title")
    private String title;
    
    @SerializedName("description")
    private String description;
    
    @SerializedName("date")
    private String date; // Format: YYYY-MM-DD
    
    @SerializedName("time")
    private String time; // Format: HH:MM
    
    @SerializedName("location")
    private String location;
    
    @SerializedName("category")
    private String category;
    
    @SerializedName("max_participants")
    private int maxParticipants;
    
    public CreateEventRequest(String title, String description, String date, String time, 
                              String location, String category, int maxParticipants) {
        this.title = title;
        this.description = description;
        this.date = date;
        this.time = time;
        this.location = location;
        this.category = category;
        this.maxParticipants = maxParticipants;
    }
    
    // Getters and Setters
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }
    
    public String getTime() { return time; }
    public void setTime(String time) { this.time = time; }
    
    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }
    
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    
    public int getMaxParticipants() { return maxParticipants; }
    public void setMaxParticipants(int maxParticipants) { this.maxParticipants = maxParticipants; }
}

