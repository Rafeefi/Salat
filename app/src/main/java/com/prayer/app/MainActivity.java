package com.prayer.app;


import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.app.PendingIntent.FLAG_IMMUTABLE;
import static android.app.PendingIntent.FLAG_UPDATE_CURRENT;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.github.msarhan.ummalqura.calendar.UmmalquraCalendar;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.TimeZone;

public class MainActivity extends AppCompatActivity {

    Button calculatePrayTimes;
    ArrayList<PrayerTime> list;
    RecyclerView times;
    RecyclerView.Adapter adapter;
    LocationManager locationManager;
    TextView date, dateHijri;
    double latitude;
    double longitude;
    private SharedPreferences.OnSharedPreferenceChangeListener listener;
    View settings;
    View compass;
    SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Prayer Reminder Notifications";
            String description = "Notifications for Prayer Times";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel("PRAYER_REMINDER_CHANNEL", name, importance);
            channel.setDescription(description);
            channel.enableVibration(true);
            channel.setVibrationPattern(new long[]{1000, 1000, 1000, 1000, 1000}); // Example pattern
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
        setContentView(R.layout.activity_home);
        date = findViewById(R.id.txtDate);
        dateHijri = findViewById(R.id.txtDateHijri);
        settings = findViewById(R.id.imageSearch);
        compass = findViewById(R.id.imageCompass);
        // For Gregorian Calendar
        Calendar gregorianCalendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        String formattedGregorianDate = dateFormat.format(gregorianCalendar.getTime());
        date.setText(formattedGregorianDate);

        // For Hijri Calendar
        UmmalquraCalendar ummalquraCalendar = new UmmalquraCalendar();
        int day = ummalquraCalendar.get(Calendar.DAY_OF_MONTH);
        int month = ummalquraCalendar.get(Calendar.MONTH) + 1; // Java months are zero-based
        int year = ummalquraCalendar.get(UmmalquraCalendar.YEAR);
        String formattedHijriDate = String.format(Locale.getDefault(), "%02d/%02d/%04d", day, month, year);
        dateHijri.setText(formattedHijriDate);
        //Runtime permissions
        if (ContextCompat.checkSelfPermission(MainActivity.this, ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{
                    ACCESS_FINE_LOCATION
            }, 100);
        }
        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = prefs.edit();


