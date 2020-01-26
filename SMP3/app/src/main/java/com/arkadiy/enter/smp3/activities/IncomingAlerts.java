package com.arkadiy.enter.smp3.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Spinner;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.arkadiy.enter.smp3.R;
import com.arkadiy.enter.smp3.dataObjects.Alert;
import com.arkadiy.enter.smp3.dataObjects.CustomAdapterAlert;
import com.arkadiy.enter.smp3.dataObjects.Task;
import com.arkadiy.enter.smp3.services.GlobalServices;
import com.baoyz.swipemenulistview.SwipeMenuListView;

import java.util.ArrayList;

public class IncomingAlerts extends AppCompatActivity {

    private ImageButton sendNewAlert;
    private static ArrayList<Alert> alerts;
    private static CustomAdapterAlert customAdapterAlert;
    private RequestQueue requestQueue;
    public static ListView alertListView;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_incoming_alerts);

        Toolbar toolbar=(Toolbar)findViewById(R.id.toolbarMine);
        setSupportActionBar(toolbar);
        GlobalServices.addListener(toolbar,this);
        sendNewAlert=(ImageButton) findViewById(R.id.sendNewAlert);
        requestQueue = Volley.newRequestQueue(this);
        alertListView = (ListView)findViewById(R.id.listViewAlerts);
        alerts=new ArrayList<>();


        //go to server and get alerts

        customAdapterAlert = new CustomAdapterAlert(this,alerts);
        alertListView.setAdapter(customAdapterAlert);


        sendNewAlert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent;
                intent = new Intent(IncomingAlerts.this, CommuicationActivity.class);
                startActivity(intent);
            }
        });


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu,menu);
        return true;
    }
}
