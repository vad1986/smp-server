package com.arkadiy.enter.smp3.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.arkadiy.enter.smp3.R;
import com.arkadiy.enter.smp3.config.ResponseCode;
import com.arkadiy.enter.smp3.dataObjects.Admin;
import com.arkadiy.enter.smp3.services.GlobalServices;
import com.arkadiy.enter.smp3.utils.Constants;

import org.json.JSONException;
import org.json.JSONObject;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class CreateAlertActivity extends AppCompatActivity {

    private TextView nameForNewAlert_textView;
    private TextView descriptionForNewAlert_textView;
    private Button createNewAlet_button;
    private int CREATE_NEW_ALERT_ID = 0;
    private RequestQueue requestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_alert);
        requestQueue = Volley.newRequestQueue(this);
        nameForNewAlert_textView = (TextView)findViewById(R.id.name_new_alertEditText);
        descriptionForNewAlert_textView = (TextView)findViewById(R.id.description_alertEditText);
        createNewAlet_button = (Button)findViewById(R.id.send_new_alertButton);
        App.setContext(this);
        createNewAlet_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (nameForNewAlert_textView.getText() !="" && descriptionForNewAlert_textView.getText()!=""){
                    createAlert();

                }
            }
        });

        Toolbar toolbar=(Toolbar)findViewById(R.id.toolbarMine);
        setSupportActionBar(toolbar);
        GlobalServices.addListener(toolbar,this);


    }
    private void createAlert(){

        try {
            JSONObject newAlert = new JSONObject().put("name",nameForNewAlert_textView.getText().toString()
            ).put("description",descriptionForNewAlert_textView.getText().toString()).put("id",CREATE_NEW_ALERT_ID).put("remove",CREATE_NEW_ALERT_ID);
            Admin.createOrEditAlert(CreateAlertActivity.this,newAlert,requestQueue, i_handler->{
                JSONObject json = new JSONObject();
                Bundle  bundle = i_handler.getData();
                try {
                    json = new JSONObject(bundle.getString("json"));

                Toast.makeText(App.getContext(),json.getString("message"),Toast.LENGTH_LONG).show();
                if (i_handler.getData().getInt(Constants.RESPONSE_CODE) < ResponseCode.ERROR){

                    Intent intent = new Intent(CreateAlertActivity.this,SavedActivity.class);
                    intent.putExtra("activity",AdminActivity.class.getName());
                    startActivity(intent);

//                    nameForNewAlert_textView.setText("");
//                    descriptionForNewAlert_textView.setText("");
                    //Toast.makeText(App.getContext(),i_handler.getData().getString("message"),Toast.LENGTH_LONG).show();
                }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

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
