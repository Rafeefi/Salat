package com.prayer.app;
import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import java.util.ArrayList;
public class PrayerTimeDatabase extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "prayerTime.db";
    public static final String TABLE_NAME = "prayerTime_table";

    public static final String COL_1 = "prayerName";
    public static final String COL_2 = "prayerTime";

    public PrayerTimeDatabase(@Nullable Context context) {
        super(context, DATABASE_NAME, null, 1);
        SQLiteDatabase db = this.getWritableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("create table " + TABLE_NAME + " (email TEXT PRIMARY KEY, password TEXT) ");
        sqLiteDatabase.execSQL("create table prayer_time (name TEXT,time TEXT);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + "prayer_time");
        onCreate(sqLiteDatabase);

    }


    public Boolean insertPrayerTimeData(String time) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("time", time);
        long result = db.insert("prayer_time", null, contentValues);
        if (result == -1) return false;
        else
            return true;
    }

    @SuppressLint("Range")
    public ArrayList<PrayerTime> getTimes(String em) {
        SQLiteDatabase db = this.getWritableDatabase();

        ArrayList<PrayerTime> list = new ArrayList<PrayerTime>();
        Cursor cur = null;

        db.beginTransaction();

        try {
            cur = db.query("prayer_time", new String[]{}, null, null, null, null, null);
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


}


