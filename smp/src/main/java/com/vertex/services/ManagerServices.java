package com.vertex.services;

import com.vertex.config.Constants;
import com.vertex.config.MessageConfig;
import com.vertex.config.MessageLog;
import com.vertex.config.ResponseUtil;
import com.vertex.config.SqlQueries;
import com.vertex.config.UpConfig;
import com.vertex.db.DbLayer;
import com.vertex.utils.LangUtils;
import com.vertex.utils.ObjectMapperUtils;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.WorkerExecutor;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.ext.web.RoutingContext;
import java.sql.JDBCType;
import java.text.ParseException;
import java.util.function.Function;

public class ManagerServices {
    private static Logger logger = LoggerFactory.getLogger(ManagerServices.class);

    public static DbLayer dbLayer;

    Vertx vertx;

    private static EventBus eventBus;

    public ManagerServices(DbLayer dbLayer, Vertx vertx, EventBus eventBus) {
        ManagerServices.dbLayer = dbLayer;
        this.vertx = vertx;
        String string = "{\"data\": [{\"name\": \"boxes\", \"status\": 0, \"manager_id\": 12, \"description\": \"bring boxes to the counter\", \"time_date_end\": \"2018-11-18 13:30:00.000000\", \"time_date_start\": \"2018-11-18 12:30:00.000000\"},{\"name\": \"boxes\", \"status\": 0, \"manager_id\": 12, \"description\": \"bring boxes to the counter\", \"time_date_end\": \"2018-11-18 13:30:00.000000\", \"time_date_start\": \"2018-11-18 12:30:00.000000\"}]}";
        JsonObject json = new JsonObject(string);
        json.toString();
        ManagerServices.eventBus = eventBus;
    }

    public void newTask(final RoutingContext routingContext) {
        WorkerExecutor executor = this.vertx.createSharedWorkerExecutor("newTask" + routingContext.toString());
        executor.executeBlocking(future -> {
            final JsonObject jsonObject = routingContext.getBodyAsJson();
            try {
                final int managerId = Integer.parseInt(routingContext.request().getHeader("user_id"));
                String dateStart = jsonObject.getString("date_start");
                String dateEnd = jsonObject.getString("date_end");
                String name = jsonObject.getString("name");
                String description = jsonObject.getString("description");
                final int userId = jsonObject.containsKey("user_id") ? jsonObject.getInteger("user_id").intValue() : 0;
                int groupId = jsonObject.containsKey("group_id") ? jsonObject.getInteger("group_id").intValue() : 0;
                dateStart = LangUtils.convertToMysqlDateTime(dateStart);
                dateEnd = LangUtils.convertToMysqlDateTime(dateEnd);
                if (managerId == 0 || dateStart == null || dateEnd == null)
                    MessageLog.sendErrorCode(routingContext, MessageConfig.MessageKey.NEW_TASK_ERROR, "one or more parameters were null" + managerId, logger);
                Function<JsonObject, Void> func = new Function<JsonObject, Void>() {
                    public Void apply(JsonObject result) {
                        if (result.getInteger("response_code").intValue() < MessageConfig.ERROR_CODE_FROM) {
                            if (result.getJsonArray("data").getInteger(8).intValue() == 0) {
                                int taskId = result.getJsonArray("data").getInteger(7).intValue();
                                jsonObject.put("task_id", Integer.valueOf(taskId));
                                jsonObject.put("manager_id", Integer.valueOf(managerId));
                                MessageLog.sendMessageObject(routingContext, MessageConfig.MessageKey.NEW_TASK, jsonObject, ManagerServices
                                        .logger);
                                if (userId != 0)
                                    ManagerServices.eventBus.publish("task", jsonObject);
                            } else if (result.getJsonArray("data").getInteger(8).intValue() == 5) {
                                MessageLog.sendErrorCode(routingContext, MessageConfig.MessageKey.USER_HAS_NO_PERMISSIONS_FOR_THIS_TASK, "This user is a lower role than the user he's trying to set task to" + managerId, ManagerServices

                                        .logger);
                            } else {
                                MessageLog.sendErrorCode(routingContext, MessageConfig.MessageKey.NEW_TASK_ERROR, "Failed to create new task. Manager id " + managerId, ManagerServices
                                        .logger);
                            }
                        } else {
                            MessageLog.sendErrorCode(routingContext, MessageConfig.MessageKey.NEW_TASK_ERROR, "Failed to create new task. Manager id " + managerId, ManagerServices
                                    .logger);
                        }
                        return null;
                    }
                };
                saveTaskInDb(func, userId, managerId, dateStart, dateEnd, name, description, groupId);
            } catch (Exception e) {
                MessageLog.sendErrorCode(routingContext, MessageConfig.MessageKey.NEW_TASK_ERROR, "Failed to create new task:" + e.getMessage(), logger);
            } finally {
                executor.close();
            }
        },x -> {

        });
    }

