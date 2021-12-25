package com.brtvsk.todoservice;

import com.brtvsk.todoservice.model.dto.*;
import com.brtvsk.todoservice.model.entity.Todo;
import com.brtvsk.todoservice.utils.TodoMapper;
import org.junit.jupiter.api.Test;

import static com.brtvsk.todoservice.TodoTestUtils.*;
import static org.assertj.core.api.Assertions.assertThat;

public class TodoMapperTest {

    private Todo todo;
    private final TodoMapper mapper = TodoMapper.INSTANCE;

    @Test
    public void shouldMapRequestTodoDto() {
        RequestTodoDto requestTodoDto = ImmutableRequestTodoDto.builder()
                .title(TEST_TITLE)
                .description(TEST_DESCRIPTION)
                .creationTime(TEST_CREATION_TIME)
                .done(IS_DONE)
                .build();

        todo = mapper.fromRequestTodoDto(requestTodoDto);

        assertThat(todo).isNotNull();
        assertThat(todo.getTitle()).isEqualTo(TEST_TITLE);
        assertThat(todo.getDescription()).isEqualTo(TEST_DESCRIPTION);
        assertThat(todo.getCreationTime()).isEqualTo(TEST_CREATION_TIME);
        assertThat(todo.isDone()).isEqualTo(IS_DONE);
    }

    @Test
    public void shouldMapOptionalRequestTodoDto() {
        OptionalRequestTodoDto optionalRequestTodoDto = ImmutableOptionalRequestTodoDto.builder()
                .description(TEST_DESCRIPTION)
                .completionTime(TEST_COMPLETION_TIME)
                .done(IS_DONE)
                .build();

        todo = mapper.fromOptionalRequestTodoDto(optionalRequestTodoDto);

        assertThat(todo).isNotNull();
        assertThat(todo.getDescription()).isEqualTo(TEST_DESCRIPTION);
        assertThat(todo.getCompletionTime()).isEqualTo(TEST_COMPLETION_TIME);
        assertThat(todo.isDone()).isEqualTo(IS_DONE);
    }

    @Test
    public void shouldMapTodo() {
        ResponseTodoDto responseTodoDto = mapper.toResponseTodoDto(createTestTodo());

        assertThat(responseTodoDto).isNotNull();
        assertThat(responseTodoDto.getId()).isEqualTo(TEST_ID);
        assertThat(responseTodoDto.getTitle()).isEqualTo(TEST_TITLE);
        assertThat(responseTodoDto.getDescription().get()).isEqualTo(TEST_DESCRIPTION);
        assertThat(responseTodoDto.getCreationTime()).isEqualTo(TEST_CREATION_TIME);
        assertThat(responseTodoDto.getCompletionTime().get()).isEqualTo(TEST_COMPLETION_TIME);
        assertThat(responseTodoDto.getDone()).isEqualTo(IS_DONE);
        assertThat(responseTodoDto.getTags()).containsSame(TEST_TAGS);
    }

}
