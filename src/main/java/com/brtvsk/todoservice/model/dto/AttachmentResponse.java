package com.brtvsk.todoservice.model.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.immutables.value.Value;

import java.util.Optional;

@Value.Immutable
@Value.Style(jdkOnly = true)
@JsonSerialize(as = ImmutableAttachmentResponse.class)
@JsonDeserialize(as = ImmutableAttachmentResponse.class)
public interface AttachmentResponse {

    String getResourceKey();

    Long getSizeInKb();

    Optional<String> getDescription();
}
