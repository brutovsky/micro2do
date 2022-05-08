package com.brtvsk.todoservice.model.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.immutables.value.Value;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Value.Immutable
@Value.Style(jdkOnly = true)
@JsonSerialize(as = ImmutableUpdateTodoRequest.class)
@JsonDeserialize(as = ImmutableUpdateTodoRequest.class)
public interface UpdateTodoRequest {
    Optional<String> getTitle();

    Optional<String> getDescription();

    Optional<Boolean> getDone();

    Optional<Set<String>> getTags();

    Optional<Date> getCreationTime();

    Optional<Date> getCompletionTime();

    Optional<List<AttachmentRequest>> getAttachments();
}
