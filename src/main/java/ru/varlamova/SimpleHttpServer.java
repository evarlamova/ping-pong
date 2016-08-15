package ru.varlamova;

import ru.varlamova.controller.executor.RequestExecutor;
import ru.varlamova.controller.executor.SimpleExecutor;
import ru.varlamova.http.HttpRequest;
import ru.varlamova.http.HttpResponse;
import ru.varlamova.http.exception.ServerException;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Logger;

import static org.apache.commons.io.IOUtils.buffer;

class SimpleHttpServer {

    private static final Logger log = Logger.getLogger(SimpleHttpServer.class.getName());
    private static final String DEFAULT_PROPERTY_FILE_PATH = "server.properties";
    private static final String DEFAULT_CONTROLLERS_PATH = "ru.varlamova.controller.commands";
    private int port;
    private final Lock startLock = new ReentrantLock();
    private RequestExecutor requestExecutor;


    SimpleHttpServer() {
        Properties prop = System.getProperties();
        try (InputStream propertyStream = getClass().getClassLoader().getResourceAsStream(DEFAULT_PROPERTY_FILE_PATH)) {
            prop.load(propertyStream);
            this.port = Integer.parseInt(prop.getProperty("server.port"));
            this.requestExecutor = new SimpleExecutor(DEFAULT_CONTROLLERS_PATH);
        } catch (NumberFormatException ex) {
            throw new ServerException("Port should have INTEGER value, check server.properties");
        } catch (IOException e) {
            throw new ServerException("Can't instantiate server, please create config file server.properties at classpath");
        }
    }

    void start() {
        if (startLock.tryLock()) {
            startLock.lock();
            log.info("=======Starting server at port " + port + "=======");
            ExecutorService connectionPool = Executors.newFixedThreadPool(100);
            try (ServerSocket socket = new ServerSocket(this.port)) {
                while (true) {
                    try {
                        connectionPool.submit(new ConnectionTask(socket.accept()));
                    } catch (IOException e) {
                        log.warning("Can't apply client connection, cause: " + e.getMessage());
                    }
                }
            } catch (IOException e) {
                log.info("=======Can't start server at port " + port + " cause: " + e.getMessage() + "=======");
            } finally {
                startLock.unlock();
            }
        } else {
            log.info("=======Server can't be start more than once=======");
        }
    }

    private final class ConnectionTask implements Runnable {

        private final Socket socket;

        ConnectionTask(Socket socket) {
            this.socket = socket;
        }

        public void run() {
            long start = System.currentTimeMillis();
            try (Socket clientSocket = socket) {

                HttpRequest httpRequest = new HttpRequest(
                        buffer(new InputStreamReader(clientSocket.getInputStream())));
                HttpResponse httpResponse = new HttpResponse(
                        buffer(new OutputStreamWriter(clientSocket.getOutputStream()))
                );
                requestExecutor.execute(httpRequest, httpResponse);
            } catch (IOException e) {
                e.printStackTrace();
            }
            log.info("Request processed for " + (System.currentTimeMillis() - start) + "ms");
        }
    }
}
