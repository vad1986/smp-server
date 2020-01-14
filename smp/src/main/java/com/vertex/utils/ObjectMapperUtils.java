package com.vertex.utils;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vertex.dataObjects.User;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import java.io.IOException;
import java.text.ParseException;
import java.util.Date;

public class ObjectMapperUtils {
    private static ObjectMapper mapper = new ObjectMapper();

    public static User mapUser(String str) throws Exception {
        try {
            mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            User user = (User)mapper.readValue(str, User.class);
            return user;
        } catch (IOException e) {
            throw new Exception("Failed to map user in the ObjectMapperUtils");
        }
    }

    public static JsonArray sortTasksByUsers(JsonArray tasks) {
        JsonArray result = new JsonArray();
        while (tasks.size() != 0) {
            JsonObject user = (new JsonObject()).put("complete", new JsonArray()).put("not_complete", new JsonArray());
            JsonObject json = (JsonObject) tasks.iterator().next();
            user.put("user_id", json.getInteger("user_id"));
            user.put("first_name", json.getString("first_name"));
            user.put("second_name", json.getString("second_name"));
            tasks = getUserTaskStatistic(tasks, user);
            result.add(user);
        }
        return result;
    }

    public static JsonArray sortPucnchClockByUsers(JsonArray clocks) throws ParseException {
        JsonArray result = new JsonArray();
        while (clocks.size() != 0) {
            JsonObject user = new JsonObject();
            JsonObject json = (JsonObject) clocks.iterator().next();
            user.put("user_id", json.getInteger("user_id"));
            user.put("first_name", json.getString("first_name"));
            user.put("second_name", json.getString("second_name"));
            clocks = getUserclockStatistic(clocks, user);
            result.add(user);
        }
        return result;
    }

    public static JsonArray getUserclockStatistic(JsonArray clocks, JsonObject user) throws ParseException {
        int userId = user.getInteger("user_id").intValue();
        user.put("data", new JsonArray());
        for (int i = 0; i < clocks.size(); ) {
            JsonObject clock = clocks.getJsonObject(i);
            if (clock.getInteger("user_id").intValue() == userId) {
                user.getJsonArray("data").add(clock);
                clocks.remove(i);
                continue;
            }
            i++;
        }
        sortUserAttendance(user.getJsonArray("data"), userId, user);
        return clocks;
    }

    public static JsonArray getUserTaskStatistic(JsonArray tasks, JsonObject user) {
        int userId = user.getInteger("user_id").intValue();
        for (int i = 0; i < tasks.size(); ) {
            JsonObject taskJson = tasks.getJsonObject(i);
            if (taskJson.getInteger("user_id").intValue() == userId) {
                if (taskJson.getInteger("status").intValue() == 0) {
                    user.getJsonArray("not_complete").add(taskJson);
                } else {
                    user.getJsonArray("complete").add(taskJson);
                }
                tasks.remove(i);
                continue;
            }
            i++;
        }
        return tasks;
    }

    public static JsonObject sortUserAttendance(JsonArray punchClocks, int userId, JsonObject user) throws ParseException {
        JsonArray array = new JsonArray();
        long sum = 0L, add = 0L;
        String finalTime = null;
        for (int i = 0; i < punchClocks.size(); i++) {
            JsonObject shift = new JsonObject();
            if (punchClocks.getJsonObject(i).getInteger("in_out") == 1) {
                String in = LangUtils.getDateString(punchClocks.getJsonObject(i).getString("date_time"));
                shift.put("start", in);
                shift.put("start_id", punchClocks.getJsonObject(i).getInteger("id"));
                if (i < punchClocks.size() - 1 && punchClocks.getJsonObject(i + 1).getInteger("in_out") == 2) {
                    String out = LangUtils.getDateString(punchClocks.getJsonObject(i + 1).getString("date_time"));
                    shift.put("finish", out);
                    shift.put("finish_id", punchClocks.getJsonObject(i + 1).getInteger("id"));
                    Date date = LangUtils.getDateFromMysqlDate(punchClocks.getJsonObject(i).getString("date_time"));
                    Date date2 = LangUtils.getDateFromMysqlDate(punchClocks.getJsonObject(i + 1).getString("date_time"));
                    if (date.getDate() < date2.getDate()) {
                        int days = date2.getDate() - date.getDate();
                        add = (days * 24 * 60 * 60 * 1000);
                    }
                    String hours = LangUtils.convertTime(date2.getTime() - date.getTime());
                    shift.put("hours", hours);
                    sum += date2.getTime() - date.getTime();
                    i++;
                }
            } else {
                String finish = LangUtils.getDateString(punchClocks.getJsonObject(i).getString("date_time"));
                shift.put("finish", finish);
                shift.put("finish_id", punchClocks.getJsonObject(i).getInteger("id"));
            }
            array.add(shift);
        }
        if (sum != 0L)
            finalTime = LangUtils.convertTime(sum);
        if (user != null)
            return user.put("data", array).put("sum", finalTime);
        return (new JsonObject()).put("user_id", userId).put("data", array).put("sum", finalTime);
    }
}
