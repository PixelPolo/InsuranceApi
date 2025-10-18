package com.ricci.insuranceapi.insurance_api.controller;

import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import com.ricci.insuranceapi.insurance_api.InsuranceApiApplicationTests;
import com.ricci.insuranceapi.insurance_api.dto.PersonDto;

import net.minidev.json.JSONArray;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.test.context.ActiveProfiles;

import java.net.URI;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

/*
 * This class performs integration tests on the PersonController.
 * It verifies the correct behavior of REST endpoints and ensures 
 * the controller properly communicates with the service and repository layers.
 * Test data is loaded from InsuranceApiApplicationTests parent class.
 * Inspired by Spring Academy materials
 */

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class PersonControllerIntegrationTest extends InsuranceApiApplicationTests {

    private static final String PATH = BASE_PATH + "clients/persons";

    @Autowired
    private TestRestTemplate rest;

    // -------------------------------
    // --- POST /clients/persons ---
    // -------------------------------

    // POST /clients/persons
    @Test
    void shouldCreateNewPerson() {
        // Post a new person
        PersonDto newPersonDto = new PersonDto();
        newPersonDto.setName("Charlie Dupuis");
        newPersonDto.setEmail("charlie@example.com");
        newPersonDto.setPhone("+41773334444");
        newPersonDto.setBirthdate(LocalDate.of(1995, 3, 12));
        ResponseEntity<Void> createResponse = rest.postForEntity(PATH, newPersonDto, Void.class);

        // Status
        assertThat(createResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        // Location
        URI locationOfNewPerson = createResponse.getHeaders().getLocation();
        assertThat(locationOfNewPerson).isNotNull();

        // Get the posted company
        ResponseEntity<String> getResponse = rest.getForEntity(locationOfNewPerson, String.class);
        assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);

        // Fields validation
        DocumentContext json = JsonPath.parse(getResponse.getBody());
        String name = json.read("$.name");
        String email = json.read("$.email");
        String phone = json.read("$.phone");
        String birthdate = json.read("$.birthdate");
        assertThat(name).isEqualTo("Charlie Dupuis");
        assertThat(email).isEqualTo("charlie@example.com");
        assertThat(phone).isEqualTo("+41773334444");
        assertThat(birthdate).isEqualTo("1995-03-12");

        if (VERBOSE) {
            LOGGER.info("POST {} → {}", PATH, name);
        }
    }

    // POST /clients/persons -> Error codes
    @Test
    void shouldNotCreatePerson() {
        // Common setup
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        // Invalid email format
        PersonDto invalidEmail = new PersonDto();
        invalidEmail.setEmail("not-an-email");
        ResponseEntity<String> response = rest.postForEntity(PATH, invalidEmail, String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);

        // Invalid phone format
        PersonDto invalidPhone = new PersonDto();
        invalidPhone.setPhone("abc");
        response = rest.postForEntity(PATH, invalidPhone, String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);

        // Unknown field
        String badJson = "{\"unknownField\":\"oops\"}";
        HttpEntity<String> badRequest = new HttpEntity<>(badJson, headers);
        response = rest.exchange(PATH, HttpMethod.POST, badRequest, String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);

        // Wrong path
        response = rest.postForEntity(PATH + "xxx", new PersonDto(), String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.METHOD_NOT_ALLOWED);

        if (VERBOSE) {
            LOGGER.info("POST {} → error codes tested", PATH);
        }
    }

    // -------------
    // --- EXTRA ---
    // -------------

    // GET /clients/persons
    @Test
    void shouldGetAllPersons() {
        // Get all persons
        ResponseEntity<String> response = rest.getForEntity(PATH, String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        DocumentContext json = JsonPath.parse(response.getBody());

        // Length should be 2 from test data
        int count = json.read("$.length()");
        assertThat(count).isEqualTo(2);

        // Names should be Alice and Bob
        JSONArray names = json.read("$..name");
        assertThat(names).contains("Alice Dupont", "Bob Martin");

        if (VERBOSE) {
            LOGGER.info("GET {} → {}", PATH, names);
        }
    }

}