    public static void saveTaskInDb(Function func, int userId, int managerId, String dateStart, String dateEnd, String name, String description, int groupId) throws Exception {
        JsonArray in = new JsonArray();
        in.add(Integer.valueOf(userId));
        in.add(Integer.valueOf(managerId)).add(dateStart);
        if (dateEnd != null) {
            in.add(dateEnd);
        } else {
            in.addNull();
        }
        in.add(name).add(description);
        if (groupId != 0) {
            in.add(Integer.valueOf(groupId));
        } else {
            in.addNull();
        }
        in.addNull()
                .addNull();
        JsonArray out = (new JsonArray()).addNull().addNull().addNull().addNull().addNull().addNull().addNull().add(JDBCType.INTEGER).add(JDBCType.INTEGER);
        dbLayer.callProcedure(func, SqlQueries.NEW_TASK, in, out, "task");
    }

    public void getOpenTasks(final RoutingContext routingContext) {
        WorkerExecutor executor = this.vertx.createSharedWorkerExecutor("getOpenTasks" + routingContext.toString());
        executor.executeBlocking(future -> {
            try {
                final int managerId = Integer.parseInt(routingContext.request().getHeader("user_id"));
                Function<JsonObject, Void> func = new Function<JsonObject, Void>() {
                    public Void apply(JsonObject result) {
                        if (result.getInteger("response_code").intValue() < MessageConfig.ERROR_CODE_FROM) {
                            MessageLog.sendMessageObject(routingContext, MessageConfig.MessageKey.GET_OPEN_TASKS,
                                    ResponseUtil.assembleMessage(result), ManagerServices.logger);
                        } else {
                            MessageLog.sendErrorCode(routingContext, MessageConfig.MessageKey.GET_OPEN_TASKS_ERROR, "Failed to get open tasks for manager:" + managerId, ManagerServices
                                    .logger);
                        }
                        return null;
                    }
                };
                dbLayer.selectFunction(func, String.format(SqlQueries.GET_OPEN_MANAGER_TASKS, new Object[] { Integer.valueOf(managerId) }));
            } catch (Exception e) {
                MessageLog.sendErrorCode(routingContext, MessageConfig.MessageKey.GET_OPEN_TASKS_ERROR, "Failed to get open tasks due to the following Exception : " + e.getMessage(), logger);
            } finally {
                executor.close();
            }
        },x -> {

        });
    }

