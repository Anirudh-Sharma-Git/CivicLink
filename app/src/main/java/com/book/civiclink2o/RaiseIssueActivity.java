package com.book.civiclink2o;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.android.material.button.MaterialButton;

public class RaiseIssueActivity extends AppCompatActivity {

    private ImageView issueImageView;
    private MaterialButton takePhotoButton, galleryButton;

    // This is the new, recommended way to handle activity results (like taking a picture)
    private ActivityResultLauncher<Void> takePictureLauncher;
    private ActivityResultLauncher<String> getContentLauncher;
    private ActivityResultLauncher<String> requestPermissionLauncher;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_raise_issue);

        // Find our views from the layout using the new IDs
        issueImageView = findViewById(R.id.issueImageView);
        takePhotoButton = findViewById(R.id.takePhotoButton);
        galleryButton = findViewById(R.id.galleryButton);

        // --- Initialize the ActivityResultLaunchers ---

        // Launcher for getting a thumbnail from the camera
        takePictureLauncher = registerForActivityResult(new ActivityResultContracts.TakePicturePreview(), result -> {
            if (result != null) {
                // We got a bitmap, so set it on our ImageView
                issueImageView.setImageBitmap(result);
            }
        });

        // Launcher for getting content (an image) from the gallery
        getContentLauncher = registerForActivityResult(new ActivityResultContracts.GetContent(), result -> {
            if (result != null) {
                // We got a URI, so set it on our ImageView
                issueImageView.setImageURI(result);
            }
        });

        // Launcher for requesting the camera permission
        requestPermissionLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
            if (isGranted) {
                // Permission is granted. Launch the camera.
                takePictureLauncher.launch(null);
            } else {
                // Permission was denied. Show a message to the user.
                Toast.makeText(this, "Camera permission is required to take photos", Toast.LENGTH_SHORT).show();
            }
        });


        // --- Set Click Listeners for the Buttons ---

        takePhotoButton.setOnClickListener(v -> {
            // Check if we already have camera permission
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                // If permission is already granted, launch the camera
                takePictureLauncher.launch(null);
            } else {
                // If permission is not granted, request it
                requestPermissionLauncher.launch(Manifest.permission.CAMERA);
            }
        });

        galleryButton.setOnClickListener(v -> {
            // Launch the gallery to pick an image. "image/*" means any image type.
            getContentLauncher.launch("image/*");
        });
    }
}