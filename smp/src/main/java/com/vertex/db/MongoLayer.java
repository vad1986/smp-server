package com.vertex.db;

import com.vertex.config.DbConfig;
import com.vertex.config.MessageConfig;
import com.vertex.config.MessageLog;
import com.vertex.config.UpConfig;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.ext.mongo.BulkOperation;
import io.vertx.ext.mongo.FindOptions;
import io.vertx.ext.mongo.MongoClient;
import io.vertx.ext.mongo.MongoClientBulkWriteResult;
import io.vertx.ext.mongo.MongoClientDeleteResult;
import io.vertx.ext.mongo.MongoClientUpdateResult;
import io.vertx.ext.mongo.UpdateOptions;
import io.vertx.ext.web.RoutingContext;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Function;

public class MongoLayer {
    public static final String CAUSE = "cause";

    public static final String RESULT = "result";

    public static final String SUCCEEDED = "succeeded";

    private static Vertx vertx;

    private static Logger logger = LoggerFactory.getLogger(MongoLayer.class);

    private JsonObject configEntries;

    private static Map<String, MongoLayer> instanceMap = new HashMap<>();

    private MongoClient mongoClient;

    private static String entries = new String();

    private static final int timeoutInMillis = 3600000;

    private static final int PERIOD_TO_CHECK_CONNECTION_MILLIS = 1000;

    private static boolean isCheckingConnection = false;

    private static long lastMongoErrorLog = 0L;

    private static MongoDependencies mongoDependencies = new MongoDependencies();

    private MongoLayer(String mongoDatabase) {
        this.configEntries = new JsonObject(entries);
        this.configEntries.put("db_name", mongoDatabase);
        this.mongoClient = MongoClient.createNonShared(vertx, this.configEntries);
    }

    public static void init(Vertx vertx) {
        MongoLayer.vertx = vertx;
        DbConfig.getMongoConfig(vertx, MongoLayer::setMongoConfig);
    }

    private static void setMongoConfig(JsonObject entries) {
        if (UpConfig.AppParameters.get("MONGO") != null)
            entries.put("host", (String)UpConfig.AppParameters.get("MONGO"));
        MongoLayer.entries = entries.encode();
    }

    public static void initMongoDependencies() {
        long timerID = -1L;
        if (entries.isEmpty()) {
            timerID = vertx.setPeriodic(100L, new Handler<Long>() {
                public void handle(Long aLong) {
                    if (!MongoLayer.entries.isEmpty()) {
                        MongoLayer.vertx.cancelTimer(aLong.longValue());
                        MongoLayer.mongoDependencies.executeAll();
                    }
                }
            });
        } else {
            mongoDependencies.executeAll();
        }
    }

    public static void addMongoDependency(String dbName, Handler<MongoLayer> handler) {
        mongoDependencies.add(dbName, handler);
    }

    public static void getInstance(final String mongoDatabase, final Handler<MongoLayer> handler) {
        long timerID = -1L;
        if (entries.isEmpty()) {
            timerID = vertx.setPeriodic(100L, new Handler<Long>() {
                public void handle(Long aLong) {
                    if (!MongoLayer.entries.isEmpty()) {
                        MongoLayer.vertx.cancelTimer(aLong.longValue());
                        handler.handle(MongoLayer.getInstance(mongoDatabase));
                    }
                }
            });
        } else {
            handler.handle(getInstance(mongoDatabase));
        }
    }

    static MongoLayer getInstance(String mongoDatabase) {
        MongoLayer mongoLayer = instanceMap.get(mongoDatabase);
        if (mongoLayer == null) {
            String hostname = "Unknown";
            try {
                InetAddress addr = InetAddress.getLocalHost();
                hostname = addr.getHostName();
            } catch (Exception ex) {
                logger.error("Hostname can not be resolved: " + ex.getMessage());
            }
            MessageLog.HOST_NAME = hostname;
            synchronized (entries) {
                mongoLayer = new MongoLayer(mongoDatabase);
                instanceMap.put(mongoDatabase, mongoLayer);
            }
        }
        return mongoLayer;
    }

    public void createCollection(String collection) {
        this.mongoClient.createCollection(collection, this::logCreateResult);
    }

    private void logCreateResult(AsyncResult<Void> voidAsyncResult) {
        if (!voidAsyncResult.succeeded())
            logger.error("failed to create collection " + voidAsyncResult.cause().getMessage());
    }

