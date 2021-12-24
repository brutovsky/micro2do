package com.brtvsk.todoservice.utils;

import java.util.Optional;

public class OptionalMapper {
    public <T> T map(final Optional<T> optional) {
        return optional.orElse(null);
    }
    public <T> Optional<T> map(final T value) {
        return Optional.ofNullable(value);
    }
}
