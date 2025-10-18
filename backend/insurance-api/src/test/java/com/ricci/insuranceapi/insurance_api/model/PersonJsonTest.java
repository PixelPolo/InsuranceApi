package com.ricci.insuranceapi.insurance_api.model;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;

import com.ricci.insuranceapi.insurance_api.dto.PersonDto;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

/*
 * This class tests the JSON serialization and deserialization
 * of the Person entity using Jackson.
 * It ensures that the JSON format matches the expected API structure.
 */

@JsonTest
class PersonJsonTest {

    @Autowired
    private JacksonTester<PersonDto> json;

    @Autowired
    private JacksonTester<PersonDto[]> jsonList;

    // -----------------------
    // --- Deserialization ---
    // -----------------------

    // Deserialization -> Single Person
    @Test
    void shouldDeserializeSinglePerson() throws IOException {
        // Deserialize test data from JSON file
        PersonDto[] persons = jsonList.read("persons.json").getObject();
        PersonDto firstPerson = persons[0];
        // Verify that all fields were correctly deserialized
        assertThat(firstPerson.getClientId()).isNotNull();
        assertThat(firstPerson.getName()).isEqualTo("Alice Dupont");
        assertThat(firstPerson.getEmail()).isEqualTo("alice@example.com");
        assertThat(firstPerson.getPhone()).isEqualTo("+41791234567");
        assertThat(firstPerson.getBirthdate().toString()).isEqualTo("1990-05-15");
        assertThat(firstPerson.getIsDeleted()).isFalse();
    }

    // Deserialization -> List
    @Test
    void shouldDeserializePersonsList() throws IOException {
        PersonDto[] persons = jsonList.read("persons.json").getObject();
        assertThat(persons).hasSize(2);
        assertThat(persons[0].getClientId()).isNotNull();
        assertThat(persons[1].getName()).isEqualTo("Bob Martin");
    }

    // ---------------------
    // --- Serialization ---
    // ---------------------

    // Serialization -> Single person
    @Test
    void shouldSerializeSinglePerson() throws IOException {
        // Deserialize test data from JSON file
        PersonDto[] persons = jsonList.read("persons.json").getObject();
        PersonDto firstPerson = persons[0];
        // Serialize Person object back to JSON and verify output
        assertThat(json.write(firstPerson)).isStrictlyEqualToJson("""
                {
                  "clientId": "550e8400-e29b-41d4-a716-446655440000",
                  "phone": "+41791234567",
                  "email": "alice@example.com",
                  "name": "Alice Dupont",
                  "birthdate": "1990-05-15",
                  "isDeleted": false,
                  "deletionDate": null
                }""");
    }

    // Serialization -> List
    @Test
    void shouldSerializePersonsListToJson() throws IOException {
        PersonDto[] persons = jsonList.read("persons.json").getObject();
        assertThat(jsonList.write(persons)).isStrictlyEqualToJson("persons.json");
    }

}