    public void insertingDocuments(String collectionName, JsonObject jsonDoc, RoutingContext routingContext, Function func) {
        try {
            this.mongoClient.insert(collectionName, jsonDoc, res -> {
                if (res.succeeded()) {
                    func.apply((new JsonObject()).put("_id", jsonDoc.getString("_id")));
                } else {
                    func.apply((new JsonObject()).putNull("_id"));
                }
            });
        } catch (Exception e) {
            routingContext.response().end(Json.encodePrettily((new JsonObject())
                    .put("response_code", (Enum)MessageConfig.MessageKey.ERROR)));
        }
    }

    public void insertingDocuments(String collectionName, JsonObject jsonDoc, Handler<AsyncResult<String>> handler) {
        try {
            this.mongoClient.insert(collectionName, jsonDoc, handler);
            logger.info("saved document");
        } catch (Exception e) {
            logger.error("failed to save document");
        }
    }

    public void saveManyDocuments(final String collectionName, final List<JsonObject> jsonDocs, JsonObject query, final boolean deleteNotInsertedDocuments, final Function<JsonObject, Void> func) {
        Function<List<JsonObject>, Void> firstFunc = new Function<List<JsonObject>, Void>() {
            public Void apply(List<JsonObject> findList) {
                List<BulkOperation> bulkOperations = new ArrayList<>();
                boolean didAdd = false;
                boolean doesExist = false;
                if (findList == null) {
                    func.apply((new JsonObject()).put("error", "could not find any data in the collection"));
                    return null;
                }
                for (JsonObject jsonDoc : jsonDocs) {
                    for (JsonObject currFind : findList) {
                        if (MongoLayer.this.isDbIdMatching(jsonDoc, currFind)) {
                            bulkOperations.add(BulkOperation.createReplace(currFind, jsonDoc));
                            didAdd = true;
                            break;
                        }
                        if (deleteNotInsertedDocuments) {
                            for (JsonObject currSearchDoc : jsonDocs) {
                                if (MongoLayer.this.isDbIdMatching(currSearchDoc, currFind)) {
                                    doesExist = true;
                                    break;
                                }
                            }
                            if (!doesExist)
                                bulkOperations.add(BulkOperation.createDelete(currFind));
                        }
                    }
                    if (!didAdd)
                        bulkOperations.add(BulkOperation.createInsert(jsonDoc));
                    didAdd = false;
                }
                MongoLayer.this.mongoClient.bulkWrite(collectionName, bulkOperations, result -> {
                    if (result.succeeded()) {
                        func.apply(((MongoClientBulkWriteResult)result.result()).toJson());
                    } else {
                        func.apply((new JsonObject()).put("error", result.cause().getMessage()));
                    }
                });
                return null;
            }
        };
        this.mongoClient.find(collectionName, query, result -> {
            if (result.succeeded()) {
                firstFunc.apply(result.result());
            } else {
                firstFunc.apply(null);
            }
        });
    }

    private boolean isDbIdMatching(JsonObject outsideDoc, JsonObject inDbDoc) {
        if (inDbDoc.getValue("_id") instanceof JsonObject && inDbDoc.getValue("_id") != null) {
            if (outsideDoc.getValue("_id") instanceof JsonObject) {
                if (inDbDoc.getJsonObject("_id").getString("$oid").equals(outsideDoc.getJsonObject("_id").getString("$oid")))
                    return true;
            } else if (outsideDoc.getValue("_id") instanceof String &&
                    inDbDoc.getJsonObject("_id").getString("$oid").equals(outsideDoc.getString("_id"))) {
                return true;
            }
        } else if (inDbDoc.getValue("_id") instanceof String && inDbDoc.getValue("_id") != null) {
            if (outsideDoc.getValue("_id") instanceof String) {
                if (inDbDoc.getString("_id").equals(outsideDoc.getString("_id")))
                    return true;
            } else if (outsideDoc.getValue("_id") instanceof JsonObject &&
                    inDbDoc.getString("_id").equals(outsideDoc.getJsonObject("_id").getString("$oid"))) {
                return true;
            }
        }
        return false;
    }

    public void deleteDocuments(String collectionName, JsonObject jsonDoc) {
        try {
            this.mongoClient.findOneAndDelete(collectionName, jsonDoc, this::logDeleteResult);
        } catch (Exception e) {
            logger.error(e);
        }
    }