        refrechTimes();
        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToSettings();
            }
        });
        compass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToCompass();
            }
        });
    }


    private void refrechTimes() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = prefs.edit();

        PrayTime prayers = new PrayTime();
        latitude = Double.parseDouble(prefs.getString("LATITUDE", "24.7248398"));
        longitude = Double.parseDouble(prefs.getString("LONGITUDE", "46.6384049"));
        double timezone = prayers.getBaseTimeZone();

        String s1 = prefs.getString(getString(R.string.juristic), "0");
        String s2 = prefs.getString(getString(R.string.calculation), "4");
        String s3 = prefs.getString(getString(R.string.latitude), "0");
        String s4 = prefs.getString(getString(R.string.time), "1");


        int RG1;
        int RG2;
        int RG3;
        int RG4 = 0; // just intilize
        if (!(s1.equals("") && s2.equals("") && s3.equals("") && s4.equals(""))) {
            RG1 = Integer.parseInt(s1);
            RG2 = Integer.parseInt(s2);
            RG3 = (int) Math.round(latitude); //because we didn't implement manage location yet
            RG4 = Integer.parseInt(s4);

            prayers.setTimeFormat(RG4);//time
            prayers.setCalcMethod(RG2);
            prayers.setAsrJuristic(RG1);
            prayers.setAdjustHighLats(RG3);// same problem here
        } else {
            prayers.setTimeFormat(1);
            prayers.setCalcMethod(4);
            prayers.setAsrJuristic(0);
            prayers.setAdjustHighLats(0);
        }


        Date now = new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(now);

        int[] offsets = {0, 0, 0, 0, 0, 0, 0}; // {Fajr,Sunrise,Dhuhr,Asr,Sunset,Maghrib,Isha}
        prayers.tune(offsets);

        ArrayList<String> prayerTimes = prayers.getPrayerTimes(cal, latitude, longitude, timezone);


        PrayerTime fajer = new PrayerTime();
        PrayerTime Sunrise = new PrayerTime();
        PrayerTime Duhur = new PrayerTime();
        PrayerTime asser = new PrayerTime();
        PrayerTime magrib = new PrayerTime();
        PrayerTime sunset = new PrayerTime();
        PrayerTime isha = new PrayerTime();

        ArrayList<String> prayerNames = prayers.getTimeNames();

        times = (RecyclerView) findViewById(R.id.recyclerHome);
        list = new ArrayList<PrayerTime>();


        Date nowtime = new Date();
        SimpleDateFormat TIME_FORMAT = new SimpleDateFormat("HH");
        String time = TIME_FORMAT.format(nowtime);

        int timeInt = Integer.parseInt(time);
        int next = 0;
        double nextFloat = 0;
        boolean isNext = true;


        for (int i = 0; i < prayerTimes.size(); i++) {
            if (i == 0) {
                if (RG4 == 0) {
                    fajer.setName(prayerNames.get(i));
                    fajer.setTime(prayerTimes.get(i));
                    String p = prayerTimes.get(i);
                    String nextPrayer = p.substring(0, 2);
                    int hour = Integer.parseInt(nextPrayer);
                    if (timeInt > hour && timeInt < 20) {
                        fajer.setNext(false);
                    } else if (next < hour && isNext) {
                        fajer.setNext(true);
                        next = hour;
                        isNext = false;
                    } else {
                        fajer.setNext(false);
                    }
                } else if (RG4 == 3) {
                    fajer.setName(prayerNames.get(i));
                    fajer.setTime(prayerTimes.get(i).substring(0, 5));
                    String p = prayerTimes.get(i).substring(0, 5);
                    double hour = Double.parseDouble(p);
                    if (timeInt > hour && timeInt < 20) {
                        fajer.setNext(false);
                    } else if (nextFloat < hour && isNext) {
                        fajer.setNext(true);
                        nextFloat = hour;
                        isNext = false;
                    } else {
                        fajer.setNext(false);
                    }
                } else {
                    fajer.setName(prayerNames.get(i));
                    fajer.setTime(prayerTimes.get(i));
                }
                list.add(fajer);
            }
            if (i == 1) {
                if (RG4 == 0) {
                    Sunrise.setName(prayerNames.get(i));
                    Sunrise.setTime(prayerTimes.get(i));
                    String p = prayerTimes.get(i);
                    String nextPrayer = p.substring(0, 2);
                    int hour = Integer.parseInt(nextPrayer);
                    if (timeInt > hour) {
                        Sunrise.setNext(false);
                    } else if (next < hour && isNext) {
                        Sunrise.setNext(true);
                        next = hour;
                        isNext = false;
                    } else {
                        Sunrise.setNext(false);
                    }
                } else if (RG4 == 3) {
                    Sunrise.setName(prayerNames.get(i));
                    Sunrise.setTime(prayerTimes.get(i).substring(0, 5));
                    String p = prayerTimes.get(i).substring(0, 5);
                    double hour = Double.parseDouble(p);
                    if (timeInt > hour) {
                        Sunrise.setNext(false);
                    } else if (nextFloat < hour && isNext) {
                        Sunrise.setNext(true);
                        nextFloat = hour;
                        isNext = false;
                    } else {
                        Sunrise.setNext(false);
                    }
                } else {
                    Sunrise.setName(prayerNames.get(i));
                    Sunrise.setTime(prayerTimes.get(i));
                }
                list.add(Sunrise);
            }
            if (i == 2) {
                if (RG4 == 0) {
                    Duhur.setName(prayerNames.get(i));
                    Duhur.setTime(prayerTimes.get(i));
                    String p = prayerTimes.get(i);
                    String nextPrayer = p.substring(0, 2);
                    int hour = Integer.parseInt(nextPrayer);
                    if (timeInt > hour) {
                        Duhur.setNext(false);
                    } else if (next < hour && isNext) {
                        Duhur.setNext(true);
                        next = hour;
                        isNext = false;
                    } else {
                        Duhur.setNext(false);
                    }
                } else if (RG4 == 3) {
                    Duhur.setName(prayerNames.get(i));
                    Duhur.setTime(prayerTimes.get(i).substring(0, 5));
                    String p = prayerTimes.get(i).substring(0, 5);
                    double hour = Double.parseDouble(p);
                    if (timeInt > hour) {
                        Duhur.setNext(false);
                    } else if (nextFloat < hour && isNext) {
                        Duhur.setNext(true);
                        nextFloat = hour;
                        isNext = false;
                    } else {
                        Duhur.setNext(false);
                    }
                } else {
                    Duhur.setName(prayerNames.get(i));
                    Duhur.setTime(prayerTimes.get(i));
                }
                list.add(Duhur);
            }
            if (i == 3) {
                if (RG4 == 0) {
                    asser.setName(prayerNames.get(i));
                    asser.setTime(prayerTimes.get(i));
                    String p = prayerTimes.get(i);
                    String nextPrayer = p.substring(0, 2);
                    int hour = Integer.parseInt(nextPrayer);
                    if (timeInt > hour) {
                        asser.setNext(false);
                    } else if (next < hour && isNext) {
                        asser.setNext(true);
                        next = hour;
                        isNext = false;
                    } else {
                        asser.setNext(false);
                    }
                } else if (RG4 == 3) {
                    asser.setName(prayerNames.get(i));
                    asser.setTime(prayerTimes.get(i).substring(0, 5));
                    String p = prayerTimes.get(i).substring(0, 5);
                    double hour = Double.parseDouble(p);
                    if (timeInt > hour) {
                        asser.setNext(false);
                    } else if (nextFloat < hour && isNext) {
                        asser.setNext(true);
                        nextFloat = hour;
                        isNext = false;
                    } else {
                        asser.setNext(false);
                    }
                } else {
                    asser.setName(prayerNames.get(i));
                    asser.setTime(prayerTimes.get(i));
                }
                list.add(asser);
            }

            if (i == 5) {
                if (RG4 == 0) {
                    magrib.setName(prayerNames.get(i));
                    magrib.setTime(prayerTimes.get(i));
                    String p = prayerTimes.get(i);
                    String nextPrayer = p.substring(0, 2);
                    int hour = Integer.parseInt(nextPrayer);
                    if (timeInt > hour) {
                        magrib.setNext(false);
                    } else if (next < hour && isNext) {
                        magrib.setNext(true);
                        next = hour;
                        isNext = false;
                    } else {
                        magrib.setNext(false);
                    }
                } else if (RG4 == 3) {
                    magrib.setName(prayerNames.get(i));
                    magrib.setTime(prayerTimes.get(i).substring(0, 5));
                    String p = prayerTimes.get(i).substring(0, 5);
                    double hour = Double.parseDouble(p);
                    if (timeInt > hour) {
                        magrib.setNext(false);
                    } else if (nextFloat < hour && isNext) {
                        magrib.setNext(true);
                        nextFloat = hour;
                        isNext = false;
                    } else {
                        magrib.setNext(false);
                    }
                } else {
                    magrib.setName(prayerNames.get(i));
                    magrib.setTime(prayerTimes.get(i));
                }
                list.add(magrib);
            }
            if (i == 6) {
                if (RG4 == 0) {
                    isha.setName(prayerNames.get(i));
                    isha.setTime(prayerTimes.get(i));
                    String p = prayerTimes.get(i);
                    String nextPrayer = p.substring(0, 2);
                    int hour = Integer.parseInt(nextPrayer);
                    if (timeInt > hour) {
                        isha.setNext(false);
                    } else if (next < hour && isNext) {
                        isha.setNext(true);
                        next = hour;
                        isNext = false;
                    } else {
                        isha.setNext(false);
                    }
                } else if (RG4 == 3) {
                    isha.setName(prayerNames.get(i));
                    isha.setTime(prayerTimes.get(i).substring(0, 5));
                    String p = prayerTimes.get(i).substring(0, 5);
                    double hour = Double.parseDouble(p);
                    System.out.println(hour);
                    if (timeInt > hour) {
                        isha.setNext(false);
                    } else if (nextFloat < hour && isNext) {
                        isha.setNext(true);
                        nextFloat = hour;
                        isNext = false;
                    } else {
                        isha.setNext(false);
                    }
                } else {
                    isha.setName(prayerNames.get(i));
                    isha.setTime(prayerTimes.get(i));
                }
                list.add(isha);
            }
        }
        times.setHasFixedSize(true);

        adapter = new TimeViewAdapter(MainActivity.this, list);

        times.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        // get the current date (today) in yyyy/MM/dd format
        Date today = new Date();
        SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy/MM/dd ");
        String date = DATE_FORMAT.format(today);

        /*
         * the user will get notified based on what prayer is upcoming next
         */

        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        for (int i = 0; i < list.size(); i++) {
            if (i == 1 || i == 5) {
                continue;
            }
            String prayerName = "Fajr";
            if (i == 2)
                prayerName = "Dhuhr";
            if (i == 3)
                prayerName = "Asr";
            if (i == 4)
                prayerName = "Maghrib";
            if (i == 6)
                prayerName = "Isha";


            // Check if notifications for this prayer are enabled
            boolean isEnabled = prefs.getBoolean(prayerName + "_notification", true); // Default is true
            //Generate a pending intent to be used later
            if (isEnabled) {
                Intent intent = new Intent(this, PrayerTimeReminderReceiver.class);
                intent.putExtra("NotificationID", prayerName.hashCode()); // Use hash code of prayer name as ID
                intent.putExtra("prayerName", prayerName); // Pass prayer name to customize notification text
                PendingIntent pendingIntent = PendingIntent.getBroadcast(this, prayerName.hashCode(), intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
                AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

                String timeInHours = "";
                if (RG4 == 0) {
                    timeInHours = list.get(i).getTime().concat(":00");
                } else if (RG4 == 3) {
                    String c = list.get(i).getTime();
                    String t = c.substring(0, c.indexOf("."));
                    if (Integer.parseInt(t) < 10) {
                        String time1 = "0" + t + ":" + c.substring(c.indexOf(".") + 1, c.indexOf(".") + 3) + ":00";
                        timeInHours = time1;
                    } else {
                        String time1 = t + ":" + c.substring(c.indexOf(".") + 1, c.indexOf(".") + 3) + ":00";
                        timeInHours = time1;
                    }
                }
                //set the alarm as hh:mm:ss
                if (list.get(i).isNext()) {
                    Toast.makeText(MainActivity.this, "alarm is set at " + timeInHours, Toast.LENGTH_SHORT).show();
                    String myDate = date.concat(timeInHours);
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                    Date finalDate = null;
                    try {
                        finalDate = sdf.parse(myDate);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                    if (finalDate != null) {
                        long timeInMillis = finalDate.getTime(); // get time in milliseconds
                        if (alarmManager != null) {
                            if (timeInMillis <= System.currentTimeMillis()) {
                                // Skip setting the alarm or add one day to 'finalDate' depending on your need
                                timeInMillis += 24 * 60 * 60 * 1000; // Uncomment to add a day
                                //  continue; // Skip setting the alarm
                            }
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                                if (alarmManager.canScheduleExactAlarms()) {
                                    alarmManager.setExact(AlarmManager.RTC_WAKEUP, timeInMillis, pendingIntent);
                                } else {
                                    // Request permission from the user
                                    intent = new Intent(android.provider.Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM);
                                    startActivity(intent);
                                }
                            } else {
                                // For older versions, just set the alarm as you normally would
                                alarmManager.setExact(AlarmManager.RTC_WAKEUP, timeInMillis, pendingIntent);
                            }
                        }
                    }
                }
            } else {
                // If notifications are disabled for this prayer, you might want to cancel any existing ones.
                Intent intent = new Intent(this, PrayerTimeReminderReceiver.class);
                PendingIntent pendingIntent = PendingIntent.getBroadcast(this, prayerName.hashCode(), intent, PendingIntent.FLAG_NO_CREATE | FLAG_IMMUTABLE);
                if (pendingIntent != null) {
                    AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
                    if (alarmManager != null) {
                        alarmManager.cancel(pendingIntent);
                        pendingIntent.cancel(); // Cancel the PendingIntent after canceling the alarm
                    }
                }
            }
        }

    }


    private void goToSettings() {
        Intent intent = new Intent(this, Settings.class);
        startActivity(intent);
    }

    private void goToCompass() {
        Intent intent = new Intent(this, CompassActivity.class);
        startActivity(intent);
    }
}
class PrayTime {

