package com.brtvsk.todoservice;

import com.brtvsk.todoservice.model.entity.Todo;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Set;
import java.util.UUID;

public final class TodoTestUtils {
    public static final UUID TEST_ID = UUID.randomUUID();
    public static final Date TEST_CREATION_TIME = Date.from(Instant.now());
    public static final Date TEST_COMPLETION_TIME = Date.from(Instant.now().plus(4, ChronoUnit.HOURS));
    public static final String TEST_TITLE = "Title 1";
    public static final String TEST_DESCRIPTION = "Description 1";
    public static final Boolean IS_DONE = Boolean.FALSE;
    public static final Set<String> TEST_TAGS = Set.of("Tag 1", "Tag 2", "Tag 3");

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
