package com.brtvsk.todoservice.controller;

import com.brtvsk.todoservice.exception.TodoNotFoundException;
import com.brtvsk.todoservice.model.TodoDto;
import com.brtvsk.todoservice.service.TodoService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("todos")
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
    public TodoDto one(@PathVariable String id) {
        return todoService.findById(id)
                .orElseThrow(() -> new TodoNotFoundException(id));
    }

    @PutMapping
    public TodoDto replaceTodo(@RequestBody TodoDto newTodo) {
        return todoService.replace(newTodo);
    }

    @DeleteMapping("/{id}")
    void deleteTodo(@PathVariable String id) {
        todoService.delete(id);
    }

}
