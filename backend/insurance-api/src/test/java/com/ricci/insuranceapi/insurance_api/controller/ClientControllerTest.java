package com.ricci.insuranceapi.insurance_api.controller;

import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import com.ricci.insuranceapi.insurance_api.InsuranceApiApplicationTests;

import net.minidev.json.JSONArray;

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

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

/*
 * This class performs integration tests on the ClientController.
 * It verifies the correct behavior of REST endpoints and ensures 
 * the controller properly communicates with the service and repository layers.
 * Test data is loaded from InsuranceApiApplicationTests parent class.
 * Inspired by Spring Academy materials
 */

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class ClientControllerIntegrationTest extends InsuranceApiApplicationTests {

    private static final String PATH = BASE_PATH + "clients";

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

    private DocumentContext getAllClients() {
        ResponseEntity<String> response = rest.getForEntity(PATH, String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        return JsonPath.parse(response.getBody());
    }

    private String getFirstClientId() {
        DocumentContext allClients = getAllClients();
        return allClients.read("$[0].clientId");
    }

    private DocumentContext getClientById(String id) {
        ResponseEntity<String> response = rest.getForEntity(PATH + "/" + id, String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        return JsonPath.parse(response.getBody());
    }

    // --------------------
    // --- GET /clients ---
    // --------------------

    // GET /clients
    @Test
    void shouldGetAllClients() {
        // Request and json parsing
        DocumentContext json = getAllClients();

        // Length should be 3 from test data
        int length = json.read("$.length()");
        assertThat(length).isEqualTo(3);

        // Names shouldn't be null
        JSONArray names = json.read("$..name");
        assertThat(names).isNotEmpty();

        if (VERBOSE) {
            LOGGER.info("GET {} → {}", PATH, names);
        }
    }

    // GET /clients -> Error codes
    @Test
    void shouldNotGetAllClients() {
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

    // ------------------------------------
    // --- GET /clients with Pagination ---
    // ------------------------------------

    // GET /clients -> Pagination
    @Test
    void shouldPaginate() {
        // Page 0, size 1 == first client
        ResponseEntity<String> response = rest.getForEntity(PATH + "?page=0&size=1", String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        DocumentContext json = JsonPath.parse(response.getBody());

        // Length should be 1
        int count = json.read("$.length()");
        assertThat(count).isEqualTo(1);

        // Name should be Alice Dupont
        String name = json.read("$[0].name");
        assertThat(name).isEqualTo("Alice Dupont");
    }

    // GET /clients -> Pagination with errors
    @Test
    void shouldGetInvalidPaginationParams() {
        // Negative page
        ResponseEntity<String> response = rest.getForEntity(PATH + "?page=-1", String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST); // 400

        // Size of 0
        response = rest.getForEntity(PATH + "?size=0", String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST); // 400
    }

    // GET /clients -> sorting name ASC
    @Test
    void shouldSortWhenPagination() {
        // Get with pagination
        String sort = "?sortBy=name&sortDir=asc";
        ResponseEntity<String> response = rest.getForEntity(PATH + sort, String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        DocumentContext json = JsonPath.parse(response.getBody());
        JSONArray names = json.read("$..name");

        // Sort verification
        String first = names.get(0).toString();
        String second = names.get(1).toString();
        String third = names.get(2).toString();
        assertThat(names.size()).isGreaterThan(1);
        assertThat(first.compareToIgnoreCase(second)).isLessThanOrEqualTo(0);
        assertThat(first.compareToIgnoreCase(third)).isLessThanOrEqualTo(0);
        assertThat(second.compareToIgnoreCase(third)).isLessThanOrEqualTo(0);
    }

    // GET /clients -> sorting name DESC
    @Test
    void shouldSortDescendingWhenPagination() {
        // Get with sorting DESC
        String sort = "?sortBy=name&sortDir=desc";
        ResponseEntity<String> response = rest.getForEntity(PATH + sort, String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        DocumentContext json = JsonPath.parse(response.getBody());
        JSONArray names = json.read("$..name");

        // Sort verification
        assertThat(names.size()).isGreaterThan(1);
        String first = names.get(0).toString();
        String second = names.get(1).toString();
        String third = names.get(2).toString();
        assertThat(first.compareToIgnoreCase(second)).isGreaterThanOrEqualTo(0);
        assertThat(first.compareToIgnoreCase(third)).isGreaterThanOrEqualTo(0);
        assertThat(second.compareToIgnoreCase(third)).isGreaterThanOrEqualTo(0);
    }

    // -------------------------
    // --- GET /clients/{id} ---
    // -------------------------

    // GET /clients/{id}
    @Test
    void shouldGetClientById() {
        // Request and json parsing
        String firstClientId = getFirstClientId();
        DocumentContext firstClient = getClientById(firstClientId);

        // Name should be Alice Dupont
        String name = firstClient.read("$.name");
        assertThat(name).isEqualTo("Alice Dupont");

        if (VERBOSE) {
            LOGGER.info("GET {}/{} → {}", PATH, firstClientId, name);
        }
    }

    // GET /clients/{id} -> Error codes
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

    // ---------------------------
    // --- PATCH /clients/{id} ---
    // ---------------------------

    // PATCH /clients/{id}
    @Test
    void shouldPatchClient() {
        // Read original state
        String firstClientId = getFirstClientId();
        DocumentContext clientBeforePatchJson = getClientById(firstClientId);
        String originalPhone = clientBeforePatchJson.read("$.phone", String.class);
        String originalBirhday = clientBeforePatchJson.read("$.birthdate", String.class);

        // Prepare the json patch and the request
        String patchJson = "{\"name\": \"Patched Name\", \"email\": \"patched@example.com\" }";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> request = new HttpEntity<>(patchJson, headers);

        // Execute PATCH
        ResponseEntity<String> patchResponse = rest.exchange(
                PATH + "/" + firstClientId,
                HttpMethod.PATCH, request, String.class);

        // Status code
        assertThat(patchResponse.getStatusCode()).isEqualTo(HttpStatus.OK);

        // Verify changes
        DocumentContext afterJson = getClientById(firstClientId);
        String name = afterJson.read("$.name", String.class);
        String email = afterJson.read("$.email", String.class);
        String phone = afterJson.read("$.phone", String.class);
        String birthday = afterJson.read("$.birthdate", String.class);
        assertThat(name).isEqualTo("Patched Name");
        assertThat(email).isEqualTo("patched@example.com");
        assertThat(phone).isEqualTo(originalPhone); // unchanged
        assertThat(birthday).isEqualTo(originalBirhday); // unchanged

        if (VERBOSE) {
            LOGGER.info("PATCH {}/{} → {} (phone unchanged: {})", PATH, firstClientId, name, phone);
        }
    }

    // PATCH /clients/{id} -> error codes
    @Test
    void shouldNotPatchClient() {
        // Common setup
        String id = getFirstClientId();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        // PATCH unknown fields
        String badPatch = "{\"badField\": \"oops\"}";
        HttpEntity<String> request = new HttpEntity<>(badPatch, headers);
        ResponseEntity<String> response = rest.exchange(PATH + "/" + id, HttpMethod.PATCH, request, String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST); // 400

        // PATCH wrong UUID
        String fakeId = "00000000-0000-0000-0000-000000000000";
        String goodPatch = "{\"name\": \"Patched Name\" }";
        request = new HttpEntity<>(goodPatch, headers);
        response = rest.exchange(PATH + "/" + fakeId, HttpMethod.PATCH, request, String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND); // 404

        // PATCH empty Body -> OK because partial update, so no changes
        HttpEntity<String> emptyRequest = new HttpEntity<>("{}", headers);
        response = rest.exchange(PATH + "/" + id, HttpMethod.PATCH, emptyRequest, String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK); // OK
    }

    // ----------------------------
    // --- DELETE /clients/{id} ---
    // ----------------------------

    // DELETE /clients/{id}
    @Test
    void shouldSoftDeleteClient() {
        // Delete first client
        String firstClientId = getFirstClientId();
        LocalDateTime now = LocalDateTime.now();
        rest.delete(PATH + "/" + firstClientId);

        // Get the first client deleted (soft delete for archives)
        DocumentContext deletedClient = getClientById(firstClientId);

        // Attribute is_deleted
        Boolean isDeleted = deletedClient.read("$.isDeleted");
        assertThat(isDeleted).isTrue();

        // Attribute deletion_date
        String deletedDateStr = deletedClient.read("$.deletionDate");
        assertThat(deletedDateStr).isNotNull();
        LocalDateTime deletionDate = LocalDateTime.parse(deletedDateStr);
        assertThat(isSameLocalDateTime(deletionDate, now)).isTrue();

        if (VERBOSE) {
            LOGGER.info("DELETE {}/{} → {}", PATH, firstClientId, isDeleted);
        }
    }

    // DELETE /clients/{id} -> Error codes
    @Test
    void shouldNotDeleteClient() {
        // Wrong UUID
        String fakeId = "00000000-0000-0000-0000-000000000000";
        ResponseEntity<String> response = rest.exchange(PATH + "/" + fakeId, HttpMethod.DELETE, null, String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND); // 404

        // Wrong path
        response = rest.exchange(PATH + "/xxx", HttpMethod.DELETE, null, String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST); // 400
    }

    // ------------------------------------------
    // --- GET /clients/{id}/contracts/active ---
    // ------------------------------------------

    // GET /clients/{id}/contracts
    @Test
    void shouldGetActiveContractsForClient() {
        String firstClientId = getFirstClientId();

        ResponseEntity<String> response = rest.getForEntity(
                PATH + "/" + firstClientId + "/contracts", String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        DocumentContext json = JsonPath.parse(response.getBody());
        JSONArray costs = json.read("$..costAmount");

        assertThat(costs).hasSize(1);
        assertThat(new BigDecimal(costs.get(0).toString())).isEqualByComparingTo("400");

        if (VERBOSE) {
            LOGGER.info("Active contracts for client {} → {}", firstClientId, costs);
        }
    }

    // GET /clients/{id}/contracts -> wrong UUID
    @Test
    void shouldNotGetActiveContractsForWrongClient() {
        String fakeId = "00000000-0000-0000-0000-000000000000";
        ResponseEntity<String> resp = rest.getForEntity(PATH + "/" + fakeId + "/contracts", String.class);
        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT); // 204
    }

    // ------------------------------------------------------------------
    // --- GET /clients/{id}/contracts/after?date=2023-01-01T00:00:00 ---
    // ------------------------------------------------------------------

    // Get active contracts for a client after a given update date
    @Test
    void shouldGetContractsUpdatedAfterGivenDate() {
        String firstClientId = getFirstClientId();

        String date = "2023-01-01T00:00:00";
        ResponseEntity<String> response = rest.getForEntity(
                PATH + "/" + firstClientId + "/contracts/after?date=" + date, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        DocumentContext json = JsonPath.parse(response.getBody());
        JSONArray clientIds = json.read("$..clientId");

        assertThat(clientIds).isNotEmpty();
        assertThat(clientIds.get(0)).isEqualTo(firstClientId);

        if (VERBOSE) {
            LOGGER.info("Contracts updated after {} → {}", date, clientIds);
        }
    }

    // Get no active contracts for a client after a given update date
    @Test
    void shouldNotGetContractsUpdatedAfterFutureDate() {
        String firstClientId = getFirstClientId();
        String futureDate = LocalDateTime.now().plusYears(10).toString();

        ResponseEntity<String> response = rest.getForEntity(
                PATH + "/" + firstClientId + "/contracts/after?date=" + futureDate, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT); // 204
    }

    // GET /clients/{id}/contracts/after -> bad date format
    @Test
    void shouldRejectContractsUpdatedAfterWithBadDate() {
        String id = getFirstClientId();
        ResponseEntity<String> resp = rest.getForEntity(
                PATH + "/" + id + "/contracts/after?date=bad-date", String.class);
        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST); // 400
    }

    // -------------------------------------------
    // --- GET /clients/{id}/contracts/costsum ---
    // -------------------------------------------

    // GET /clients/{id}/contracts/costsum
    @Test
    void shouldGetContractsSummary() {
        String firstClientId = getFirstClientId();

        ResponseEntity<String> response = rest.getForEntity(
                PATH + "/" + firstClientId + "/contracts/costsum", String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        BigDecimal sum = new BigDecimal(response.getBody());
        assertThat(sum).isGreaterThan(BigDecimal.ZERO);
        assertThat(sum).isEqualByComparingTo(new BigDecimal("400"));

        if (VERBOSE) {
            LOGGER.info("Contracts sum for first client → {}", sum);
        }
    }

    // GET /clients/{id}/contracts/costsum -> wrong UUID
    @Test
    void shouldNotGetContractsSummaryForWrongClient() {
        String fakeId = "00000000-0000-0000-0000-000000000000";
        ResponseEntity<String> resp = rest.getForEntity(PATH + "/" + fakeId + "/contracts/costsum", String.class);
        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(new BigDecimal(resp.getBody())).isEqualByComparingTo(BigDecimal.ZERO);
    }

}
