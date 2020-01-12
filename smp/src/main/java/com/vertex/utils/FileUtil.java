package com.vertex.utils;

import com.vertex.config.MessageConfig;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.json.JsonObject;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.function.Function;

public class FileUtil {
    public static void getFileContent(Function<JsonObject, Void> nextMethod, String file, Vertx vertx) {
        vertx.fileSystem().readFile(file, result -> {
            JsonObject resultJson = new JsonObject();
            if (result.succeeded()) {
                resultJson.put("response_code", Integer.valueOf(MessageConfig.MessageKey.READ_FILE_SUCCESS.val()));
                resultJson.put("content", ((Buffer)result.result()).toString());
                nextMethod.apply(resultJson);
            } else {
                resultJson.put("response_code", Integer.valueOf(MessageConfig.MessageKey.READ_FILE_ERROR.val()));
                resultJson.put("cause", result.cause());
                nextMethod.apply(resultJson);
            }
        });
    }

    public static void getFileContent(String file, Vertx vertx, Handler<JsonObject> handler) {
        vertx.fileSystem().readFile(file, result -> {
            JsonObject resultJson = new JsonObject();
            if (result.succeeded()) {
                resultJson.put("response_code", Integer.valueOf(MessageConfig.MessageKey.READ_FILE_SUCCESS.val()));
                resultJson.put("content", ((Buffer)result.result()).toString());
                handler.handle(resultJson);
            } else {
                resultJson.put("response_code", Integer.valueOf(MessageConfig.MessageKey.READ_FILE_ERROR.val()));
                resultJson.put("cause", result.cause());
                handler.handle(resultJson);
            }
        });
    }

    public static void overWriteFileContents(String fileName, String content) throws FileNotFoundException {
        PrintWriter prw = new PrintWriter(fileName);
        prw.println(content);
        prw.close();
    }
}
