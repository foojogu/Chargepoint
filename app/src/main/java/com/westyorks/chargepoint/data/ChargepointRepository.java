package com.westyorks.chargepoint.data;

import android.app.Application;
import android.os.AsyncTask;
import androidx.lifecycle.LiveData;
import com.westyorks.chargepoint.model.Chargepoint;
import java.util.List;

public class ChargepointRepository {
    private ChargepointDao chargepointDao;
    private LiveData<List<Chargepoint>> allChargepoints;

    public ChargepointRepository(Application application) {
        AppDatabase database = AppDatabase.getInstance(application);
        chargepointDao = database.chargepointDao();
        allChargepoints = chargepointDao.getAllChargepoints();
    }

    public LiveData<List<Chargepoint>> getAllChargepoints() {
        return allChargepoints;
    }

    public LiveData<List<Chargepoint>> searchChargepoints(String query) {
        return chargepointDao.searchChargepoints("%" + query + "%");
    }

    public void insert(Chargepoint chargepoint) {
        new InsertChargepointAsyncTask(chargepointDao).execute(chargepoint);
    }

    public void update(Chargepoint chargepoint) {
        new UpdateChargepointAsyncTask(chargepointDao).execute(chargepoint);
    }

    public void delete(Chargepoint chargepoint) {
        new DeleteChargepointAsyncTask(chargepointDao).execute(chargepoint);
    }

    private static class InsertChargepointAsyncTask extends AsyncTask<Chargepoint, Void, Void> {
        private ChargepointDao chargepointDao;

        private InsertChargepointAsyncTask(ChargepointDao chargepointDao) {
            this.chargepointDao = chargepointDao;
        }

        @Override
        protected Void doInBackground(Chargepoint... chargepoints) {
            chargepointDao.insert(chargepoints[0]);
            return null;
        }
    }

    private static class UpdateChargepointAsyncTask extends AsyncTask<Chargepoint, Void, Void> {
        private ChargepointDao chargepointDao;

        private UpdateChargepointAsyncTask(ChargepointDao chargepointDao) {
            this.chargepointDao = chargepointDao;
        }

        @Override
        protected Void doInBackground(Chargepoint... chargepoints) {
            chargepointDao.update(chargepoints[0]);
            return null;
        }
    }

    private static class DeleteChargepointAsyncTask extends AsyncTask<Chargepoint, Void, Void> {
        private ChargepointDao chargepointDao;

        private DeleteChargepointAsyncTask(ChargepointDao chargepointDao) {
            this.chargepointDao = chargepointDao;
        }

        @Override
        protected Void doInBackground(Chargepoint... chargepoints) {
            chargepointDao.delete(chargepoints[0]);
            return null;
        }
    }
} 