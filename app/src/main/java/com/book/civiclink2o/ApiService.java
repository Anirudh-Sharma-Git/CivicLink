package com.book.civiclink2o;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface ApiService {

    // User Authentication Endpoints
    @POST("api/auth/register")
    Call<Void> registerUser(@Body User user);

    @POST("api/auth/login")
    Call<LoginResponse> loginUser(@Body LoginRequest loginRequest);

    @POST("api/auth/send-otp")
    Call<Void> sendOtp(@Body SendOtpRequest sendOtpRequest);

    @POST("api/auth/verify-otp")
    Call<LoginResponse> verifyOtp(@Body VerifyOtpRequest verifyOtpRequest);

    // --- THIS IS THE NEW ENDPOINT ---
    // Issue Reporting Endpoint
    @POST("api/issues")
    Call<Void> createIssue(@Body IssueRequest issueRequest);
}