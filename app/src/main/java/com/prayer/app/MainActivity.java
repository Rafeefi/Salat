package com.prayer.app;


import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;


public class MainActivity extends AppCompatActivity {


ImageView settings ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home2);
        settings = findViewById(R.id.imageSearch);
       settings.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               goToSettings();
           }
       });


    }

    private void goToSettings() {
        Intent intent=new Intent(this, Settings.class);
        startActivity(intent);
    }
}



