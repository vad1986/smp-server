package com.arkadiy.enter.smp3.activities;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;

import com.arkadiy.enter.smp3.R;

import androidx.appcompat.app.AppCompatActivity;

public class SavedActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saved);


            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    try {
                    Intent intent = getIntent();
                    String activityName = intent.getStringExtra("activity");
                    Class activityClass= Class.forName(activityName);
                    intent = new Intent(App.getContext(),activityClass);
                    startActivity(intent);
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }

                }
            }, 2000);




    }
}
