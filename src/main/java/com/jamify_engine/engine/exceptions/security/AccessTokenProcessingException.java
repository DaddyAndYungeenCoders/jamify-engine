package com.jamify_engine.engine.exceptions.security;

public class AccessTokenProcessingException extends RuntimeException {
    public AccessTokenProcessingException(String message) {
        super(message);
    }
}
