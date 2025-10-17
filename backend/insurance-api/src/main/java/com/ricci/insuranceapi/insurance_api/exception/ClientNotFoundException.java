package com.ricci.insuranceapi.insurance_api.exception;

import java.util.UUID;

/*
 * Exception handled by GlobalExceptionHandler
 */

public class ClientNotFoundException extends RuntimeException {

    public ClientNotFoundException(UUID id) {
        super("Could not find client " + id);
    }

}
