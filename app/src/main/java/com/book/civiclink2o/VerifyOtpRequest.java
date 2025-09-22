package com.book.civiclink2o;

// This class now includes an optional name for sign-ups
public class VerifyOtpRequest {
    private String phoneNumber;
    private String otp;
    private String name; // This is new!

    // Constructor for login (no name needed)
    public VerifyOtpRequest(String phoneNumber, String otp) {
        this.phoneNumber = phoneNumber;
        this.otp = otp;
        this.name = null; // Default to null
    }

    // Constructor for sign-up (includes the name)
    public VerifyOtpRequest(String phoneNumber, String otp, String name) {
        this.phoneNumber = phoneNumber;
        this.otp = otp;
        this.name = name;
    }
}