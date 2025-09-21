package com.book.civiclink2o;

import android.Manifest;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.material.button.MaterialButton;

import java.util.Locale;

public class RaiseIssueActivity extends AppCompatActivity {

    private ImageView issueImageView, gpsButton;
    private MaterialButton takePhotoButton, galleryButton;
    private AutoCompleteTextView categoryAutoCompleteTextView;
    private TextView locationTextView;

    // Launcher for requesting location permissions
    private ActivityResultLauncher<String[]> locationPermissionLauncher;
    // Client for getting the device's location
    private FusedLocationProviderClient fusedLocationClient;
    private boolean isLocationPermissionGranted = false;

    private ActivityResultLauncher<Void> takePictureLauncher;
    private ActivityResultLauncher<String> getContentLauncher;
    private ActivityResultLauncher<String> requestCameraPermissionLauncher;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_raise_issue);

        // Find views
        issueImageView = findViewById(R.id.issueImageView);
        takePhotoButton = findViewById(R.id.takePhotoButton);
        galleryButton = findViewById(R.id.galleryButton);
        categoryAutoCompleteTextView = findViewById(R.id.categoryAutoCompleteTextView);
        locationTextView = findViewById(R.id.locationTextView);
        gpsButton = findViewById(R.id.gpsButton);

        // Initialize the location client
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        // Initialize the location permission launcher
        locationPermissionLauncher = registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(), permissions -> {
            if (Boolean.TRUE.equals(permissions.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false)) ||
                    Boolean.TRUE.equals(permissions.getOrDefault(Manifest.permission.ACCESS_COARSE_LOCATION, false))) {
                isLocationPermissionGranted = true;
                fetchLocation();
            } else {
                isLocationPermissionGranted = false;
                Toast.makeText(this, "Location permission is required to fetch address", Toast.LENGTH_SHORT).show();
            }
        });


        // Setup for dropdown
        String[] categories = getResources().getStringArray(R.array.issue_categories);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, categories);
        categoryAutoCompleteTextView.setAdapter(adapter);

        // Setup for camera/gallery
        setupCameraAndGalleryLaunchers();
        setupButtonClickListeners();

        // Automatically try to fetch location when the screen opens
        requestLocationPermission();
    }

    private void setupCameraAndGalleryLaunchers() {
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
    }

    /**
     * Checks for location permissions and requests them if not already granted.
     */
    private void requestLocationPermission() {
        boolean hasFineLocation = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
        boolean hasCoarseLocation = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED;

        isLocationPermissionGranted = hasFineLocation || hasCoarseLocation;

        if (!isLocationPermissionGranted) {
            locationPermissionLauncher.launch(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION});
        } else {
            fetchLocation();
        }
    }

    /**
     * Fetches the last known location of the device and displays the coordinates.
     */
    private void fetchLocation() {
        // Double-check permission before proceeding (this is required by Android)
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // This check is mainly for the IDE; the logic in requestLocationPermission() should prevent this from being called without permission.
            return;
        }

        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, location -> {
                    if (location != null) {
                        // --- THIS IS THE FIX ---
                        // We got the location. Now format the coordinates into a string.
                        double latitude = location.getLatitude();
                        double longitude = location.getLongitude();
                        // Format to 5 decimal places for good precision
                        String coordinates = String.format(Locale.getDefault(), "Lat: %.5f, Lon: %.5f", latitude, longitude);

                        // Update the TextView
                        locationTextView.setText(coordinates);
                        locationTextView.setTextColor(ContextCompat.getColor(this, R.color.text_primary));
                        // --- END OF FIX ---
                    } else {
                        locationTextView.setText("Could not get location. Try again.");
                        Toast.makeText(this, "Unable to fetch location. Please ensure GPS is on.", Toast.LENGTH_LONG).show();
                    }
                });
    }
}