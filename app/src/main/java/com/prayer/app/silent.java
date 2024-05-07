package com.prayer.app;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import android.Manifest;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;


public class silent extends AppCompatActivity {

    private Button btnStartTime, btnEndTime ,btnSelectLocation;
    private TextView tvSelectedTimes;
    Calendar calendar = Calendar.getInstance();
    int year = calendar.get(Calendar.YEAR);
    PrayTime prayTime = new PrayTime();
    int month = calendar.get(Calendar.MONTH) + 1; // Months are 0-indexed, so we add 1
    int day = calendar.get(Calendar.DAY_OF_MONTH);


    private int startHour, startMinute, endHour, endMinute;
    ImageView imageTurnnotificati1, imageTurnnotificati2, imageTurnnotificati3, imageTurnnotificati4, back, home, imageTurnnotificati5;

    boolean fajr, dhuhr, asr, maghrib, isha;

    private static final String SILENT_MODE_ACTION = "com.example.silentmode.action";
    SharedPreferences prefs;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.silentpage);

        boolean isFajrEnabled = getNotificationState("Fajr");
        boolean isDhuhrEnabled = getNotificationState("Dhuhr");
        boolean isAsrEnabled = getNotificationState("Asr");
        boolean isMaghribEnabled = getNotificationState("Maghrib");
        boolean isIshaEnabled = getNotificationState("Isha");

        btnStartTime = findViewById(R.id.StarttimeB);
        tvSelectedTimes = findViewById(R.id.StarttextView);
        imageTurnnotificati1 = findViewById(R.id.imageTurnnotificati1);
        imageTurnnotificati2 = findViewById(R.id.imageTurnnotificati2);
        imageTurnnotificati3 = findViewById(R.id.imageTurnnotificati3);
        imageTurnnotificati4 = findViewById(R.id.imageTurnnotificati4);
        imageTurnnotificati5 = findViewById(R.id.imageTurnnotificati5);
        back = findViewById(R.id.imageBack);
        home = findViewById(R.id.home);
        btnEndTime = ((Button) findViewById(R.id.CancelTimer));
        btnSelectLocation=((Button) findViewById(R.id.btnSelectLocation));
        imageTurnnotificati1.setImageResource(isFajrEnabled ? R.drawable.img_turnnotificati : R.drawable.img_turnoffnotification);
        fajr = isFajrEnabled;
        imageTurnnotificati2.setImageResource(isDhuhrEnabled ? R.drawable.img_turnnotificati : R.drawable.img_turnoffnotification);
        dhuhr = isDhuhrEnabled;
        imageTurnnotificati3.setImageResource(isAsrEnabled ? R.drawable.img_turnnotificati : R.drawable.img_turnoffnotification);
        asr = isAsrEnabled;
        imageTurnnotificati4.setImageResource(isMaghribEnabled ? R.drawable.img_turnnotificati : R.drawable.img_turnoffnotification);
        maghrib = isMaghribEnabled;
        imageTurnnotificati5.setImageResource(isIshaEnabled ? R.drawable.img_turnnotificati : R.drawable.img_turnoffnotification);
        isha = isIshaEnabled;
        btnSelectLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
             GotoMap();
            }
        });
        btnEndTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancelSilentMode();
            }
        });
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goBack();
            }
        });
        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goHome();
            }
        });


        btnStartTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTimePickerDialog(false);
            }
        });


        imageTurnnotificati1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (fajr) {
                    cancelAlarmForFajr();
                    fajr = false;
                    imageTurnnotificati1.setImageResource(R.drawable.img_turnoffnotification);
                } else {
                    enableFajrNotificatio();
                    fajr = true;
                    imageTurnnotificati1.setImageResource(R.drawable.img_turnnotificati);
                }
                saveNotificationState("Fajr", fajr);
            }
        });
        imageTurnnotificati2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (dhuhr) {
                    cancelAlarmForDhuhr();
                    dhuhr = false;
                } else {
                    enableDhuhrNotification();
                    dhuhr = true;
                    imageTurnnotificati2.setImageResource(R.drawable.img_turnnotificati);
                }
                saveNotificationState("Dhuhr", dhuhr);

            }
        });
        imageTurnnotificati3.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (asr) {
                    cancelAlarmForAsr();
                    asr = false;
                } else {
                    enableasrNotification();
                    asr = true;
                    imageTurnnotificati3.setImageResource(R.drawable.img_turnnotificati);
                }
                saveNotificationState("Asr", asr);
            }
        });
        imageTurnnotificati4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (maghrib) {
                    cancelAlarmForMaghrib();
                    maghrib = false;
                } else {
                    enablemaghribNotification();
                    maghrib = true;
                    imageTurnnotificati4.setImageResource(R.drawable.img_turnnotificati);
                }
                saveNotificationState("Maghrib", maghrib);
            }
        });
        imageTurnnotificati5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isha) {
                    cancelAlarmForIsha();
                    isha = false;
                } else {
                    enableishaNotification();
                    isha = true;
                    imageTurnnotificati5.setImageResource(R.drawable.img_turnnotificati);
                }
                saveNotificationState("Isha", isha);
            }
        });
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        int startHour = prefs.getInt("startHour", -1);
        int startMinute = prefs.getInt("startMinute", -1);
        int endHour = prefs.getInt("endHour", -1);
        int endMinute = prefs.getInt("endMinute", -1);

        // Update TextView with the saved silent mode schedule
        if (startHour != -1 && startMinute != -1 && endHour != -1 && endMinute != -1) {
            String startTime = String.format(Locale.getDefault(), "%02d:%02d", startHour, startMinute);
            String endTime = String.format(Locale.getDefault(), "%02d:%02d", endHour, endMinute);
            String silentTimeRange = "Start: " + startTime + " - End: " + endTime;
            tvSelectedTimes.setText(silentTimeRange);
        }

    }

    private void GotoMap() {
        Intent intent = new Intent(this, MapActivity.class);
        startActivity(intent);
    }

    private void goHome() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    private void goBack() {
        Intent intent = new Intent(this, Settings.class);
        startActivity(intent);
    }

    // Method to cancel the alarm for Fajr prayer time
    private void cancelAlarmForFajr() {
        saveNotificationState("Fajr", false);
        Toast.makeText(this, "Fajr notification disabled", Toast.LENGTH_SHORT).show();
        imageTurnnotificati1.setImageResource(R.drawable.img_turnoffnotification);

    }

    private void cancelAlarmForAsr() {
        saveNotificationState("Asr", false);
        Toast.makeText(this, "Asr notification disabled", Toast.LENGTH_SHORT).show();
        imageTurnnotificati3.setImageResource(R.drawable.img_turnoffnotification);

    }

    private void cancelAlarmForDhuhr() {
        saveNotificationState("Dhuhr", false);
        Toast.makeText(this, "Dhuhr notification disabled", Toast.LENGTH_SHORT).show();
        imageTurnnotificati2.setImageResource(R.drawable.img_turnoffnotification);

    }

    private void cancelAlarmForMaghrib() {
        saveNotificationState("Maghrib", false);
        Toast.makeText(this, "Maghrib notification disabled", Toast.LENGTH_SHORT).show();
        imageTurnnotificati4.setImageResource(R.drawable.img_turnoffnotification);

    }

    private void cancelAlarmForIsha() {
        saveNotificationState("Isha", false);
        Toast.makeText(this, "Isha notification disabled", Toast.LENGTH_SHORT).show();
        imageTurnnotificati5.setImageResource(R.drawable.img_turnoffnotification);

    }

    private void enableFajrNotificatio() {
        saveNotificationState("Fajr", true);
        Toast.makeText(this, "Fajr notification enabled", Toast.LENGTH_SHORT).show();
        imageTurnnotificati1.setImageResource(R.drawable.img_turnnotificati);
    }


    private void enableasrNotification() {
        saveNotificationState("Asr", true);
        Toast.makeText(this, "Asr notification enabled", Toast.LENGTH_SHORT).show();
        imageTurnnotificati3.setImageResource(R.drawable.img_turnnotificati);
    }

    private void enablemaghribNotification() {
        saveNotificationState("Maghrib", true);
        Toast.makeText(this, "Maghrib notification enabled", Toast.LENGTH_SHORT).show();
        imageTurnnotificati4.setImageResource(R.drawable.img_turnnotificati);


    }

    private void enableishaNotification() {
        saveNotificationState("Isha", true);
        Toast.makeText(this, "Isha notification enabled", Toast.LENGTH_SHORT).show();
        imageTurnnotificati5.setImageResource(R.drawable.img_turnnotificati);

    }

    public long parsePrayerTimeToMilliseconds(String prayerTime) {
        // Assuming the time format is "HH:mm"
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());
        Date prayerTimeDate;
        try {
            prayerTimeDate = sdf.parse(prayerTime);
        } catch (ParseException e) {
            e.printStackTrace();
            return -1; // Return -1 to indicate an error
        }

        // Convert the prayer time Date object to milliseconds since epoch
        return prayerTimeDate.getTime();
    }

    private void enableDhuhrNotification() {
        saveNotificationState("Dhuhr", true);
        Toast.makeText(this, "Dhuhr notification enabled", Toast.LENGTH_SHORT).show();
        imageTurnnotificati2.setImageResource(R.drawable.img_turnnotificati);

    }

    private void saveNotificationState(String prayerName, boolean isEnabled) {
        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean(prayerName + "_notification", isEnabled);
        editor.apply();
    }

    private boolean getNotificationState(String prayerName) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        // Return the saved state; default to true if not found
        return prefs.getBoolean(prayerName + "_notification", true);
    }


    private void showTimePickerDialog(final boolean isUnsetSilentMode) {
        Calendar currentTime = Calendar.getInstance();
        int hour = currentTime.get(Calendar.HOUR_OF_DAY);
        int minute = currentTime.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        if (isUnsetSilentMode) {
                            // Handle unset silent mode

                        } else {
                            // Handle set silent mode
                            startHour = hourOfDay;
                            startMinute = minute;
                            // Show end time picker immediately after setting the start time
                            showEndTimePickerDialog();
                        }
                    }
                }, hour, minute, true);

        timePickerDialog.show();
    }

    private void showEndTimePickerDialog() {
        Calendar currentTime = Calendar.getInstance();
        int hour = currentTime.get(Calendar.HOUR_OF_DAY);
        int minute = currentTime.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        endHour = hourOfDay;
                        endMinute = minute;
                        // Handle set silent mode with start and end times
                        setSilentMode(startHour, startMinute, endHour, endMinute);
                    }
                }, hour, minute, true);

        timePickerDialog.show();
    }

    private static final int PERMISSION_REQUEST_NOTIFICATION_POLICY = 1001;
    private static final String SILENT_MODE_PREF = "silent_mode_pref";

    private void setSilentMode(final int startHour, final int startMinute, final int endHour, final int endMinute) {
        String startTime = String.format(Locale.getDefault(), "%02d:%02d", startHour, startMinute);
        String endTime = String.format(Locale.getDefault(), "%02d:%02d", endHour, endMinute);

        // Update TextView with both start and end times
        String silentTimeRange = "Start: " + startTime + " - End: " + endTime;
        tvSelectedTimes.setText(silentTimeRange);

        // Save the scheduled silent mode state
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(this).edit();
        editor.putInt("startHour", startHour);
        editor.putInt("startMinute", startMinute);
        editor.putInt("endHour", endHour);
        editor.putInt("endMinute", endMinute);
        editor.apply();

        try {
            // Check if permission is granted
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_NOTIFICATION_POLICY) != PackageManager.PERMISSION_GRANTED) {
                // Permission is not granted, request it
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_NOTIFICATION_POLICY}, PERMISSION_REQUEST_NOTIFICATION_POLICY);
            } else {
                // Permission is granted, proceed to set silent mode
                setSilentModeInternal(startHour, startMinute, endHour, endMinute);
                // Show toast message indicating scheduled silent mode
                Toast.makeText(this, "Silent mode scheduled for " + silentTimeRange, Toast.LENGTH_SHORT).show();
            }
        } catch (SecurityException e) {
            // Handle SecurityException
            Toast.makeText(this, "Failed to change Do Not Disturb state. Please check your device settings.", Toast.LENGTH_SHORT).show();
            e.printStackTrace(); // Log the exception for debugging purposes
        }
    }

    // Method to cancel scheduled silent mode
    private void cancelSilentMode() {
        // Remove scheduled task
        new Handler().removeCallbacksAndMessages(null);
        // Revert phone's audio profile back to normal immediately
        AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        audioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
        // Clear saved silent mode state
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(this).edit();
        editor.clear().apply();
        // Clear text in TextView
        tvSelectedTimes.setText("");
        // Show toast message indicating cancellation
        Toast.makeText(this, "Silent mode schedule canceled", Toast.LENGTH_SHORT).show();
    }

    // Override onRequestPermissionsResult to handle the result:
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_NOTIFICATION_POLICY) {
            // Check if the permission has been granted
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission has been granted, call internal method to set silent mode
                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
                int startHour = prefs.getInt("startHour", -1);
                int startMinute = prefs.getInt("startMinute", -1);
                int endHour = prefs.getInt("endHour", -1);
                int endMinute = prefs.getInt("endMinute", -1);
                setSilentModeInternal(startHour, startMinute, endHour, endMinute);
            } else {
                // Permission denied, handle accordingly
                Toast.makeText(this, "Permission denied. Cannot change Do Not Disturb state.", Toast.LENGTH_SHORT).show();
            }
        }
    }
