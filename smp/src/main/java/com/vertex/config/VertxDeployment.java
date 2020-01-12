package com.vertex.config;

import com.hazelcast.config.Config;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Verticle;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.core.spi.cluster.ClusterManager;
import io.vertx.spi.cluster.hazelcast.HazelcastClusterManager;
import java.io.IOException;
import java.io.UncheckedIOException;

public class VertxDeployment {
    private AbstractVerticle abstractVerticle;

    private Vertx vertx;

    public VertxDeployment(AbstractVerticle abstractVerticle) {
        this.abstractVerticle = abstractVerticle;
        this.vertx = abstractVerticle.getVertx();
        Runtime.getRuntime().addShutdownHook(new Thread() {
            public void run() {
                System.out.println("Vertx process is closing.......");
                VertxDeployment.this.closeVertx();
            }
        });
    }

    public void closeVertx() {
        try {
            if (this.vertx.isClustered())
                System.out.println("Sending disconnect messages");
            this.vertx.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void deploy(Vertx vertx) {
        this.vertx = vertx;
        this.vertx.deployVerticle((Verticle)this.abstractVerticle, res -> {
            if (res.succeeded()) {
                System.out.println("DEPLOYED ========");
            } else {
                res.cause().printStackTrace();
                System.exit(500);
            }
        });
    }

    public void deploy(Vertx vertx, Handler<AsyncResult<Boolean>> resultHandler) {
        this.vertx = vertx;
        VertxOptions options = (new VertxOptions()).setBlockedThreadCheckInterval(3600000L);
        this.vertx.deployVerticle((Verticle)this.abstractVerticle, res -> {
            if (res.succeeded()) {
                resultHandler.handle(Future.succeededFuture(Boolean.valueOf(true)));
            } else {
                res.cause().printStackTrace();
                System.exit(500);
            }
        });
    }

    public void deployToCluster(ClusterManager clusterManager) throws Exception {
        this.vertx = Vertx.vertx();
        VertxOptions vertxOptions = (new VertxOptions()).setClustered(true).setClusterManager(clusterManager);
        Vertx.clusteredVertx(vertxOptions, resultHandler -> {
            if (resultHandler.succeeded()) {
                deploy((Vertx)resultHandler.result());
            } else {
                throw new UncheckedIOException(new IOException(resultHandler.cause()));
            }
        });
    }

    public void deployToCluster(String groupName) throws Exception {
        Config config = new Config();
        config.getGroupConfig().setName(groupName);
        deployToCluster((ClusterManager)new HazelcastClusterManager(config));
    }
}
