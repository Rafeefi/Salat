package com.prayer.app;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;


public class silent extends AppCompatActivity {

    private Button btnStartTime, btnEndTime;
    private TextView tvSelectedTimes;
    private int startHour, startMinute, endHour, endMinute;
    ImageView imageTurnnotificati1, imageTurnnotificati2, imageTurnnotificati3, imageTurnnotificati4, imageTurnnotificati5;
    AlarmManager alarmManager;
    PendingIntent pendingIntent1, pendingIntent2, pendingIntent3, pendingIntent4, pendingIntent5;
    boolean fajr = true;
    public static final String PREF_SILENT = "silent";
    private static final String SILENT_MODE_ACTION = "com.example.silentmode.action";

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.silentpage);

        btnStartTime = findViewById(R.id.StarttimeB);
        tvSelectedTimes = findViewById(R.id.StarttextView);
        imageTurnnotificati1 = findViewById(R.id.imageTurnnotificati1);
        imageTurnnotificati2 = findViewById(R.id.imageTurnnotificati2);
        imageTurnnotificati3 = findViewById(R.id.imageTurnnotificati3);
        imageTurnnotificati4 = findViewById(R.id.imageTurnnotificati4);
        imageTurnnotificati5 = findViewById(R.id.imageTurnnotificati5);
        // Register BroadcastReceiver to receive custom intent
        registerReceiver(new SilentModeBroadcast(), new IntentFilter(SILENT_MODE_ACTION));

        btnStartTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTimePickerDialog(false);
            }
        });

        btnEndTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTimePickerDialog(true);
            }
        });


        imageTurnnotificati1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (fajr) {
                    cancelAlarmForFajr();
                    fajr = false;
                } else {
                    String fajrTime = getFajrTime();
                    long fajrTimeMillisecond = parseFajrTimeToMilliseconds(fajrTime);
                    enableAlarmForFajr(fajrTimeMillisecond);
                }
            }
        });
        imageTurnnotificati2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancelAlarmForDhuhr();
            }
        });
        imageTurnnotificati3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancelAlarmForAsr();
            }
        });
        imageTurnnotificati4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancelAlarmForMaghrib();
            }
        });
        imageTurnnotificati5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancelAlarmForIsha();
            }
        });

    }

    // Method to cancel the alarm for Fajr prayer time
    private void cancelAlarmForFajr() {
        Intent intent = new Intent(this, PrayerTimeBroadcast.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_IMMUTABLE);
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        if (alarmManager != null && pendingIntent != null) {
            alarmManager.cancel(pendingIntent);
            Toast.makeText(this, "Fajr notification disabled", Toast.LENGTH_SHORT).show();
            imageTurnnotificati1.setImageResource(R.drawable.img_turnoffnotification);
        }
    }

    private void cancelAlarmForAsr() {
        Intent intent = new Intent(this, PrayerTimeBroadcast.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_IMMUTABLE);
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        if (alarmManager != null && pendingIntent != null) {
            alarmManager.cancel(pendingIntent);
            Toast.makeText(this, "Asr notification disabled", Toast.LENGTH_SHORT).show();
            imageTurnnotificati3.setImageResource(R.drawable.img_turnoffnotification);
        }
    }

    private void cancelAlarmForDhuhr() {
        Intent intent = new Intent(this, PrayerTimeBroadcast.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_IMMUTABLE);
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        if (alarmManager != null && pendingIntent != null) {
            alarmManager.cancel(pendingIntent);
            Toast.makeText(this, "Dhuhr notification disabled", Toast.LENGTH_SHORT).show();
            imageTurnnotificati2.setImageResource(R.drawable.img_turnoffnotification);
        }
    }

    private void cancelAlarmForMaghrib() {
        Intent intent = new Intent(this, PrayerTimeBroadcast.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_IMMUTABLE);
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        if (alarmManager != null && pendingIntent != null) {
            alarmManager.cancel(pendingIntent);
            Toast.makeText(this, "Maghrib notification disabled", Toast.LENGTH_SHORT).show();
            imageTurnnotificati4.setImageResource(R.drawable.img_turnoffnotification);
        }
    }

    private void cancelAlarmForIsha() {
        Intent intent = new Intent(this, PrayerTimeBroadcast.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_IMMUTABLE);
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        if (alarmManager != null && pendingIntent != null) {
            alarmManager.cancel(pendingIntent);
            Toast.makeText(this, "Isha notification disabled", Toast.LENGTH_SHORT).show();
            imageTurnnotificati5.setImageResource(R.drawable.img_turnoffnotification);
        }
    }

    private void enableAlarmForFajr(long fajrTimeInMillis) {
        Intent intent = new Intent(this, PrayerTimeBroadcast.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_IMMUTABLE);
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        if (alarmManager != null && pendingIntent != null) {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, fajrTimeInMillis, pendingIntent);
            Toast.makeText(this, "Fajr notification enabled", Toast.LENGTH_SHORT).show();
            imageTurnnotificati1.setImageResource(R.drawable.img_turnnotificati);
            fajr = true;
        }
    }

    private String getFajrTime() {
        PrayerTimeDatabase prayerTimeDatabase = new PrayerTimeDatabase(getApplicationContext());
        return prayerTimeDatabase.getFajrTime();
    }
    private long parseFajrTimeToMilliseconds(String fajrTime) {
        DateFormat dateFormat = new SimpleDateFormat("HH:mm"); // Assuming the format is "HH:mm"
        try {
            Date date = dateFormat.parse(fajrTime);
            return date.getTime(); // Return the milliseconds since January 1, 1970, 00:00:00 GMT
        } catch (ParseException e) {
            e.printStackTrace();
            return -1; // Return -1 if parsing fails
        }
    }
    // Method to get Fajr time from the database and convert it to milliseconds
    private long getFajrTimeInMillis() {
        PrayerTimeDatabase prayerTimeDatabase = new PrayerTimeDatabase(this);
        ArrayList<PrayerTime> prayerTimes = prayerTimeDatabase.getTimes();
        for (PrayerTime prayerTime : prayerTimes) {
            if (prayerTime.getName().equalsIgnoreCase("Fajr")) {
                return parseFajrTimeToMilliseconds(prayerTime.getTime());
            }
        }
        return -1; // Return -1 if Fajr time is not found
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
                        setSilentMode(startHour, startMinute, endHour,endMinute);
                    }
                }, hour, minute, true);

        timePickerDialog.show();
    }
    private void setSilentMode(int startHour, int startMinute,int endHour, int endMinute) {
        String startTime = String.format(Locale.getDefault(), "%02d:%02d", startHour, startMinute);
        String endTime = String.format(Locale.getDefault(), "%02d:%02d", endHour, endMinute);

        // Update TextView with both start and end times
        String silentTimeRange = "Start: " + startTime + " - End: " + endTime;
        tvSelectedTimes.setText(silentTimeRange);
        // Send broadcast to set/unset silent mode
        sendBroadcast(new Intent(SILENT_MODE_ACTION)
                    .putExtra("setSilentMode", true)
                .putExtra("startHour", startHour)
                .putExtra("startMinute", startMinute)
                .putExtra("endHour", endHour)
                .putExtra("endMinute", endMinute));

            // Only show the toast message when setting the silent mode
            if (startHour != -1 && endHour != -1) {
                Toast.makeText(this, "Silent mode set for " + startHour + ":" + startMinute, Toast.LENGTH_SHORT).show();
            }



    }
}








