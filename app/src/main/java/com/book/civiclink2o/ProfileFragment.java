package com.book.civiclink2o;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.google.android.material.button.MaterialButton;

public class ProfileFragment extends Fragment {

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

        // Find the logout button by its new ID
        MaterialButton logoutButton = view.findViewById(R.id.logoutButton);

        // Set a click listener on the button
        logoutButton.setOnClickListener(v -> {
            // Create an Intent to open the LoginActivity
            Intent intent = new Intent(getActivity(), LoginActivity.class);

            // These flags are very important for a logout flow.
            // They clear the entire "back stack" of activities, so the user can't
            // press the back button to get back into the app after logging out.
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

            // Start the LoginActivity
            startActivity(intent);

            // Finish the current activity (HomeActivity)
            if (getActivity() != null) {
                getActivity().finish();
            }
        });

        // --- END OF NEW LOGIC ---
    }
}