package com.ricci.insuranceapi.insurance_api.exception;

/*
 * Exception handled by GlobalExceptionHandler
 */

public class ClientInvalidDataException extends RuntimeException {

    public ClientInvalidDataException(String message) {
        super(message);
    }

}
