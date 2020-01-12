package com.arkadiy.enter.smp3.dataObjects;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONException;
import org.json.JSONObject;


public class Task implements Parcelable {
    private JSONObject task;
    Parcelable CREATOR;

    public Task(JSONObject task) {
        this.task = task;
    }


    public Task(float userId, String start, String end, String name, String dec) {
        this.task = new JSONObject();
        setUserId(userId);
        setTimeDateStart(start);
        setTimeDateEnd(end);
        setNameTask(name);
        setDescription(dec);


    }

    public Task(String start, String end, String name, String dec,long groupId) {
        this.task = new JSONObject();
        setTimeDateStart(start);
        setTimeDateEnd(end);
        setNameTask(name);
        setDescription(dec);
        setGroupId(groupId);

    }
    public JSONObject getTask() {
        return task;
    }

    public void setTask(JSONObject task) {
        this.task = task;
    }

    public long getIdTask(){
        try {
            if(this.task.has("id"))
                return this.task.getLong("id");

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public String getNameTask(){
        try {
            if(this.task.has("name"))
                return this.task.getString("name");

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }



    public int getStatus(){
        try {
            if(this.task.has("status"))
                return this.task.getInt("status");

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return -1;
    }
    public int getUserId(){
        try {
            if(this.task.has("user_id"))
                return this.task.getInt("user_id");

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public int getGroupId(){
        try {
            if(this.task.has("group_id"))
                return this.task.getInt("group_id");

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public int getMAnagerId(){
        try {
            if(this.task.has("manager_id"))
                return this.task.getInt("manager_id");

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public String getDescription(){
        try {
            if(this.task.has("description"))
                return this.task.getString("description");

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String getTimeDateEnd(){
        try {
            if(this.task.has("time_date_end"))
                return this.task.getString("time_date_end");

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String getTimeDateStart(){
        try {
            if(this.task.has("time_date_start"))
                return this.task.getString("time_date_start");

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void setIdTask(long idTask){
        try {
            this.task.put("id",idTask);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void setNameTask(String nameTask){


        try {
            this.task.put("name",nameTask);
        } catch (JSONException e) {
            e.printStackTrace();
        }


    }



    public void setStatus(int status){
        try {
            this.task.put("status",status);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    public void setUserId(float userId){
        try {


            this.task.put("user_id",userId);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void setGroupId(long groupId){
        try {

            this.task.put("group_id",groupId);

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public void setMAnagerId(int mAnagerId){
        try {

            this.task.put("manager_id",mAnagerId);

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public void setDescription(String description){
        try {

            this.task.put("description",description);

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public void setTimeDateEnd(String timeDateEnd){
        try {
            this.task.put("date_end",timeDateEnd);

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public void setTimeDateStart(String timeDateStart) {
        try {

            this.task.put("date_start", timeDateStart);

        } catch (JSONException e) {
            e.printStackTrace();
        }


    }






    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {

    }
}





