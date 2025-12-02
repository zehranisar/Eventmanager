package com.example.eventmanager.api.response;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class EventListResponse extends BaseResponse {
    
    @SerializedName("count")
    private int count;
    
    @SerializedName("events")
    private List<EventData> events;
    
    public int getCount() { return count; }
    public void setCount(int count) { this.count = count; }
    
    public List<EventData> getEvents() { return events; }
    public void setEvents(List<EventData> events) { this.events = events; }
}

