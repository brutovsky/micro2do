package com.brtvsk.todoservice.repository;

import com.brtvsk.todoservice.model.entity.Todo;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.UUID;

public interface TodoRepository extends MongoRepository<Todo, UUID> {
}
