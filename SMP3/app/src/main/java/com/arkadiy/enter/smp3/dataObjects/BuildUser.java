package com.arkadiy.enter.smp3.dataObjects;

import android.content.Context;
import android.os.Bundle;
import android.os.Message;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.arkadiy.enter.smp3.config.AppConfig;
import com.arkadiy.enter.smp3.config.ResponseCode;
import com.arkadiy.enter.smp3.services.DataServices;

import org.json.JSONException;
import org.json.JSONObject;

public class BuildUser {
    private long userId;
    private String userName;
    private String firstName;
    private String secondName;
    private String password;
    private String city;
    private String street;
    private int house_number;
    private int doorNumber;
    private String telephone;
    private String email;
    private int role;
    private int sex;
    private JSONObject jsonNewUser;
    private RequestQueue requestQueue;
    private int action;
    private int managerId;
    private Context context;
    private long departmentId;


    public BuildUser(long userId,String userName, String firstName, String secondName, String password, String city, String street, int house_number,
                     int doorNumber, String telephone, String email, int role,long departmentId, int sex,int action ,Context context,IHandler iHandler) {

        this.userId = userId;
        this.userName = userName;
        this.firstName = firstName;
        this.secondName = secondName;
        this.password = password;
        this.city = city;
        this.street = street;
        this.house_number = house_number;
        this.doorNumber = doorNumber;
        this.telephone = telephone;
        this.email = email;
        this.role = role;
        this.departmentId = departmentId;
        this.sex  = sex;
        this.action = action;
        this.context = context;
        this.managerId = managerId;

        requestQueue = Volley.newRequestQueue(context);
        jsonNewUser = new JSONObject();
        try {
            jsonNewUser.put("user_id",this.getUserId());
            jsonNewUser.put("user_name", this.getUserName());
            jsonNewUser.put("first_name", this.getFirstName());
            jsonNewUser.put("second_name", this.getSecondName());
            jsonNewUser.put("password", this.getPassword());
            jsonNewUser.put("city", this.getCity());
            jsonNewUser.put("street", this.getStreet());
            jsonNewUser.put("house_number", this.getHouse_number());
            jsonNewUser.put("door_number", this.getDoorNumber());
            jsonNewUser.put("telephone", this.getTelephone());
            jsonNewUser.put("email", this.getEmail());
            jsonNewUser.put("role", this.getRole());
            jsonNewUser.put("sex",this.getSex());
           // jsonNewUser.put("managerId",this.getManagerId());
            jsonNewUser.put("action",this.getAction());
            jsonNewUser.put("gps",1);//TODO fix this parameter
            jsonNewUser.put("department",this.getDepartmentId());
            DataServices.sendData(AppConfig.ADD_NEW_USER,jsonNewUser, requestQueue, context,1, buildUser->{
                JSONObject json;
                Bundle bundle = new Bundle();
                bundle = buildUser.getData();
                Message msg = new Message();
                try{

                    json = new JSONObject(bundle.getString("json"));

                    bundle.putString("json",String.valueOf(json));
                    msg.setData(bundle);
                    iHandler.sendMessage(msg);

                }catch (JSONException e){
                    e.printStackTrace();
                }


                return true;
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
//        creatNewUser();
    }

    public JSONObject getJsonNewUser() {
        return jsonNewUser;
    }

    public String getUserName() {
        return userName;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getSecondName() {
        return secondName;
    }

    public String getPassword() {
        return password;
    }

    public String getCity() {
        return city;
    }

    public String getStreet() {
        return street;
    }

    public int getHouse_number() {
        return house_number;
    }

    public int getDoorNumber() {
        return doorNumber;
    }

    public String getTelephone() {
        return telephone;
    }

    public String getEmail() {
        return email;
    }

    public int getRole() {
        return role;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setSecondName(String secondName) {
        this.secondName = secondName;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public void setHouse_number(int house_number) {
        this.house_number = house_number;
    }

    public void setDoorNumber(int doorNumber) {
        this.doorNumber = doorNumber;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setRole(int role) {
        this.role = role;
    }

    public int getSex() {
        return sex;
    }

    public void setSex(int sex) {
        this.sex = sex;
    }

    public int getAction() {
        return action;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public void setJsonNewUser(JSONObject jsonNewUser) {
        this.jsonNewUser = jsonNewUser;
    }

    public long getDepartmentId() {
        return departmentId;
    }

    public int getManagerId() {
        return managerId;
    }

    public void setManagerId(int managerId) {
        this.managerId = managerId;
    }

    public void setDepartmentId(long departmentId) {
        this.departmentId = departmentId;
    }

    public void creatNewUser() {


//        String url = AppConfig.MAIN_SERVER_IP + AppConfig.MAIN_SERVER_PORT + "/manager/new_user";

//
//        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
//                url, this.getJsonNewUser(),
//                new Response.Listener() {
//                    @Override
//                    public void onResponse(Object response) {
//
//                    }
//                },
//                new Response.ErrorListener() {
//                    @Override
//                    public void onErrorResponse(VolleyError error) {
//                        //Failure Callback
//                    }
//                })
//        {
//            /** Passing some request headers* */
//            @Override
//            public Map getHeaders() throws AuthFailureError {
//                HashMap<String, String> headers = new HashMap<>();
//                //           headers.put("Content-Type", "application/json");
//
//
//                try {
//                    JSONObject j = new JSONObject(DataServices.readFile(FileConfig.USER_FILE,context));
//                    headers.put("user_name",j.getString("user_name"));
//                    headers.put("private_key",j.getString("private_key"));
//                    headers.put("user_id", String.valueOf(j.getInt("user_id")));
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }


//
//                return headers;
//            }
//        };
//        requestQueue.add(jsonObjReq);
//
//    }

//            "user_name":"as233e",
//            "first_name":"arkashaa",
//            "second_name":"nurmagamedov",
//            "password":"araara123",
//            "city":"yehud",
//            "street":"histadrut",
//            "house_number":2,
//            "door_number":1,
//            "telephone":"0546567712",
//            "email":"ahshjbsh@mail.com",
//            "role":2
    }
}
