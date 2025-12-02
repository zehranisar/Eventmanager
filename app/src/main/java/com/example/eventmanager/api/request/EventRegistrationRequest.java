package com.example.eventmanager.api.request;

import com.google.gson.annotations.SerializedName;

public class EventRegistrationRequest {
    
    @SerializedName("name")
    private String name;
    
    @SerializedName("email")
    private String email;
    
    @SerializedName("phone")
    private String phone;
    
    @SerializedName("student_id")
    private String studentId;
    
    public EventRegistrationRequest(String name, String email, String phone, String studentId) {
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.studentId = studentId;
    }
    
    // Getters and Setters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    
    public String getStudentId() { return studentId; }
    public void setStudentId(String studentId) { this.studentId = studentId; }
}

