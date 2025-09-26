package com.book.civiclink2o;

public class UpdateUserRequest {
    private String name;
    private String email;
    private String phoneNumber;
    private String currentPassword; // Can be null
    private String newPassword;     // Can be null

    public UpdateUserRequest(String name, String email, String phoneNumber, String currentPassword, String newPassword) {
        this.name = name;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.currentPassword = currentPassword;
        this.newPassword = newPassword;
    }
}