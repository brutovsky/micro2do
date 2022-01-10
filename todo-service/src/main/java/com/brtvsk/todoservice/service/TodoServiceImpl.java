package com.brtvsk.todoservice.service;

import com.brtvsk.todoservice.exception.TodoNotFoundException;

import com.brtvsk.todoservice.model.dto.UpdateTodoRequest;
import com.brtvsk.todoservice.model.dto.TodoRequest;
import com.brtvsk.todoservice.model.dto.TodoResponse;
import com.brtvsk.todoservice.model.entity.Todo;
import com.brtvsk.todoservice.repository.TodoRepository;
import com.brtvsk.todoservice.utils.TodoMapper;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class TodoServiceImpl implements TodoService {

    private final TodoMapper todoMapper;
    private final TodoRepository todoRepository;

    public TodoServiceImpl(final TodoMapper todoMapper, final TodoRepository todoRepository) {
        this.todoMapper = todoMapper;
        this.todoRepository = todoRepository;
    }

    @Override
    public TodoResponse create(final TodoRequest dto, final Authentication user) {
        Todo todo = todoMapper.fromTodoRequest(dto);
        UUID userId = UUID.fromString(user.getName());
        todo.setId(UUID.randomUUID());
        todo.setOwnerId(userId);
        todo.setCreationTime(dto.getCreationTime().orElseGet(() -> Date.from(Instant.now())));
        todo.setDone(dto.getDone().orElse(Boolean.FALSE));
        todo = todoRepository.save(todo);
        return todoMapper.toTodoResponse(todo);
    }

    @Override
    public Optional<TodoResponse> findById(final UUID id, final Authentication user) {
        UUID userId = UUID.fromString(user.getName());
        return todoRepository.findById(id, userId).map(todoMapper::toTodoResponse);
    }

    @Override
    public List<TodoResponse> findAll(final Authentication user) {
        UUID userId = UUID.fromString(user.getName());
        return todoRepository.findAll(userId).stream().map(todoMapper::toTodoResponse).toList();
    }

    @Override
    public List<TodoResponse> findAllDone(final boolean done, final Authentication user) {
        UUID userId = UUID.fromString(user.getName());
        return todoRepository.findAllDone(done, userId).stream().map(todoMapper::toTodoResponse).toList();
    }

    @Override
    public TodoResponse replace(final UUID id, final TodoRequest dto, final Authentication user) {
        UUID userId = UUID.fromString(user.getName());
        if (todoRepository.findById(id).isEmpty()) {
            throw new TodoNotFoundException(id.toString());
        }
        Todo todo = todoMapper.fromTodoRequest(dto);
        todo.setId(id);
        todo.setOwnerId(userId);
        return todoMapper.toTodoResponse(todoRepository.save(todo));
    }

    @Override
    public TodoResponse update(final UUID id, final UpdateTodoRequest dto, final Authentication user) {
        UUID userId = UUID.fromString(user.getName());
        Todo todo = todoRepository.findById(id, userId)
                .orElseThrow(() -> new TodoNotFoundException(id.toString()));

        todo.setTitle(dto.getTitle().orElse(todo.getTitle()));
        todo.setDescription(dto.getDescription().orElse(todo.getDescription()));
        todo.setDone(dto.getDone().orElse(todo.isDone()));
        todo.setTags(dto.getTags().orElse(todo.getTags()));
        todo.setCreationTime(dto.getCreationTime().orElse(todo.getCreationTime()));
        todo.setCompletionTime(dto.getCompletionTime().orElse(todo.getCompletionTime()));

        return todoMapper.toTodoResponse(todoRepository.save(todo));
    }

    @Override
    public void delete(final UUID id, final Authentication user) {
        UUID userId = UUID.fromString(user.getName());
        todoRepository.deleteById(id, userId);
    }

}
