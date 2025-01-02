package com.westyorks.chargepoint.admin;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import com.westyorks.chargepoint.R;
import com.westyorks.chargepoint.model.Chargepoint;

public class ChargepointDialog extends DialogFragment {
    private static final String ARG_CHARGEPOINT = "chargepoint";
    
    private EditText etReferenceId;
    private EditText etName;
    private EditText etTown;
    private EditText etCounty;
    private EditText etPostcode;
    private EditText etChargerType;
    private EditText etChargerStatus;
    private EditText etConnectorId;
    private EditText etLatitude;
    private EditText etLongitude;
    private OnSaveClickListener listener;

    public static ChargepointDialog newInstance(Chargepoint chargepoint) {
        ChargepointDialog fragment = new ChargepointDialog();
        Bundle args = new Bundle();
        args.putParcelable(ARG_CHARGEPOINT, chargepoint);
        fragment.setArguments(args);
        return fragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
        View view = getLayoutInflater().inflate(R.layout.dialog_chargepoint, null);
        
        initializeViews(view);
        setupListeners();

        Chargepoint chargepoint = null;
        if (getArguments() != null) {
            chargepoint = getArguments().getParcelable(ARG_CHARGEPOINT);
        }
        if (chargepoint != null) {
            populateFields(chargepoint);
        }

        builder.setView(view)
               .setTitle(chargepoint == null ? "Add Chargepoint" : "Edit Chargepoint");

        return builder.create();
    }

    private void initializeViews(View view) {
        etReferenceId = view.findViewById(R.id.etReferenceId);
        etName = view.findViewById(R.id.etName);
        etTown = view.findViewById(R.id.etTown);
        etCounty = view.findViewById(R.id.etCounty);
        etPostcode = view.findViewById(R.id.etPostcode);
        etChargerType = view.findViewById(R.id.etChargerType);
        etChargerStatus = view.findViewById(R.id.etChargerStatus);
        etConnectorId = view.findViewById(R.id.etConnectorId);
        etLatitude = view.findViewById(R.id.etLatitude);
        etLongitude = view.findViewById(R.id.etLongitude);
    }

    private void setupListeners() {
        Button btnSave = requireView().findViewById(R.id.btnSave);
        Button btnCancel = requireView().findViewById(R.id.btnCancel);

        btnSave.setOnClickListener(v -> {
            if (validateInput()) {
                saveChargepoint();
            }
        });

        btnCancel.setOnClickListener(v -> dismiss());
    }

    private void populateFields(Chargepoint chargepoint) {
        etReferenceId.setText(chargepoint.getReferenceId());
        etName.setText(chargepoint.getName());
        etTown.setText(chargepoint.getTown());
        etCounty.setText(chargepoint.getCounty());
        etPostcode.setText(chargepoint.getPostcode());
        etChargerType.setText(chargepoint.getChargerType());
        etChargerStatus.setText(chargepoint.getChargerStatus());
        etConnectorId.setText(chargepoint.getConnectorId());
        etLatitude.setText(String.valueOf(chargepoint.getLatitude()));
        etLongitude.setText(String.valueOf(chargepoint.getLongitude()));
    }

    private boolean validateInput() {
        if (etReferenceId.getText().toString().trim().isEmpty()) {
            etReferenceId.setError("Reference ID is required");
            return false;
        }
        
        try {
            Double.parseDouble(etLatitude.getText().toString().trim());
            Double.parseDouble(etLongitude.getText().toString().trim());
        } catch (NumberFormatException e) {
            etLatitude.setError("Invalid latitude");
            etLongitude.setError("Invalid longitude");
            return false;
        }
        
        return true;
    }

    private void saveChargepoint() {
        Chargepoint chargepoint = getArguments() != null ? 
            getArguments().getParcelable(ARG_CHARGEPOINT) : new Chargepoint();

        if (chargepoint == null) {
            chargepoint = new Chargepoint();
        }

        chargepoint.setReferenceId(etReferenceId.getText().toString().trim());
        chargepoint.setName(etName.getText().toString().trim());
        chargepoint.setTown(etTown.getText().toString().trim());
        chargepoint.setCounty(etCounty.getText().toString().trim());
        chargepoint.setPostcode(etPostcode.getText().toString().trim());
        chargepoint.setChargerType(etChargerType.getText().toString().trim());
        chargepoint.setChargerStatus(etChargerStatus.getText().toString().trim());
        chargepoint.setConnectorId(etConnectorId.getText().toString().trim());
        
        try {
            chargepoint.setLatitude(Double.parseDouble(etLatitude.getText().toString().trim()));
            chargepoint.setLongitude(Double.parseDouble(etLongitude.getText().toString().trim()));
        } catch (NumberFormatException e) {
            etLatitude.setError("Invalid latitude");
            etLongitude.setError("Invalid longitude");
            return;
        }

        if (listener != null) {
            listener.onSave(chargepoint);
        }
        dismiss();
    }

    public void setOnSaveClickListener(OnSaveClickListener listener) {
        this.listener = listener;
    }

    public interface OnSaveClickListener {
        void onSave(Chargepoint chargepoint);
    }
}