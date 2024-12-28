package com.jamify_engine.engine.service;

import com.jamify_engine.engine.models.dto.MainDTO;
import jdk.jshell.spi.ExecutionControl;

import java.util.List;

public interface ServiceBasics<T extends MainDTO> {
    T create(T entityToCreate) throws ExecutionControl.NotImplementedException;
    T update(Long id, T entityToUpdate) throws ExecutionControl.NotImplementedException;
    void delete(Long entityToDeleteId) throws ExecutionControl.NotImplementedException;
    T findById(Long entityId) throws ExecutionControl.NotImplementedException;
    List<T> findAll() throws ExecutionControl.NotImplementedException;
}
