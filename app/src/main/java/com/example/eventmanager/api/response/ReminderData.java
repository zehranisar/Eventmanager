package com.example.eventmanager.api.response;

import com.google.gson.annotations.SerializedName;

public class ReminderData {
    
    @SerializedName("id")
    private int id;
    
    @SerializedName("user")
    private int user;
    
    @SerializedName("event")
    private int event;
    
    @SerializedName("event_title")
    private String eventTitle;
    
    @SerializedName("event_date")
    private String eventDate;
    
    @SerializedName("event_time")
    private String eventTime;
    
    @SerializedName("remind_at")
    private String remindAt;
    
    @SerializedName("is_sent")
    private boolean isSent;
    
    @SerializedName("created_at")
    private String createdAt;
    
    // Getters
    public int getId() { return id; }
    public int getUser() { return user; }
    public int getEvent() { return event; }
    public String getEventTitle() { return eventTitle; }
    public String getEventDate() { return eventDate; }
    public String getEventTime() { return eventTime; }
    public String getRemindAt() { return remindAt; }
    public boolean isSent() { return isSent; }
    public String getCreatedAt() { return createdAt; }
    
    // Setters
    public void setId(int id) { this.id = id; }
    public void setUser(int user) { this.user = user; }
    public void setEvent(int event) { this.event = event; }
    public void setEventTitle(String eventTitle) { this.eventTitle = eventTitle; }
    public void setEventDate(String eventDate) { this.eventDate = eventDate; }
    public void setEventTime(String eventTime) { this.eventTime = eventTime; }
    public void setRemindAt(String remindAt) { this.remindAt = remindAt; }
    public void setSent(boolean sent) { isSent = sent; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
}

