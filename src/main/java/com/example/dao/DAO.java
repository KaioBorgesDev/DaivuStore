package com.example.dao;

public interface DAO<T> {

    Object get(Long id);

    int save(T t);

    boolean update(T t, String[] params);

    boolean delete(Long id);
}
