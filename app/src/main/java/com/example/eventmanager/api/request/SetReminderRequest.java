package com.example.eventmanager.api.request;

import com.google.gson.annotations.SerializedName;

public class SetReminderRequest {
    
    @SerializedName("timing")
    private String timing;
    
    public SetReminderRequest(String timing) {
        this.timing = timing;
    }
    
    public String getTiming() {
        return timing;
    }
    
    public void setTiming(String timing) {
        this.timing = timing;
    }
}

