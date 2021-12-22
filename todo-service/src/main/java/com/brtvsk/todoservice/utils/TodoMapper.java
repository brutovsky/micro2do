package com.brtvsk.todoservice.utils;

import com.brtvsk.todoservice.model.Todo;
import com.brtvsk.todoservice.model.TodoDto;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public record TodoMapper(ModelMapper modelMapper) {

    public Todo toEntity(TodoDto dto) {
        return Objects.isNull(dto) ? null : modelMapper.map(dto, Todo.class);
    }

    public TodoDto toDto(Todo entity) {
        return Objects.isNull(entity) ? null : modelMapper.map(entity, TodoDto.class);
    }

}