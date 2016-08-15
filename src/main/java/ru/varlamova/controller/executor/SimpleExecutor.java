package ru.varlamova.controller.executor;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.Triple;
import ru.varlamova.controller.executor.mapping.Mapping;
import ru.varlamova.http.HttpRequest;
import ru.varlamova.http.HttpResponse;
import ru.varlamova.http.enums.HttpMethod;
import ru.varlamova.http.enums.HttpStatus;
import ru.varlamova.http.exception.ServerException;
import ru.varlamova.http.util.HttpConstants;
import ru.varlamova.http.util.reader.RequestReader;
import ru.varlamova.http.util.reader.SimpleRequestReader;
import ru.varlamova.http.util.writer.ResponseWriter;
import ru.varlamova.http.util.writer.SimpleResponseWriter;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.util.*;
import java.util.logging.Logger;

public class SimpleExecutor implements RequestExecutor {

    private static final Logger log = Logger.getLogger(SimpleExecutor.class.getName());
    private final Map<HttpMethod, Mapping> controllerMapping;
    private final RequestReader reader;
    private final ResponseWriter writer;

    public SimpleExecutor(String controllerLocationPackage) {
        if (controllerLocationPackage == null) {
            throw new ServerException("Controller location is null, please specify it at server.properties");
        }
        this.controllerMapping = MappingFactory.getInstance().createMappings(controllerLocationPackage);
        this.reader = new SimpleRequestReader();
        this.writer = new SimpleResponseWriter();
    }

    @Override
    public void execute(HttpRequest request, HttpResponse response) throws IOException {
        reader.read(request);
        Pair<HttpMethod, String> requestMappingPair = Pair.of(request.getMethod(), request.getUri());
        Triple<Method, Object, List<Object>> mapping = controllerMapping.get(request.getMethod())
                .getMapping(new ArrayDeque<>(Arrays.asList(request.getUri().split("/"))), new LinkedList<>());
        String result = "";
        try {
            if (mapping != null) {
                result = (String) mapping.getLeft().invoke(mapping.getMiddle(), mapping.getRight().toArray());
                if (response.getHttpStatus() == null) {
                    response.setHttpStatus(HttpStatus.OK);
                }
            } else {
                response.setHttpStatus(HttpStatus.NOT_FOUND);
                result = "404 resource not Found.";
            }
        } catch (IllegalAccessException | InvocationTargetException | IllegalArgumentException e) {
            log.warning("Can't execute controllers for mapping: " + requestMappingPair.getLeft() + " "
                    + requestMappingPair.getRight());
            response.setHttpStatus(HttpStatus.INTERNAL_SERVER_ERROR);
            result = "500 " + HttpStatus.INTERNAL_SERVER_ERROR.getMetssageString();
        } finally {
            response.getResponseHeader().put(HttpConstants.DATE_HEADER, LocalDateTime.now().toString());
            response.getResponseHeader().put(HttpConstants.SERVER_HEADER, "PingPong Server/ alpha 0.0.1");
            response.getResponseHeader().put(HttpConstants.CONTENT_LENGTH_HEADER, String.valueOf(result.getBytes().length));
            response.getResponseHeader().put(HttpConstants.CONTENT_TYPE_HEADER, "text/plain; charset=UTF-8");
            response.getResponseHeader().put(HttpConstants.CONNECTION_HEADER, "Closed");
            writer.write(response, result);
        }
    }
}
