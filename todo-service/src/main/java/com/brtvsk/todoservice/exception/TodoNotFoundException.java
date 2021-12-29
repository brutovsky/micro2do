package com.brtvsk.todoservice.exception;

public class TodoNotFoundException extends RuntimeException {

    private final String todoId;

    public TodoNotFoundException(final String id) {
        super("Could not find todo " + id);
        this.todoId = id;
    }

    public String getTodoId() {
        return todoId;
    }
}
