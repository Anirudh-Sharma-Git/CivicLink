package com.book.civiclink2o;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView; // Make sure this is imported

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.button.MaterialButton;

public class ProfileFragment extends Fragment {

    // --- THIS IS NEW: Add references for the views we need to update ---
    private TextView profileName;
    private TextView profileInitial;
    private SessionManager sessionManager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // This connects our Java file to the layout for the profile fragment
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // --- THIS IS THE NEW LOGIC ---

        // 1. Initialize our SessionManager
        sessionManager = new SessionManager(getContext());

        // 2. Find the TextViews from the layout by their IDs
        profileName = view.findViewById(R.id.profile_name);
        profileInitial = view.findViewById(R.id.profile_initial);

        // 3. Check if a user is actually logged in
        if (sessionManager.isLoggedIn()) {
            // If they are, get their name from the session "memory"
            String userName = sessionManager.getUserName();

            // Update the main name TextView
            profileName.setText(userName);

            // Update the initial in the circle
            if (userName != null && !userName.isEmpty()) {
                profileInitial.setText(String.valueOf(userName.charAt(0)));
            }
        }
        // If no one is logged in, the layout will just show the default "Guest User" text.

        // --- END OF NEW LOGIC ---


        // This is the logout button logic we already built
        MaterialButton logoutButton = view.findViewById(R.id.logoutButton);
        logoutButton.setOnClickListener(v -> {
            // --- THIS IS A SMALL FIX: We need to clear the session on logout ---
            sessionManager.logoutUser(); // This clears the app's "memory"

            Intent intent = new Intent(getActivity(), LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            if (getActivity() != null) {
                getActivity().finish();
            }
        });
    }
}