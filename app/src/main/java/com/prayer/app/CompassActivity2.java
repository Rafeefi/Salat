package com.prayer.app;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.Image;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.hardware.GeomagneticField;

import androidx.core.app.ActivityCompat;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.widget.TextView;

public class CompassActivity2 extends Activity implements SensorEventListener, LocationListener {

    private ImageView compassImage;
    private float currentDegree = 0f;
    private SensorManager sensorManager;
    private LocationManager locationManager;
    private Paint angleLinePaint;
    private float kaabaBearing;
    private PrefManager pref;
    private Location userLocation;
    private float currentDegreeNeedle = 0f;
    private ImageView qibla_arrow;
    private TextView tvHeading;
    ImageView back ;
    ImageView settings ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.compass_activity_2);
        compassImage = findViewById(R.id.imageCompass);
        qibla_arrow = (ImageView) findViewById(R.id.qibla_arrow);
        tvHeading = (TextView) findViewById(R.id.heading);
        pref = new PrefManager(CompassActivity2.this);
        settings  = findViewById(R.id.imageSettings);
        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToSettings();
            }
        });

        back = findViewById(R.id.imageBack);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goBack();
            }
        });



        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
    }

    private void goBack() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    private void goToSettings() {
        Intent intent = new Intent(this, Settings.class);
        startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Register sensor listeners
        sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION),
                SensorManager.SENSOR_DELAY_GAME);
        // Request location updates
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 10, this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Unregister sensor listeners
        sensorManager.unregisterListener(this);
        // Remove location updates
        locationManager.removeUpdates(this);
    }
    float bearingToKaaba(){
        Location kaabaLocation = new Location("");
        kaabaLocation.setLatitude(21.4225);
        kaabaLocation.setLongitude(39.8262);
        userLocation = new Location("");
        userLocation.setLatitude(pref.getLatitude());
        userLocation.setLongitude(pref.getLongtiude());
        kaabaBearing = userLocation.bearingTo(kaabaLocation);
        return kaabaBearing;
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {

        float degree = Math.round(sensorEvent.values[0]);
        float head = Math.round(sensorEvent.values[0]);
        float bearTo =  bearingToKaaba();
        GeomagneticField geomagneticField  = new GeomagneticField( Double.valueOf( pref.getLatitude() ).floatValue(), Double
                .valueOf( pref.getLongtiude() ).floatValue(),
                Double.valueOf( userLocation.getAltitude() ).floatValue(),
                System.currentTimeMillis() );
        head = head - geomagneticField .getDeclination(); // converts magnetic north into true north

        if (bearTo < 0) {
            bearTo = bearTo + 360;
        }

        float direction = bearTo - head;

        // If the direction is smaller than 0, add 360 to get the rotation clockwise.
        if (direction < 0) {
            direction = direction + 360;
        }
        tvHeading.setText("Phone is heading to: " + Float.toString(degree) + " degrees" );

        RotateAnimation raQibla = new RotateAnimation(currentDegreeNeedle, direction, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        raQibla.setDuration(210);
        raQibla.setFillAfter(true);

        qibla_arrow.startAnimation(raQibla);
        compassImage.startAnimation(raQibla);

        currentDegreeNeedle = direction;

        RotateAnimation ra = new RotateAnimation(currentDegree, -degree, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);

        ra.setDuration(210);
        ra.setFillAfter(true);
        compassImage.startAnimation(ra);

        currentDegree = -degree;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Do nothing
    }

    @Override
    public void onLocationChanged(Location location) {
        // Get the current location coordinates
        double latitude = location.getLatitude();
        double longitude = location.getLongitude();
        pref.setLocation(latitude, longitude);
        kaabaBearing = bearingToKaaba();
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        // Do nothing
    }

    @Override
    public void onProviderEnabled(String provider) {
        // Do nothing
    }

    @Override
    public void onProviderDisabled(String provider) {
        // Do nothing
    }

}