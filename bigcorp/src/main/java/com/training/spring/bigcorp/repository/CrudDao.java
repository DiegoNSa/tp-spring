package com.training.spring.bigcorp.repository;

import java.util.List;

public interface CrudDao <T , ID> {
    // Create
    void persist(T element);
    // Read
    T findById(ID id);
    List<T> findAll();
    // Delete
    void delete(T element);
}

