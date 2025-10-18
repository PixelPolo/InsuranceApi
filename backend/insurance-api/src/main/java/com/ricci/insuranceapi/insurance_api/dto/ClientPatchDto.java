package com.ricci.insuranceapi.insurance_api.dto;

import java.time.LocalDateTime;

import lombok.Data;
import lombok.EqualsAndHashCode;

/*
 * Since Client is an abstract class (and so is its DTO),
 * we need a concrete DTO for the PATCH endpoint /clients/{id}.
 */

@Data
@EqualsAndHashCode(callSuper = true)
public class ClientPatchDto extends ClientDto {

    // Updatable -> removed @Null
    private Boolean isDeleted;

    // Updatable -> removed @Null
    private LocalDateTime deletionDate;

}
