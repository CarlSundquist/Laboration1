package se.liu.itn.kts.tnk115.laboration1;

import java.util.List;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

@Dao
public interface GPSDataDao {
    @Query("SELECT * FROM gpsdata")
    List<GPSData> getFilteredData();
    @Insert
    void insertAll(GPSData... gpsData);
    @Delete
    void delete(GPSData gpsData);

}
