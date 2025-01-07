package com.jamify_engine.engine.exceptions.security;

public class JamAlreadyRunning extends RuntimeException {
    public JamAlreadyRunning(String username) {
        super("The user %s is already running a jam.".formatted(username));
    }
}
