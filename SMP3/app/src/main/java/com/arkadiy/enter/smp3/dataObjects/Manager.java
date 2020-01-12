package com.arkadiy.enter.smp3.dataObjects;

import android.os.Bundle;
import android.os.Message;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.arkadiy.enter.smp3.activities.App;
import com.arkadiy.enter.smp3.config.AppConfig;
import com.arkadiy.enter.smp3.config.ResponseCode;
import com.arkadiy.enter.smp3.services.DataServices;
import com.arkadiy.enter.smp3.utils.Constants;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static com.arkadiy.enter.smp3.activities.App.getContext;

public class Manager extends User {


    public Manager(JSONObject jsonObject) {
        super(jsonObject);
    }

    public void orderReport(int reportId, String fromDate, String toDate){
        //uses DataService to send json object to the server

    }
    public void editTask(Task task){
        // edit task and send to server

    }
    public void editShift(Shift shift){

    }

    public static void getReport(RequestQueue requestQueue , JSONObject jsonObject){

        DataServices.sendData(AppConfig.GET_REPORT,jsonObject,requestQueue, App.getContext(), Constants.METHOD_POST,reportHenler->{



            return true;
        });
    }
    public static void getShiftByUser(long userId,RequestQueue requestQueue,IHandler houresHadler){
        //GET: /user-attendance-all/(USER ID HERE)
        DataServices.sendData(AppConfig.GET_SHIFTS_BY_USER+userId,null,requestQueue,App.getContext(),
                Constants.METHOD_GET,reqHandlerHoures->{


                    JSONObject jsonRespons;
                    Message msg = new Message();
                    Bundle bundle = reqHandlerHoures.getData();
                    try {

                        if(bundle.getInt(Constants.RESPONSE_CODE) < ResponseCode.ERROR){
                            jsonRespons = new JSONObject(bundle.get("json").toString());
                            msg.setData(bundle);
                            houresHadler .sendMessage(msg);
                        }

                    }catch (Exception e){
                        e.printStackTrace();
                    }



            return true;
        });
    }

    public static void sendEditUserAttendancy(RequestQueue requestQueue,JSONObject jsonObject ,IHandler userAttendacyHandler){


        DataServices.sendData(AppConfig.MANAGER_PUNCH_CLOCK,jsonObject ,requestQueue ,App.getContext(),
                Constants.METHOD_POST,responseUserAttendancy->{

                    JSONObject jsonRespons;
                    Message msg = new Message();
                    Bundle bundle = responseUserAttendancy.getData();
                    try {

                        if(bundle.getInt(Constants.RESPONSE_CODE) < ResponseCode.ERROR){
                            jsonRespons = new JSONObject(bundle.get("json").toString());
                            Toast.makeText(getContext(),String.valueOf(jsonRespons.getInt("response_code")),Toast.LENGTH_LONG).show();
                            msg.setData(bundle);
                            userAttendacyHandler.sendMessage(msg);
                        }

                    }catch (Exception e){
                        e.printStackTrace();
                    }

            return true;
        });

    }

    public static List<String> getRollNames() {

        List<String> rollNames = new ArrayList<>();
        for (int i = 0 ; i < Store.rolles.size() ; i++){

            rollNames.add(Store.rolles.get(i).getNameRolle());

        }
        return rollNames;
    }

    public static int getRolleIdByName(String nameR){
        int rollId;
        for (int i = 0 ; i < Store.rolles.size() ; i++){
            if (nameR == Store.rolles.get(i).getNameRolle()){
                rollId = Store.rolles.get(i).getIdRolle();

                return rollId;
            }
        }
        return -1; // retunr -1 if dont have nameRolle
    }

    public static String getRolleNameById(int rollId){
        String nameR;
        for (int i = 0 ; i < Store.rolles.size() ; i++){
            if (rollId == Store.rolles.get(i).getIdRolle()){
                nameR= Store.rolles.get(i).getNameRolle();

                return nameR;
            }
        }
        return null; // retunr null if dont have nameRolle
    }

    public static long getManagerIdByName(String name){

        long id = 0;
        for (int i = 0 ; i < Store.getManagerList().size() ; i++){

            if (name == Store.getManagerList().get(i).getUserName()){

                id =  Store.getManagerList().get(i).getUserId();
                break;
            }
        }
        return id;

    }

}
