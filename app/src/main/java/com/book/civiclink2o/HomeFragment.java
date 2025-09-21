package com.book.civiclink2o;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment implements View.OnClickListener {

    private List<TextView> filterChips;
    // References to our card containers
    private View cardPothole, cardGarbage, cardStreetLight, cardPothole2;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // --- Setup for Filter Chips (same as before) ---
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

        // --- THIS IS NEW: Find the card containers by their IDs ---
        cardPothole = view.findViewById(R.id.card_pothole);
        cardGarbage = view.findViewById(R.id.card_garbage);
        cardStreetLight = view.findViewById(R.id.card_street_light);
        cardPothole2 = view.findViewById(R.id.card_pothole_2);


        // Set the "All" chip as selected by default when the screen loads
        chipAll.setSelected(true);
    }

    @Override
    public void onClick(View clickedView) {
        // This part handles updating the visual style of the clicked chip
        for (TextView chip : filterChips) {
            chip.setSelected(chip == clickedView);
        }

        // --- THIS IS THE NEW FILTERING LOGIC ---
        // This part shows/hides the cards based on which chip was clicked
        int id = clickedView.getId();
        if (id == R.id.chip_all) {
            // Show all cards
            cardPothole.setVisibility(View.VISIBLE);
            cardGarbage.setVisibility(View.VISIBLE);
            cardStreetLight.setVisibility(View.VISIBLE);
            cardPothole2.setVisibility(View.VISIBLE);
        } else if (id == R.id.chip_pending) {
            // Show only 'Pending' cards (our sample Pothole cards)
            cardPothole.setVisibility(View.VISIBLE);
            cardPothole2.setVisibility(View.VISIBLE);
            cardGarbage.setVisibility(View.GONE);
            cardStreetLight.setVisibility(View.GONE);
        } else if (id == R.id.chip_in_progress) {
            // Show only 'In Progress' cards (our sample Garbage card)
            cardPothole.setVisibility(View.GONE);
            cardGarbage.setVisibility(View.VISIBLE);
            cardStreetLight.setVisibility(View.GONE);
            cardPothole2.setVisibility(View.GONE);
        } else if (id == R.id.chip_resolved) {
            // Show only 'Resolved' cards (our sample Street Light card)
            cardPothole.setVisibility(View.GONE);
            cardGarbage.setVisibility(View.GONE);
            cardStreetLight.setVisibility(View.VISIBLE);
            cardPothole2.setVisibility(View.GONE);
        }
    }
}