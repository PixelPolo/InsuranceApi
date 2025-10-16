package com.ricci.insuranceapi.insurance_api.controller;

import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import com.ricci.insuranceapi.insurance_api.model.Client;

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
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

/*
 * This class performs integration tests on the ClientController.
 * It verifies the correct behavior of all REST endpoints (CRUD operations)
 * and ensures the controller properly communicates with the service and repository layers.
 * Test data is loaded via Flyway from:
 * backend/insurance-api/src/test/resources/db/migration/R__sample-test-data.sql
 * Inspired by Spring Academy materials.
 */

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class ClientControllerIntegrationTest {

    private static final String apiVersion = System.getProperty("api.version", "v1");
    private static final String path = "/api/" + apiVersion + "/clients";
    private static final Logger log = LoggerFactory.getLogger(ClientControllerIntegrationTest.class);
    private static final boolean verbose = "true".equalsIgnoreCase(System.getProperty("test.verbose"));

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

    // POST /clients
    @Test
    void shouldCreateANewClient() {
        Client newClient = new Client();
        newClient.setName("John Doe");
        newClient.setEmail("john.doe@example.com");
        newClient.setPhone("+41770000000");

        ResponseEntity<Void> createResponse = rest
                .postForEntity(path, newClient, Void.class);
        assertThat(createResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        URI locationOfNewClient = createResponse.getHeaders().getLocation();
        assertThat(locationOfNewClient).isNotNull();

        ResponseEntity<String> getResponse = rest.getForEntity(locationOfNewClient, String.class);
        assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);

        DocumentContext json = JsonPath.parse(getResponse.getBody());
        String name = json.read("$.name");
        String email = json.read("$.email");
        String phone = json.read("$.phone");

        assertThat(name).isEqualTo("John Doe");
        assertThat(email).isEqualTo("john.doe@example.com");
        assertThat(phone).isEqualTo("+41770000000");

        if (verbose) {
            log.info("POST {} → {}", path, name);
        }
    }

    // PUT /clients/{id}
    @Test
    void shouldUpdateAnExistingClient() {
        ResponseEntity<Client[]> listResponse = rest.getForEntity(path, Client[].class);

        Client[] body = listResponse.getBody();
        if (body == null || body.length == 0) {
            throw new IllegalStateException("No clients found for test setup");
        }

        UUID id = body[0].getClientId();

        Client updatedClient = new Client();
        updatedClient.setName("Updated Name");
        updatedClient.setEmail("updated@example.com");
        updatedClient.setPhone("+41771111111");

        HttpEntity<Client> request = new HttpEntity<>(updatedClient);

        // This was to test PUT, replaced for requirements
        // ResponseEntity<Void> response = rest.exchange(path + "/" + id,
        // HttpMethod.PUT, request, Void.class);

        ResponseEntity<Void> response = rest.exchange(path + "/" + id, HttpMethod.PATCH, request, Void.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        ResponseEntity<String> getResponse = rest.getForEntity(path + "/" + id, String.class);
        assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);

        DocumentContext json = JsonPath.parse(getResponse.getBody());
        String name = json.read("$.name");
        String email = json.read("$.email");

        assertThat(name).isEqualTo("Updated Name");
        assertThat(email).isEqualTo("updated@example.com");

        if (verbose) {
            // log.info("PUT {}/{} → {}", path, id, name);
            log.info("PACTH {}/{} → {}", path, id, name);
        }
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
