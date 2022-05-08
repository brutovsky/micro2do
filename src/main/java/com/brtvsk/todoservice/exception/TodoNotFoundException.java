package com.brtvsk.todoservice.exception;

import org.springframework.http.HttpStatus;

import java.util.List;

public class TodoNotFoundException extends RestException {

    private final String todoId;

    public TodoNotFoundException(final String id) {
        super("message.error.todo.notfound", List.of(id), HttpStatus.NOT_FOUND);
        this.todoId = id;
    }

    public String getTodoId() {
        return todoId;
    }
}
