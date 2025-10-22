package com.ricci.insuranceapi.insurance_api.repository;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import com.ricci.insuranceapi.insurance_api.InsuranceApiApplicationTests;
import com.ricci.insuranceapi.insurance_api.model.Client;
import com.ricci.insuranceapi.insurance_api.model.Company;
import com.ricci.insuranceapi.insurance_api.model.Person;

import static org.assertj.core.api.Assertions.assertThat;

/*
 * This class tests the ClientRepository to verify the communication
 * between Spring Boot, Hibernate, and the PostgreSQL database.
 * Test data is loaded from InsuranceApiApplicationTests parent class.
 * Inspired by Spring Academy materials
 */

@SpringBootTest
@ActiveProfiles("test")
public class ClientRepositoryTest extends InsuranceApiApplicationTests {

    @Autowired
    private ClientRepository clientRepository;

    // --------------------
    // --- Read Clients ---
    // --------------------

    // Read -> Find All Clients
    @Test
    void shouldFindAllClients() {
        List<Client> clients = clientRepository.findAll();

        assertThat(clients).isNotNull().hasSize(3);

        if (VERBOSE) {
            LOGGER.info("Clients found: {}", clients);
        }
    }

    // Read -> Find By ID
    @Test
    void shouldFindClientById() {
        Client first = clientRepository.findAll().get(0);
        Client found = clientRepository.findById(first.getClientId()).orElse(null);

        assertThat(found).isNotNull();
        assertThat(found.getEmail()).isEqualTo(first.getEmail());

        if (VERBOSE) {
            LOGGER.info("Found client by ID: {}", found);
        }
    }

    // --------------------------------------------
    // --- Create Client (concrete child class) ---
    // --------------------------------------------

    // Create -> Insert New Person (Client's child)
    @Test
    void shouldCreateNewPerson() {
        String name = "John Doe";
        String email = "john.doe@example.com";
        String phone = "+41770000000";
        LocalDate birthdate = LocalDate.of(1995, 3, 12);

        Person client = new Person();
        client.setName(name);
        client.setEmail(email);
        client.setPhone(phone);
        client.setBirthdate(birthdate);

        Person created = clientRepository.save(client);

        List<Client> all = clientRepository.findAll();
        assertThat(all).hasSize(4);

        assertThat(created).isNotNull();
        assertThat(created.getName()).isEqualTo(name);
        assertThat(created.getEmail()).isEqualTo(email);
        assertThat(created.getPhone()).isEqualTo(phone);
        assertThat(created.getBirthdate()).isEqualTo(birthdate);

        if (VERBOSE) {
            LOGGER.info("New client created: {}", created);
        }
    }

    // Create -> Insert New Company (Client's child)
    @Test
    void shouldCreateNewCompany() {
        String name = "ACME Corp";
        String email = "contact@acme.com";
        String phone = "+41771112233";
        String companyId = "CHE-123.456.789";

        Company company = new Company();
        company.setName(name);
        company.setEmail(email);
        company.setPhone(phone);
        company.setCompanyIdentifier(companyId);

        Company created = clientRepository.save(company);

        List<Client> all = clientRepository.findAll();
        assertThat(all).hasSize(4);

        assertThat(created).isNotNull();
        assertThat(created.getName()).isEqualTo(name);
        assertThat(created.getEmail()).isEqualTo(email);
        assertThat(created.getPhone()).isEqualTo(phone);
        assertThat(created.getCompanyIdentifier()).isEqualTo(companyId);

        if (VERBOSE) {
            LOGGER.info("New company created: {}", created);
        }
    }

    // ---------------------
    // --- Update Client ---
    // ---------------------

    // Update -> Partial update a Client
    @Test
    void shouldUpdateClient() {
        Client client = clientRepository.findAll().get(0);
        String newEmail = "updated@example.com";
        client.setEmail(newEmail);

        Client updated = clientRepository.save(client);

        assertThat(updated.getEmail()).isEqualTo(newEmail);
    }

    // ---------------------
    // --- Delete Client ---
    // ---------------------

    // Delete -> Soft Delete Client
    @Test
    void shouldSoftDeleteClient() {
        Client client = clientRepository.findAll().get(0);
        client.setIsDeleted(true);
        clientRepository.save(client);

        Client updated = clientRepository.findById(client.getClientId()).orElseThrow();
        assertThat(updated.getIsDeleted()).isTrue();

        if (VERBOSE) {
            LOGGER.info("Client soft deleted: {}", updated);
        }
    }

}
