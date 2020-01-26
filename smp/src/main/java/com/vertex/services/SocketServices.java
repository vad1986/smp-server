package com.vertex.services;

import com.vertex.config.MessageConfig;
import com.vertex.config.MessageLog;
import com.vertex.config.UpConfig;
import com.vertex.dataObjects.User;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.WorkerExecutor;
import io.vertx.core.eventbus.Message;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.ServerWebSocket;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class SocketServices {
    private static Map<User, ServerWebSocket> connectedUsers;

    private static Map<ServerWebSocket, User> connectedSockets;

    private final Vertx vertx;

    protected Logger logger = LoggerFactory.getLogger(getClass());

    public SocketServices(Vertx vertx) {
        connectedUsers = new HashMap<>();
        connectedSockets = new HashMap<>();
        this.vertx = vertx;
    }

    public void startWebsocket(HttpServer server) {
        server.websocketHandler(new Handler<ServerWebSocket>() {
            public void handle(final ServerWebSocket serverWebSocket) {
                System.out.println("connected finally");
                serverWebSocket.writeTextMessage("hey dude this is the answer from the server.P.s.  Arkasha zevel!!!!");
                serverWebSocket.textMessageHandler(new Handler<String>() {
                    public void handle(String s) {
                        System.out.println(s);
                        SocketServices.this.incomingMessage(s, serverWebSocket);
                    }
                });
                serverWebSocket.closeHandler(new Handler<Void>() {
                    public void handle(Void event) {
                        System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!THE SOCKET CONNECTION WAS CLOSED!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
                        SocketServices.this.removeConnectedSocket(serverWebSocket);
                    }
                });
            }
        }).listen(UpConfig.SOCKET_PORT);
    }

    public void sendNewMessage(Message msg) {
        WorkerExecutor executor = this.vertx.createSharedWorkerExecutor("sendNewMessage" + msg.toString());
        executor.executeBlocking(future -> {
            try {
                JsonObject jsonObject = (JsonObject) msg.body();
                int userId = jsonObject.getInteger("user_id_to");
                int userIdFrom = jsonObject.getInteger("user_id_from");
                jsonObject.getJsonObject("message").put("user_id_from", userIdFrom);
                String messageString = jsonObject.getJsonObject("message").put("command", "alert").toString();
                sendActualMessage(userId, msg, messageString);
            } catch (Exception e) {
                MessageLog.logMessage("Failed sending new message through socket due to thefollowing Exception: " + e.getMessage(), this.logger);
                msg.reply((new JsonObject()).put("response_code", MessageConfig.MessageKey.SOCKET_MESSAGE_SEND_ERROR.val()));
            } finally {
                executor.close();
            }
        }, x -> {

        });
    }

    private void sendActualMessage(int userId, Message msg, String messageString) {
        User user = new User(userId);
        ServerWebSocket socket = getUserWebsocket(user);
        if (socket != null) {
            socket.writeTextMessage(messageString);
            MessageLog.logMessage("Successfully sent message to user: " + userId, this.logger);
            msg.reply((new JsonObject()).put("response_code", MessageConfig.MessageKey.SOCKET_MESSAGE_SEND.val()));
        } else {
            MessageLog.logMessage("Failed sending new message through socket.No socket address was found in the connected users map assosiated with userId: " + userId, this.logger);
            msg.reply((new JsonObject()).put("response_code", MessageConfig.MessageKey.SOCKET_MESSAGE_SEND_ERROR.val()));
        }
    }

    public void incomingMessage(String stringMessage, ServerWebSocket serverWebSocket) {
        int userId;
        String name;
        JsonObject json = new JsonObject(stringMessage);
        String command = json.getString("command");
        switch (command) {
            case "subscribe":
                System.out.println("subscribe");
                userId = json.getInteger("user_id");
                name = json.getString("user_name");
                addUserWebSocket(new User(userId, name), serverWebSocket);
                sendCommandToAll(json);
                break;
        }
    }

    private void sendCommandToAll(JsonObject jsonObject) {
        connectedUsers.forEach((id, socket) -> socket.writeTextMessage(jsonObject.toString()));
    }

    private void addUserWebSocket(User user, ServerWebSocket serverWebSocket) {
        if (connectedUsers == null)
            connectedUsers = new HashMap<>();
        if (connectedSockets == null)
            connectedSockets = new HashMap<>();
        connectedSockets.put(serverWebSocket, user);
        connectedUsers.put(user, serverWebSocket);
    }

    private ServerWebSocket getUserWebsocket(User user) {
        if (connectedUsers != null && connectedUsers.containsKey(user))
            return connectedUsers.get(user);
        return null;
    }

    public void sendTask(Message msg) {
        WorkerExecutor executor = this.vertx.createSharedWorkerExecutor("logoutUser" + msg.toString());
        executor.executeBlocking(future -> {
            try {
                JsonObject jsonObject = (JsonObject) msg.body();
                jsonObject.put("command", "task");
                int userId = jsonObject.getInteger("user_id");
                sendActualMessage(userId, msg, jsonObject.toString());
            } catch (Exception e) {
                msg.reply((new JsonObject()).put("response_code", MessageConfig.MessageKey.SOCKET_MESSAGE_SEND_ERROR.val()));
            } finally {
                executor.close();
            }
        }, x -> {

        });
    }

    private void removeConnectedUser(User user) {
        System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!  REMOVED USER " + user.getUserID() + " !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
        if (connectedUsers != null) {
            connectedUsers.remove(user);
            sendCommandToAll((new JsonObject()).put("command", "unsubscribe").put("user_name", user.getUserName()).put("user_id", user.getUserID()));
        }
    }

    private void removeConnectedSocket(ServerWebSocket socket) {
        if (connectedSockets != null) {
            User user = connectedSockets.remove(socket);
            removeConnectedUser(user);
            System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!  REMOVED socket " + user + " !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
        }
    }

    public void logoutUser(Message msg) {
        WorkerExecutor executor = this.vertx.createSharedWorkerExecutor("logoutUser" + msg.toString());
        executor.executeBlocking(future -> {
            try {
                JsonObject jsonObject = (JsonObject) msg.body();
                int userId = jsonObject.getInteger("user_id");
                String name = jsonObject.getString("user_name");
                removeConnectedUser(new User(userId, name));
                sendCommandToAll(jsonObject);
            } catch (Exception e) {
                MessageLog.logMessage("Failed removing user from online users due to thefollowing Exception: " + e.getMessage(), this.logger);
            } finally {
                executor.close();
            }
        }, x -> {

        });
    }

    public void sendNewAlert(Message msg) {
        WorkerExecutor executor = this.vertx.createSharedWorkerExecutor("sendNewAlert" + msg.toString());
        executor.executeBlocking(future -> {
            try {
                JsonObject jsonObject = (JsonObject) msg.body();
                jsonObject.put("command", "new_alert");
                sendCommandToAll(jsonObject);
            } catch (Exception e) {
                MessageLog.logMessage("Failed removing user from online users due to thefollowing Exception: " + e.getMessage(), this.logger);
            } finally {
                executor.close();
            }
        }, x -> {

        });
    }

    public void getOnlineUsers(Message message) {
        WorkerExecutor executor = this.vertx.createSharedWorkerExecutor("getOnlineUsers" + message.toString());
        executor.executeBlocking(future -> {
            try {
                JsonArray jsonArray = getUsersJsonArray();
                message.reply(jsonArray);
            } catch (Exception e) {
                MessageLog.logMessage("Failed sending new message through socket due to thefollowing Exception: " + e.getMessage(), this.logger);
                message.reply(new JsonArray());
            } finally {
                executor.close();
            }
        }, x -> {

        });
    }

    private JsonArray getUsersJsonArray() {
        JsonArray array = new JsonArray();
        connectedUsers.forEach((user, socket) -> array.add((new JsonObject()).put("user_name", user.getUserName()).put("user_id", user.getUserID())));
        return array;
    }
}
