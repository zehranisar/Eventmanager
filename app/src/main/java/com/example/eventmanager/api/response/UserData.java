package com.example.eventmanager.api.response;

import com.google.gson.annotations.SerializedName;

public class UserData {
    
    @SerializedName("id")
    private int id;
    
    @SerializedName("email")
    private String email;
    
    @SerializedName("name")
    private String name;
    
    @SerializedName("role")
    private String role;
    
    @SerializedName("created_at")
    private String createdAt;
    
    // Getters
    public int getId() { return id; }
    public String getEmail() { return email; }
    public String getName() { return name; }
    public String getRole() { return role; }
    public String getCreatedAt() { return createdAt; }
    
    // Setters
    public void setId(int id) { this.id = id; }
    public void setEmail(String email) { this.email = email; }
    public void setName(String name) { this.name = name; }
    public void setRole(String role) { this.role = role; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
    
    public boolean isAdmin() {
        return "admin".equals(role);
    }
}

