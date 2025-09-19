package com.book.civiclink2o;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

public class LoginActivity extends AppCompatActivity {

    // UI elements for the segmented control (Phone/Email toggle)
    private TextView phoneLoginToggle, emailLoginToggle;

    // UI elements for Phone Login
    private LinearLayout phoneLoginLayout;
    private EditText phoneLoginNumber;
    private Button sendOtpButton;

    // UI elements for Email Login
    private LinearLayout emailLoginLayout;
    private EditText emailLoginEmail, emailLoginPassword;
    private Button loginButton;

    // UI elements for Phone Sign Up
    private LinearLayout phoneSignUpLayout;
    private EditText phoneSignUpName, phoneSignUpNumber;
    private Button phoneSignUpButton;

    // UI elements for Email Sign Up
    private LinearLayout emailSignUpLayout;
    private EditText emailSignUpName, emailSignUpEmail, emailSignUpPassword, emailSignUpConfirmPassword;
    private Button emailSignUpButton;

    // Link to switch between Login and Sign Up
    private TextView toggleLoginSignUp;

    // State variables
    private boolean isLoginPage = true; // Tracks if we are on the Login or Sign Up page
    private boolean isPhoneMode = true; // Tracks if we are in Phone or Email mode

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Initialize all the views from the XML layout
        initializeViews();

        // Set up the click listeners for the interactive elements
        setupClickListeners();

        // Set the initial state of the screen (Phone Login)
        updateUI();
    }

    /**
     * Finds and assigns all the View instances from the layout file.
     */
    private void initializeViews() {
        // Toggles
        phoneLoginToggle = findViewById(R.id.phoneLoginToggle);
        emailLoginToggle = findViewById(R.id.emailLoginToggle);
        toggleLoginSignUp = findViewById(R.id.toggleLoginSignUp);

        // Phone Login
        phoneLoginLayout = findViewById(R.id.phoneLoginLayout);
        phoneLoginNumber = findViewById(R.id.phoneLoginNumber);
        sendOtpButton = findViewById(R.id.sendOtpButton);

        // Email Login
        emailLoginLayout = findViewById(R.id.emailLoginLayout);
        emailLoginEmail = findViewById(R.id.emailLoginEmail);
        emailLoginPassword = findViewById(R.id.emailLoginPassword);
        loginButton = findViewById(R.id.loginButton);

        // Phone Sign Up
        phoneSignUpLayout = findViewById(R.id.phoneSignUpLayout);
        phoneSignUpName = findViewById(R.id.phoneSignUpName);
        phoneSignUpNumber = findViewById(R.id.phoneSignUpNumber);
        phoneSignUpButton = findViewById(R.id.phoneSignUpButton);

        // Email Sign Up
        emailSignUpLayout = findViewById(R.id.emailSignUpLayout);
        emailSignUpName = findViewById(R.id.emailSignUpName);
        emailSignUpEmail = findViewById(R.id.emailSignUpEmail);
        emailSignUpPassword = findViewById(R.id.emailSignUpPassword);
        emailSignUpConfirmPassword = findViewById(R.id.emailSignUpConfirmPassword);
        emailSignUpButton = findViewById(R.id.emailSignUpButton);
    }

    /**
     * Sets OnClickListeners for all the interactive views.
     */
    private void setupClickListeners() {
        // Listener for the Phone/Email toggle
        phoneLoginToggle.setOnClickListener(v -> {
            isPhoneMode = true;
            updateUI();
        });

        emailLoginToggle.setOnClickListener(v -> {
            isPhoneMode = false;
            updateUI();
        });

        // Listener to switch between Login and Sign Up pages
        toggleLoginSignUp.setOnClickListener(v -> {
            isLoginPage = !isLoginPage;
            updateUI();
        });

        // Dummy listeners for the buttons to show a message
        sendOtpButton.setOnClickListener(v -> showToast("Send OTP clicked"));
        loginButton.setOnClickListener(v -> showToast("Login clicked"));
        phoneSignUpButton.setOnClickListener(v -> showToast("Sign Up with Phone clicked"));
        emailSignUpButton.setOnClickListener(v -> showToast("Sign Up with Email clicked"));
    }

    /**
     * This is the core logic. It shows/hides views based on the current state.
     */
    private void updateUI() {
        // --- 1. Update the Phone/Email Toggle UI ---
        if (isPhoneMode) {
            phoneLoginToggle.setBackgroundResource(R.drawable.toggle_selected_background);
            phoneLoginToggle.setTextColor(Color.WHITE);
            emailLoginToggle.setBackgroundResource(R.drawable.toggle_unselected_background);
            emailLoginToggle.setTextColor(ContextCompat.getColor(this, R.color.text_secondary));
        } else {
            emailLoginToggle.setBackgroundResource(R.drawable.toggle_selected_background);
            emailLoginToggle.setTextColor(Color.WHITE);
            phoneLoginToggle.setBackgroundResource(R.drawable.toggle_unselected_background);
            phoneLoginToggle.setTextColor(ContextCompat.getColor(this, R.color.text_secondary));
        }

        // --- 2. Hide all four form layouts initially ---
        phoneLoginLayout.setVisibility(View.GONE);
        emailLoginLayout.setVisibility(View.GONE);
        phoneSignUpLayout.setVisibility(View.GONE);
        emailSignUpLayout.setVisibility(View.GONE);

        // --- 3. Show the correct form and update the link text ---
        if (isLoginPage) {
            if (isPhoneMode) {
                phoneLoginLayout.setVisibility(View.VISIBLE);
            } else {
                emailLoginLayout.setVisibility(View.VISIBLE);
            }
            toggleLoginSignUp.setText(getString(R.string.new_user_signup)); // "New user? Sign up"
        } else { // isSignUpPage
            if (isPhoneMode) {
                phoneSignUpLayout.setVisibility(View.VISIBLE);
            } else {
                emailSignUpLayout.setVisibility(View.VISIBLE);
            }
            toggleLoginSignUp.setText(getString(R.string.already_have_account_login)); // "Already have an account? Login"
        }
    }

    /**
     * A helper method to easily show Toast messages.
     * @param message The message to display.
     */
    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}