    public void createNewUser(final RoutingContext routingContext) {
        try {
            JsonObject jsonObject = routingContext.getBodyAsJson();
            int managerId = Integer.parseInt(routingContext.request()
                    .getHeader("user_id"));
            String userName = jsonObject.getString("user_name");
            String firstName = jsonObject.getString("first_name");
            String secondName = jsonObject.getString("second_name");
            String password = jsonObject.getString("password");
            String city = jsonObject.getString("city");
            String street = jsonObject.getString("street");
            int houseNumber = jsonObject.getInteger("house_number").intValue();
            int doorNumber = jsonObject.getInteger("door_number").intValue();
            String telephone = jsonObject.getString("telephone");
            String email = jsonObject.getString("email");
            int role = jsonObject.getInteger("role").intValue();
            int action = jsonObject.getInteger("action").intValue();
            int userId = jsonObject.getInteger("user_id").intValue();
            int gpsOn = jsonObject.getInteger("gps").intValue();
            int sex = jsonObject.getInteger("sex").intValue();
            int department = jsonObject.getInteger("department").intValue();
            Function<JsonObject, Void> func = new Function<JsonObject, Void>() {
                public Void apply(JsonObject result) {
                    if (result.getInteger("response_code").intValue() < MessageConfig.ERROR_CODE_FROM && result
                            .getJsonArray("data").getInteger(17).intValue() == 0) {
                        int userId = result.getJsonArray("data").getInteger(18).intValue();
                        MessageLog.sendMessageObject(routingContext, MessageConfig.MessageKey.NEW_TASK, (new JsonObject())
                                .put("user_id", Integer.valueOf(userId)), ManagerServices.logger);
                    } else if (result.getInteger("response_code").intValue() < MessageConfig.ERROR_CODE_FROM && result
                            .getJsonArray("data").getInteger(17).intValue() == 9) {
                        MessageLog.sendErrorCode(routingContext, MessageConfig.MessageKey.USER_HAS_NO_PERMISSIONS_FOR_THIS_TASK, "Failed to create new user. Manager doesnt have permission for this action ", ManagerServices
                                .logger);
                    } else if (result.getInteger("response_code").intValue() < MessageConfig.ERROR_CODE_FROM && result
                            .getJsonArray("data").getInteger(17).intValue() == 8) {
                        MessageLog.sendErrorCode(routingContext, MessageConfig.MessageKey.ERROR, "Failed to create new user. This user already exists", ManagerServices
                                .logger);
                    } else if (result.getInteger("response_code").intValue() < MessageConfig.ERROR_CODE_FROM && result
                            .getJsonArray("data").getInteger(17).intValue() == 7) {
                        MessageLog.sendErrorCode(routingContext, MessageConfig.MessageKey.USER_DOESNT_EXIST_ERROR, "Failed to edit new user. This user doesn't exist", ManagerServices
                                .logger);
                    } else if (result.getInteger("response_code").intValue() < MessageConfig.ERROR_CODE_FROM && result
                            .getJsonArray("data").getInteger(17).intValue() == 6) {
                        MessageLog.sendErrorCode(routingContext, MessageConfig.MessageKey.CANNOT_REMOVE_ACTING_MANAGER, "Can not remove acting manager.Please select a new manager for this department and then proceed with removing/disabling this user", ManagerServices

                                .logger);
                    } else {
                        MessageLog.sendErrorCode(routingContext, MessageConfig.MessageKey.ERROR, "Failed to create/edit  user  due to error in the db", ManagerServices
                                .logger);
                    }
                    return null;
                }
            };
            JsonArray in = (new JsonArray()).add(Integer.valueOf(managerId)).add(userName).add(firstName).add(secondName).add(password).add(city).add(street).add(Integer.valueOf(houseNumber)).add(Integer.valueOf(doorNumber)).add(telephone).add(email).add(Integer.valueOf(role)).add(Integer.valueOf(action)).add(Integer.valueOf(userId)).add(Integer.valueOf(gpsOn)).add(Integer.valueOf(sex)).add(Integer.valueOf(department)).addNull().addNull().addNull();
            JsonArray out = (new JsonArray()).addNull().addNull().addNull().addNull().addNull().addNull().addNull().addNull().addNull().addNull().addNull().addNull().addNull().addNull().addNull().addNull().addNull().add(JDBCType.INTEGER).add(JDBCType.INTEGER).add(JDBCType.VARCHAR);
            dbLayer.callProcedure(func, SqlQueries.NEW_USER, in, out, "user");
        } catch (Exception e) {
            MessageLog.sendErrorCode(routingContext, MessageConfig.MessageKey.ERROR, "Failed to create/edit  user  due to Exception " + e
                    .getMessage(), logger);
        }
    }

    public void closeTask(final RoutingContext routingContext) {
        try {
            JsonObject jsonObject = routingContext.getBodyAsJson();
            final int userId = Integer.parseInt(routingContext.request()
                    .getHeader("user_id"));
            int taskId = jsonObject.getInteger("task_id").intValue();
            String description = jsonObject.getString("close_description");
            Function<JsonObject, Void> func = new Function<JsonObject, Void>() {
                public Void apply(JsonObject result) {
                    if (result.getInteger("response_code").intValue() < MessageConfig.ERROR_CODE_FROM && result
                            .getJsonArray("data").getInteger(3).intValue() == 0) {
                        MessageLog.sendMessageCode(routingContext, MessageConfig.MessageKey.CLOSED_TASK, "Successfully closed task", ManagerServices
                                .logger);
                    } else {
                        MessageLog.sendErrorCode(routingContext, MessageConfig.MessageKey.CLOSE_TASK_ERROR, "Failed to close task:" + userId, ManagerServices

                                .logger);
                    }
                    return null;
                }
            };
            JsonArray in = (new JsonArray()).add(Integer.valueOf(userId)).add(Integer.valueOf(taskId)).add(description).addNull();
            JsonArray out = (new JsonArray()).addNull().addNull().addNull().add(JDBCType.INTEGER);
            dbLayer.callProcedure(func, SqlQueries.CLOSE_TASK, in, out, "CLOSE_TASK");
        } catch (Exception e) {
            MessageLog.sendErrorCode(routingContext, MessageConfig.MessageKey.CLOSE_TASK_ERROR, "Failed to close task:", logger);
        }
    }

