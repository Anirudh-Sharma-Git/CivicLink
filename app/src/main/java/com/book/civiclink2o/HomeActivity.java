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

        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        fab = findViewById(R.id.fab);
        bottomAppBar = findViewById(R.id.bottomAppBar);

        bottomNavigationView.getMenu().findItem(R.id.navigation_placeholder).setEnabled(false);

        bottomAppBar.setFabAlignmentMode(BottomAppBar.FAB_ALIGNMENT_MODE_CENTER);

        handleIntent(getIntent());

        if (savedInstanceState == null && getIntent().getStringExtra("NAVIGATE_TO") == null) {
            loadFragment(new HomeFragment());
        }

        bottomNavigationView.setOnItemSelectedListener(item -> {
            Fragment selectedFragment = null;
            int itemId = item.getItemId();

            if (itemId == R.id.navigation_placeholder) {
                return false;
            }

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
                return true;
            }
            return false;
        });

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HomeActivity.this, RaiseIssueActivity.class);
                startActivity(intent);
            }
        });
    }


    private void handleIntent(Intent intent) {
        if (intent != null && "MY_REPORTS".equals(intent.getStringExtra("NAVIGATE_TO"))) {
            loadFragment(new MyReportsFragment());
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

