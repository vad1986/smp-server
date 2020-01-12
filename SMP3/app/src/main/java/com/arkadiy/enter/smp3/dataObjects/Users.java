package com.arkadiy.enter.smp3.dataObjects;

import org.json.JSONObject;

public class Users {
    private  String userName;
    private String privateKey;
    private int userId;
    private int role;


    public Users(String userName,String privateKey,int userId) {
        userName=userName;
        privateKey=privateKey;
        userId=userId;
    }

    public Users(JSONObject jsonObject) {
        userId=jsonObject.optInt("userID");
        userName=jsonObject.optString("userName")+" "+jsonObject.optString("userLastName");
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPrivateKey() {
        return privateKey;
    }

    public void setPrivateKey(String privateKey) {
        this.privateKey = privateKey;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getRole() {
        return role;
    }

    public void setRole(int role) {
        this.role = role;
    }
}
