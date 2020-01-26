package com.vertex.verticle;

import com.vertex.config.DbConfig;
import com.vertex.config.MessageConfig;
import com.vertex.config.UpConfig;
import com.vertex.config.UriRoles;
import com.vertex.db.DbLayer;
import com.vertex.db.MongoLayer;
import com.vertex.services.ChatServices;
import com.vertex.services.ManagerServices;
import com.vertex.services.UserServices;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Verticle;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.core.eventbus.Message;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.HttpServer;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.ext.jdbc.JDBCClient;
import io.vertx.ext.sql.SQLClient;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.CorsHandler;
import java.util.HashSet;
import java.util.Set;

public class MainVerticle extends AbstractVerticle {
    protected Logger logger = LoggerFactory.getLogger(getClass());

    protected UserServices userServices;

    protected ManagerServices managerServices;

    protected DbLayer dbLayer;

    protected ChatServices chatServices;

    public void start(Future<Void> startFuture) throws Exception {
        super.start(startFuture);
        this.dbLayer = new DbLayer(this.vertx);
        Router router = Router.router(this.vertx);
        router.route().handler(BodyHandler.create().setMergeFormAttributes(true));
        HttpServer server = this.vertx.createHttpServer();
        server.requestHandler(router).listen(UpConfig.PORT);

        this.logger.info("Vertx Started on port " + UpConfig.PORT);
        router.route().handler(BodyHandler.create());
        Set<HttpMethod> allowedMethods = new HashSet<>();
        allowedMethods.add(HttpMethod.GET);
        allowedMethods.add(HttpMethod.POST);
        allowedMethods.add(HttpMethod.PUT);
        allowedMethods.add(HttpMethod.DELETE);
        allowedMethods.add(HttpMethod.OPTIONS);
        Set<String> allowdHeaders = new HashSet<>();
        allowdHeaders.add("user_id");
        allowdHeaders.add("manager_id");
        allowdHeaders.add("access-control-allow-origin");
        allowdHeaders.add("Content-Type");
        router.route().handler((Handler<RoutingContext>)CorsHandler.create("*")
                .allowedMethods(allowedMethods)
                .allowedHeaders(allowdHeaders));
        router.route().handler((Handler<RoutingContext>)BodyHandler.create());
        init(this.vertx, router);
    }

    public void init(Vertx vertx, Router router) {
        this.userServices = new UserServices(this.dbLayer, vertx, vertx.eventBus());
        this.managerServices = new ManagerServices(this.dbLayer, vertx, vertx.eventBus());
        this.chatServices = new ChatServices(vertx.eventBus());
        this.chatServices.init(this.dbLayer, null, vertx, this.logger);
        router.route("/*").handler(this::printRequest);
        router.route(UriRoles.LOGIN + "/*").handler(this.userServices::checkAuthInfo);
        router.route(UriRoles.MANAGER + "/*").handler(this.userServices::checkPermissions);
        router.post("/login").handler(this.userServices::login);
        router.post(UriRoles.LOGIN + "/logout").handler(this.userServices::logout);
        router.post(UriRoles.LOGIN + "/punchClock").handler(this.userServices::punchClock);
        router.post(UriRoles.MANAGER + "/punchClock").handler(this.managerServices::punchClock);
        router.post("/createSentence").handler(this.managerServices::createNewSentence);
        router.post("/createDepartment").handler(this.managerServices::createDepartment);
        router.post(UriRoles.LOGIN + "/new_task").handler(this.managerServices::newTask);
        router.post(UriRoles.MANAGER + "/new_user").handler(this.managerServices::createNewUser);
        router.post("/message").handler(this.userServices::sendNewMessage);
        router.post(UriRoles.LOGIN + "/close-task").handler(this.managerServices::closeTask);
        router.post("/createConfig").handler(this.chatServices::testcreateConfigForUser);
        router.post("/sendMessageToUser").handler(this.chatServices::testcreateConfigForUser);
        router.post("/sendMail").handler(this.chatServices::sendMail);
        router.post(UriRoles.MANAGER + "/addShopLocation").handler(this.managerServices::addShopLocation);
        router.post("/report").handler(this.managerServices::sendReport);
        router.post("/forgot-password").handler(this.userServices::forgotPassword);
        router.get(UriRoles.MANAGER + "/get_open_tasks").handler(this.managerServices::getOpenTasks);
        router.get(UriRoles.LOGIN + "/get_my_users").handler(this.managerServices::getMyUsers);
        router.get(UriRoles.LOGIN + "/get_user_tasks/:status").handler(this.userServices::getUserTasks);
        router.get( "/messages").handler(this.userServices::getUserMessages);//UriRoles.LOGIN +
        router.get(UriRoles.LOGIN + "/config").handler(this.userServices::getUserChatConfig);
        router.get("/checkPrivate/:key/:user_id").handler(this.userServices::checkPrivateKey);
        router.get("/onlineUsers").handler(this.chatServices::getOnlineUsers);
        router.get("/punch_clock-params").handler(this.userServices::getMainParams);
        router.get("/user-attendance/:type").handler(this.userServices::getUserAttendance);
        router.get("/user-attendance-all/:id").handler(this.managerServices::getUserAttendanceForManager);
        router.get(UriRoles.LOGIN + "/ping").handler(this.managerServices::ping);

        vertx.setTimer(2000L, handler -> sendTestMessage());
    }

    private void printRequest(RoutingContext routingContext) {
        if (routingContext.request().method() != HttpMethod.GET) {
            if(!routingContext.request().isEnded()){
                JsonObject body = routingContext.getBodyAsJson();
                System.out.println("HEADERS:");
                routingContext.request().headers().forEach(header -> System.out.println("key: " + (String)header.getKey() + " value: " + (String)header.getValue()));
                System.out.println("BODY: ");
                System.out.println(body.toString());
            }
            routingContext.next();
        } else {
            routingContext.next();
        }
    }

    private void sendTestMessage() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.put("user_id_to", Integer.valueOf(2));
        jsonObject.put("message", "hey dude");
        this.vertx.eventBus().send("message", jsonObject, reply -> {
            if (reply.succeeded()) {
                JsonObject jsonMessage = (JsonObject)((Message)reply.result()).body();
                int responseCode = jsonMessage.getInteger("response_code").intValue();
                if (responseCode < MessageConfig.MessageKey.ERROR_CODE.val()) {
                    System.out.println("got a reply after sending test message to socket Verticle");
                } else {
                    System.out.println("failed inside");
                }
            }
        });
    }

    private void sendTestEvent() {
        this.vertx.eventBus().send("message", "Hello dude");
    }

    public void initServices(MongoLayer mongoLayer) {}

    public static void main(String[] args) {
        try {
            VertxOptions vertxOptions = new VertxOptions();
            vertxOptions.setClustered(true);
            UpConfig.setArgs(args);
            String verticleName = (String)UpConfig.AppParameters.getOrDefault("Verticle", "com.vertex.verticle.MainVerticle");
            Class<?> ctClass = Class.forName(verticleName);
            AbstractVerticle verticle = (AbstractVerticle) ctClass.getDeclaredConstructor(new Class[0]).newInstance(new Object[0]);
            Vertx.clusteredVertx(vertxOptions, res -> {
                if (res.succeeded()) {
                    Vertx vertx = (Vertx)res.result();
                    vertx.deployVerticle((Verticle)verticle);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
