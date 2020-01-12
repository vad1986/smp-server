package com.arkadiy.enter.smp3.dataObjects;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class JsonAssembler {

    public static ArrayList<Task> getTasksArray(JSONArray tasksJsonArray){
        try {
            ArrayList<Task> taskArrayList =new ArrayList<>();
            for (int i = 0; i < tasksJsonArray.length(); i++) {
                taskArrayList.add(new Task((JSONObject) tasksJsonArray.get(i)));
            }
            return taskArrayList;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

}