    private void logDeleteResult(AsyncResult<JsonObject> jsonObjectAsyncResult) {
        if (!jsonObjectAsyncResult.succeeded())
            logger.error("failed to delete document to mongo ", jsonObjectAsyncResult.cause());
    }

    public void getCollections(Handler<List> handler) {
        this.mongoClient.getCollections(res -> {
            if (res.succeeded()) {
                List<String> collections = (List<String>)res.result();
                handler.handle(collections);
            } else {
                res.cause().printStackTrace();
            }
        });
    }

    public void findUserData(String collection, JsonObject query, Handler<AsyncResult<JsonObject>> handler) {
        this.mongoClient.findOne(collection, query, new JsonObject(), handler);
    }

    public void find(String collection, JsonObject query, RoutingContext routingContext) {
        find(collection, query, this::sendResult, routingContext);
    }

    public void find(String collection, JsonObject query, Handler<RoutingContext> handler, RoutingContext routingContext) {
        this.mongoClient.find(collection, query, res -> {
            routingContext.put("succeeded", Boolean.valueOf(res.succeeded()));
            if (res.succeeded()) {
                JsonObject resultObject = new JsonObject();
                resultObject.put("result", res.result());
                routingContext.put("result", resultObject);
            } else {
                routingContext.put("cause", res.cause());
            }
            handler.handle(routingContext);
        });
    }

    public void find(String collection, JsonObject query, Function<List<JsonObject>, Void> func) {
        this.mongoClient.find(collection, query, res -> {
            if (res.succeeded()) {
                func.apply(res.result());
            } else {
                func.apply(new ArrayList());
            }
        });
    }

    public void findOne(String collection, JsonObject query, Function<JsonObject, Void> func) {
        this.mongoClient.findOne(collection, query, null, res -> {
            if (res.succeeded()) {
                func.apply(res.result());
            } else {
                func.apply(null);
            }
        });
    }

    public void findLimit(String collection, JsonObject query, Function<List<JsonObject>, Void> func, int limit) {
        FindOptions findOptions = new FindOptions();
        findOptions.setLimit(limit);
        findOptions.setSort((new JsonObject()).put("$natural", Integer.valueOf(-1)));
        this.mongoClient.findWithOptions(collection, query, findOptions, res -> {
            if (res.succeeded()) {
                func.apply(res.result());
            } else {
                func.apply(new ArrayList());
            }
        });
    }

    private void findSort(String collection, JsonObject query, Handler<RoutingContext> handler, RoutingContext routingContext, int limit) {
        FindOptions findOptions = new FindOptions();
        findOptions.setLimit(limit);
        this.mongoClient.findWithOptions(collection, query, findOptions, res -> {
            routingContext.put("succeeded", Boolean.valueOf(res.succeeded()));
            if (res.succeeded()) {
                JsonObject resultObject = new JsonObject();
                resultObject.put("result", res.result());
                routingContext.put("result", resultObject);
            } else {
                routingContext.put("cause", res.cause());
            }
            handler.handle(routingContext);
        });
    }

    public void findSort(String collection, JsonObject query, RoutingContext routingContext, int limit) {
        findSort(collection, query, this::sendResult, routingContext, limit);
    }

    public void findSortByParams(String collection, JsonObject query, RoutingContext routingContext, int way, String name) {
        findSortByParams(collection, query, this::sendResult, routingContext, way, name, -1);
    }

    public void findSortByParams(String collection, JsonObject query, RoutingContext routingContext, int way, String name, int limit) {
        findSortByParams(collection, query, this::sendResult, routingContext, way, name, limit);
    }

    private void findSortByParams(String collection, JsonObject query, Handler<RoutingContext> handler, RoutingContext routingContext, int way, String name, int limit) {
        FindOptions findOptions = new FindOptions();
        findOptions.setSort((new JsonObject()).put(name, Integer.valueOf(way)));
        if (limit > 0)
            findOptions.setLimit(limit);
        this.mongoClient.findWithOptions(collection, query, findOptions, res -> {
            routingContext.put("succeeded", Boolean.valueOf(res.succeeded()));
            if (res.succeeded()) {
                JsonObject resultObject = new JsonObject();
                resultObject.put("result", res.result());
                routingContext.put("result", resultObject);
            } else {
                routingContext.put("cause", res.cause());
            }
            handler.handle(routingContext);
        });
    }

