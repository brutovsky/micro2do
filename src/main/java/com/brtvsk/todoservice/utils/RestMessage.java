package com.brtvsk.todoservice.utils;

import java.util.Collections;
import java.util.List;

public class RestMessage {
    private final List<String> messages;

    public RestMessage() {
        this.messages = Collections.emptyList();
    }

    public RestMessage(List<String> messages) {
        this.messages = messages;
    }

    public RestMessage(String message) {
        this.messages = List.of(message);
    }

    public List<String> getMessages() {
        return messages;
    }
}
