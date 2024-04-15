package com.prayer.app;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;

import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import android.os.Bundle;
import android.os.Looper;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.Manifest;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.preference.PreferenceManager;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

public class ManageLocation extends AppCompatActivity {
    private static  final int REQUEST_LOCATION = 1;
    ImageView back ;
    ImageView home ;
    Button btnLocation;
    TextView tvLatitude, tvLongitude;
    FusedLocationProviderClient fusedLocationProviderClient;
    LocationRequest locationRequest;
    SharedPreferences prefs;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.manage_location);
        back = findViewById(R.id.imageBack);
        home = findViewById(R.id.home);
        btnLocation = findViewById(R.id.btnGetLocation);
        tvLatitude = findViewById(R.id.tv_latitude);
        tvLongitude = findViewById(R.id.tv_longitude);
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(
                ManageLocation.this
        );
        prefs = PreferenceManager.getDefaultSharedPreferences(ManageLocation.this);
        tvLatitude.setText(String.valueOf(Double.parseDouble(prefs.getString("LATITUDE", "0.0"))));
        tvLongitude.setText(String.valueOf(Double.parseDouble(prefs.getString("LONGITUDE", "0.0"))));

        locationRequest = LocationRequest.create();
        locationRequest.setInterval(10000); // Update interval in milliseconds
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        btnLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ActivityCompat.checkSelfPermission(ManageLocation.this,
                        Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(ManageLocation.this,
                        Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){
                    Toast.makeText(ManageLocation.this, "req per", Toast.LENGTH_SHORT).show();
                    ActivityCompat.requestPermissions(ManageLocation.this, new String[]
                            {Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);
                }
                else{
                    getLocation();
                }
            }
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
                gotoHome();
            }
        });

    }
    @SuppressLint("MissingPermission")
    private void getLocation(){
        Toast.makeText(this, "second", Toast.LENGTH_SHORT).show();
        fusedLocationProviderClient.requestLocationUpdates(locationRequest, new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult != null) {
                    Location location = locationResult.getLastLocation();
                    if (location != null) {
                        // Update UI elements with the new location data
                        double latitude = location.getLatitude();
                        double longitude = location.getLongitude();
                        tvLatitude.setText(String.valueOf(latitude));
                        tvLongitude.setText(String.valueOf(longitude));
                        SharedPreferences.Editor editor = prefs.edit();
                        editor.putString("LATITUDE", String.valueOf(location.getLatitude()));
                        editor.putString("LONGITUDE", String.valueOf(location.getLongitude()));
                        editor.apply();


                    }
                }
            }
        }, Looper.getMainLooper()); // Use Looper.getMainLooper() to receive updates on the main thread
    }
    // Handle the result of the permission request
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_LOCATION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, get the location
                getLocation();
            } else {
                Toast.makeText(this, "Location permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void gotoHome() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    private void goBack() {
        Intent intent = new Intent(this, Settings.class);
        startActivity(intent);
    }
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == android.R.id.home)
            finish();
        return super.onOptionsItemSelected(item);
    }
}