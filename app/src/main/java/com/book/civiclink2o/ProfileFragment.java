package com.book.civiclink2o;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.button.MaterialButton;

public class ProfileFragment extends Fragment {

    private TextView profileName;
    private TextView profileInitial;
    private SessionManager sessionManager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        sessionManager = new SessionManager(getContext());

        profileName = view.findViewById(R.id.profile_name);
        profileInitial = view.findViewById(R.id.profile_initial);
        MaterialButton editProfileButton = view.findViewById(R.id.editProfileButton);
        MaterialButton logoutButton = view.findViewById(R.id.logoutButton);

        updateProfileInfo();

        editProfileButton.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), EditProfileActivity.class);
            startActivity(intent);
        });

        logoutButton.setOnClickListener(v -> {
            sessionManager.logoutUser();
            Intent intent = new Intent(getActivity(), LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            if (getActivity() != null) {
                getActivity().finish();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();

        updateProfileInfo();
    }


    private void updateProfileInfo() {
        if (sessionManager.isLoggedIn()) {
            String userName = sessionManager.getUserName();
            profileName.setText(userName);

            if (userName != null && !userName.isEmpty()) {
                // Set the initial in the circle, converting it to uppercase
                profileInitial.setText(String.valueOf(userName.charAt(0)).toUpperCase());
            }
        }
    }
}