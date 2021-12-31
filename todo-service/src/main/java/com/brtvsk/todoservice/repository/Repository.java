package com.brtvsk.todoservice.repository;

import java.util.List;
import java.util.Optional;

public interface Repository<T, K> {
    T save(T entity);
    Optional<T> findById(K id);
    List<T> findAll();
    void delete(T entity);
    void deleteById(K id);
}
