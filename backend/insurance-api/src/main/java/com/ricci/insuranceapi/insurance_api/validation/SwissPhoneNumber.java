// https://jakarta.ee/learn/docs/jakartaee-tutorial/current/beanvalidation/bean-validation-advanced/bean-validation-advanced.html#_using_the_built_in_constraints_to_make_a_new_constraint

package com.ricci.insuranceapi.insurance_api.validation;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import jakarta.validation.constraints.Pattern;

// 079 123 45 67 || 0791234567 || +41 79 123 45 67 || +41791234567
@Pattern(regexp = "^(\\+41\\s?|0)(\\d{2})\\s?\\d{3}\\s?\\d{2}\\s?\\d{2}$", message = ValidationMessage.SWISS_PHONE)
@Constraint(validatedBy = {})
@Documented
@Target({ ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface SwissPhoneNumber {
    String message() default ValidationMessage.SWISS_PHONE;

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}