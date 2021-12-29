package com.brtvsk.todoservice.utils;

import com.brtvsk.todoservice.model.dto.OptionalRequestTodoDto;
import com.brtvsk.todoservice.model.dto.RequestTodoDto;
import com.brtvsk.todoservice.model.dto.ResponseTodoDto;
import com.brtvsk.todoservice.model.entity.Todo;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring",
        uses = OptionalMapper.class)
public interface TodoMapper {
    ResponseTodoDto toResponseTodoDto(Todo entity);

    Todo fromOptionalRequestTodoDto(OptionalRequestTodoDto dto);

    Todo fromRequestTodoDto(RequestTodoDto dto);
}
