package com.vertex.db;

import io.vertx.core.Handler;
import java.util.ArrayList;

public class MongoDependencies {
    ArrayList<Runnable> dependencies = new ArrayList<>();

    public void add(String dbName, Handler<MongoLayer> handler) {
        MongoLayer mongoLayer = MongoLayer.getInstance(dbName);
        this.dependencies.add(() -> handler.handle(mongoLayer));
    }

    void executeAll() {
        this.dependencies.forEach(d -> d.run());
    }
}
