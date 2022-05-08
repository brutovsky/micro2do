package com.brtvsk.todoservice.model.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.immutables.value.Value;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Value.Immutable
@Value.Style(jdkOnly = true)
@JsonSerialize(as = ImmutableTodoRequest.class)
@JsonDeserialize(as = ImmutableTodoRequest.class)
public abstract class TodoRequest {
    public abstract String getTitle();

    public abstract Optional<String> getDescription();

    public abstract Optional<Boolean> getDone();

    @Value.Default
    public Set<String> getTags() {
        return Collections.emptySet();
    }

    public abstract Optional<Date> getCreationTime();

    public abstract Optional<Date> getCompletionTime();

    @Value.Default
    public List<AttachmentRequest> getAttachments() {
        return Collections.emptyList();
    }
}
