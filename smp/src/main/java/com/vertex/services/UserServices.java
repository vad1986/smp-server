package com.vertex.services;

import com.vertex.config.Constants;
import com.vertex.config.MessageConfig;
import com.vertex.config.MessageLog;
import com.vertex.config.ResponseUtil;
import com.vertex.config.SqlQueries;
import com.vertex.config.UpConfig;
import com.vertex.dataObjects.User;
import com.vertex.dataObjects.UsersCollection;
import com.vertex.db.DbLayer;
import com.vertex.utils.LangUtils;
import com.vertex.utils.ObjectMapperUtils;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.WorkerExecutor;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.ext.web.RoutingContext;
import java.sql.JDBCType;
import java.text.ParseException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class UserServices {
    private static Logger logger = LoggerFactory.getLogger(UserServices.class);

    private static Vertx vertx;

    private static DbLayer dbLayer;

    private static Map<Integer, Integer> userPunchClockAttempts;

    private EventBus eventBus;

    public UserServices(DbLayer dbLayer, Vertx vertx, EventBus eventBus) {
        userPunchClockAttempts = new HashMap<>();
        UserServices.dbLayer = dbLayer;
        UserServices.vertx = vertx;
        getMainParams(res -> getLoggedInUsers());
        this.eventBus = eventBus;
    }

    private static int increasePunchClockAttemptForUser(int userId) {
        int num = 1;
        if (userPunchClockAttempts.containsKey(Integer.valueOf(userId))) {
            userPunchClockAttempts.put(Integer.valueOf(userId), Integer.valueOf(((Integer)userPunchClockAttempts.get(Integer.valueOf(userId))).intValue() + 1));
            if (((Integer)userPunchClockAttempts.get(Integer.valueOf(userId))).intValue() == UpConfig.MAIN_NUMBER_ATTEMPTS - 1)
                return 1;
            if (((Integer)userPunchClockAttempts.get(Integer.valueOf(userId))).intValue() >= UpConfig.MAIN_NUMBER_ATTEMPTS)
                return -1;
            return 0;
        }
        userPunchClockAttempts.put(Integer.valueOf(userId), Integer.valueOf(num));
        return 0;
    }

    private void login(final RoutingContext routingContext, boolean isFreshLogin) throws Exception {
        String privateKey, userName = null, password = null;
        long userId = 0L;
        if (isFreshLogin) {
            privateKey = LangUtils.generatePrivateKey();
            JsonObject jsonObject = routingContext.getBodyAsJson();
            userName = jsonObject.getString("user_name");
            password = jsonObject.getString("password");
        } else {
            privateKey = routingContext.request().getParam("key");
            userId = Long.parseLong(routingContext.request().getParam("user_id"));
        }
        final String finalPrivateKey = privateKey;
        final String finalUserName = userName;
        Function<JsonObject, Void> func = new Function<JsonObject, Void>() {
            public Void apply(JsonObject result) {
                if (result.getInteger("response_code").intValue() < MessageConfig.ERROR_CODE_FROM && result
                        .getJsonArray("data").getInteger(4).intValue() == 0) {
                    try {
                        JsonObject response = new JsonObject();
                        JsonArray outParams = result.getJsonArray("data");
                        JsonObject userJson = null;
                        JsonObject globalRoles = null;
                        JsonObject globalDepartments = null;
                        JsonObject jsonManagers = null;
                        JsonObject jsonReports = null;
                        JsonObject jsonDepartments = null;
                        userJson = DbLayer.getOutSlashes2(outParams.getString(5));
                        globalRoles = DbLayer.getOutSlashes2(outParams.getString(6));
                        globalDepartments = DbLayer.getOutSlashes2(outParams.getString(8));
                        jsonReports = DbLayer.getOutSlashes2(outParams.getString(10));
                        jsonDepartments = DbLayer.getOutSlashes2(outParams.getString(7));
                        jsonManagers = DbLayer.getOutSlashes2(outParams.getString(9));
                        User user = ObjectMapperUtils.mapUser(userJson.toString());
                        user.setPrivateKey(finalPrivateKey);
                        UsersCollection.logInUser(user);
                        response.put("user_id", Integer.valueOf(user.getUserID()));
                        response.put("sex", Integer.valueOf(user.getSex()));
                        response.put("street", user.getStreet());
                        response.put("houseNumber", Integer.valueOf(user.getHouseNumber()));
                        response.put("doorNumber", Integer.valueOf(user.getDoorNumber()));
                        response.put("managerId", Integer.valueOf(user.getManagerId()));
                        response.put("mail", user.getEmail());
                        response.put("role", Integer.valueOf(user.getUserRole()));
                        response.put("gps", Integer.valueOf(user.getGps()));
                        response.put("departmentId", Integer.valueOf(user.getDepartmentId()));
                        response.put("user_name", user.getUserName());
                        response.put("private_key", user.getPrivateKey());
                        response.put("roles_data", globalRoles.remove("q200"));
                        response.put("departments_data", globalDepartments.remove("q200"));
                        response.put("departments", jsonDepartments.getJsonArray("departments"));
                        response.put("managers", jsonManagers.remove("q200"));
                        response.put("socket", UpConfig.DOMAIN + ":" + UpConfig.SOCKET_PORT);
                        response.put("reports", jsonReports.remove("q200"));
                        response.put("sentences", ChatServices.sentencesArray);
                        MessageLog.sendMessageObject(routingContext, MessageConfig.MessageKey.LOGIN, response, UserServices
                                .logger);
                    } catch (Exception e) {
                        MessageLog.sendErrorCode(routingContext, MessageConfig.MessageKey.LOGIN_ERROR, "Failed to login.Server error", UserServices
                                .logger);
                    }
                } else {
                    MessageLog.sendErrorCode(routingContext, MessageConfig.MessageKey.LOGIN_ERROR, "Failed to login user" + finalUserName, UserServices
                            .logger);
                }
                return null;
            }
        };
        JsonArray in = new JsonArray();
        in.add(Long.valueOf(userId));
        if (userName != null) {
            in.add(userName);
        } else {
            in.addNull();
        }
        if (password != null) {
            in.add(password);
        } else {
            in.addNull();
        }
        in.add(privateKey)
                .addNull().addNull().addNull().addNull().addNull().addNull().addNull();
        JsonArray out = new JsonArray();
        out.addNull().addNull().addNull().addNull().add(JDBCType.INTEGER)
                .add(JDBCType.OTHER).add(JDBCType.OTHER).add(JDBCType.VARCHAR).add(JDBCType.VARCHAR).add(JDBCType.VARCHAR)
                .add(JDBCType.VARCHAR);
        dbLayer.callProcedure(func, SqlQueries.LOGIN_USER, in, out, "docs");
    }

    public void login(RoutingContext routingContext) {
        WorkerExecutor executor = vertx.createSharedWorkerExecutor("login" + routingContext.toString());
        executor.executeBlocking(future -> {
            try {
                login(routingContext, true);
            } catch (Exception e) {
                MessageLog.sendErrorCode(routingContext, MessageConfig.MessageKey.LOGIN_ERROR, "Failed to login user", logger);
            } finally {
                executor.close();
            }
        },x -> {

        });
    }

    public void getMainParams(final RoutingContext routingContext) {
        WorkerExecutor executor = vertx.createSharedWorkerExecutor("getMainParams" + routingContext.toString());
        executor.executeBlocking(future -> {
            try {
                Function<JsonObject, Void> func = new Function<JsonObject, Void>() {
                    public Void apply(JsonObject result) {
                        if (result.getInteger("response_code").intValue() < MessageConfig.ERROR_CODE_FROM) {
                            JsonObject resultSet = result.getJsonObject("data").getJsonArray("q200").getJsonObject(0);
                            MessageLog.sendMessageObject(routingContext, MessageConfig.MessageKey.GET_PUNCH_CLOCK_PARAMS, resultSet, UserServices.logger);
                        } else {
                            MessageLog.sendErrorCode(routingContext, MessageConfig.MessageKey.GET_PUNCH_CLOCK_PARAMS_ERROR, "Failed getting Punch Clock Params due to error in the mysql", UserServices
                                    .logger);
                        }
                        return null;
                    }
                };
                dbLayer.selectFunction(func, SqlQueries.GET_MAIN_PARAMS);
            } catch (Exception e) {
                MessageLog.sendErrorCode(routingContext, MessageConfig.MessageKey.GET_PUNCH_CLOCK_PARAMS_ERROR, "Failed getting Punch Clock Params due to following Exception " + e.getMessage(), logger);
            } finally {
                executor.close();
            }
        },x -> {

        });
    }

    private void getMainParams(final Handler<Boolean> handler) {
        Function<JsonObject, Void> func = new Function<JsonObject, Void>() {
            public Void apply(JsonObject result) {
                if (result.getInteger("response_code").intValue() < MessageConfig.ERROR_CODE_FROM) {
                    JsonObject resultSet = result.getJsonObject("data").getJsonArray("q200").getJsonObject(0);
                    UpConfig.MAIN_LATITUDE = resultSet.getDouble(Constants.LATITUDE).doubleValue();
                    UpConfig.MAIN_LONGTITUDE = resultSet.getDouble(Constants.LONGTITUDE).doubleValue();
                    UpConfig.MAIN_RADIUS = resultSet.getDouble(Constants.RADIUS).doubleValue();
                    UpConfig.GPS = resultSet.getInteger(Constants.GPS);
                    UpConfig.MAIN_NUMBER_ATTEMPTS = resultSet.getInteger(Constants.ATTEMPTS_NUMBER).intValue();
                    handler.handle(Boolean.valueOf(true));
                } else {
                    MessageLog.logMessage("Failed getting main params from Data Base", UserServices
                            .logger);
                }
                return null;
            }
        };
        dbLayer.selectFunction(func, SqlQueries.GET_MAIN_PARAMS);
    }

    public void punchClock(RoutingContext routingContext) {
        WorkerExecutor executor = vertx.createSharedWorkerExecutor("punchClock" + routingContext.toString());
        executor.executeBlocking(future -> {
            try {
                JsonObject jsonObject = routingContext.getBodyAsJson();
                int userId = Integer.parseInt(routingContext.request().getHeader("user_id"));
                int inOut = jsonObject.getInteger("in_out");
                String time = jsonObject.getString("date_time");
                int gpsOn = jsonObject.getInteger("gps");
                if (UpConfig.GPS == 0 || gpsOn == 0) {
                    punchClockAttempt(routingContext, userId, inOut, time, -1.0F, -1);
                } else {
                    double loc = ManagerServices.isGpsLocationInRange(routingContext);
                    float distance = 0.0F;
                    if (loc != 0.0D) {
                        distance = (float)Math.round(loc - UpConfig.MAIN_RADIUS);
                        HashMap<String, Object> send = new HashMap<>();
                        send.put("meters", distance);
                        send.put("user_id", userId);
                        switch (increasePunchClockAttemptForUser(userId)) {
                            case 0:
                                send.put("attempts", userPunchClockAttempts.get(userId));
                                send.put("message", "Try again inside the range please");
                                MessageLog.sendMessageCode(routingContext, MessageConfig.MessageKey.PUNCH_CLOCK_FAULT, send, logger);
                                return;
                            case 1:
                                send.put("message", "You have one more attempt to punch clock!!");
                                send.put("attempts", userPunchClockAttempts.get(userId));
                                MessageLog.sendMessageCode(routingContext, MessageConfig.MessageKey.PUNCH_CLOCK_FAULT, send, logger);
                                break;
                            case -1:
                                send.put("message", "Contact your manager,he/she was alerted of this behaviour.");
                                MessageLog.sendMessageCode(routingContext, MessageConfig.MessageKey.PUNCH_CLOCK_FAULT, send, logger);
                                if ((Integer) userPunchClockAttempts.get(userId) == UpConfig.MAIN_NUMBER_ATTEMPTS)
                                    ManagerServices.getManagerInfoAndSendMail(userId);
                                userPunchClockAttempts.put(userId, UpConfig.MAIN_NUMBER_ATTEMPTS + 1);
                                return;
                        }
                    } else {
                        punchClockAttempt(routingContext, userId, inOut, time, distance, -1);
                    }
                }
            } catch (Exception e) {
                MessageLog.sendErrorCode(routingContext, MessageConfig.MessageKey.PUNCH_CLOCK_ERROR, "Failed to punch clock", logger);
            } finally {
                executor.close();
            }
        },x -> {

        });
    }

    public static void punchClockAttempt(RoutingContext routingContext, int userId, int inOut, String time, float distance, int id) throws Exception {
        JsonArray in = new JsonArray();
        JsonArray out = new JsonArray();
        Function<JsonObject, Void> afterDb = new Function<JsonObject, Void>() {
            public Void apply(JsonObject result) {
                if (result.getInteger("response_code") < MessageConfig.ERROR_CODE_FROM && result.getJsonArray("data").getInteger(7) == 0) {
                    int id=result.getJsonArray("data").getInteger(6);
                    JsonObject jsonObject=new JsonObject().put("id",id);
                    MessageLog.sendMessageObject(routingContext,MessageConfig.MessageKey.PUNCH_CLOCK,jsonObject,logger);
                } else {
                    MessageLog.sendErrorCode(routingContext, MessageConfig.MessageKey.PUNCH_CLOCK_ERROR, "Failed to punch clock for user" + userId + " In_out= " + inOut, logger);
                }
                return null;
            }

        };

        in.add(userId).add(time).add(inOut).add(Boolean.TRUE).add(distance).add(id).addNull().addNull().addNull();
        out.addNull().addNull().addNull().addNull().addNull().addNull().add(JDBCType.INTEGER).add(JDBCType.INTEGER).add(JDBCType.VARCHAR);
        dbLayer.callProcedure(afterDb, SqlQueries.PUNCH_CLOCK, in, out, "docs");
    }

    public void getUserMessages(final RoutingContext routingContext) {
        final int userId = Integer.parseInt(routingContext.request()
                .getHeader("user_id"));
        Function<JsonObject, Void> func = new Function<JsonObject, Void>() {
            public Void apply(JsonObject result) {
                if (result.getInteger("response_code").intValue() < MessageConfig.ERROR_CODE_FROM) {
                    MessageLog.sendMessageObject(routingContext, MessageConfig.MessageKey.GET_OPEN_TASKS,
                            ResponseUtil.assembleMessage(result), UserServices.logger);
                } else {
                    MessageLog.sendErrorCode(routingContext, MessageConfig.MessageKey.GET_OPEN_TASKS_ERROR, "Failed to get tasks for user:" + userId, UserServices
                            .logger);
                }
                return null;
            }
        };
    }

    public void sendNewMessage(RoutingContext routingContext) {
        int userId = Integer.parseInt(routingContext.request()
                .getHeader("user_id"));
        JsonObject message = routingContext.getBodyAsJson();
        int messageId = routingContext.getBodyAsJson().getInteger("message_id").intValue();
        int userIdTo = message.getInteger("user_id_to").intValue();
        JsonObject sentence = ChatServices.getSentence(messageId);
        message.put("message", sentence);
        message.put("user_id_to", Integer.valueOf(userIdTo));
        message.put("user_id_from", Integer.valueOf(userId));
        if (sentence != null) {
            this.eventBus.send("message", message, res -> {
                if (res.succeeded() && ((JsonObject)((Message)res.result()).body()).getInteger("response_code").intValue() < MessageConfig.MessageKey.ERROR_CODE.val()) {
                    MessageLog.sendMessageCode(routingContext, MessageConfig.MessageKey.SOCKET_MESSAGE_SEND, "Successfully sent message through soocket", logger);
                } else {
                    MessageLog.sendErrorCode(routingContext, MessageConfig.MessageKey.MESSAGE_ERROR, "Failed to send message to communication server. Response came back false", logger);
                }
            });
        } else {
            MessageLog.sendErrorCode(routingContext, MessageConfig.MessageKey.MESSAGE_ERROR, "Failed to send message to communication serverdue to no sentence found for user: " + userId, logger);
        }
    }

    public void getUserTasks(final RoutingContext routingContext) {
        final int userId = Integer.parseInt(routingContext.request()
                .getHeader("user_id"));
        int status = Integer.parseInt(routingContext.request().getParam("status"));
        Function<JsonObject, Void> func = new Function<JsonObject, Void>() {
            public Void apply(JsonObject result) {
                if (result.getInteger("response_code").intValue() < MessageConfig.ERROR_CODE_FROM) {
                    MessageLog.sendMessageObject(routingContext, MessageConfig.MessageKey.GET_OPEN_TASKS,
                            ResponseUtil.assembleMessage(result), UserServices.logger);
                } else {
                    MessageLog.sendErrorCode(routingContext, MessageConfig.MessageKey.GET_OPEN_TASKS_ERROR, "Failed to get tasks for user:" + userId, UserServices
                            .logger);
                }
                return null;
            }
        };
        dbLayer.selectFunction(func, String.format(SqlQueries.GET_USER_TASKS, new Object[] { Integer.valueOf(userId), Integer.valueOf(status) }));
    }

    public void returnError(RoutingContext routingContext) {
        MessageLog.sendMessageObject(routingContext, MessageConfig.MessageKey.GET_OPEN_TASKS_ERROR, (new JsonObject())

                .put("msg", "You got to the server OK"), logger);
    }

    public void checkAuthInfo(RoutingContext routingContext) {
        try {
            String userName = routingContext.request().getHeader("user_name");
            String key = routingContext.request().getHeader("private_key");
            if (userPermited(userName, key)) {
                routingContext.next();
            } else {
                MessageLog.sendErrorCode(routingContext, MessageConfig.MessageKey.AUTH_ERROR, "User private key is wrong or user is'nt logged in", logger);
            }
        } catch (NumberFormatException e) {
            MessageLog.sendErrorCode(routingContext, MessageConfig.MessageKey.AUTH_ERROR, "User is not allowed to access", logger);
        }
    }

    private boolean userPermited(String userName, String key) {
        if (userName != null && UsersCollection.isUserLoggedIn(userName) &&
                UsersCollection.userPrivateKeyMatch(userName, key) != null)
            return true;
        return false;
    }

    public boolean checkLoginStatus(String userName, RoutingContext routingContext) {
        if (userName == null || UsersCollection.isUserLoggedIn(userName)) {
            MessageLog.sendErrorCode(routingContext, MessageConfig.MessageKey.LOGIN_ERROR, "User already logged in", logger);
            return false;
        }
        return true;
    }

    public void checkPermissions(RoutingContext routingContext) {
        String userName = routingContext.request().getHeader("user_name");
        String key = routingContext.request().getHeader("private_key");
        String uri = routingContext.request().uri();
        if (userPermited(userName, key)) {
            if (UsersCollection.userPermittedForAction(userName, key, uri)) {
                routingContext.next();
            } else {
                MessageLog.sendErrorCode(routingContext, MessageConfig.MessageKey.AUTH_ERROR, "User not permitted for this action", logger);
            }
        } else {
            MessageLog.sendErrorCode(routingContext, MessageConfig.MessageKey.LOGIN_ERROR, "User not logged in.", logger);
        }
    }

    public void getLoggedInUsers() {
        Function<JsonObject, Void> afterDb = new Function<JsonObject, Void>() {
            public Void apply(JsonObject result) {
                if (result.getInteger("response_code").intValue() < MessageConfig.ERROR_CODE_FROM) {
                    JsonArray array = result.getJsonObject("data").getJsonArray("q200");
                    array.forEach(user -> {
                        try {
                            User user1 = ObjectMapperUtils.mapUser(user.toString());
                            UsersCollection.logInUser(user1);
                            UserServices.logger.info("User: " + user1.getUserName() + " Logged in");
                        } catch (Exception e) {
                            MessageLog.logMessage("Failed to ge user due to this Exception:" + e.getMessage(), UserServices.logger);
                        }
                    });
                } else {
                    MessageLog.logMessage("Failed to get logged in users from database", UserServices.logger);
                }
                return null;
            }
        };
        dbLayer.selectFunction(afterDb, SqlQueries.GET_LOGGED_IN_USERS);
    }

    private static void checkUserShifts() {}

    public void logout(final RoutingContext routingContext) {
        try {
            final String userName = routingContext.request().getHeader("user_name");
            String key = routingContext.request().getHeader("private_key");
            final int userId = Integer.parseInt(routingContext.request().getHeader("user_id"));
            Function<JsonObject, Void> afterDb = new Function<JsonObject, Void>() {
                public Void apply(JsonObject result) {
                    if (result.getInteger("response_code").intValue() < MessageConfig.ERROR_CODE_FROM && result
                            .getJsonArray("data").getInteger(3).intValue() == 0) {
                        MessageLog.sendMessageCode(routingContext, MessageConfig.MessageKey.LOG_OUT, "User " + userName + " logged out of the system", UserServices
                                .logger);
                        JsonObject json = new JsonObject();
                        json.put("command", "logout");
                        json.put("user_id", Integer.valueOf(userId));
                        json.put("user_name", userName);
                        if (userId != 0)
                            UserServices.this.eventBus.publish("logout", json);
                    } else {
                        MessageLog.sendErrorCode(routingContext, MessageConfig.MessageKey.LOG_OUT_ERROR, "Failed to log out user " + userName, UserServices
                                .logger);
                    }
                    return null;
                }
            };
            if (UsersCollection.logOutUser(userName, key)) {
                JsonArray in = (new JsonArray()).add(userName).add(Integer.valueOf(userId)).add(key).addNull();
                JsonArray out = (new JsonArray()).addNull().addNull().addNull().add(JDBCType.INTEGER);
                dbLayer.callProcedure(afterDb, "call sys.logout_user(?,?,?,?)", in, out, "");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void getUserChatConfig(RoutingContext routingContext) {}

    public void checkPrivateKey(final RoutingContext routingContext) {
        final int userId = Integer.parseInt(routingContext.request().getParam("user_id"));
        String key = routingContext.request().getParam("key");
        Function<JsonObject, Void> afterDb = new Function<JsonObject, Void>() {
            public Void apply(JsonObject result) {
                if (result.getInteger("response_code").intValue() < MessageConfig.ERROR_CODE_FROM) {
                    int status = result.getJsonObject("data").getInteger("stat").intValue();
                    if (status == 1) {
                        try {
                            UserServices.this.login(routingContext, false);
                        } catch (Exception e) {
                            MessageLog.sendErrorCode(routingContext, MessageConfig.MessageKey.PRIVATE_KEY_FAIL, "Failed to match key for user:" + userId, UserServices
                                    .logger);
                        }
                    } else {
                        MessageLog.sendErrorCode(routingContext, MessageConfig.MessageKey.PRIVATE_KEY_FAIL, "Failed to match key for user:" + userId, UserServices
                                .logger);
                    }
                } else {
                    MessageLog.sendErrorCode(routingContext, MessageConfig.MessageKey.PRIVATE_KEY_FAIL, "Failed to match key for user:" + userId, UserServices
                            .logger);
                }
                return null;
            }
        };
        dbLayer.selectFunction(afterDb, String.format(SqlQueries.CHECK_PRIVATE_KEY, new Object[] { Integer.valueOf(userId), key }));
    }

    public void forgotPassword(RoutingContext routingContext) {
        WorkerExecutor executor = vertx.createSharedWorkerExecutor("forgotPassword" + routingContext.toString());
        executor.executeBlocking(future -> {
            try {
                JsonObject body = routingContext.getBodyAsJson();
                String userName = body.getString("name");
                String mail = body.getString("mail");
                Function<JsonObject, Void> afterDb = new Function<JsonObject, Void>() {
                    public Void apply(JsonObject result) {
                        if (result.getInteger("response_code").intValue() < MessageConfig.ERROR_CODE_FROM) {
                            int status = result.getJsonObject("data").getInteger("stat").intValue();
                            if (status == 1) {
                                try {
                                    UserServices.this.login(routingContext, false);
                                } catch (Exception e) {
                                    MessageLog.sendErrorCode(routingContext, MessageConfig.MessageKey.PRIVATE_KEY_FAIL, "Failed to match key for user:" + userName, UserServices
                                            .logger);
                                }
                            } else {
                                MessageLog.sendErrorCode(routingContext, MessageConfig.MessageKey.PRIVATE_KEY_FAIL, "Failed to match key for user:" + userName, UserServices
                                        .logger);
                            }
                        } else {
                            MessageLog.sendErrorCode(routingContext, MessageConfig.MessageKey.PRIVATE_KEY_FAIL, "Failed to match key for user:" + userName, UserServices
                                    .logger);
                        }
                        return null;
                    }
                };

                dbLayer.selectFunction(afterDb, String.format(SqlQueries.FORGOT_PASSWORD, new Object[] { userName, mail }));
            } catch (Exception e) {
                MessageLog.sendErrorCode(routingContext, MessageConfig.MessageKey.FORGOT_PASSWORD_ERROR, "Failed to send password do to following Exception " + e.getMessage(), logger);
            } finally {
                executor.close();
            }
        },x -> {

        });
    }

    public void getUserAttendance(RoutingContext routingContext) {
        WorkerExecutor executor = vertx.createSharedWorkerExecutor("getUserAttendance" + routingContext.toString());
        executor.executeBlocking(future -> {
            try {
                int userId = Integer.parseInt(routingContext.request().getHeader("user_id"));
                int type = Integer.parseInt(routingContext.request().getParam("type"));
                getUserAttendanceStatic(routingContext, userId, type);
            } catch (Exception e) {
                MessageLog.sendErrorCode(routingContext, MessageConfig.MessageKey.GET_USER_ATTENDANCE_ERROR, "Failed to get user attendance due to the following Exception " + e.getMessage(), logger);
            } finally {
                executor.close();
            }
        },x -> {

        });
    }

    public static void getUserAttendanceStatic(RoutingContext routingContext, int userId, int type) throws Exception {
        LocalDate monthBegin = LocalDate.now().withDayOfMonth(1);
        LocalDate monthEnd = LocalDate.now().plusMonths(1L).withDayOfMonth(1).minusDays(1L);

        Function after = result -> {
            if (((JsonObject) result).getInteger("response_code") < MessageConfig.ERROR_CODE_FROM) {
                JsonObject object = null;
                try {
                    object = ObjectMapperUtils.sortUserAttendance(((JsonObject)result).getJsonArray("data"), userId, null);
                } catch (ParseException e) {
                    MessageLog.sendErrorCode(routingContext, MessageConfig.MessageKey.GET_USER_ATTENDANCE_ERROR, "Failed to get user attendance due to error in parsing dates", logger);
                }
                MessageLog.sendMessageObject(routingContext, MessageConfig.MessageKey.GET_USER_ATTENDANCE, object, logger);
            } else {
                MessageLog.sendErrorCode(routingContext, MessageConfig.MessageKey.GET_USER_ATTENDANCE_ERROR, "Failed to get user attendance", logger);
            }
            return null;
        };
        JsonArray in = (new JsonArray()).add(Integer.valueOf(userId)).add(monthBegin.toString()).add(monthEnd.toString()).add(Integer.valueOf(type));
        JsonArray out = (new JsonArray()).addNull().addNull().addNull().addNull();
        dbLayer.callSelectProcedure(after, SqlQueries.GET_USER_ATTENDANCE, in, out, "user attendance");
    }
}
