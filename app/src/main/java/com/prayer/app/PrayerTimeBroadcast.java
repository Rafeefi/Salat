package com.prayer.app;

import android.Manifest;
import android.app.Notification;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

public class PrayerTimeBroadcast extends BroadcastReceiver {
    private NotificationManagerCompat notificationManager;
    @Override
    public void onReceive(Context context, Intent intent) {
        String ChannelID = "CHANEL_1";
        int NotificationID = intent.getIntExtra("NotificationID", 0);
        Notification notification = new NotificationCompat.Builder(context, ChannelID)
                .setSmallIcon(android.R.drawable.ic_lock_idle_alarm)
                .setContentTitle("Prayer Time")
                .setContentText("Reminder for next prayer time")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                 .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                .build();
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.VIBRATE) == PackageManager.PERMISSION_GRANTED) {
            // You have permission, proceed with showing the notification
            notificationManager.notify(NotificationID, notification);
        }

    }


}