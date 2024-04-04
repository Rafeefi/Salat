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
import android.net.Uri;
import android.os.Build;
import android.preference.PreferenceManager;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

public class PrayerTimeReminderReceiver extends BroadcastReceiver {
    private NotificationManagerCompat notificationManager;

    @SuppressLint("MissingPermission")
    @Override
    public void onReceive(Context context, Intent intent) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        String prayerName = intent.getStringExtra("prayerName");
        boolean isEnabled = prefs.getBoolean(prayerName + "_notification", true);

        // Only proceed if the notification is enabled for this prayer
        if (isEnabled) {
            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);

            // Define a fallback prayer name in case the intent extra is missing
            if (prayerName == null) prayerName = "Prayer";

            // Channel ID must be defined in your app and created beforehand
            String channelID = "PRAYER_REMINDER_CHANNEL";
            int notificationID = intent.getIntExtra("NotificationID", 0); // Unique ID for each notification

            // Intent that triggers when the notification is clicked
            Intent notificationIntent = new Intent(context, PrayerDetailActivity.class);
            notificationIntent.putExtra("prayerName", prayerName);
            notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, notificationID, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

            // Building the notification
            Uri soundUri = Uri.parse("android.resource://" + context.getPackageName()+ "/" + R.raw.athan);
            Notification notification = new NotificationCompat.Builder(context, channelID)
                    .setSmallIcon(android.R.drawable.ic_lock_idle_alarm) // Replace with your app-specific icon
                    .setContentTitle("Prayer Time Reminder") // Notification title
                    .setContentText(prayerName + " Time Reminder") // Notification text
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                    .setContentIntent(pendingIntent)
                    // Set the custom sound
                    .setSound(soundUri)
                    .setAutoCancel(true)
                    .build();

            // No need to check for VIBRATE permission as it's not directly used here
            notificationManager.notify(notificationID, notification);
        }
    }

}