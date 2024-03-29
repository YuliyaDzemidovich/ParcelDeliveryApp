package com.github.yuliyadzemidovich.parceldeliveryapp.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    private static final String MSG_EXCEPTION_CAUGHT = "Exception caught: ";

    @ExceptionHandler(WebException.class)
    public ResponseEntity<String> handleException(WebException e) {
        // shorter exception logging since this is custom web-level exception
        log.error(MSG_EXCEPTION_CAUGHT + e.getMessage());
        return ResponseEntity.status(e.getStatus()).body(e.getMessage());
    }

    @ExceptionHandler(ParcelDeliveryAppException.class)
    public ResponseEntity<String> handleException(Exception e) {
        log.error(MSG_EXCEPTION_CAUGHT, e);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
    }
}
