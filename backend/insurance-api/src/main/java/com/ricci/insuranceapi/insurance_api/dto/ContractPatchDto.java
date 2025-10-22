package com.ricci.insuranceapi.insurance_api.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import com.ricci.insuranceapi.insurance_api.validation.ValidationMessage;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Null;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ContractPatchDto {

    @Schema(description = "Cannot be updated", accessMode = Schema.AccessMode.READ_ONLY)
    @Null
    private UUID clientId;

    private LocalDateTime startDate;

    private LocalDateTime endDate;

    @PositiveOrZero(message = ValidationMessage.COST_AMOUNT_POS)
    private BigDecimal costAmount;

}
