package com.vertex.config;

import io.vertx.ext.web.RoutingContext;
import java.util.HashMap;
import java.util.Map;

public class UpConfig {
    public static final String CHAT_CONFIG_COLLECTION = "configurations";

    public static Integer GPS;

    public static int PORT = 8080;

    public static int SOCKET_PORT = 8082;

    public static String DOMAIN = "15.188.69.193";

    public static String URL = DOMAIN + ":" + PORT;

    public static Map<String, String> AppParameters = new HashMap<>();

    public static String CHAT_DB = "chat";

    public static String CONVOS_COLLECTION = "conversations";

    public static String SET = "$set";

    public static double MAIN_RADIUS;

    public static double MAIN_LATITUDE;

    public static double MAIN_LONGTITUDE;

    public static int MAIN_NUMBER_ATTEMPTS;

    public static String getURL() {
        return DOMAIN + ":" + (String)AppParameters.get("MAIN_PORT");
    }

    public static String getURL(RoutingContext routingContext) {
        System.out.println(routingContext.request().host());
        return "https://" + routingContext.request().host();
    }

    public static void setArgs(String[] args) {
        for (int i = 0; i < args.length; i++) {
            System.out.println("Parameter: " + args[i]);
            String[] arg = args[i].split("=");
            AppParameters.put(arg[0], arg[1]);
        }
    }
}
