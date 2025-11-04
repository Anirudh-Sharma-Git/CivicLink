package com.book.civiclink2o;

import android.content.Intent;
import android.os.Bundle;
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
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp);

        sessionManager = new SessionManager(getApplicationContext());
        otpSubtitle = findViewById(R.id.otpSubtitle);
        otpEditText = findViewById(R.id.otpEditText);
        verifyOtpButton = findViewById(R.id.verifyOtpButton);

        phoneNumber = getIntent().getStringExtra("PHONE_NUMBER");
        userName = getIntent().getStringExtra("USER_NAME");

        if (phoneNumber != null) {
            otpSubtitle.setText("An OTP has been sent to +91 " + phoneNumber);
        }

        apiService = ApiClient.getClient().create(ApiService.class);

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
        if (phone == null) { return; }

        Call<LoginResponse> apiCall;

        if (name != null) {
            VerifyOtpRequest request = new VerifyOtpRequest(phone, otp, name);
            apiCall = apiService.verifyOtpForSignUp(request);
        } else {
            VerifyOtpRequest request = new VerifyOtpRequest(phone, otp);
            apiCall = apiService.verifyOtpForLogin(request);
        }

        apiCall.enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Toast.makeText(OtpActivity.this, "Verification Successful!", Toast.LENGTH_SHORT).show();

                    LoginResponse.UserData user = response.body().getUser();
                    sessionManager.createLoginSession(user.getId(), user.getName());

                    Intent intent = new Intent(OtpActivity.this, HomeActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                } else {
                    String errorMessage = "Invalid OTP or error occurred.";
                    try {
                        if (response.errorBody() != null) {

                            errorMessage = response.errorBody().string().split("\"")[3];
                        }
                    } catch (Exception e) { e.printStackTrace(); }

                    Toast.makeText(OtpActivity.this, errorMessage, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                Toast.makeText(OtpActivity.this, "Network Error: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}
