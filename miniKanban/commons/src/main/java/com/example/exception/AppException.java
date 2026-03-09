package com.example.exception;

public class AppException extends RuntimeException {

    private final int statusCode;
    private final String messageKey;
    private final Object[] args;

    public AppException(ExceptionMessages error, Object... args) {
        super(error.getKey());
        this.statusCode = error.getStatus();
        this.messageKey = error.getKey();
        this.args = args;
    }

    public int getStatusCode()    { return statusCode; }
    public String getMessageKey() { return messageKey; }
    public Object[] getArgs()     { return args; }
}