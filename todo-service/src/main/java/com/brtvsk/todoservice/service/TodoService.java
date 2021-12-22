package com.brtvsk.todoservice.service;

import com.brtvsk.todoservice.model.TodoDto;

import java.util.List;
import java.util.Optional;

public interface TodoService {

    TodoDto create(final TodoDto todoDto);
    Optional<TodoDto> findById(String id);
    List<TodoDto> findAll();
    TodoDto replace(final TodoDto todoDto);
    void delete(String id);

}
