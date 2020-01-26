package com.arkadiy.enter.smp3.dataObjects;

import android.app.Notification;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.arkadiy.enter.smp3.R;
import com.arkadiy.enter.smp3.activities.App;
import com.arkadiy.enter.smp3.activities.LogInActivity;
import com.arkadiy.enter.smp3.activities.MainActivity;
import com.arkadiy.enter.smp3.config.AppConfig;
import com.arkadiy.enter.smp3.config.ResponseCode;
import com.arkadiy.enter.smp3.services.DataServices;
import com.arkadiy.enter.smp3.services.SocketServices;
import com.arkadiy.enter.smp3.utils.Constants;
import com.arkadiy.enter.smp3.utils.ConstantsJson;
import com.arkadiy.enter.smp3.utils.FileService;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import static com.arkadiy.enter.smp3.activities.App.CHANNEL_1_ID;
import static com.arkadiy.enter.smp3.activities.App.getContext;

public class User {

    private static IHandler taskActivityHandler;
    private static NotificationManagerCompat notificationManager;
    private static IHandler alertActivityHandler;
    private long userId;
    private String userName;
    private String userFirstName;
    private String password;
    private int role;
    private String userLastName;
    private String street;
    private int houseNumber;
    private int doorNumber;
    private int telephone;

    private int sex;
    private String privateKey;
    private String phone;
    private String city;
    private String email;
    private int status;
    private int groupId;
    private int managerId;
    public static ArrayList<Task>myTasks;
    private List<Alert>recivedAlerts;
    private List<Alert>sentAlerts;
    private List<Shift>lastShifts;
    public static RequestQueue requestQueue;
    private static Handler handler;
    private static long myUserId;
    private static String myPrivateKey;
    private static String myUserName;
    private static String myEmail;
    private static int myRole;
    private static int myDepartmentId;
    private  int departmentId;
    private static int GPSAnuble;
    private static int numberNewTack; //TODO sum new task in this var
    private static Department myDepartment;

    private static Map<Integer,String>userOnline;


    public User() {
    }

    public User(JSONObject jsonObject) {

        try {
            if(allDataExists(jsonObject)){
                this.city = jsonObject.getString("city");
                this.email = jsonObject.getString("email");
                this.status = jsonObject.getInt("status");
                this.userId = jsonObject.getLong("userID");
                //TODO i need get this parameters from server
                this.street = jsonObject.getString("street");

                this.houseNumber = jsonObject.optInt("houseNumber");
                this.sex = jsonObject.optInt("sex");
                this.doorNumber = jsonObject.optInt("doorNumber");
                this.telephone = jsonObject.optInt("telephone");
                this.userName = jsonObject.getString("userName");
                this.password = jsonObject.getString("password");
                this.role = jsonObject.getInt("userRole");

                this.managerId = jsonObject.optInt("managerId");
                this.phone = jsonObject.getString("telephone");
                this.userFirstName = jsonObject.getString("userFirstName");
                this.userLastName = jsonObject.getString("userLastName");
                this.myEmail = jsonObject.getString("email");
                //TODO: create GPS parameters
                //TODO: check if simple user get value -1 if yes delete operator if
                this.departmentId = jsonObject.getInt(ConstantsJson.DEPARTMENT_ID);
            }else{
                System.out.println("++++++++++++   User doesn't have all the needed data ++++++++++++");
            }


        } catch (JSONException e) {
            e.printStackTrace();
        }


    }

    private boolean allDataExists(JSONObject jsonObject) {

       return jsonObject.has("city") &&
        jsonObject.has("email") &&
        jsonObject.has("status") &&
        jsonObject.has("userID") &&
        jsonObject.has("street") &&
        jsonObject.has("houseNumber") &&
        jsonObject.has("sex") &&
        jsonObject.has("doorNumber") &&
        jsonObject.has("telephone") &&
        jsonObject.has("userName") &&
        jsonObject.has("password") &&
        jsonObject.has("userRole") &&
        jsonObject.has("managerId") &&
        jsonObject.has("telephone") &&
        jsonObject.has("userFirstName") &&
        jsonObject.has("userLastName") &&
        jsonObject.has("email") &&
        jsonObject.has(ConstantsJson.DEPARTMENT_ID);

    }


