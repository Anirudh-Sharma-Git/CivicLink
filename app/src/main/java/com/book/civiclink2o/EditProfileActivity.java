package com.book.civiclink2o;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText; // THE FIX: Import the correct component
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class EditProfileActivity extends AppCompatActivity {

    // --- UI Elements ---
    private TextView userIdTextView;
    // THE FIX: Use TextInputEditText to match the modern layout
    private TextInputEditText nameEditText, emailEditText, phoneEditText;
    private LinearLayout currentPasswordLayout, newPasswordLayout;
    private TextInputEditText currentPasswordEditText, newPasswordEditText, confirmNewPasswordEditText;
    private MaterialButton updateProfileButton;

    // --- Backend and Session ---
    private ApiService apiService;
    private SessionManager sessionManager;
    private int currentUserId = -1;
    private boolean userHasPassword = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        sessionManager = new SessionManager(this);
        currentUserId = sessionManager.getUserId();

        initializeViews();
        setupApiService();
        loadUserDetails();

        updateProfileButton.setOnClickListener(v -> handleUpdateProfile());
    }

    private void initializeViews() {
        userIdTextView = findViewById(R.id.userIdTextView);
        nameEditText = findViewById(R.id.nameEditText);
        emailEditText = findViewById(R.id.emailEditText);
        phoneEditText = findViewById(R.id.phoneEditText);
        currentPasswordLayout = findViewById(R.id.currentPasswordLayout);
        newPasswordLayout = findViewById(R.id.newPasswordLayout);
        currentPasswordEditText = findViewById(R.id.currentPasswordEditText);
        newPasswordEditText = findViewById(R.id.newPasswordEditText);
        confirmNewPasswordEditText = findViewById(R.id.confirmNewPasswordEditText);
        updateProfileButton = findViewById(R.id.updateProfileButton);
    }

    private void setupApiService() {
        // THE FIX: Get the telephone system from our new, central ApiClient
        apiService = ApiClient.getClient().create(ApiService.class);
    }

    private void loadUserDetails() {
        if (currentUserId == -1) {
            Toast.makeText(this, "Error: Not logged in.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        apiService.getUserDetails(currentUserId).enqueue(new Callback<UserDetails>() {
            @Override
            public void onResponse(Call<UserDetails> call, Response<UserDetails> response) {
                if (response.isSuccessful() && response.body() != null) {
                    UserDetails user = response.body();
                    userIdTextView.setText(String.valueOf(user.id));
                    nameEditText.setText(user.name);
                    emailEditText.setText(user.email);
                    phoneEditText.setText(user.phoneNumber);

                    userHasPassword = user.hasPassword;
                    if (userHasPassword) {
                        currentPasswordLayout.setVisibility(View.VISIBLE);
                        newPasswordLayout.setVisibility(View.GONE);
                    } else {
                        currentPasswordLayout.setVisibility(View.GONE);
                        newPasswordLayout.setVisibility(View.VISIBLE);
                    }
                } else {
                    Toast.makeText(EditProfileActivity.this, "Failed to load profile data.", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<UserDetails> call, Throwable t) {
                Toast.makeText(EditProfileActivity.this, "Network Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void handleUpdateProfile() {
        String name = nameEditText.getText().toString().trim();
        String email = emailEditText.getText().toString().trim();
        String phone = phoneEditText.getText().toString().trim();
        String currentPassword = null;
        String newPassword = null;

        if (name.isEmpty() || email.isEmpty() || phone.isEmpty()) {
            Toast.makeText(this, "Name, email, and phone are required.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (userHasPassword) {
            currentPassword = currentPasswordEditText.getText().toString();
            if (currentPassword.isEmpty()) {
                Toast.makeText(this, "Please enter your current password to make changes.", Toast.LENGTH_SHORT).show();
                return;
            }
        } else {
            newPassword = newPasswordEditText.getText().toString();
            String confirmNewPassword = confirmNewPasswordEditText.getText().toString();
            if (newPassword.isEmpty() || !newPassword.equals(confirmNewPassword)) {
                Toast.makeText(this, "New passwords do not match or are empty.", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        UpdateUserRequest request = new UpdateUserRequest(name, email, phone, currentPassword, newPassword);
        apiService.updateUserDetails(currentUserId, request).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(EditProfileActivity.this, "Profile updated successfully!", Toast.LENGTH_SHORT).show();
                    sessionManager.createLoginSession(currentUserId, name);
                    finish();
                } else {
                    Toast.makeText(EditProfileActivity.this, "Update failed. Check password or email/phone may be in use.", Toast.LENGTH_LONG).show();
                }
            }
            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(EditProfileActivity.this, "Network Error: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}