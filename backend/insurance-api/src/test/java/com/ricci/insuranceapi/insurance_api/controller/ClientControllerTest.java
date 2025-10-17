package com.ricci.insuranceapi.insurance_api.controller;

import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;

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
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.assertj.core.api.Assertions.assertThat;

/*
 * This class performs integration tests on the ClientController.
 * It verifies the correct behavior of REST endpoints
 * and ensures the controller properly communicates with the service and repository layers.
 * Test data is loaded from: backend/insurance-api/src/test/resources/db/migration/R__sample-test-data.sql
 * Inspired by Spring Academy materials.
 */

// TODO - Refactor and improve tests

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class ClientControllerIntegrationTest {

    private static final String apiVersion = System.getProperty("api.version", "v1");
    private static final String path = "/api/" + apiVersion + "/clients";
    private static final Logger log = LoggerFactory.getLogger(ClientControllerIntegrationTest.class);
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

    // GET /clients
    @Test
    void shouldGetAllClients() {
        ResponseEntity<String> response = rest.getForEntity(path, String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        DocumentContext json = JsonPath.parse(response.getBody());
        int count = json.read("$.length()");
        assertThat(count).isEqualTo(3);

        JSONArray names = json.read("$..name");
        assertThat(names).isNotEmpty();

        if (verbose) {
            log.info("GET {} → {}", path, names);
        }
    }

    // GET /clients/{id}
    @Test
    void shouldGetClientById() {
        ResponseEntity<String> listResponse = rest.getForEntity(path, String.class);
        DocumentContext jsonList = JsonPath.parse(listResponse.getBody());
        String id = jsonList.read("$[0].clientId");

        ResponseEntity<String> response = rest.getForEntity(path + "/" + id, String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        DocumentContext json = JsonPath.parse(response.getBody());
        String name = json.read("$.name");
        assertThat(name).isNotBlank();

        if (verbose) {
            log.info("GET {}/{} → {}", path, id, name);
        }
    }

    // PATCH /clients/{id}
    @Test
    void shouldPartiallyUpdateClient() {
        String list = rest.getForObject(path, String.class);
        String id = JsonPath.parse(list).read("$[0].clientId");

        // Read original state
        String before = rest.getForObject(path + "/" + id, String.class);
        DocumentContext beforeJson = JsonPath.parse(before);
        String originalPhone = beforeJson.read("$.phone", String.class);

        // We patch a Person
        String originalBirhday = beforeJson.read("$.birthdate", String.class);

        // Prepare partial update
        String patchJson = """
                {
                  "name": "Patched Name",
                  "email": "patched@example.com"
                }
                """;

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> request = new HttpEntity<>(patchJson, headers);

        // Execute PATCH
        ResponseEntity<String> patchResponse = rest.exchange(path + "/" + id, HttpMethod.PATCH, request, String.class);
        assertThat(patchResponse.getStatusCode()).isEqualTo(HttpStatus.OK);

        // Verify changes
        String after = rest.getForObject(path + "/" + id, String.class);
        DocumentContext afterJson = JsonPath.parse(after);

        String name = afterJson.read("$.name", String.class);
        String email = afterJson.read("$.email", String.class);
        String phone = afterJson.read("$.phone", String.class);
        String birthday = afterJson.read("$.birthdate", String.class);

        assertThat(name).isEqualTo("Patched Name");
        assertThat(email).isEqualTo("patched@example.com");
        assertThat(phone).isEqualTo(originalPhone); // unchanged
        assertThat(birthday).isEqualTo(originalBirhday); // unchanged

        if (verbose)
            log.info("PATCH {}/{} → {} (phone unchanged: {})", path, id, name, phone);
    }

    // DELETE /clients/{id}
    @Test
    void shouldSoftDeleteClient() {
        String list = rest.getForObject(path, String.class);
        String id = JsonPath.parse(list).read("$[0].clientId");

        rest.delete(path + "/" + id);

        String deletedJson = rest.getForObject(path + "/" + id, String.class);
        Boolean isDeleted = JsonPath.parse(deletedJson).read("$.isDeleted");
        assertThat(isDeleted).isTrue();

        if (verbose) {
            log.info("DELETE {}/{} → {}", path, id, isDeleted);
        }
    }

}
