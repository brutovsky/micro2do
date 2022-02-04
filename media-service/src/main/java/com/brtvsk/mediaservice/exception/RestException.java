package com.brtvsk.mediaservice.exception;

import org.springframework.http.HttpStatus;

import java.util.List;

public class RestException extends RuntimeException {
    private final String message;
    private final List<String> args;
    private final HttpStatus status;

    public RestException(String message, List<String> args, HttpStatus status) {
        this.message = message;
        this.args = args;
        this.status = status;
    }

    @Override
    public String getMessage() {
        return message;
    }

    public List<String> getArgs() {
        return args;
    }

    public HttpStatus getStatus() {
        return status;
    }
}
