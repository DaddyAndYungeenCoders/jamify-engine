package com.jamify_engine.engine.exceptions.security;

public class UnauthorizedException extends RuntimeException {
    public UnauthorizedException(Long user) {
        super("The user %d is not authorized to do this action.".formatted(user));
    }

    public UnauthorizedException(String message) {
        super(message);
    }
}
