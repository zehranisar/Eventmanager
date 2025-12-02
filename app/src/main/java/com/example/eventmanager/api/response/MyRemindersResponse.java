package com.example.eventmanager.api.response;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class MyRemindersResponse extends BaseResponse {
    
    @SerializedName("count")
    private int count;
    
    @SerializedName("reminders")
    private List<ReminderData> reminders;
    
    public int getCount() { return count; }
    public void setCount(int count) { this.count = count; }
    
    public List<ReminderData> getReminders() { return reminders; }
    public void setReminders(List<ReminderData> reminders) { this.reminders = reminders; }
}

