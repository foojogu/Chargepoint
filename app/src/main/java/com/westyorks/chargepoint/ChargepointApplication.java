package com.westyorks.chargepoint;

import android.app.Application;
import com.google.firebase.database.FirebaseDatabase;
import com.westyorks.chargepoint.data.AppDatabase;
import com.westyorks.chargepoint.data.DatabaseInitializer;

public class ChargepointApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        
        // Initialize Firebase persistence
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        
        // Initialize local database
        AppDatabase db = AppDatabase.getInstance(this);
        DatabaseInitializer.initializeDb(this, db);
    }
}