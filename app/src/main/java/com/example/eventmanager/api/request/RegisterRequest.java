package com.example.eventmanager.api.request;

import com.google.gson.annotations.SerializedName;

public class RegisterRequest {
    
    @SerializedName("email")
    private String email;
    
    @SerializedName("name")
    private String name;
    
    @SerializedName("password")
    private String password;
    
    @SerializedName("confirm_password")
    private String confirmPassword;
    
    @SerializedName("role")
    private String role;
    
    public RegisterRequest(String email, String name, String password, String confirmPassword) {
        this.email = email;
        this.name = name;
        this.password = password;
        this.confirmPassword = confirmPassword;
        this.role = "student"; // Default role
    }
    
    // Getters and Setters
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    
    public String getConfirmPassword() { return confirmPassword; }
    public void setConfirmPassword(String confirmPassword) { this.confirmPassword = confirmPassword; }
    
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
}