    // ---------------------- Global Variables --------------------
    private int calcMethod; // caculation method
    private int asrJuristic; // Juristic method for Asr
    private int dhuhrMinutes; // minutes after mid-day for Dhuhr
    private int adjustHighLats; // adjusting method for higher latitudes
    private int timeFormat; // time format
    private double lat; // latitude
    private double lng; // longitude
    private double timeZone; // time-zone
    private double JDate; // Julian date
    // ------------------------------------------------------------
    // Calculation Methods
    private int Jafari; // Ithna Ashari
    private int Karachi; // University of Islamic Sciences, Karachi
    private int ISNA; // Islamic Society of North America (ISNA)
    private int MWL; // Muslim World League (MWL)
    private int Makkah; // Umm al-Qura, Makkah
    private int Egypt; // Egyptian General Authority of Survey
    private int Custom; // Custom Setting
    private int Tehran; // Institute of Geophysics, University of Tehran
    // Juristic Methods
    private int Shafii; // Shafii (standard)
    private int Hanafi; // Hanafi
    // Adjusting Methods for Higher Latitudes
    private int None; // No adjustment
    private int MidNight; // middle of night
    private int OneSeventh; // 1/7th of night
    private int AngleBased; // angle/60th of night
    // Time Formats
    private int Time24; // 24-hour format
    private int Time12; // 12-hour format
    private int Time12NS; // 12-hour format with no suffix
    private int Floating; // floating point number
    // Time Names
    private ArrayList<String> timeNames;
    private String InvalidTime; // The string used for invalid times
    // --------------------- Technical Settings --------------------
    private int numIterations; // number of iterations needed to compute times
    // ------------------- Calc Method Parameters --------------------
    private HashMap<Integer, double[]> methodParams;

