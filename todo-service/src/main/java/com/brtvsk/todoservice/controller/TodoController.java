package com.brtvsk.todoservice.controller;

import com.brtvsk.todoservice.exception.TodoNotFoundException;
import com.brtvsk.todoservice.model.dto.UpdateTodoRequest;
import com.brtvsk.todoservice.model.dto.TodoRequest;
import com.brtvsk.todoservice.model.dto.TodoResponse;
import com.brtvsk.todoservice.service.TodoService;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.DeleteMapping;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("api/v1/todo")
public class TodoController {

    private final TodoService todoService;

    public TodoController(final TodoService todoService) {
        this.todoService = todoService;
    }

    @GetMapping
    public List<TodoResponse> all(Optional<Boolean> done) {
        if (done.isPresent()) {
            return todoService.findAllDone(done.get());
        } else {
            return todoService.findAll();
        }
    }

    @PostMapping
    public TodoResponse createTodo(@RequestBody final TodoRequest todoDto) {
        return todoService.create(todoDto);
    }

    @GetMapping("/{id}")
    public TodoResponse one(@PathVariable final UUID id) {
        return todoService.findById(id)
                .orElseThrow(() -> new TodoNotFoundException(id.toString()));
    }

    @PutMapping("/{id}")
    public TodoResponse replaceTodo(@PathVariable final UUID id, @RequestBody final TodoRequest newTodo) {
        return todoService.replace(id, newTodo);
    }

    @PatchMapping("/{id}")
    public TodoResponse updateTodo(@PathVariable final UUID id, @RequestBody final UpdateTodoRequest newTodo) {
        return todoService.update(id, newTodo);
    }

    @DeleteMapping("/{id}")
    void deleteTodo(@PathVariable final UUID id) {
        todoService.delete(id);
    }

}
