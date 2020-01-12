package com.arkadiy.enter.smp3.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.arkadiy.enter.smp3.R;
import com.arkadiy.enter.smp3.dataObjects.GetDate;
import com.arkadiy.enter.smp3.dataObjects.Manager;
import com.arkadiy.enter.smp3.services.GlobalServices;

import org.json.JSONException;
import org.json.JSONObject;

public class EditUserAttendancy extends AppCompatActivity {

    private EditText editFromTimeEditText;
    private EditText editToTimeEditText;
    private Button  sendFromEditTimeButton;
    private Button  sendEndToEditTimeButton;
    private int in_out;
    private long userId;
    private int startId;
    private int endId;
    private RequestQueue requestQueue;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_user_attendancy);
        App.setContext(EditUserAttendancy.this);
        requestQueue = Volley.newRequestQueue(App.getContext());
        userId = Long.parseLong(getIntent().getStringExtra("userId"));
        editFromTimeEditText = (EditText)findViewById(R.id.editFromTime_EditText);
        editToTimeEditText = (EditText)findViewById(R.id.editToTime_EditText);

        sendFromEditTimeButton = (Button)findViewById(R.id.sendFromEditTime_Button);
        sendEndToEditTimeButton = (Button)findViewById(R.id.sendEndToEditTime_Button);

        editFromTimeEditText.setText(getIntent().getStringExtra("start"));
        editToTimeEditText.setText(getIntent().getStringExtra("finish"));
        startId = Integer.parseInt(getIntent().getStringExtra("start_id"));
        endId = Integer.parseInt(getIntent().getStringExtra("finish_id"));

        editFromTimeEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                GetDate.getDate(editFromTimeEditText);

            }
        });

        editToTimeEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GetDate.getDate(editToTimeEditText);
            }
        });

        sendFromEditTimeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                in_out = 1;
                sendToServer(in_out,editFromTimeEditText.getText().toString(),userId,startId);
            }
        });

        sendEndToEditTimeButton.setOnClickListener(new View.OnClickListener() {



            @Override
            public void onClick(View v) {
                in_out = 2;
                sendToServer(in_out,editToTimeEditText.getText().toString(),userId,endId);
            }
        });

        Toolbar toolbar=(Toolbar)findViewById(R.id.toolbarMine);
        setSupportActionBar(toolbar);
        GlobalServices.addListener(toolbar,this);

    }

    private void sendToServer(int inOrOut ,String dateTime,long forUserId,int idStatus) {




        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("date_time",dateTime);
            jsonObject.put("in_out",inOrOut);
            jsonObject.put("user_id",forUserId);
            if (idStatus != -100 ){

                jsonObject.put("id",idStatus);
            }else{
                jsonObject.put("id",-1);
            }

            Manager.sendEditUserAttendancy(requestQueue,jsonObject,userAttendacyHandler->{



                return true;
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu,menu);
        return true;
    }
}
