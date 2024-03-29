package com.prayer.app;
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

import com.prayer.app.R;

public class ManageLocationFragment extends PreferenceFragment{
    public static final String PREF_LATITUDE = "latitude";
    private static final int CALLBACK_CODE = 0;
    private SharedPreferences.OnSharedPreferenceChangeListener preferenceChangeListener;
    private int RG3 = 0;
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.locationpreference);
        //Listener for all the groups
        preferenceChangeListener = new SharedPreferences.OnSharedPreferenceChangeListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {

                if (key.equals(PREF_LATITUDE)) {
                    ListPreference latitudePref = (ListPreference) findPreference(key);
                    RG3 = Integer.valueOf((latitudePref.getValue()));

                    Toast.makeText(getActivity(), "Latitude adjustment updated", Toast.LENGTH_SHORT).show();

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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CALLBACK_CODE) {
            Toast.makeText(getActivity(), "Acccess granted!", Toast.LENGTH_SHORT).show();
        }

    }

    @RequiresApi(api = Build.VERSION_CODES.M)




    public int getRG3() {
        return RG3;
    }




}
