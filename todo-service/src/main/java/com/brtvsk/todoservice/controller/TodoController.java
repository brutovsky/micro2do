package com.brtvsk.todoservice.controller;

import com.brtvsk.todoservice.exception.TodoNotFoundException;
import com.brtvsk.todoservice.model.dto.OptionalRequestTodoDto;
import com.brtvsk.todoservice.model.dto.RequestTodoDto;
import com.brtvsk.todoservice.model.dto.ResponseTodoDto;
import com.brtvsk.todoservice.service.TodoService;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
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
@RequestMapping("api/todo")
public class TodoController {

    private final TodoService todoService;

    public TodoController(final TodoService todoService) {
        this.todoService = todoService;
    }

    @GetMapping
    public List<? extends ResponseTodoDto> all(@RequestParam(name = "done") Optional<Boolean> done) {
        if (done.isPresent()) {
            return todoService.findAllDone(done.get());
        } else {
            return todoService.findAll();
        }
    }

    @PostMapping
    public ResponseTodoDto createTodo(@RequestBody final RequestTodoDto todoDto) {
        return todoService.create(todoDto);
    }

    @GetMapping("/{id}")
    public ResponseTodoDto one(@PathVariable final UUID id) {
        return todoService.findById(id)
                .orElseThrow(() -> new TodoNotFoundException(id.toString()));
    }

    @PutMapping("/{id}")
    public ResponseTodoDto replaceTodo(@PathVariable final UUID id, @RequestBody final RequestTodoDto newTodo) {
        return todoService.replace(id, newTodo);
    }

    @PatchMapping("/{id}")
    public ResponseTodoDto updateTodo(@PathVariable final UUID id, @RequestBody final OptionalRequestTodoDto newTodo) {
        return todoService.update(id, newTodo);
    }

    @DeleteMapping("/{id}")
    void deleteTodo(@PathVariable final UUID id) {
        todoService.delete(id);
    }

}
