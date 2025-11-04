package com.book.civiclink2o;

import java.util.List;


import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;

public interface ApiService {

    @POST("api/auth/register") Call<Void> registerUser(@Body User user);
    @POST("api/auth/login") Call<LoginResponse> loginUser(@Body LoginRequest loginRequest);
    @GET("api/auth/user/{userId}") Call<UserDetails> getUserDetails(@Path("userId") int userId);
    @PUT("api/auth/user/{userId}") Call<Void> updateUserDetails(@Path("userId") int userId, @Body UpdateUserRequest request);
    @POST("api/auth/send-otp") Call<Void> sendOtp(@Body SendOtpRequest sendOtpRequest);
    @POST("api/auth/verify-otp/login") Call<LoginResponse> verifyOtpForLogin(@Body VerifyOtpRequest verifyOtpRequest);
    @POST("api/auth/verify-otp/signup") Call<LoginResponse> verifyOtpForSignUp(@Body VerifyOtpRequest verifyOtpRequest);

    @GET("api/issues") Call<List<Issue>> getAllIssues();
    @GET("api/issues/user/{userId}") Call<List<Issue>> getIssuesForUser(@Path("userId") int userId);


    @Multipart
    @POST("api/issues")
    Call<Void> createIssue(
            @Part MultipartBody.Part image, //file itself
            @Part("category") RequestBody category,
            @Part("description") RequestBody description,
            @Part("latitude") RequestBody latitude,
            @Part("longitude") RequestBody longitude,
            @Part("reportedBy") RequestBody reportedBy
    );
}

