package com.arkadiy.enter.smp3.activities;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.arkadiy.enter.smp3.R;
import com.arkadiy.enter.smp3.dataObjects.Alert;
import com.arkadiy.enter.smp3.dataObjects.CustomAdapterAlert;
import com.arkadiy.enter.smp3.dataObjects.Store;
import com.arkadiy.enter.smp3.dataObjects.User;
import com.arkadiy.enter.smp3.services.GlobalServices;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class CommuicationActivity extends AppCompatActivity {
    public static ListView alertListView;

    public static List<String>users ;
    private static HashMap<String,Integer>onlineUsers;
    private List<Integer> id;
    private RequestQueue requestQueue;
    private static Spinner onlineUserSpinner;
    private Alert alert;
    private static ArrayList<Alert> alerts;
    private int sendToId;
    private static CustomAdapterAlert customAdapterAlert;

    private static ArrayAdapter onlineUsersAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        App.setContext(this);
        try {
            setContentView(R.layout.activity_commuication);
            requestQueue = Volley.newRequestQueue(this);
            id =new ArrayList<Integer>();
            users = new ArrayList<>();
            alerts = (ArrayList<Alert>) Store.getAlerts();
            onlineUserSpinner = (Spinner)findViewById(R.id.onlineUsers_Spinner);
            onlineUsersAdapter = new ArrayAdapter <String>(CommuicationActivity.this,android.R.layout.simple_spinner_dropdown_item,users);
            onlineUsers = new HashMap< String,Integer>();
            alertListView = (ListView)findViewById(R.id.alert_ListView);
            Store.checkUsersOnline(CommuicationActivity.this,requestQueue,hendler->{


                for (int i = 0 ; i < Store.sizeUserOnline();i++ ){
                    try {
                        onlineUsers.put(Store.getUsersOnline().getJSONObject(i).getString("user_name"),Store.getUsersOnline().getJSONObject(i).getInt("user_id"));// for spinner
                        //onlineUsersAdapter.add(Store.getUsersOnline().getJSONObject(i).getString("user_name"));// for spinner

                        users.add(Store.getUsersOnline().getJSONObject(i).getString("user_name"));
                        id.add(Store.getUsersOnline().getJSONObject(i).getInt("user_id"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }


                }

                customAdapterAlert = new CustomAdapterAlert(this,alerts);
                alertListView.setAdapter(customAdapterAlert);
                onlineUserSpinner.setAdapter(onlineUsersAdapter);
                return true;
            });

            alertListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    alert = Store.getAlerts().get(position);
                    alert.setUserto(sendToId);
                    //Toast.makeText(CommuicationActivity.this,users.get(position),Toast.LENGTH_LONG).show();

                    sendAlert(alert.getId(),alert.getUserto());



                }
            });

            onlineUserSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                    Toast.makeText(CommuicationActivity.this,onlineUsers.get(onlineUsersAdapter.getItem(position)).toString(),Toast.LENGTH_LONG).show();

                    sendToId = onlineUsers.get(onlineUsersAdapter.getItem(position));

                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });

        }catch (Exception e){
            e.printStackTrace();
        }

        Toolbar toolbar=(Toolbar)findViewById(R.id.toolbarMine);
        setSupportActionBar(toolbar);
        GlobalServices.addListener(toolbar,this);


    }
    //send alert to User Class and them to server and another user
    private void sendAlert(int messagesId,int userIdTo){

        try {
            JSONObject jsonAlert = new JSONObject().put("message_id",messagesId).put("user_id_to",userIdTo);
            User.sendAlert(CommuicationActivity.this,jsonAlert,requestQueue);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
    public static void removeUser(String name){
        try {

            new Handler(Looper.getMainLooper()).post(new Runnable(){
                @Override
                public void run() {
                    if (onlineUsers.containsKey(name)){

                        onlineUsers.remove(name);
                        users.remove(name);
                        //onlineUserSpinner.setAdapter(onlineUsersAdapter);
                        onlineUsersAdapter.notifyDataSetChanged();

                    }
                }
            });



        }catch (Exception e){
            e.printStackTrace();
        }


    }

    public static void addOnlineUser(JSONObject userOnline){

            new Handler(Looper.getMainLooper()).post(new Runnable(){
                @Override
                public void run() {
                    int  userId = 0;
                    try {
                        userId = userOnline.getInt("user_id");
                        String userName = userOnline.getString("user_name");

                        if (userId != User.getMyUserId() && !onlineUsers.containsKey(userName)) {
                            onlineUsers.put(userName, userId);
                            users.add(userName);
                            onlineUsersAdapter.notifyDataSetChanged();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }



                }
            });
    }
    public static void setNewAlertInAdapter(Alert newAlert){


        new Handler(Looper.getMainLooper()).post(new Runnable(){
            @Override
            public void run() {
                alerts.add(0,newAlert);
                customAdapterAlert.add(newAlert);

                //customAdapterAlert.addNewAlertToList(newAlert);
                alertListView.deferNotifyDataSetChanged();

            }
        });



    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu,menu);
        return true;
    }
}
