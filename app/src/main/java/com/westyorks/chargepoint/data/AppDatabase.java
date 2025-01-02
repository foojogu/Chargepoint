package com.westyorks.chargepoint.data;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;
import com.westyorks.chargepoint.model.Chargepoint;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(entities = {Chargepoint.class}, version = 2, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {
    private static volatile AppDatabase instance;
    private static final ExecutorService databaseExecutor = Executors.newSingleThreadExecutor();

    public abstract ChargepointDao chargepointDao();

    public static AppDatabase getInstance(Context context) {
        if (instance == null) {
            synchronized (AppDatabase.class) {
                if (instance == null) {
                    instance = Room.databaseBuilder(context.getApplicationContext(),
                            AppDatabase.class, "chargepoint_database")
                            .fallbackToDestructiveMigration()
                            .addCallback(new RoomDatabase.Callback() {
                                @Override
                                public void onCreate(@NonNull SupportSQLiteDatabase db) {
                                    super.onCreate(db);
                                    databaseExecutor.execute(() -> {
                                        // Initialize database in background
                                        DatabaseInitializer.initializeDb(context.getApplicationContext(), instance);
                                    });
                                }
                            })
                            .build();
                }
            }
        }
        return instance;
    }
}