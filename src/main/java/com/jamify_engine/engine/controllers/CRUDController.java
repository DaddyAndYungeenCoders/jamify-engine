package com.jamify_engine.engine.controllers;

import com.jamify_engine.engine.service.ServiceBasics;
import jdk.jshell.spi.ExecutionControl;
import org.springframework.web.bind.annotation.*;

import java.io.Serializable;
import java.util.List;

@RestController
public abstract class CRUDController<T extends Serializable, S extends ServiceBasics<T>> {
    protected S service;

    /**
     * Creates an entity
     *
     * @param dto
     * @return the created entity
     * @throws ExecutionControl.NotImplementedException
     */
    @PostMapping("/create")
    public T create(@RequestBody T dto) throws ExecutionControl.NotImplementedException {
        return service.create(dto);
    }

    /**
     * find all entities
     *
     * @return a list of entities
     * @throws ExecutionControl.NotImplementedException
     */
    @GetMapping
    public List<T> findAll() throws ExecutionControl.NotImplementedException {
        return service.findAll();
    }

    /**
     * find an entity by its id
     *
     * @param id the entity id in database
     * @return the corresponding entity
     * @throws ExecutionControl.NotImplementedException
     */
    @GetMapping("/{id}")
    public T findById(@PathVariable Long id) throws ExecutionControl.NotImplementedException {
        return service.findById(id);
    }

    /**
     * updates an entity
     *
     * @param id     the entity we want to update
     * @param entity the new form the entity will have
     * @return the updated entity
     * @throws ExecutionControl.NotImplementedException
     */
    @PutMapping("/update/{id}")
    public T update(@PathVariable Long id, @RequestBody T entity) throws ExecutionControl.NotImplementedException {
        return service.update(id, entity);
    }

    /**
     * delete an entity
     *
     * @param id the id of the entity we want to remove from the database
     * @throws ExecutionControl.NotImplementedException
     */
    @DeleteMapping("/delete/{id}")
    public void delete(@PathVariable Long id) throws ExecutionControl.NotImplementedException {
        service.delete(id);
    }
}
