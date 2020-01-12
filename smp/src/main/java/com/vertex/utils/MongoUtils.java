package com.vertex.utils;

import com.vertex.config.MessageConfig;
import com.vertex.config.MessageLog;
import com.vertex.config.UpConfig;
import com.vertex.db.MongoLayer;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.ext.web.RoutingContext;
import java.util.function.Function;

public class MongoUtils {
    public static MongoLayer MONGO_DB;

    private static Logger logger = LoggerFactory.getLogger(MongoLayer.class);

    public static void init() {
        MongoLayer.getInstance(UpConfig.CHAT_DB, res -> MONGO_DB = res);
    }

    private void getUserMessages(int convoId, Function function) {
        JsonObject jsonObject = (new JsonObject()).put("_id", Integer.valueOf(convoId));
        MONGO_DB.find(UpConfig.CONVOS_COLLECTION, jsonObject, function);
    }

    private void insertNewMessage(String convoId, JsonArray messages, Function function, RoutingContext routingContext) {
        try {
            JsonObject jsonObject = (new JsonObject()).put("_id", convoId);
            JsonObject updateOptions = new JsonObject();
            updateOptions.put(UpConfig.SET, (new JsonObject()).put("messages", messages));
            MONGO_DB.updateDocuments(UpConfig.CONVOS_COLLECTION, jsonObject, updateOptions, function);
        } catch (Exception e) {
            MessageLog.sendErrorCode(routingContext, MessageConfig.MessageKey.MONGO_UPDATE_ERROR, "Failed to update messages in Mongo", logger);
        }
    }
}
