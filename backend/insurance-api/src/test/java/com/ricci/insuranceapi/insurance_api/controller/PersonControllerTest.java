package com.ricci.insuranceapi.insurance_api.controller;

import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import com.ricci.insuranceapi.insurance_api.model.Person;

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
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class PersonControllerIntegrationTest {

    private static final String apiVersion = System.getProperty("api.version", "v1");
    private static final String path = "/api/" + apiVersion + "/clients/persons";
    private static final Logger log = LoggerFactory.getLogger(PersonControllerIntegrationTest.class);
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

    // POST /clients/persons
    @Test
    void shouldCreateNewPerson() {
        Person newPerson = new Person();
        newPerson.setName("Charlie Dupuis");
        newPerson.setEmail("charlie@example.com");
        newPerson.setPhone("+41773334444");
        newPerson.setBirthdate(LocalDate.of(1995, 3, 12));

        ResponseEntity<Void> createResponse = rest.postForEntity(path, newPerson, Void.class);
        assertThat(createResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        URI locationOfNewPerson = createResponse.getHeaders().getLocation();
        assertThat(locationOfNewPerson).isNotNull();

        ResponseEntity<String> getResponse = rest.getForEntity(locationOfNewPerson, String.class);
        assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);

        DocumentContext json = JsonPath.parse(getResponse.getBody());
        String name = json.read("$.name");
        String email = json.read("$.email");
        String phone = json.read("$.phone");
        String birthdate = json.read("$.birthdate");

        assertThat(name).isEqualTo("Charlie Dupuis");
        assertThat(email).isEqualTo("charlie@example.com");
        assertThat(phone).isEqualTo("+41773334444");
        assertThat(birthdate).isEqualTo("1995-03-12");

        if (verbose) {
            log.info("POST {} → {}", path, name);
        }
    }

    // --- EXTRA ---

    // GET /clients/persons
    @Test
    void shouldGetAllPersons() {
        ResponseEntity<String> response = rest.getForEntity(path, String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        DocumentContext json = JsonPath.parse(response.getBody());
        int count = json.read("$.length()");
        assertThat(count).isEqualTo(2); // Alice + Bob in sample data

        JSONArray names = json.read("$..name");
        assertThat(names).contains("Alice Dupont", "Bob Martin");

        if (verbose) {
            log.info("GET {} → {}", path, names);
        }
    }
}
