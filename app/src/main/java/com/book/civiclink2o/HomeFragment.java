package com.book.civiclink2o;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment implements View.OnClickListener, OnMapReadyCallback {

    private List<TextView> filterChips;
    private View cardPothole, cardGarbage, cardStreetLight, cardPothole2;
    private GoogleMap googleMap;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Setup filter chips
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

        // Find cards
        cardPothole = view.findViewById(R.id.card_pothole);
        cardGarbage = view.findViewById(R.id.card_garbage);
        cardStreetLight = view.findViewById(R.id.card_street_light);
        cardPothole2 = view.findViewById(R.id.card_pothole_2);

        // Default selection
        chipAll.setSelected(true);

        // Setup Map
        FragmentManager fm = getChildFragmentManager();
        SupportMapFragment mapFragment = (SupportMapFragment) fm.findFragmentById(R.id.map_container);
        if (mapFragment == null) {
            mapFragment = SupportMapFragment.newInstance();
            fm.beginTransaction().replace(R.id.map_container, mapFragment).commit();
        }
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onClick(View clickedView) {
        // Update chip selection state
        for (TextView chip : filterChips) {
            chip.setSelected(chip == clickedView);
        }

        int id = clickedView.getId();
        if (id == R.id.chip_all) {
            cardPothole.setVisibility(View.VISIBLE);
            cardGarbage.setVisibility(View.VISIBLE);
            cardStreetLight.setVisibility(View.VISIBLE);
            cardPothole2.setVisibility(View.VISIBLE);
        } else if (id == R.id.chip_pending) {
            cardPothole.setVisibility(View.VISIBLE);
            cardPothole2.setVisibility(View.VISIBLE);
            cardGarbage.setVisibility(View.GONE);
            cardStreetLight.setVisibility(View.GONE);
        } else if (id == R.id.chip_in_progress) {
            cardPothole.setVisibility(View.GONE);
            cardGarbage.setVisibility(View.VISIBLE);
            cardStreetLight.setVisibility(View.GONE);
            cardPothole2.setVisibility(View.GONE);
        } else if (id == R.id.chip_resolved) {
            cardPothole.setVisibility(View.GONE);
            cardGarbage.setVisibility(View.GONE);
            cardStreetLight.setVisibility(View.VISIBLE);
            cardPothole2.setVisibility(View.GONE);
        }
    }

    @Override
    public void onMapReady(@NonNull GoogleMap map) {
        googleMap = map;

        // Example marker: Navi Mumbai
        LatLng nmims = new LatLng(19.0330, 73.0297);
        googleMap.addMarker(new MarkerOptions().position(nmims).title("NMIMS Navi Mumbai"));
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(nmims, 14));
    }
}
