package com.prayer.app;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class ManageLocation extends AppCompatActivity {
    ImageView back ;
    ImageView home ;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.manage_location);
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
                gotoHome();
            }
        });
    }

    private void gotoHome() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    private void goBack() {
        Intent intent = new Intent(this, Settings.class);
        startActivity(intent);
    }
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == android.R.id.home)
            finish();
        return super.onOptionsItemSelected(item);
    }
}