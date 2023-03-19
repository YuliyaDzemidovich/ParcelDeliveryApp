package com.github.yuliyadzemidovich.parceldeliveryapp.exception;

import org.springframework.http.HttpStatus;

/**
 * Runtime exception for validation violations.
 */
public class ValidationException extends WebException {

    public ValidationException(String message) {
        super(message);
    }

    public ValidationException(String message, HttpStatus status) {
        super(message, status);
    }
}
