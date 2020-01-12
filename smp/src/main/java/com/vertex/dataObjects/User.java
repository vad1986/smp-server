package com.vertex.dataObjects;

import com.fasterxml.jackson.annotation.JsonProperty;

public class User {
    @JsonProperty("userID")
    private int userID;

    @JsonProperty("userName")
    private String userName;

    @JsonProperty("userFirstName")
    private String userFirstName;

    @JsonProperty("userLastName")
    private String userLastName;

    @JsonProperty("password")
    private String password;

    @JsonProperty("privateKey")
    private String privateKey;

    @JsonProperty("userRole")
    private int userRole;

    @JsonProperty("departmentId")
    private int departmentId;

    @JsonProperty("city")
    private String city;

    @JsonProperty("telephone")
    private String telephone;

    @JsonProperty("email")
    private String email;

    @JsonProperty("street")
    private String street;

    @JsonProperty("managerId")
    private int managerId;

    @JsonProperty("gps")
    private int gps;

    @JsonProperty("houseNumber")
    private int houseNumber;

    @JsonProperty("doorNumber")
    private int doorNumber;

    @JsonProperty("sex")
    private int sex;

    public User() {}

    public User(int userID, String name) {
        this.userID = userID;
        this.userName = name;
    }

    public User(int userID) {
        this.userID = userID;
    }

    public User(int userID, String userName, String userFirstName, String userLastName, String password, String privateKey, int userRole, String city, String telephone, String email, int managerId) {
        this.userID = userID;
        this.userName = userName;
        this.userFirstName = userFirstName;
        this.userLastName = userLastName;
        this.password = password;
        this.privateKey = privateKey;
        this.userRole = userRole;
        this.city = city;
        this.telephone = telephone;
        this.email = email;
        this.managerId = managerId;
    }

    public int getHouseNumber() {
        return this.houseNumber;
    }

    public void setHouseNumber(int houseNumber) {
        this.houseNumber = houseNumber;
    }

    public String getStreet() {
        return this.street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public int getDoorNumber() {
        return this.doorNumber;
    }

    public void setDoorNumber(int doorNumber) {
        this.doorNumber = doorNumber;
    }

    public int getSex() {
        return this.sex;
    }

    public void setSex(int sex) {
        this.sex = sex;
    }

    public String getCity() {
        return this.city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getTelephone() {
        return this.telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public String getEmail() {
        return this.email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getManagerId() {
        return this.managerId;
    }

    public void setManagerId(int managerId) {
        this.managerId = managerId;
    }

    public String getUserName() {
        return this.userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserFirstName() {
        return this.userFirstName;
    }

    public void setUserFirstName(String userFirstName) {
        this.userFirstName = userFirstName;
    }

    public String getUserLastName() {
        return this.userLastName;
    }

    public void setUserLastName(String userLastName) {
        this.userLastName = userLastName;
    }

    public int getUserRole() {
        return this.userRole;
    }

    public void setDepartmentId(int departmentId) {
        this.departmentId = departmentId;
    }

    public int getDepartmentId() {
        return this.departmentId;
    }

    public void setUserRole(int userRole) {
        this.userRole = userRole;
    }

    public int getUserID() {
        return this.userID;
    }

    public void setUserID(int userID) {
        this.userID = userID;
    }

    public String getPassword() {
        return this.password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPrivateKey() {
        return this.privateKey;
    }

    public void setPrivateKey(String privateKey) {
        this.privateKey = privateKey;
    }

    public boolean equals(Object obj) {
        if (obj.getClass() == User.class) {
            User other = (User)obj;
            if (this.userID == other.getUserID())
                return true;
            return false;
        }
        if (this.userID == ((Integer)obj).intValue())
            return true;
        return false;
    }

    public int hashCode() {
        return this.userID;
    }

    public int getGps() {
        return this.gps;
    }

    public void setGps(int gps) {
        this.gps = gps;
    }
}
