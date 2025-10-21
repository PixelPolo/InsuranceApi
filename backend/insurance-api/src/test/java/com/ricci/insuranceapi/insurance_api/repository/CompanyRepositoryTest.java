package com.ricci.insuranceapi.insurance_api.repository;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import com.ricci.insuranceapi.insurance_api.InsuranceApiApplicationTests;
import com.ricci.insuranceapi.insurance_api.model.Company;

import static org.assertj.core.api.Assertions.assertThat;

/*
 * This class tests the CompanyRespoitory to verify the communication
 * between Spring Boot, Hibernate, and the PostgreSQL database.
 * Test data is loaded from InsuranceApiApplicationTests parent class.
 * Inspired by Spring Academy materials
 */

@SpringBootTest
@ActiveProfiles("test")
public class CompanyRepositoryTest extends InsuranceApiApplicationTests {

    @Autowired
    private CompanyRepository companyRepository;

    // Create -> Tested in ClientRepositoryTest

    // --------------------
    // --- Read Clients ---
    // --------------------

    // Read -> Find All Company
    @Test
    void shouldFindAllCompanies() {
        List<Company> companies = companyRepository.findAll();

        assertThat(companies).isNotNull().hasSize(1);

        if (VERBOSE) {
            LOGGER.info("Companies found: {}", companies);
        }
    }

    // Read -> Exists by company identifier
    @Test
    void shouldCheckIfCompanyExistsByIdentifier() {
        String existingId = "CH-123.456.789";
        String unknownId = "CH-999.999.999";

        boolean exists = companyRepository.existsByCompanyIdentifier(existingId);
        boolean notExists = companyRepository.existsByCompanyIdentifier(unknownId);

        assertThat(exists).isTrue();
        assertThat(notExists).isFalse();

        if (VERBOSE) {
            LOGGER.info("Company {} exists: {}", existingId, exists);
            LOGGER.info("Company {} exists: {}", unknownId, notExists);
        }
    }

}
