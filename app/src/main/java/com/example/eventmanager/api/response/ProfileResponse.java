package com.example.eventmanager.api.response;

import com.google.gson.annotations.SerializedName;

public class ProfileResponse extends BaseResponse {
    
    @SerializedName("user")
    private UserData user;
    
    public UserData getUser() { return user; }
    public void setUser(UserData user) { this.user = user; }
}

