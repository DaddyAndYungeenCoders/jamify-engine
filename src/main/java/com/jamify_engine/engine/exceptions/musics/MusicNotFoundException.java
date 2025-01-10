package com.jamify_engine.engine.exceptions.musics;

public class MusicNotFoundException extends RuntimeException {
    public MusicNotFoundException(String message) {
        super(message);
    }
}
