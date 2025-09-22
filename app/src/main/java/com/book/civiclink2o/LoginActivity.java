package com.book.civiclink2o;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.android.material.button.MaterialButton;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class LoginActivity extends AppCompatActivity {

    // --- References for ALL form fields ---
    private EditText emailSignUpName, emailSignUpEmail, emailSignUpPassword, emailSignUpConfirmPassword;
    private EditText emailLoginEmail, emailLoginPassword;
    private EditText phoneLoginNumber;
    private EditText phoneSignUpName, phoneSignUpNumber;
    private MaterialButton emailSignUpButton, loginButton, sendOtpButton, phoneSignUpButton;

    // State tracking variables
    private boolean isLogin = true;
    private boolean isPhoneMode = true;

    // UI Elements
    private TextView phoneLoginToggle, emailLoginToggle;
    private LinearLayout phoneLoginLayout, emailLoginLayout, phoneSignUpLayout, emailSignUpLayout;
    private TextView toggleLoginSignUp;
    private Button guestButton;
    private ApiService apiService;

    // --- The Session Manager for remembering the logged-in user ---
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // --- Initialize the Session Manager ---
        sessionManager = new SessionManager(getApplicationContext());

        // --- Check if the user is already logged in ---
        // If they are, we go straight to the Home screen without showing the login page.
        if (sessionManager.isLoggedIn()) {
            navigateToHome();
            return; // Important: return to stop the rest of onCreate from running
        }

        // --- Initialize Retrofit (our "telephone system") ---
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://10.0.2.2:3000") // Special address for emulator to reach laptop's localhost
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        apiService = retrofit.create(ApiService.class);

        initializeViews();
        setupClickListeners();
        updateUI();
    }

    private void initializeViews() {
        // Toggles and containers
        phoneLoginToggle = findViewById(R.id.phoneLoginToggle);
        emailLoginToggle = findViewById(R.id.emailLoginToggle);
        toggleLoginSignUp = findViewById(R.id.toggleLoginSignUp);
        guestButton = findViewById(R.id.guestButton);
        phoneLoginLayout = findViewById(R.id.phoneLoginLayout);
        emailLoginLayout = findViewById(R.id.emailLoginLayout);
        phoneSignUpLayout = findViewById(R.id.phoneSignUpLayout);
        emailSignUpLayout = findViewById(R.id.emailSignUpLayout);

        // Email Sign Up fields
        emailSignUpName = findViewById(R.id.emailSignUpName);
        emailSignUpEmail = findViewById(R.id.emailSignUpEmail);
        emailSignUpPassword = findViewById(R.id.emailSignUpPassword);
        emailSignUpConfirmPassword = findViewById(R.id.emailSignUpConfirmPassword);
        emailSignUpButton = findViewById(R.id.emailSignUpButton);

        // Email Login fields
        emailLoginEmail = findViewById(R.id.emailLoginEmail);
        emailLoginPassword = findViewById(R.id.emailLoginPassword);
        loginButton = findViewById(R.id.loginButton);

        // Phone Login fields
        phoneLoginNumber = findViewById(R.id.phoneLoginNumber);
        sendOtpButton = findViewById(R.id.sendOtpButton);

        // Phone Sign Up fields
        phoneSignUpName = findViewById(R.id.phoneSignUpName);
        phoneSignUpNumber = findViewById(R.id.phoneSignUpNumber);
        phoneSignUpButton = findViewById(R.id.phoneSignUpButton);
    }

    private void setupClickListeners() {
        phoneLoginToggle.setOnClickListener(v -> { isPhoneMode = true; updateUI(); });
        emailLoginToggle.setOnClickListener(v -> { isPhoneMode = false; updateUI(); });
        toggleLoginSignUp.setOnClickListener(v -> { isLogin = !isLogin; updateUI(); });
        guestButton.setOnClickListener(v -> navigateToHome());

        // Set listeners for all 4 action buttons
        emailSignUpButton.setOnClickListener(v -> handleEmailSignUp());
        loginButton.setOnClickListener(v -> handleEmailLogin());
        sendOtpButton.setOnClickListener(v -> handleSendOtp(false)); // isSignUp = false
        phoneSignUpButton.setOnClickListener(v -> handleSendOtp(true));  // isSignUp = true
    }

    private void handleSendOtp(boolean isSignUp) {
        String phoneNumber;
        String name = null; // Name is null by default (for login)

        if (isSignUp) {
            phoneNumber = phoneSignUpNumber.getText().toString().trim();
            name = phoneSignUpName.getText().toString().trim();
            if (name.isEmpty()) {
                Toast.makeText(this, "Please enter your name", Toast.LENGTH_SHORT).show();
                return;
            }
        } else { // It's a login attempt
            phoneNumber = phoneLoginNumber.getText().toString().trim();
        }

        if (phoneNumber.isEmpty() || phoneNumber.length() < 10) {
            Toast.makeText(this, "Please enter a valid 10-digit phone number", Toast.LENGTH_SHORT).show();
            return;
        }

        final String finalName = name; // Need a final variable for the inner class
        apiService.sendOtp(new SendOtpRequest(phoneNumber)).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(LoginActivity.this, "OTP Sent (Simulated as 1234)", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(LoginActivity.this, OtpActivity.class);
                    intent.putExtra("PHONE_NUMBER", phoneNumber);
                    if (isSignUp) {
                        intent.putExtra("USER_NAME", finalName); // Pass the name only if it's a sign-up
                    }
                    startActivity(intent);
                } else {
                    Toast.makeText(LoginActivity.this, "Failed to send OTP", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(LoginActivity.this, "Network Error: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void handleEmailLogin() {
        String email = emailLoginEmail.getText().toString().trim();
        String password = emailLoginPassword.getText().toString().trim();
        if (email.isEmpty() || password.isEmpty()) { Toast.makeText(this, "Please enter email and password", Toast.LENGTH_SHORT).show(); return; }

        LoginRequest loginRequest = new LoginRequest(email, password);
        apiService.loginUser(loginRequest).enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Toast.makeText(LoginActivity.this, "Login Successful!", Toast.LENGTH_SHORT).show();

                    // Get the user data from the server's response
                    LoginResponse.UserData user = response.body().getUser();
                    // Save the user's session using our SessionManager
                    sessionManager.createLoginSession(user.getId(), user.getName());

                    navigateToHome();
                } else {
                    Toast.makeText(LoginActivity.this, "Login failed. Please check credentials.", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                Toast.makeText(LoginActivity.this, "Network Error: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void handleEmailSignUp() {
        String name = emailSignUpName.getText().toString().trim();
        String email = emailSignUpEmail.getText().toString().trim();
        String password = emailSignUpPassword.getText().toString().trim();
        String confirmPassword = emailSignUpConfirmPassword.getText().toString().trim();
        if (name.isEmpty() || email.isEmpty() || password.isEmpty()) { Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show(); return; }
        if (!password.equals(confirmPassword)) { Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show(); return; }

        User user = new User(name, email, password);
        apiService.registerUser(user).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(LoginActivity.this, "Sign up successful! Please log in.", Toast.LENGTH_LONG).show();
                    isLogin = true;
                    updateUI();
                } else {
                    Toast.makeText(LoginActivity.this, "Sign up failed (Email might already be in use).", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(LoginActivity.this, "Network Error: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void navigateToHome() {
        Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
        startActivity(intent);
        finish();
    }

    private void updateUI() {
        guestButton.setVisibility(isLogin ? View.VISIBLE : View.GONE);
        updateToggleStyles();
        phoneLoginLayout.setVisibility(View.GONE);
        emailLoginLayout.setVisibility(View.GONE);
        phoneSignUpLayout.setVisibility(View.GONE);
        emailSignUpLayout.setVisibility(View.GONE);
        toggleLoginSignUp.setText(isLogin ? R.string.new_user_signup : R.string.already_have_account);
        if (isLogin) {
            if (isPhoneMode) {
                phoneLoginLayout.setVisibility(View.VISIBLE);
            } else {
                emailLoginLayout.setVisibility(View.VISIBLE);
            }
        } else { // isSignUP
            if (isPhoneMode) {
                phoneSignUpLayout.setVisibility(View.VISIBLE);
            } else {
                emailSignUpLayout.setVisibility(View.VISIBLE);
            }
        }
    }

    private void updateToggleStyles() {
        if (isPhoneMode) {
            phoneLoginToggle.setBackgroundResource(R.drawable.toggle_selected_background);
            phoneLoginToggle.setTextColor(ContextCompat.getColor(this, R.color.white));
            emailLoginToggle.setBackgroundResource(R.drawable.toggle_unselected_background);
            emailLoginToggle.setTextColor(ContextCompat.getColor(this, R.color.text_secondary));
        } else {
            emailLoginToggle.setBackgroundResource(R.drawable.toggle_selected_background);
            emailLoginToggle.setTextColor(ContextCompat.getColor(this, R.color.white));
            phoneLoginToggle.setBackgroundResource(R.drawable.toggle_unselected_background);
            phoneLoginToggle.setTextColor(ContextCompat.getColor(this, R.color.text_secondary));
        }
    }
}