package com.example.eventmanager.api.response;

import com.google.gson.annotations.SerializedName;

public class AuthResponse extends BaseResponse {
    
    @SerializedName("user")
    private UserData user;
    
    @SerializedName("tokens")
    private TokenData tokens;
    
    public UserData getUser() { return user; }
    public void setUser(UserData user) { this.user = user; }
    
    public TokenData getTokens() { return tokens; }
    public void setTokens(TokenData tokens) { this.tokens = tokens; }
}

