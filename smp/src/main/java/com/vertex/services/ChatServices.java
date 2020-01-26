package com.vertex.services;

import com.vertex.config.MessageConfig;
import com.vertex.config.MessageLog;
import com.vertex.config.SqlQueries;
import com.vertex.config.UpConfig;
import com.vertex.db.DbLayer;
import com.vertex.db.MongoLayer;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.WorkerExecutor;
import io.vertx.core.eventbus.DeliveryOptions;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.ext.web.RoutingContext;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class ChatServices {
    public static DbLayer dbLayer;

    public static MongoLayer mongoLayer;

    public static Vertx vertx;

    public static Logger logger;

    public static JsonArray sentencesArray;

    public static Map<Integer, JsonObject> sentences;

    public static EventBus eventBus;

    public ChatServices(EventBus eventBus) {
        ChatServices.eventBus = eventBus;
    }

    public void init(DbLayer dbLayer, MongoLayer mongoLayer, Vertx vertx, Logger logger) {
        ChatServices.dbLayer = dbLayer;
        ChatServices.vertx = vertx;
        ChatServices.logger = logger;
        ChatServices.mongoLayer = mongoLayer;
        sentences = new HashMap<>();
        getSentencesFromDb();
    }

    private void fillSentences(JsonArray array) {
        sentencesArray = array;
        int x = 0;
        for (Object senteceObject : array) {
            JsonObject json = (JsonObject) senteceObject;
            json.put("array_id", Integer.valueOf(x++));
            sentences.put(json.getInteger("id"), json);
        }
    }

    public static void fillSentencesAfterUpdate(JsonObject sentence, int index) {
        if (index == -2) {
            int x = sentence.getInteger("array_id").intValue();
            sentencesArray.remove(x);
            return;
        }
        if (index != -1) {
            sentencesArray.getJsonObject(index).put("description", sentence.getString("description"))
                    .put("name", sentence.getString("name")).put("array_id", Integer.valueOf(index)).put("id", sentence.getInteger("id"));
        } else {
            sentencesArray.add(sentence);
            index = sentencesArray.size() - 1;
            sentence.put("array_id", Integer.valueOf(index));
        }
        DeliveryOptions options = new DeliveryOptions();
        options.setSendTimeout(10000L);
        eventBus.send("new_alert", sentence);
    }

    private void getSentencesFromDb() {
        Function<JsonObject, Void> func = new Function<JsonObject, Void>() {
            public Void apply(JsonObject result) {
                if (result.getInteger("response_code").intValue() <= MessageConfig.ERROR_CODE_FROM) {
                    JsonArray sentences = result.getJsonObject("data").getJsonArray("q200");
                    ChatServices.this.fillSentences(sentences);
                } else {
                    MessageLog.logMessage("Error when trying to GET sentences from the db", ChatServices.logger);
                }
                return null;
            }
        };
        dbLayer.selectFunction(func, String.format(SqlQueries.GET_SENTENCES, new Object[]{"sq_sentences_get"}));
    }

    public void testcreateConfigForUser(RoutingContext routingContext) {
        JsonObject json = new JsonObject();
        int userId = Integer.parseInt(routingContext.request().headers().get("id"));
        json.put("_id", String.valueOf(userId));
        json.put(UpConfig.CONVOS_COLLECTION, new JsonArray());
        mongoLayer.insertingDocuments("configurations", json, result -> {
            if (result.succeeded()) {
                result.result();
                boolean bool = false;
            } else {
                result.result();
                boolean bool = false;
            }
        });
    }

//    public static void createMongoConfigForNewUser(int userId, Handler<Boolean> handler) {
//        WorkerExecutor executor = vertx.createSharedWorkerExecutor("createMongoConfigForNewUser" + userId);
//        executor.executeBlocking(future -> {
//            try {
//                JsonObject json = new JsonObject();
//                json.put("_id", String.valueOf(userId));
//                json.put(UpConfig.CONVOS_COLLECTION, new JsonArray());
//                mongoLayer.insertingDocuments("configurations", json, ());
//            } catch (Exception e) {
//                throw new UncheckedIOException(new IOException(e));
//            } finally {
//                executor.close();
//            }
//        }x -> {
//
//        });
//    }

    public static JsonObject getSentence(int messageId) {
        return sentences.getOrDefault(Integer.valueOf(messageId), (new JsonObject()).put("name", "test"));
    }

    public void sendMail(RoutingContext routingContext) {
        ManagerServices.getManagerInfoAndSendMail(2);
    }

    public static void sendMailForUserPunchClock(String userName, String managerMail, String managerName) {
        String txt = "Dear mr " + managerName + ". The user " + userName + " has tried to punch clock several times from outside of the allowed range !!";
        NotificationsServices.sendDynamicMail(logger, managerMail, "send_mail.html", vertx, null, new Object[]{"Punch clock not allowed for user " + userName, txt});
    }

    public static void addSentence(int alertId, JsonObject alert) {
        int index = -1;
        if (sentences == null)
            sentences = new HashMap<>();
        if (sentences.containsKey(Integer.valueOf(alertId))) {
            index = ((JsonObject) sentences.get(Integer.valueOf(alertId))).getInteger("array_id").intValue();
        } else {
            sentences.put(Integer.valueOf(alertId), alert);
        }
        fillSentencesAfterUpdate(alert, index);
    }

    public static void removeSentence(int alertId) {
        JsonObject jsonObject = new JsonObject();
        if (sentences != null && sentences.containsKey(Integer.valueOf(alertId))) {
            jsonObject = sentences.remove(Integer.valueOf(alertId));
            fillSentencesAfterUpdate(jsonObject, -2);
        }
    }

    public void getOnlineUsers(RoutingContext routingContext) {
        WorkerExecutor executor = vertx.createSharedWorkerExecutor("getOnlineUsers" + routingContext.toString());
        executor.executeBlocking(future -> {
            try {
                DeliveryOptions options = new DeliveryOptions();
                options.setSendTimeout(10000L);
//                eventBus.send("online", null, options, ());
                this.eventBus.send("online", null, res -> {
                    if (res.succeeded()) {
                        MessageLog.sendMessageObject(routingContext, MessageConfig.MessageKey.SOCKET_MESSAGE_SEND,
                                new JsonObject().put("online",res.result().body()), logger);
                    } else {
                        MessageLog.sendErrorCode(routingContext, MessageConfig.MessageKey.MESSAGE_ERROR, "Failed to send message to communication server. Response came back false", logger);
                    }
                });
            } catch (Exception e) {
                MessageLog.sendErrorCode(routingContext, MessageConfig.MessageKey.ONLINE_USERS_ERROR, "Failed getting online users,due to the following Exception" + e.getMessage(), logger);
            } finally {
                executor.close();
            }
        }, x -> {
        });
    }
}