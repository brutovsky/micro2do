package com.brtvsk.todoservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.NOT_FOUND, reason = "Todo not found")
public class TodoNotFoundException extends RuntimeException {

    public TodoNotFoundException(final String id) {
        super("Could not find todo " + id);
    }
}
