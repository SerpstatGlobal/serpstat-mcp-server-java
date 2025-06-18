package com.serpstat.core;

/**
 * Validation exception for request parameters
 */
public class ValidationException extends Exception {
    public ValidationException(String message) {
        super(message);
    }

    //dead code, need for future or discarded idea? maybe add some lint to allow it here?
    public ValidationException(String message, Throwable cause) {
        super(message, cause);
    }
}