package com.westyorks.chargepoint.util;

import android.net.Uri;
import android.content.Context;
import com.westyorks.chargepoint.model.Chargepoint;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class CSVParser {
    public static List<Chargepoint> parseCSV(Context context, Uri uri) throws IOException {
        List<Chargepoint> chargepoints = new ArrayList<>();
        
        try (InputStream inputStream = context.getContentResolver().openInputStream(uri);
             BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
            
            // Skip header line
            String headerLine = reader.readLine();
            if (headerLine == null) return chargepoints;
            
            String line;
            while ((line = reader.readLine()) != null) {
                try {
                    String[] values = line.split(",");
                    if (values.length >= 9) {
                        Chargepoint chargepoint = new Chargepoint();
                        chargepoint.setReferenceId(values[0].trim());
                        chargepoint.setLatitude(Double.parseDouble(values[1].trim()));
                        chargepoint.setLongitude(Double.parseDouble(values[2].trim()));
                        chargepoint.setTown(values[3].trim());
                        chargepoint.setCounty(values[4].trim());
                        chargepoint.setPostcode(values[5].trim());
                        chargepoint.setChargerStatus(values[6].trim());
                        chargepoint.setConnectorId(values[7].trim());
                        chargepoint.setChargerType(values[8].trim());
                        
                        // Set a default name using town and reference
                        chargepoint.setName(String.format("%s - %s", values[3].trim(), values[0].trim()));
                        
                        chargepoints.add(chargepoint);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    // Continue parsing other lines if one fails
                }
            }
        }
        
        return chargepoints;
    }
}
