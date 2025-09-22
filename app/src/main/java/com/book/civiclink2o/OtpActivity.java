package com.book.civiclink2o;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class OtpActivity extends AppCompatActivity {

    private TextView otpSubtitle;
    private EditText otpEditText;
    private MaterialButton verifyOtpButton;
    private String phoneNumber;
    private String userName;
    private ApiService apiService;

    // --- THIS IS NEW ---
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp);

        // --- THIS IS NEW ---
        sessionManager = new SessionManager(getApplicationContext());

        otpSubtitle = findViewById(R.id.otpSubtitle);
        otpEditText = findViewById(R.id.otpEditText);
        verifyOtpButton = findViewById(R.id.verifyOtpButton);

        phoneNumber = getIntent().getStringExtra("PHONE_NUMBER");
        userName = getIntent().getStringExtra("USER_NAME");

        if (phoneNumber != null) {
            otpSubtitle.setText("An OTP has been sent to +91 " + phoneNumber);
        }

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://10.0.2.2:3000")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        apiService = retrofit.create(ApiService.class);

        verifyOtpButton.setOnClickListener(v -> {
            String otp = otpEditText.getText().toString().trim();
            if (otp.length() != 4) {
                Toast.makeText(this, "Please enter the 4-digit OTP", Toast.LENGTH_SHORT).show();
                return;
            }
            verifyOtp(phoneNumber, otp, userName);
        });
    }

    private void verifyOtp(String phone, String otp, String name) {
        if (phone == null) {
            Toast.makeText(this, "An error occurred. Please try again.", Toast.LENGTH_SHORT).show();
            return;
        }

        VerifyOtpRequest request = (name != null)
                ? new VerifyOtpRequest(phone, otp, name)
                : new VerifyOtpRequest(phone, otp);

        apiService.verifyOtp(request).enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Toast.makeText(OtpActivity.this, "Login Successful!", Toast.LENGTH_SHORT).show();

                    // --- THIS IS THE FIX ---
                    // Get the user data from the server's response
                    LoginResponse.UserData user = response.body().getUser();
                    // Save the user's session using our SessionManager
                    sessionManager.createLoginSession(user.getId(), user.getName());

                    // Navigate to the home screen
                    Intent intent = new Intent(OtpActivity.this, HomeActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(OtpActivity.this, "Invalid OTP", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                Toast.makeText(OtpActivity.this, "Network Error: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}