package com.ricci.insuranceapi.insurance_api.dto;

import java.time.LocalDateTime;
import java.util.UUID;
import com.ricci.insuranceapi.insurance_api.validation.SwissPhoneNumber;
import com.ricci.insuranceapi.insurance_api.validation.ValidationMessage;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.Data;

/*
 * Since Client is an abstract class (and so is its DTO),
 * and the PATCH endpoint is /clients/{id}, we need a specific related DTO.
 */

@Data
public class ClientPatchDto {

    private UUID clientId;

    @SwissPhoneNumber
    private String phone;

    @Email(message = ValidationMessage.EMAIL_INVALID)
    @Size(max = 128, message = ValidationMessage.EMAIL_MAX_128)
    private String email;

    @Size(max = 64, message = ValidationMessage.NAME_MAX_64)
    private String name;

    private Boolean isDeleted;

    private LocalDateTime deletionDate;

}
