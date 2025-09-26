package com.book.civiclink2o;

import java.util.List;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface ApiService {

    // (Your existing endpoints for email, issues, and profile are correct)
    @POST("api/auth/register") Call<Void> registerUser(@Body User user);
    @POST("api/auth/login") Call<LoginResponse> loginUser(@Body LoginRequest loginRequest);
    @POST("api/issues") Call<Void> createIssue(@Body IssueRequest issueRequest);
    @GET("api/issues") Call<List<Issue>> getAllIssues();
    @GET("api/issues/user/{userId}") Call<List<Issue>> getIssuesForUser(@Path("userId") int userId);
    @GET("api/auth/user/{userId}") Call<UserDetails> getUserDetails(@Path("userId") int userId);
    @PUT("api/auth/user/{userId}") Call<Void> updateUserDetails(@Path("userId") int userId, @Body UpdateUserRequest request);

    // --- PHONE AUTH (UPDATED) ---
    @POST("api/auth/send-otp")
    Call<Void> sendOtp(@Body SendOtpRequest sendOtpRequest);

    // --- THE FIX: Two new, specific endpoints for OTP verification ---
    @POST("api/auth/verify-otp/login")
    Call<LoginResponse> verifyOtpForLogin(@Body VerifyOtpRequest verifyOtpRequest);

    @POST("api/auth/verify-otp/signup")
    Call<LoginResponse> verifyOtpForSignUp(@Body VerifyOtpRequest verifyOtpRequest);
}
