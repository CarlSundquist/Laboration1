package se.liu.itn.kts.tnk115.laboration1;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.material.slider.RangeSlider;
import com.google.maps.android.heatmaps.HeatmapTileProvider;
import com.google.maps.android.heatmaps.WeightedLatLng;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback {

    private BroadcastReceiver receiver;
    private GoogleMap map;

    private GPSDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mapactivity);

        // Get a handle to the fragment and register the callback.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);

        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        db = Room.databaseBuilder(getApplicationContext(), GPSDatabase.class, "database-name")
                .allowMainThreadQueries()
                .build();


        Spinner modeSpinner = (Spinner) findViewById(R.id.mode_spinner);
        modeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                updateMap(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        configureSliders();
    }



    private void configureSliders()
    {
        // Setting hour slider.
        RangeSlider hourSlider = (RangeSlider) findViewById(R.id.time_bar);
        hourSlider.setValueFrom(0);
        hourSlider.setValueTo(24);
        hourSlider.setStepSize(1f);
        hourSlider.setMinSeparation(1f);
        hourSlider.setMinSeparationValue(1f);
        hourSlider.setValues(Arrays.asList(0f, 24f));

        // Setting day slider
        RangeSlider daySlider = (RangeSlider) findViewById(R.id.day_bar);
        // TODO: Set the sliders.
        daySlider.setValueFrom(0);
        daySlider.setValueTo(10);
        daySlider.setStepSize(1f);
        daySlider.setMinSeparation(1f);
        daySlider.setMinSeparationValue(1f);
        daySlider.setValues(Arrays.asList(0f, 10f));
    }
    protected void updateMap(int mode)
    {
        List<GPSData> data = db.userDao().getFilteredData();
        Log.d("MapActivity", "Database contains " + data.size() + " entries");
        for (GPSData gps : data) {
            Log.d("MapActivity", "Database Entry - Lat: " + gps.latitude + ", Lng: " + gps.longitude + ", Speed: " + gps.speed);
        }
        if(map != null){
            // Clearing map
            map.clear();

            if(mode == 0)
            {
                addPoints(data);
            }
            else if(mode == 1)
            {
                addLines(data);
            }
            else if (mode == 2)
            {
                addHeatmap(data);
            }
        }
    }

    private void addPoints(List<GPSData> data)
    {
        // TODO: Add points.
        Log.d("MapActivity", "Adding marker");
        if (data.isEmpty()){
            Log.d("MapActivity", "Empty data");
            return;
        }

        for (GPSData gps : data) {
            LatLng position = new LatLng(gps.latitude, gps.longitude);
            map.addMarker(new MarkerOptions()
                    .position(position)
                    .title("")
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
            Log.d("MapActivity", "Added marker at Lat: " + gps.latitude + ", Lng: " + gps.longitude);
        }
        if (!data.isEmpty()) {

            map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(data.get(0).latitude, data.get(0).longitude), 15));
        }
    }

    private void addLines(List<GPSData> data)
    {
        // TODO: Add points.
        Log.d("MapActivity", "Adding lines");
        if (data.isEmpty()){
            Log.d("MapActivity", "Empty data");
            return;
        }

        PolylineOptions polylineOptions = new PolylineOptions();
        LatLng start = null;
        LatLng end = null;

        for (GPSData gps : data) {
            LatLng position = new LatLng(gps.latitude, gps.longitude);
            polylineOptions.add(position);
            if (start == null) start = position;
            end = position;
        }

        Polyline polyline = map.addPolyline(polylineOptions);
        polyline.setColor(0xFFFF0000);

        if (start != null && end != null) {
            map.addMarker(new MarkerOptions().position(start).title("Start").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
            map.addMarker(new MarkerOptions().position(end).title("End").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
        }
    }

    private void addHeatmap(List<GPSData> data)
    {
        // TODO: Add points.
        List<WeightedLatLng> points = new ArrayList<>();
        for (GPSData gps : data) {
            points.add(new WeightedLatLng(new LatLng(gps.latitude, gps.longitude), gps.speed));
            Log.d("MapActivity", "Added heatmap point at Lat: " + gps.latitude + ", Lng: " + gps.longitude);
        }
        HeatmapTileProvider provider = new HeatmapTileProvider.Builder().weightedData(points).build();
        map.addTileOverlay(new com.google.android.gms.maps.model.TileOverlayOptions().tileProvider(provider));


    }

    private List<GPSData> getData()
    {
        RangeSlider hourSlider = (RangeSlider) findViewById(R.id.time_bar);
        RangeSlider daySlider = (RangeSlider) findViewById(R.id.day_bar);
        // TODO: Extract start and end values from sliders.
        int startHour = 0;
        int endHour = 0;
        int startday = 0;
        int endDay = 0;

        // TODO: Extract data based on filter.
        return new ArrayList<GPSData>();
    }
    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        Log.d("MapActivity", "Map ready");
        map = googleMap;
        updateMap(0);
    }
}