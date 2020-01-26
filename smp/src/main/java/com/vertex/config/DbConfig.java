package com.vertex.config;

import com.vertex.utils.FileUtil;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import java.util.function.Function;

public class DbConfig {
    private static String localDbUrl = "jdbc:mysql://smp.c01ihlvk92af.eu-west-3.rds.amazonaws.com:3306/sys?useSSL=false";

    public static JsonObject localDbConfig = (new JsonObject())
            .put("url", localDbUrl)
            .put("user", "vadim")
            .put("password", "vad2000!")
            .put("driver_class", "com.mysql.jdbc.Driver")
            .put("max_pool_size", 30)
            .put("max_idle_time", 10)
            .put("autoReconnect", Boolean.TRUE);

    public static void getMongoConfig(Vertx vertx, final Handler<JsonObject> method) {
        Function<JsonObject, Void> func = new Function<JsonObject, Void>() {
            public Void apply(JsonObject result) {
                try {
                    if (result.getInteger("response_code").intValue() < MessageConfig.ERROR_CODE_FROM) {
                        JsonObject contentJson = new JsonObject(result.getString("content"));
                        method.handle(contentJson);
                    } else {
                        method.handle(result);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }
        };
        FileUtil.getFileContent(func, "config/mongo.json", vertx);
    }
}