    /*
     * this.methodParams[methodNum] = new Array(fa, ms, mv, is, iv);
     *
     * fa : fajr angle ms : maghrib selector (0 = angle; 1 = minutes after
     * sunset) mv : maghrib parameter value (in angle or minutes) is : isha
     * selector (0 = angle; 1 = minutes after maghrib) iv : isha parameter value
     * (in angle or minutes)
     */
    private double[] prayerTimesCurrent;
    private int[] offsets;

    public PrayTime() {
        // Initialize vars
        //   settings_Activity s =new settings_Activity();

       /* this.setAsrJuristic(0);
        this.setCalcMethod(4);
        this.setAdjustHighLats(0);
        this.setTimeFormat(1);
   */
        this.setAsrJuristic(1);
        this.setCalcMethod(4);
        this.setAdjustHighLats(0);
        this.setTimeFormat(1);

        //////////////////////////////////////////////
        this.setDhuhrMinutes(0);


        // Calculation Methods
        this.setJafari(0); // Ithna Ashari
        this.setKarachi(1); // University of Islamic Sciences, Karachi
        this.setISNA(2); // Islamic Society of North America (ISNA)
        this.setMWL(3); // Muslim World League (MWL)
        this.setMakkah(4); // Umm al-Qura, Makkah
        this.setEgypt(5); // Egyptian General Authority of Survey
        this.setTehran(6); // Institute of Geophysics, University of Tehran
        this.setCustom(7); // Custom Setting

        // Juristic Methods
        this.setShafii(0); // Shafii (standard)
        this.setHanafi(1); // Hanafi

        // Adjusting Methods for Higher Latitudes
        this.setNone(0); // No adjustment
        this.setMidNight(1); // middle of night
        this.setOneSeventh(2); // 1/7th of night
        this.setAngleBased(3); // angle/60th of night

        // Time Formats
        this.setTime24(0); // 24-hour format
        this.setTime12(1); // 12-hour format
        this.setTime12NS(2); // 12-hour format with no suffix
        this.setFloating(3); // floating point number

        // Time Names
        timeNames = new ArrayList<String>();
        timeNames.add("Fajr");
        timeNames.add("Sunrise");
        timeNames.add("Dhuhr");
        timeNames.add("Asr");
        timeNames.add("Sunset");
        timeNames.add("Maghrib");
        timeNames.add("Isha");

        InvalidTime = "-----"; // The string used for invalid times

        // --------------------- Technical Settings --------------------

        this.setNumIterations(1); // number of iterations needed to compute
        // times

        // ------------------- Calc Method Parameters --------------------

        // Tuning offsets {fajr, sunrise, dhuhr, asr, sunset, maghrib, isha}
        offsets = new int[7];
        offsets[0] = 0;
        offsets[1] = 0;
        offsets[2] = 0;
        offsets[3] = 0;
        offsets[4] = 0;
        offsets[5] = 0;
        offsets[6] = 0;

        /*
         *
         * fa : fajr angle ms : maghrib selector (0 = angle; 1 = minutes after
         * sunset) mv : maghrib parameter value (in angle or minutes) is : isha
         * selector (0 = angle; 1 = minutes after maghrib) iv : isha parameter
         * value (in angle or minutes)
         */
        methodParams = new HashMap<Integer, double[]>();

        // Jafari
        double[] Jvalues = {16, 0, 4, 0, 14};
        methodParams.put(Integer.valueOf(this.getJafari()), Jvalues);

        // Karachi
        double[] Kvalues = {18, 1, 0, 0, 18};
        methodParams.put(Integer.valueOf(this.getKarachi()), Kvalues);

        // ISNA
        double[] Ivalues = {15, 1, 0, 0, 15};
        methodParams.put(Integer.valueOf(this.getISNA()), Ivalues);

        // MWL
        double[] MWvalues = {18, 1, 0, 0, 17};
        methodParams.put(Integer.valueOf(this.getMWL()), MWvalues);

        // Makkah
        double[] MKvalues = {18.5, 1, 0, 1, 90};
        methodParams.put(Integer.valueOf(this.getMakkah()), MKvalues);

        // Egypt
        double[] Evalues = {19.5, 1, 0, 0, 17.5};
        methodParams.put(Integer.valueOf(this.getEgypt()), Evalues);

        // Tehran
        double[] Tvalues = {17.7, 0, 4.5, 0, 14};
        methodParams.put(Integer.valueOf(this.getTehran()), Tvalues);

        // Custom
        double[] Cvalues = {18, 1, 0, 0, 17};
        methodParams.put(Integer.valueOf(this.getCustom()), Cvalues);

    }

