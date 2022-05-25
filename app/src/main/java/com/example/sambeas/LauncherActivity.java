package com.example.sambeas;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

public class LauncherActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launcher);

        int secondsDelayed = 5000;
        new Handler().postDelayed(new Runnable() {
            public void run() {
                startActivity(new Intent(LauncherActivity.this, AddContact.class));
                finish();
            }
        },secondsDelayed);
    }
}