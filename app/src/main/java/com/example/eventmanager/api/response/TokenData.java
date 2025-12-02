package com.example.eventmanager.api.response;

import com.google.gson.annotations.SerializedName;

public class TokenData {
    
    @SerializedName("access")
    private String access;
    
    @SerializedName("refresh")
    private String refresh;
    
    public String getAccess() { return access; }
    public void setAccess(String access) { this.access = access; }
    
    public String getRefresh() { return refresh; }
    public void setRefresh(String refresh) { this.refresh = refresh; }
}

