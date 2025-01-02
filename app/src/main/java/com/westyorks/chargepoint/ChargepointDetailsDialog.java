package com.westyorks.chargepoint;

import android.app.Dialog;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import com.westyorks.chargepoint.model.Chargepoint;

public class ChargepointDetailsDialog extends DialogFragment {
    private static final String ARG_CHARGEPOINT = "chargepoint";

    public static ChargepointDetailsDialog newInstance(Chargepoint chargepoint) {
        ChargepointDetailsDialog fragment = new ChargepointDetailsDialog();
        Bundle args = new Bundle();
        args.putParcelable(ARG_CHARGEPOINT, (Parcelable) chargepoint);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Chargepoint chargepoint = getArguments().getParcelable(ARG_CHARGEPOINT);
        
        View view = LayoutInflater.from(getContext())
                .inflate(R.layout.dialog_chargepoint_details, null);
        
        // Set up views with chargepoint data
        // ... (we'll create this layout next)

        return new AlertDialog.Builder(requireContext())
                .setTitle(chargepoint.getName())
                .setView(view)
                .setPositiveButton(android.R.string.ok, null)
                .create();
    }
} 