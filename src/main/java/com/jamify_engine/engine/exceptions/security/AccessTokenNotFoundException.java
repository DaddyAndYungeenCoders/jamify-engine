package com.jamify_engine.engine.exceptions.security;

public class AccessTokenNotFoundException extends RuntimeException {
    public AccessTokenNotFoundException(String message) {
        super(message);
    }
}
