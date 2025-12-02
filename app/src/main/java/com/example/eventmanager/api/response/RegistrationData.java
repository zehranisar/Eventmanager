package com.example.eventmanager.api.response;

import com.google.gson.annotations.SerializedName;

public class RegistrationData {
    
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
    
    @SerializedName("event_location")
    private String eventLocation;
    
    @SerializedName("user_name")
    private String userName;
    
    @SerializedName("registered_at")
    private String registeredAt;
    
    // Getters
    public int getId() { return id; }
    public int getUser() { return user; }
    public int getEvent() { return event; }
    public String getEventTitle() { return eventTitle; }
    public String getEventDate() { return eventDate; }
    public String getEventTime() { return eventTime; }
    public String getEventLocation() { return eventLocation; }
    public String getUserName() { return userName; }
    public String getRegisteredAt() { return registeredAt; }
    
    // Setters
    public void setId(int id) { this.id = id; }
    public void setUser(int user) { this.user = user; }
    public void setEvent(int event) { this.event = event; }
    public void setEventTitle(String eventTitle) { this.eventTitle = eventTitle; }
    public void setEventDate(String eventDate) { this.eventDate = eventDate; }
    public void setEventTime(String eventTime) { this.eventTime = eventTime; }
    public void setEventLocation(String eventLocation) { this.eventLocation = eventLocation; }
    public void setUserName(String userName) { this.userName = userName; }
    public void setRegisteredAt(String registeredAt) { this.registeredAt = registeredAt; }
}

