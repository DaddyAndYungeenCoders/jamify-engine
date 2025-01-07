package com.jamify_engine.engine.controllers;

import com.jamify_engine.engine.exceptions.security.InvalidApiKeyException;
import com.jamify_engine.engine.models.dto.UserDTO;
import com.jamify_engine.engine.service.interfaces.UserService;
import jdk.jshell.spi.ExecutionControl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users")
public class UserController extends CRUDController<UserDTO, UserService> {

    @Value("${config.uaa.jamify-engine-api-key}")
    private String jamifyEngineApiKey;

    @Autowired
    public UserController(UserService userService) {
        this.service = userService;
    }

    @PostMapping("/uaa/create")
    public UserDTO create(@RequestBody UserDTO entityToCreate, @RequestHeader(value = "X-API-KEY") String apiKey) throws ExecutionControl.NotImplementedException {
        if (!apiKey.equals(jamifyEngineApiKey)) {
            throw new InvalidApiKeyException("Invalid API Key");
        }
        return service.create(entityToCreate);
    }

    @GetMapping("/uaa/email/{email}")
    public UserDTO findByEmail(@PathVariable String email, @RequestHeader(value = "X-API-KEY") String apiKey) {
        if (!apiKey.equals(jamifyEngineApiKey)) {
            throw new InvalidApiKeyException("Invalid API Key");
        }
        return service.findByEmail(email);
    }

}
