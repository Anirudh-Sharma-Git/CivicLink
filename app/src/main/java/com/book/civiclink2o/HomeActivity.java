package com.book.civiclink2o;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class HomeActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;
    private FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // Find the views from our layout file
        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        fab = findViewById(R.id.fab);

        // This prevents the placeholder item in the middle from being clickable
        bottomNavigationView.getMenu().findItem(R.id.navigation_placeholder).setEnabled(false);

        // Load the default fragment (HomeFragment) when the app starts
        if (savedInstanceState == null) {
            loadFragment(new HomeFragment());
        }

        // Set up the listener for the bottom navigation view
        bottomNavigationView.setOnItemSelectedListener(item -> {
            Fragment selectedFragment = null;
            int itemId = item.getItemId();

            // A switch statement is a clean way to handle multiple menu items
            if (itemId == R.id.navigation_home) {
                selectedFragment = new HomeFragment();
            } else if (itemId == R.id.navigation_reports) {
                selectedFragment = new MyReportsFragment();
            } else if (itemId == R.id.navigation_leaderboard) {
                selectedFragment = new LeaderboardFragment();
            } else if (itemId == R.id.navigation_profile) {
                selectedFragment = new ProfileFragment();
            }

            if (selectedFragment != null) {
                loadFragment(selectedFragment);
                return true; // Return true to show the item as selected
            }
            return false;
        });

        // Set up the listener for the floating action button (+)
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // --- THIS IS THE FIX ---
                // The following two lines have been uncommented to make the button work.
                Intent intent = new Intent(HomeActivity.this, RaiseIssueActivity.class);
                startActivity(intent);
                // --- END OF FIX ---
            }
        });
    }

    /**
     * A helper method to replace the current fragment in the container.
     * @param fragment The fragment to display.
     */
    private void loadFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, fragment);
        fragmentTransaction.commit();
    }
}

