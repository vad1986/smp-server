package com.arkadiy.enter.smp3.activities;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.arkadiy.enter.smp3.R;
import com.arkadiy.enter.smp3.config.AppConfig;
import com.arkadiy.enter.smp3.config.ResponseCode;
import com.arkadiy.enter.smp3.dataObjects.CustomAdapterHours;
import com.arkadiy.enter.smp3.dataObjects.DayWork;
import com.arkadiy.enter.smp3.dataObjects.IHandler;
import com.arkadiy.enter.smp3.dataObjects.User;
import com.arkadiy.enter.smp3.services.GlobalServices;
import com.arkadiy.enter.smp3.services.GpsChecker;
import com.arkadiy.enter.smp3.utils.Constants;
import com.arkadiy.enter.smp3.utils.ConstantsJson;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

public class SigningAClockActivity extends AppCompatActivity implements ActivityCompat.OnRequestPermissionsResultCallback, OnMapReadyCallback {
    private TextView enterDataTextView;
    private TextView exitDataTextView;
    private TextView userName;
    private Button enterButton;
    private Button exitButton;
    private JSONObject jsonClock;
    private JSONObject jsonRes;
    private RequestQueue requestQueue;
    private Criteria criteria;
    private LocationManager locationManager;
    private GpsChecker checker;
    private int in_out;
    private IHandler iHandler;
    private String enterTime;
    private String endTime;
    private String sumTime;
    private int day;
    private DayWork dayWork;
    private ArrayList<DayWork> listWork;
    private ListView weekWork;
    private CustomAdapterHours customAdapterHours;
    private GoogleMap mMap;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signing_aclock);

//        file = new File(getFilesDir(),FileConfig.SING_IN_A_CLOCK);


        initGPS();// build GPS items
        listWork = new ArrayList<DayWork>();
        enterDataTextView = (TextView) findViewById(R.id.enterData_TextView);
        exitDataTextView = (TextView) findViewById(R.id.exitData_TextView);
        enterButton = (Button) findViewById(R.id.enter_button);
        exitButton = (Button) findViewById(R.id.exit_Button);

