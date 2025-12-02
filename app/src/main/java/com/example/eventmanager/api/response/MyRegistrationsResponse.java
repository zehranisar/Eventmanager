package com.example.eventmanager.api.response;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class MyRegistrationsResponse extends BaseResponse {
    
    @SerializedName("count")
    private int count;
    
    @SerializedName("registrations")
    private List<RegistrationData> registrations;
    
    public int getCount() { return count; }
    public void setCount(int count) { this.count = count; }
    
    public List<RegistrationData> getRegistrations() { return registrations; }
    public void setRegistrations(List<RegistrationData> registrations) { this.registrations = registrations; }
}

