package com.arkadiy.enter.smp3.services;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.android.volley.AuthFailureError;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.arkadiy.enter.smp3.activities.LogInActivity;
import com.arkadiy.enter.smp3.activities.MainActivity;
import com.arkadiy.enter.smp3.dataObjects.IHandler;
import com.arkadiy.enter.smp3.dataObjects.Task;
import com.arkadiy.enter.smp3.dataObjects.User;
import com.arkadiy.enter.smp3.dataObjects.Users;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.arkadiy.enter.smp3.utils.Constants.TASKS;

public class DataServices {
    private static ArrayList<Task> tasks = null;
    private static String punch_clock;
    private static Context context;
    public static final String ANSI_RED = "\u001B[31m";

    public static final Handler inserUserHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            Bundle bundle = msg.getData();
            if(bundle.getBoolean("status")){
                Intent intent = new Intent(context,MainActivity.class);
                context.startActivity(intent);
            }else{
                //handle error
            }
        }
    };

    public static void sendData(final String url, final JSONObject jsonObject, final RequestQueue requestQueue,
                                final Context context, int method, IHandler handler) {
        System.out.println("send data     "+ url);
        JsonObjectRequest jsonObjReq = null;
        DataServices.context = context;
        if(jsonObject != null){
            System.out.println();
            System.out.println("===========================================");
            System.out.println("\n\n"+" JSON OBJECT SEND TO SERVER \n\n");
            System.out.println(jsonObject.toString());
            System.out.println("===========================================");
        }else {
            System.out.println();
            System.out.println("===========================================");
            System.out.println("\n\n"+" JSON OBJECT SEND TO SERVER \n\n");
            System.out.println("JSON OBJECT IS NULL");
            System.out.println("===========================================");
        }

        jsonObjReq = new JsonObjectRequest(method, url, jsonObject,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        try {

                            JSONObject jsonResponse = new JSONObject(String.valueOf(response));
                            System.out.println();
                            System.out.println("=============================================================");
                            System.out.println("\n\n"+" ==RESPONSE FROM SERVER ==\n\n");
                            System.out.println(response);
                            System.out.println("=============================================================");
                            if (handler!=null){

                                sendDataToHandler(handler, String.valueOf(response));
                            }


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                System.out.println("error");

            }
        }) {
            /**
             * Passing some request headers*
             */
            @Override
            public Map getHeaders() throws AuthFailureError {
                HashMap headers = new HashMap();
                headers.put("user_name", User.getMyUserName());
                headers.put("user_id", String.valueOf(User.getMyUserId()));
                headers.put("private_key", User.getMyPrivateKey());
                return headers;
            }


        };
        requestQueue.add(jsonObjReq);
    }



    private static void setUsers(JSONArray departments) {

        for (int i = 0 ; i < departments.length(); i++){
            try {
                String name=((JSONObject)departments.get(i)).keys().next();
                JSONArray arrayOfUsers=((JSONObject)departments.get(i)).getJSONArray(name);
                ArrayList<Users>users=new ArrayList<>();
                LogInActivity.MYDEPARTMENTS.put(LogInActivity.DEPARTMANTS.get(Integer.parseInt(name)),Integer.parseInt(name));
                for (int j = 0 ; j < arrayOfUsers.length(); j++){
                    users.add(new Users(((JSONObject)arrayOfUsers.get(j))));
                }
                MainActivity.USERS.put(Integer.parseInt(name),users);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

    }

    private static void setDepatments(JSONArray globalRoles) {

        //{ "identifier" : 1, "description" : "cashier" }
        for(int i = 0 ; i < globalRoles.length(); i++){
            try {
                LogInActivity.DEPARTMANTS.put(((JSONObject )globalRoles.get(i)).getInt("identifier"),
                                                ((JSONObject )globalRoles.get(i)).getString("description"));

                LogInActivity.dataBaseHelperGlobalRoles.addRoles(((JSONObject )globalRoles.get(i)).getInt("identifier"),//set parameters in to local database
                        ((JSONObject )globalRoles.get(i)).getString("description"));

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }


    }

    private static void sendTasksBack(Handler mHandler,ArrayList<Task> tasks){
        Message msg = mHandler.obtainMessage();
        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList(TASKS,tasks);
        msg.setData(bundle);
        mHandler.sendMessage(msg);
    }

//    public static void sendTask(JSONObject task, RequestQueue requestQueue, Context context){
//        sendData(AppConfig.ADD_NEW_TASK,task,requestQueue,context,Constants.METHOD_POST, null);
//
//    }
    public  static ArrayList getTasksArray(final String url, final RequestQueue requestQueue, final Context context, int method,
                                           Handler handler){
        JSONObject object = new JSONObject();
//        sendData(url,object,requestQueue,context,0,handler);
        return tasks;

    }


    public static void sendDataToHandler(IHandler handler,String json){
        Message msg = new Message();
        Bundle bundle = new Bundle();

        if(handler!=null){

            bundle.putString("json",json);
            msg.setData(bundle);
            handler.sendMessage(msg);

        }
    }

//    public static void insertIntoUser(String str,Handler handler){
//        Message msg = new Message();
//        Bundle bundle = new Bundle();
//        if (str != null){
//            try {
//                JSONObject j = new JSONObject(str);
//
//                LogInActivity.dataBaseHelper.addUser(j.getString("user_id"),j.getString("user_name"),
//                            " ",j.getString("private_key"),j.getString("role"));
//
//                getUserIntoDataBase();
//
//              if(handler!=null){
//                  bundle.putBoolean("status",true);
//                  msg.setData(bundle);
//                  handler.sendMessage(msg);
//              }
//
//            } catch (JSONException e) {
//                bundle.putBoolean("status",false);
//                msg.setData(bundle);
//                handler.sendMessage(msg);
//                e.printStackTrace();
//
//            }
//        }else{
//
//            User.setUserName(null);
//            User.setPrivateKey(null);
//            User.setUserId("0");
//            User.setRole("0");
//            LogInActivity.dataBaseHelper.deleteParameters();
//            LogInActivity.dataBaseHelperGlobalRoles.deleteAll();
//        }
//
//
//    }

    public static void getUserIntoDataBase(User user){
        String userParameters[] = LogInActivity.dataBaseHelper.getUser();

        for (int i = 0 ; i < userParameters.length;i++){
            switch (i){
                case 1: // set user_id
                    user.setUserId(Integer.parseInt(userParameters[i]));
                    break;
                case 2:  // set user_name
                    user.setUserName(userParameters[i]);
                    break;
                case 4: // set private_key
                    user.setPrivateKey(userParameters[i]);
                    break;
                case 5: // set_role
                    user.setRole(Integer.parseInt(userParameters[i]));
                    break;
            }
        }
    }





}
