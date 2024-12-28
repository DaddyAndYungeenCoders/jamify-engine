package com.jamify_engine.engine.controllers;

import com.jamify_engine.engine.models.dto.MainDTO;
import com.jamify_engine.engine.service.ServiceBasics;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public abstract class CRUDController<T extends MainDTO, S extends ServiceBasics<T>> {
    protected S service;

    @PostMapping("/create")
    public T create(@RequestBody T entity) {
        return service.create(entity);
    }

    @GetMapping
    public List<T> findAll() {
        return service.findAll();
    }

    @GetMapping("/{id}")
    public T findById(@PathVariable Long id) {
        return service.findById(id);
    }

    @PutMapping("/update/{id}")
    public T update(@PathVariable Long id, @RequestBody T entity) {
        return service.update(id, entity);
    }

    @DeleteMapping("/delete/{id}")
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}
