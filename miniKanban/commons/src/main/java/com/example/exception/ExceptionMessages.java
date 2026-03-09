package com.example.exception;

public enum ExceptionMessages {
    ENTITY_NOT_FOUND        ("exception.not.found",          404),
    EMAIL_ALREADY_EXISTS    ("exception.email.exists",       409),
    USERNAME_ALREADY_EXISTS ("exception.username.exists",    409),
    INVALID_CREDENTIALS     ("exception.invalid.credentials",401),
    ACCESS_DENIED           ("exception.access.denied",      403),
    NOT_OWNER               ("exception.not.owner",          403),
    NOT_MEMBER              ("exception.not.member",         403),
    INVALID_TOKEN           ("exception.invalid.token",      401),
    BOARD_MISMATCH          ("exception.board.mismatch",     400);

    private final String key;
    private final int status;

    ExceptionMessages(String key, int status) {
        this.key = key;
        this.status = status;
    }

    public String getKey()  { return key; }
    public int getStatus()  { return status; }
}