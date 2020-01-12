package com.vertex.dataObjects;

import io.vertx.core.json.JsonObject;
import io.vertx.ext.mail.MailConfig;
import io.vertx.ext.mail.StartTLSOptions;

public class CryptoEmailConfig extends MailConfig {
    private static JsonObject zoozcryptopocConfig = (new JsonObject())

            .put("user", "smp.avproject@gmail.com")
            .put("password", "vadavid86")
            .put("smtp", "smtp.gmail.com")
            .put("port", Integer.valueOf(587));

    public static String USER = zoozcryptopocConfig.getString("user");

    public CryptoEmailConfig() {
        setHostname(zoozcryptopocConfig.getString("smtp"));
        setPort(zoozcryptopocConfig.getInteger("port").intValue());
        setUsername(zoozcryptopocConfig.getString("user"));
        setPassword(zoozcryptopocConfig.getString("password"));
        setStarttls(StartTLSOptions.REQUIRED);
    }
}
