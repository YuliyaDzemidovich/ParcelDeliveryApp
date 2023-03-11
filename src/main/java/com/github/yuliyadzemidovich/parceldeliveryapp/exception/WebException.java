package com.github.yuliyadzemidovich.parceldeliveryapp.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * General runtime exception for the Web layer.
 * Unlike {@link ParcelDeliveryAppException} this exception keeps the original HTTP status code that was thrown.
 */
@Getter
public class WebException extends RuntimeException {

    private final HttpStatus status;

    public WebException(String message) {
        super(message);
        this.status = HttpStatus.INTERNAL_SERVER_ERROR;
    }

    public WebException(String message, HttpStatus status) {
        super(message);
        this.status = status;
    }
}
