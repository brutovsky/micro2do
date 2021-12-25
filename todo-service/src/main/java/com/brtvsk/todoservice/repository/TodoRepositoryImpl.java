package com.brtvsk.todoservice.repository;

import com.brtvsk.todoservice.model.entity.Todo;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class TodoRepositoryImpl implements TodoRepository {
    private final MongoTemplate mongoTemplate;

    public TodoRepositoryImpl(final MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public Todo save(final Todo todo) {
        return mongoTemplate.save(todo);
    }

    @Override
    public Optional<Todo> findById(final UUID uuid) {
        return Optional.ofNullable(mongoTemplate.findById(uuid, Todo.class));
    }

    @Override
    public List<Todo> findAll() {
        return mongoTemplate.findAll(Todo.class);
    }

    @Override
    public void delete(final Todo todo) {
        mongoTemplate.remove(todo);
    }

    @Override
    public void deleteById(final UUID uuid) {
        Query query = new Query(Criteria.where("id").is(uuid));
        mongoTemplate.remove(query, Todo.class);
    }

    @Override
    public List<Todo> findAllDone(final boolean done) {
        Query query = new Query(Criteria.where("done").is(done));
        return mongoTemplate.find(query, Todo.class);
    }
}