    // ---------------------- Trigonometric Functions -----------------------
    // range reduce angle in degrees.
    public double fixangle(double a) {

        a = a - (360 * (Math.floor(a / 360.0)));

        a = a < 0 ? (a + 360) : a;

        return a;
    }

    // range reduce hours to 0..23
    public double fixhour(double a) {
        a = a - 24.0 * Math.floor(a / 24.0);
        a = a < 0 ? (a + 24) : a;
        return a;
    }

    // radian to degree
    public double radiansToDegrees(double alpha) {
        return ((alpha * 180.0) / Math.PI);
    }

    // deree to radian
    public double DegreesToRadians(double alpha) {
        return ((alpha * Math.PI) / 180.0);
    }

    // degree sin
    public double dsin(double d) {
        return (Math.sin(DegreesToRadians(d)));
    }

    // degree cos
    public double dcos(double d) {
        return (Math.cos(DegreesToRadians(d)));
    }

    // degree tan
    public double dtan(double d) {
        return (Math.tan(DegreesToRadians(d)));
    }

    // degree arcsin
    public double darcsin(double x) {
        double val = Math.asin(x);
        return radiansToDegrees(val);
    }

    // degree arccos
    public double darccos(double x) {
        double val = Math.acos(x);
        return radiansToDegrees(val);
    }

    // degree arctan
    public double darctan(double x) {
        double val = Math.atan(x);
        return radiansToDegrees(val);
    }

    // degree arctan2
    public double darctan2(double y, double x) {
        double val = Math.atan2(y, x);
        return radiansToDegrees(val);
    }

    // degree arccot
    public double darccot(double x) {
        double val = Math.atan2(1.0, x);
        return radiansToDegrees(val);
    }

    // ---------------------- Time-Zone Functions -----------------------
    // compute local time-zone for a specific date
    public double getTimeZone1() {
        TimeZone timez = TimeZone.getDefault();
        double hoursDiff = (timez.getRawOffset() / 1000.0) / 3600;
        return hoursDiff;
    }

    // compute base time-zone of the system
    public double getBaseTimeZone() {
        TimeZone timez = TimeZone.getDefault();
        double hoursDiff = (timez.getRawOffset() / 1000.0) / 3600;
        return hoursDiff;
    }

    // detect daylight saving in a given date
    public double detectDaylightSaving() {
        TimeZone timez = TimeZone.getDefault();
        double hoursDiff = timez.getDSTSavings();
        return hoursDiff;
    }

    // ---------------------- Julian Date Functions -----------------------
    // calculate julian date from a calendar date
    public double julianDate(int year, int month, int day) {

        if (month <= 2) {
            year -= 1;
            month += 12;
        }
        double A = Math.floor(year / 100.0);

        double B = 2 - A + Math.floor(A / 4.0);

        double JD = Math.floor(365.25 * (year + 4716))
                + Math.floor(30.6001 * (month + 1)) + day + B - 1524.5;

        return JD;
    }

    // convert a calendar date to julian date (second method)
    public double calcJD(int year, int month, int day) {
        double J1970 = 2440588.0;
        Date date = new Date(year, month - 1, day);

        double ms = date.getTime(); // # of milliseconds since midnight Jan 1,
        // 1970
        double days = Math.floor(ms / (1000.0 * 60.0 * 60.0 * 24.0));
        return J1970 + days - 0.5;

    }

