package se.liu.itn.kts.tnk115.laboration1;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.room.Insert;
import androidx.room.Room;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.Collections;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private boolean collecting = false;
    private FusedLocationProviderClient fusedLocationClient;
    private LocationCallback locationCallback;

    private GPSDatabase db;
    //GPSDataDao userDao = db.userDao();
    private GoogleMap map;






    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        // Get a FusedLocationProviderClient.
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        db = Room.databaseBuilder(getApplicationContext(), GPSDatabase.class, "database-name").allowMainThreadQueries().build();
        // Update the GUI with the last known location.
        //getLastKnownLocation();
    }


    public void toggleGps(View view) {

        Button button = (Button) findViewById(R.id.gps_buttonv21);

        if (collecting) {
            Log.d("MainActivity", "GPS button clicked, stop collecting GPS data.");
            button.setText(R.string.start_gps);
            stopCollecting();
        } else {
            Log.d("MainActivity", "GPS button clicked, start collecting GPS data.");
            button.setText(R.string.stop_gps);
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
                return;
            }
            createLocationRequest();
            startCollecting();
        }

        collecting = !collecting;
    }

    public void startMapActivity(View view){
        Log.d("MainActivity", "Starting MapActivity.");
        Intent intent = new Intent(MainActivity.this, MapActivity.class);
        startActivity(intent);
    }
    protected void getLastKnownLocation() {

        // TODO: Get last known location.
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        // Got last known location. In some rare situations this can be null.
                        if (location != null) {
                            Log.d("MainActivity", "Updating GUI with last known location.");
                            updateValues(location);
                            saveLocation(location);
                        }
                    }
                });
    }

    protected LocationRequest createLocationRequest() {
        Log.d("MainActivity", "Creating LocationRequest");
        // TODO: Create location request.
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setInterval(30 * 1000);
        locationRequest.setFastestInterval(5000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        return locationRequest;
    }

    protected void saveLocation(Location location){

        // TODO: Save into Room database.

        Log.d("MainActivity", "Saving location to database.");


        GPSData data = new GPSData();
        data.latitude = location.getLatitude();
        data.longitude = location.getLongitude();
        data.heading = location.getBearing();
        data.speed = location.getSpeed();
        data.accuracy = location.getAccuracy();

        //data.timestamp = System.currentTimeMillis();
        db.userDao().insertAll(data);
        Log.d("MainActivity", "Saved location to database. Size: " + db.userDao().getFilteredData().size());


    }
    protected void updateValues(Location location) {
        TextView latitude = (TextView) findViewById(R.id.latitude_value);
        TextView longitude = (TextView) findViewById(R.id.longitude_value);
        TextView heading = (TextView) findViewById(R.id.heading_value);
        TextView speed = (TextView) findViewById(R.id.speed_value);
        TextView accuracy = (TextView) findViewById(R.id.accuracy_value);

        // TODO: Update the GUI with location data.
        latitude.setText(String.format("%.7f", location.getLatitude()));
        longitude.setText(String.format("%.7f", location.getLongitude()));
        heading.setText(String.format("%.2f\u00B0", location.getBearing()));
        speed.setText(String.format("%.2f m/s", location.getSpeed()));
        accuracy.setText(String.format("%.2f m", location.getAccuracy()));
    }
    


    protected void startCollecting() {

        Log.d("MainActivity", "Starting to request location updates.");
        // TODO: start requesting location updates.
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    return;
                }
                for (Location location : locationResult.getLocations()) {
                    Log.d("MainActivity", "Updating GUI with current location.");
                    updateValues(location);
                    saveLocation(location);
                }
            }
        };

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }

        fusedLocationClient.requestLocationUpdates(createLocationRequest(), locationCallback, Looper.myLooper());
    }

    protected void stopCollecting(){

        Log.d("MainActivity", "Stopping location updates.");
        // TODO: Stop requesting location updates.
        fusedLocationClient.removeLocationUpdates(locationCallback);
    }
}