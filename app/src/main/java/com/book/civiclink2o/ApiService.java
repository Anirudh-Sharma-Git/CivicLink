package com.book.civiclink2o;

import java.util.List; // Import the List class
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET; // Import the GET annotation
import retrofit2.http.POST;
import retrofit2.http.Path; // Import the Path annotation

public interface ApiService {

    // --- User Authentication Endpoints (Same as before) ---
    @POST("api/auth/register")
    Call<Void> registerUser(@Body User user);

    @POST("api/auth/login")
    Call<LoginResponse> loginUser(@Body LoginRequest loginRequest);

    @POST("api/auth/send-otp")
    Call<Void> sendOtp(@Body SendOtpRequest sendOtpRequest);

    @POST("api/auth/verify-otp")
    Call<LoginResponse> verifyOtp(@Body VerifyOtpRequest verifyOtpRequest);

    // --- Issue Reporting Endpoint (Same as before) ---
    @POST("api/issues")
    Call<Void> createIssue(@Body IssueRequest issueRequest);

    // --- THIS IS NEW: Endpoints for FETCHING issues ---

    // This gets ALL issues for the Home screen
    @GET("api/issues")
    Call<List<Issue>> getAllIssues();

    // This gets issues for a SPECIFIC user for the "My Reports" screen
    @GET("api/issues/user/{userId}")
    Call<List<Issue>> getIssuesForUser(@Path("userId") int userId);
}