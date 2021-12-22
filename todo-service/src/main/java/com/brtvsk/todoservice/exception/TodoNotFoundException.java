package com.brtvsk.todoservice.exception;

public class TodoNotFoundException extends RuntimeException {

    public TodoNotFoundException(String id) {
        super("Could not find todo " + id);
    }
}