//        userName.setText("Hello "+User.getUserName()); //TODO: FIX THIS
        requestQueue = Volley.newRequestQueue(this);
        weekWork = (ListView)findViewById(R.id.hours_ListView);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


        User.getShiftsFromServer(iHandlerShifts->{
            Message msg = new Message();
            Bundle bundle = iHandlerShifts.getData();
            try {
                jsonRes = new JSONObject(bundle.getString("json"));
                setDayWork(jsonRes);

            } catch (JSONException e) {
                e.printStackTrace();
            }
            return true;
        });





        enterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                in_out = Constants.SET_IN_OCLOCK;

                checkLocation();
                enterTime = getTime();
                //day = getDay();
                sendToServer(enterTime, in_out,enterDataTextView);
                setLocationOnMap();


            }
        });
        exitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                in_out = Constants.SET_OUT_OCLOCK;
                checkLocation();
                endTime = getTime();
                sendToServer(endTime,in_out,exitDataTextView);



                setLocationOnMap();
            }
        });
        App.setContext(this);

        customAdapterHours = new CustomAdapterHours(SigningAClockActivity.this,listWork);
        weekWork.setAdapter(customAdapterHours);

        Toolbar toolbar=(Toolbar)findViewById(R.id.toolbarMine);
        setSupportActionBar(toolbar);
        GlobalServices.addListener(toolbar,this);


    }




    private void setDayWork(JSONObject jsonResponse) {


        java.text.DateFormat df = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        java.text.DateFormat dfForDay = new java.text.SimpleDateFormat("yyyy-MM-dd");
        java.util.Date dateDay = null;
        String date1 = null;
        String date2 = null;
        long startId = -100;// if null = -100
        long endId = -100;// if null = -100
        boolean dateNull =false;
        try {
            for (int i = 0;i < jsonResponse.getJSONArray("data").length();i++){

                if (!jsonResponse.getJSONArray("data").getJSONObject(i).has("start")){
                    date1 = "--";
                    dateDay = null;
                }else {

                    date1 =  jsonResponse.getJSONArray("data").getJSONObject(i).getString("start");
                    startId = jsonResponse.getJSONArray("data").getJSONObject(i).getLong("start_id");
                    dateDay = dfForDay.parse(jsonResponse.getJSONArray("data").getJSONObject(i).getString("start"));

                }
                if (!jsonResponse.getJSONArray("data").getJSONObject(i).has("finish")){
                    date2 = "--";

                }else {
                    date2 = jsonResponse.getJSONArray("data").getJSONObject(i).getString("finish");
                    endId = jsonResponse.getJSONArray("data").getJSONObject(i).getLong("start_id");

                }

//            date1 = df.parse(enterTime);
//            date2 = df.parse(endTime);
                if ( jsonResponse.getJSONArray("data").getJSONObject(i).has("hours")){

                    //long diff = date2.getTime() - date1.getTime();
                    sumTime =  jsonResponse.getJSONArray("data").getJSONObject(i).getString("hours");

                }
                else {
                    sumTime ="--";
                    if (dateDay == null){
                        dateDay = dfForDay.parse(jsonResponse.getJSONArray("data").getJSONObject(i).getString("finish"));
                    }
                }



                // sumTime =  jsonResponse.getJSONArray("data").getJSONObject(i).getString("hours");
//        sumTime = String.valueOf(diff);
                dayWork = new DayWork(getDay(dateDay),date1,date2,sumTime,startId,endId);
                listWork.add(dayWork);
                customAdapterHours.notifyDataSetChanged();
                dateDay =  null;

            }
        } catch (ParseException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    public void sendToServer(String time, int in_out,TextView textView) {


        try {
            jsonClock = new JSONObject();
            jsonClock.put("Latitude", Double.parseDouble(checker.getLatitude()));
            jsonClock.put("Longtitude", Double.parseDouble(checker.getLongtitude()));
            jsonClock.put("in_out", in_out); // in_out String
            jsonClock.put("date_time", String.valueOf(time)); // time String
            jsonClock.put("gps",User.getGPSAnuble());
            User.punchClock(SigningAClockActivity.this, jsonClock, AppConfig.PUNCH_CLOCK, requestQueue, iHandler -> {
                try {

                    JSONObject json;
                    Bundle bundle = iHandler.getData();

                        json =new JSONObject(bundle.getString("json"));



                     if (iHandler.getData().getInt(ConstantsJson.RESPONSE_CODE) == ResponseCode.NOT_IN_RANGE) {

                      } else if (iHandler.getData().getInt(ConstantsJson.RESPONSE_CODE) == ResponseCode.PUNCH_CLOCK) {
                         Toast.makeText(App.getContext(),"Clock stamping was successful ",Toast.LENGTH_LONG).show();
                           textView.setText(time);

                            //chengVisibility(time);
                      }else {
                         Toast.makeText(App.getContext(),json.getString("message"),Toast.LENGTH_LONG).show();
                     }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                return true;
            });


        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public String getTime() {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String StringDate = df.format(Calendar.getInstance().getTime());
        return StringDate;
    }
    public int getDay(Date day){
        Calendar c = Calendar.getInstance();
        c.setTime(day);
        //c.setTime(Calendar.getInstance().getTime());
        int dayOfWeek = c.get(Calendar.DAY_OF_WEEK);
        return dayOfWeek;
    }

    private void initGPS() {


        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        checker = new GpsChecker(locationManager, this, afterGotLocation -> {
//            Bundle bundle = msg.getData();
            String date = getTime();
            if (afterGotLocation.getData() != null) {
//                   userName.setText(checker.getLatitude());
//                if (Constants.SET_IN_OCLOCK == in_out) {
//
//                    chengVisibility(date);
//
//                } else if (Constants.SET_OUT_OCLOCK == in_out) {
//
//                    chengVisibility(date);
//
//                }

               // sendToServer(date, in_out);
            }
            return true;
        });
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
//        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1, 0.0f, checker);
//        locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);



        checker.run();
        criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        criteria.setPowerRequirement(Criteria.POWER_LOW);
        criteria.setAltitudeRequired(false);
        criteria.setSpeedRequired(false);
        criteria.setCostAllowed(true);
        criteria.setBearingRequired(false);
        criteria.setHorizontalAccuracy(Criteria.ACCURACY_HIGH);
        criteria.setVerticalAccuracy(Criteria.ACCURACY_HIGH);
        locationManager.requestLocationUpdates(1000,1,criteria,checker,null);
//        locationManager.removeUpdates(checker);
//        locationManager=null;

    }
    private void checkLocation(){

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) !=
                        PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        //
        // locationManager.requestLocationUpdates(1000,1,criteria,checker,null);
        locationManager.requestSingleUpdate("gps",checker,null);

    }




    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
    }
    private void setLocationOnMap(){
        // Add a marker in Sydney, Australia, and move the camera.
        LatLng location = new LatLng(Double.parseDouble(checker.getLatitude()),Double.parseDouble(checker.getLongtitude()));
        float zoomLevel = 15.0f;
        mMap.addMarker(new MarkerOptions().position(location).title("Your Location"));
        // mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney,zoomeLevel));
        CameraUpdate myLocation = CameraUpdateFactory.newLatLngZoom(location,zoomLevel);
        mMap.animateCamera(myLocation);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu,menu);
        return true;
    }
}
