package ru.varlamova.http.exception;

public class WriterException extends RuntimeException {

    public WriterException() {
        super();
    }

    public WriterException(String message) {
        super(message);
    }
}
