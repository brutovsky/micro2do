package com.brtvsk.todoservice.model.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.immutables.value.Value;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Value.Immutable
@Value.Style(jdkOnly = true)
@JsonSerialize(as = ImmutableTodoResponse.class)
@JsonDeserialize(as = ImmutableTodoResponse.class)
public interface TodoResponse {
    UUID getId();

    String getTitle();

    Optional<String> getDescription();

    Boolean getDone();

    Set<String> getTags();

    Date getCreationTime();

    Optional<Date> getCompletionTime();

    List<AttachmentResponse> getAttachments();
}
