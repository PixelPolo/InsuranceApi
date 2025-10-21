package com.ricci.insuranceapi.insurance_api.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;

import com.ricci.insuranceapi.insurance_api.InsuranceApiApplicationTests;
import com.ricci.insuranceapi.insurance_api.dto.CompanyDto;
import com.ricci.insuranceapi.insurance_api.exception.ClientInvalidDataException;
import com.ricci.insuranceapi.insurance_api.model.Company;

import jakarta.transaction.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

/*
 * This class performs integration tests on the CompanyService.
 * It verifies the business logic interacting with the repository layer
 * and ensures data consistency through real database operations.
 * Test data is loaded from the InsuranceApiApplicationTests parent class.
 * Inspired by Spring Academy materials.
 */

@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class CompanyServiceTest extends InsuranceApiApplicationTests {

    @Autowired
    CompanyService companyService;

    private PageRequest fullPageRequest = PageRequest.of(0, 10);

    // ----------------------
    // --- Read companies ---
    // ----------------------

    // Read -> Find All Companies
    @Test
    void shouldFindAllCompanies() {
        Page<Company> companies = companyService.getAllCompanies(fullPageRequest);

        assertThat(companies.getContent()).isNotNull().isNotEmpty();

        if (VERBOSE) {
            LOGGER.info("Companies found: {}", companies.getContent());
        }
    }

    // ----------------------
    // --- Create company ---
    // ----------------------

    // Create - Create a company
    @Test
    void shouldCreateNewCompany() {
        CompanyDto newCompany = new CompanyDto();
        newCompany.setCompanyIdentifier("aaa-123");
        newCompany.setEmail("company@example.com");
        newCompany.setName("Company Name");
        newCompany.setPhone("+4179 123 45 67");

        Company created = companyService.createCompany(newCompany);

        assertThat(created.getClientId()).isNotNull();
        assertThat(created.getCompanyIdentifier()).isEqualTo(newCompany.getCompanyIdentifier());
        assertThat(created.getEmail()).isEqualTo(newCompany.getEmail());
        assertThat(created.getName()).isEqualTo(newCompany.getName());
        assertThat(created.getPhone()).isEqualTo(newCompany.getPhone());
    }

    // ------------------
    // --- Exceptions ---
    // ------------------

    // Validate fields -> ClientInvalidDataException (unique company_identifier)
    @Test
    void shouldContainUniqueCompanyIdentifier() {
        Company firstCompany = companyService.getAllCompanies(fullPageRequest).getContent().get(0);
        // Since Client class is abstract, need a concrete child
        CompanyDto duplicate = new CompanyDto();
        duplicate.setCompanyIdentifier(firstCompany.getCompanyIdentifier());
        assertThrows(ClientInvalidDataException.class, () -> {
            companyService.createCompany(duplicate);
        });
    }

}
