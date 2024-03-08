package com.prayer.app;
import static android.app.PendingIntent.FLAG_UPDATE_CURRENT;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.os.Build;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
public class SilentModeFragment  extends PreferenceFragment {
    public static final String PREF_SILENT = "silent";
    private static final int CALLBACK_CODE = 0;
    private SharedPreferences.OnSharedPreferenceChangeListener preferenceChangeListener;

    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.silentmodepreference);
        //Listener for all the groups
        preferenceChangeListener = new SharedPreferences.OnSharedPreferenceChangeListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
                if (key.equals(PREF_SILENT)) {
                    EditTextPreference editText = (EditTextPreference) findPreference(key);
                    String time = editText.getText();
                    long timeInMillis = Long.valueOf(time);
                    silencePhone(timeInMillis);
                }
            }
        };
    }


    @Override
    public void onResume() {
        super.onResume();

        getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(preferenceChangeListener);
    }

    @Override
    public void onPause() {
        super.onPause();
        getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(preferenceChangeListener);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void silencePhone(long timeInMillis) {
        NotificationManager notificationManager = (NotificationManager) getActivity().getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
        if (notificationManager.isNotificationPolicyAccessGranted()) {
            AudioManager audioManager = (AudioManager) getActivity().getApplicationContext().getSystemService(Context.AUDIO_SERVICE);
            audioManager.setRingerMode(AudioManager.RINGER_MODE_SILENT);

            Intent intent = new Intent(getActivity(), SilentModeBroadcast.class);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(getActivity(), 0, intent, FLAG_UPDATE_CURRENT);
            AlarmManager alarmManager = (AlarmManager) getActivity().getSystemService(Context.ALARM_SERVICE);

            alarmManager.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + (timeInMillis * 60000), pendingIntent);
            Toast.makeText(getActivity(), "Phone will be silent for " + timeInMillis + " minute(s)", Toast.LENGTH_SHORT).show();
        } else {
            // Ask the user to grant access
            Intent intent = new Intent(android.provider.Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS);
            startActivityForResult(intent, CALLBACK_CODE);
        }
    }
}