    public static JSONObject parsFromObjToJSON(User user){
        JSONObject JSuser = new JSONObject();
        try {

//        this.city = jsonObject.getString("city");
//        this.email = jsonObject.getString("email");
//        this.status = jsonObject.getInt("status");
//        this.userId = jsonObject.getInt("userID");
//        this.userName = jsonObject.getString("userName");
//        this.role = jsonObject.getInt("userRole");
//        this.managerId = jsonObject.getInt("managerId");
//        this.phone = jsonObject.getString("telephone");
//        this.userFirstName = jsonObject.getString("userFirstName");
//        this.userLastName = jsonObject.getString("userLastName");
//        this.myEmail = jsonObject.getString("mail");
            JSuser.put("userID",user.getUserId());
            JSuser.put("userName", user.getUserName());
            JSuser.put("userFirstName", user.getUserFirstName());
            JSuser.put("userLastName", user.getUserLastName());
            JSuser.put("status",user.getStatus());
            JSuser.put("password", user.getPassword());
            JSuser.put("city", user.getCity());
            JSuser.put("street", user.getStreet());
            JSuser.put("houseNumber", user.getHouseNumber());
            JSuser.put("doorNumber", user.getDoorNumber());
            JSuser.put("managerId",user.getManagerId());
            JSuser.put("telephone", user.getTelephone());
            JSuser.put("email", user.getEmail());
            JSuser.put("userRole", user.getRole());
            JSuser.put("departmentId", user.getDepartmentId());
            JSuser.put("sex", user.getSex());

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return JSuser;

    }

    public static void addNewTask(JSONObject taskJS){
        Task newTask = new Task(taskJS);
        if (myTasks == null){
            myTasks = new ArrayList<>();
        }
        myTasks.add(newTask);
        Message msg = new Message();
        Bundle bundle=new Bundle();
        bundle.putString("task",taskJS.toString());
        msg.setData(bundle);
       taskActivityHandler.sendMessage(msg);

    }

    public static void setTaskHandler(IHandler handler) {
        User.taskActivityHandler=handler;
    }
    public static void setAlertHandler(IHandler handler) {
        User.alertActivityHandler=handler;
    }

    public static void setMyDepartment(Department myDepartment) {
        User.myDepartment = myDepartment;
    }

    public static Department getMyDepartment() {
        return myDepartment;
    }

    public void closeTask(int taskPosition){
        if(myTasks!=null){
            JSONObject json=myTasks.get(taskPosition).getTask();
            DataServices.sendData(AppConfig.CLOSE_TASK,json,requestQueue,App.getContext(), Constants.METHOD_POST, x->{

                return true;
            });

//            DataServices.sendData(AppConfig.CLOSE_TASK,json,requestQueue,User.context, Constants.METHOD_POST,
//                    handler->{
//
//            });
        }

    }


    public static void logIn(JSONObject json, TextView errorResponseView){
        requestQueue= Volley.newRequestQueue(App.getContext());
        DataServices.sendData(AppConfig.LOGIN,json,requestQueue,
                getContext(),Constants.METHOD_POST,handLogIn->{
            try {
                    Bundle bundle = handLogIn.getData();
                    JSONObject jsonRespons;
                    if(bundle.getString("json")!=null) {

                        jsonRespons =new JSONObject(bundle.getString("json"));
                        if (jsonRespons.getInt(ConstantsJson.RESPONSE_CODE) < ResponseCode.ERROR)
                        {
                            successfulLoginAndFillData(jsonRespons);

                        }else {
                            errorResponseView.setText("Incorrect Username or Password");
                            return false;
                        }



                        }
                    }catch (JSONException e) {
                     e.printStackTrace();
            }

            return true;
                });

    }

    public static void successfulLoginAndFillData(JSONObject jsonRespons){
        saveUser(jsonRespons);
        Store.fillStoreData(jsonRespons);
        SocketServices messageSender=new SocketServices();
        messageSender.execute();
        Intent intent = new Intent(App.getContext(), MainActivity.class);
        App.getContext().startActivity(intent);
    }


    public static void sendAlert(Context context,JSONObject alert,RequestQueue requestQueue){
        // Sends  JSObject to data serveses and insert into sent alerts
        //TODO: send please handler
        DataServices.sendData(AppConfig.SEND_ALERT,alert,requestQueue,context,Constants.METHOD_POST,null);

    }

    //Save My Users params
    public static void saveUser (JSONObject json){
        try {
            requestQueue=Volley.newRequestQueue(App.getContext());
            User.setMyUserId(json.getLong("user_id"));
            User.setMyRole(json.getInt("role"));
            User.setMyPrivateKey(json.getString("private_key"));
            User.setMyUserName(json.getString("user_name"));
            User.setGPSAnuble(json.getInt("gps"));
            User.setMyDepartmentId(json.getInt("departmentId"));

            String string=createStringForFile(User.getMyPrivateKey(),User.getMyUserId());
            FileService.save(string);


            //            myAcount.put("user_id",json.getInt("user_id"));
            //            myAcount.put("role",json.getInt("role"));
            //            myAcount.put("private_key",json.getInt("private_key"));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        //json.getString("user_id"),j.getString("user_name")," ",j.getString("private_key"),j.getString("role")

    }


    private static String createStringForFile(String privateKey,long userId){
        StringBuilder stringBuilder=new StringBuilder();

        String string=stringBuilder.append(privateKey).append("\n").append(userId).toString();

        return string;
    }
    public static void sendNewTask (boolean personal,Context context, JSONObject jsonTask,RequestQueue requestQueue,IHandler sendTaskHandler){

        DataServices.sendData(AppConfig.ADD_NEW_TASK,jsonTask,requestQueue,context,Constants.METHOD_POST,sendTask->{
            JSONObject object;
            Message msg = new Message();
            Bundle bundle = sendTask.getData();
            if (bundle.getString("json")!=null){
                try {
                    object = new JSONObject(bundle.getString("json"));
                    if (object.getInt("response_code") < ResponseCode.ERROR){
                        msg.setData(bundle);
                        sendTaskHandler.sendMessage(msg);
                        return true;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            return false;
        });

    }
    public static void getTasksFromServer(Context context,IHandler i_handler){

        DataServices.sendData(AppConfig.GET_USER_TASKS,null,requestQueue,context,Constants.METHOD_GET,handTask->{
            try {

                JSONObject json;
                Message msg = new Message();
                Bundle bundle = handTask.getData();
            if(bundle.getString("json")!=null) {
                json =new JSONObject(bundle.getString("json"));
                if(!json.isNull("data")){
                    JSONArray jsonArray = json.getJSONArray("data");
                    setTasks(jsonArray);
                    msg.setData(bundle);
                    i_handler.sendMessage(msg);
                }else {
                    json.put("response_code",ResponseCode.ERROR);
                    Bundle bundle2 = new Bundle();
                    bundle2.putString("json",json.toString());
                    msg.setData(bundle2);
                    i_handler.sendMessage(msg);
                }


                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            return true;
        });
    }


    public static void punchClock(Context contextClock,JSONObject jsonClock,String url,RequestQueue requestQueue , IHandler i_handler){

        DataServices.sendData(AppConfig.PUNCH_CLOCK, jsonClock, requestQueue, contextClock,Constants.METHOD_POST, handClock->{
                    JSONObject json;
                     Message msg = new Message();
                    Bundle bundle = handClock.getData();
            try {
                json = new JSONObject(bundle.getString("json"));

                    if(json.getInt(Constants.RESPONSE_CODE) == ResponseCode.PUNCH_CLOCK){

                        bundle.putInt(ConstantsJson.RESPONSE_CODE,json.getInt(Constants.RESPONSE_CODE));
                        msg.setData(bundle);
                        i_handler.sendMessage(msg);

                        return true;
                    }else if(json.getInt(Constants.RESPONSE_CODE) == ResponseCode.NOT_IN_RANGE){
                        Toast.makeText(contextClock,json.getString(ConstantsJson.MESSAGE),Toast.LENGTH_LONG).show();
                        bundle.putInt(ConstantsJson.RESPONSE_CODE,json.getInt(Constants.RESPONSE_CODE));
                        msg.setData(bundle);
                        i_handler.sendMessage(msg);
                    }else return false;
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return false;
                });


    }
    public static void getShiftsFromServer(IHandler i_handler){
        DataServices.sendData(AppConfig.GET_SHIFTS+"2",null,requestQueue,getContext(),Constants.METHOD_GET,getShiftsHand->{

            JSONObject jsonRespons;
            Message msg = new Message();
            Bundle bundle = getShiftsHand.getData();
            try {

                if(bundle.getInt(Constants.RESPONSE_CODE) < ResponseCode.ERROR){
                    jsonRespons = new JSONObject(bundle.get("json").toString());
                    Toast.makeText(getContext(),String.valueOf(jsonRespons.getInt("response_code")),Toast.LENGTH_LONG).show();
                    msg.setData(bundle);
                    i_handler .sendMessage(msg);
                }

            }catch (Exception e){
                e.printStackTrace();
            }

            return true;
        });
    }

    public static void setTasks(JSONArray tasksArray){
        myTasks = new ArrayList<Task>();
        for (int i = 0 ;i < tasksArray.length() ; i++){

            try {
                Task task = new Task((JSONObject) tasksArray.get(i));
                myTasks.add(task);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

    }
    public  JSONObject getJson() {
        try {
            return  new JSONObject()
                    .put("user_name",userName)
                    .put("private_key",privateKey)
                    .put("user_id", getUserId());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return  null;
    }
    public static void getAlertFromSocket(Alert alert){
        String fromUser = "";
        for (int i = 0 ; i<Store.sizeUserOnline() ; i++){

            try {
                if(alert.getUserFrom() == Store.getUsersOnline().getJSONObject(i).getInt("user_id")){
                    fromUser = Store.getUsersOnline().getJSONObject(i).getString("user_name");
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
        Message msg = new Message();
        Bundle bundle=new Bundle();
        bundle.putString("alert","");
        msg.setData(bundle);
        alertActivityHandler.sendMessage(msg);
        notifyApp(alert,"New Alert from user: " + fromUser +" Title: " +alert.getName());


    }

    public static void notifyApp(Alert alert,String message){
        notificationManager = NotificationManagerCompat.from(App.getContext());
        Notification notification = new NotificationCompat.Builder(App.getContext(),CHANNEL_1_ID)
                .setSmallIcon(R.drawable.ic_one)
                .setContentTitle(message)
                .setContentText(alert.getDescription())
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setCategory(NotificationCompat.CATEGORY_ALARM)
                .build();

        notificationManager.notify(1,notification);
    }
    public int getDepartmentId() {
        return departmentId;
    }

    public static void setMyDepartmentId(int departmentId) {
        User.myDepartmentId = departmentId;
    }

    public static long getMyUserId() {
        return myUserId;
    }

    public static void setMyUserId(long myUserId) {
        User.myUserId = myUserId;
    }

    public static String getMyPrivateKey() {
        return myPrivateKey;
    }

    public static void setMyPrivateKey(String myPrivateKey) {
        User.myPrivateKey = myPrivateKey;
    }

    public static String getMyUserName() {
        return myUserName;
    }

    public static void setMyUserName(String myUserName) {
        User.myUserName = myUserName;
    }

    public static int getMyRole() {
        return myRole;
    }

    public static void setMyRole(int myRole) {
        User.myRole = myRole;
    }

    public long getUserId() {
        return userId;
    }

    public int getRole() {
        return role;
    }

    public List<Alert> getSentAlerts() {
        return sentAlerts;
    }

    public void setSentAlerts(List<Alert> sentAlerts) {
        this.sentAlerts = sentAlerts;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public String getUserFirstName() {
        return userFirstName;
    }

    public void setUserFirstName(String userFirstName) {
        this.userFirstName = userFirstName;
    }

    public void setRole(int role) {
        this.role = role;
    }

    public String getUserLastName() {
        return userLastName;
    }

    public void setUserLastName(String userLastName) {
        this.userLastName = userLastName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getGroupId() {
        return groupId;
    }

    public void setGroupId(int groupId) {
        this.groupId = groupId;
    }

    public int getManagerId() {
        return managerId;
    }

    public void setManagerId(int managerId) {
        this.managerId = managerId;
    }

    public List<Task> getTasks() {
        return myTasks;
    }

    public void setTasks(ArrayList<Task> tasks) {
        this.myTasks = tasks;
    }

    public List<Alert> getRecivedAlerts() {
        return recivedAlerts;
    }

    public void setRecivedAlerts(List<Alert> recivedAlerts) {
        this.recivedAlerts = recivedAlerts;
    }

    public List<Alert> getSendAlerts() {
        return sentAlerts;
    }

    public void setSendAlerts(List<Alert> sendAlerts) {
        this.sentAlerts = sendAlerts;
    }

    public List<Shift> getLastShifts() {
        return lastShifts;
    }

    public void setLastShifts(List<Shift> lastShifts) {
        this.lastShifts = lastShifts;
    }


    public static int getNumberNewTack() {
        return numberNewTack;
    }

    public static void setNumberNewTack(int numberNewTack) {
        User.numberNewTack = numberNewTack;
    }


    public static void logOut(){

        DataServices.sendData(AppConfig.LOGOUT_SERVER,new JSONObject(),requestQueue, App.getContext(),Constants.METHOD_POST,handLogOut->{
            FileService.removePrivateKey();
            Intent intent = new Intent(App.getContext(), LogInActivity.class);
            App.getContext().startActivity(intent);
                    return true;
        });

    }

    public  void setUserName(String userName) {
        this.userName = userName;
    }

    public  String getUserName() {
        return userName;
    }

    public  String getPrivateKey() {
        return privateKey;
    }

    public  void setPrivateKey(String privateKey) {
        this.privateKey = privateKey;
    }

    public static int getGPSAnuble() {
        return GPSAnuble;
    }

    public static void setGPSAnuble(int GPSAnuble) {
        User.GPSAnuble = GPSAnuble;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public int getHouseNumber() {
        return houseNumber;
    }

    public void setHouseNumber(int houseNumber) {
        this.houseNumber = houseNumber;
    }

    public int getDoorNumber() {
        return doorNumber;
    }

    public void setDoorNumber(int doorNumber) {
        this.doorNumber = doorNumber;
    }

    public int getTelephone() {
        return telephone;
    }


    public void setTelephone(int telephone) {
        this.telephone = telephone;
    }

    public int getSex() {
        return sex;
    }

    public void setSex(int sex) {
        this.sex = sex;
    }


    public static String getMyEmail() {
        return myEmail;
    }

    public static void setMyEmail(String myEmail) {
        User.myEmail = myEmail;
    }

    public static int getMyDepartmentId() {
        return myDepartmentId;
    }
}

