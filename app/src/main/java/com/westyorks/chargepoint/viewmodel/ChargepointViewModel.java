package com.westyorks.chargepoint.viewmodel;

import android.app.Application;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import com.westyorks.chargepoint.data.AppDatabase;
import com.westyorks.chargepoint.data.ChargepointDao;
import com.westyorks.chargepoint.model.Chargepoint;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ChargepointViewModel extends AndroidViewModel {
    private ChargepointDao chargepointDao;
    private LiveData<List<Chargepoint>> allChargepoints;
    private final ExecutorService executorService;

    public ChargepointViewModel(Application application) {
        super(application);
        AppDatabase database = AppDatabase.getInstance(application);
        chargepointDao = database.chargepointDao();
        allChargepoints = chargepointDao.getAllChargepoints();
        executorService = Executors.newSingleThreadExecutor();
    }

    public LiveData<List<Chargepoint>> getAllChargepoints() {
        return allChargepoints;
    }

    public void insert(Chargepoint chargepoint) {
        executorService.execute(() -> {
            chargepointDao.insert(chargepoint);
        });
    }

    public void insertAll(List<Chargepoint> chargepoints) {
        executorService.execute(() -> {
            chargepointDao.insertAll(chargepoints);
        });
    }

    public void update(Chargepoint chargepoint) {
        executorService.execute(() -> {
            chargepointDao.update(chargepoint);
        });
    }

    public void delete(Chargepoint chargepoint) {
        executorService.execute(() -> {
            chargepointDao.delete(chargepoint);
        });
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        executorService.shutdown();
    }
}