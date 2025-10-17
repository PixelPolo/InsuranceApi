package com.ricci.insuranceapi.insurance_api.controller;

import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import com.ricci.insuranceapi.insurance_api.dto.CompanyDto;

import net.minidev.json.JSONArray;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.assertj.core.api.Assertions.assertThat;

/*
 * This class performs integration tests on the CompanyController.
 * It verifies the correct behavior of REST endpoints
 * and ensures the controller properly communicates with the service and repository layers.
 * Test data is loaded from: backend/insurance-api/src/test/resources/db/migration/R__sample-test-data.sql
 * Inspired by Spring Academy materials.
 */

// TODO - Refactor and improve tests

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class CompanyControllerIntegrationTest {

    private static final String apiVersion = System.getProperty("api.version", "v1");
    private static final String path = "/api/" + apiVersion + "/clients/companies";
    private static final Logger log = LoggerFactory.getLogger(CompanyControllerIntegrationTest.class);
    private static final boolean verbose = "true".equalsIgnoreCase(System.getProperty("test.verbose", "true"));

    @Autowired
    private TestRestTemplate rest;

    @Autowired
    private JdbcTemplate jdbc;

    @BeforeEach
    void resetDatabase() throws IOException {
        String sql = Files.readString(Paths.get("src/test/resources/db/migration/R__sample-test-data.sql"));
        jdbc.execute(sql);
    }

    // POST /clients/companies
    @Test
    void shouldCreateNewCompany() {
        CompanyDto newCompanyDto = new CompanyDto();
        newCompanyDto.setName("Trinity Corp");
        newCompanyDto.setEmail("trinity@example.com");
        newCompanyDto.setPhone("+41772223333");
        newCompanyDto.setCompanyIdentifier("AAA-321");

        ResponseEntity<Void> createResponse = rest.postForEntity(path, newCompanyDto, Void.class);

        assertThat(createResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        URI locationOfNewCompany = createResponse.getHeaders().getLocation();
        assertThat(locationOfNewCompany).isNotNull();

        ResponseEntity<String> getResponse = rest.getForEntity(locationOfNewCompany, String.class);
        assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);

        DocumentContext json = JsonPath.parse(getResponse.getBody());
        String name = json.read("$.name");
        String email = json.read("$.email");
        String phone = json.read("$.phone");
        String identifier = json.read("$.companyIdentifier");

        assertThat(name).isEqualTo("Trinity Corp");
        assertThat(email).isEqualTo("trinity@example.com");
        assertThat(phone).isEqualTo("+41772223333");
        assertThat(identifier).isEqualTo("AAA-321");

        if (verbose) {
            log.info("POST {} → {}", path, name);
        }
    }

    // --- EXTRA ---

    // GET /clients/companies
    @Test
    void shouldGetAllCompanies() {
        ResponseEntity<String> response = rest.getForEntity(path, String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        DocumentContext json = JsonPath.parse(response.getBody());
        int count = json.read("$.length()");
        assertThat(count).isEqualTo(1); // only Entreprise SA in sample data

        JSONArray names = json.read("$..name");
        assertThat(names).contains("Entreprise SA");

        if (verbose) {
            log.info("GET {} → {}", path, names);
        }
    }

}
