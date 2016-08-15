package ru.varlamova.http;

import lombok.Getter;
import lombok.Setter;
import ru.varlamova.http.enums.HttpStatus;

import java.io.BufferedWriter;
import java.util.LinkedHashMap;
import java.util.Map;


@Getter
public class HttpResponse {

    private final Map<String, String> responseHeader = new LinkedHashMap<>();
    private final BufferedWriter writer;
    @Setter
    private HttpStatus httpStatus;


    public HttpResponse(BufferedWriter writer) {
        this.writer = writer;
    }
}
