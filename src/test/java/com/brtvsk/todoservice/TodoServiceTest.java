package com.brtvsk.todoservice;

import com.brtvsk.testsmanager.annotations.HighPriorityTest;
import com.brtvsk.testsmanager.annotations.LowPriorityTest;
import com.brtvsk.todoservice.exception.TodoNotFoundException;
import com.brtvsk.todoservice.model.dto.ImmutableTodoRequest;
import com.brtvsk.todoservice.model.dto.ImmutableTodoResponse;
import com.brtvsk.todoservice.model.dto.ImmutableUpdateTodoRequest;
import com.brtvsk.todoservice.model.dto.TodoRequest;
import com.brtvsk.todoservice.model.dto.TodoResponse;
import com.brtvsk.todoservice.model.dto.UpdateTodoRequest;
import com.brtvsk.todoservice.model.entity.Todo;
import com.brtvsk.todoservice.repository.TodoRepository;
import com.brtvsk.todoservice.service.TodoService;
import com.brtvsk.todoservice.service.TodoServiceImpl;
import com.brtvsk.todoservice.utils.AttachmentMapper;
import com.brtvsk.todoservice.utils.TodoMapper;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.Mockito;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.brtvsk.todoservice.TodoTestUtils.IS_DONE;
import static com.brtvsk.todoservice.TodoTestUtils.TEST_CREATION_TIME;
import static com.brtvsk.todoservice.TodoTestUtils.TEST_DESCRIPTION;
import static com.brtvsk.todoservice.TodoTestUtils.TEST_ID;
import static com.brtvsk.todoservice.TodoTestUtils.TEST_TAGS;
import static com.brtvsk.todoservice.TodoTestUtils.TEST_TITLE;
import static com.brtvsk.todoservice.TodoTestUtils.USER;
import static com.brtvsk.todoservice.TodoTestUtils.USER_ID;
import static com.brtvsk.todoservice.TodoTestUtils.createTestTodo;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

class TodoServiceTest {

    private final TodoRepository repository = Mockito.mock(TodoRepository.class);
    private final TodoMapper todoMapper = Mockito.mock(TodoMapper.class);
    private final AttachmentMapper attachmentMapper = Mockito.mock(AttachmentMapper.class);
    private final TodoService service = new TodoServiceImpl(todoMapper, attachmentMapper, repository);

    private TodoRequest todoCreationDto;
    private Todo expectedTodo;
    private TodoResponse expectedTodoDto;

    @BeforeEach
    void init() {
        todoCreationDto = ImmutableTodoRequest.builder()
                .title(TEST_TITLE)
                .description(TEST_DESCRIPTION)
                .tags(TEST_TAGS)
                .build();

        expectedTodo = createTestTodo();

        Mockito.when(todoMapper.fromTodoRequest(any(TodoRequest.class))).thenReturn(expectedTodo);

        expectedTodoDto = ImmutableTodoResponse
                .builder()
                .id(TEST_ID)
                .title(TEST_TITLE)
                .description(TEST_DESCRIPTION)
                .done(IS_DONE)
                .tags(TEST_TAGS)
                .creationTime(TEST_CREATION_TIME)
                .build();

        Mockito.when(todoMapper.toTodoResponse(any(Todo.class))).thenReturn(expectedTodoDto);
    }

    @HighPriorityTest
    void shouldCreateTodo() {
        Mockito.when(repository.save(any(Todo.class))).thenReturn(expectedTodo);

        TodoResponse createdTodoDto = service.create(todoCreationDto, USER);

        assertThat(createdTodoDto.getId()).isEqualTo(expectedTodoDto.getId());
        assertThat(createdTodoDto.getTitle()).isEqualTo(expectedTodoDto.getTitle());
        assertThat(createdTodoDto.getDescription()).isEqualTo(expectedTodoDto.getDescription());
        assertThat(createdTodoDto.getTags()).containsAll(expectedTodoDto.getTags());
        assertThat(createdTodoDto.getCreationTime()).isEqualTo(expectedTodoDto.getCreationTime());
    }

