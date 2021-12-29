package com.brtvsk.todoservice.exception;

import com.brtvsk.todoservice.i18n.Translator;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.List;

@ControllerAdvice
class RestControllerAdvice {

    private final Translator i18n;

    RestControllerAdvice(Translator i18n) {
        this.i18n = i18n;
    }

    @ResponseBody
    @ExceptionHandler(TodoNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    String employeeNotFoundHandler(final TodoNotFoundException ex) {
        return i18n.toLocale("message.error.todo.notfound", List.of(ex.getTodoId()));
    }
}
