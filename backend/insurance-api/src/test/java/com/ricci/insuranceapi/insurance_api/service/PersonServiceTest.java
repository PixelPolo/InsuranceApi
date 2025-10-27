package com.ricci.insuranceapi.insurance_api.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import com.ricci.insuranceapi.insurance_api.InsuranceApiApplicationTests;
import com.ricci.insuranceapi.insurance_api.dto.PersonDto;
import com.ricci.insuranceapi.insurance_api.model.Person;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;

/*
 * This class performs integration tests on the PersonService.
 * It verifies the business logic interacting with the repository layer
 * and ensures data consistency through real database operations.
 * Test data is loaded from the InsuranceApiApplicationTests parent class.
 * Inspired by Spring Academy materials.
 */

public class PersonServiceTest extends InsuranceApiApplicationTests {

    @Autowired
    PersonService personService;

    private PageRequest fullPageRequest = PageRequest.of(0, 10);

    // ----------------------
    // --- Read persons ---
    // ----------------------

    // Read -> Find All Persons
    @Test
    void shouldFindAllPersons() {
        Page<Person> persons = personService.getAllPersons(fullPageRequest);

        assertThat(persons.getContent()).isNotNull().isNotEmpty();

        if (VERBOSE) {
            LOGGER.info("Persons found: {}", persons.getContent());
        }
    }

    // ----------------------
    // --- Create persons ---
    // ----------------------

    // Create - Create a person
    @Test
    void shouldCreateNewPerson() {
        PersonDto newPerson = new PersonDto();
        newPerson.setBirthdate(LocalDate.of(1995, 3, 12));
        newPerson.setEmail("person@example.com");
        newPerson.setName("Person Name");
        newPerson.setPhone("+4179 123 45 67");

        Person created = personService.createPerson(newPerson);

        assertThat(created.getClientId()).isNotNull();
        assertThat(created.getBirthdate()).isEqualTo(newPerson.getBirthdate());
        assertThat(created.getEmail()).isEqualTo(newPerson.getEmail());
        assertThat(created.getName()).isEqualTo(newPerson.getName());
        assertThat(created.getPhone()).isEqualTo(newPerson.getPhone());
    }

}
