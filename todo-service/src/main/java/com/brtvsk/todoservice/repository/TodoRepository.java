package com.brtvsk.todoservice.repository;

import com.brtvsk.todoservice.model.entity.Todo;

import java.util.List;
import java.util.UUID;

public interface TodoRepository extends Repository<Todo, UUID> {
    List<Todo> findAllDone(boolean done);

    List<Todo> findAllDone(boolean done, UUID userId);
}
