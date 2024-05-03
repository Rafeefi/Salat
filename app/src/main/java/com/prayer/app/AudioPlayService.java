package com.prayer.app;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;

import androidx.core.app.NotificationCompat;

public class AudioPlayService extends Service {

    private MediaPlayer mediaPlayer;
    public static final String ACTION_PLAY_AUDIO = "com.prayer.app.action.PLAY_AUDIO";
    public static final String ACTION_STOP_AUDIO = "com.prayer.app.action.STOP_AUDIO";
    private  int NOTIFICATION_ID; // Use a static ID for this demo

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_PLAY_AUDIO.equals(action)) {
                String prayerName = intent.getStringExtra("prayerName");
                NOTIFICATION_ID = intent.getIntExtra("NotificationID", 0);
                playAudio();
                buildForegroundNotification(prayerName,NOTIFICATION_ID); // Create and show the notification here
            } else if (ACTION_STOP_AUDIO.equals(action)) {
                stopAudio();
            }
        }
        return START_STICKY;
    }

    private void playAudio() {
        if (mediaPlayer == null) {
            mediaPlayer = MediaPlayer.create(this, R.raw.adhan);
            mediaPlayer.setOnCompletionListener(mp -> stopAudio());
        }

        if (!mediaPlayer.isPlaying()) {
            mediaPlayer.start();
        }
    }

    private void stopAudio() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
        stopForeground(true); // Remove the notification
        stopSelf();
    }

    private void buildForegroundNotification(String prayerName , int NotificationID) {
        Intent stopSelf = new Intent(this, AudioPlayService.class);
        stopSelf.setAction(ACTION_STOP_AUDIO);
        PendingIntent pendingStopSelf = PendingIntent.getService(this, 0, stopSelf, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        Intent notificationIntent = new Intent(this, PrayerDetailActivity.class);
        notificationIntent.putExtra("prayerName", prayerName);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, NotificationID, notificationIntent,  PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        Notification notification = new NotificationCompat.Builder(this, "PRAYER_REMINDER_CHANNEL") // Use your actual channel ID
                .setContentTitle("Prayer Time")
                .setContentText("Time for " + prayerName)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                .setSmallIcon(android.R.drawable.ic_lock_idle_alarm)
                .setContentIntent(pendingIntent)
                .addAction(android.R.drawable.ic_media_pause, "Stop", pendingStopSelf) // Adding the "Stop" action button
                .build();

        startForeground(NOTIFICATION_ID, notification);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null; // Not providing binding
    }
}