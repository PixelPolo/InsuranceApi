package com.ricci.insuranceapi.insurance_api.repository;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import com.ricci.insuranceapi.insurance_api.InsuranceApiApplicationTests;
import com.ricci.insuranceapi.insurance_api.model.Person;

import static org.assertj.core.api.Assertions.assertThat;

/*
 * This class tests the PersonRepository to verify the communication
 * between Spring Boot, Hibernate, and the PostgreSQL database.
 * Test data is loaded from InsuranceApiApplicationTests parent class.
 * Inspired by Spring Academy materials
 */

@SpringBootTest
@ActiveProfiles("test")
public class PersonRepositoryTest extends InsuranceApiApplicationTests {

    @Autowired
    private PersonRepository personRepository;

    // Create -> Tested in ClientRepositoryTest

    // Read -> Find All Persons
    @Test
    void shouldFindAllPersons() {
        List<Person> persons = personRepository.findAll();

        assertThat(persons).isNotNull().hasSize(2);

        if (VERBOSE) {
            LOGGER.info("Persons found: {}", persons);
        }
    }

}
