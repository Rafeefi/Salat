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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;


public class silent extends AppCompatActivity {

    private Button btnStartTime, btnEndTime;
    private TextView tvSelectedTimes;
    Calendar calendar = Calendar.getInstance();
    int year = calendar.get(Calendar.YEAR);
    PrayTime prayTime = new PrayTime();
    int month = calendar.get(Calendar.MONTH) + 1; // Months are 0-indexed, so we add 1
    int day = calendar.get(Calendar.DAY_OF_MONTH);

    // Example latitude, longitude, and time zone (adjust as needed)
    double latitude = 40.7128;   // Example latitude (e.g., latitude of New York City)
    double longitude = -74.0060; // Example longitude (e.g., longitude of New York City)
    double timeZone = -5;
    ArrayList<String> prayerTimes = prayTime.getDatePrayerTimes(year, month, day, latitude, longitude, timeZone);

    private int startHour, startMinute, endHour, endMinute;
    ImageView imageTurnnotificati1, imageTurnnotificati2, imageTurnnotificati3, imageTurnnotificati4, imageTurnnotificati5;
    AlarmManager alarmManager;
    PendingIntent pendingIntent1, pendingIntent2, pendingIntent3, pendingIntent4, pendingIntent5;
    boolean fajr=true,dhuhr = true,asr=true,maghrib=true,isha=true;
    public static final String PREF_SILENT = "silent";
    private static final String SILENT_MODE_ACTION = "com.example.silentmode.action";
    PrayerTimeDatabase database;
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.silentpage);

        PrayerTimeDatabase database = new PrayerTimeDatabase(this);
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




        imageTurnnotificati1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (fajr) {
                    cancelAlarmForFajr();
                    fajr = false;
                } else {
                    enableFajrNotificatio();
                    fajr= true;
                    imageTurnnotificati1.setImageResource(R.drawable.img_turnnotificati);
                }
            }
        });
        imageTurnnotificati2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (dhuhr) {
                    cancelAlarmForDhuhr();
                    dhuhr= false;
                } else {
                    enableDhuhrNotification();
                    dhuhr= true;
                    imageTurnnotificati2.setImageResource(R.drawable.img_turnnotificati);
                }
            }
        });
        imageTurnnotificati3.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (asr) {
                    cancelAlarmForAsr();
                    asr= false;
                } else {
                    enableasrNotification();
                    dhuhr= true;
                    imageTurnnotificati3.setImageResource(R.drawable.img_turnnotificati);
                }
            }
        });
        imageTurnnotificati4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (maghrib) {
                    cancelAlarmForMaghrib();
                    maghrib= false;
                } else {
                    enablemaghribNotification();
                    maghrib= true;
                    imageTurnnotificati4.setImageResource(R.drawable.img_turnnotificati);
                }
            }
        });
        imageTurnnotificati5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isha) {
                    cancelAlarmForIsha();
                    isha= false;
                } else {
                    enableishaNotification();
                    isha= true;
                    imageTurnnotificati5.setImageResource(R.drawable.img_turnnotificati);
                }
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

    private void enableFajrNotificatio() {
        String fajrTime = prayerTimes.get(0);
        if (fajrTime != null) {

            long fajrTimeMillisecond = parsePrayerTimeToMilliseconds(fajrTime);
            Intent intent = new Intent(this, PrayerTimeBroadcast.class);
            intent.putExtra("prayerName", "Fajr");
            intent.putExtra("NotificationID", 0); // Unique notification ID
            PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_IMMUTABLE);

// Get the AlarmManager service
            AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

// Set the alarm to trigger the intent at the Dhuhr prayer time
            if (alarmManager != null) {
                // Set the alarm to trigger at the Dhuhr prayer time in milliseconds
                alarmManager.setExact(AlarmManager.RTC_WAKEUP, fajrTimeMillisecond, pendingIntent);
            }
            // Show a toast message or log that the Dhuhr notification is enabled
            Toast.makeText(this, "Fajr notification enabled", Toast.LENGTH_SHORT).show();
        } else {
            // Handle the case where Dhuhr time couldn't be fetched
            Toast.makeText(this, "Failed to fetch Fajr time", Toast.LENGTH_SHORT).show();
        }
    }


    private void enableasrNotification(){
        String asrTime = prayerTimes.get(3);
        if (asrTime != null) {

            long asrTimeMillisecond = parsePrayerTimeToMilliseconds(asrTime);
            Intent intent = new Intent(this, PrayerTimeBroadcast.class);
            intent.putExtra("prayerName", "Asr");
            intent.putExtra("NotificationID", 3); // Unique notification ID
            PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_IMMUTABLE);

// Get the AlarmManager service
            AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

            if (alarmManager != null) {

                alarmManager.setExact(AlarmManager.RTC_WAKEUP, asrTimeMillisecond, pendingIntent);
            }

            Toast.makeText(this, "Asr notification enabled", Toast.LENGTH_SHORT).show();
        } else {
            // Handle the case where Dhuhr time couldn't be fetched
            Toast.makeText(this, "Failed to fetch Asr time", Toast.LENGTH_SHORT).show();
        }
    }
    private void enablemaghribNotification()

    {
        String maghribTime = prayerTimes.get(4);
        if (maghribTime != null) {
            long maghribTimeMillisecond = parsePrayerTimeToMilliseconds(maghribTime);
            Intent intent = new Intent(this, PrayerTimeBroadcast.class);
            intent.putExtra("prayerName", "Maghrib");
            intent.putExtra("NotificationID", 4); // Unique notification ID
            PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_IMMUTABLE);

// Get the AlarmManager service
            AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

            if (alarmManager != null) {

                alarmManager.setExact(AlarmManager.RTC_WAKEUP, maghribTimeMillisecond, pendingIntent);
            }

            Toast.makeText(this, "Maghrib notification enabled", Toast.LENGTH_SHORT).show();
        } else {
            // Handle the case where Dhuhr time couldn't be fetched
            Toast.makeText(this, "Failed to fetch Maghrib time", Toast.LENGTH_SHORT).show();
        }
    }
    private void enableishaNotification()
    {
        String ishaTime = prayerTimes.get(5);
        if (ishaTime != null) {

            long ishaTimeMillisecond = parsePrayerTimeToMilliseconds(ishaTime);
            Intent intent = new Intent(this, PrayerTimeBroadcast.class);
            intent.putExtra("prayerName", "Isha");
            intent.putExtra("NotificationID", 5); // Unique notification ID
            PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_IMMUTABLE);

// Get the AlarmManager service
            AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

            if (alarmManager != null) {

                alarmManager.setExact(AlarmManager.RTC_WAKEUP, ishaTimeMillisecond, pendingIntent);
            }

            Toast.makeText(this, "Isha notification enabled", Toast.LENGTH_SHORT).show();
        } else {
            // Handle the case where Dhuhr time couldn't be fetched
            Toast.makeText(this, "Failed to fetch Isha time", Toast.LENGTH_SHORT).show();
        }
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
        // Fetch the Dhuhr prayer time from the database

        String dhuhrTime = prayerTimes.get(1);
        if (dhuhrTime != null) {
            // Convert the fetched Dhuhr time to milliseconds
            long dhuhrTimeMillisecond = parsePrayerTimeToMilliseconds(dhuhrTime);
            Intent intent = new Intent(this, PrayerTimeBroadcast.class);
            intent.putExtra("prayerName", "Dhuhr");
            intent.putExtra("NotificationID", 1); // Unique notification ID
            PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_IMMUTABLE);

// Get the AlarmManager service
            AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

// Set the alarm to trigger the intent at the Dhuhr prayer time
            if (alarmManager != null) {
                // Set the alarm to trigger at the Dhuhr prayer time in milliseconds
                alarmManager.setExact(AlarmManager.RTC_WAKEUP, dhuhrTimeMillisecond, pendingIntent);
            }
            // Show a toast message or log that the Dhuhr notification is enabled
            Toast.makeText(this, "Dhuhr notification enabled", Toast.LENGTH_SHORT).show();
        } else {
            // Handle the case where Dhuhr time couldn't be fetched
            Toast.makeText(this, "Failed to fetch Dhuhr time", Toast.LENGTH_SHORT).show();
        }
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








