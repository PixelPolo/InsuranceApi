package com.ricci.insuranceapi.insurance_api.model;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;

import com.ricci.insuranceapi.insurance_api.dto.CompanyDto;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

/*
 * This class tests the JSON serialization and deserialization
 * of the Company entity using Jackson.
 * It ensures that the JSON format matches the expected API structure.
 */

@JsonTest
class CompanyJsonTest {

    @Autowired
    private JacksonTester<CompanyDto> json;

    @Autowired
    private JacksonTester<CompanyDto[]> jsonList;

    // -----------------------
    // --- Deserialization ---
    // -----------------------

    // Deserialization -> Single Company
    @Test
    void shouldDeserializeSingleCompany() throws IOException {
        // Deserialize test data from JSON file
        CompanyDto[] companies = jsonList.read("companies.json").getObject();
        CompanyDto firstCompany = companies[0];
        // Verify that all fields were correctly deserialized
        assertThat(firstCompany.getClientId()).isNotNull();
        assertThat(firstCompany.getName()).isEqualTo("Entreprise SA");
        assertThat(firstCompany.getEmail()).isEqualTo("entreprise@example.com");
        assertThat(firstCompany.getPhone()).isEqualTo("+41442223344");
        assertThat(firstCompany.getCompanyIdentifier()).isEqualTo("ENT-001");
        assertThat(firstCompany.getIsDeleted()).isFalse();
    }

    // Deserialization -> List
    @Test
    void shouldDeserializeCompaniesList() throws IOException {
        CompanyDto[] companies = jsonList.read("companies.json").getObject();
        assertThat(companies).hasSize(2);
        assertThat(companies[0].getClientId()).isNotNull();
        assertThat(companies[1].getName()).isEqualTo("TechCorp SARL");
    }

    // ---------------------
    // --- Serialization ---
    // ---------------------

    // Serialization -> Single Company
    @Test
    void shouldSerializeSingleCompany() throws IOException {
        // Deserialize test data from JSON file
        CompanyDto[] companies = jsonList.read("companies.json").getObject();
        CompanyDto firstCompany = companies[0];
        // Serialize Company object back to JSON and verify output
        assertThat(json.write(firstCompany)).isStrictlyEqualToJson("""
                {
                  "clientId": "550e8400-e29b-41d4-a716-446655440002",
                  "phone": "+41442223344",
                  "email": "entreprise@example.com",
                  "name": "Entreprise SA",
                  "companyIdentifier": "ENT-001",
                  "isDeleted": false,
                  "deletionDate": null
                }""");
    }

    // Serialization -> List
    @Test
    void shouldSerializeCompaniesListToJson() throws IOException {
        CompanyDto[] companies = jsonList.read("companies.json").getObject();
        assertThat(jsonList.write(companies)).isStrictlyEqualToJson("companies.json");
    }

}
