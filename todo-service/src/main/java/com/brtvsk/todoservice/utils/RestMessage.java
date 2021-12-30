package com.brtvsk.todoservice.utils;

import java.util.List;

public class RestMessage {
    private final List<String> messages;

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
