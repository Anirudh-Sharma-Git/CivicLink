package com.book.civiclink2o;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Build; // NEW IMPORT
import android.os.Bundle;
// Import removed: Handler and Looper are no longer needed
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.material.button.MaterialButton;

import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RaiseIssueActivity extends AppCompatActivity {

    // (Your existing UI, API, and Location variables are the same)
    private ImageView issueImageView, gpsButton;
    private MaterialButton takePhotoButton, galleryButton, submitButton;
    private AutoCompleteTextView categoryAutoCompleteTextView;
    private EditText descriptionEditText, locationEditText;
    private ApiService apiService;
    private SessionManager sessionManager;
    private FusedLocationProviderClient fusedLocationClient;
    private Location lastKnownLocation;

    // --- THIS IS NEW: Notification specific variables ---
    private NotificationHelper notificationHelper;
    private ActivityResultLauncher<String> requestNotificationPermissionLauncher;
    // --- END NEW ---

    // Activity Result Launchers
    private ActivityResultLauncher<Void> takePictureLauncher;
    private ActivityResultLauncher<String> getContentLauncher;
    private ActivityResultLauncher<String> requestCameraPermissionLauncher;
    private ActivityResultLauncher<String[]> locationPermissionLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_raise_issue);

        sessionManager = new SessionManager(getApplicationContext());
        apiService = ApiClient.getClient().create(ApiService.class);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        // --- THIS IS NEW: Initialize Notification Helper ---
        // (The channel is created in the helper's constructor)
        notificationHelper = new NotificationHelper(this);
        // --- END NEW ---

        initializeViews();
        setupLaunchers(); // This will now include the notification permission launcher
        setupButtonClickListeners();

        String[] categories = getResources().getStringArray(R.array.issue_categories);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, categories);
        categoryAutoCompleteTextView.setAdapter(adapter);

        requestLocationPermission();
    }

    private void initializeViews() {
        // (This method is unchanged)
        issueImageView = findViewById(R.id.issueImageView);
        takePhotoButton = findViewById(R.id.takePhotoButton);
        galleryButton = findViewById(R.id.galleryButton);
        categoryAutoCompleteTextView = findViewById(R.id.categoryAutoCompleteTextView);
        descriptionEditText = findViewById(R.id.descriptionEditText);
        locationEditText = findViewById(R.id.locationEditText);
        gpsButton = findViewById(R.id.gpsButton);
        submitButton = findViewById(R.id.submitButton);
    }

    private void setupLaunchers() {
        // (Your existing launchers are the same)
        takePictureLauncher = registerForActivityResult(new ActivityResultContracts.TakePicturePreview(), result -> {
            if (result != null) { issueImageView.setImageBitmap(result); }
        });
        getContentLauncher = registerForActivityResult(new ActivityResultContracts.GetContent(), result -> {
            if (result != null) { issueImageView.setImageURI(result); }
        });
        requestCameraPermissionLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
            if (isGranted) { takePictureLauncher.launch(null); } else {
                Toast.makeText(this, "Camera permission is required", Toast.LENGTH_SHORT).show();
            }
        });
        locationPermissionLauncher = registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(), permissions -> {
            if (Boolean.TRUE.equals(permissions.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false))) {
                fetchLocation();
            } else {
                Toast.makeText(this, "Location permission denied", Toast.LENGTH_SHORT).show();
            }
        });

        // --- THIS IS NEW: Initialize the launcher for the notification permission ---
        requestNotificationPermissionLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
            if (isGranted) {
                // Permission was granted *after* we asked, so now send the notification
                notificationHelper.sendReportSubmittedNotification();
            } else {
                // Permission was denied.
                Toast.makeText(this, "Notification permission denied.", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void setupButtonClickListeners() {
        // (This method is unchanged)
        takePhotoButton.setOnClickListener(v -> {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                takePictureLauncher.launch(null);
            } else {
                requestCameraPermissionLauncher.launch(Manifest.permission.CAMERA);
            }
        });
        galleryButton.setOnClickListener(v -> getContentLauncher.launch("image/*"));
        gpsButton.setOnClickListener(v -> requestLocationPermission());
        submitButton.setOnClickListener(v -> handleSubmitIssue());
    }

    private void handleSubmitIssue() {
        // (This method is unchanged up to the success response)
        String category = categoryAutoCompleteTextView.getText().toString();
        String description = descriptionEditText.getText().toString().trim();
        int userId = sessionManager.getUserId();

        if (category.isEmpty() || description.isEmpty()) { /* ... */ return; }
        if (lastKnownLocation == null) { /* ... */ return; }
        if (userId == -1) { /* ... */ return; }

        IssueRequest issueRequest = new IssueRequest(
                category,
                description,
                lastKnownLocation.getLatitude(),
                lastKnownLocation.getLongitude(),
                userId
        );

        apiService.createIssue(issueRequest).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(RaiseIssueActivity.this, "Issue submitted successfully!", Toast.LENGTH_LONG).show();

                    // --- THIS IS THE NEW LOGIC ---
                    // We are now triggering the notification INSTANTLY.
                    triggerNotification();
                    // --- END NEW LOGIC ---

                    finish(); // Close the form immediately
                } else {
                    Toast.makeText(RaiseIssueActivity.this, "Failed to submit issue. Please try again.", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(RaiseIssueActivity.this, "Network Error: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    // --- THIS IS A NEW HELPER METHOD ---
    private void triggerNotification() {
        // On modern Android, we must check for permission before sending a notification
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) { // TIRAMISU is Android 13
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
                // Permission is already granted, send the notification
                notificationHelper.sendReportSubmittedNotification();
            } else {
                // Permission is not granted, request it from the user
                // The launcher's callback (defined in setupLaunchers) will handle the result.
                requestNotificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS);
            }
        } else {
            // This is an older phone (Android 12 or below), so permission is not needed. Just send it.
            notificationHelper.sendReportSubmittedNotification();
        }
    }

    // (The location methods are unchanged)
    private void requestLocationPermission() {
        boolean hasPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
        if (!hasPermission) {
            locationPermissionLauncher.launch(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION});
        } else {
            fetchLocation();
        }
    }

    private void fetchLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) { return; }

        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, location -> {
                    if (location != null) {
                        lastKnownLocation = location;
                        String coordinates = String.format(Locale.getDefault(), "Lat: %.5f, Lon: %.5f", location.getLatitude(), location.getLongitude());
                        locationEditText.setText(coordinates);
                    } else {
                        Toast.makeText(this, "Unable to fetch location. Please ensure GPS is on.", Toast.LENGTH_LONG).show();
                    }
                });
    }
}

