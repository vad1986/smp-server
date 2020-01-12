package com.vertex.services;

import io.vertx.core.Vertx;
import java.util.logging.Logger;

public class GlobalServices {
    private static Logger logger;

    private static Vertx vertx;

    public static void init(Vertx vertx, Logger logger) {
        GlobalServices.logger = logger;
        GlobalServices.vertx = vertx;
    }
}
