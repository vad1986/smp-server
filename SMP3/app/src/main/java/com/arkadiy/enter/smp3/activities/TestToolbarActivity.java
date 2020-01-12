package com.arkadiy.enter.smp3.activities;

import android.os.Bundle;
import android.view.View;

import com.arkadiy.enter.smp3.R;
import com.arkadiy.enter.smp3.services.GlobalServices;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class TestToolbarActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test_toolbar);


        Toolbar toolbar=(Toolbar)findViewById(R.id.toolbarMine);
        setSupportActionBar(toolbar);
        GlobalServices.addListener(toolbar,this);

    }
}
