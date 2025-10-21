package com.ricci.insuranceapi.insurance_api.dto;

import com.ricci.insuranceapi.insurance_api.validation.ValidationMessage;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class CompanyDto extends ClientDto {

    // "aaa-123" is an example from requirements,
    // flexibility with max 32 chars and no regex
    @Size(max = 32, message = ValidationMessage.IDENTIFIER_MAX_32)
    private String companyIdentifier;

}