//Ckecking if merged correctly
    // Calculate duration in milliseconds between start time and end time
    private long calculateDurationInMillis(int startHour, int startMinute, int endHour, int endMinute) {
        Calendar startTime = Calendar.getInstance();
        startTime.set(Calendar.HOUR_OF_DAY, startHour);
        startTime.set(Calendar.MINUTE, startMinute);
        Calendar endTime = Calendar.getInstance();
        endTime.set(Calendar.HOUR_OF_DAY, endHour);
        endTime.set(Calendar.MINUTE, endMinute);

        // Check if the end time is before the start time, indicating a next-day schedule
        if (endTime.before(startTime)) {
            endTime.add(Calendar.DAY_OF_MONTH, 1);
        }

        return endTime.getTimeInMillis() - startTime.getTimeInMillis();
    }
    // Internal method to set silent mode, called when permission is granted
    private void setSilentModeInternal(final int startHour, final int startMinute, final int endHour, final int endMinute) {
        // Get AudioManager service
        final AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);

        // Set phone to silent mode
        audioManager.setRingerMode(AudioManager.RINGER_MODE_SILENT);

        // Schedule a task to revert phone's audio profile back to normal after the specified duration
        final long durationInMillis = calculateDurationInMillis(startHour, startMinute, endHour, endMinute);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // Revert phone's audio profile back to normal
                audioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
                // Show toast message indicating that silent mode has ended
                Toast.makeText(getApplicationContext(), "Silent mode ended", Toast.LENGTH_SHORT).show();
            }
        }, durationInMillis);
    }
}

