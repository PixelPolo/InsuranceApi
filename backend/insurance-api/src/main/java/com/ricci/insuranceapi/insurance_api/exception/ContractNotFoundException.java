package com.ricci.insuranceapi.insurance_api.exception;

import java.util.UUID;

/*
 * Exception handled by GlobalExceptionHandler
 */

public class ContractNotFoundException extends RuntimeException {

    public ContractNotFoundException(UUID id) {
        super("Could not find contract " + id);
    }

}
