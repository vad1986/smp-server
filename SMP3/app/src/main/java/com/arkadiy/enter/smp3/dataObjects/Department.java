package com.arkadiy.enter.smp3.dataObjects;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Department {

    private long id;
    private String name;
    private  ArrayList users;
    private String description;
    private Manager manager;
    private ArrayList listTasks;

    public Department(int id,String description ){
        this.id = id;
        this.description = description;
    }

    public Department(JSONObject jsonObject){
    //fill users and tasks here by using innner methods
        try {
            setName(jsonObject.getJSONObject("data").getString("name"));
            setId(jsonObject.getJSONObject("data").getInt("id"));
            if (!jsonObject.isNull("users")) {
                fillUsers(jsonObject.getJSONArray("users"));
            }
            if (!jsonObject.isNull("tasks")) {
                fillTasks(jsonObject.getJSONArray("tasks"));
            }
            if (!jsonObject.isNull("manager")){
                Manager man = new Manager(jsonObject.getJSONObject("manager"));
                setManager(man);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private  void fillTasks(JSONArray jsonTasks)throws Exception {
        this.listTasks=new ArrayList<Task>();
        if(jsonTasks != null){
            for(int i = 0 ; i < jsonTasks.length(); i++){

                Task task=new Task((JSONObject) jsonTasks.get(i));
                //
                this.listTasks.add(task);
            }
        }

    }



    public void fillUsers(JSONArray usersArray)throws Exception{
        this.users=new ArrayList<User>();
        if(usersArray != null){
            for(int i = 0 ; i < usersArray.length(); i++){

                User user=new User((JSONObject) usersArray.get(i));
                //
                this.users.add(user);
                Admin.setAllUsers(user);//TODO delete and cheek params on Admin Class
                Store.setAllUsers(user);
            }
        }

        //run through array and add to users
    }
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }



    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

   public  ArrayList<User> getUsers() {
       return users;
    }

    public void setUsers(ArrayList<User> users) {
        this.users = users;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Manager getManager() {
        return manager;
    }
//
    public void setManager(Manager manager) {
        this.manager = manager;
    }
//
//    public List<Task> getTasks() {
//        return tasks;
//    }
//
   public void setTasks(ArrayList<Task> tasks) {
        this.listTasks = tasks;
   }

   public static List<String> getDepartmentName(){
        List<String> departmentName = new ArrayList<>();
        for (int i = 0 ; i < Store.getDepartments().size() ; i++){
            departmentName.add(Store.getDepartments().get(i).getName());
        }
        return departmentName;
   }
   public static long getDepartmentIdByName(String departmentName){
        long departmentId = -1;
       for (int i = 0 ; i < Store.getDepartments().size() ; i++){

           if (departmentName == Store.getDepartments().get(i).getName()){
               departmentId = Store.getDepartments().get(i).getId();
               break;
           }
       }
        return departmentId;
   }
}