    public void getMyUsers(final RoutingContext routingContext) {
        final int userId = Integer.parseInt(routingContext.request()
                .getHeader("user_id"));
        Function<JsonObject, Void> func = new Function<JsonObject, Void>() {
            public Void apply(JsonObject result) {
                if (result.getInteger("response_code").intValue() < MessageConfig.ERROR_CODE_FROM) {
                    JsonArray users = result.getJsonObject("data").getJsonArray("q200");
                    if (users != null) {
                        int role = result.getJsonObject("data").getInteger("role").intValue();
                        result = ResponseUtil.assembleUsersByDepartment(users, role);
                    } else {
                        result = (new JsonObject()).put("users", users);
                    }
                    MessageLog.sendMessageObject(routingContext, MessageConfig.MessageKey.GET_USERS, result, ManagerServices
                            .logger);
                } else {
                    MessageLog.sendErrorCode(routingContext, MessageConfig.MessageKey.GET_USERS_ERROR, "Failed to get users for user " + userId, ManagerServices
                            .logger);
                }
                return null;
            }
        };
        dbLayer.selectFunction(func, String.format(SqlQueries.GET_MY_USERS, new Object[] { Integer.valueOf(userId) }));
    }

    public void ping(RoutingContext routingContext) {}

    public void addShopLocation(final RoutingContext routingContext) {
        WorkerExecutor executor = this.vertx.createSharedWorkerExecutor("addShopLocation" + routingContext.toString());
        executor.executeBlocking(future -> {
            try {
                JsonObject locationJson = routingContext.getBodyAsJson();
                double latitude = locationJson.getDouble(Constants.LATITUDE).doubleValue();
                double longtitude = locationJson.getDouble(Constants.LONGTITUDE).doubleValue();
                double radius = locationJson.getDouble(Constants.SIZE).doubleValue();
                int gps = locationJson.getInteger(Constants.GPS).intValue();
                int numberOfAttempts = locationJson.getInteger(Constants.ATTEMPTS_NUMBER).intValue();
                UpConfig.GPS = Integer.valueOf(gps);
                UpConfig.MAIN_LATITUDE = latitude;
                UpConfig.MAIN_LONGTITUDE = longtitude;
                UpConfig.MAIN_NUMBER_ATTEMPTS = numberOfAttempts;
                Function<JsonObject, Void> afterAddingLocation = new Function<JsonObject, Void>() {
                    public Void apply(JsonObject result) {
                        if (result.getInteger("response_code").intValue() < MessageConfig.ERROR_CODE_FROM) {
                            MessageLog.sendMessageCode(routingContext, MessageConfig.MessageKey.ALTERING_SHOP_LOCATION, "Successfully changed shop location", ManagerServices
                                    .logger);
                        } else {
                            MessageLog.sendErrorCode(routingContext, MessageConfig.MessageKey.ALTERING_SHOP_LOCATION, "Failed to change shop location due to error in the data base", ManagerServices

                                    .logger);
                        }
                        return null;
                    }
                };
                JsonArray in = new JsonArray();
                JsonArray out = new JsonArray();
                in.add(Double.valueOf(latitude)).add(Double.valueOf(longtitude)).add(Double.valueOf(radius)).add(Integer.valueOf(gps)).add(Integer.valueOf(numberOfAttempts));
                out.addNull().addNull().addNull().addNull().addNull();
                dbLayer.callProcedure(afterAddingLocation, "call sys.add_shop_location(?,?,?,?,?)", in, out, "");
            } catch (Exception e) {
                MessageLog.sendErrorCode(routingContext, MessageConfig.MessageKey.ALTERING_SHOP_LOCATION_ERROR, "Failed to change shop location due to error: " + e.getMessage(), logger);
            } finally {
                executor.close();
            }
        },x -> {

        });
    }

