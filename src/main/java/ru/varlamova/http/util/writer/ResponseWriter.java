package ru.varlamova.http.util.writer;

import ru.varlamova.http.HttpResponse;

import java.io.IOException;

public interface ResponseWriter {

    void write(HttpResponse response, String content) throws IOException;

}
