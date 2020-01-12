package com.vertex.config;

import io.vertx.core.json.Json;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.ext.web.RoutingContext;
import java.util.HashSet;
import java.util.Set;

public class ResponseUtil {
    public static void sendMessageObject(RoutingContext routingContext, MessageConfig.MessageKey messageKey, String message, Logger logger) {
        logger.info(sendMessageCode(routingContext, messageKey, message));
    }

    public static void sendErrorObject(RoutingContext routingContext, MessageConfig.MessageKey messageKey, String errorMessage, Logger logger) {
        logger.error(sendMessageCode(routingContext, messageKey, errorMessage));
    }

    public static JsonObject getAsJson(MessageConfig.MessageKey messageKey, String errorMessage, Logger logger) {
        return getAsJson(messageKey, errorMessage, logger, null);
    }

    public static JsonObject getAsJson(MessageConfig.MessageKey messageKey, String errorMessage, Logger logger, Throwable e) {
        String str = sendMessageCode(null, messageKey, errorMessage);
        if (messageKey.val() < 100) {
            logger.info(str);
        } else if (e == null) {
            logger.error(str);
        } else {
            logger.error(str, e);
        }
        return new JsonObject(str);
    }

    public static void sendErrorObject(RoutingContext routingContext, MessageConfig.MessageKey messageKey, String errorMessage, Logger logger, Throwable e) {
        logger.error(sendMessageCode(routingContext, messageKey, errorMessage), e);
    }

    private static String sendMessageCode(RoutingContext routingContext, MessageConfig.MessageKey messageKey, String description) {
        JsonObject messageObject = new JsonObject();
        messageObject.put("response_code", Integer.valueOf(messageKey.val()));
        String srtMessage = Json.encodePrettily(messageObject);
        if (routingContext != null)
            routingContext.response().putHeader("content-type", "application/json; charset=utf-8").end(srtMessage);
        messageObject.put("description", description);
        return Json.encodePrettily(messageObject);
    }

    public static JsonObject assembleMessage(JsonObject object) {
        JsonArray jsonArray = object.getJsonObject("data").getJsonArray("q200");
        object.remove("description");
        object.remove("data");
        object.put("data", jsonArray);
        return object;
    }

    public static JsonObject assembleUsersByDepartment(JsonArray users, int role) {
        JsonObject responce = new JsonObject();
        Set<Integer> departments = new HashSet<>();
        JsonArray usersDepartments = new JsonArray();
        if (role == 6) {
            usersDepartments.add((new JsonObject())

                    .put(users.getJsonObject(0).getInteger("group_id").toString(), users));
        } else if (role >= 7) {
            users.forEach(user -> departments.add(((JsonObject)user).getInteger("group_id")));
            departments.forEach(department -> {
                JsonObject depart = new JsonObject();
                JsonArray array = new JsonArray();
//                users.forEach(());
                depart.put(department.toString(), array);
                usersDepartments.add(depart);
            });
        }
        responce.put("users", usersDepartments);
        return responce;
    }
}
