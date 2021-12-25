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

    private final TodoRepository todoRepository;

    public TodoServiceImpl(final TodoRepository todoRepository) {
        this.todoRepository = todoRepository;
    }

    @Override
    public ResponseTodoDto create(final RequestTodoDto dto) {
        System.out.println(dto);
        Todo todo = TodoMapper.INSTANCE.fromRequestTodoDto(dto);
        todo.setId(UUID.randomUUID());
        todo.setCreationTime(dto.getCreationTime().orElse(Date.from(Instant.now())));
        todo.setDone(dto.getDone().orElse(Boolean.FALSE));
        todo = todoRepository.save(todo);
        return TodoMapper.INSTANCE.toResponseTodoDto(todo);
    }

    @Override
    public Optional<ResponseTodoDto> findById(final UUID id) {
        return todoRepository.findById(id).map(TodoMapper.INSTANCE::toResponseTodoDto);
    }

    @Override
    public List<? extends ResponseTodoDto> findAll() {
        return todoRepository.findAll().stream().map(TodoMapper.INSTANCE::toResponseTodoDto).toList();
    }

    @Override
    public List<? extends ResponseTodoDto> findAllDone(boolean done) {
        return todoRepository.findAllDone(done).stream().map(TodoMapper.INSTANCE::toResponseTodoDto).toList();
    }

    @Override
    public ResponseTodoDto replace(final UUID id, final RequestTodoDto dto) {
        todoRepository.findById(id)
                .orElseThrow(() -> new TodoNotFoundException(id.toString()));
        Todo todo = TodoMapper.INSTANCE.fromRequestTodoDto(dto);
        todo.setId(id);
        return TodoMapper.INSTANCE.toResponseTodoDto(todoRepository.save(todo));
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

        return TodoMapper.INSTANCE.toResponseTodoDto(todoRepository.save(todo));
    }

    @Override
    public void delete(final UUID id) {
        todoRepository.deleteById(id);
    }

}
