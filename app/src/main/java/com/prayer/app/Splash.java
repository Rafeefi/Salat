package com.prayer.app;



import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import com.prayer.app.HomePage;
public class Splash  extends Activity {
    Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(Splash.this, HomePage.class);
                startActivity(intent);
                finish();
            }
        }, 1000);

    }
}

