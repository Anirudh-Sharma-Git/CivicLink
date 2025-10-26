package com.book.civiclink2o;

import android.Manifest;
import android.content.ContentResolver; // NEW IMPORT
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.webkit.MimeTypeMap; // NEW IMPORT
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

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Locale;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RaiseIssueActivity extends AppCompatActivity {

    // (All your existing variables are correct and do not need to change)
    private ImageView issueImageView, gpsButton;
    private MaterialButton takePhotoButton, galleryButton, submitButton;
    private AutoCompleteTextView categoryAutoCompleteTextView;
    private EditText descriptionEditText, locationEditText;
    private ApiService apiService;
    private SessionManager sessionManager;
    private FusedLocationProviderClient fusedLocationClient;
    private Location lastKnownLocation;
    private Uri selectedImageUri = null;
    private NotificationHelper notificationHelper;
    private ActivityResultLauncher<String> requestNotificationPermissionLauncher;
    private ActivityResultLauncher<Void> takePictureLauncher;
    private ActivityResultLauncher<String> getContentLauncher;
    private ActivityResultLauncher<String> requestCameraPermissionLauncher;
    private ActivityResultLauncher<String[]> locationPermissionLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_raise_issue);
        // (All your onCreate code is correct and does not need to change)
        sessionManager = new SessionManager(getApplicationContext());
        apiService = ApiClient.getClient().create(ApiService.class);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        notificationHelper = new NotificationHelper(this);
        initializeViews();
        setupLaunchers();
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
        // (This method is unchanged)
        getContentLauncher = registerForActivityResult(new ActivityResultContracts.GetContent(), result -> {
            if (result != null) {
                selectedImageUri = result;
                issueImageView.setImageURI(selectedImageUri);
            }
        });
        takePictureLauncher = registerForActivityResult(new ActivityResultContracts.TakePicturePreview(), result -> {});
        requestCameraPermissionLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
            if (!isGranted) { Toast.makeText(this, "Camera permission is required", Toast.LENGTH_SHORT).show(); }
        });
        locationPermissionLauncher = registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(), permissions -> {
            if (Boolean.TRUE.equals(permissions.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false))) {
                fetchLocation();
            } else {
                Toast.makeText(this, "Location permission denied", Toast.LENGTH_SHORT).show();
            }
        });
        requestNotificationPermissionLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
            if (isGranted) {
                notificationHelper.sendReportSubmittedNotification();
            } else {
                Toast.makeText(this, "Notification permission denied.", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void setupButtonClickListeners() {
        // (This method is unchanged)
        takePhotoButton.setEnabled(false);
        takePhotoButton.setText("Camera (Soon)");
        galleryButton.setOnClickListener(v -> getContentLauncher.launch("image/*"));
        gpsButton.setOnClickListener(v -> requestLocationPermission());
        submitButton.setOnClickListener(v -> handleSubmitIssue());
    }

    private void handleSubmitIssue() {
        // (This method is unchanged, it was already correct)
        String category = categoryAutoCompleteTextView.getText().toString();
        String description = descriptionEditText.getText().toString().trim();
        int userId = sessionManager.getUserId();

        if (selectedImageUri == null) { Toast.makeText(this, "Please select an image.", Toast.LENGTH_SHORT).show(); return; }
        if (category.isEmpty() || description.isEmpty()) { Toast.makeText(this, "Please fill category and description.", Toast.LENGTH_SHORT).show(); return; }
        if (lastKnownLocation == null) { Toast.makeText(this, "Location not available.", Toast.LENGTH_SHORT).show(); return; }
        if (userId == -1) { Toast.makeText(this, "Error: You are not logged in.", Toast.LENGTH_SHORT).show(); return; }

        RequestBody categoryPart = RequestBody.create(MediaType.parse("text/plain"), category);
        RequestBody descriptionPart = RequestBody.create(MediaType.parse("text/plain"), description);
        RequestBody latPart = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(lastKnownLocation.getLatitude()));
        RequestBody lonPart = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(lastKnownLocation.getLongitude()));
        RequestBody userIdPart = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(userId));

        MultipartBody.Part imagePart = prepareFilePart("image", selectedImageUri);
        if (imagePart == null) {
            Toast.makeText(this, "Error preparing image file.", Toast.LENGTH_SHORT).show();
            return;
        }

        apiService.createIssue(imagePart, categoryPart, descriptionPart, latPart, lonPart, userIdPart)
                .enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        if (response.isSuccessful()) {
                            Toast.makeText(RaiseIssueActivity.this, "Issue submitted successfully!", Toast.LENGTH_LONG).show();
                            triggerNotification();
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

    // --- THIS IS A NEW HELPER METHOD ---
    /**
     * Gets the file extension (e.g., "jpg", "png") from a content Uri.
     */
    private String getFileExtension(Context context, Uri uri) {
        ContentResolver cR = context.getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }

    // --- THIS METHOD IS NOW CORRECTED ---
    private MultipartBody.Part prepareFilePart(String partName, Uri fileUri) {
        try {
            Context context = getApplicationContext();
            File file = FileUtils.getFileFromUri(context, fileUri);
            if (file == null) {
                Toast.makeText(this, "Failed to create temp file", Toast.LENGTH_SHORT).show();
                return null;
            }

            // THE FIX: We now create a proper filename with an extension
            String fileExtension = getFileExtension(context, fileUri);
            String fileName = partName + "-" + System.currentTimeMillis() + "." + fileExtension;
            // --- END OF FIX ---

            RequestBody requestFile = RequestBody.create(
                    MediaType.parse(context.getContentResolver().getType(fileUri)),
                    file
            );

            // Send the real filename to the server
            return MultipartBody.Part.createFormData(partName, fileName, requestFile);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // (The notification and location methods are unchanged)
    private void triggerNotification() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
                notificationHelper.sendReportSubmittedNotification();
            } else {
                requestNotificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS);
            }
        } else {
            notificationHelper.sendReportSubmittedNotification();
        }
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

