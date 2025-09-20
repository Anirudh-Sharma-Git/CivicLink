package com.book.civiclink2o;

import android.content.Intent; // Import the Intent class
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

public class LoginActivity extends AppCompatActivity {

    // State tracking variables
    private boolean isLogin = true;
    private boolean isPhoneMode = true;

    // UI Elements
    private TextView phoneLoginToggle, emailLoginToggle;
    private LinearLayout phoneLoginLayout, emailLoginLayout, phoneSignUpLayout, emailSignUpLayout;
    private TextView toggleLoginSignUp;
    private Button guestButton; // Reference to the guest button

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Initialize all views from the layout
        initializeViews();

        // Set up click listeners for the toggles
        setupClickListeners();

        // Set the initial UI state (Phone Login)
        updateUI();
    }

    private void initializeViews() {
        phoneLoginToggle = findViewById(R.id.phoneLoginToggle);
        emailLoginToggle = findViewById(R.id.emailLoginToggle);
        toggleLoginSignUp = findViewById(R.id.toggleLoginSignUp);
        guestButton = findViewById(R.id.guestButton); // Find the guest button

        // Find all the form layouts
        phoneLoginLayout = findViewById(R.id.phoneLoginLayout);
        emailLoginLayout = findViewById(R.id.emailLoginLayout);
        phoneSignUpLayout = findViewById(R.id.phoneSignUpLayout);
        emailSignUpLayout = findViewById(R.id.emailSignUpLayout);
    }

    private void setupClickListeners() {
        phoneLoginToggle.setOnClickListener(v -> {
            isPhoneMode = true;
            updateUI();
        });

        emailLoginToggle.setOnClickListener(v -> {
            isPhoneMode = false;
            updateUI();
        });

        toggleLoginSignUp.setOnClickListener(v -> {
            isLogin = !isLogin; // Flip between login and sign-up
            updateUI();
        });

        // --- THIS IS THE FIX ---
        // Add the missing click listener for the guest button
        guestButton.setOnClickListener(v -> {
            navigateToHome();
        });
        // --- END OF FIX ---
    }

    // --- THIS IS A NEW HELPER METHOD ---
    // A clean way to handle navigation to the home screen
    private void navigateToHome() {
        Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
        startActivity(intent);
        finish(); // Finish LoginActivity so the user can't go back to it
    }
    // --- END OF NEW HELPER METHOD ---

    private void updateUI() {
        // Show the guest button ONLY when on a login screen. Hide it for sign-up.
        if (isLogin) {
            guestButton.setVisibility(View.VISIBLE);
        } else {
            guestButton.setVisibility(View.GONE);
        }

        // Update toggle styles
        updateToggleStyles();

        // Hide all forms initially
        phoneLoginLayout.setVisibility(View.GONE);
        emailLoginLayout.setVisibility(View.GONE);
        phoneSignUpLayout.setVisibility(View.GONE);
        emailSignUpLayout.setVisibility(View.GONE);

        // Update the link text (e.g., "New user? Sign up")
        toggleLoginSignUp.setText(isLogin ? R.string.new_user_signup : R.string.already_have_account);

        // Show the correct form based on the current state
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