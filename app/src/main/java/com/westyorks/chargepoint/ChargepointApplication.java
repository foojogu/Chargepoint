package com.westyorks.chargepoint;

import android.app.Application;
import com.westyorks.chargepoint.data.AppDatabase;
import com.westyorks.chargepoint.data.DatabaseInitializer;

public class ChargepointApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        AppDatabase db = AppDatabase.getInstance(this);
        DatabaseInitializer.initializeDb(this, db);
    }
} 