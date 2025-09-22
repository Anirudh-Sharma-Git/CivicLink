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

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MyReportsFragment extends Fragment implements View.OnClickListener {

    // --- UI Elements ---
    private RecyclerView myReportsRecyclerView;
    private IssuesAdapter issuesAdapter;
    private List<TextView> filterChips;

    // --- Data & API ---
    private ApiService apiService;
    private SessionManager sessionManager;
    private List<Issue> myIssues = new ArrayList<>(); // Master list of the user's issues

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_my_reports, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // --- Initialize everything ---
        sessionManager = new SessionManager(getContext());
        setupRecyclerView(view);
        setupFilterChips(view);
        setupApiService();

        // --- Start loading the data ---
        fetchUserIssues();
    }

    private void setupRecyclerView(View view) {
        myReportsRecyclerView = view.findViewById(R.id.myReportsRecyclerView);
        myReportsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        issuesAdapter = new IssuesAdapter(getContext(), new ArrayList<>());
        myReportsRecyclerView.setAdapter(issuesAdapter);
    }

    private void setupFilterChips(View view) {
        filterChips = new ArrayList<>();
        TextView chipAll = view.findViewById(R.id.my_reports_chip_all);
        TextView chipPending = view.findViewById(R.id.my_reports_chip_pending);
        TextView chipInProgress = view.findViewById(R.id.my_reports_chip_in_progress);
        TextView chipResolved = view.findViewById(R.id.my_reports_chip_resolved);
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

    private void fetchUserIssues() {
        int userId = sessionManager.getUserId();
        if (userId == -1) {
            Toast.makeText(getContext(), "You are not logged in.", Toast.LENGTH_SHORT).show();
            // Optionally, clear the list if a guest user gets here
            myIssues.clear();
            filterList("All");
            return;
        }

        // Make the specific API call to get issues for this user
        apiService.getIssuesForUser(userId).enqueue(new Callback<List<Issue>>() {
            @Override
            public void onResponse(Call<List<Issue>> call, Response<List<Issue>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    myIssues.clear();
                    myIssues.addAll(response.body());
                    filterList("All"); // Show all of the user's issues by default
                } else {
                    Toast.makeText(getContext(), "Failed to load your reports", Toast.LENGTH_SHORT).show();
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
            filteredList = new ArrayList<>(myIssues);
        } else {
            // Use modern Java streams to filter the list
            filteredList = myIssues.stream()
                    .filter(issue -> issue.getStatus().equalsIgnoreCase(status))
                    .collect(Collectors.toList());
        }
        issuesAdapter.updateIssues(filteredList);
    }
}