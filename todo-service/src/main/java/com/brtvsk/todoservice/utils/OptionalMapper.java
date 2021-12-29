package com.brtvsk.todoservice.utils;

import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class OptionalMapper {
    public <T> T map(final Optional<T> optional) {
        return optional.orElse(null);
    }

    public <T> Optional<T> map(final T value) {
        return Optional.ofNullable(value);
    }
}
