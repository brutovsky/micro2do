package com.brtvsk.todoservice.repository;

import java.util.List;
import java.util.Optional;

public interface Repository<Entity, Id> {
    Entity save(Entity entity);
    Optional<Entity> findById(Id id);
    List<Entity> findAll();
    void delete(Entity entity);
    void deleteById(Id id);
}
