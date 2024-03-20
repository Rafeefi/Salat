package com.prayer.app;

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
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;


public class TimeCalculations extends AppCompatActivity {
    ImageView back;
    public static final String PREF_SILENT = "silent";

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.time_calculations);
        if (findViewById(R.id.fragment) != null && savedInstanceState == null) {
            getFragmentManager()
                    .beginTransaction()
                    .add(R.id.fragment, new timeCalculationsFragment())
                    .commit();
        }
          //  back.setOnClickListener(new View.OnClickListener() {
         //       @Override
            //    public void onClick(View v) {
           //         goBack();
             //   }
          //  });

    }
        private void goBack () {
            Intent intent = new Intent(this, Settings.class);
            startActivity(intent);
        }
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == android.R.id.home)
            finish();
        return super.onOptionsItemSelected(item);
    }

    }
