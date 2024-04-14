package com.prayer.app;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import androidx.preference.PreferenceManager;

import java.util.HashMap;

public class PrefManager {
    SharedPreferences pref;
    SharedPreferences.Editor editor;
    Context _context;
    int PRIVATE_MODE = 0;
    public static final String SERIVCE_RUN = "SERIVCE_RUN";
    private static final String PREF_NAME = "prayTimeApp";
    public static final String CITY = "CityEditText";
    public static final String LATITIUDE = "LatitudeEditText";
    public static final String lONGTIUDE = "longitudeEditText";
    public static final String IS_LOC_ENABLE = "location";

    SharedPreferences  sharedPreferences = null;
    // Constructor
    public PrefManager(Context context){
        this._context = context;
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);

        editor = sharedPreferences.edit();
    }

    public void setCityName(String city){
        editor.putString(CITY, city);
        editor.commit();
    }

    public String getCityName(){
        return sharedPreferences.getString(CITY, "");
    }


    public void setLocation(double latitude, double longitude) {
        editor.putString(LATITIUDE, Double.toString( latitude));
        editor.putString(lONGTIUDE, Double.toString( longitude));
        editor.commit();
    }

    public double getLatitude(){
        String lat =  sharedPreferences.getString(LATITIUDE, "0");
        return Double.parseDouble(lat);
    }
    public double getLongtiude(){
        String lng = sharedPreferences.getString(lONGTIUDE, "0");
        return Double.parseDouble(lng);
    }
    public boolean isLocationEnabled (){
        return sharedPreferences.getBoolean(IS_LOC_ENABLE, false);
    }



    public void setServiceRun(Boolean t){
        editor.putBoolean(SERIVCE_RUN, t);
        editor.commit();
    }
    public boolean getServiceRun(){
        return sharedPreferences.getBoolean(SERIVCE_RUN, true);
    }


}
