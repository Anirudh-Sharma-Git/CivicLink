package com.book.civiclink2o;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
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
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RaiseIssueActivity extends AppCompatActivity {

    // UI Elements
    private ImageView issueImageView, gpsButton;
    private MaterialButton takePhotoButton, galleryButton, submitButton;
    private AutoCompleteTextView categoryAutoCompleteTextView;
    private EditText descriptionEditText, locationEditText;

    // API and Session Management
    private ApiService apiService;
    private SessionManager sessionManager;

    // Location Services
    private FusedLocationProviderClient fusedLocationClient; // This was declared...
    private Location lastKnownLocation;

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
        // THE FIX: Get the telephone system from our new, central ApiClient
        apiService = ApiClient.getClient().create(ApiService.class);

        // --- THIS IS THE FIX ---
        // We must initialize the location client here, before we ever try to use it.
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        // --- END OF FIX ---

        initializeViews();
        setupLaunchers();
        setupButtonClickListeners();

        String[] categories = getResources().getStringArray(R.array.issue_categories);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, categories);
        categoryAutoCompleteTextView.setAdapter(adapter);

        requestLocationPermission();
    }

    private void initializeViews() {
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
    }

    private void setupButtonClickListeners() {
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
        String category = categoryAutoCompleteTextView.getText().toString();
        String description = descriptionEditText.getText().toString().trim();
        int userId = sessionManager.getUserId();

        if (category.isEmpty() || description.isEmpty()) {
            Toast.makeText(this, "Please fill category and description.", Toast.LENGTH_SHORT).show();
            return;
        }
        if (lastKnownLocation == null) {
            Toast.makeText(this, "Location not available. Please try fetching it again.", Toast.LENGTH_SHORT).show();
            return;
        }
        if (userId == -1) {
            Toast.makeText(this, "Error: You are not logged in.", Toast.LENGTH_SHORT).show();
            return;
        }

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
                    finish();
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