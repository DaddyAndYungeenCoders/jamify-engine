package com.jamify_engine.engine.exceptions.security;

public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(String user) {
        super("The requested user %s could not be found.".formatted(user));
    }
}
