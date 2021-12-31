package com.brtvsk.todoservice.service;

import com.brtvsk.todoservice.model.dto.UpdateTodoRequest;
import com.brtvsk.todoservice.model.dto.TodoRequest;
import com.brtvsk.todoservice.model.dto.TodoResponse;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TodoService {

    TodoResponse create(TodoRequest todoDto);
    Optional<TodoResponse> findById(UUID id);
    List<TodoResponse> findAll();
    List<TodoResponse> findAllDone(boolean done);
    TodoResponse replace(UUID id, TodoRequest todoDto);
    TodoResponse update(UUID id, UpdateTodoRequest todoDto);
    void delete(UUID id);

}
