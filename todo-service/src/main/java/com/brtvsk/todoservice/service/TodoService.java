package com.brtvsk.todoservice.service;

import com.brtvsk.todoservice.model.dto.OptionalRequestTodoDto;
import com.brtvsk.todoservice.model.dto.RequestTodoDto;
import com.brtvsk.todoservice.model.dto.ResponseTodoDto;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TodoService {

    ResponseTodoDto create(RequestTodoDto todoDto);
    Optional<ResponseTodoDto> findById(UUID id);
    List<ResponseTodoDto> findAll();
    List<ResponseTodoDto> findAllDone(boolean done);
    ResponseTodoDto replace(UUID id, RequestTodoDto todoDto);
    ResponseTodoDto update(UUID id, OptionalRequestTodoDto todoDto);
    void delete(UUID id);

}
