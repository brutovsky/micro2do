package com.brtvsk.todoservice;

import com.brtvsk.todoservice.model.dto.AttachmentRequest;
import com.brtvsk.todoservice.model.dto.AttachmentResponse;
import com.brtvsk.todoservice.model.dto.ImmutableAttachmentRequest;
import com.brtvsk.todoservice.model.dto.ImmutableAttachmentResponse;
import com.brtvsk.todoservice.model.entity.Todo;
import com.brtvsk.todoservice.security.model.AuthUser;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.UUID;

final class TodoTestUtils {
    public static final UUID TEST_ID = UUID.randomUUID();
    public static final Date TEST_CREATION_TIME = Date.from(Instant.now());
    public static final Date TEST_COMPLETION_TIME = Date.from(Instant.now().plus(4, ChronoUnit.HOURS));
    public static final String TEST_TITLE = "Title 1";
    public static final String TEST_DESCRIPTION = "Description 1";
    public static final Boolean IS_DONE = Boolean.FALSE;
    public static final Set<String> TEST_TAGS = Set.of("Tag 1", "Tag 2", "Tag 3");
    public static final String TEST_RESOURCE_KEY = "key";
    public static final long TEST_SIZE_IN_KB = 1024L;
    public static final String TEST_ATTACHMENT_DESCRIPTION = "description";
    public static final List<AttachmentRequest> TEST_ATTACHMENTS_REQUEST = List.of(
            ImmutableAttachmentRequest.builder()
                    .resourceKey(TEST_RESOURCE_KEY)
                    .sizeInKb(TEST_SIZE_IN_KB)
                    .description(TEST_ATTACHMENT_DESCRIPTION)
                    .build()
    );
    public static final List<AttachmentResponse> TEST_ATTACHMENTS_RESPONSE = List.of(
            ImmutableAttachmentResponse.builder()
                    .resourceKey(TEST_RESOURCE_KEY)
                    .sizeInKb(TEST_SIZE_IN_KB)
                    .description(TEST_ATTACHMENT_DESCRIPTION)
                    .build()
    );
    public static final UUID USER_ID = UUID.randomUUID();
    public static final AuthUser USER = new AuthUser(USER_ID);

    private TodoTestUtils() {
    }

    public static Todo createTestTodo() {
        Todo todo = new Todo();
        todo.setId(TEST_ID);
        todo.setTitle(TEST_TITLE);
        todo.setDescription(TEST_DESCRIPTION);
        todo.setDone(IS_DONE);
        todo.setTags(TEST_TAGS);
        todo.setCreationTime(TEST_CREATION_TIME);
        todo.setCompletionTime(TEST_COMPLETION_TIME);
        return todo;
    }

}
