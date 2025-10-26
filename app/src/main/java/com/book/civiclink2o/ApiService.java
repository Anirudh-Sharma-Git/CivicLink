package com.book.civiclink2o;

import java.util.List;

// --- NEW IMPORTS ---
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Multipart; // New
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part; // New
import retrofit2.http.Path;

public interface ApiService {

    // (All your user auth endpoints remain the same)
    @POST("api/auth/register") Call<Void> registerUser(@Body User user);
    @POST("api/auth/login") Call<LoginResponse> loginUser(@Body LoginRequest loginRequest);
    @GET("api/auth/user/{userId}") Call<UserDetails> getUserDetails(@Path("userId") int userId);
    @PUT("api/auth/user/{userId}") Call<Void> updateUserDetails(@Path("userId") int userId, @Body UpdateUserRequest request);
    @POST("api/auth/send-otp") Call<Void> sendOtp(@Body SendOtpRequest sendOtpRequest);
    @POST("api/auth/verify-otp/login") Call<LoginResponse> verifyOtpForLogin(@Body VerifyOtpRequest verifyOtpRequest);
    @POST("api/auth/verify-otp/signup") Call<LoginResponse> verifyOtpForSignUp(@Body VerifyOtpRequest verifyOtpRequest);

    // (Your GET issue endpoints remain the same)
    @GET("api/issues") Call<List<Issue>> getAllIssues();
    @GET("api/issues/user/{userId}") Call<List<Issue>> getIssuesForUser(@Path("userId") int userId);


    // --- THIS IS THE FIX: The createIssue endpoint is now completely rewritten ---
    @Multipart // This tells Retrofit we are sending a multipart "shipping box"
    @POST("api/issues")
    Call<Void> createIssue(
            @Part MultipartBody.Part image, // This is the file itself
            @Part("category") RequestBody category, // This is a piece of text data
            @Part("description") RequestBody description,
            @Part("latitude") RequestBody latitude,
            @Part("longitude") RequestBody longitude,
            @Part("reportedBy") RequestBody reportedBy
    );
}

