package com.arkadiy.enter.smp3.dataObjects;

import android.content.Context;
import android.os.Bundle;
import android.os.Message;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.arkadiy.enter.smp3.config.AppConfig;
import com.arkadiy.enter.smp3.services.DataServices;
import com.arkadiy.enter.smp3.utils.Constants;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Admin extends Manager {
    private static ArrayList<User> AllUsers ;
    public Admin(JSONObject jsonObject) {
        super(jsonObject);
    }

    public static void createOrEditAlert(Context context, JSONObject newAlert, RequestQueue requestQueue, IHandler i_handlerNewAlert ){

        DataServices.sendData(AppConfig.CREATE_SENTENCE,newAlert,requestQueue, context, Constants.METHOD_POST,i_handler->{

            JSONObject json;
            Message msg = new Message();
            Bundle  bundle = i_handler.getData();

            try {
                json = new JSONObject(bundle.getString("json"));
                //Toast.makeText(context,json.getString("description"),Toast.LENGTH_LONG).show();
                //TODO create some response and send this response in to activity
                msg.setData(bundle);
                i_handlerNewAlert.sendMessage(msg);

            } catch (JSONException e) {
                e.printStackTrace();
            }

            return true;
        });

    }

    public static void createOrEditDepartment(Context context, JSONObject department, RequestQueue requestQueue, IHandler departmentHandler){


        DataServices.sendData(AppConfig.CREATE_DEPARTMENT,department,requestQueue, context, Constants.METHOD_POST,depHand->{

            JSONObject json;
            Message msg = new Message();
            Bundle  bundle = depHand.getData();

            try {
                json = new JSONObject(bundle.getString("json"));
                Toast.makeText(context,json.getString("message"),Toast.LENGTH_LONG).show();
                //TODO create some response and send this response in to activity


            } catch (JSONException e) {
                e.printStackTrace();
            }
            return true;
        });
    }

    public static ArrayList<User> getAllUsers() {
        return AllUsers;
    }

    public static void setAllUsers(User user) {
        if (AllUsers == null){
            AllUsers = new ArrayList<User>();
        }
        AllUsers.add(user);
    }

    public static void addShopSettingLocation(Context context, JSONObject newJsonSettingsLocation, RequestQueue requestQueue, IHandler iHandlerLcation){

        DataServices.sendData(AppConfig.ADD_SHOP_SETTING_LOCATION,newJsonSettingsLocation,requestQueue, context, Constants.METHOD_POST,iHendlerLoc->{

            Message msg = new Message();
            Bundle  bundle = iHendlerLoc.getData();
            msg.setData(bundle);
            iHandlerLcation.sendMessage(msg);



            return true;
        });
    }

    public static void getGpsCurrentParams(Context context, JSONObject newJsonSettingsLocation, RequestQueue requestQueue, IHandler iHandlerLcation){

        DataServices.sendData(AppConfig.GET_GPS_CURRENT_SETTINGS,newJsonSettingsLocation,requestQueue, context, Constants.METHOD_GET,result->{

            Message msg = new Message();
            Bundle  bundle = result.getData();
                msg.setData(bundle);
                iHandlerLcation.sendMessage(msg);


            return true;
        });
    }

    public static void getAlerts(Context context, JSONObject newJsonSettingsLocation, RequestQueue requestQueue, IHandler iHandlerLcation){

        DataServices.sendData(AppConfig.GET_ALERTS,newJsonSettingsLocation,requestQueue, context, Constants.METHOD_GET,result->{

            Message msg = new Message();
            Bundle  bundle = result.getData();
                msg.setData(bundle);
                iHandlerLcation.sendMessage(msg);


            return true;
        });
    }

    public static void replaceUser(User user) {


        for (int i = 0 ; i < AllUsers.size(); i++){
            if (user.getUserId() ==AllUsers.get(i).getUserId()){
                AllUsers.set(i,user);
                return;
            }
        }


    }

    public static List<String> getNameManagersList(){
        List<String >managersName = new ArrayList<>();
        for (int i = 0; i < Store.getManagerList().size() ; i++){

            managersName.add(Store.getManagerList().get(i).getUserName());
        }
        return managersName;
    }
}
