package com.vertex.config;

import io.vertx.core.json.JsonObject;

public class MessageConfig {
    public static JsonObject USER_MESSAGES = new JsonObject();

    public static int ERROR_CODE_FROM = 500;

    public enum MessageKey {
        OK(1),
        USER_ADD(2),
        LOGIN(3),
        PUNCH_CLOCK(4),
        NEW_TASK(5),
        GET_OPEN_TASKS(6),
        AUTH(7),
        LOG_OUT(8),
        READ_FILE_SUCCESS(9),
        CLOSED_TASK(10),
        GET_USERS(11),
        ALTERING_SHOP_LOCATION(12),
        PUNCH_CLOCK_FAULT(13),
        PRIVATE_KEY_MATCH(14),
        SOCKET_MESSAGE_SEND(15),
        SENTENCES_SUCESS(16),
        ONLINE_USERS(17),
        DEPARTMENT_CREATE(18),
        REPORT(19),
        FORGOT_PASSWORD(20),
        GET_PUNCH_CLOCK_PARAMS(21),
        GET_USER_ATTENDANCE(22),
        ERROR_CODE(500),
        DB_SQL_ERROR(501),
        DB_CONNECTION_FAILED(502),
        USER_ADD_ERROR(503),
        LOGIN_ERROR(504),
        NEW_TASK_ERROR(506),
        GET_OPEN_TASKS_ERROR(507),
        AUTH_ERROR(508),
        LOG_OUT_ERROR(509),
        READ_FILE_ERROR(510),
        MONGO_UPDATE_ERROR(511),
        USER_HAS_NO_PERMISSIONS_FOR_THIS_TASK(512),
        CLOSE_TASK_ERROR(513),
        GET_USERS_ERROR(514),
        ERROR(515),
        ALTERING_SHOP_LOCATION_ERROR(516),
        MAIN_PARAMS_ERROR(517),
        PUNCH_CLOCK_ERROR(518),
        MESSAGE_ERROR(519),
        PRIVATE_KEY_FAIL(520),
        SOCKET_MESSAGE_SEND_ERROR(521),
        SENTENCES_ERROR(522),
        INSUFFICIENT_ROLE(523),
        DEPARTMENT_CREATE_ERROR(524),
        REPORT_ERROR(525),
        ONLINE_USERS_ERROR(526),
        FORGOT_PASSWORD_ERROR(527),
        GET_PUNCH_CLOCK_PARAMS_ERROR(528),
        GET_USER_ATTENDANCE_ERROR(529),
        USER_DOESNT_EXIST_ERROR(530),
        CANNOT_REMOVE_ACTING_MANAGER(531);

        int val;

        MessageKey(int i) {
            this.val = i;
        }

        public int val() {
            return this.val;
        }
    }
}
