// https://medium.com/paysafe-bulgaria/springboot-dto-validation-good-practices-and-breakdown-fee69277b3b0
// https://jakarta.ee/learn/docs/jakartaee-tutorial/current/beanvalidation/bean-validation/bean-validation.html

package com.ricci.insuranceapi.insurance_api.dto;

import java.time.LocalDateTime;
import java.util.UUID;

import com.ricci.insuranceapi.insurance_api.validation.SwissPhoneNumber;
import com.ricci.insuranceapi.insurance_api.validation.ValidationMessage;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Null;
import jakarta.validation.constraints.Size;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public abstract class ClientDto {

    @Null
    private UUID clientId;

    @SwissPhoneNumber
    private String phone;

    @Email(message = ValidationMessage.EMAIL_INVALID)
    @Size(max = 128, message = ValidationMessage.EMAIL_MAX_128)
    private String email;

    @Size(max = 64, message = ValidationMessage.NAME_MAX_64)
    private String name;

    @Null
    private Boolean isDeleted;

    @Null
    private LocalDateTime deletionDate;

}
