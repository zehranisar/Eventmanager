package com.example.eventmanager.api.response;

import com.google.gson.annotations.SerializedName;

public class ForgotPasswordResponse extends BaseResponse {
    
    @SerializedName("otp")
    private String otp; // Only for testing - remove in production
    
    public String getOtp() { return otp; }
    public void setOtp(String otp) { this.otp = otp; }
}

