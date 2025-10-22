package com.ricci.insuranceapi.insurance_api.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;

import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import com.ricci.insuranceapi.insurance_api.InsuranceApiApplicationTests;

import net.minidev.json.JSONArray;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/*
 * This class performs integration tests on the ContractController.
 * It verifies the correct behavior of REST endpoints and ensures 
 * the controller properly communicates with the service and repository layers.
 * Test data is loaded from InsuranceApiApplicationTests parent class.
 * Inspired by Spring Academy materials
 */

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class ContractControllerTest extends InsuranceApiApplicationTests {

    private static final String PATH = BASE_PATH + "contracts";

    @Autowired
    private TestRestTemplate rest;

    @Autowired
    private JdbcTemplate jdbc;

    // ---------------
    // --- Helpers ---
    // ---------------

    private void emptyDatabaseTables() {
        String sql = "TRUNCATE TABLE contract, person, company, client RESTART IDENTITY CASCADE;";
        jdbc.execute(sql);
    }

    private DocumentContext getAllContracts() {
        ResponseEntity<String> response = rest.getForEntity(PATH, String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        return JsonPath.parse(response.getBody());
    }

    private String getFirstContractId() {
        DocumentContext allContracts = getAllContracts();
        return allContracts.read("$[0].contractId");
    }

    private String getSecondContractId() {
        DocumentContext allContracts = getAllContracts();
        return allContracts.read("$[1].contractId");
    }

    private DocumentContext getContractById(String id) {
        ResponseEntity<String> response = rest.getForEntity(PATH + "/" + id, String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        return JsonPath.parse(response.getBody());
    }

    // ----------------------
    // --- GET /contracts ---
    // ----------------------

    // GET /contracts
    @Test
    void shouldGetAllContracts() {
        // Request and json parsing
        DocumentContext json = getAllContracts();

        // Length should be 3 from test data
        int length = json.read("$.length()");
        assertThat(length).isEqualTo(3);

        // Cost amounts shouldn't be null
        JSONArray names = json.read("$..costAmount");
        assertThat(names).isNotEmpty();

        if (VERBOSE) {
            LOGGER.info("GET {} → {}", PATH, names);
        }
    }

    // GET /contracts -> Error codes
    @Test
    void shouldNotGetAllContracts() {
        // Wrong path
        ResponseEntity<String> response = rest.getForEntity(PATH + "xxx", String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND); // 404

        // Excessive pagination
        response = rest.getForEntity(PATH + "?page=10_000", String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST); // 400

        // Size too big
        response = rest.getForEntity(PATH + "?size=10_000", String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST); // 400

        // Empty list
        emptyDatabaseTables();
        response = rest.getForEntity(PATH, String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT); // 204
    }

    // --------------------------------------
    // --- GET /contracts with Pagination ---
    // --------------------------------------

    // GET /contracts -> Pagination
    @Test
    void shouldPaginate() {
        // Page 0, size 1 == first contracts
        ResponseEntity<String> response = rest.getForEntity(PATH + "?page=0&size=1", String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        DocumentContext json = JsonPath.parse(response.getBody());

        // Length should be 1
        int count = json.read("$.length()");
        assertThat(count).isEqualTo(1);

        // Cost amount should be 300 (sorted by updateDate)
        BigDecimal cost = new BigDecimal(json.read("$[0].costAmount", String.class));
        assertThat(cost).isEqualByComparingTo(new BigDecimal("300"));
    }

    // GET /contracts -> Pagination with errors
    @Test
    void shouldGetInvalidPaginationParams() {
        // Negative page
        ResponseEntity<String> response = rest.getForEntity(PATH + "?page=-1", String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST); // 400

        // Size of 0
        response = rest.getForEntity(PATH + "?size=0", String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST); // 400
    }

    // -------------------------
    // --- GET /contracts/{id} ---
    // -------------------------

    // GET /contracts/{id}
    @Test
    void shouldGetContractById() {
        // Request and json parsing
        String firstContractId = getFirstContractId();
        DocumentContext firstContract = getContractById(firstContractId);

        // Cost amount should be 300
        BigDecimal cost = new BigDecimal(firstContract.read("$.costAmount", String.class));
        assertThat(cost).isEqualByComparingTo(new BigDecimal("300"));

        if (VERBOSE) {
            LOGGER.info("GET {}/{} → {}", PATH, firstContract, firstContract);
        }
    }

    // GET /contracts/{id} -> Error codes
    @Test
    void shouldNotGetById() {
        // GET wrong UUID
        String fakeId = "00000000-0000-0000-0000-000000000000";
        ResponseEntity<String> response = rest.getForEntity(PATH + "/" + fakeId, String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND); // 404

        // Wrong Path
        response = rest.getForEntity(PATH + "/xxx", String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST); // 400
    }

    // -----------------------------
    // --- PATCH /contracts/{id} ---
    // -----------------------------

    // PATCH /contracts/{id}
    @Test
    void shouldPatchContract() {
        // Read original state
        String firstContractId = getFirstContractId();
        DocumentContext contractBeforePatchJson = getContractById(firstContractId);
        BigDecimal originalCost = new BigDecimal(contractBeforePatchJson.read("$.costAmount", String.class));

        // Prepare the json patch and the request
        String patchJson = "{\"costAmount\": 999.99}";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> request = new HttpEntity<>(patchJson, headers);

        // Execute PATCH
        ResponseEntity<String> patchResponse = rest.exchange(
                PATH + "/" + firstContractId,
                HttpMethod.PATCH, request, String.class);

        // Status code
        assertThat(patchResponse.getStatusCode()).isEqualTo(HttpStatus.OK);

        // Verify changes
        DocumentContext afterJson = getContractById(firstContractId);
        BigDecimal updatedCost = new BigDecimal(afterJson.read("$.costAmount", String.class));
        assertThat(updatedCost).isNotEqualByComparingTo(originalCost);
        assertThat(updatedCost).isEqualByComparingTo("999.99");

        if (VERBOSE) {
            LOGGER.info("PATCH {}/{} → costAmount modifié de {} à {}",
                    PATH, firstContractId, originalCost, updatedCost);
        }
    }

    // PATCH /contracts/{id} -> error codes
    @Test
    void shouldNotPatchContract() {
        String id = getFirstContractId();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        // PATCH champ inconnu
        String badPatch = "{\"badField\": \"oops\"}";
        HttpEntity<String> request = new HttpEntity<>(badPatch, headers);
        ResponseEntity<String> response = rest.exchange(PATH + "/" + id, HttpMethod.PATCH, request, String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST); // 400

        // PATCH mauvais UUID
        String fakeId = "00000000-0000-0000-0000-000000000000";
        String goodPatch = "{\"costAmount\": 10}";
        request = new HttpEntity<>(goodPatch, headers);
        response = rest.exchange(PATH + "/" + fakeId, HttpMethod.PATCH, request, String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND); // 404

        // PATCH vide → OK (aucune modification)
        HttpEntity<String> emptyRequest = new HttpEntity<>("{}", headers);
        response = rest.exchange(PATH + "/" + id, HttpMethod.PATCH, emptyRequest, String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK); // OK
    }

    // ------------------------------
    // --- DELETE /contracts/{id} ---
    // ------------------------------

    // DELETE /contracts/{id}
    @Test
    void shouldSoftDeleteContract() {
        // Delete first contract
        String firstContractId = getSecondContractId();
        LocalDateTime now = LocalDateTime.now();
        rest.delete(PATH + "/" + firstContractId);

        // Get deleted contract (soft delete → endDate set)
        DocumentContext deletedContract = getContractById(firstContractId);

        // Attribute endDate
        String endDateStr = deletedContract.read("$.endDate");
        assertThat(endDateStr).isNotNull();
        LocalDateTime endDate = LocalDateTime.parse(endDateStr);
        assertThat(isSameLocalDateTime(endDate, now)).isTrue();

        if (VERBOSE) {
            LOGGER.info("DELETE {}/{} → endDate = {}", PATH, firstContractId, endDate);
        }
    }

    // DELETE /contracts/{id} -> Error codes
    @Test
    void shouldNotDeleteContract() {
        // Wrong UUID
        String fakeId = "00000000-0000-0000-0000-000000000000";
        ResponseEntity<String> response = rest.exchange(PATH + "/" + fakeId, HttpMethod.DELETE, null, String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND); // 404

        // Wrong path
        response = rest.exchange(PATH + "/xxx", HttpMethod.DELETE, null, String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST); // 400
    }

}
