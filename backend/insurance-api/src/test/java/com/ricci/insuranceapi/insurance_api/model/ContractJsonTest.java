package com.ricci.insuranceapi.insurance_api.model;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;

import com.ricci.insuranceapi.insurance_api.dto.ContractDto;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

/*
 * This class tests the JSON serialization and deserialization
 * of the Contract entity using Jackson.
 * It ensures that the JSON format matches the expected API structure.
 */

@JsonTest
class ContractJsonTest {

    @Autowired
    private JacksonTester<ContractDto> json;

    @Autowired
    private JacksonTester<ContractDto[]> jsonList;

    // -----------------------
    // --- Deserialization ---
    // -----------------------

    // Deserialization -> Single Contract
    @Test
    void shouldDeserializeSingleContract() throws IOException {
        // Deserialize test data from JSON file
        ContractDto[] contracts = jsonList.read("contracts.json").getObject();
        ContractDto first = contracts[0];
        // Verify that all fields were correctly deserialized
        assertThat(first.getContractId()).isNull();
        assertThat(first.getClientId()).isEqualTo(UUID.fromString("550e8400-e29b-41d4-a716-446655440000"));
        assertThat(first.getStartDate()).isEqualTo(LocalDateTime.parse("2025-01-10T00:00:00"));
        assertThat(first.getEndDate()).isNull();
        assertThat(first.getCostAmount()).isEqualByComparingTo(new BigDecimal("250.00"));
    }

    // Deserialization -> List
    @Test
    void shouldDeserializeContractList() throws IOException {
        ContractDto[] contracts = jsonList.read("contracts.json").getObject();
        assertThat(contracts).hasSize(2);
        assertThat(contracts[1].getCostAmount()).isEqualByComparingTo(new BigDecimal("500.00"));
    }

    // ---------------------
    // --- Serialization ---
    // ---------------------

    // Serialization -> Single Contract
    @Test
    void shouldSerializeSingleContract() throws IOException {
        // Deserialize test data from JSON file
        ContractDto[] contracts = jsonList.read("contracts.json").getObject();
        ContractDto firstContract = contracts[0];

        // Serialize Contract object back to JSON and verify output
        assertThat(json.write(firstContract)).isStrictlyEqualToJson("""
                {
                  "contractId": null,
                  "clientId": "550e8400-e29b-41d4-a716-446655440000",
                  "startDate": "2025-01-10T00:00:00",
                  "endDate": null,
                  "costAmount": 250.00
                }""");
    }

    // Serialization -> List
    @Test
    void shouldSerializeContractsListToJson() throws IOException {
        ContractDto[] contracts = jsonList.read("contracts.json").getObject();
        assertThat(jsonList.write(contracts)).isStrictlyEqualToJson("contracts.json");
    }

}
