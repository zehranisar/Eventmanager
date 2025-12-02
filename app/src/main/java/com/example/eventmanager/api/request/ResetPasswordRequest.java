package com.example.eventmanager.api.request;

import com.google.gson.annotations.SerializedName;

public class ResetPasswordRequest {
    
    @SerializedName("email")
    private String email;
    
    @SerializedName("otp")
    private String otp;
    
    @SerializedName("new_password")
    private String newPassword;
    
    @SerializedName("confirm_password")
    private String confirmPassword;
    
    public ResetPasswordRequest(String email, String otp, String newPassword, String confirmPassword) {
        this.email = email;
        this.otp = otp;
        this.newPassword = newPassword;
        this.confirmPassword = confirmPassword;
    }
    
    // Getters and Setters
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    
    public String getOtp() { return otp; }
    public void setOtp(String otp) { this.otp = otp; }
    
    public String getNewPassword() { return newPassword; }
    public void setNewPassword(String newPassword) { this.newPassword = newPassword; }
    
    public String getConfirmPassword() { return confirmPassword; }
    public void setConfirmPassword(String confirmPassword) { this.confirmPassword = confirmPassword; }
}

