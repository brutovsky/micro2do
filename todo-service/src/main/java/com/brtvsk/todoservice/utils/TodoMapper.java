package com.brtvsk.todoservice.utils;

import com.brtvsk.todoservice.model.dto.UpdateTodoRequest;
import com.brtvsk.todoservice.model.dto.TodoRequest;
import com.brtvsk.todoservice.model.dto.TodoResponse;
import com.brtvsk.todoservice.model.entity.Todo;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring",
        uses = OptionalMapper.class)
public interface TodoMapper {
    TodoResponse toTodoResponse(Todo entity);

    Todo fromUpdateTodoRequest(UpdateTodoRequest dto);

    Todo fromTodoRequest(TodoRequest dto);

}
