package com.arkadiy.enter.smp3.services;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Message;
import android.widget.Toast;

import com.arkadiy.enter.smp3.dataObjects.IHandler;

/**
 * Created by vadnu on 11/30/2018.
 */

public class GpsChecker implements LocationListener, Runnable {
    LocationManager locationManager;
    Context context;
    String latitude;
    String longtitude;
    IHandler handler;




    public GpsChecker(LocationManager locationManager, Context context, IHandler handler) {

        this.locationManager = locationManager;
        this.context=context;
        this.handler=handler;

    }


    private void sendResult(Location location) {
//        System.out.print(location);
        //Toast.makeText(this.context,"Location111 "+location.toString(), Toast.LENGTH_LONG).show();

    }



    @Override
    public void run() {


        LocationListener locationListener = new LocationListener() {
            public void onLocationChanged(Location location) {
                // Called when a new location is found by the network location provider.

                sendResult(location);
//                setLatitude(String.valueOf(location.getLatitude()));
//                setLongtitude(String.valueOf(location.getLongitude()));
            }

            public void onStatusChanged(String provider, int status, Bundle extras) {
            }

            public void onProviderEnabled(String provider) {
            }

            public void onProviderDisabled(String provider) {

            }
        };

    }

    @Override
    public void onLocationChanged(Location location) {
//        Log.i("","Latitude is--> "+location.getLatitude());
//        Log.w("","Longtitude is--> "+location.getLongitude());
        Message msg = null;
        try {
            setLatitude(String.valueOf(location.getLatitude()));
            setLongtitude(String.valueOf(location.getLongitude()));

            String x="Latitude is--> "+location.getLatitude();
            String y="Longtitude is--> "+location.getLongitude();


            //Toast.makeText(this.context,"LATITUDE="+x+" LONGTITUDE="+y, Toast.LENGTH_LONG).show();
            //locationManager.removeUpdates(this);

//            msg = new Message();
//            Bundle bundle = new Bundle();
//            bundle.putBoolean("status",true);
//            msg.setData(bundle);
//            handler.sendMessage(msg);
        } catch (Exception e) {
//            msg = new Message();
//            Bundle bundle = new Bundle();
//            bundle.putBoolean("status",false);
//            msg.setData(bundle);
//            handler.sendMessage(msg);
       }
            sendDataToHandler(handler);


//        txtview.setText(x+"\n"+y);


    }

    public static void sendDataToHandler(IHandler handler){
        Message msg = new Message();
        Bundle bundle = new Bundle();

        if(handler!=null){
            bundle.putBoolean("json",true);
            msg.setData(bundle);
            handler.sendMessage(msg);

        }else{

            bundle.putBoolean("status",false);
            msg.setData(bundle);
            handler.sendMessage(msg);
        }
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
//        System.out.print(provider.toString());
        //Toast.makeText(this.context,"status changed "+provider.toString(), Toast.LENGTH_LONG).show();

//        txtview.setText(provider.toString());

    }

    @Override
    public void onProviderEnabled(String provider) {
//        txtview.setText(provider.toString());
        //Toast.makeText(this.context,"provider enabled"+provider, Toast.LENGTH_LONG).show();

    }

    @Override
    public void onProviderDisabled(String provider) {
//        txtview.setText(provider.toString());
       // Toast.makeText(this.context,"provider disabled"+provider, Toast.LENGTH_LONG).show();

    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongtitude() {
        return longtitude;
    }

    public void setLongtitude(String longtitude) {
        this.longtitude = longtitude;
    }

}