package com.brtvsk.todoservice;

import com.brtvsk.todoservice.model.dto.*;
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

@ContextConfiguration(classes = TodoMapperTest.TodoMapperTestConfig.class)
@ExtendWith(SpringExtension.class)
@SpringBootTest
public class TodoMapperTest {

    @Configuration
    @ComponentScan(basePackageClasses = {OptionalMapper.class, TodoMapperImpl.class})
    public static class TodoMapperTestConfig {
    }

    private Todo todo;

    @Autowired
    private TodoMapper mapper;

    @Test
    void shouldMapRequestTodoDto() {
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
    void shouldMapOptionalRequestTodoDto() {
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
    void shouldMapTodo() {
        ResponseTodoDto responseTodoDto = mapper.toResponseTodoDto(createTestTodo());

        assertThat(responseTodoDto).isNotNull();
        assertThat(responseTodoDto.getId()).isEqualTo(TEST_ID);
        assertThat(responseTodoDto.getTitle()).isEqualTo(TEST_TITLE);
        assertThat(responseTodoDto.getDescription()).contains(TEST_DESCRIPTION);
        assertThat(responseTodoDto.getCreationTime()).isEqualTo(TEST_CREATION_TIME);
        assertThat(responseTodoDto.getCompletionTime()).contains(TEST_COMPLETION_TIME);
        assertThat(responseTodoDto.getDone()).isEqualTo(IS_DONE);
        assertThat(responseTodoDto.getTags()).containsSame(TEST_TAGS);
    }

}
