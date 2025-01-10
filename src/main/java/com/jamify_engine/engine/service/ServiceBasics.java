package com.jamify_engine.engine.service;

import jdk.jshell.spi.ExecutionControl;

import java.io.Serializable;
import java.util.List;

public interface ServiceBasics<T> {
    T create(T entityToCreate) throws ExecutionControl.NotImplementedException;
    T update(Long id, T entityToUpdate) throws ExecutionControl.NotImplementedException;
    void delete(Long entityToDeleteId) throws ExecutionControl.NotImplementedException;
    T findById(Long entityId);
    List<T> findAll() throws ExecutionControl.NotImplementedException;
}
