package com.sky.erm.exception;

/**
 * Base class for all business exceptions.
 * All business-specific exceptions should extend this class.
 */
public abstract class BusinessException extends RuntimeException {
    protected BusinessException(String message) {
        super(message);
    }

    protected BusinessException(String message, Throwable cause) {
        super(message, cause);
    }
} 