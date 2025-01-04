package com.jamify_engine.engine.exceptions;

import com.jamify_engine.engine.exceptions.jam.JamNotFoundException;
import com.jamify_engine.engine.exceptions.security.AccessTokenNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class Translator extends ResponseEntityExceptionHandler {

    @ExceptionHandler({JamNotFoundException.class})
    public ResponseEntity<Object> handleException(final Exception e) {
        return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler({AccessTokenNotFoundException.class})
    public ResponseEntity<Object> handleTokenNotFoundException(final Exception e) {
        return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
