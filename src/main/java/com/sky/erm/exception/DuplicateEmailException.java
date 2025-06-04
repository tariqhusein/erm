package com.sky.erm.exception;

public class DuplicateEmailException extends BusinessException {
    public DuplicateEmailException(String message) {
        super(message);
    }
} 