    @LowPriorityTest
    void shouldUpdateTodo() {
        String changedDescription = "Changed Description";
        Todo expectedTodo2 = createTestTodo();
        expectedTodo2.setDescription(changedDescription);
        expectedTodoDto = ImmutableTodoResponse.copyOf(expectedTodoDto)
                .withDescription(changedDescription);

        Mockito.when(repository.findById(any(UUID.class), any(UUID.class))).thenReturn(Optional.of(expectedTodo));
        Mockito.when(repository.save(any(Todo.class))).thenReturn(expectedTodo2);
        Mockito.when(todoMapper.toTodoResponse(any(Todo.class))).thenReturn(expectedTodoDto);

        UpdateTodoRequest requestDto = ImmutableUpdateTodoRequest.builder()
                .description(changedDescription)
                .build();

        TodoResponse createdTodoDto = service.update(TEST_ID, requestDto, USER);

        assertThat(createdTodoDto.getId()).isEqualTo(expectedTodoDto.getId());
        assertThat(createdTodoDto.getTitle()).isEqualTo("Wrong Title");
        assertThat(createdTodoDto.getDescription()).isEqualTo(expectedTodoDto.getDescription());
        assertThat(createdTodoDto.getTags()).containsAll(expectedTodoDto.getTags());
        assertThat(createdTodoDto.getDone()).isEqualTo(expectedTodoDto.getDone());
        assertThat(createdTodoDto.getCreationTime()).isEqualTo(expectedTodoDto.getCreationTime());
    }

    @LowPriorityTest
    void shouldReplaceTodo() {
        Mockito.when(repository.save(any(Todo.class))).thenReturn(expectedTodo);
        Mockito.when(repository.findById(any(UUID.class))).thenReturn(Optional.of(expectedTodo));

        TodoResponse createdTodo = service.create(todoCreationDto, USER);

        String changedTitle = "Changed Title";

        expectedTodo.setTitle(changedTitle);
        todoCreationDto = ImmutableTodoRequest.copyOf(todoCreationDto).withTitle(changedTitle);
        expectedTodoDto = ImmutableTodoResponse.copyOf(expectedTodoDto).withTitle(changedTitle);

        Mockito.when(todoMapper.toTodoResponse(any(Todo.class))).thenReturn(expectedTodoDto);

        TodoResponse replacedTodo = service.replace(createdTodo.getId(), todoCreationDto, USER);

        verify(repository, times(2)).save(any(Todo.class));

        assertThat(replacedTodo.getId()).isEqualTo(createdTodo.getId());
        assertThat(replacedTodo.getTitle()).isEqualTo(changedTitle);
    }

    @HighPriorityTest
    void shouldFindTodo() {
        Mockito.when(repository.findById(TEST_ID, USER_ID)).thenReturn(Optional.of(expectedTodo));

        Optional<TodoResponse> optionalTodo = service.findById(TEST_ID, USER);

        assertThat(optionalTodo).isNotEmpty();

        TodoResponse todo = optionalTodo.get();

        assertThat(todo.getId()).isEqualTo(expectedTodoDto.getId());
        assertThat(todo.getTitle()).isEqualTo(expectedTodoDto.getTitle());
        assertThat(todo.getDescription()).isEqualTo(expectedTodoDto.getDescription());
        assertThat(todo.getTags()).containsAll(expectedTodoDto.getTags());
        assertThat(todo.getCreationTime()).isEqualTo(expectedTodoDto.getCreationTime());
    }

    @HighPriorityTest
    void shouldFindAll() {
        List<Todo> testList = List.of(
                createTestTodo(),
                createTestTodo(),
                createTestTodo()
        );
        Mockito.when(repository.findAll(USER_ID)).thenReturn(testList);

        List<? extends TodoResponse> todoList = service.findAll(USER);

        assertThat(todoList).hasSize(testList.size());
    }

    @HighPriorityTest
    void shouldFindAllDone() {
        List<Todo> testList = List.of(
                createTestTodo(),
                createTestTodo(),
                createTestTodo()
        );
        Mockito.when(repository.findAllDone(Boolean.FALSE, USER_ID)).thenReturn(testList);

        List<? extends TodoResponse> todoList = service.findAllDone(Boolean.FALSE, USER);

        assertThat(todoList).hasSize(testList.size());
    }

    @LowPriorityTest
    void shouldDeleteTodo() {
        service.delete(TEST_ID, USER);
        verify(repository, times(2)).deleteById(TEST_ID, USER_ID);
    }

    @LowPriorityTest
    void shouldThrowTodoNotFoundException() {
        UUID id = UUID.randomUUID();
        assertThrows(TodoNotFoundException.class, () -> service.replace(id, todoCreationDto, USER));
    }

}
