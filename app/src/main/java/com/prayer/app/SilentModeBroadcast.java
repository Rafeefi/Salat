package com.prayer.app;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Build;
import android.widget.Toast;

import java.util.Calendar;

public class SilentModeBroadcast extends BroadcastReceiver {
    AudioManager audioManager;
    @Override
    public void onReceive(Context context, Intent intent) {
        boolean setSilentMode = intent.getBooleanExtra("setSilentMode", false);
        audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);

        if (setSilentMode) {
            handleSetSilentMode(context, intent);
        } else {
            handleUnsetSilentMode(context, intent);
        }
    }

    private void handleSetSilentMode(Context context, Intent intent) {
        int hour = intent.getIntExtra("hour", 0);
        int minute = intent.getIntExtra("minute", 0);
        boolean isEndTime = intent.getBooleanExtra("isEndTime", false);

        Calendar silentTime = Calendar.getInstance();
        silentTime.set(Calendar.HOUR_OF_DAY, hour);
        silentTime.set(Calendar.MINUTE, minute);
        silentTime.set(Calendar.SECOND, 0);
        long startTimeInMillis = silentTime.getTimeInMillis();

        long currentTimeInMillis = System.currentTimeMillis();
        long delayInMillis = startTimeInMillis - currentTimeInMillis;

        if (isEndTime) {
            // If it's end time, adjust the delay to avoid negative values
            if (delayInMillis < 0) {
                delayInMillis += 24 * 60 * 60 * 1000; // Add 24 hours if the selected time is in the past
            }
        }

        // Schedule silent mode
        scheduleSilentMode(context, delayInMillis, hour, minute, isEndTime);
    }

    private void handleUnsetSilentMode(Context context, Intent intent) {
        int hour = intent.getIntExtra("hour", 0);
        int minute = intent.getIntExtra("minute", 0);

        Calendar endTime = Calendar.getInstance();
        endTime.set(Calendar.HOUR_OF_DAY, hour);
        endTime.set(Calendar.MINUTE, minute);
        endTime.set(Calendar.SECOND, 0);
        long endTimeInMillis = endTime.getTimeInMillis();


    }

    private void scheduleSilentMode(Context context, long delayInMillis, int hour, int minute, boolean isEndTime) {
        Intent silentModeIntent = new Intent(context, SilentModeBroadcast.class)
                .putExtra("setSilentMode", false);
        context.sendBroadcast(silentModeIntent);
        if (isEndTime) {
            // Only show the toast message when both start and end times are specified
            Toast.makeText(context, "Silent mode scheduled", Toast.LENGTH_SHORT).show();
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // Use setExactAndAllowWhileIdle for API 23 and above
            AlarmHelper.setExactAndAllowWhileIdle(context, delayInMillis, silentModeIntent);
        } else {
            // Use setExact for API 19 and above
            AlarmHelper.setExact(context, delayInMillis, silentModeIntent);
        }

        Toast.makeText(context, "Silent mode scheduled", Toast.LENGTH_SHORT).show();
    }
}

