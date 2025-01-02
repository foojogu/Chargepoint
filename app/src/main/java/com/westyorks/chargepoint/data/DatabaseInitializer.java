package com.westyorks.chargepoint.data;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import com.westyorks.chargepoint.model.Chargepoint;
import com.westyorks.chargepoint.util.DataImporter;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DatabaseInitializer {
    private static final String TAG = "DatabaseInitializer";
    private static final String PREF_NAME = "DatabasePrefs";
    private static final String PREF_DB_INITIALIZED = "db_initialized";
    private static final String INITIAL_DATA_FILE = "chargepoints.csv";
    private static final ExecutorService executorService = Executors.newSingleThreadExecutor();

    public static void initializeDb(Context context, AppDatabase db) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        boolean isInitialized = prefs.getBoolean(PREF_DB_INITIALIZED, false);

        if (!isInitialized) {
            executorService.execute(() -> {
                try {
                    List<Chargepoint> chargepoints = DataImporter.importInitialData(context, INITIAL_DATA_FILE);
                    if (!chargepoints.isEmpty()) {
                        db.chargepointDao().insertAll(chargepoints);
                        prefs.edit().putBoolean(PREF_DB_INITIALIZED, true).apply();
                        Log.i(TAG, String.format("Initialized database with %d chargepoints", chargepoints.size()));
                    } else {
                        Log.w(TAG, "No chargepoints found in initial data file");
                    }
                } catch (Exception e) {
                    Log.e(TAG, "Error initializing database: " + e.getMessage());
                }
            });
        }
    }
}