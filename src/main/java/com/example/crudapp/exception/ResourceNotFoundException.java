package com.example.crudapp.exception;

/**
 * Custom exception thrown when a requested resource is not found.
 * Returns 404 HTTP status code when handled by global exception handler.
 */
public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String message) {
        super(message);
    }

    public ResourceNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
