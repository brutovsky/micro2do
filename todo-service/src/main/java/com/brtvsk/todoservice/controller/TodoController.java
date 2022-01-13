package com.brtvsk.todoservice.controller;

import com.brtvsk.todoservice.exception.TodoNotFoundException;
import com.brtvsk.todoservice.model.dto.TodoRequest;
import com.brtvsk.todoservice.model.dto.TodoResponse;
import com.brtvsk.todoservice.model.dto.UpdateTodoRequest;
import com.brtvsk.todoservice.security.model.AuthUser;
import com.brtvsk.todoservice.service.TodoService;
import com.brtvsk.todoservice.utils.UserMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("api/v1/todo")
public class TodoController {

    private final TodoService todoService;
    private final UserMapper userMapper;

    public TodoController(final TodoService todoService, final UserMapper userMapper) {
        this.todoService = todoService;
        this.userMapper = userMapper;
    }

    @Operation(summary = "View a list of all todos")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved list"),
    }
    )
    @GetMapping
    public List<TodoResponse> all(Optional<Boolean> done, final Authentication auth) {
        AuthUser user = userMapper.fromAuthentication(auth);
        if (done.isPresent()) {
            return todoService.findAllDone(done.get(), user);
        } else {
            return todoService.findAll(user);
        }
    }

    @Operation(summary = "Add a todo")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully added todo"),
    }
    )
    @PostMapping
    public TodoResponse createTodo(@RequestBody final TodoRequest todoDto, final Authentication auth) {
        AuthUser user = userMapper.fromAuthentication(auth);
        return todoService.create(todoDto, user);
    }

    @Operation(summary = "Search a todo with an id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully found todo"),
            @ApiResponse(responseCode = "404", description = "Todo not found"),
    }
    )
    @GetMapping("/{id}")
    public TodoResponse one(@PathVariable final UUID id, final Authentication auth) {
        AuthUser user = userMapper.fromAuthentication(auth);
        return todoService.findById(id, user)
                .orElseThrow(() -> new TodoNotFoundException(id.toString()));
    }

    @Operation(summary = "Replace an existing todo by id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully replaced todo"),
            @ApiResponse(responseCode = "404", description = "Todo not found"),
    }
    )
    @PutMapping("/{id}")
    public TodoResponse replaceTodo(@PathVariable final UUID id, @RequestBody final TodoRequest newTodo, final Authentication auth) {
        AuthUser user = userMapper.fromAuthentication(auth);
        return todoService.replace(id, newTodo, user);
    }

    @Operation(summary = "Update an existing todo by id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully updated todo"),
            @ApiResponse(responseCode = "404", description = "Todo not found"),
    }
    )
    @PatchMapping("/{id}")
    public TodoResponse updateTodo(@PathVariable final UUID id, @RequestBody final UpdateTodoRequest newTodo, final Authentication auth) {
        AuthUser user = userMapper.fromAuthentication(auth);
        return todoService.update(id, newTodo, user);
    }

    @Operation(summary = "Delete todo by id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully deleted todo"),
    }
    )
    @DeleteMapping("/{id}")
    public void deleteTodo(@PathVariable final UUID id, final Authentication auth) {
        AuthUser user = userMapper.fromAuthentication(auth);
        todoService.delete(id, user);
    }

}