    // ---------------------- Calculation Functions -----------------------
    // References:
    // http://www.ummah.net/astronomy/saltime
    // http://aa.usno.navy.mil/faq/docs/SunApprox.html
    // compute declination angle of sun and equation of time
    public double[] sunPosition(double jd) {

        double D = jd - 2451545;
        double g = fixangle(357.529 + 0.98560028 * D);
        double q = fixangle(280.459 + 0.98564736 * D);
        double L = fixangle(q + (1.915 * dsin(g)) + (0.020 * dsin(2 * g)));

        // double R = 1.00014 - 0.01671 * [self dcos:g] - 0.00014 * [self dcos:
        // (2*g)];
        double e = 23.439 - (0.00000036 * D);
        double d = darcsin(dsin(e) * dsin(L));
        double RA = (darctan2((dcos(e) * dsin(L)), (dcos(L)))) / 15.0;
        RA = fixhour(RA);
        double EqT = q / 15.0 - RA;
        double[] sPosition = new double[2];
        sPosition[0] = d;
        sPosition[1] = EqT;

        return sPosition;
    }

    // compute equation of time
    public double equationOfTime(double jd) {
        double eq = sunPosition(jd)[1];
        return eq;
    }

    // compute declination angle of sun
    public double sunDeclination(double jd) {
        double d = sunPosition(jd)[0];
        return d;
    }

    // compute mid-day (Dhuhr, Zawal) time
    public double computeMidDay(double t) {
        double T = equationOfTime(this.getJDate() + t);
        double Z = fixhour(12 - T);
        return Z;
    }

    // compute time for a given angle G
    public double computeTime(double G, double t) {

        double D = sunDeclination(this.getJDate() + t);
        double Z = computeMidDay(t);
        double Beg = -dsin(G) - dsin(D) * dsin(this.getLat());
        double Mid = dcos(D) * dcos(this.getLat());
        double V = darccos(Beg / Mid) / 15.0;

        return Z + (G > 90 ? -V : V);
    }

    // compute the time of Asr
    // Shafii: step=1, Hanafi: step=2
    public double computeAsr(double step, double t) {
        double D = sunDeclination(this.getJDate() + t);
        double G = -darccot(step + dtan(Math.abs(this.getLat() - D)));
        return computeTime(G, t);
    }

    // ---------------------- Misc Functions -----------------------
    // compute the difference between two times
    public double timeDiff(double time1, double time2) {
        return fixhour(time2 - time1);
    }

    // -------------------- Interface Functions --------------------
    // return prayer times for a given date
    public ArrayList<String> getDatePrayerTimes(int year, int month, int day,
                                                double latitude, double longitude, double tZone) {
        this.setLat(latitude);
        this.setLng(longitude);
        this.setTimeZone(tZone);
        this.setJDate(julianDate(year, month, day));
        double lonDiff = longitude / (15.0 * 24.0);
        this.setJDate(this.getJDate() - lonDiff);
        return computeDayTimes();
    }

    // return prayer times for a given date
    public ArrayList<String> getPrayerTimes(Calendar date, double latitude,
                                            double longitude, double tZone) {

        int year = date.get(Calendar.YEAR);
        int month = date.get(Calendar.MONTH);
        int day = date.get(Calendar.DATE);

        return getDatePrayerTimes(year, month + 1, day, latitude, longitude, tZone);
    }

    // set custom values for calculation parameters
    public void setCustomParams(double[] params) {

        for (int i = 0; i < 5; i++) {
            if (params[i] == -1) {
                params[i] = methodParams.get(this.getCalcMethod())[i];
                methodParams.put(this.getCustom(), params);
            } else {
                methodParams.get(this.getCustom())[i] = params[i];
            }
        }
        this.setCalcMethod(this.getCustom());
    }

    // set the angle for calculating Fajr
    public void setFajrAngle(double angle) {
        double[] params = {angle, -1, -1, -1, -1};
        setCustomParams(params);
    }

    // set the angle for calculating Maghrib
    public void setMaghribAngle(double angle) {
        double[] params = {-1, 0, angle, -1, -1};
        setCustomParams(params);

    }

    // set the angle for calculating Isha
    public void setIshaAngle(double angle) {
        double[] params = {-1, -1, -1, 0, angle};
        setCustomParams(params);

    }

    // set the minutes after Sunset for calculating Maghrib
    public void setMaghribMinutes(double minutes) {
        double[] params = {-1, 1, minutes, -1, -1};
        setCustomParams(params);

    }

    // set the minutes after Maghrib for calculating Isha
    public void setIshaMinutes(double minutes) {
        double[] params = {-1, -1, -1, 1, minutes};
        setCustomParams(params);

    }

    // convert double hours to 24h format
    public String floatToTime24(double time) {

        String result;

        if (Double.isNaN(time)) {
            return InvalidTime;
        }

        time = fixhour(time + 0.5 / 60.0); // add 0.5 minutes to round
        int hours = (int) Math.floor(time);
        double minutes = Math.floor((time - hours) * 60.0);

        if ((hours >= 0 && hours <= 9) && (minutes >= 0 && minutes <= 9)) {
            result = "0" + hours + ":0" + Math.round(minutes);
        } else if ((hours >= 0 && hours <= 9)) {
            result = "0" + hours + ":" + Math.round(minutes);
        } else if ((minutes >= 0 && minutes <= 9)) {
            result = hours + ":0" + Math.round(minutes);
        } else {
            result = hours + ":" + Math.round(minutes);
        }
        return result;
    }

