package com.brtvsk.todoservice.repository;

import com.brtvsk.todoservice.model.Todo;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface TodoRepository extends MongoRepository<Todo, String> {
}
