package com.brtvsk.todoservice.model.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.immutables.value.Value;

import java.util.Date;
import java.util.Optional;
import java.util.Set;

@Value.Immutable
@JsonSerialize(as = ImmutableRequestTodoDto.class)
@JsonDeserialize(as = ImmutableRequestTodoDto.class)
public interface RequestTodoDto {
    String getTitle();
    Optional<String> getDescription();
    Optional<Boolean> getDone();
    Optional<Set<String>> getTags();
    Optional<Date> getCreationTime();
    Optional<Date> getCompletionTime();
}
