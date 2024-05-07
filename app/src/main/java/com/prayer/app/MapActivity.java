package com.prayer.app;

import android.Manifest;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationManager;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationServices;

import org.osmdroid.config.Configuration;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;

public class MapActivity extends Activity {
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private MapView mapView;
    ImageView back, home ;
    private Button btnSaveLocation, btnDisableLocation;
    private GeofencingClient geofencingClient;
    private PendingIntent geofencePendingIntent;
    private GeoPoint selectedLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Configuration.getInstance().load(this, getPreferences(MODE_PRIVATE));
        setContentView(R.layout.activity_map);
        back = findViewById(R.id.imageBack);
        home=findViewById(R.id.home);
        geofencingClient = LocationServices.getGeofencingClient(this);
        mapView = findViewById(R.id.map);
        mapView.setTileSource(org.osmdroid.tileprovider.tilesource.TileSourceFactory.MAPNIK);
        mapView.setBuiltInZoomControls(true);
        mapView.setMultiTouchControls(true);
        Marker redDotMarker = new Marker(mapView);
        Drawable redDot = ContextCompat.getDrawable(this, R.drawable.red_dot); // Load red dot drawable
        redDotMarker.setIcon(redDot);
        mapView.getOverlays().add(redDotMarker);

        org.osmdroid.api.IMapController mapController = mapView.getController();
        mapController.setZoom(15.0);
        mapController.setCenter(new GeoPoint(24.7136, 46.6753)); // Riyadh

        btnSaveLocation = findViewById(R.id.btnSaveLocation);
        btnSaveLocation.setOnClickListener(v -> {
            if (selectedLocation != null) {
                saveLocation(selectedLocation);
                setUpGeofence();
            } else {
                Toast.makeText(MapActivity.this, "No location selected!", Toast.LENGTH_SHORT).show();
            }
        });

        btnDisableLocation = findViewById(R.id.btnRemoveLocation);
        btnDisableLocation.setOnClickListener(v -> disableGeofence());

        mapView.getOverlays().add(new Marker(mapView));
        mapView.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                selectedLocation = (GeoPoint) mapView.getProjection().fromPixels((int) event.getX(), (int) event.getY());
                redDotMarker.setPosition(selectedLocation);
                mapView.invalidate(); // Refresh the map to show the marker
                return true; // consume the event
            }
            return false; // not consumed here
        });
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goBack();
            }
        });
        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goHome();
            }
        });
    }

    private void goHome() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    private void goBack() {
        Intent intent = new Intent(this, Settings.class);
        startActivity(intent);
    }

    private void saveLocation(GeoPoint geoPoint) {
        SharedPreferences sharedPreferences = getSharedPreferences("GeoPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putFloat("Latitude", (float) geoPoint.getLatitude());
        editor.putFloat("Longitude", (float) geoPoint.getLongitude());
        editor.apply();
    }

    private void setUpGeofence() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
            return;
        }

        if (selectedLocation == null) {
            Toast.makeText(MapActivity.this, "No location selected!", Toast.LENGTH_SHORT).show();
            return;
        }

        Geofence geofence = new Geofence.Builder()
                .setRequestId("SilentZone")
                .setCircularRegion(selectedLocation.getLatitude(), selectedLocation.getLongitude(), 100)

                .setExpirationDuration(Geofence.NEVER_EXPIRE)
                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER)
                .build();

        GeofencingRequest geofencingRequest = new GeofencingRequest.Builder()
                .setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER)
                .addGeofence(geofence)
                .build();

        geofencingClient.addGeofences(geofencingRequest, getGeofencePendingIntent())
                .addOnSuccessListener(this, aVoid -> {
                    Toast.makeText(MapActivity.this, "Geofence added", Toast.LENGTH_SHORT).show();
                    if (isInSelectedLocation()) {
                        setSilentMode();
                    }
                })
                .addOnFailureListener(this, e -> Toast.makeText(MapActivity.this, "Failed to add geofence", Toast.LENGTH_SHORT).show());
    }

    private boolean isInSelectedLocation() {
        GeoPoint currentLocation = getCurrentLocation();
        if (currentLocation != null) {
            float[] results = new float[1];
            Location.distanceBetween(currentLocation.getLatitude(), currentLocation.getLongitude(),
                    selectedLocation.getLatitude(), selectedLocation.getLongitude(), results);
            float distanceInMeters = results[0];
            // Assuming a tolerance of 1000 meters
            return distanceInMeters <= 1000;
        }
        return false;
    }

    private GeoPoint getCurrentLocation() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return null;
        }

        Location lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        if (lastKnownLocation != null) {
            return new GeoPoint(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude());
        }
        return null;
    }

    private void disableGeofence() {
        geofencingClient.removeGeofences(getGeofencePendingIntent())
                .addOnSuccessListener(this, aVoid -> {
                    SharedPreferences sharedPreferences = getSharedPreferences("GeoPrefs", MODE_PRIVATE);
                    sharedPreferences.edit().clear().apply();  // Clear saved location
                    Toast.makeText(MapActivity.this, "Geofence disabled and location cleared", Toast.LENGTH_SHORT).show();
                    cancelSilentMode();
                })
                .addOnFailureListener(this, e -> Toast.makeText(MapActivity.this, "Failed to remove geofence", Toast.LENGTH_SHORT).show());
    }

    private PendingIntent getGeofencePendingIntent() {
        if (geofencePendingIntent != null) {
            return geofencePendingIntent;
        }
        Intent intent = new Intent(this, MapActivity.class);
        geofencePendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        return geofencePendingIntent;
    }

    private void setSilentMode() {
        AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        if (audioManager != null) {
            audioManager.setRingerMode(AudioManager.RINGER_MODE_SILENT);
            Toast.makeText(this, "Phone set to silent mode", Toast.LENGTH_SHORT).show();
        }
    }
    private void cancelSilentMode() {
        // Remove scheduled task
        new Handler().removeCallbacksAndMessages(null);
        // Revert phone's audio profile back to normal immediately
        AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        audioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);

        // Show toast message indicating cancellation
        Toast.makeText(this, "Silent mode schedule canceled", Toast.LENGTH_SHORT).show();
    }
    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }
}