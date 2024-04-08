package com.prayer.app;

import static android.content.Intent.getIntent;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.preference.PreferenceManager;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;


public class PrayerTimeReminderReceiver extends BroadcastReceiver {



    @SuppressLint("MissingPermission")
    @Override
    public void onReceive(Context context, Intent intent) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        String prayerName = intent.getStringExtra("prayerName");
        int NotificationID = intent.getIntExtra("NotificationID" , 0);
        boolean isEnabled = prefs.getBoolean(prayerName + "_notification", true);

        // Only proceed if the notification is enabled for this prayer
        if (isEnabled) {
            // Define a fallback prayer name in case the intent extra is missing
            if (prayerName == null) prayerName = "Prayer";
            Intent serviceIntent = new Intent(context, AudioPlayService.class);
            serviceIntent.setAction(AudioPlayService.ACTION_PLAY_AUDIO);
            serviceIntent.putExtra("prayerName", prayerName);
            serviceIntent.putExtra("NotificationID",NotificationID);
            ContextCompat.startForegroundService(context, serviceIntent);
        }
    }

}