    // convert double hours to 12h format
    public String floatToTime12(double time, boolean noSuffix) {

        if (Double.isNaN(time)) {
            return InvalidTime;
        }

        time = fixhour(time + 0.5 / 60); // add 0.5 minutes to round
        int hours = (int) Math.floor(time);
        double minutes = Math.floor((time - hours) * 60);
        String suffix, result;
        if (hours >= 12) {
            suffix = "pm";
        } else {
            suffix = "am";
        }
        hours = ((((hours + 12) - 1) % (12)) + 1);
        /*hours = (hours + 12) - 1;
        int hrs = (int) hours % 12;
        hrs += 1;*/
        if (noSuffix == false) {
            if ((hours >= 0 && hours <= 9) && (minutes >= 0 && minutes <= 9)) {
                result = "0" + hours + ":0" + Math.round(minutes) + " "
                        + suffix;
            } else if ((hours >= 0 && hours <= 9)) {
                result = "0" + hours + ":" + Math.round(minutes) + " " + suffix;
            } else if ((minutes >= 0 && minutes <= 9)) {
                result = hours + ":0" + Math.round(minutes) + " " + suffix;
            } else {
                result = hours + ":" + Math.round(minutes) + " " + suffix;
            }

        } else {
            if ((hours >= 0 && hours <= 9) && (minutes >= 0 && minutes <= 9)) {
                result = "0" + hours + ":0" + Math.round(minutes);
            } else if ((hours >= 0 && hours <= 9)) {
                result = "0" + hours + ":" + Math.round(minutes);
            } else if ((minutes >= 0 && minutes <= 9)) {
                result = hours + ":0" + Math.round(minutes);
            } else {
                result = hours + ":" + Math.round(minutes);
            }
        }
        return result;

    }

    // convert double hours to 12h format with no suffix
    public String floatToTime12NS(double time) {
        return floatToTime12(time, true);
    }

    // ---------------------- Compute Prayer Times -----------------------
    // compute prayer times at given julian date
    private double[] computeTimes(double[] times) {

        double[] t = dayPortion(times);

        double Fajr = this.computeTime(
                180 - methodParams.get(this.getCalcMethod())[0], t[0]);

        double Sunrise = this.computeTime(180 - 0.833, t[1]);

        double Dhuhr = this.computeMidDay(t[2]);
        double Asr = this.computeAsr(1 + this.getAsrJuristic(), t[3]);
        double Sunset = this.computeTime(0.833, t[4]);

        double Maghrib = this.computeTime(
                methodParams.get(this.getCalcMethod())[2], t[5]);
        double Isha = this.computeTime(
                methodParams.get(this.getCalcMethod())[4], t[6]);

        double[] CTimes = {Fajr, Sunrise, Dhuhr, Asr, Sunset, Maghrib, Isha};

        return CTimes;

    }

    // compute prayer times at given julian date
    private ArrayList<String> computeDayTimes() {
        double[] times = {5, 6, 12, 13, 18, 18, 18}; // default times

        for (int i = 1; i <= this.getNumIterations(); i++) {
            times = computeTimes(times);
        }

        times = adjustTimes(times);
        times = tuneTimes(times);

        return adjustTimesFormat(times);
    }

    // adjust times in a prayer time array
    private double[] adjustTimes(double[] times) {
        for (int i = 0; i < times.length; i++) {
            times[i] += this.getTimeZone() - this.getLng() / 15;
        }

        times[2] += this.getDhuhrMinutes() / 60; // Dhuhr
        if (methodParams.get(this.getCalcMethod())[1] == 1) // Maghrib
        {
            times[5] = times[4] + methodParams.get(this.getCalcMethod())[2] / 60;
        }
        if (methodParams.get(this.getCalcMethod())[3] == 1) // Isha
        {
            times[6] = times[5] + methodParams.get(this.getCalcMethod())[4] / 60;
        }

        if (this.getAdjustHighLats() != this.getNone()) {
            times = adjustHighLatTimes(times);
        }

        return times;
    }

    // convert times array to given time format
    private ArrayList<String> adjustTimesFormat(double[] times) {

        ArrayList<String> result = new ArrayList<String>();

        if (this.getTimeFormat() == this.getFloating()) {
            for (double time : times) {
                result.add(String.valueOf(time));
            }
            return result;
        }

        for (int i = 0; i < 7; i++) {
            if (this.getTimeFormat() == this.getTime12()) {
                result.add(floatToTime12(times[i], false));
            } else if (this.getTimeFormat() == this.getTime12NS()) {
                result.add(floatToTime12(times[i], true));
            } else {
                result.add(floatToTime24(times[i]));
            }
        }
        return result;
    }

    // adjust Fajr, Isha and Maghrib for locations in higher latitudes
    private double[] adjustHighLatTimes(double[] times) {
        double nightTime = timeDiff(times[4], times[1]); // sunset to sunrise

        // Adjust Fajr
        double FajrDiff = nightPortion(methodParams.get(this.getCalcMethod())[0]) * nightTime;

        if (Double.isNaN(times[0]) || timeDiff(times[0], times[1]) > FajrDiff) {
            times[0] = times[1] - FajrDiff;
        }

        // Adjust Isha
        double IshaAngle = (methodParams.get(this.getCalcMethod())[3] == 0) ? methodParams.get(this.getCalcMethod())[4] : 18;
        double IshaDiff = this.nightPortion(IshaAngle) * nightTime;
        if (Double.isNaN(times[6]) || this.timeDiff(times[4], times[6]) > IshaDiff) {
            times[6] = times[4] + IshaDiff;
        }

        // Adjust Maghrib
        double MaghribAngle = (methodParams.get(this.getCalcMethod())[1] == 0) ? methodParams.get(this.getCalcMethod())[2] : 4;
        double MaghribDiff = nightPortion(MaghribAngle) * nightTime;
        if (Double.isNaN(times[5]) || this.timeDiff(times[4], times[5]) > MaghribDiff) {
            times[5] = times[4] + MaghribDiff;
        }

        return times;
    }

