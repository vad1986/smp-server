package com.vertex.config;

public class SqlQueries {
    public static final String ADMIN_CREATE_DEPARTMENT = "call sys.create_department(?,?,?,?,?,?);";

    public static final String GET_TASK_REPORT_FOR_ALL = "call sys.get_report_tasks_all_users(?,?)";

    public static final String GET_TASK_REPORT_FOR_ONE = "call sys.get_report_tasks_for_user(?,?,?)";

    public static final String GET_TASK_REPORT_FOR_DEPARTMENT = "call sys.get_report_tasks_for_department(?,?,?)";

    public static final String GET_PUNCH_CLOCK_REPORT_FOR_ALL = "call sys.get_clock_report_for_all(?,?)";

    public static final String GET_PUNCH_CLOCK_REPORT_FOR_ONE = "call sys.get_clock_report_for_user(?,?,?)";

    public static final String GET_PUNCH_CLOCK_REPORT_FOR_DEPARTMENT = "call sys.get_clock_report_for_department(?,?,?)";

    public static String NEW_USER = "call sys.create_user(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

    public static final String LOG_OUT_USER = "call sys.logout_user(?,?,?,?)";

    public static final String ADD_SHOP_LOCATION = "call sys.add_shop_location(?,?,?,?,?)";

    public static String PUNCH_CLOCK = "call sys.punch_clock(?,?,?,?,?,?,?,?,?);";

    public static String ADMIN_CREATE_ALERT = "call sys.create_alert(?,?,?,?,?,?,?);";

    public static String LOGIN_USER = "call sys.login_user(?,?,?,?,?,?,?,?,?,?,?);";

    public static String NEW_TASK = "call sys.insert_new_task(?,?,?,?,?,?,?,?,?)";

    public static String CLOSE_TASK = "call sys.close_task(?,?,?,?)";

    public static String GET_OPEN_MANAGER_TASKS = "select sys.get_open_tasks(%d)res from dual";

    public static String GET_USER_TASKS = "select sys.get_user_tasks(%d,%d)res from dual;";

    public static String GET_LOGGED_IN_USERS = "select sys.get_logedin_users()res from dual;";

    public static String GET_MY_USERS = "select sys.get_my_users(%d)res from dual;";

    public static String GET_MAIN_PARAMS = "select sys.get_main_params()res from dual;";

    public static String CHECK_PRIVATE_KEY = "select sys.check_private_key(%d,'%s')res from dual;";

    public static String FORGOT_PASSWORD = "select sys.forgot_password('%s','%s')res from dual;";

    public static String GET_USER_MANAGER = "select sys.get_user_manager(%d)res from dual;";

    public static String GET_SENTENCES = "select sys.get_sentences()res from dual;";

    public static String GET_USER_ATTENDANCE = "call sys.get_user_attendance(?,?,?,?)";
}
