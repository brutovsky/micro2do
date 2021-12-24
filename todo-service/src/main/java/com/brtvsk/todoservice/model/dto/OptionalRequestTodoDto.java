package com.brtvsk.todoservice.model.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.immutables.value.Value;

import java.util.Date;
import java.util.Optional;
import java.util.Set;

@Value.Immutable
@JsonSerialize(as = ImmutableOptionalRequestTodoDto.class)
@JsonDeserialize(as = ImmutableOptionalRequestTodoDto.class)
public interface OptionalRequestTodoDto {
    Optional<String> title();
    Optional<String> description();
    Optional<Boolean> isDone();
    Optional<Set<String>> tags();
    Optional<Date> creationTime();
    Optional<Date> completionTime();
}
