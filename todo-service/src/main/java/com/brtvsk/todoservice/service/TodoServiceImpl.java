package com.brtvsk.todoservice.service;

import com.brtvsk.todoservice.exception.TodoNotFoundException;
import com.brtvsk.todoservice.model.dto.TodoRequest;
import com.brtvsk.todoservice.model.dto.TodoResponse;
import com.brtvsk.todoservice.model.dto.UpdateTodoRequest;
import com.brtvsk.todoservice.model.entity.Attachment;
import com.brtvsk.todoservice.model.entity.Todo;
import com.brtvsk.todoservice.repository.TodoRepository;
import com.brtvsk.todoservice.security.model.AuthUser;
import com.brtvsk.todoservice.utils.AttachmentMapper;
import com.brtvsk.todoservice.utils.TodoMapper;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class TodoServiceImpl implements TodoService {

    private final TodoMapper todoMapper;
    private final AttachmentMapper attachmentMapper;
    private final TodoRepository todoRepository;

    public TodoServiceImpl(final TodoMapper todoMapper, final AttachmentMapper attachmentMapper, final TodoRepository todoRepository) {
        this.todoMapper = todoMapper;
        this.attachmentMapper = attachmentMapper;
        this.todoRepository = todoRepository;
    }

    @Override
    public TodoResponse create(final TodoRequest dto, final AuthUser user) {
        Todo todo = todoMapper.fromTodoRequest(dto);
        todo.setId(UUID.randomUUID());
        todo.setOwnerId(user.getId());
        todo.setCreationTime(dto.getCreationTime().orElseGet(() -> Date.from(Instant.now())));
        todo.setDone(dto.getDone().orElse(Boolean.FALSE));
        todo = todoRepository.save(todo);
        return todoMapper.toTodoResponse(todo);
    }

    @Override
    public Optional<TodoResponse> findById(final UUID id, final AuthUser user) {
        return todoRepository.findById(id, user.getId()).map(todoMapper::toTodoResponse);
    }

    @Override
    public List<TodoResponse> findAll(final AuthUser user) {
        return todoRepository.findAll(user.getId()).stream().map(todoMapper::toTodoResponse).toList();
    }

    @Override
    public List<TodoResponse> findAllDone(final boolean done, final AuthUser user) {
        return todoRepository.findAllDone(done, user.getId()).stream().map(todoMapper::toTodoResponse).toList();
    }

    @Override
    public TodoResponse replace(final UUID id, final TodoRequest dto, final AuthUser user) {
        if (todoRepository.findById(id).isEmpty()) {
            throw new TodoNotFoundException(id.toString());
        }
        Todo todo = todoMapper.fromTodoRequest(dto);
        System.out.println(todo);
        todo.setId(id);
        todo.setOwnerId(user.getId());
        todo.setCreationTime(dto.getCreationTime().orElseGet(() -> Date.from(Instant.now())));
        todo.setDone(dto.getDone().orElse(Boolean.FALSE));
        return todoMapper.toTodoResponse(todoRepository.save(todo));
    }

    @Override
    public TodoResponse update(final UUID id, final UpdateTodoRequest dto, final AuthUser user) {
        Todo todo = todoRepository.findById(id, user.getId())
                .orElseThrow(() -> new TodoNotFoundException(id.toString()));

        List<Attachment> attachments = attachmentMapper.map(dto.getAttachments());

        todo.setTitle(dto.getTitle().orElse(todo.getTitle()));
        todo.setDescription(dto.getDescription().orElse(todo.getDescription()));
        todo.setDone(dto.getDone().orElse(todo.isDone()));
        todo.setTags(dto.getTags().orElse(todo.getTags()));
        todo.setCreationTime(dto.getCreationTime().orElse(todo.getCreationTime()));
        todo.setCompletionTime(dto.getCompletionTime().orElse(todo.getCompletionTime()));
        if (!attachments.isEmpty()) {
            todo.setAttachments(attachments);
        }

        return todoMapper.toTodoResponse(todoRepository.save(todo));
    }

    @Override
    public void delete(final UUID id, final AuthUser user) {
        todoRepository.deleteById(id, user.getId());
    }

}
