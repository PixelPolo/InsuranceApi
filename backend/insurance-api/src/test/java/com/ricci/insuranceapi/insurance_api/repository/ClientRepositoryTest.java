package com.ricci.insuranceapi.insurance_api.repository;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;

import com.ricci.insuranceapi.insurance_api.model.Client;

import static org.assertj.core.api.Assertions.assertThat;

/*
 * This class tests the ClientRepository to verify the communication
 * between Spring Boot, Hibernate, and the PostgreSQL database.
 * Test data is injected by the Flyway migration tool from:
 * backend/insurance-api/src/test/resources/db/migration/R__sample-test-data.sql
 * Inspired by Spring Academy materials.
 */

@SpringBootTest
@ActiveProfiles("test")
public class ClientRepositoryTest {

    private static final Logger log = LoggerFactory.getLogger(ClientRepositoryTest.class);
    private static final boolean verbose = "true".equalsIgnoreCase(System.getProperty("test.verbose"));

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private JdbcTemplate jdbc;

    @BeforeEach
    void resetDatabase() throws IOException {
        String sql = Files.readString(Paths.get("src/test/resources/db/migration/R__sample-test-data.sql"));
        jdbc.execute(sql);
    }

    // Read -> Find All Clients
    @Test
    void shouldFindAllClients() {
        List<Client> clients = clientRepository.findAll();

        assertThat(clients).isNotNull().hasSize(3);

        if (verbose) {
            log.info("Clients found: {}", clients);
        }
    }

    // Read -> Find By ID
    @Test
    void shouldFindClientById() {
        Client first = clientRepository.findAll().get(0);
        Client found = clientRepository.findById(first.getClientId()).orElse(null);

        assertThat(found).isNotNull();
        assertThat(found.getEmail()).isEqualTo(first.getEmail());

        if (verbose) {
            log.info("Found client by ID: {}", found);
        }
    }

    // Create -> Insert New Client
    @Test
    void shouldCreateNewClient() {
        Client client = new Client();
        client.setName("John Doe");
        client.setEmail("john.doe@example.com");
        client.setPhone("+41770000000");

        clientRepository.save(client);

        List<Client> all = clientRepository.findAll();
        assertThat(all).hasSize(4);

        if (verbose) {
            log.info("New client created: {}", client);
        }
    }

    // Delete -> Soft Delete Client
    @Test
    void shouldSoftDeleteClient() {
        Client client = clientRepository.findAll().get(0);
        client.setIsDeleted(true);
        clientRepository.save(client);

        Client updated = clientRepository.findById(client.getClientId()).orElseThrow();
        assertThat(updated.getIsDeleted()).isTrue();

        if (verbose) {
            log.info("Client soft deleted: {}", updated);
        }
    }

}
