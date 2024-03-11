package com.prayer.app;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

public class Home2 extends AppCompatActivity {
       ImageView Settings ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home2);
        Settings = findViewById(R.id.imageSearch);
        Settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MovetoSettings();
            }



});
    }

    private void MovetoSettings() {
        Intent intent = new Intent(this,Settings.class);
        startActivity(intent);
    }
}