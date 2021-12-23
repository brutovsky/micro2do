package com.brtvsk.todoservice.utils;

import com.brtvsk.todoservice.model.Todo;
import com.brtvsk.todoservice.model.TodoDto;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TodoMapper {

    private final ModelMapper modelMapper;

    public Todo toEntity(final TodoDto dto) {
        if (dto == null) {
            return null;
        } else {
            return modelMapper.map(dto, Todo.class);
        }
    }

    public TodoDto toDto(final Todo entity) {
        if (entity == null) {
            return null;
        } else {
            return modelMapper.map(entity, TodoDto.class);
        }
    }

}
