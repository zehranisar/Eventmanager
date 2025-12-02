package com.example.eventmanager.api.response;

import com.google.gson.annotations.SerializedName;

public class ReminderResponse extends BaseResponse {
    
    @SerializedName("reminder")
    private ReminderData reminder;
    
    public ReminderData getReminder() { return reminder; }
    public void setReminder(ReminderData reminder) { this.reminder = reminder; }
}

