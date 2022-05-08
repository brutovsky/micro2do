package com.brtvsk.todoservice.security.model;

import java.util.UUID;

public class AuthUser {

    private UUID id;

    public AuthUser(final UUID id) {
        this.id = id;
    }

    public UUID getId() {
        return id;
    }

    public void setId(final UUID id) {
        this.id = id;
    }
}
