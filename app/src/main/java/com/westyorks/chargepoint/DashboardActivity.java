package com.westyorks.chargepoint;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.widget.ViewPager2;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.firebase.auth.FirebaseAuth;
import com.westyorks.chargepoint.auth.FirebaseAuthHelper;
import com.westyorks.chargepoint.auth.UserPermissionHelper;
import com.westyorks.chargepoint.viewmodel.ChargepointViewModel;

/**
 * DashboardActivity is the main screen of the application after login.
 * It manages the ViewPager2 for switching between the list and map views,
 * handles location permissions, and manages the loading state of chargepoint data.
 */
public class DashboardActivity extends AppCompatActivity implements FirebaseAuthHelper.AuthCallback {
    
    // UI Components
    private ViewPager2 viewPager;
    private TabLayout tabLayout;
    private ProgressBar progressBar;
    
    // Helper Classes
    private FirebaseAuthHelper authHelper;
    private DashboardPagerAdapter pagerAdapter;
    private ChargepointViewModel viewModel;
    private UserPermissionHelper permissionHelper;
    
    // Permission Handling
    private ActivityResultLauncher<String> requestPermissionLauncher;
    private TabLayoutMediator tabLayoutMediator;
    private ViewPager2.OnPageChangeCallback pageChangeCallback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        // Initialize Firebase Auth and Permission Helper
        authHelper = new FirebaseAuthHelper(this);
        permissionHelper = new UserPermissionHelper();
        
        // Check if user is logged in
        if (!authHelper.isUserLoggedIn()) {
            // If not logged in, redirect to login activity
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        // Initialize views and setup UI
        initializeViews();
        setupPermissionLauncher();
        initializeViewModel();
        checkLocationPermission();
        checkUserPermissionsAndSetupViewPager();
    }

    /**
     * Initializes all UI components and sets their initial visibility.
     */
    private void initializeViews() {
        progressBar = findViewById(R.id.progressBar);
        viewPager = findViewById(R.id.viewPager);
        tabLayout = findViewById(R.id.tabLayout);

        // Enable swipe to change pages
        viewPager.setUserInputEnabled(true);
        
        // Show loading indicator initially
        progressBar.setVisibility(View.VISIBLE);
        viewPager.setVisibility(View.GONE);
        tabLayout.setVisibility(View.GONE);
    }

    /**
     * Sets up the permission launcher for location access.
     */
    private void setupPermissionLauncher() {
        requestPermissionLauncher = registerForActivityResult(
            new ActivityResultContracts.RequestPermission(),
            isGranted -> {
                if (!isGranted) {
                    Toast.makeText(this, "Location permission is required for full functionality", 
                        Toast.LENGTH_LONG).show();
                }
            }
        );
    }

    /**
     * Initializes the ViewModel and observes chargepoint data.
     */
    private void initializeViewModel() {
        viewModel = new ViewModelProvider(this).get(ChargepointViewModel.class);
        
        // Observe data loading
        viewModel.getAllChargepoints().observe(this, chargepoints -> {
            if (progressBar.getVisibility() == View.VISIBLE) {
                progressBar.setVisibility(View.GONE);
                viewPager.setVisibility(View.VISIBLE);
                tabLayout.setVisibility(View.VISIBLE);
            }
        });
    }

    /**
     * Checks and requests location permission if not granted.
     */
    private void checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION);
        }
    }

    private void checkUserPermissionsAndSetupViewPager() {
        permissionHelper.checkEditPermission(hasEditPermission -> {
            // Initialize ViewPager with permission status
            pagerAdapter = new DashboardPagerAdapter(this);
            pagerAdapter.setHasEditPermission(hasEditPermission);
            viewPager.setAdapter(pagerAdapter);
            
            // Setup TabLayout with ViewPager
            if (tabLayoutMediator != null) {
                tabLayoutMediator.detach();
            }
            
            tabLayoutMediator = new TabLayoutMediator(tabLayout, viewPager,
                (tab, position) -> tab.setText(position == 0 ? "List" : "Map")
            );
            tabLayoutMediator.attach();

            // Register page change callback
            if (pageChangeCallback != null) {
                viewPager.unregisterOnPageChangeCallback(pageChangeCallback);
            }
            
            pageChangeCallback = new ViewPager2.OnPageChangeCallback() {
                @Override
                public void onPageSelected(int position) {
                    super.onPageSelected(position);
                    // Update UI based on selected page if needed
                }
            };
            viewPager.registerOnPageChangeCallback(pageChangeCallback);

            progressBar.setVisibility(View.GONE);
            viewPager.setVisibility(View.VISIBLE);
            tabLayout.setVisibility(View.VISIBLE);
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (tabLayoutMediator != null) {
            tabLayoutMediator.detach();
        }
        if (pageChangeCallback != null) {
            viewPager.unregisterOnPageChangeCallback(pageChangeCallback);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu with logout option
        getMenuInflater().inflate(R.menu.menu_dashboard, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_logout) {
            // Handle logout action
            authHelper.logoutUser();
            startActivity(new Intent(this, MainActivity.class));
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // FirebaseAuthHelper.AuthCallback implementation
    @Override
    public void onSuccess() {
        // Not used in this activity
    }

    @Override
    public void onError(String error) {
        Toast.makeText(this, error, Toast.LENGTH_SHORT).show();
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }
}