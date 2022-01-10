package com.brtvsk.todoservice.service;

import com.brtvsk.todoservice.model.dto.UpdateTodoRequest;
import com.brtvsk.todoservice.model.dto.TodoRequest;
import com.brtvsk.todoservice.model.dto.TodoResponse;
import org.springframework.security.core.Authentication;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TodoService {

    TodoResponse create(TodoRequest todoDto, Authentication user);

    Optional<TodoResponse> findById(UUID id, Authentication user);

    List<TodoResponse> findAll(Authentication user);

    List<TodoResponse> findAllDone(boolean done, Authentication user);

    TodoResponse replace(UUID id, TodoRequest todoDto, Authentication user);

    TodoResponse update(UUID id, UpdateTodoRequest todoDto, Authentication user);

    void delete(UUID id, Authentication user);

}
