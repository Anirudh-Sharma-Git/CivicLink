package com.book.civiclink2o;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragment extends Fragment implements View.OnClickListener, OnMapReadyCallback {

    // (Your existing variables are correct)
    private RecyclerView issuesRecyclerView;
    private IssuesAdapter issuesAdapter;
    private List<TextView> filterChips;
    private GoogleMap mMap;
    private ApiService apiService;
    private List<Issue> allIssues = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setupRecyclerView(view);
        setupFilterChips(view);
        setupApiService();
        setupMap();

        FloatingActionButton expandMapButton = view.findViewById(R.id.expandMapButton);
        expandMapButton.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), MapActivity.class);
            // We now correctly pass our list of issues to the new activity
            intent.putParcelableArrayListExtra("ISSUES_LIST", new ArrayList<>(allIssues));
            startActivity(intent);
        });

        fetchIssues();
    }

    private void setupMap() {
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;
        // Just set the initial camera position. We'll add markers later.
        LatLng ranchi = new LatLng(23.3441, 85.3096);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(ranchi, 12f));
        // We call addMarkersToMap() here in case the map loads AFTER the issues have already arrived.
        addMarkersToMap();
    }

    private void fetchIssues() {
        apiService.getAllIssues().enqueue(new Callback<List<Issue>>() {
            @Override
            public void onResponse(Call<List<Issue>> call, Response<List<Issue>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    allIssues.clear();
                    allIssues.addAll(response.body());
                    filterList("All");

                    // THE FIX: The "delivery truck" has arrived. NOW we add the markers.
                    addMarkersToMap();
                } else {
                    Toast.makeText(getContext(), "Failed to load issues", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<List<Issue>> call, Throwable t) {
                Toast.makeText(getContext(), "Network Error: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void addMarkersToMap() {
        // This check prevents a crash if the map isn't ready yet or if there's no data.
        if (mMap == null || allIssues.isEmpty()) {
            return;
        }

        mMap.clear(); // Clear any old markers

        // Loop through every issue we fetched from the server
        for (Issue issue : allIssues) {
            if (issue.getLatitude() != 0 && issue.getLongitude() != 0) {
                LatLng issueLocation = new LatLng(issue.getLatitude(), issue.getLongitude());
                mMap.addMarker(new MarkerOptions().position(issueLocation).title(issue.getCategory()));
            }
        }
    }

    // (The rest of your methods: setupRecyclerView, setupFilterChips, setupApiService, onClick, filterList are all correct and remain the same)
    private void setupRecyclerView(View view) {
        issuesRecyclerView = view.findViewById(R.id.issuesRecyclerView);
        issuesRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        issuesAdapter = new IssuesAdapter(getContext(), new ArrayList<>());
        issuesRecyclerView.setAdapter(issuesAdapter);
    }

    private void setupFilterChips(View view) {
        filterChips = new ArrayList<>();
        TextView chipAll = view.findViewById(R.id.chip_all);
        TextView chipPending = view.findViewById(R.id.chip_pending);
        TextView chipInProgress = view.findViewById(R.id.chip_in_progress);
        TextView chipResolved = view.findViewById(R.id.chip_resolved);
        filterChips.add(chipAll);
        filterChips.add(chipPending);
        filterChips.add(chipInProgress);
        filterChips.add(chipResolved);
        for (TextView chip : filterChips) {
            chip.setOnClickListener(this);
        }
        chipAll.setSelected(true);
    }

    private void setupApiService() {
        apiService = ApiClient.getClient().create(ApiService.class);
    }

    @Override
    public void onClick(View clickedView) {
        for (TextView chip : filterChips) {
            chip.setSelected(chip == clickedView);
        }
        String filterType = ((TextView) clickedView).getText().toString();
        filterList(filterType);
    }

    private void filterList(String status) {
        List<Issue> filteredList;
        if (status.equalsIgnoreCase("All")) {
            filteredList = new ArrayList<>(allIssues);
        } else {
            filteredList = allIssues.stream()
                    .filter(issue -> issue.getStatus().equalsIgnoreCase(status))
                    .collect(Collectors.toList());
        }
        issuesAdapter.updateIssues(filteredList);
    }
}