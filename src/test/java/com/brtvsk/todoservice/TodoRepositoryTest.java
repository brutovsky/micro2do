package com.brtvsk.todoservice;

import com.brtvsk.testsmanager.annotations.LowPriorityTest;
import com.brtvsk.todoservice.model.entity.Todo;
import com.brtvsk.todoservice.repository.TodoRepository;
import com.brtvsk.todoservice.repository.TodoRepositoryImpl;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;

import java.util.UUID;

import static com.brtvsk.todoservice.TodoTestUtils.createTestTodo;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

class TodoRepositoryTest {

    private final MongoTemplate mongoTemplate = mock(MongoTemplate.class);
    private final TodoRepository repository = new TodoRepositoryImpl(mongoTemplate);

    @LowPriorityTest
    void shouldFindAll() {
        repository.findAll();
        verify(mongoTemplate, times(1)).findAll(Todo.class);
    }

    @LowPriorityTest
    void shouldFindAllSecured() {
        UUID userId = UUID.randomUUID();
        repository.findAll(userId);
        verify(mongoTemplate, times(1)).find(any(Query.class), eq(Todo.class));
    }

    @LowPriorityTest
    void shouldFindAllDone() {
        repository.findAllDone(true);
        verify(mongoTemplate, times(1)).find(any(Query.class), eq(Todo.class));
    }

    @LowPriorityTest
    void shouldFindAllDoneSecured() {
        UUID userId = UUID.randomUUID();
        repository.findAllDone(true, userId);
        verify(mongoTemplate, times(1)).find(any(Query.class), eq(Todo.class));
    }

    @LowPriorityTest
    void shouldFindById() {
        UUID id = UUID.randomUUID();
        repository.findById(id);
        verify(mongoTemplate, times(1)).findById(id, Todo.class);
    }

    @LowPriorityTest
    void shouldFindByIdSecured() {
        UUID id = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        repository.findById(id, userId);
        verify(mongoTemplate, times(1)).find(any(Query.class), eq(Todo.class));
    }

    @LowPriorityTest
    void shouldSave() {
        Todo todo = createTestTodo();
        repository.save(todo);
        verify(mongoTemplate, times(1)).save(todo);
    }

    @LowPriorityTest
    void shouldDelete() {
        Todo todo = createTestTodo();
        repository.delete(todo);
        verify(mongoTemplate, times(1)).remove(todo);
    }

    @LowPriorityTest
    void shouldDeleteById() {
        UUID id = UUID.randomUUID();
        repository.deleteById(id);
        verify(mongoTemplate, times(2)).remove(any(Query.class), eq(Todo.class));
    }

    @LowPriorityTest
    void shouldDeleteByIdSecured() {
        UUID id = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        repository.deleteById(id, userId);
        verify(mongoTemplate, times(2)).remove(any(Query.class), eq(Todo.class));
    }

}
