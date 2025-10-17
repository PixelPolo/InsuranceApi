package com.ricci.insuranceapi.insurance_api.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;

import com.ricci.insuranceapi.insurance_api.dto.ClientPatchDto;
import com.ricci.insuranceapi.insurance_api.exception.ClientNotFoundException;
import com.ricci.insuranceapi.insurance_api.model.Client;
import com.ricci.insuranceapi.insurance_api.model.Person;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

/*
 * This class tests the ClientService layer to verify the business logic
 * interacting with the ClientRepository and database.
 * Test data is loaded from: backend/insurance-api/src/test/resources/db/migration/R__sample-test-data.sql
 * Inspired by Spring Academy materials.
 */

// TODO - Refactor and improve tests

@SpringBootTest
@ActiveProfiles("test")
class ClientServiceTest {

    private static final Logger log = LoggerFactory.getLogger(ClientServiceTest.class);
    private static final boolean verbose = "true".equalsIgnoreCase(System.getProperty("test.verbose", "true"));

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
        Page<Client> clients = clientService.getAllClients(PageRequest.of(0, 10));

        assertThat(clients.getContent()).isNotNull().hasSize(3);

        if (verbose) {
            log.info("Clients found: {}", clients.getContent());
        }
    }

    // Read -> Find By ID
    @Test
    void shouldFindClientById() {
        Client first = clientService.getAllClients(
                PageRequest.of(0, 1))
                .getContent().get(0);
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

    // Read -> Pagination
    @Test
    void shouldReturnSingleClientWithPagination() {
        Page<Client> page = clientService.getAllClients(PageRequest.of(0, 1));

        assertThat(page.getContent()).hasSize(1);
        assertThat(page.getTotalElements()).isEqualTo(3);

        Client first = page.getContent().get(0);
        assertThat(first).isNotNull();

        if (verbose) {
            log.info("Page 0 (size 1) returned client: {}", first);
        }
    }

    // Partial update
    @Test
    void shouldPartiallyUpdateClient() {
        Client existing = clientService.getAllClients(PageRequest.of(0, 1)).getContent().get(0);
        UUID id = existing.getClientId();

        String oldPhone = existing.getPhone();

        ClientPatchDto updates = new ClientPatchDto();
        updates.setName("Updated Name");
        updates.setEmail("updated@example.com");

        Client updated = clientService.partialUpdate(id, updates);

        // Cast to Person to check birthdate
        Person updatedPerson = (Person) updated;
        Person existingPerson = (Person) existing;

        assertThat(updatedPerson.getName()).isEqualTo("Updated Name");
        assertThat(updatedPerson.getEmail()).isEqualTo("updated@example.com");
        assertThat(updatedPerson.getPhone()).isEqualTo(oldPhone); // unchanged
        assertThat(updatedPerson.getBirthdate()).isEqualTo(existingPerson.getBirthdate()); // unchanged

        if (verbose) {
            log.info("Client partially updated: {}", updated);
        }
    }

    // Delete -> Soft Delete Client
    @Test
    void shouldSoftDeleteClient() {
        Client client = clientService.getAllClients(PageRequest.of(0, 1))
                .getContent()
                .get(0);

        Client deleted = clientService.deleteClient(client.getClientId());

        assertThat(deleted.getIsDeleted()).isTrue();
        LocalDate deletedDate = deleted.getDeletionDate().toLocalDate();
        assertThat(deletedDate).isEqualTo(LocalDate.now());

        if (verbose) {
            log.info("Client soft deleted: {}", deleted);
        }
    }

    // Delete -> Already Deleted
    @Test
    void shouldNotReDeleteAlreadyDeletedClient() {
        Client client = clientService.getAllClients(PageRequest.of(0, 1))
                .getContent()
                .get(0);

        // First deletion
        Client firstDelete = clientService.deleteClient(client.getClientId());

        // Second deletion should not change anything
        Client secondDelete = clientService.deleteClient(client.getClientId());

        assertThat(secondDelete.getIsDeleted()).isTrue();
        // Truncate to avoid precision error with nanoseconds
        LocalDateTime firstDeleteDate = firstDelete.getDeletionDate().truncatedTo(ChronoUnit.SECONDS);
        LocalDateTime seconDeleteDate = secondDelete.getDeletionDate().truncatedTo(ChronoUnit.SECONDS);
        assertThat(firstDeleteDate).isEqualTo(seconDeleteDate);
    }

}
