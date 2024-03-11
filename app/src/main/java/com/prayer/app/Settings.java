package com.prayer.app;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.view.View;
import android.widget.Button;

public class Settings extends AppCompatActivity {
    Button b1, b2, b3;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        b1 = ((Button)findViewById(R.id.btnTimeCalc));
        b2 =((Button)findViewById(R.id.btnNotification));

    }

    public void Move(View view) {
        int button = ((Button) view).getId();
        if (button == R.id.btnNotification) {
            Intent intent = new Intent(this, silent.class);
            startActivity(intent);
        } else if (button == R.id.btnTimeCalc) {
            Intent intent = new Intent(this, timeCalculationsFragment.class);
            startActivity(intent);
        } else if (button == R.id.btnGetLocation) {
            Intent intent = new Intent(this, ManageLocation.class);
            startActivity(intent);

        }

    }
}
