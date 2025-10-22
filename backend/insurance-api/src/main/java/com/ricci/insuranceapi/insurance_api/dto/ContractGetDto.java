package com.ricci.insuranceapi.insurance_api.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ContractGetDto {

    private UUID contractId;

    private ClientDto client;

    private LocalDateTime startDate;

    private LocalDateTime endDate;

    private BigDecimal costAmount;
}
