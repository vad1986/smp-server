package com.vertex.db;

import com.vertex.config.DbConfig;
import com.vertex.config.MessageConfig;
import com.vertex.config.ResponseUtil;
import io.vertx.core.AsyncResult;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.ext.jdbc.JDBCClient;
import io.vertx.ext.sql.ResultSet;
import io.vertx.ext.sql.SQLClient;
import io.vertx.ext.sql.SQLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class DbLayer {
    private Logger logger = LoggerFactory.getLogger(getClass());

    protected SQLClient sqlClient;

    public DbLayer(Vertx vertx) {
        JsonObject config = DbConfig.localDbConfig;
        this.sqlClient = (SQLClient)JDBCClient.createShared(vertx, config);
    }



    public void callProcedure(Function<JsonObject, Void> nextMethod, String sql, JsonArray in, JsonArray out, String id) throws Exception {
        this.sqlClient.getConnection((con) -> {
            if (con.succeeded()) {
                SQLConnection connection = (SQLConnection)con.result();
                connection.callWithParams(sql, in, out, (res) -> {
                    String outStr = "";
                    if (res.succeeded()) {
                        ResultSet result = (ResultSet)res.result();
                        JsonObject jsonObject = (new JsonObject()).put("data", result.getOutput());

                        try {
                            jsonObject = getOutSlashes(jsonObject.toString());
                        } catch (Exception var14) {
                            this.logger.error(var14);
                        } finally {
                            nextMethod.apply(ResponseUtil.getAsJson(MessageConfig.MessageKey.OK, sql, this.logger).put("id", id).put("data", jsonObject.getJsonArray("data")));
                        }
                    } else {
                        nextMethod.apply(ResponseUtil.getAsJson(MessageConfig.MessageKey.DB_SQL_ERROR, "Failed to get data " + res.cause().getMessage() + " IN=" + in + " OUT=" + out, this.logger, res.cause()));
                    }

                });
            } else {
                nextMethod.apply(ResponseUtil.getAsJson(MessageConfig.MessageKey.DB_CONNECTION_FAILED, "Failed to get connection " + sql + " : " + con.cause().getMessage(), this.logger, con.cause()));
            }

        });
    }


    public void callSelectProcedure(Function<JsonObject, Void> nextMethod, String sql, JsonArray in, JsonArray out, String id) throws Exception {
        this.sqlClient.getConnection((con) -> {
            if (con.succeeded()) {
                SQLConnection connection = (SQLConnection)con.result();
                connection.callWithParams(sql, in, out, (res) -> {
                    String outStr = "";
                    if (res.succeeded()) {
                        ResultSet rs = (ResultSet)res.result();
                        List<JsonObject> resultList = new ArrayList();
                        if (rs != null) {
                            resultList = rs.getRows();
                        }

                        nextMethod.apply(ResponseUtil.getAsJson(MessageConfig.MessageKey.OK, sql, this.logger).put("id", id).put("data", resultList));
                    } else {
                        nextMethod.apply((new JsonObject()).put("cause", res.cause()));
                    }

                    connection.close((done) -> {
                        if (done.failed()) {
                            this.logger.error(done.cause());
                        }

                    });
                });
            } else {
                nextMethod.apply(ResponseUtil.getAsJson(MessageConfig.MessageKey.DB_CONNECTION_FAILED, "Failed to get connection " + sql + " : " + con.cause().getMessage(), this.logger, con.cause()));
            }

        });
    }

    public static JsonObject getOutSlashes(String outVal) throws Exception {
        String outStr = "";
        if (outStr.contains("["))
            outStr = outStr.replaceFirst(" \"", " ");
        outStr = outVal.toString().replaceAll("\\\\", "");
        outStr = outStr.replaceAll("}\"", "}");
        outStr = outStr.replaceAll("]\"", "]");
        return new JsonObject(outStr);
    }

    public static JsonObject getOutSlashes2(String outVal) throws Exception {
        String outStr = "";
        String s = "\"\\{";
        String s2 = ",]";
        outStr = outVal.toString().replaceAll("\\\\", "");
        outStr = outStr.replaceAll("}\"", "}");
        outStr = outStr.replaceAll(s, "{");
        outStr = outStr.replaceAll("]\"", "]");
        outStr = outStr.replaceAll("\"\\[", "[");
        outStr = outStr.replaceAll(s2, "]");
        return new JsonObject(outStr);
    }

    public static JsonArray getOutSlashesForArray(String outVal) throws Exception {
        String outStr = "";
        outStr = outVal.toString().replaceAll("\\\\", "");
        outStr = outStr.replaceAll("}\"", "}");
        outStr = outStr.replaceAll("]\"", "]");
        outStr = outStr.replaceFirst(" \"", " ");
        return new JsonArray(outStr);
    }

    public void selectFunction(Function<JsonObject, Void> nextMethod, String sql) {
        this.sqlClient.getConnection((res) -> {
            if (res.succeeded()) {
                SQLConnection connection = (SQLConnection)res.result();
                connection.query(sql, (res2) -> {
                    if (res2.succeeded()) {
                        ResultSet rs = (ResultSet)res2.result();
                        JsonArray outList = new JsonArray(rs.getRows());
                        JsonObject jsonObject = outList.getJsonObject(0);
                        String outStr = jsonObject.getString("res");

                        try {
                            jsonObject = getOutSlashes2(outStr);
                        } catch (Exception var12) {
                            this.logger.error(var12);
                        } finally {
                            nextMethod.apply(ResponseUtil.getAsJson(MessageConfig.MessageKey.OK, sql, this.logger).put("data", jsonObject));
                        }
                    }

                });
            } else {
                nextMethod.apply(ResponseUtil.getAsJson(MessageConfig.MessageKey.DB_CONNECTION_FAILED, "Failed to get connection " + res.cause(), this.logger));
            }

        });
    }
}
