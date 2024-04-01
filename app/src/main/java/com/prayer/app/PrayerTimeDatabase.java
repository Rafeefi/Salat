package com.prayer.app;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

import java.util.ArrayList;
public class PrayerTimeDatabase extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "prayerTime.db";
    public static final String TABLE_NAME = "prayerTime_table";

    public static final String COL_1 = "prayerName";
    public static final String COL_2 = "prayerTime";

    public PrayerTimeDatabase(@Nullable Context context) {
        super(context, DATABASE_NAME, null, 1);
        if (context == null) {
            Log.e("PrayerTimeDatabase", "Context is null!");
        }
        else {
            SQLiteDatabase db = this.getWritableDatabase();
        }
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("create table "+ TABLE_NAME + " (name TEXT,time TEXT);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(sqLiteDatabase);

    }


    public Boolean insertPrayerTimeData(String time) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("time", time);
        long result = db.insert(TABLE_NAME, null, contentValues);
        if (result == -1) return false;
        else
            return true;
    }

    @SuppressLint("Range")
    public ArrayList<PrayerTime> getTimes() {
        SQLiteDatabase db = this.getWritableDatabase();

        ArrayList<PrayerTime> list = new ArrayList<PrayerTime>();
        Cursor cur = null;

        db.beginTransaction();

        try {
            cur = db.query(TABLE_NAME, new String[]{}, null, null, null, null, null);
            if (cur != null) {
                if (cur.moveToFirst()) {
                    do {
                        PrayerTime t = new PrayerTime();
                        t.setName(cur.getString(cur.getColumnIndex("name")));
                        t.setTime(cur.getString(cur.getColumnIndex("time")));
                        list.add(t);
                    } while (cur.moveToNext());
                }
            }
        } finally {
            db.endTransaction();
            cur.close();
        }

        return list;

    }
    public String getFajrTime() {
        SQLiteDatabase db = this.getReadableDatabase();
        String fajrTime = null;

        Cursor cursor = db.rawQuery("SELECT " + COL_2 + " FROM " + TABLE_NAME + " WHERE " + COL_1 + " = 'Fajr'", null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                fajrTime = cursor.getString(0); // Assuming the Fajr time is stored in the first column
            }
            cursor.close();
        }

        return fajrTime;
    }

}