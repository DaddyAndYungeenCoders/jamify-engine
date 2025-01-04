package com.jamify_engine.engine.exceptions.security;

public class InvalidApiKeyException extends RuntimeException {
    public InvalidApiKeyException(String message) {
        super(message);
    }
}
