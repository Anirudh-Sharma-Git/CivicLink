package com.book.civiclink2o;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener {

    private GoogleMap mMap;
    private ArrayList<Issue> issuesList;
    private BottomSheetBehavior<FrameLayout> bottomSheetBehavior;
    private FrameLayout bottomSheetContainer;
    private FusedLocationProviderClient fusedLocationClient;

    private final ActivityResultLauncher<String[]> locationPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(), result -> {
                if (Boolean.TRUE.equals(result.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false))) {
                    centerMapOnUserLocation();
                } else {
                    Toast.makeText(this, "Location permission denied", Toast.LENGTH_SHORT).show();
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        issuesList = getIntent().getParcelableArrayListExtra("ISSUES_LIST");
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.full_map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        bottomSheetContainer = findViewById(R.id.bottom_sheet_container);
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheetContainer);
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);

        FloatingActionButton myLocationButton = findViewById(R.id.myLocationButton);
        myLocationButton.setOnClickListener(v -> requestLocationPermission());
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setOnMarkerClickListener(this);

        if (issuesList != null && !issuesList.isEmpty()) {
            for (Issue issue : issuesList) {
                if (issue.getLatitude() != 0 && issue.getLongitude() != 0) {
                    LatLng issueLocation = new LatLng(issue.getLatitude(), issue.getLongitude());
                    Marker marker = mMap.addMarker(new MarkerOptions()
                            .position(issueLocation)
                            .title(issue.getCategory())
                            .icon(BitmapDescriptorFactory.defaultMarker(getMarkerColor(issue.getStatus()))));
                    if (marker != null) {
                        marker.setTag(issue);
                    }
                }
            }
            LatLng firstIssueLocation = new LatLng(issuesList.get(0).getLatitude(), issuesList.get(0).getLongitude());
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(firstIssueLocation, 12f));
        } else {
            LatLng ranchi = new LatLng(23.3441, 85.3096);
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(ranchi, 12f));
        }
    }

    @Override
    public boolean onMarkerClick(@NonNull Marker marker) {
        Issue issue = (Issue) marker.getTag();
        if (issue != null) {
            View sheetView = getLayoutInflater().inflate(R.layout.bottom_sheet_issue_details, bottomSheetContainer, false);

            TextView category = sheetView.findViewById(R.id.sheet_issue_category);
            TextView status = sheetView.findViewById(R.id.sheet_issue_status);
            TextView title = sheetView.findViewById(R.id.sheet_issue_title);
            TextView location = sheetView.findViewById(R.id.sheet_issue_location);
            TextView time = sheetView.findViewById(R.id.sheet_issue_time);
            TextView upvotes = sheetView.findViewById(R.id.sheet_issue_upvotes);

            category.setText(issue.getCategory());
            title.setText(issue.getDescription());
            location.setText(issue.getReportedByName());
            time.setText(issue.getCreatedAt());
            upvotes.setText(String.valueOf(issue.getUpvotes()));
            status.setText(issue.getStatus());


            bottomSheetContainer.removeAllViews();
            bottomSheetContainer.addView(sheetView);
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        }
        return true;
    }

    private void requestLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            centerMapOnUserLocation();
        } else {
            locationPermissionLauncher.launch(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION});
        }
    }

    private void centerMapOnUserLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mMap.setMyLocationEnabled(true);
        fusedLocationClient.getLastLocation().addOnSuccessListener(this, location -> {
            if (location != null) {
                LatLng myLocation = new LatLng(location.getLatitude(), location.getLongitude());
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(myLocation, 15f));
            } else {
                Toast.makeText(this, "Could not get current location. Ensure GPS is enabled.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private float getMarkerColor(String status) {
        if (status == null) return BitmapDescriptorFactory.HUE_RED;
        switch (status) {
            case "Pending":
                return BitmapDescriptorFactory.HUE_RED;
            case "In Progress":
                return BitmapDescriptorFactory.HUE_YELLOW;
            case "Resolved":
                return BitmapDescriptorFactory.HUE_GREEN;
            default:
                return BitmapDescriptorFactory.HUE_RED;
        }
    }
}

