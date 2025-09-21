package com.book.civiclink2o;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import java.util.ArrayList;
import java.util.List;

public class MyReportsFragment extends Fragment implements View.OnClickListener {

    private List<TextView> filterChips;
    // References to our card containers
    private CardView cardPothole, cardStreetlight, cardGarbage;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_my_reports, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // --- Setup for Filter Chips ---
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

        // --- Find the card containers by their IDs ---
        cardPothole = view.findViewById(R.id.my_reports_card_pothole);
        cardStreetlight = view.findViewById(R.id.my_reports_card_streetlight);
        cardGarbage = view.findViewById(R.id.my_reports_card_garbage);

        // Set the "All" chip as selected by default
        chipAll.setSelected(true);
    }

    @Override
    public void onClick(View clickedView) {
        // Part 1: Update the visual style of the clicked chip
        for (TextView chip : filterChips) {
            chip.setSelected(chip == clickedView);
        }

        // Part 2: Show/hide the cards based on which chip was clicked
        int id = clickedView.getId();
        if (id == R.id.my_reports_chip_all) {
            // Show all cards
            cardPothole.setVisibility(View.VISIBLE);
            cardStreetlight.setVisibility(View.VISIBLE);
            cardGarbage.setVisibility(View.VISIBLE);
        } else if (id == R.id.my_reports_chip_pending) {
            // Show only 'Pending' cards (our sample Garbage card)
            cardPothole.setVisibility(View.GONE);
            cardStreetlight.setVisibility(View.GONE);
            cardGarbage.setVisibility(View.VISIBLE);
        } else if (id == R.id.my_reports_chip_in_progress) {
            // Show only 'In Progress' cards (our sample Pothole card)
            cardPothole.setVisibility(View.VISIBLE);
            cardStreetlight.setVisibility(View.GONE);
            cardGarbage.setVisibility(View.GONE);
        } else if (id == R.id.my_reports_chip_resolved) {
            // Show only 'Resolved' cards (our sample Street Light card)
            cardPothole.setVisibility(View.GONE);
            cardStreetlight.setVisibility(View.VISIBLE);
            cardGarbage.setVisibility(View.GONE);
        }
    }
}