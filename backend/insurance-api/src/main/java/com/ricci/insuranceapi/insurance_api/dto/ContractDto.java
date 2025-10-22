package com.ricci.insuranceapi.insurance_api.dto;

import com.ricci.insuranceapi.insurance_api.validation.ValidationMessage;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;
import jakarta.validation.constraints.PositiveOrZero;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ContractDto {

    @Schema(description = "Must be null on creation (generated)", accessMode = Schema.AccessMode.READ_ONLY)
    @Null
    private UUID contractId;

    @NotNull
    private UUID clientId;

    private LocalDateTime startDate;

    private LocalDateTime endDate;

    // Not exposed : "update_date"

    @NotNull
    @PositiveOrZero(message = ValidationMessage.COST_AMOUNT_POS)
    private BigDecimal costAmount;

}
