package com.westyorks.chargepoint.data;

import androidx.lifecycle.LiveData;
import androidx.room.*;
import com.westyorks.chargepoint.model.Chargepoint;
import java.util.List;

@Dao
public interface ChargepointDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Chargepoint chargepoint);
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<Chargepoint> chargepoints);
    
    @Update
    void update(Chargepoint chargepoint);
    
    @Delete
    void delete(Chargepoint chargepoint);
    
    @Query("SELECT * FROM chargepoints")
    LiveData<List<Chargepoint>> getAllChargepoints();
    
    @Query("SELECT * FROM chargepoints WHERE county LIKE :search OR chargerType LIKE :search")
    LiveData<List<Chargepoint>> searchChargepoints(String search);
} 