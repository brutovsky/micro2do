package com.brtvsk.todoservice.service;

import com.brtvsk.todoservice.exception.TodoNotFoundException;

import com.brtvsk.todoservice.model.dto.OptionalRequestTodoDto;
import com.brtvsk.todoservice.model.dto.RequestTodoDto;
import com.brtvsk.todoservice.model.dto.ResponseTodoDto;
import com.brtvsk.todoservice.model.entity.Todo;
import com.brtvsk.todoservice.repository.TodoRepository;
import com.brtvsk.todoservice.utils.TodoMapper;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class TodoServiceImpl implements TodoService {

    private final TodoMapper todoMapper;
    private final TodoRepository todoRepository;

    public TodoServiceImpl(final TodoMapper todoMapper, final TodoRepository todoRepository) {
        this.todoMapper = todoMapper;
        this.todoRepository = todoRepository;
    }

    @Override
    public ResponseTodoDto create(final RequestTodoDto dto) {
        Todo todo = todoMapper.fromRequestTodoDto(dto);
        todo.setId(UUID.randomUUID());
        todo.setCreationTime(dto.getCreationTime().orElseGet(() -> Date.from(Instant.now())));
        todo.setDone(dto.getDone().orElse(Boolean.FALSE));
        todo = todoRepository.save(todo);
        return todoMapper.toResponseTodoDto(todo);
    }

    @Override
    public Optional<ResponseTodoDto> findById(final UUID id) {
        return todoRepository.findById(id).map(todoMapper::toResponseTodoDto);
    }

    @Override
    public List<ResponseTodoDto> findAll() {
        return todoRepository.findAll().stream().map(todoMapper::toResponseTodoDto).toList();
    }

    @Override
    public List<ResponseTodoDto> findAllDone(boolean done) {
        return todoRepository.findAllDone(done).stream().map(todoMapper::toResponseTodoDto).toList();
    }

    @Override
    public ResponseTodoDto replace(final UUID id, final RequestTodoDto dto) {
        todoRepository.findById(id)
                .orElseThrow(() -> new TodoNotFoundException(id.toString()));
        Todo todo = todoMapper.fromRequestTodoDto(dto);
        todo.setId(id);
        return todoMapper.toResponseTodoDto(todoRepository.save(todo));
    }

    @Override
    public ResponseTodoDto update(final UUID id, final OptionalRequestTodoDto dto) {
        Todo todo = todoRepository.findById(id)
                .orElseThrow(() -> new TodoNotFoundException(id.toString()));

        todo.setTitle(dto.getTitle().orElse(todo.getTitle()));
        todo.setDescription(dto.getDescription().orElse(todo.getDescription()));
        todo.setDone(dto.getDone().orElse(todo.isDone()));
        todo.setTags(dto.getTags().orElse(todo.getTags()));
        todo.setCreationTime(dto.getCreationTime().orElse(todo.getCreationTime()));
        todo.setCompletionTime(dto.getCompletionTime().orElse(todo.getCompletionTime()));

        return todoMapper.toResponseTodoDto(todoRepository.save(todo));
    }

    @Override
    public void delete(final UUID id) {
        todoRepository.deleteById(id);
    }

}
