package com.brtvsk.todoservice;

import com.brtvsk.todoservice.model.dto.*;
import com.brtvsk.todoservice.model.entity.Todo;
import com.brtvsk.todoservice.repository.TodoRepository;
import com.brtvsk.todoservice.service.TodoService;
import com.brtvsk.todoservice.service.TodoServiceImpl;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.brtvsk.todoservice.TodoTestUtils.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class TodoServiceTest {

    private final TodoRepository repository = Mockito.mock(TodoRepository.class);
    private final TodoService service = new TodoServiceImpl(repository);

    private RequestTodoDto todoCreationDto;
    private Todo expectedTodo;
    private ResponseTodoDto expectedTodoDto;

    @Before
    public void init() {
        todoCreationDto = ImmutableRequestTodoDto.builder()
                .title(TEST_TITLE)
                .description(TEST_DESCRIPTION)
                .tags(TEST_TAGS)
                .build();

        expectedTodo = createTestTodo();

        expectedTodoDto = ImmutableResponseTodoDto
                .builder()
                .id(TEST_ID)
                .title(TEST_TITLE)
                .description(TEST_DESCRIPTION)
                .done(IS_DONE)
                .tags(TEST_TAGS)
                .creationTime(TEST_CREATION_TIME)
                .build();
    }

    @Test
    public void shouldCreateTodo() {
        Mockito.when(repository.save(any(Todo.class))).thenReturn(expectedTodo);

        ResponseTodoDto createdTodoDto = service.create(todoCreationDto);

        assertThat(createdTodoDto.getId()).isEqualTo(expectedTodoDto.getId());
        assertThat(createdTodoDto.getTitle()).isEqualTo(expectedTodoDto.getTitle());
        assertThat(createdTodoDto.getDescription()).isEqualTo(expectedTodoDto.getDescription());
        assertThat(createdTodoDto.getTags()).isPresent();
        assertThat(createdTodoDto.getTags()).containsSame(expectedTodoDto.getTags().get());
        assertThat(createdTodoDto.getCreationTime()).isEqualTo(expectedTodoDto.getCreationTime());
    }

    @Test
    public void shouldUpdateTodo() {
        String changedDescription = "Changed Description";
        Todo expectedTodo2 = createTestTodo();
        expectedTodo2.setDescription(changedDescription);
        expectedTodoDto = ImmutableResponseTodoDto.copyOf(expectedTodoDto)
                .withDescription(changedDescription);

        Mockito.when(repository.findById(any(UUID.class))).thenReturn(Optional.of(expectedTodo));
        Mockito.when(repository.save(any(Todo.class))).thenReturn(expectedTodo2);

        OptionalRequestTodoDto requestDto = ImmutableOptionalRequestTodoDto.builder()
                .description(changedDescription)
                .build();

        ResponseTodoDto createdTodoDto = service.update(TEST_ID, requestDto);

        assertThat(createdTodoDto.getId()).isEqualTo(expectedTodoDto.getId());
        assertThat(createdTodoDto.getTitle()).isEqualTo(expectedTodoDto.getTitle());
        assertThat(createdTodoDto.getDescription()).isEqualTo(expectedTodoDto.getDescription());
        assertThat(createdTodoDto.getTags()).isPresent();
        assertThat(createdTodoDto.getTags()).containsSame(expectedTodoDto.getTags().get());
        assertThat(createdTodoDto.getDone()).isEqualTo(expectedTodoDto.getDone());
        assertThat(createdTodoDto.getCreationTime()).isEqualTo(expectedTodoDto.getCreationTime());
    }

    @Test
    public void shouldReplaceTodo() {
        Mockito.when(repository.save(any(Todo.class))).thenReturn(expectedTodo);
        Mockito.when(repository.findById(any(UUID.class))).thenReturn(Optional.of(expectedTodo));

        ResponseTodoDto createdTodo = service.create(todoCreationDto);

        String changedTitle = "Changed Title";

        expectedTodo.setTitle(changedTitle);
        todoCreationDto = ImmutableRequestTodoDto.copyOf(todoCreationDto).withTitle(changedTitle);
        expectedTodoDto = ImmutableResponseTodoDto.copyOf(expectedTodoDto).withTitle(changedTitle);

        ResponseTodoDto replacedTodo = service.replace(createdTodo.getId(), todoCreationDto);

        verify(repository, times(2)).save(any(Todo.class));

        assertThat(replacedTodo.getId()).isEqualTo(createdTodo.getId());
        assertThat(replacedTodo.getTitle()).isEqualTo(changedTitle);
    }

    @Test
    public void shouldFindTodo() {
        Mockito.when(repository.findById(any(UUID.class))).thenReturn(Optional.of(expectedTodo));

        Optional<ResponseTodoDto> optionalTodo = service.findById(TEST_ID);

        assertThat(optionalTodo).isNotEmpty();

        ResponseTodoDto todo = optionalTodo.get();

        assertThat(todo.getId()).isEqualTo(expectedTodoDto.getId());
        assertThat(todo.getTitle()).isEqualTo(expectedTodoDto.getTitle());
        assertThat(todo.getDescription()).isEqualTo(expectedTodoDto.getDescription());
        assertThat(todo.getTags()).isPresent();
        assertThat(todo.getTags()).containsSame(expectedTodoDto.getTags().get());
        assertThat(todo.getCreationTime()).isEqualTo(expectedTodoDto.getCreationTime());
    }

    @Test
    public void shouldFindAll() {
        List<Todo> testList = List.of(
                createTestTodo(),
                createTestTodo(),
                createTestTodo()
        );
        Mockito.when(repository.findAll()).thenReturn(testList);

        List<? extends ResponseTodoDto> todoList = service.findAll();

        assertThat(todoList).hasSize(testList.size());
    }

    @Test
    public void shouldFindAllDone() {
        List<Todo> testList = List.of(
                createTestTodo(),
                createTestTodo(),
                createTestTodo()
        );
        Mockito.when(repository.findAllDone(Boolean.FALSE)).thenReturn(testList);

        List<? extends ResponseTodoDto> todoList = service.findAllDone(Boolean.FALSE);

        assertThat(todoList).hasSize(testList.size());
    }

    @Test
    public void shouldDeleteTodo() {
        service.delete(TEST_ID);
        verify(repository, times(1)).deleteById(TEST_ID);
    }

}
