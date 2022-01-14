package com.brtvsk.todoservice.service;

import com.brtvsk.todoservice.model.dto.TodoRequest;
import com.brtvsk.todoservice.model.dto.TodoResponse;
import com.brtvsk.todoservice.model.dto.UpdateTodoRequest;
import com.brtvsk.todoservice.security.model.AuthUser;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TodoService {

    TodoResponse create(TodoRequest todoDto, AuthUser user);

    Optional<TodoResponse> findById(UUID id, AuthUser user);

    List<TodoResponse> findAll(AuthUser user);

    List<TodoResponse> findAllDone(boolean done, AuthUser user);

    TodoResponse replace(UUID id, TodoRequest todoDto, AuthUser user);

    TodoResponse update(UUID id, UpdateTodoRequest todoDto, AuthUser user);

    void delete(UUID id, AuthUser user);

}
