package com.arkadiy.enter.smp3.config;

public class AppConfig {
    public static String DATA_FILE_NAME="smp_data";
//    public static String AWS="http://15.188.69.193";
//    public static String LOCAL="localhost";
//    public static String LOCAL="http://10.0.0.9";
    public static String LOCAL="http://10.0.0.4";
    public static String MAIN_SERVER_IP=LOCAL;
    public static String SOCKET_SERVER_URL="";
    public static String MAIN_SERVER_PORT=":8080";
    public static String LOGIN=MAIN_SERVER_IP+MAIN_SERVER_PORT+"/login";
    public static String ADD_NEW_USER=MAIN_SERVER_IP+MAIN_SERVER_PORT+"/manager/new_user";
    public static String ADD_NEW_TASK=MAIN_SERVER_IP+MAIN_SERVER_PORT+"/user/new_task";
    public static String PUNCH_CLOCK=MAIN_SERVER_IP+MAIN_SERVER_PORT+"/user/punchClock";
    public static String LOGOUT_SERVER=MAIN_SERVER_IP+MAIN_SERVER_PORT+"/user/logout";
    public static String GET_USER_TASKS=MAIN_SERVER_IP+MAIN_SERVER_PORT+"/user/get_user_tasks/0";
    public static String CLOSE_TASK=MAIN_SERVER_IP+MAIN_SERVER_PORT+"/user/close-task";
    public static String GET_MY_USERS=MAIN_SERVER_IP+MAIN_SERVER_PORT+"/user/get_my_users";
    public static String CHECK_PRIVATE_KEY = MAIN_SERVER_IP+MAIN_SERVER_PORT+"/checkPrivate/";
    public static String CREATE_SENTENCE = MAIN_SERVER_IP+MAIN_SERVER_PORT+"/createSentence";
    public static String ONLINE_USERS = MAIN_SERVER_IP+MAIN_SERVER_PORT+"/onlineUsers";
    public static String SEND_ALERT = MAIN_SERVER_IP+MAIN_SERVER_PORT+"/message";
    public static String GET_GPS_CURRENT_SETTINGS = MAIN_SERVER_IP+MAIN_SERVER_PORT+"/punch_clock-params";
    public static String GET_ALERTS = MAIN_SERVER_IP+MAIN_SERVER_PORT+"/sentences";
    public static String ADD_SHOP_SETTING_LOCATION = MAIN_SERVER_IP+MAIN_SERVER_PORT+"/manager/addShopLocation";
    public static String GET_SHIFTS = MAIN_SERVER_IP+MAIN_SERVER_PORT+"/user-attendance/";
    public static String GET_SHIFTS_BY_USER = MAIN_SERVER_IP+MAIN_SERVER_PORT+"/user-attendance-all/";
    public static String GET_REPORT = MAIN_SERVER_IP+MAIN_SERVER_PORT+"/report";
    public static String FORGOT_PASSWORD = MAIN_SERVER_IP+MAIN_SERVER_PORT+"/forgot-password";
    public static String MANAGER_PUNCH_CLOCK = MAIN_SERVER_IP+MAIN_SERVER_PORT+"/manager/punchClock";
    public static String CREATE_DEPARTMENT = MAIN_SERVER_IP+MAIN_SERVER_PORT+"/createDepartment";


}
