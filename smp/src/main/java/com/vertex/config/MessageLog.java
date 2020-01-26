package com.vertex.config;

import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.ext.web.RoutingContext;
import java.util.HashMap;

public class MessageLog {
    public static String HOST_NAME = "Unknown";

    public static void sendMessageCode(RoutingContext routingContext, MessageConfig.MessageKey messageKey, String msg, Logger logger) {
        JsonObject response = new JsonObject();
        response.put("response_code", Integer.valueOf(messageKey.val()));
        response.put("message", msg);
        logger.info(response.encodePrettily());
        routingContext.response().end(Json.encodePrettily(response));
    }

    public static void sendMessageCode(RoutingContext routingContext, MessageConfig.MessageKey messageKey, HashMap<String, Object> params, Logger logger) {
        JsonObject response = new JsonObject();
        params.forEach((name, param) -> response.put(name, param));
        response.put("response_code", Integer.valueOf(messageKey.val()));
        logger.info(response.encodePrettily());
        routingContext.response().end(Json.encodePrettily(response));
    }

    public static void sendMessageObject(RoutingContext routingContext, MessageConfig.MessageKey messageKey, JsonObject msg, Logger logger) {
        msg.put("response_code", Integer.valueOf(messageKey.val()));
        logger.info(msg.encodePrettily());
        routingContext.response().end(Json.encodePrettily(msg));
    }

    private static String sendMessageCode(RoutingContext routingContext, MessageConfig.MessageKey messageKey, String description) {
        JsonObject messageObject = new JsonObject();
        messageObject.put("response_code", Integer.valueOf(messageKey.val()));
        messageObject.put("description", description);
        String srtMessage = Json.encodePrettily(messageObject);
        if (routingContext != null)
            routingContext.response().putHeader("content-type", "application/json; charset=utf-8").end(srtMessage);
        messageObject.put("description", description);
        return Json.encodePrettily(messageObject);
    }

    public static void sendErrorCode(RoutingContext routingContext, MessageConfig.MessageKey messageKey, String errorMessage, Logger logger) {
       if(routingContext!=null && !routingContext.response().ended())
        logger.error(sendMessageCode(routingContext, messageKey, errorMessage));
    }

    public static void logMessage(String msg, Logger logger) {
        logger.info(Json.encodePrettily(msg));
    }
}
