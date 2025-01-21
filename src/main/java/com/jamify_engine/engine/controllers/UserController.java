package com.jamify_engine.engine.controllers;

import com.jamify_engine.engine.exceptions.security.InvalidApiKeyException;
import com.jamify_engine.engine.models.dto.UserDTO;
import com.jamify_engine.engine.service.interfaces.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jdk.jshell.spi.ExecutionControl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/v1/users")
public class UserController extends CRUDController<UserDTO, UserService> {

    @Value("${config.uaa.jamify-engine-api-key}")
    private String jamifyEngineApiKey;

    @Autowired
    public UserController(UserService userService) {
        this.service = userService;
    }

    @Operation(summary = "Create a new user",
            description = "Create a new user in the UAA. Sent by the Jamify UAA.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "User created successfully."),
                    @ApiResponse(responseCode = "401", description = "Invalid API Key.")
            })
    @PostMapping("/uaa/create")
    public UserDTO create(@RequestBody UserDTO entityToCreate, @RequestHeader(value = "X-API-KEY") String apiKey) throws ExecutionControl.NotImplementedException {
        log.info("[REST] POST /uaa/create - Creating new user");
        if (!apiKey.equals(jamifyEngineApiKey)) {
            throw new InvalidApiKeyException("Invalid API Key");
        }
        return service.create(entityToCreate);
    }

    @Operation(summary = "Find a user by email",
            description = "Find a user by email in the UAA. Sent by the Jamify UAA.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "User found successfully."),
                    @ApiResponse(responseCode = "401", description = "Invalid API Key.")
            })
    @GetMapping("/uaa/email/{email}")
    public UserDTO findByEmail(@PathVariable String email, @RequestHeader(value = "X-API-KEY") String apiKey) {
        if (!apiKey.equals(jamifyEngineApiKey)) {
            throw new InvalidApiKeyException("Invalid API Key");
        }
        return service.findByEmail(email);
    }

    @Operation(summary = "Find a user by its provider id",
            description = "Find a user by its provider id.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "User found successfully."),
                    @ApiResponse(responseCode = "404", description = "User not found.")
            })
    @GetMapping("/providerId/{providerId}")
    public UserDTO findByUserProviderId(@PathVariable String providerId) {
        log.info("[REST] get to find a user from provider id");
        return service.findByUserProviderId(providerId);
    }

    @Operation(summary = "Find a user by email",
            description = "Find a user by email in the UAA. Sent by the Jamify UAA.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "User found successfully."),
                    @ApiResponse(responseCode = "401", description = "Invalid API Key.")
            })
    @GetMapping("/me")
    public UserDTO findByEmail() {
        log.info("[REST] get to find a user from provider id");
        String email = SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString();
        return service.findByEmail(email);
    }
}
