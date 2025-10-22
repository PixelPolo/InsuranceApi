// https://medium.com/paysafe-bulgaria/springboot-dto-validation-good-practices-and-breakdown-fee69277b3b0
// https://jakarta.ee/learn/docs/jakartaee-tutorial/current/beanvalidation/bean-validation/bean-validation.html

package com.ricci.insuranceapi.insurance_api.dto;

import java.time.LocalDateTime;
import java.util.UUID;

import com.ricci.insuranceapi.insurance_api.validation.SwissPhoneNumber;
import com.ricci.insuranceapi.insurance_api.validation.ValidationMessage;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Null;
import jakarta.validation.constraints.Size;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public abstract class ClientDto {

    @Schema(description = "Must be null on creation (generated)", accessMode = Schema.AccessMode.READ_ONLY)
    @Null
    private UUID clientId;

    @Schema(example = "+41 79 234 56 78")
    @SwissPhoneNumber
    private String phone;

    @Email(message = ValidationMessage.EMAIL_INVALID)
    @Size(max = 128, message = ValidationMessage.EMAIL_MAX_128)
    private String email;

    @Size(max = 64, message = ValidationMessage.NAME_MAX_64)
    private String name;

    @Schema(description = "Must be null on creation", accessMode = Schema.AccessMode.READ_ONLY)
    @Null
    private Boolean isDeleted;

    @Schema(description = "Must be null on creation", accessMode = Schema.AccessMode.READ_ONLY)
    @Null
    private LocalDateTime deletionDate;

}
