package com.westyorks.chargepoint;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;
import com.westyorks.chargepoint.model.Chargepoint;

public class ChargepointInfoWindowAdapter implements GoogleMap.InfoWindowAdapter {
    private final View window;
    private final Context context;

    public ChargepointInfoWindowAdapter(Context context) {
        this.context = context;
        window = LayoutInflater.from(context).inflate(R.layout.custom_info_window, null);
    }

    private void renderWindow(Marker marker, View view) {
        Chargepoint chargepoint = (Chargepoint) marker.getTag();
        if (chargepoint == null) return;

        TextView tvName = view.findViewById(R.id.tvName);
        TextView tvAddress = view.findViewById(R.id.tvAddress);
        TextView tvChargerType = view.findViewById(R.id.tvChargerType);
        TextView tvStatus = view.findViewById(R.id.tvStatus);

        tvName.setText(chargepoint.getName());
        
        // Combine town, county, and postcode for the address
        String address = String.format("%s, %s, %s", 
            chargepoint.getTown(), 
            chargepoint.getCounty(), 
            chargepoint.getPostcode());
        tvAddress.setText(address);
        
        tvChargerType.setText(String.format("Type: %s", chargepoint.getChargerType()));
        tvStatus.setText(String.format("Status: %s", chargepoint.getChargerStatus()));
    }

    @Override
    public View getInfoWindow(Marker marker) {
        renderWindow(marker, window);
        return window;
    }

    @Override
    public View getInfoContents(Marker marker) {
        renderWindow(marker, window);
        return window;
    }
} 