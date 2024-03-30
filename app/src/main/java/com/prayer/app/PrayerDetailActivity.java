package com.prayer.app;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class PrayerDetailActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prayer_detail);

        TextView prayerDetailsTextView = findViewById(R.id.prayerDetails);
        Button confirmButton = findViewById(R.id.confirmButton);
        Button ignoreButton = findViewById(R.id.ignoreButton);

        // Assuming you pass the prayer name as an extra in the intent
        String prayerName = getIntent().getStringExtra("prayerName");
        prayerDetailsTextView.setText("Time for " + prayerName + ". Would you like to confirm or ignore?");

        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle confirmation logic here
                Toast.makeText(PrayerDetailActivity.this, "Confirmed!", Toast.LENGTH_SHORT).show();
                // Optionally close the activity
                finish();
            }
        });

        ignoreButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle ignore logic here
                Toast.makeText(PrayerDetailActivity.this, "Ignored.", Toast.LENGTH_SHORT).show();
                // Optionally close the activity
                finish();
            }
        });
    }
}
