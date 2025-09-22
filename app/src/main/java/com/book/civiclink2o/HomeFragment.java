package com.book.civiclink2o;

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

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class HomeFragment extends Fragment implements View.OnClickListener, OnMapReadyCallback {

    // --- UI Elements ---
    private RecyclerView issuesRecyclerView;
    private IssuesAdapter issuesAdapter;
    private List<TextView> filterChips;
    private GoogleMap mMap;

    // --- Data & API ---
    private ApiService apiService;
    private List<Issue> allIssues = new ArrayList<>(); // Master list from the server

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // --- Initialize everything ---
        setupRecyclerView(view);
        setupFilterChips(view);
        setupApiService();
        setupMap();

        // --- Start loading the data ---
        fetchIssues();
    }

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
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://10.0.2.2:3000")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        apiService = retrofit.create(ApiService.class);
    }

    private void setupMap() {
        // Use getChildFragmentManager for fragments inside fragments
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
    }

    private void fetchIssues() {
        apiService.getAllIssues().enqueue(new Callback<List<Issue>>() {
            @Override
            public void onResponse(Call<List<Issue>> call, Response<List<Issue>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    allIssues.clear();
                    allIssues.addAll(response.body());
                    filterList("All"); // Show all issues by default
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
            // Use modern Java streams to filter the list
            filteredList = allIssues.stream()
                    .filter(issue -> issue.getStatus().equalsIgnoreCase(status))
                    .collect(Collectors.toList());
        }
        // Update the adapter with the new, filtered list
        issuesAdapter.updateIssues(filteredList);
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;
        // Example: Center map on Ranchi
        LatLng ranchi = new LatLng(23.3441, 85.3096);
        mMap.addMarker(new MarkerOptions().position(ranchi).title("Ranchi"));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(ranchi, 12f));
        // In the future, we would add markers for each issue from the 'allIssues' list here
    }
}