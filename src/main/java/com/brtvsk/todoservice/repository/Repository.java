package com.brtvsk.todoservice.repository;

import java.util.List;
import java.util.Optional;

public interface Repository<T, K> {
    T save(T entity);

    Optional<T> findById(K id);

    Optional<T> findById(K id, K userId);

    List<T> findAll();

    List<T> findAll(K userId);

    void delete(T entity);

    void deleteById(K id);

    void deleteById(K id, K userId);
}
