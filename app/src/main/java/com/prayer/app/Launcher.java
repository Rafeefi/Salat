package com.prayer.app;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;

import com.prayer.app.R;

public class Launcher extends Activity {
    Button regButton, logButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launcher);

        // regButton = findViewById(R.id.regButton);
        // logButton = findViewById(R.id.logButton);

        regButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               Intent i = new Intent(Launcher.this,MainActivity.class);
               startActivity(i);
            }
        });

        logButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Intent i = new Intent(LauncherActivity.this,loginActivity.class);
                //  startActivity(i);
            }
        });


    }
}