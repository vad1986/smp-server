package com.vertex.verticle;

import com.vertex.config.UpConfig;
import com.vertex.db.DbLayer;
import com.vertex.services.SocketServices;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.HttpServer;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.CorsHandler;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class CommunicationVerticle extends AbstractVerticle {
    protected Logger logger = LoggerFactory.getLogger(getClass());

    protected DbLayer dbLayer;

    SocketServices socketServices;

    public void start(Future<Void> startFuture) throws Exception {
        super.start(startFuture);
        this.dbLayer = new DbLayer(this.vertx);
        Router router = Router.router(this.vertx);
        router.route().handler((Handler)BodyHandler.create().setMergeFormAttributes(true));
        HttpServer server = this.vertx.createHttpServer();
        this.logger.info("Vertix Started on port " + UpConfig.SOCKET_PORT);
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
        router.route().handler((Handler)CorsHandler.create("*")
                .allowedMethods(allowedMethods)
                .allowedHeaders(allowdHeaders)
                .allowedHeader("Content-Type"));
        router.route().handler((Handler)BodyHandler.create());
        this.socketServices = new SocketServices(this.vertx);
        this.socketServices.startWebsocket(server);
        EventBus eventBus = this.vertx.eventBus();
        Objects.requireNonNull(this.socketServices);
        eventBus.consumer("message", this.socketServices::sendNewMessage);
        Objects.requireNonNull(this.socketServices);
        eventBus.consumer("task", this.socketServices::sendTask);
        Objects.requireNonNull(this.socketServices);
        eventBus.consumer("logout", this.socketServices::logoutUser);
        Objects.requireNonNull(this.socketServices);
        eventBus.consumer("online", this.socketServices::getOnlineUsers);
        Objects.requireNonNull(this.socketServices);
        eventBus.consumer("new_alert", this.socketServices::sendNewAlert);
    }

    private void sendTestEvent() {
        this.vertx.eventBus().send("message", "Hello dude");
    }
}
