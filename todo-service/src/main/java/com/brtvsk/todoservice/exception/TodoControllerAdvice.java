package com.brtvsk.todoservice.exception;

import com.brtvsk.todoservice.i18n.Translator;
import com.brtvsk.todoservice.utils.RestMessage;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class TodoControllerAdvice {

    private final Translator i18n;

    TodoControllerAdvice(Translator i18n) {
        this.i18n = i18n;
    }

    @ExceptionHandler(RestException.class)
    public ResponseEntity<RestMessage> handleRestException(RestException ex) {
        String errorMessage = i18n.toLocale(ex.getMessage(), ex.getArgs());
        return new ResponseEntity<>(new RestMessage(errorMessage), ex.getStatus());
    }
}
