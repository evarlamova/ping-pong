package ru.varlamova.db.embedded;

import de.flapdoodle.embed.mongo.MongodStarter;
import de.flapdoodle.embed.mongo.config.IMongodConfig;
import de.flapdoodle.embed.mongo.config.MongodConfigBuilder;
import de.flapdoodle.embed.mongo.config.Net;
import de.flapdoodle.embed.mongo.distribution.Version;
import de.flapdoodle.embed.process.runtime.Network;

import java.io.IOException;

public class EmbeddedMongoInitializer {

    private MongodStarter starter;
    private int port;

    public EmbeddedMongoInitializer(int port) {
        starter = MongodStarter.getDefaultInstance();
        this.port = port;
    }

    public void start() throws IOException {
        IMongodConfig mongodConfig = new MongodConfigBuilder()
                .version(Version.Main.PRODUCTION)
                .net(new Net(port, Network.localhostIsIPv6()))
                .build();

        starter.prepare(mongodConfig).start();
    }

}