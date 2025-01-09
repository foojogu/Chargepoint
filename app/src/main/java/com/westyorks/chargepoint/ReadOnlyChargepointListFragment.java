package com.westyorks.chargepoint;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.westyorks.chargepoint.adapter.ChargepointAdapter;
import com.westyorks.chargepoint.auth.FirebaseAuthHelper;
import com.westyorks.chargepoint.viewmodel.ChargepointViewModel;

public class ReadOnlyChargepointListFragment extends Fragment implements SearchView.OnQueryTextListener, FirebaseAuthHelper.AuthCallback {
    private ChargepointViewModel viewModel;
    private ChargepointAdapter adapter;
    private androidx.appcompat.widget.SearchView searchView;
    private RecyclerView recyclerView;
    private MaterialButton logoutButton;
    private FirebaseAuthHelper authHelper;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity()).get(ChargepointViewModel.class);
        authHelper = new FirebaseAuthHelper(this);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chargepoint_list_readonly, container, false);

        // Initialize views
        recyclerView = view.findViewById(R.id.recyclerView);
        searchView = view.findViewById(R.id.searchView);
        logoutButton = view.findViewById(R.id.logoutButton);

        // Setup RecyclerView
        setupRecyclerView();

        // Setup SearchView
        searchView.setOnQueryTextListener(this);

        // Setup logout button with confirmation dialog
        logoutButton.setOnClickListener(v -> {
            new MaterialAlertDialogBuilder(requireContext())
                .setTitle("Logout")
                .setMessage("Are you sure you want to logout?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    authHelper.logoutUser();
                })
                .setNegativeButton("No", null)
                .show();
        });

        // Observe chargepoints
        observeChargepoints();

        return view;
    }

    private void setupRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        adapter = new ChargepointAdapter();
        adapter.setReadOnly(true); // Set adapter to read-only mode
        recyclerView.setAdapter(adapter);
    }

    private void observeChargepoints() {
        viewModel.getAllChargepoints().observe(getViewLifecycleOwner(), chargepoints -> {
            adapter.setChargepoints(chargepoints);
        });
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        adapter.filter(query);
        return true;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        adapter.filter(newText);
        return true;
    }

    @Override
    public void onError(String error) {
        if (getActivity() != null) {
            Toast.makeText(getActivity(), error, Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onSuccess() {
        // After successful logout, navigate to login screen
        if (getActivity() != null) {
            Intent intent = new Intent(getActivity(), LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            getActivity().finish();
        }
    }
}
