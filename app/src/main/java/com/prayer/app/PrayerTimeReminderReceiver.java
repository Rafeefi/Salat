package com.prayer.app;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.core.app.NotificationCompat;

public class PrayerTimeReminderReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        // Extract extra data from the Intent
        String prayerName = intent.getStringExtra("prayerName");

        // Show notification
        showPrayerTimeNotification(context, prayerName);
    }

    private void showPrayerTimeNotification(Context context, String prayerName) {
        Intent notificationIntent = new Intent(context, PrayerDetailActivity.class);
        notificationIntent.putExtra("prayerName", prayerName);
        PendingIntent contentIntent = PendingIntent.getActivity(context, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        String channelId = "prayer_time_channel";
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId, "Prayer Time Notifications", NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);
        }

        Notification notification = new NotificationCompat.Builder(context, channelId)
                .setSmallIcon(R.mipmap.logo)
                .setContentTitle("Prayer Time Reminder")
                .setContentText("Time for " + prayerName)
                .setContentIntent(contentIntent)
                .build();

        notificationManager.notify(1, notification);
    }
}