package com.springboot.auth.dto;

import com.springboot.auth.entity.User;

public class AuthResponse {
    private User user;
    private String token;
    private String message;
    private String status;

    public AuthResponse(User user, String token, String message) {
        this.user = user;
        this.token = token;
        this.message = message;
        this.status = "success";
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