    public void findUserData(String collection, JsonObject query, RoutingContext routingContext) {
        findUserData(collection, query, this::sendResult, routingContext);
    }

    public void findUserData(String collection, JsonObject query, Handler<RoutingContext> handler, RoutingContext routingContext) {
        this.mongoClient.findOne(collection, query, null, res -> {
            routingContext.put("succeeded", Boolean.valueOf(res.succeeded()));
            if (res.succeeded()) {
                routingContext.put("result", res.result());
            } else {
                routingContext.put("cause", res.cause());
            }
            handler.handle(routingContext);
        });
    }

    public void findUserData(String collection, JsonObject query, Function<JsonObject, Void> func) {
        this.mongoClient.find(collection, query, res -> {
            if (res.succeeded()) {
                JsonObject resultObject = new JsonObject();
                resultObject.put("result", res.result());
                func.apply(resultObject);
            } else {
                logger.error(res.cause());
            }
        });
    }

    private void sendResult(RoutingContext routingContext) {
        try {
            boolean succeeded = ((Boolean)routingContext.get("succeeded")).booleanValue();
            if (succeeded) {
                JsonObject resultObject = (JsonObject)routingContext.get("result");
                MessageLog.sendMessageObject(routingContext, MessageConfig.MessageKey.OK, resultObject, logger);
            }
        } catch (Exception e) {
            MessageLog.sendErrorCode(routingContext, MessageConfig.MessageKey.ERROR, "Failed to pars mongo result", logger);
        }
    }

    public void removeDocument(String collectionName, JsonObject jsonDoc) {
        try {
            this.mongoClient.removeDocument(collectionName, jsonDoc, this::documentRemoved);
        } catch (Exception e) {
            logger.error(e);
        }
    }

    public void replaceDocument(String collectionName, JsonObject jsonDoc1, JsonObject jsonDoc2) {
        try {
            this.mongoClient.replaceDocuments(collectionName, jsonDoc1, jsonDoc2, this::documentReplace);
        } catch (Exception e) {
            logger.error(e);
        }
    }

    private void documentReplace(AsyncResult<MongoClientUpdateResult> mongoClientUpdateResultAsyncResult) {}

    private void documentRemoved(AsyncResult<MongoClientDeleteResult> mongoClientDeleteResultAsyncResult) {}

    public void updateDocuments(String collectionName, JsonObject query, JsonObject update) {
        try {
            this.mongoClient.findOneAndUpdate(collectionName, query, update, this::updateRest);
        } catch (Exception e) {
            logger.error(e);
        }
    }

    public void updateDocuments(String collectionName, JsonObject query, JsonObject update, RoutingContext routingContext) {
        try {
            this.mongoClient.findOneAndUpdate(collectionName, query, update, this::updateRest);
            routingContext.response().end(Json.encodePrettily((new JsonObject()).put("response_code", Integer.valueOf(MessageConfig.MessageKey.ERROR.val()))));
        } catch (Exception e) {
            routingContext.response().end(Json.encodePrettily((new JsonObject()).put("response_code", Integer.valueOf(MessageConfig.MessageKey.ERROR.val()))));
            logger.error(e);
        }
    }

    public void pushToDocuments(String collectionName, JsonObject query, JsonObject update) {
        try {
            JsonObject push = (new JsonObject()).put("$push", update);
            this.mongoClient.findOneAndUpdate(collectionName, query, push, this::updateRest);
        } catch (Exception e) {
            logger.error(e);
        }
    }

    private void updateRest(AsyncResult<JsonObject> jsonObjectAsyncResult) {
        if (!jsonObjectAsyncResult.succeeded())
            logger.error("failed to removed document to mongo ", jsonObjectAsyncResult.cause());
    }

    public void getGeneratedLinkToPdf(RoutingContext routingContext) {
        String id = routingContext.request().getParam("id");
        JsonObject jsonObject = (new JsonObject()).put("_id", id);
        find("ContentMessages", jsonObject, routingContext);
    }

    public void dropCollection(String collectionName) {
        try {
            this.mongoClient.dropCollection(collectionName, res -> {
                if (!res.succeeded())
                    res.cause().printStackTrace();
            });
        } catch (Exception e) {
            logger.error(e);
        }
    }

