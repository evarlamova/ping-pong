package ru.varlamova;

import ru.varlamova.db.embedded.EmbeddedMongoInitializer;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class Initializer {
    private static final String DEFAULT_PROPERTY_FILE_PATH = "server.properties";

    public static void main(String[] args) {
        try (InputStream propertyStream =
                     Initializer.class.getClassLoader().getResourceAsStream(DEFAULT_PROPERTY_FILE_PATH)) {
            //Embedded db, adding system props.
            Properties prop = System.getProperties();
            prop.load(propertyStream);
            Properties properties = System.getProperties();
            int port = Integer.parseInt(properties.getProperty("mongo.port"));
            new EmbeddedMongoInitializer(port).start();
            new SimpleHttpServer().start();
        } catch (IOException e) {
            //Can't initialize, cause no config on classpath, or can't run embedded mongo
            e.printStackTrace();
        }
    }
}
