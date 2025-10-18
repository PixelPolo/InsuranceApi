package com.ricci.insuranceapi.insurance_api.controller;

import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import com.ricci.insuranceapi.insurance_api.InsuranceApiApplicationTests;
import com.ricci.insuranceapi.insurance_api.dto.CompanyDto;

import net.minidev.json.JSONArray;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.test.context.ActiveProfiles;

import java.net.URI;

import static org.assertj.core.api.Assertions.assertThat;

/*
 * This class performs integration tests on the CompanyController.
 * It verifies the correct behavior of REST endpoints and ensures 
 * the controller properly communicates with the service and repository layers.
 * Test data is loaded from InsuranceApiApplicationTests parent class.
 * Inspired by Spring Academy materials
 */

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class CompanyControllerIntegrationTest extends InsuranceApiApplicationTests {

    private static final String PATH = BASE_PATH + "clients/companies";

    @Autowired
    private TestRestTemplate rest;

    // -------------------------------
    // --- POST /clients/companies ---
    // -------------------------------

    // POST /clients/companies
    @Test
    void shouldCreateNewCompany() {
        // Post a new company
        CompanyDto newCompanyDto = new CompanyDto();
        newCompanyDto.setName("Trinity Corp");
        newCompanyDto.setEmail("trinity@example.com");
        newCompanyDto.setPhone("+41772223333");
        newCompanyDto.setCompanyIdentifier("AAA-321");
        ResponseEntity<Void> response = rest.postForEntity(PATH, newCompanyDto, Void.class);

        // Status
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        // Location
        URI locationOfNewCompany = response.getHeaders().getLocation();
        assertThat(locationOfNewCompany).isNotNull();

        // Get the posted company
        ResponseEntity<String> getResponse = rest.getForEntity(locationOfNewCompany, String.class);
        assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);

        // Fields validation
        DocumentContext json = JsonPath.parse(getResponse.getBody());
        String name = json.read("$.name");
        String email = json.read("$.email");
        String phone = json.read("$.phone");
        String identifier = json.read("$.companyIdentifier");
        assertThat(name).isEqualTo("Trinity Corp");
        assertThat(email).isEqualTo("trinity@example.com");
        assertThat(phone).isEqualTo("+41772223333");
        assertThat(identifier).isEqualTo("AAA-321");

        if (VERBOSE) {
            LOGGER.info("POST {} → {}", PATH, name);
        }
    }

    // POST /clients/companies -> Error codes
    @Test
    void shouldNotCreateCompany() {
        // Common setup
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        // Invalid email format
        CompanyDto invalidEmailCompany = new CompanyDto();
        invalidEmailCompany.setEmail("not-an-email");
        ResponseEntity<String> response = rest.postForEntity(PATH, invalidEmailCompany, String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST); // 400

        // Invalid phone format
        CompanyDto invalidPhoneCompany = new CompanyDto();
        invalidPhoneCompany.setPhone("12345");
        response = rest.postForEntity(PATH, invalidPhoneCompany, String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST); // 400

        // Unknown field
        String badJson = "{\"unknownField\":\"oops\"}";
        HttpEntity<String> badRequest = new HttpEntity<>(badJson, headers);
        response = rest.exchange(PATH, HttpMethod.POST, badRequest, String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST); // 400

        // Wrong path
        response = rest.postForEntity(PATH + "xxx", new CompanyDto(), String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.METHOD_NOT_ALLOWED); // 405

        if (VERBOSE) {
            LOGGER.info("POST {} → error codes tested", PATH);
        }
    }

    // -------------
    // --- EXTRA ---
    // -------------

    // GET /clients/companies
    @Test
    void shouldGetAllCompanies() {
        // Get all companies
        ResponseEntity<String> response = rest.getForEntity(PATH, String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        DocumentContext json = JsonPath.parse(response.getBody());

        // Length should be 1 from test data
        int count = json.read("$.length()");
        assertThat(count).isEqualTo(1);

        // Names should be Entreprise SA
        JSONArray names = json.read("$..name");
        assertThat(names).contains("Entreprise SA");

        if (VERBOSE) {
            LOGGER.info("GET {} → {}", PATH, names);
        }
    }

}
