package com.example.gpsdemo;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.Window;
import java.util.Timer;
import java.util.TimerTask;

public class Pantallazo extends AppCompatActivity {
    private static final long DELAY = 3000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_pantallazo);
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                Intent intent = new Intent().setClass(Pantallazo.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        };
        Timer crono = new Timer();
        crono.schedule(task,DELAY);
    }
}