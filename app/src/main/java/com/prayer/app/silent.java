package com.prayer.app;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
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

import androidx.appcompat.app.AppCompatActivity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
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



    private int startHour, startMinute, endHour, endMinute;
    ImageView imageTurnnotificati1, imageTurnnotificati2, imageTurnnotificati3, imageTurnnotificati4,back , home ,  imageTurnnotificati5;
    AlarmManager alarmManager;
    PendingIntent pendingIntent1, pendingIntent2, pendingIntent3, pendingIntent4, pendingIntent5;
    boolean fajr,dhuhr,asr,maghrib,isha;
    public static final String PREF_SILENT = "silent";
    private static final String SILENT_MODE_ACTION = "com.prayer.app.silent.action";
    SharedPreferences prefs ;

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
                    imageTurnnotificati1.setImageResource(R.drawable.img_turnoffnotification);
                } else {
                    enableFajrNotificatio();
                    fajr= true;
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
                    dhuhr= false;
                } else {
                    enableDhuhrNotification();
                    dhuhr= true;
                    imageTurnnotificati2.setImageResource(R.drawable.img_turnnotificati);
                }
                saveNotificationState("Dhuhr", dhuhr);

            }
        });
        imageTurnnotificati3.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (asr) {
                    cancelAlarmForAsr();
                    asr= false;
                } else {
                    enableasrNotification();
                    asr= true;
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
                    maghrib= false;
                } else {
                    enablemaghribNotification();
                    maghrib= true;
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
                    isha= false;
                } else {
                    enableishaNotification();
                    isha= true;
                    imageTurnnotificati5.setImageResource(R.drawable.img_turnnotificati);
                }
                saveNotificationState("Isha", isha);
            }
        });

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



    private void enableasrNotification(){
        saveNotificationState("Asr", true);
        Toast.makeText(this, "Asr notification enabled", Toast.LENGTH_SHORT).show();
        imageTurnnotificati3.setImageResource(R.drawable.img_turnnotificati);
    }

    private void enablemaghribNotification()

    {
        saveNotificationState("Maghrib", true);
        Toast.makeText(this, "Maghrib notification enabled", Toast.LENGTH_SHORT).show();
        imageTurnnotificati4.setImageResource(R.drawable.img_turnnotificati);


    }
    private void enableishaNotification()
    {
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
                        setSilentMode(startHour, startMinute, endHour,endMinute);
                    }
                }, hour, minute, true);

        timePickerDialog.show();
    }
    private Handler mHandler = new Handler();

    private void setSilentMode(final int startHour, final int startMinute, final int endHour, final int endMinute) {
        String startTime = String.format(Locale.getDefault(), "%02d:%02d", startHour, startMinute);
        String endTime = String.format(Locale.getDefault(), "%02d:%02d", endHour, endMinute);

        // Update TextView with both start and end times
        String silentTimeRange = "Start: " + startTime + " - End: " + endTime;
        tvSelectedTimes.setText(silentTimeRange);
        // Schedule a task to check the time every minute
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                checkTime(startHour, startMinute, endHour, endMinute);
                // Schedule the next check after one minute
                mHandler.postDelayed(this, 60000); // 60000 milliseconds = 1 minute
            }
        }, 60000); // Start the first check after one minute
    }

    private void checkTime(int startHour, int startMinute, int endHour, int endMinute) {
        // Get the current time
        Calendar currentTime = Calendar.getInstance();
        int currentHour = currentTime.get(Calendar.HOUR_OF_DAY);
        int currentMinute = currentTime.get(Calendar.MINUTE);

        // Check if current time matches the start time
        if (currentHour == startHour && currentMinute == startMinute) {
            // Enable silent mode
            AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
            audioManager.setRingerMode(AudioManager.RINGER_MODE_SILENT);
            Toast.makeText(this, "Silent mode enabled", Toast.LENGTH_SHORT).show();
        }
        // Check if current time matches the end time
        else if (currentHour == endHour && currentMinute == endMinute) {
            // Disable silent mode
            AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
            audioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
            Toast.makeText(this, "Silent mode disabled", Toast.LENGTH_SHORT).show();
        }
    }




    // Calculate duration in milliseconds from start time to end time
    private long calculateDuration(int startHour, int startMinute, int endHour, int endMinute) {
        long startMillis = convertTimeToMillis(startHour, startMinute);
        long endMillis = convertTimeToMillis(endHour, endMinute);

        // Calculate duration
        long duration = endMillis - startMillis;
        if (duration < 0) {
            // If the end time is before the start time, add 24 hours
            duration += 24 * 60 * 60 * 1000; // 24 hours in milliseconds
        }
        return duration;
    }

    // Convert time to milliseconds
    private long convertTimeToMillis(int hour, int minute) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTimeInMillis();
    }
}