    // the night portion used for adjusting times in higher latitudes
    private double nightPortion(double angle) {
        double calc = 0;

        if (adjustHighLats == AngleBased)
            calc = (angle) / 60.0;
        else if (adjustHighLats == MidNight)
            calc = 0.5;
        else if (adjustHighLats == OneSeventh)
            calc = 0.14286;

        return calc;
    }

    // convert hours to day portions
    private double[] dayPortion(double[] times) {
        for (int i = 0; i < 7; i++) {
            times[i] /= 24;
        }
        return times;
    }

    // Tune timings for adjustments
    // Set time offsets
    public void tune(int[] offsetTimes) {

        for (int i = 0; i < offsetTimes.length; i++) { // offsetTimes length
            // should be 7 in order
            // of Fajr, Sunrise,
            // Dhuhr, Asr, Sunset,
            // Maghrib, Isha
            this.offsets[i] = offsetTimes[i];
        }
    }

    private double[] tuneTimes(double[] times) {
        for (int i = 0; i < times.length; i++) {
            times[i] = times[i] + this.offsets[i] / 60.0;
        }

        return times;
    }

    public int getCalcMethod() {
        return calcMethod;
    }

    public void setCalcMethod(int calcMethod) {
        this.calcMethod = calcMethod;
    }

    public int getAsrJuristic() {
        return asrJuristic;
    }

    public void setAsrJuristic(int asrJuristic) {
        this.asrJuristic = asrJuristic;
    }

    public int getDhuhrMinutes() {
        return dhuhrMinutes;
    }

    public void setDhuhrMinutes(int dhuhrMinutes) {
        this.dhuhrMinutes = dhuhrMinutes;
    }

    public int getAdjustHighLats() {
        return adjustHighLats;
    }

    public void setAdjustHighLats(int adjustHighLats) {
        this.adjustHighLats = adjustHighLats;
    }

    public int getTimeFormat() {
        return timeFormat;
    }

    public void setTimeFormat(int timeFormat) {
        this.timeFormat = timeFormat;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    public double getTimeZone() {
        return timeZone;
    }

    public void setTimeZone(double timeZone) {
        this.timeZone = timeZone;
    }

    public double getJDate() {
        return JDate;
    }

    public void setJDate(double jDate) {
        JDate = jDate;
    }

    public int getJafari() {
        return Jafari;
    }

    public void setJafari(int jafari) {
        Jafari = jafari;
    }

    public int getKarachi() {
        return Karachi;
    }

    public void setKarachi(int karachi) {
        Karachi = karachi;
    }

    public int getISNA() {
        return ISNA;
    }

    public void setISNA(int iSNA) {
        ISNA = iSNA;
    }

    public int getMWL() {
        return MWL;
    }

    public void setMWL(int mWL) {
        MWL = mWL;
    }

    public int getMakkah() {
        return Makkah;
    }

    public void setMakkah(int makkah) {
        Makkah = makkah;
    }

    public int getEgypt() {
        return Egypt;
    }

    public void setEgypt(int egypt) {
        Egypt = egypt;
    }

    public int getCustom() {
        return Custom;
    }

    public void setCustom(int custom) {
        Custom = custom;
    }

    public int getTehran() {
        return Tehran;
    }

    public void setTehran(int tehran) {
        Tehran = tehran;
    }

    public int getShafii() {
        return Shafii;
    }

    public void setShafii(int shafii) {
        Shafii = shafii;
    }

    public int getHanafi() {
        return Hanafi;
    }

    public void setHanafi(int hanafi) {
        Hanafi = hanafi;
    }

    public int getNone() {
        return None;
    }

    public void setNone(int none) {
        None = none;
    }

    public int getMidNight() {
        return MidNight;
    }

    public void setMidNight(int midNight) {
        MidNight = midNight;
    }

    public int getOneSeventh() {
        return OneSeventh;
    }

    public void setOneSeventh(int oneSeventh) {
        OneSeventh = oneSeventh;
    }

    public int getAngleBased() {
        return AngleBased;
    }

    public void setAngleBased(int angleBased) {
        AngleBased = angleBased;
    }

    public int getTime24() {
        return Time24;
    }

    public void setTime24(int time24) {
        Time24 = time24;
    }

    public int getTime12() {
        return Time12;
    }

    public void setTime12(int time12) {
        Time12 = time12;
    }

    public int getTime12NS() {
        return Time12NS;
    }

    public void setTime12NS(int time12ns) {
        Time12NS = time12ns;
    }

    public int getFloating() {
        return Floating;
    }

    public void setFloating(int floating) {
        Floating = floating;
    }

    public int getNumIterations() {
        return numIterations;
    }

    public void setNumIterations(int numIterations) {
        this.numIterations = numIterations;
    }

    public ArrayList<String> getTimeNames() {
        return timeNames;
    }
}