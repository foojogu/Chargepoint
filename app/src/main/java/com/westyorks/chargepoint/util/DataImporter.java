package com.westyorks.chargepoint.util;

import android.content.Context;
import com.westyorks.chargepoint.model.Chargepoint;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class DataImporter {
    private static final String CSV_DELIMITER = ",";
    private static final int EXPECTED_COLUMNS = 9;

    public static List<Chargepoint> importInitialData(Context context, String filename) {
        List<Chargepoint> chargepoints = new ArrayList<>();

        try {
            InputStream inputStream = context.getAssets().open(filename);
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            
            // Skip header line
            String headerLine = reader.readLine();
            if (headerLine == null) {
                throw new IOException("Data file is empty");
            }

            String line;
            int lineNumber = 1;
            while ((line = reader.readLine()) != null) {
                lineNumber++;
                try {
                    String[] values = line.split(CSV_DELIMITER);
                    if (values.length < EXPECTED_COLUMNS) {
                        throw new IOException(String.format("Line %d: Expected %d columns but found %d", 
                            lineNumber, EXPECTED_COLUMNS, values.length));
                    }

                    Chargepoint chargepoint = new Chargepoint();
                    
                    // referenceID,latitude,longitude,town,county,postcode,chargeDeviceStatus,connectorID,connectorType
                    chargepoint.setReferenceId(values[0].trim());
                    try {
                        chargepoint.setLatitude(Double.parseDouble(values[1].trim()));
                        chargepoint.setLongitude(Double.parseDouble(values[2].trim()));
                    } catch (NumberFormatException e) {
                        throw new IOException(String.format("Line %d: Invalid coordinates - %s", 
                            lineNumber, e.getMessage()));
                    }
                    
                    chargepoint.setTown(values[3].trim());
                    chargepoint.setCounty(values[4].trim());
                    chargepoint.setPostcode(values[5].trim());
                    chargepoint.setChargerStatus(values[6].trim());
                    chargepoint.setConnectorId(values[7].trim());
                    chargepoint.setChargerType(values[8].trim());
                    
                    // Set a default name using town and reference
                    chargepoint.setName(String.format("%s - %s", values[3].trim(), values[0].trim()));

                    chargepoints.add(chargepoint);
                } catch (Exception e) {
                    throw new IOException(String.format("Error parsing line %d: %s", 
                        lineNumber, e.getMessage()));
                }
            }

            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return chargepoints;
    }
}