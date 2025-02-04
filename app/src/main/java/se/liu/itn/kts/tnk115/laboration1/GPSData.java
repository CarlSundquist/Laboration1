package se.liu.itn.kts.tnk115.laboration1;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class GPSData {
    @PrimaryKey(autoGenerate = true)
    public int uid;
    public double latitude;
    public double longitude;
    public double heading;
    public double speed;
    public double accuracy;

    //public long timestamp;

}
