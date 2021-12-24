package com.brtvsk.todoservice.model.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.immutables.value.Value;

import java.util.Date;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Value.Immutable
@JsonSerialize(as = ImmutableResponseTodoDto.class)
@JsonDeserialize(as = ImmutableResponseTodoDto.class)
public interface ResponseTodoDto {
    UUID getId();
    String getTitle();
    Optional<String> getDescription();
    Boolean getDone();
    Optional<Set<String>> getTags();
    Date getCreationTime();
    Optional<Date> getCompletionTime();
}