    public void replaceDoc(String collectionName, JsonObject original, JsonObject update) throws Exception {
        try {
            this.mongoClient.replaceDocuments(collectionName, original, update, res -> {
                if (!res.succeeded())
                    logger.error("Failed replacing error");
            });
        } catch (Exception e) {
            throw new Exception(e);
        }
    }

    public void insertingDocuments(String collectionName, JsonObject jsonDoc, Function<AsyncResult<String>, Void> func) throws Exception {
        try {
            jsonDoc.remove("_id");
            this.mongoClient.insert(collectionName, jsonDoc, res -> {
                if (res.succeeded()) {
                    func.apply(res);
                } else {
                    func.apply(null);
                }
            });
        } catch (Exception e) {
            logger.info("failed to save document");
            throw new Exception(e);
        }
    }

    public void isAddressExists(String collectionName, JsonObject query, Function<JsonObject, Void> func) throws Exception {
        try {
            AtomicBoolean isExists = new AtomicBoolean(false);
            JsonObject response = (new JsonObject()).put("query", query);
            this.mongoClient.find(collectionName, query, res -> {
                if (res.succeeded()) {
                    if (!((List)res.result()).isEmpty()) {
                        isExists.set(true);
                        long userID = ((JsonObject)((List<JsonObject>)res.result()).get(0)).getLong("userID").longValue();
                        long accountID = ((JsonObject)((List<JsonObject>)res.result()).get(0)).getLong("accountID").longValue();
                        String symbol = ((JsonObject)((List<JsonObject>)res.result()).get(0)).getString("symbol");
                        long timeStamp = ((JsonObject)((List<JsonObject>)res.result()).get(0)).getLong("time").longValue();
                        response.put("user_id", Long.valueOf(userID));
                        response.put("symbol", symbol);
                        response.put("accountID", Long.valueOf(accountID));
                        response.put("time_stamp", Long.valueOf(timeStamp));
                    }
                    response.put("isExists", Boolean.valueOf(isExists.get()));
                    func.apply(response);
                } else {
                    func.apply(null);
                }
            });
        } catch (Exception e) {
            throw new Exception(e);
        }
    }

    public void deleteDocuments(String collectionName, JsonObject jsonDoc, Function func) throws Exception {
        try {
            jsonDoc.remove("_id");
            this.mongoClient.findOneAndDelete(collectionName, jsonDoc, res -> {
                if (res.succeeded()) {
                    JsonObject jsonObject = (JsonObject)res.result();
                    func.apply(jsonObject);
                } else {
                    func.apply(null);
                }
            });
        } catch (Exception e) {
            logger.error(e);
            throw new Exception(e);
        }
    }

    public void removeDocuments(String collectionName, JsonObject query, Function func) {
        this.mongoClient.removeDocuments(collectionName, query, res -> {
            if (res.succeeded()) {
                func.apply(((MongoClientDeleteResult)res.result()).toJson());
            } else {
                func.apply(null);
            }
        });
    }

    public void updateDocuments(String collectionName, JsonObject query, JsonObject update, Function<AsyncResult<JsonObject>, Void> func) throws Exception {
        try {
            this.mongoClient.findOneAndUpdate(collectionName, query, update, res -> {
                if (res.succeeded()) {
                    func.apply(res);
                } else {
                    MessageLog.logMessage("Failed updating Mongo tx: " + res.cause(), logger);
                }
            });
        } catch (Exception e) {
            logger.error(e);
            throw new Exception(e);
        }
    }

    public void updateDocumentArray(String collectionName, JsonObject query, JsonObject update, Function<JsonObject, Void> func) throws Exception {
        try {
            this.mongoClient.findOneAndUpdate(collectionName, query, update, res -> {
                if (res.succeeded()) {
                    func.apply(res.result());
                } else {
                    func.apply(null);
                }
                MessageLog.logMessage("Failed updating Mongo tx: " + res.cause(), logger);
            });
        } catch (Exception e) {
            logger.error(e);
            throw new Exception(e);
        }
    }

    public void findOneAndUpdateWithOptions(String collectionName, JsonObject query, JsonObject update, FindOptions findOptions, UpdateOptions updateOptions, Function<JsonObject, Void> func) {
        try {
            this.mongoClient.findOneAndUpdateWithOptions(collectionName, query, update, findOptions, updateOptions, res -> {
                if (res.succeeded()) {
                    func.apply(res.result());
                } else {
                    func.apply(null);
                }
            });
        } catch (Exception e) {
            func.apply(null);
        }
    }
}
