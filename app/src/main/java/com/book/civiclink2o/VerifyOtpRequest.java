package com.book.civiclink2o;

public class VerifyOtpRequest {
    private String phoneNumber;
    private String otp;
    private String name;

    public VerifyOtpRequest(String phoneNumber, String otp) {
        this.phoneNumber = phoneNumber;
        this.otp = otp;
        this.name = null;
    }

    public VerifyOtpRequest(String phoneNumber, String otp, String name) {
        this.phoneNumber = phoneNumber;
        this.otp = otp;
        this.name = name;
    }
}