package com.arkadiy.enter.smp3.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.arkadiy.enter.smp3.R;
import com.arkadiy.enter.smp3.config.AppConfig;
import com.arkadiy.enter.smp3.config.ResponseCode;
import com.arkadiy.enter.smp3.dataObjects.User;
import com.arkadiy.enter.smp3.dataObjects.Users;
import com.arkadiy.enter.smp3.services.DataServices;
import com.arkadiy.enter.smp3.services.GlobalServices;
import com.arkadiy.enter.smp3.utils.Constants;
import com.arkadiy.enter.smp3.utils.ConstantsJson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity  implements ActivityCompat.OnRequestPermissionsResultCallback {

    private static Handler handler;
    private static Context context;
    private Button exitButton;
    //private Button addNewUserButton;
    private Button signingAClockButton;
    private Button tasksButton;
    //private Button addNewTaskButton;
    private Button adminOptionsButton;
    private Button managerOptionsButton;
    private Button alertButton;

    private ListView promoListView;
    private ListAdapter listAdapter;
    private JSONObject jsonObject;
    private RequestQueue requestQueue;
    public static HashMap<Integer,ArrayList<Users>> USERS;
    static final Integer LOCATION = 0x1;
    public JSONArray globalRoles;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        USERS =  new HashMap<>();

        context = MainActivity.this;

        getMyUsers();
        //here start all gps content
        //here ends gps
        checkRoll();

        App.setContext(this);



        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            askForPermission(Manifest.permission.ACCESS_FINE_LOCATION,LOCATION);
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }

        Toolbar toolbar=(Toolbar)findViewById(R.id.toolbarMine);
        setSupportActionBar(toolbar);
        GlobalServices.addListener(toolbar,this);


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu,menu);
        return true;
    }



    private void checkRoll(){
        int role = User.getMyRole();
        switch (role){
            case 0: // user
                createUserUI();
                break;
            case 1://Department manager
                createUserUI();
                createManagerUI();
            break;
            case 2://shift Manager
                createUserUI();
                createManagerUI();
            break;
            case 3://Main Manager
                createUserUI();
                createManagerUI();

            break;
            case 4://Administrator
                createUserUI();
                createManagerUI();
                createAdminUI();
            break;

        }
    }
    private void createUserUI(){
//        exitButton = (Button)findViewById(R.id.exit_Button);
        signingAClockButton = (Button)findViewById(R.id.signingAClock_Button);
        tasksButton = (Button)findViewById(R.id.tasks_Button);

        //Deleted this button and move to the task Activity
        //addNewTaskButton = (Button)findViewById(R.id.addNewTask_Button);

        alertButton = (Button)findViewById(R.id.communication_Button);
        managerOptionsButton =(Button)findViewById(R.id.manager_options);
        adminOptionsButton = (Button)findViewById(R.id.admin_options);
        managerOptionsButton.setVisibility(View.GONE);
        adminOptionsButton.setVisibility(View.GONE);
        tasksButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this,TasksActivity.class);
                startActivity(intent);
            }
        });
        signingAClockButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this,SigningAClockActivity.class);
                startActivity(intent);
            }
        });

//        addNewTaskButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent = new Intent(MainActivity.this,AddNewTaskActivity.class);
//                startActivity(intent);
//            }
//        });
        alertButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,CommuicationActivity.class);
                startActivity(intent);
            }
        });
//        exitButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
////                DataServices.sendData(AppConfig.LOGOUT_SERVER,jsonObject,requestQueue,
////                MainActivity.this,Constants.METHOD_POST,null);
//                User.logOut();
//            }
//        });
    }
//    private void createMainManagerUI(){
//
//        Intent intent = new Intent(MainActivity.this,ManagerActivity.class);
//        startActivity(intent);
//
//
//    }

    private void createManagerUI(){
        managerOptionsButton.setVisibility(View.VISIBLE);
        managerOptionsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,ManagerActivity.class);
                startActivity(intent);
            }
        });

    }

    private void createAdminUI(){


        adminOptionsButton.setVisibility(View.VISIBLE);

        adminOptionsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,AdminActivity.class);
                startActivity(intent);
            }
        });
    }
    private void getMyUsers(){
        getHendlerMain();
        requestQueue = Volley.newRequestQueue(this);

    }
    private void turnGPSOn(){
        String provider = Settings.Secure.getString(getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);

        if(!provider.contains("gps")){ //if gps is disabled
            final Intent poke = new Intent();
            poke.setClassName("com.android.settings", "com.android.settings.widget.SettingsAppWidgetProvider");
            poke.addCategory(Intent.CATEGORY_ALTERNATIVE);
            poke.setData(Uri.parse("3"));
            sendBroadcast(poke);
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (ActivityCompat.checkSelfPermission(this, permissions[0]) == PackageManager.PERMISSION_GRANTED) {
            switch (requestCode) {
                //Location
                case 1:
                    askForGPS();
                    break;
            }
        }
    }

    @SuppressLint("MissingPermission")
    private void askForGPS() {
//        locationManager.requestLocationUpdates(1000, 1, criteria, checker,null);

    }

    private void askForPermission(String permission, Integer requestCode) {
        if (ContextCompat.checkSelfPermission(MainActivity.this, permission) != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, permission)) {

                //This is called if user has denied the permission before
                //In this case I am just asking the permission again
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{permission}, requestCode);

            } else {

                ActivityCompat.requestPermissions(MainActivity.this, new String[]{permission}, requestCode);
            }
        } else {
            Toast.makeText(this, "" + permission + " is already granted.", Toast.LENGTH_SHORT).show();
        }
    }


    public static void getHendlerMain(){
        handler=new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message msg) {
                try {
                    Bundle bundle = msg.getData();
                    JSONObject json;
                    if(bundle.getString("json")!=null)
                        json =new JSONObject(bundle.getString("json"));
                    else return false;

                    int responseCode=json.getInt(ConstantsJson.RESPONSE_CODE);

                    switch (responseCode){
                        case ResponseCode.GET_MY_USERS:
                            break;
//                        case ResponseCode.ERROR:
//                            Intent intent = new Intent(context, LogInActivity.class );
//                            //aintent.putExtra("globalRoles", String.valueOf(json.getJSONArray(ConstantsJson.GLOBAL_ROLES)));
//                            context.startActivity(intent);
//                            break;
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                return true;
            }
        });
    }


}
