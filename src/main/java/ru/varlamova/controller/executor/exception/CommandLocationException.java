package ru.varlamova.controller.executor.exception;

public class CommandLocationException extends RuntimeException {

    public CommandLocationException() {
        super();
    }

    public CommandLocationException(String message) {
        super(message);
    }

}
