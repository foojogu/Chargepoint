package com.westyorks.chargepoint;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.widget.ViewPager2;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.westyorks.chargepoint.model.Chargepoint;
import com.westyorks.chargepoint.view.MapFrameLayout;
import com.westyorks.chargepoint.viewmodel.ChargepointViewModel;
import java.util.List;

/**
 * ChargepointMapFragment displays chargepoints on a Google Map.
 * It handles map initialization, marker placement, and location permissions.
 * The fragment also manages the state of pending chargepoints when the map
 * is not yet ready.
 */
public class ChargepointMapFragment extends Fragment implements OnMapReadyCallback {
    private static final String TAG = "ChargepointMapFragment";
    
    // Map-related fields
    private GoogleMap googleMap;
    private static final LatLng WEST_YORKSHIRE_CENTER = new LatLng(53.7947, -1.5478);
    private static final float DEFAULT_ZOOM = 10f;
    private boolean mapReady = false;
    
    // Data management
    private ChargepointViewModel viewModel;
    private List<Chargepoint> pendingChargepoints;
    private MapFrameLayout mapContainer;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Initialize ViewModel
        viewModel = new ViewModelProvider(requireActivity()).get(ChargepointViewModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chargepoint_map, container, false);
        
        // Get the MapFrameLayout and set up the ViewPager2
        mapContainer = view.findViewById(R.id.map_container);
        ViewPager2 viewPager = (ViewPager2) requireActivity().findViewById(R.id.viewPager);
        mapContainer.setViewPager(viewPager);
        
        // Get the map fragment and initialize the map
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
        
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onMapReady(@NonNull GoogleMap map) {
        googleMap = map;
        mapReady = true;
        mapContainer.setMapReady(true);

        // Set up map UI settings
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            googleMap.setMyLocationEnabled(true);
        }
        
        googleMap.getUiSettings().setZoomControlsEnabled(true);
        googleMap.getUiSettings().setMapToolbarEnabled(true);
        
        // Move camera to West Yorkshire
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(WEST_YORKSHIRE_CENTER, DEFAULT_ZOOM));
        
        // Set up custom info window adapter
        googleMap.setInfoWindowAdapter(new ChargepointInfoWindowAdapter(requireContext()));
        
        // Process any pending chargepoints
        if (pendingChargepoints != null) {
            updateMapMarkers(pendingChargepoints);
            pendingChargepoints = null;
        }
        
        // Observe chargepoints data
        viewModel.getAllChargepoints().observe(getViewLifecycleOwner(), this::updateMapMarkers);
    }

    /**
     * Updates map markers with the provided list of chargepoints.
     * If the map is not ready, stores the chargepoints for later processing.
     *
     * @param chargepoints List of chargepoints to display on the map
     */
    private void updateMapMarkers(List<Chargepoint> chargepoints) {
        if (!mapReady) {
            pendingChargepoints = chargepoints;
            return;
        }

        googleMap.clear();
        for (Chargepoint chargepoint : chargepoints) {
            LatLng position = new LatLng(chargepoint.getLatitude(), chargepoint.getLongitude());
            String snippet = String.format("%s, %s, %s - %s", 
                chargepoint.getTown(), 
                chargepoint.getCounty(), 
                chargepoint.getPostcode(),
                chargepoint.getChargerType());
            googleMap.addMarker(new MarkerOptions()
                    .position(position)
                    .title(chargepoint.getName())
                    .snippet(snippet));
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mapReady = false;
        if (mapContainer != null) {
            mapContainer.setMapReady(false);
        }
    }

    /**
     * Called when this fragment becomes visible in ViewPager2.
     * Refreshes the map view to ensure proper display.
     */
    public void onPageSelected() {
        if (googleMap != null) {
            // Refresh the map if needed
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                googleMap.getCameraPosition().target,
                googleMap.getCameraPosition().zoom
            ));
        }
    }
}