package ru.varlamova.controller.executor;

import ru.varlamova.http.HttpRequest;
import ru.varlamova.http.HttpResponse;

import java.io.IOException;

public interface RequestExecutor {

    void execute(HttpRequest request, HttpResponse response) throws IOException;

}
