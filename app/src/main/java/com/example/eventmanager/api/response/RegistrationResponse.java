package com.example.eventmanager.api.response;

import com.google.gson.annotations.SerializedName;

public class RegistrationResponse extends BaseResponse {
    
    @SerializedName("registration")
    private RegistrationData registration;
    
    public RegistrationData getRegistration() { return registration; }
    public void setRegistration(RegistrationData registration) { this.registration = registration; }
}

