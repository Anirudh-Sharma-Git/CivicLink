package com.book.civiclink2o;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class HomeActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;
    private FloatingActionButton fab;
    private BottomAppBar bottomAppBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // Find the views from our layout file
        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        fab = findViewById(R.id.fab);
        bottomAppBar = findViewById(R.id.bottomAppBar);

        // This prevents the placeholder item in the middle from being clickable
        bottomNavigationView.getMenu().findItem(R.id.navigation_placeholder).setEnabled(false);

        // Ensure FAB is centered programmatically
        bottomAppBar.setFabAlignmentMode(BottomAppBar.FAB_ALIGNMENT_MODE_CENTER);

        // --- THIS IS THE NEW LOGIC TO HANDLE NOTIFICATION CLICKS ---
        // We check the intent that started this activity
        handleIntent(getIntent());
        // --- END OF NEW LOGIC ---

        // Load the default fragment (HomeFragment) when the app starts
        // We add a check to make sure we're not being launched from a notification
        if (savedInstanceState == null && getIntent().getStringExtra("NAVIGATE_TO") == null) {
            loadFragment(new HomeFragment());
        }

        // Set up the listener for the bottom navigation view
        bottomNavigationView.setOnItemSelectedListener(item -> {
            Fragment selectedFragment = null;
            int itemId = item.getItemId();

            // Handle navigation item selection - ignore placeholder clicks
            if (itemId == R.id.navigation_placeholder) {
                return false; // Ignore clicks on the placeholder
            }

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
                Intent intent = new Intent(HomeActivity.this, RaiseIssueActivity.class);
                startActivity(intent);
            }
        });
    }

    // --- THIS IS A NEW HELPER METHOD ---
    /**
     * Checks the intent that started this activity to see if it came from a notification.
     * @param intent The intent to check.
     */
    private void handleIntent(Intent intent) {
        if (intent != null && "MY_REPORTS".equals(intent.getStringExtra("NAVIGATE_TO"))) {
            // The "extra" from our notification was found!
            // Load the MyReportsFragment instead of the default HomeFragment.
            loadFragment(new MyReportsFragment());
            // Also, update the bottom navigation bar to show the "My Reports" icon as selected.
            bottomNavigationView.setSelectedItemId(R.id.navigation_reports);
        }
    }

    // --- THIS IS A NEW OVERRIDE METHOD ---
    /**
     * This is a special method that's called if the activity is already running in the background
     * when the user taps the notification. This ensures it still works correctly.
     */
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        // We received a new intent (from the notification), so we must handle it.
        handleIntent(intent);
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