    public static double isGpsLocationInRange(RoutingContext routingContext) {
        JsonObject locationJson = routingContext.getBodyAsJson();
        double latitude = locationJson.getDouble(Constants.LATITUDE).doubleValue();
        double longtitude = locationJson.getDouble(Constants.LONGTITUDE).doubleValue();
        double distance = distance(latitude, longtitude, UpConfig.MAIN_LATITUDE, UpConfig.MAIN_LONGTITUDE, 'K') * 1000.0D;
        if (distance <= UpConfig.MAIN_RADIUS)
            return 0.0D;
        return distance;
    }

    private static double distance(double lat1, double lon1, double lat2, double lon2, char unit) {
        double theta = lon1 - lon2;
        double dist = Math.sin(deg2rad(lat1)) * Math.sin(deg2rad(lat2)) + Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2)) * Math.cos(deg2rad(theta));
        dist = Math.acos(dist);
        dist = rad2deg(dist);
        dist = dist * 60.0D * 1.1515D;
        if (unit == 'K') {
            dist *= 1.609344D;
        } else if (unit == 'N') {
            dist *= 0.8684D;
        }
        return dist;
    }

    private static double deg2rad(double deg) {
        return deg * Math.PI / 180.0D;
    }

    private static double rad2deg(double rad) {
        return rad * 180.0D / Math.PI;
    }

    public static void getManagerInfoAndSendMail(final int userId) {
        Function<JsonObject, Void> func = new Function<JsonObject, Void>() {
            public Void apply(JsonObject result) {
                if (result.getInteger("response_code") < MessageConfig.ERROR_CODE_FROM) {
                    JsonObject manager = result.getJsonObject("data");
                    if (manager != null)
                        try {
                            ManagerServices.openTaskForPunchClockApprove(userId, manager);
                            ChatServices.sendMailForUserPunchClock(manager
                                    .getString("userFirstName") + " " + manager
                                    .getString("userSecondName"), manager
                                    .getString("email"), manager.getString("managerName"));
                        } catch (Exception e) {
                            MessageLog.logMessage("Failed to let manager know about this failed Punch Clock attempt", ManagerServices
                                    .logger);
                        }
                }
                return null;
            }
        };
        dbLayer.selectFunction(func, String.format(SqlQueries.GET_USER_MANAGER, new Object[] { Integer.valueOf(userId) }));
    }

    private static void openTaskForPunchClockApprove(int userId, JsonObject manager) throws Exception {
        JsonObject jsonObject = new JsonObject();
        String timeStart = LangUtils.getCurrentMysqlTime();
        Function func = result -> {
            if (((JsonObject)result).getInteger("response_code").intValue() < MessageConfig.ERROR_CODE_FROM) {
                if (((JsonObject)result).getJsonArray("data").getInteger(8).intValue() == 0) {
                    int taskId = ((JsonObject)result).getJsonArray("data").getInteger(7).intValue();
                    jsonObject.put("id", Integer.valueOf(taskId));
                    jsonObject.put("user_id", manager.getInteger("userID"));
                    jsonObject.put("name", "Punch Clock user:" + userId);
                    jsonObject.put("time_date_start", timeStart);
                    jsonObject.put("description", "This user tried to Punch Clock from the wrong location");
                    MessageLog.logMessage(jsonObject.toString(), logger);
                    if (manager.getInteger("userID").intValue() != 0)
                        eventBus.publish("task", jsonObject);
                } else {
                    MessageLog.logMessage("Failed to create new task due to not allowed action in the db. Manager id " + manager.getInteger("userID"), logger);
                }
            } else {
                MessageLog.logMessage("Failed to create new task. Manager id " + manager.getInteger("userID"), logger);
            }
            return null;
        };
        saveTaskInDb(func, userId, manager.getInteger("userID").intValue(), timeStart, null, "Punch Clock user:" + userId, "This user tried to Punch Clock from the wrong location", 0);
    }

    public void createNewSentence(final RoutingContext routingContext) {
        WorkerExecutor executor = this.vertx.createSharedWorkerExecutor("createNewSentence" + routingContext.toString());
        executor.executeBlocking(future -> {
            try {
                JsonObject body = routingContext.getBodyAsJson();
                int userId = Integer.parseInt(routingContext.request().getHeader("user_id"));
                final String name = body.getString("name");
                final String description = body.getString("description");
                final int alertId = body.getInteger("id").intValue();
                int remove = body.getInteger("remove").intValue();
                Function<JsonObject, Void> afterCreatingAlert = new Function<JsonObject, Void>() {
                    public Void apply(JsonObject result) {
                        if (result.getInteger("response_code").intValue() < MessageConfig.ERROR_CODE_FROM) {
                            if (result.getJsonArray("data").getInteger(5).intValue() == 0) {
                                MessageLog.sendMessageCode(routingContext, MessageConfig.MessageKey.SENTENCES_SUCESS, "Successfully created alert", ManagerServices
                                        .logger);
                                ManagerServices.this.updateAlertInMemory(result.getJsonArray("data").getInteger(6), name, description);
                            } else if (result.getJsonArray("data").getInteger(5).intValue() == 1) {
                                MessageLog.sendMessageCode(routingContext, MessageConfig.MessageKey.SENTENCES_SUCESS, "Successfully UPDATED alert", ManagerServices
                                        .logger);
                                ManagerServices.this.updateAlertInMemory(Integer.valueOf(alertId), name, description);
                            } else if (result.getJsonArray("data").getInteger(5).intValue() == 2) {
                                MessageLog.sendMessageCode(routingContext, MessageConfig.MessageKey.SENTENCES_SUCESS, "Successfully REMOVED alert", ManagerServices
                                        .logger);
                                ManagerServices.this.removeAlertInMemory(Integer.valueOf(alertId));
                            } else {
                                MessageLog.sendErrorCode(routingContext, MessageConfig.MessageKey.INSUFFICIENT_ROLE, "You dont have permission to create alerts", ManagerServices

                                        .logger);
                            }
                        } else {
                            MessageLog.sendErrorCode(routingContext, MessageConfig.MessageKey.SENTENCES_ERROR, "Failed to create alert in the db due to error in the db", ManagerServices

                                    .logger);
                        }
                        return null;
                    }
                };
                JsonArray in = new JsonArray();
                JsonArray out = new JsonArray();
                in.add(Integer.valueOf(userId)).add(name).add(description);
                if (alertId != 0) {
                    in.add(Integer.valueOf(alertId));
                } else {
                    in.addNull();
                }
                in.add(Integer.valueOf(remove)).addNull().addNull();
                out.addNull().addNull().addNull().addNull().addNull().add(JDBCType.INTEGER).add(JDBCType.INTEGER);
                dbLayer.callProcedure(afterCreatingAlert, SqlQueries.ADMIN_CREATE_ALERT, in, out, "");
            } catch (Exception e) {
                MessageLog.sendErrorCode(routingContext, MessageConfig.MessageKey.SENTENCES_ERROR, "Failed to create alert in the db due to following exception: " + e.getMessage(), logger);
            } finally {
                executor.close();
            }
        },x -> {

        });
    }

    public void createDepartment(final RoutingContext routingContext) {
        try {
            JsonObject body = routingContext.getBodyAsJson();
            int adminId = Integer.parseInt(routingContext.request()
                    .getHeader("user_id"));
            String departmentName = body.getString("name");
            int managerId = body.getInteger("manager_id").intValue();
            int departmentId = body.getInteger("department_id").intValue();
            int remove = body.getInteger("remove").intValue();
            Function<JsonObject, Void> afterCreatingAlert = new Function<JsonObject, Void>() {
                public Void apply(JsonObject result) {
                    if (result.getInteger("response_code").intValue() < MessageConfig.ERROR_CODE_FROM) {
                        if (result.getJsonArray("data").getInteger(5).intValue() == 0) {
                            MessageLog.sendMessageCode(routingContext, MessageConfig.MessageKey.DEPARTMENT_CREATE, "Successfully created department", ManagerServices
                                    .logger);
                        } else {
                            MessageLog.sendErrorCode(routingContext, MessageConfig.MessageKey.DEPARTMENT_CREATE_ERROR, "You dont have permission to create department", ManagerServices

                                    .logger);
                        }
                    } else {
                        MessageLog.sendErrorCode(routingContext, MessageConfig.MessageKey.DEPARTMENT_CREATE_ERROR, "Failed to create department in the db due to error in the db", ManagerServices

                                .logger);
                    }
                    return null;
                }
            };
            JsonArray in = new JsonArray();
            JsonArray out = new JsonArray();
            in.add(Integer.valueOf(adminId)).add(Integer.valueOf(managerId)).add(departmentName);
            if (departmentId != 0) {
                in.add(Integer.valueOf(departmentId));
            } else {
                in.addNull();
            }
            if (remove != 0) {
                in.add(Integer.valueOf(remove));
            } else {
                in.addNull();
            }
            in.addNull();
            out.addNull().addNull().addNull().addNull().addNull().add(JDBCType.INTEGER);
            dbLayer.callProcedure(afterCreatingAlert, "call sys.create_department(?,?,?,?,?,?);", in, out, "");
        } catch (Exception e) {
            MessageLog.sendErrorCode(routingContext, MessageConfig.MessageKey.DEPARTMENT_CREATE_ERROR, "Failed to create department in the db due to following exception: " + e

                    .getMessage(), logger);
        }
    }

    private void updateAlertInMemory(Integer id, String name, String description) {
        JsonObject json = (new JsonObject()).put("name", name).put("description", description).put("id", id);
        ChatServices.addSentence(id.intValue(), json);
    }

    private void removeAlertInMemory(Integer id) {
        ChatServices.removeSentence(id.intValue());
    }

    public void sendReport(RoutingContext routingContext) {
        WorkerExecutor executor = this.vertx.createSharedWorkerExecutor("sendReport" + routingContext.toString());
        executor.executeBlocking(future -> {
            try {
                JsonObject body = routingContext.getBodyAsJson();
                int managerId = Integer.parseInt(routingContext.request().getHeader("user_id"));
                int reportId = body.getInteger("report_id").intValue();
                String fromDateTime = body.getString("from_date_time");
                String toDateTime = body.getString("to_date_time");
                int group = body.getInteger("group").intValue();
                int id = body.getInteger("id").intValue();
                String mail = body.getString("mail");
                switch (reportId) {
                    case 1:
                        prepareTaskCompletionReport(routingContext, managerId, fromDateTime, toDateTime, group, id, mail);
                        break;
                    case 2:
                        preparePunchClockReport(routingContext, fromDateTime, toDateTime, group, id, mail);
                        break;
                }
            } catch (Exception e) {
                MessageLog.sendErrorCode(routingContext, MessageConfig.MessageKey.SENTENCES_ERROR, "Failed to send report due to Exception: " + e.getMessage(), logger);
            } finally {
                executor.close();
            }
        },x -> {

        });
    }

    private void prepareTaskCompletionReport(final RoutingContext routingContext, int id, String fromDateTime, String toDateTime, int group, int managerId, final String mail) {
        try {
            String query = getTaskQuery(group);
            JsonArray in = getInParamsForReport(fromDateTime, toDateTime, group, id);
            JsonArray out = getOutParamsForReport(fromDateTime, toDateTime, group, id);
            Function<JsonObject, Void> afterGettingReportFromDb = new Function<JsonObject, Void>() {
                public Void apply(JsonObject result) {
                    if (result.getInteger("response_code").intValue() < MessageConfig.ERROR_CODE_FROM) {
                        JsonArray array = ObjectMapperUtils.sortTasksByUsers(result.getJsonArray("data"));
                        NotificationsServices.sendReportsMail(ManagerServices.logger, mail, array, ManagerServices.this.vertx, "report_tasks.html", routingContext);
                    } else {
                        MessageLog.sendErrorCode(routingContext, MessageConfig.MessageKey.REPORT_ERROR, "Failed to create REPORT in the db due to error in the db", ManagerServices

                                .logger);
                    }
                    return null;
                }
            };
            dbLayer.callSelectProcedure(afterGettingReportFromDb, query, in, out, "getting args for report");
        } catch (Exception e) {
            MessageLog.sendErrorCode(routingContext, MessageConfig.MessageKey.REPORT_ERROR, "Failed to create REPORT due to exception " + e

                    .getMessage(), logger);
        }
    }

    private JsonArray getInParamsForReport(String fromDateTime, String toDateTime, int group, int id) {
        JsonArray in = new JsonArray();
        switch (group) {
            case 0:
                in.add(Integer.valueOf(id)).add(fromDateTime).add(toDateTime);
                break;
            case 1:
                in.add(fromDateTime).add(toDateTime);
                break;
            case 2:
                in.add(Integer.valueOf(id)).add(fromDateTime).add(toDateTime);
                break;
        }
        return in;
    }

    private JsonArray getOutParamsForReport(String fromDateTime, String toDateTime, int group, int id) {
        JsonArray out = new JsonArray();
        switch (group) {
            case 0:
                out.addNull().addNull().addNull();
                break;
            case 1:
                out.addNull().addNull();
                break;
            case 2:
                out.addNull().addNull().addNull();
                break;
        }
        return out;
    }

    private static String getTaskQuery(int group) {
        String query = null;
        switch (group) {
            case 0:
                query = "call sys.get_report_tasks_for_user(?,?,?)";
                break;
            case 1:
                query = "call sys.get_report_tasks_all_users(?,?)";
                break;
            case 2:
                query = "call sys.get_report_tasks_for_department(?,?,?)";
                break;
        }
        return query;
    }

    private static String getPunchClockQuery(int group) {
        String query = null;
        switch (group) {
            case 0:
                query = "call sys.get_clock_report_for_user(?,?,?)";
                break;
            case 1:
                query = "call sys.get_clock_report_for_all(?,?)";
                break;
            case 2:
                query = "call sys.get_clock_report_for_department(?,?,?)";
                break;
        }
        return query;
    }

    private void preparePunchClockReport(RoutingContext routingContext, String fromDateTime, String toDateTime, int group, int id, String mail) throws Exception {
        try {
            String query = getPunchClockQuery(group);
            JsonArray in = getInParamsForReport(fromDateTime, toDateTime, group, id);
            JsonArray out = getOutParamsForReport(fromDateTime, toDateTime, group, id);
            Function afterGettingReportFromDb = result -> {
                if (((JsonObject)result).getInteger("response_code").intValue() < MessageConfig.ERROR_CODE_FROM) {
                    JsonArray array = null;
                    try {
                        array = ObjectMapperUtils.sortPucnchClockByUsers(((JsonObject)result).getJsonArray("data"));
                        NotificationsServices.sendReportsMail(logger, mail, array, this.vertx, "report_punch_clock.html", routingContext);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                } else {
                    MessageLog.sendErrorCode(routingContext, MessageConfig.MessageKey.REPORT_ERROR, "Failed to create REPORT in the db due to error in the db", logger);
                }
                return null;
            };
            dbLayer.callSelectProcedure(afterGettingReportFromDb, query, in, out, "getting args for report");
        } catch (Exception e) {
            MessageLog.sendErrorCode(routingContext, MessageConfig.MessageKey.REPORT_ERROR, "Failed to create REPORT due to exception " + e

                    .getMessage(), logger);
        }
    }

    public void punchClock(RoutingContext routingContext) {
        WorkerExecutor executor = this.vertx.createSharedWorkerExecutor("punchClock" + routingContext.toString());
        executor.executeBlocking(future -> {
            try {
                JsonObject jsonObject = routingContext.getBodyAsJson();
                int userId = jsonObject.getInteger("user_id").intValue();
                int inOut = jsonObject.getInteger("in_out").intValue();
                String time = jsonObject.getString("date_time");
                int id = jsonObject.getInteger("id").intValue();
                UserServices.punchClockAttempt(routingContext, userId, inOut, time, 0.0F, id);
            } catch (Exception e) {
                MessageLog.sendErrorCode(routingContext, MessageConfig.MessageKey.PUNCH_CLOCK_ERROR, "Failed to punch clock", logger);
            } finally {
                executor.close();
            }
        },x -> {

        });
    }

    public void getUserAttendanceForManager(RoutingContext routingContext) {
        WorkerExecutor executor = this.vertx.createSharedWorkerExecutor("getUserAttendanceForManager" + routingContext.toString());
        executor.executeBlocking(future -> {
            try {
                int userId = Integer.parseInt(routingContext.request().getParam("id"));
                UserServices.getUserAttendanceStatic(routingContext, userId, 0);
            } catch (Exception e) {
                MessageLog.sendErrorCode(routingContext, MessageConfig.MessageKey.GET_USER_ATTENDANCE_ERROR, "Failed to get user attendance for manager due to the following Exception " + e.getMessage(), logger);
            } finally {
                executor.close();
            }
        },x -> {

        });
    }
}
