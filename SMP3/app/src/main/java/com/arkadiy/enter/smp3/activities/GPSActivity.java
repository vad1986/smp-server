package com.arkadiy.enter.smp3.activities;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.arkadiy.enter.smp3.R;
import com.arkadiy.enter.smp3.dataObjects.Admin;
import com.arkadiy.enter.smp3.dataObjects.Department;
import com.arkadiy.enter.smp3.services.GpsChecker;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONException;
import org.json.JSONObject;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class GPSActivity extends FragmentActivity implements OnMapReadyCallback {
    private TextView newLocationTextView;
    private Button getNewLocationBtn;
    private Button cancelButton;
    private Button sendNewLocationBtn;
    private GpsChecker checker;
    private Criteria criteria;
    private LocationManager locationManager;
    private Switch sw;
    private EditText radiusSizeEditText;
    private RequestQueue requestQueue;
    private int StoreGPSAnable;
    private GoogleMap mMap;
    private Geocoder geocoder;
    private List<Address>addresses;
    private Spinner  attemptsSpinner;
    private ArrayAdapter<String> attemptsAdapter;
    private List<String> attemptsList;
    private int currentAttempts;
    private String currentRadius="0.0";
    private double currentLatitude;
    private double currentLongtitude;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gps);
        App.setContext(this);
        fillAttempts();
        attemptsAdapter = new ArrayAdapter(App.getContext(),android.R.layout.simple_spinner_dropdown_item,attemptsList);
        sw = (Switch)findViewById(R.id.switchGPS) ;
        requestQueue = Volley.newRequestQueue(this);
        getNewLocationBtn = (Button) findViewById(R.id.get_New_Location_Btn);
        sendNewLocationBtn = (Button) findViewById(R.id.send_new_location_Btn);
        cancelButton = (Button) findViewById(R.id.cancel_button);
        radiusSizeEditText = (EditText) findViewById(R.id.radiusSize_EditText);
        attemptsSpinner = (Spinner) findViewById(R.id.attemptsSpinner);
        attemptsSpinner.setAdapter(attemptsAdapter);


        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        geocoder = new Geocoder(this, Locale.getDefault());

        initGPS();




        sw.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if(!isChecked){

                    getNewLocationBtn.setVisibility(View.INVISIBLE);
                    radiusSizeEditText.setVisibility(View.INVISIBLE);
                    Toast.makeText(App.getContext(),"You chose an option without GPS ",Toast.LENGTH_LONG).show();
                    StoreGPSAnable = 0;
                }
                else {
                    radiusSizeEditText.setText(currentRadius);
                    getNewLocationBtn.setVisibility(View.VISIBLE);
                    radiusSizeEditText.setVisibility(View.VISIBLE);
                    Toast.makeText(App.getContext(),"You chose to use GPS ",Toast.LENGTH_LONG).show();
                    StoreGPSAnable = 1;
                    setLocationOnMap(currentLatitude,currentLongtitude);

                }

            }
        });
        getNewLocationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                checkLocation();




            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(App.getContext(), AdminActivity.class);
                App.getContext().startActivity(intent);
            }
        });

        sendNewLocationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO: send string for new location to server
                if(radiusSizeEditText.getText().toString() != ""){

                    sendToServer();

                }
            }
        });


        attemptsSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                currentAttempts = Integer.parseInt(attemptsList.get(position));
                attemptsSpinner.setSelection(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        getCurrentParams();



    }

    private void getCurrentParams() {
        Admin.getGpsCurrentParams(this,null,requestQueue,handler->{
            try {

                JSONObject json = new JSONObject(handler.getData().getString("json"));
                attemptsSpinner.setSelection(json.getInt("number")-1);
                currentLatitude=json.getDouble("Latitude");
                currentLongtitude=json.getDouble("Longtitude");
                currentRadius=String.valueOf(json.getDouble("Radius"));

                if(json.getInt("gps")==1){
                    sw.setChecked(true);
                    StoreGPSAnable = 1;
                    getNewLocationBtn.setVisibility(View.VISIBLE);
                    radiusSizeEditText.setVisibility(View.VISIBLE);
                    radiusSizeEditText.setText(String.valueOf(json.getDouble("Radius")));
                    setLocationOnMap(currentLatitude,currentLongtitude);

                }else{
                    sw.setChecked(false);
                    StoreGPSAnable = 0;
                    getNewLocationBtn.setVisibility(View.INVISIBLE);
                    radiusSizeEditText.setVisibility(View.INVISIBLE);

                }


            } catch (JSONException e) {
                e.printStackTrace();
            }

            return true;
        });
    }

    private void fillAttempts() {
        attemptsList=new ArrayList<>();
        for (int i=1;i<6;i++){
            attemptsList.add(String.valueOf(i));
        }


    }

    private void sendToServer(){
        try {

            JSONObject newJsonSettingsLocation = new JSONObject()
                    .put("Latitude",Double.parseDouble(checker.getLatitude()))
                    .put("number",currentAttempts)
                    .put("Longtitude",Double.parseDouble(checker.getLongtitude()))
                    .put("Size",Double.parseDouble(radiusSizeEditText.getText().toString()))
                    .put("gps",StoreGPSAnable);
            Admin.addShopSettingLocation(GPSActivity.this,newJsonSettingsLocation,requestQueue,handler->{
                try {
                    JSONObject json = new JSONObject(handler.getData().getString("json"));
                    Toast.makeText(App.getContext(),json.getString("message"),Toast.LENGTH_LONG).show();

                } catch (JSONException e) {
                    e.printStackTrace();
                }


                return true;
            });

        } catch (JSONException e) {
            e.printStackTrace();
        }


    }
    private void initGPS() {



        locationManager = (LocationManager) GPSActivity.this.getSystemService(Context.LOCATION_SERVICE);

        checker = new GpsChecker(locationManager, GPSActivity.this, handConfGps->{

            if (handConfGps.getData()!=null) {

            }
            return true;
        });
//
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
        checker.onLocationChanged(locationManager.getLastKnownLocation(checker.getLatitude()+checker.getLongtitude()));
//        newLocationTextView.setText("Latitude: "+checker.getLatitude() + " Longtitude: "+checker.getLongtitude());

        setLocationOnMap(Double.parseDouble(checker.getLatitude()),Double.parseDouble(checker.getLongtitude()));

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
    }

    public void setLocationOnMap(double latitude,double longtitude){
        // Add a marker in Sydney, Australia, and move the camera.
        LatLng location = new LatLng(latitude,longtitude);

        float zoomLevel = 18.0f;


        mMap.addMarker(new MarkerOptions().position(location).title("Your Location"));
        // mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney,zoomeLevel));
       // CameraUpdate myLocation = CameraUpdateFactory.newLatLngZoom(location,zoomLevel);
       // mMap.animateCamera(myLocation);

        CircleOptions circleoptions=new CircleOptions().strokeWidth(2).strokeColor(Color.BLUE).fillColor(Color.parseColor("#500084d3"));
        mMap.addMarker(new MarkerOptions().position(location));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(location));
        Circle circle=mMap.addCircle(circleoptions.center(location).radius(Double.parseDouble(radiusSizeEditText.getText().toString())));
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(circleoptions.getCenter(),zoomLevel));
        getAdrres();


    }

    private void getAdrres(){

        try{
            addresses = geocoder.getFromLocation(Double.parseDouble(checker.getLatitude()),Double.parseDouble(checker.getLongtitude()),1);
            String address = addresses.get(0).getAddressLine(0);
            String area = addresses.get(0).getLocality();
            String city = addresses.get(0).getAdminArea();
            String country = addresses.get(0).getCountryName();
            String postalCode = addresses.get(0).getPostalCode();

            newLocationTextView.setText("Address: "+address+"\n"+
                                        "Area: "+area+"\n"+
                                        "City: "+city+"\n"+
                                        "Country: "+country+"\n"+
                                        "Postal Code: "+postalCode+"\n"+
                                        "Latit"+checker.getLatitude()+"\n"+
                                        "LONG"+checker.getLongtitude());

        }catch (Exception e){
            e.printStackTrace();
        }

    }

    public int getZoomLevel(Circle circle) {
        int zoomlevel = 3;
        if (circle != null) {
            double radius = circle.getRadius() + circle.getRadius() / 2;
            double scale = radius / 500;
            zoomlevel = (int) (16 - Math.log(scale) / Math.log(2));
        }
        return zoomlevel;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu,menu);
        return true;
    }
}
