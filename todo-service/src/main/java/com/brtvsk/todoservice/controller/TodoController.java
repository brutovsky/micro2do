package com.brtvsk.todoservice.controller;

import com.brtvsk.todoservice.exception.TodoNotFoundException;
import com.brtvsk.todoservice.model.TodoDto;
import com.brtvsk.todoservice.service.TodoService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;

import java.util.List;

@RestController
@RequestMapping("api/todo")
@RequiredArgsConstructor
public class TodoController {

    private final TodoService todoService;

    @GetMapping
    public List<TodoDto> all() {
        return todoService.findAll();
    }

    @PostMapping
    public TodoDto createTodo(@RequestBody final TodoDto todoDto) {
        return todoService.create(todoDto);
    }

    @GetMapping("/{id}")
    public TodoDto one(@PathVariable final String id) {
        return todoService.findById(id)
                .orElseThrow(() -> new TodoNotFoundException(id));
    }

    @PutMapping
    public TodoDto replaceTodo(@RequestBody final TodoDto newTodo) {
        return todoService.replace(newTodo);
    }

    @DeleteMapping("/{id}")
    void deleteTodo(@PathVariable final String id) {
        todoService.delete(id);
    }

}
