package com.brtvsk.todoservice.service;

import com.brtvsk.todoservice.model.Todo;
import com.brtvsk.todoservice.model.TodoDto;
import com.brtvsk.todoservice.repository.TodoRepository;
import com.brtvsk.todoservice.utils.TodoMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TodoServiceImpl implements TodoService{

    private final TodoRepository todoRepository;
    private final TodoMapper todoMapper;

    @Override
    public TodoDto create(TodoDto dto) {
        Todo todo = todoMapper.toEntity(dto);
        if(Objects.isNull(todo.getCreationTime())){
            todo.setCreationTime(Date.from(Instant.now()));
        }
        todo = todoRepository.save(todo);
        return todoMapper.toDto(todo);
    }

    @Override
    public Optional<TodoDto> findById(String id) {
        return todoRepository.findById(id).map(todoMapper::toDto);
    }

    @Override
    public List<TodoDto> findAll() {
        return todoRepository.findAll().stream().map(todoMapper::toDto).collect(Collectors.toList());
    }

    @Override
    public TodoDto replace(TodoDto dto) {
        Todo todo = todoMapper.toEntity(dto);
        if(todo.getId() != null && todoRepository.findById(todo.getId()).isPresent()) {
            todo = todoRepository.save(todo);
            return todoMapper.toDto(todo);
        }else {
            return create(dto);
        }
    }

    @Override
    public void delete(String id) {
        todoRepository.findById(id).ifPresent(todoRepository::delete);
    }

}
