package com.example.eventmanager.api.response;

import com.google.gson.annotations.SerializedName;

public class EventDetailResponse extends BaseResponse {
    
    @SerializedName("event")
    private EventData event;
    
    public EventData getEvent() { return event; }
    public void setEvent(EventData event) { this.event = event; }
}

