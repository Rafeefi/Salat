package com.prayer.app;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

public class Settings extends AppCompatActivity {
    Button b1, b2, b3;
    ImageView back ;
    ImageView home ;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        b1 = ((Button)findViewById(R.id.btnTimeCalc));
        b2 =((Button)findViewById(R.id.btnNotification));
        b3= ((Button)findViewById(R.id.btnGetLocation));
        back = findViewById(R.id.imageBack);
        home = findViewById(R.id.home);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goBack();
            }
        });
        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToHome();
            }
        });



    }

    private void goToHome() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    public void Move(View view) {
        int button = ((Button) view).getId();
        if (button == R.id.btnNotification) {
            Intent intent = new Intent(this, silent.class);
            startActivity(intent);
        } else if (button == R.id.btnTimeCalc) {
            Intent intent = new Intent(this, TimeCalculations.class);
            startActivity(intent);
        } else if (button == R.id.btnGetLocation) {
            Intent intent = new Intent(this, ManageLocation.class);
            startActivity(intent);

        }

    }
    private void goBack() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}
