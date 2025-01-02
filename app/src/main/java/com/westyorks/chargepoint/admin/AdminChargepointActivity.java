package com.westyorks.chargepoint.admin;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.westyorks.chargepoint.R;
import com.westyorks.chargepoint.model.Chargepoint;
import com.westyorks.chargepoint.util.CsvImporter;
import com.westyorks.chargepoint.viewmodel.ChargepointViewModel;
import java.io.IOException;
import java.util.List;

public class AdminChargepointActivity extends AppCompatActivity implements ChargepointDialog.OnSaveClickListener {
    private static final int PICK_CSV_FILE = 1;
    private ChargepointViewModel viewModel;
    private AdminChargepointAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_chargepoint);

        viewModel = new ViewModelProvider(this).get(ChargepointViewModel.class);

        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new AdminChargepointAdapter();
        recyclerView.setAdapter(adapter);

        FloatingActionButton fab = findViewById(R.id.fabAdd);
        fab.setOnClickListener(v -> showAddDialog());

        adapter.setOnItemClickListener(this::showEditDialog);

        viewModel.getAllChargepoints().observe(this, chargepoints -> {
            adapter.setChargepoints(chargepoints);
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_admin, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_import) {
            openCsvPicker();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void openCsvPicker() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("text/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(Intent.createChooser(intent, "Select CSV File"), PICK_CSV_FILE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_CSV_FILE && resultCode == RESULT_OK && data != null) {
            Uri uri = data.getData();
            if (uri != null) {
                importCsv(uri);
            }
        }
    }

    private void importCsv(Uri uri) {
        try {
            List<Chargepoint> chargepoints = CsvImporter.importFromUri(this, uri);
            for (Chargepoint chargepoint : chargepoints) {
                viewModel.insert(chargepoint);
            }
            Toast.makeText(this, String.format("Successfully imported %d chargepoints", chargepoints.size()), 
                Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            Toast.makeText(this, "Error importing CSV: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void showAddDialog() {
        ChargepointDialog dialog = ChargepointDialog.newInstance(null);
        dialog.setOnSaveClickListener(this);
        dialog.show(getSupportFragmentManager(), "add_chargepoint");
    }

    private void showEditDialog(Chargepoint chargepoint) {
        ChargepointDialog dialog = ChargepointDialog.newInstance(chargepoint);
        dialog.setOnSaveClickListener(this);
        dialog.show(getSupportFragmentManager(), "edit_chargepoint");
    }

    @Override
    public void onSave(Chargepoint chargepoint) {
        if (chargepoint.getReferenceId() == null) {
            viewModel.insert(chargepoint);
        } else {
            viewModel.update(chargepoint);
        }
    }
}