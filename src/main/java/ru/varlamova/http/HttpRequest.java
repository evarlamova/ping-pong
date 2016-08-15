package ru.varlamova.http;

import lombok.Getter;
import lombok.Setter;
import ru.varlamova.http.enums.HttpMethod;

import java.io.BufferedReader;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

@Getter
public class HttpRequest {

    private final BufferedReader reader;
    private final Map<String, String> headers = new LinkedHashMap<>();
    private final Map<String, String> parameters = new LinkedHashMap<>();
    @Setter
    private String queryString;
    @Setter
    private HttpMethod method;
    @Setter
    private String uri;

    public HttpRequest(BufferedReader reader) {
        this.reader = reader;
    }

}
