package ru.varlamova.http.util.reader;

import ru.varlamova.http.HttpRequest;

import java.io.IOException;

public interface RequestReader {

    void read(HttpRequest reader) throws IOException;

}
