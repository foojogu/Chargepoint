package com.westyorks.chargepoint.dialog;

import android.app.Dialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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

    public static ChargepointDialog newInstance(@Nullable Chargepoint chargepoint) {
        ChargepointDialog dialog = new ChargepointDialog();
        Bundle args = new Bundle();
        args.putParcelable(ARG_CHARGEPOINT, chargepoint);
        dialog.setArguments(args);
        return dialog;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.dialog_chargepoint, null);
        initializeViews(view);

        Chargepoint chargepoint = null;
        if (getArguments() != null) {
            chargepoint = getArguments().getParcelable(ARG_CHARGEPOINT);
        }
        if (chargepoint != null) {
            populateFields(chargepoint);
        }

        return new AlertDialog.Builder(requireContext())
            .setTitle(chargepoint == null ? "Add Chargepoint" : "Edit Chargepoint")
            .setView(view)
            .setPositiveButton("Save", (dialog, which) -> {
                if (validateInput()) {
                    saveChargepoint();
                }
            })
            .setNegativeButton("Cancel", null)
            .create();
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
        boolean isValid = true;

        // Required fields
        if (TextUtils.isEmpty(etReferenceId.getText())) {
            etReferenceId.setError("Reference ID is required");
            isValid = false;
        }

        if (TextUtils.isEmpty(etName.getText())) {
            etName.setError("Name is required");
            isValid = false;
        }

        if (TextUtils.isEmpty(etTown.getText())) {
            etTown.setError("Town is required");
            isValid = false;
        }

        if (TextUtils.isEmpty(etCounty.getText())) {
            etCounty.setError("County is required");
            isValid = false;
        }

        if (TextUtils.isEmpty(etPostcode.getText())) {
            etPostcode.setError("Postcode is required");
            isValid = false;
        }

        // Validate coordinates
        try {
            Double.parseDouble(etLatitude.getText().toString());
        } catch (NumberFormatException e) {
            etLatitude.setError("Invalid latitude");
            isValid = false;
        }

        try {
            Double.parseDouble(etLongitude.getText().toString());
        } catch (NumberFormatException e) {
            etLongitude.setError("Invalid longitude");
            isValid = false;
        }

        return isValid;
    }

    private void saveChargepoint() {
        Chargepoint chargepoint = new Chargepoint();
        chargepoint.setReferenceId(etReferenceId.getText().toString().trim());
        chargepoint.setName(etName.getText().toString().trim());
        chargepoint.setTown(etTown.getText().toString().trim());
        chargepoint.setCounty(etCounty.getText().toString().trim());
        chargepoint.setPostcode(etPostcode.getText().toString().trim());
        chargepoint.setChargerType(etChargerType.getText().toString().trim());
        chargepoint.setChargerStatus(etChargerStatus.getText().toString().trim());
        chargepoint.setConnectorId(etConnectorId.getText().toString().trim());
        chargepoint.setLatitude(Double.parseDouble(etLatitude.getText().toString().trim()));
        chargepoint.setLongitude(Double.parseDouble(etLongitude.getText().toString().trim()));

        if (listener != null) {
            listener.onSave(chargepoint);
        }
    }

    public interface OnSaveClickListener {
        void onSave(Chargepoint chargepoint);
    }

    public void setOnSaveClickListener(OnSaveClickListener listener) {
        this.listener = listener;
    }
}
