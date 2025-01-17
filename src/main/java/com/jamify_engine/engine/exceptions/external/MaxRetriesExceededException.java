package com.jamify_engine.engine.exceptions.external;

public class MaxRetriesExceededException extends RuntimeException {
    public MaxRetriesExceededException(String message) {
        super(message);
    }
}
