package se.liu.itn.kts.tnk115.laboration1;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {GPSData.class}, version = 1)
public abstract class GPSDatabase extends RoomDatabase {
    public abstract GPSDataDao userDao();
}