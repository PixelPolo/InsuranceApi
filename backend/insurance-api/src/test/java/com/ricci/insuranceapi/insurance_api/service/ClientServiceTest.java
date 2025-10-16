package com.ricci.insuranceapi.insurance_api.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;

import com.ricci.insuranceapi.insurance_api.exception.ClientNotFoundException;
import com.ricci.insuranceapi.insurance_api.model.Client;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

/*
 * This class tests the ClientService layer to verify the business logic
 * interacting with the ClientRepository and database.
 * The database is reset before each test using the sample Flyway script:
 * backend/insurance-api/src/test/resources/db/migration/R__sample-test-data.sql
 */

@SpringBootTest
@ActiveProfiles("test")
class ClientServiceTest {

    private static final Logger log = LoggerFactory.getLogger(ClientServiceTest.class);
    private static final boolean verbose = true;

    @Autowired
    private ClientService clientService;

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
        List<Client> clients = clientService.getAllClients();

        assertThat(clients).isNotNull().hasSize(3);

        if (verbose) {
            log.info("Clients found: {}", clients);
        }
    }

    // Read -> Find By ID
    @Test
    void shouldFindClientById() {
        Client first = clientService.getAllClients().get(0);
        Client found = clientService.getClient(first.getClientId());

        assertThat(found).isNotNull();
        assertThat(found.getEmail()).isEqualTo(first.getEmail());

        if (verbose) {
            log.info("Found client by ID: {}", found);
        }
    }

    // Read -> Should throw if client not found
    @Test
    void shouldThrowWhenClientNotFound() {
        UUID fakeId = UUID.randomUUID();

        assertThrows(ClientNotFoundException.class, () -> {
            clientService.getClient(fakeId);
        });
    }

    // Create -> Insert New Client
    @Test
    void shouldCreateNewClient() {
        Client client = new Client();
        client.setName("John Doe");
        client.setEmail("john.doe@example.com");
        client.setPhone("+41770000000");

        clientService.createClient(client);

        List<Client> all = clientService.getAllClients();
        assertThat(all).hasSize(4);

        if (verbose) {
            log.info("New client created: {}", client);
        }
    }

    // Update -> Update existing Client
    @Test
    void shouldUpdateClient() {
        Client existing = clientService.getAllClients().get(0);
        Client updates = new Client();
        updates.setName("Updated Name");
        updates.setEmail("updated@example.com");
        updates.setPhone("+41771111111");

        Client updated = clientService.updateClient(existing.getClientId(), updates);

        assertThat(updated.getName()).isEqualTo("Updated Name");
        assertThat(updated.getEmail()).isEqualTo("updated@example.com");

        if (verbose) {
            log.info("Client updated: {}", updated);
        }
    }

    // Delete -> Soft Delete Client
    @Test
    void shouldSoftDeleteClient() {
        Client client = clientService.getAllClients().get(0);

        Client deleted = clientService.deleteClient(client.getClientId());

        assertThat(deleted.getIsDeleted()).isTrue();
        assertThat(deleted.getDeletionDate()).isEqualTo(LocalDate.now());

        if (verbose) {
            log.info("Client soft deleted: {}", deleted);
        }
    }

    // Delete -> Already Deleted
    @Test
    void shouldNotReDeleteAlreadyDeletedClient() {
        Client client = clientService.getAllClients().get(0);

        // First deletion
        Client firstDelete = clientService.deleteClient(client.getClientId());

        // Second deletion should not change anything
        Client secondDelete = clientService.deleteClient(client.getClientId());

        assertThat(secondDelete.getIsDeleted()).isTrue();
        assertThat(secondDelete.getDeletionDate()).isEqualTo(firstDelete.getDeletionDate());
    }
}
