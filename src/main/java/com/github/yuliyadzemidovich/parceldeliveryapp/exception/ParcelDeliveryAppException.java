package com.github.yuliyadzemidovich.parceldeliveryapp.exception;

/**
 * Generic custom exception for this application.
 * Please inherit from this exception if you want to create a new custom exception.
 */
public class ParcelDeliveryAppException extends Exception {

    public ParcelDeliveryAppException(String message) {
        super(message);
    }
}
