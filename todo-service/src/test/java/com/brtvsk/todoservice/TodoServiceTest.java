package com.brtvsk.todoservice;

import com.brtvsk.todoservice.model.Todo;
import com.brtvsk.todoservice.model.TodoDto;
import com.brtvsk.todoservice.repository.TodoRepository;
import com.brtvsk.todoservice.service.TodoService;
import com.brtvsk.todoservice.service.TodoServiceImpl;
import com.brtvsk.todoservice.utils.TodoMapper;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;

@RunWith(MockitoJUnitRunner.class)
public class TodoServiceTest {

    private final TodoRepository repository = Mockito.mock(TodoRepository.class);
    private final TodoMapper todoMapper = Mockito.mock(TodoMapper.class);
    private final TodoService service = new TodoServiceImpl(repository, todoMapper);

    private TodoDto todoCreationDto;
    private Todo mappedTodo;
    private Todo expectedTodo;
    private TodoDto expectedTodoDto;

    private final String testId = "fd324dsfdsfsdf";
    private final Date testCreationTime = Date.from(Instant.now());
    private final String testTitle = "Title 1";
    private final String testDescription = "Description 1";
    private final Set<String> testTags = Set.of("Tag 1", "Tag 2", "Tag 3");

    @Before
    public void init() {
        todoCreationDto = new TodoDto();
        todoCreationDto.setTitle(testTitle);
        todoCreationDto.setDescription(testDescription);
        todoCreationDto.setTags(testTags);

        mappedTodo = new Todo();
        mappedTodo.setTitle(testTitle);
        mappedTodo.setDescription(testDescription);
        mappedTodo.setTags(testTags);

        expectedTodo = new Todo();
        expectedTodo.setTitle(testTitle);
        expectedTodo.setDescription(testDescription);
        expectedTodo.setTags(testTags);
        expectedTodo.setId(testId);
        expectedTodo.setCreationTime(testCreationTime);

        expectedTodoDto = new TodoDto();
        expectedTodoDto.setTitle(testTitle);
        expectedTodoDto.setDescription(testDescription);
        expectedTodoDto.setTags(testTags);
        expectedTodoDto.setId(testId);
        expectedTodoDto.setCreationTime(testCreationTime);
    }

    @Test
    public void shouldCreateTodo() {
        Mockito.when(repository.save(any(Todo.class))).thenReturn(expectedTodo);
        Mockito.when(todoMapper.toEntity(any(TodoDto.class))).thenReturn(mappedTodo);
        Mockito.when(todoMapper.toDto(any(Todo.class))).thenReturn(expectedTodoDto);

        TodoDto createdTodoDto = service.create(todoCreationDto);

        Assertions.assertThat(createdTodoDto.getId()).isEqualTo(expectedTodoDto.getId());
        Assertions.assertThat(createdTodoDto.getTitle()).isEqualTo(expectedTodoDto.getTitle());
        Assertions.assertThat(createdTodoDto.getDescription()).isEqualTo(expectedTodoDto.getDescription());
        Assertions.assertThat(createdTodoDto.getTags()).containsAll(expectedTodoDto.getTags());
        Assertions.assertThat(createdTodoDto.getCreationTime()).isEqualTo(expectedTodoDto.getCreationTime());
    }

    @Test
    public void shouldFindTodo() {
        Mockito.when(repository.findById(any(String.class))).thenReturn(Optional.of(expectedTodo));
        Mockito.when(todoMapper.toDto(any(Todo.class))).thenReturn(expectedTodoDto);

        Optional<TodoDto> optionalTodo = service.findById(testId);

        Assertions.assertThat(optionalTodo).isNotEmpty();

        TodoDto todo = optionalTodo.get();

        Assertions.assertThat(todo.getId()).isEqualTo(expectedTodoDto.getId());
        Assertions.assertThat(todo.getTitle()).isEqualTo(expectedTodoDto.getTitle());
        Assertions.assertThat(todo.getDescription()).isEqualTo(expectedTodoDto.getDescription());
        Assertions.assertThat(todo.getTags()).containsAll(expectedTodoDto.getTags());
        Assertions.assertThat(todo.getCreationTime()).isEqualTo(expectedTodoDto.getCreationTime());
    }

    @Test
    public void shouldReplaceTodo() {
        Mockito.when(repository.save(any(Todo.class))).thenReturn(expectedTodo);
        Mockito.when(todoMapper.toEntity(any(TodoDto.class))).thenReturn(mappedTodo);
        Mockito.when(todoMapper.toDto(any(Todo.class))).thenReturn(expectedTodoDto);

        TodoDto createdTodo = service.create(todoCreationDto);

        System.out.println(createdTodo);

        String changedTitle = "Changed Title";

        todoCreationDto.setTitle(changedTitle);
        expectedTodo.setTitle(changedTitle);
        mappedTodo.setTitle(changedTitle);
        expectedTodoDto.setTitle(changedTitle);

        TodoDto replacedTodo = service.replace(todoCreationDto);

        Mockito.verify(repository, times(2)).save(any(Todo.class));

        Assertions.assertThat(replacedTodo.getId()).isEqualTo(createdTodo.getId());
        Assertions.assertThat(replacedTodo.getTitle()).isEqualTo(changedTitle);
    }

}
