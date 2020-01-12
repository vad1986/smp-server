package com.arkadiy.enter.smp3.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Button;

import com.arkadiy.enter.smp3.R;
import com.arkadiy.enter.smp3.services.GlobalServices;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class AdminActivity extends AppCompatActivity {

    private Button addNewUser_Btn;
    private Button editUser_Btn;
   // private Button editDepartment_Btn;
    private Button createDepartment_Btn;
    private Button createAlert_Btn;
    private Button editAlert_Btn;
    private Button GPS_conf_Btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);
        addNewUser_Btn = (Button)findViewById(R.id.addNewUser_Btn);
        editUser_Btn = (Button)findViewById(R.id.editUser_Btn);

        createDepartment_Btn = (Button)findViewById(R.id.createDepartments_Btn);
        createAlert_Btn = (Button)findViewById(R.id.createAlert_Btn);
        editAlert_Btn = (Button)findViewById(R.id.editAlert_Btn);
        GPS_conf_Btn = (Button)findViewById(R.id.GPS_conf_Btn);
        App.setContext(this);

        addNewUser_Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent( AdminActivity.this,AddNewUserActivity.class);
                startActivity(intent);
            }
        });

        editUser_Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdminActivity.this,EditUsersActivity.class);
                startActivity(intent);
            }
        });



        createDepartment_Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdminActivity.this, BuildDepartmentActivity.class);
                startActivity(intent);
            }
        });

        createAlert_Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdminActivity.this, CreateAlertActivity.class);
                startActivity(intent);
            }
        });

        editAlert_Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdminActivity.this, EditAlertActivity.class);
                startActivity(intent);
            }
        });

        GPS_conf_Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(AdminActivity.this, GPSActivity.class);
                startActivity(intent);

            }
        });

        Toolbar toolbar=(Toolbar)findViewById(R.id.toolbarMine);
        setSupportActionBar(toolbar);
        GlobalServices.addListener(toolbar,this);


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu,menu);
        return true;
    }
}
