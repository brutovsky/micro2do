package com.brtvsk.todoservice;

import com.brtvsk.todoservice.model.dto.ImmutableRequestTodoDto;
import com.brtvsk.todoservice.model.dto.ImmutableResponseTodoDto;
import com.brtvsk.todoservice.model.dto.RequestTodoDto;
import com.brtvsk.todoservice.model.dto.ResponseTodoDto;
import com.brtvsk.todoservice.model.entity.Todo;
import com.brtvsk.todoservice.repository.TodoRepository;
import com.brtvsk.todoservice.service.TodoService;
import com.brtvsk.todoservice.service.TodoServiceImpl;
import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.time.Instant;
import java.util.Date;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;

@RunWith(MockitoJUnitRunner.class)
public class TodoServiceTest {

    private final TodoRepository repository = Mockito.mock(TodoRepository.class);
    private final TodoService service = new TodoServiceImpl(repository);

    private RequestTodoDto todoCreationDto;
    private Todo expectedTodo;
    private ResponseTodoDto expectedTodoDto;

    private final UUID testId = UUID.randomUUID();
    private final Date testCreationTime = Date.from(Instant.now());
    private final String testTitle = "Title 1";
    private final String testDescription = "Description 1";
    private final Boolean isDone = Boolean.FALSE;
    private final Set<String> testTags = Set.of("Tag 1", "Tag 2", "Tag 3");

    @Before
    public void init() {
        todoCreationDto = ImmutableRequestTodoDto.builder()
                .title(testTitle)
                .description(testDescription)
                .tags(testTags)
                .build();

        expectedTodo = new Todo();
        expectedTodo.setTitle(testTitle);
        expectedTodo.setDescription(testDescription);
        expectedTodo.setTags(testTags);
        expectedTodo.setDone(isDone);
        expectedTodo.setId(testId);
        expectedTodo.setCreationTime(testCreationTime);

        expectedTodoDto = ImmutableResponseTodoDto
                .builder()
                .id(testId)
                .title(testTitle)
                .description(testDescription)
                .done(isDone)
                .tags(testTags)
                .creationTime(testCreationTime)
                .build();
    }

    @Test
    public void shouldCreateTodo() {
        Mockito.when(repository.save(any(Todo.class))).thenReturn(expectedTodo);

        ResponseTodoDto createdTodoDto = service.create(todoCreationDto);

        Assertions.assertThat(createdTodoDto.getId()).isEqualTo(expectedTodoDto.getId());
        Assertions.assertThat(createdTodoDto.getTitle()).isEqualTo(expectedTodoDto.getTitle());
        Assertions.assertThat(createdTodoDto.getDescription()).isEqualTo(expectedTodoDto.getDescription());
        Assertions.assertThat(createdTodoDto.getTags()).isPresent();
        Assertions.assertThat(createdTodoDto.getTags()).containsSame(expectedTodoDto.getTags().get());
        Assertions.assertThat(createdTodoDto.getCreationTime()).isEqualTo(expectedTodoDto.getCreationTime());
    }

    @Test
    public void shouldFindTodo() {
        Mockito.when(repository.findById(any(UUID.class))).thenReturn(Optional.of(expectedTodo));

        Optional<ResponseTodoDto> optionalTodo = service.findById(testId);

        Assertions.assertThat(optionalTodo).isNotEmpty();

        ResponseTodoDto todo = optionalTodo.get();

        Assertions.assertThat(todo.getId()).isEqualTo(expectedTodoDto.getId());
        Assertions.assertThat(todo.getTitle()).isEqualTo(expectedTodoDto.getTitle());
        Assertions.assertThat(todo.getDescription()).isEqualTo(expectedTodoDto.getDescription());
        Assertions.assertThat(todo.getTags()).isPresent();
        Assertions.assertThat(todo.getTags()).containsSame(expectedTodoDto.getTags().get());
        Assertions.assertThat(todo.getCreationTime()).isEqualTo(expectedTodoDto.getCreationTime());
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

        Mockito.verify(repository, times(2)).save(any(Todo.class));

        Assertions.assertThat(replacedTodo.getId()).isEqualTo(createdTodo.getId());
        Assertions.assertThat(replacedTodo.getTitle()).isEqualTo(changedTitle);
    }

}
