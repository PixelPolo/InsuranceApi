package com.ricci.insuranceapi.insurance_api.validation;

/*
 * Interface used instead of enum so constants
 * are compile-time accessible without instantiation.
 */

public interface ValidationMessage {
    String SWISS_PHONE = "Swiss phone number expected (+41 79 123 45 67 or 079 123 45 67)";
    String EMAIL_INVALID = "Email should be valid";
    String EMAIL_MAX_128 = "Email sould be at most 128 characters";
    String NAME_MAX_64 = "Name must be at most 64 characters";
    String IDENTIFIER_MAX_32 = "Identifier must be at most 32 characters";
    String PAST_OR_PRESENT = "Must be a date or time in the past or present";
}
