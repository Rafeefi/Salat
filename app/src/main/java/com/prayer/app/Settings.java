package com.prayer.app;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.os.Build;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.PreferenceManager;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
public class Settings extends AppCompatActivity {
    Button b1, b2, b3;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

    }

    public void Move(View view) {
        int button = ((Button) view).getId();
        if (button == R.id.btnNotification) {
            Intent intent = new Intent(this, silent.class);
            startActivity(intent);
        } else if (button == R.id.btnTimeCalc) {
            Intent intent = new Intent(this, timeCalculations.class);
            startActivity(intent);
        } else if (button == R.id.btnGetLocation) {
            Intent intent = new Intent(this, ManageLocation.class);
            startActivity(intent);

        }

    }
}
