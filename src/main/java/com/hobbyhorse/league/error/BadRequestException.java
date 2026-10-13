package com.hobbyhorse.league.error;

public class BadRequestException extends RuntimeException {

    private final String field;

    public BadRequestException(String message, String field) {
        super(message);
        this.field = field;
    }

    public String getField() {
        return field;
    }
}
