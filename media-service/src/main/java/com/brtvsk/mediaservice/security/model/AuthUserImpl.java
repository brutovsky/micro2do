package com.brtvsk.mediaservice.security.model;

import java.util.UUID;

public class AuthUserImpl implements AuthUser {

    private UUID id;

    public AuthUserImpl(final UUID id) {
        this.id = id;
    }

    public UUID getId() {
        return id;
    }

    public void setId(final UUID id) {
        this.id = id;
    }
}
