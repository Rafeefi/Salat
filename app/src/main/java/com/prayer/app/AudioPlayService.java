package com.prayer.app;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;

public class AudioPlayService extends Service {
    private MediaPlayer mediaPlayer;
    public static final String ACTION_PLAY_AUDIO = "com.prayer.app.action.PLAY_AUDIO";
    public static final String ACTION_STOP_AUDIO = "com.prayer.app.action.STOP_AUDIO";

    public AudioPlayService() {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_PLAY_AUDIO.equals(action)) {
                playAudio();
            } else if (ACTION_STOP_AUDIO.equals(action)) {
                stopAudio();
            }
        }
        return START_STICKY;
    }

    private void playAudio() {
        // Implement audio playback logic
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
        stopSelf();
    }

    @Override
    public IBinder onBind(Intent intent) {
        // We don't provide binding, so return null
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopAudio(); // Ensure audio is stopped when service is destroyed
    }
}
