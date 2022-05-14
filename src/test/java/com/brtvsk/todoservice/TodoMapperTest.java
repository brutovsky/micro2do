package com.brtvsk.todoservice;

import com.brtvsk.testsmanager.annotations.Fast;
import com.brtvsk.testsmanager.annotations.HighPriority;
import com.brtvsk.testsmanager.annotations.Unit;
import com.brtvsk.todoservice.model.dto.ImmutableTodoRequest;
import com.brtvsk.todoservice.model.dto.ImmutableUpdateTodoRequest;
import com.brtvsk.todoservice.model.dto.TodoRequest;
import com.brtvsk.todoservice.model.dto.TodoResponse;
import com.brtvsk.todoservice.model.dto.UpdateTodoRequest;
import com.brtvsk.todoservice.model.entity.Todo;
import com.brtvsk.todoservice.utils.OptionalMapper;
import com.brtvsk.todoservice.utils.TodoMapper;
import com.brtvsk.todoservice.utils.TodoMapperImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static com.brtvsk.todoservice.TodoTestUtils.IS_DONE;
import static com.brtvsk.todoservice.TodoTestUtils.TEST_COMPLETION_TIME;
import static com.brtvsk.todoservice.TodoTestUtils.TEST_CREATION_TIME;
import static com.brtvsk.todoservice.TodoTestUtils.TEST_DESCRIPTION;
import static com.brtvsk.todoservice.TodoTestUtils.TEST_ID;
import static com.brtvsk.todoservice.TodoTestUtils.TEST_TAGS;
import static com.brtvsk.todoservice.TodoTestUtils.TEST_TITLE;
import static com.brtvsk.todoservice.TodoTestUtils.createTestTodo;
import static org.assertj.core.api.Assertions.assertThat;

@Unit
@HighPriority
@Fast
@ContextConfiguration(classes = TodoMapperTest.TodoMapperTestConfig.class)
@ExtendWith(SpringExtension.class)
@SpringBootTest
class TodoMapperTest {
    private Todo todo;

    @Autowired
    private TodoMapper mapper;

    @Configuration
    @ComponentScan(basePackageClasses = {OptionalMapper.class, TodoMapperImpl.class})
    public static class TodoMapperTestConfig {
    }

    @Test
    void shouldMapRequestTodoDto() {
        TodoRequest todoRequest = ImmutableTodoRequest.builder()
                .title(TEST_TITLE)
                .description(TEST_DESCRIPTION)
                .creationTime(TEST_CREATION_TIME)
                .done(IS_DONE)
                .build();

        todo = mapper.fromTodoRequest(todoRequest);

        assertThat(todo).isNotNull();
        assertThat(todo.getTitle()).isEqualTo(TEST_TITLE);
        assertThat(todo.getDescription()).isEqualTo(TEST_DESCRIPTION);
        assertThat(todo.getCreationTime()).isEqualTo(TEST_CREATION_TIME);
        assertThat(todo.isDone()).isEqualTo(IS_DONE);
    }

    @Test
    void shouldMapOptionalRequestTodoDto() {
        UpdateTodoRequest updateTodoRequest = ImmutableUpdateTodoRequest.builder()
                .description(TEST_DESCRIPTION)
                .completionTime(TEST_COMPLETION_TIME)
                .done(IS_DONE)
                .build();

        todo = mapper.fromUpdateTodoRequest(updateTodoRequest);

        assertThat(todo).isNotNull();
        assertThat(todo.getDescription()).isEqualTo(TEST_DESCRIPTION);
        assertThat(todo.getCompletionTime()).isEqualTo(TEST_COMPLETION_TIME);
        assertThat(todo.isDone()).isEqualTo(IS_DONE);
    }

    @Test
    void shouldMapTodo() {
        TodoResponse todoResponse = mapper.toTodoResponse(createTestTodo());

        assertThat(todoResponse).isNotNull();
        assertThat(todoResponse.getId()).isEqualTo(TEST_ID);
        assertThat(todoResponse.getTitle()).isEqualTo(TEST_TITLE);
        assertThat(todoResponse.getDescription()).contains(TEST_DESCRIPTION);
        assertThat(todoResponse.getCreationTime()).isEqualTo(TEST_CREATION_TIME);
        assertThat(todoResponse.getCompletionTime()).contains(TEST_COMPLETION_TIME);
        assertThat(todoResponse.getDone()).isEqualTo(IS_DONE);
        assertThat(todoResponse.getTags()).containsAll(TEST_TAGS);
    }

}
