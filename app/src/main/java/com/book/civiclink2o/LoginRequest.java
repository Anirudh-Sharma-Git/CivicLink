package com.book.civiclink2o;

// This class represents the data we SEND to the server when logging in.
public class LoginRequest {
    private String email;
    private String password;

    public LoginRequest(String email, String password) {
        this.email = email;
        this.password = password;
    }
}