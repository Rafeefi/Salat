package com.prayer.app;



import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.os.Build;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.PreferenceFragment;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;


public class timeCalculations extends PreferenceFragment {

    // list prefs
    public static final String PREF_JURISTIC = "juristic";
    public static final String PREF_CALC = "calculation";
    public static final String PREF_TIME = "time";
    private static final int CALLBACK_CODE = 0;

    private SharedPreferences.OnSharedPreferenceChangeListener preferenceChangeListener;

    private int RG1 = 0;
    private int RG2 = 4;
    private int RG3 = 0;
    private int RG4 = 1;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.timepreferences);
        //Listener for all the groups
        preferenceChangeListener = new SharedPreferences.OnSharedPreferenceChangeListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
                if (key.equals(PREF_JURISTIC)) {
                    ListPreference juristicPref = (ListPreference) findPreference(key); //if you need the preference key similar to find by ID
                    RG1 = Integer.valueOf((juristicPref.getValue()));
                    Toast.makeText(getActivity(), "Juristic method updated", Toast.LENGTH_SHORT).show();
                }
                if (key.equals(PREF_CALC)) {
                    ListPreference calculatePref = (ListPreference) findPreference(key);
                    RG2 = Integer.valueOf((calculatePref.getValue()));

                    Toast.makeText(getActivity(),"Calculation convention updated" , Toast.LENGTH_SHORT).show();
                }

                if (key.equals(PREF_TIME)) {
                    ListPreference timePref = (ListPreference) findPreference(key);
                    RG4 = Integer.valueOf((timePref.getValue()));

                    Toast.makeText(getActivity(), "Time format updated", Toast.LENGTH_SHORT).show();
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


    public int getRG1() {
        return RG1;
    }

    public int getRG2() {
        return RG2;
    }

    public int getRG3() {
        return RG3;
    }

    public int getRG4() {
        return RG4;
    }

}