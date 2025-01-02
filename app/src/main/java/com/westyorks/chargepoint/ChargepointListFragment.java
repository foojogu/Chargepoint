package com.westyorks.chargepoint;

import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.westyorks.chargepoint.adapter.ChargepointAdapter;
import com.westyorks.chargepoint.auth.FirebaseAuthHelper;
import com.westyorks.chargepoint.dialog.ChargepointDialog;
import com.westyorks.chargepoint.model.Chargepoint;
import com.westyorks.chargepoint.util.CsvImporter;
import com.westyorks.chargepoint.viewmodel.ChargepointViewModel;
import java.io.IOException;
import java.util.List;

public class ChargepointListFragment extends Fragment implements SearchView.OnQueryTextListener, FirebaseAuthHelper.AuthCallback {
    private ChargepointViewModel viewModel;
    private ChargepointAdapter adapter;
    private ActivityResultLauncher<Intent> csvImportLauncher;
    private androidx.appcompat.widget.SearchView searchView;
    private RecyclerView recyclerView;
    private MaterialButton importButton;
    private MaterialButton logoutButton;
    private FloatingActionButton fabAdd;
    private FirebaseAuthHelper authHelper;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity()).get(ChargepointViewModel.class);
        authHelper = new FirebaseAuthHelper(this);
        
        csvImportLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getData() != null) {
                    importCSV(result.getData().getData());
                }
            }
        );
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chargepoint_list, container, false);

        // Initialize views
        recyclerView = view.findViewById(R.id.recyclerView);
        searchView = view.findViewById(R.id.searchView);
        importButton = view.findViewById(R.id.importButton);
        logoutButton = view.findViewById(R.id.logoutButton);
        fabAdd = view.findViewById(R.id.fabAdd);

        // Setup RecyclerView
        setupRecyclerView();

        // Setup SearchView
        searchView.setOnQueryTextListener(this);

        // Setup buttons
        importButton.setOnClickListener(v -> openCSVPicker());
        fabAdd.setOnClickListener(v -> showAddDialog());
        logoutButton.setOnClickListener(v -> {
            new AlertDialog.Builder(requireContext())
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
        recyclerView.setAdapter(adapter);

        adapter.setOnItemClickListener(this::showEditDialog);
        adapter.setOnItemLongClickListener(this::showDeleteDialog);
    }

    private void observeChargepoints() {
        viewModel.getAllChargepoints().observe(getViewLifecycleOwner(), chargepoints -> {
            adapter.setChargepoints(chargepoints);
        });
    }

    private void showAddDialog() {
        ChargepointDialog dialog = ChargepointDialog.newInstance(null);
        dialog.setOnSaveClickListener(chargepoint -> {
            viewModel.insert(chargepoint);
            Toast.makeText(requireContext(), "Chargepoint added successfully", Toast.LENGTH_SHORT).show();
        });
        dialog.show(getChildFragmentManager(), "add_chargepoint");
    }

    private void showEditDialog(Chargepoint chargepoint) {
        ChargepointDialog dialog = ChargepointDialog.newInstance(chargepoint);
        dialog.setOnSaveClickListener(updatedChargepoint -> {
            viewModel.update(updatedChargepoint);
            Toast.makeText(requireContext(), "Chargepoint updated successfully", Toast.LENGTH_SHORT).show();
        });
        dialog.show(getChildFragmentManager(), "edit_chargepoint");
    }

    private void showDeleteDialog(Chargepoint chargepoint) {
        new AlertDialog.Builder(requireContext())
            .setTitle("Delete Chargepoint")
            .setMessage("Are you sure you want to delete this chargepoint?")
            .setPositiveButton("Delete", (dialog, which) -> {
                viewModel.delete(chargepoint);
                Toast.makeText(requireContext(), "Chargepoint deleted successfully", Toast.LENGTH_SHORT).show();
            })
            .setNegativeButton("Cancel", null)
            .show();
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

    private void openCSVPicker() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("text/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        csvImportLauncher.launch(Intent.createChooser(intent, "Select CSV File"));
    }

    private void importCSV(Uri uri) {
        if (uri == null) return;
        
        try {
            List<Chargepoint> chargepoints = CsvImporter.importFromUri(requireContext(), uri);
            for (Chargepoint chargepoint : chargepoints) {
                viewModel.insert(chargepoint);
            }
            Toast.makeText(requireContext(), 
                String.format("Successfully imported %d chargepoints", chargepoints.size()), 
                Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            Toast.makeText(requireContext(), "Error importing CSV: " + e.getMessage(), 
                Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onSuccess() {
        // Navigate back to MainActivity after successful logout
        startActivity(new Intent(requireContext(), MainActivity.class));
        requireActivity().finish();
    }

    @Override
    public void onError(String error) {
        Toast.makeText(requireContext(), "Logout error: " + error, Toast.LENGTH_SHORT).show();
    }
}