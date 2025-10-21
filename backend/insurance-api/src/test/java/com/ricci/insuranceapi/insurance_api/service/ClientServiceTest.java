package com.ricci.insuranceapi.insurance_api.service;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;

import com.ricci.insuranceapi.insurance_api.InsuranceApiApplicationTests;
import com.ricci.insuranceapi.insurance_api.dto.ClientPatchDto;
import com.ricci.insuranceapi.insurance_api.exception.ClientInvalidDataException;
import com.ricci.insuranceapi.insurance_api.exception.ClientNotFoundException;
import com.ricci.insuranceapi.insurance_api.model.Client;
import com.ricci.insuranceapi.insurance_api.model.Contract;
import com.ricci.insuranceapi.insurance_api.model.Person;

import jakarta.transaction.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

/*
 * This class performs integration tests on the ClientService.
 * It verifies the business logic interacting with the repository layer 
 * and ensures data consistency through real database operations.
 * Test data is loaded from the InsuranceApiApplicationTests parent class.
 * Inspired by Spring Academy materials.
 */

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class ClientServiceTest extends InsuranceApiApplicationTests {

    @Autowired
    private ClientService clientService;

    @Autowired
    private ContractService contractService;

    private PageRequest fullPageRequest = PageRequest.of(0, 10);
    private PageRequest firstPageSizeOne = PageRequest.of(0, 1);

    // --------------------
    // --- Read clients ---
    // --------------------

    // Read -> Find All Clients
    @Test
    void shouldFindAllClients() {
        Page<Client> clients = clientService.getAllClients(fullPageRequest);

        assertThat(clients.getContent()).isNotNull().hasSize(3);

        if (VERBOSE) {
            LOGGER.info("Clients found: {}", clients.getContent());
        }
    }

    // Read -> Find By ID
    @Test
    void shouldFindClientById() {
        Client first = clientService.getAllClients(fullPageRequest).getContent().get(0);
        Client found = clientService.getClient(first.getClientId());

        assertThat(found).isNotNull();
        assertThat(found.getEmail()).isEqualTo(first.getEmail());

        if (VERBOSE) {
            LOGGER.info("Found client by ID: {}", found);
        }
    }

    // Read -> Pagination
    @Test
    void shouldFindFirstClientWithPagination() {
        Page<Client> page = clientService.getAllClients(firstPageSizeOne);

        assertThat(page.getContent()).hasSize(1);
        assertThat(page.getTotalElements()).isEqualTo(3);

        Client first = page.getContent().get(0);
        assertThat(first).isNotNull();

        assertThrows(IndexOutOfBoundsException.class, () -> {
            page.getContent().get(1);
        });

        if (VERBOSE) {
            LOGGER.info("Page 0 size 1 returned client: {}", first);
        }
    }

    // ----------------------
    // --- Update clients ---
    // ----------------------

    // Partial update
    @Test
    void shouldPartiallyUpdateClient() {
        ClientPatchDto update = new ClientPatchDto();
        update.setName("Updated Name");
        update.setEmail("updated@example.com");

        Client firstClient = clientService.getAllClients(fullPageRequest).getContent().get(0);
        Client firstClientUpdated = clientService.partialUpdate(firstClient.getClientId(), update);

        assertThat(firstClientUpdated.getName()).isEqualTo("Updated Name");
        assertThat(firstClientUpdated.getEmail()).isEqualTo("updated@example.com");
        assertThat(firstClientUpdated.getPhone()).isEqualTo(firstClient.getPhone()); // unchanged

        // Need a cast to Person to check the birthdate specific field
        if (firstClientUpdated instanceof Person person && firstClient instanceof Person original) {
            assertThat(person.getBirthdate()).isEqualTo(original.getBirthdate()); // unchanged
        }

        if (VERBOSE) {
            LOGGER.info("Client partially updated: {}", firstClientUpdated);
        }
    }

    // ----------------------
    // --- Delete clients ---
    // ----------------------

    // Delete -> Soft Delete Client
    @Test
    void shouldSoftDeleteClient() {
        Client firstClient = clientService.getAllClients(fullPageRequest).getContent().get(0);
        List<Contract> firstContracts = contractService.getActiveContracts(firstClient.getClientId());

        Client deleted = clientService.deleteClient(firstClient.getClientId());
        List<Contract> deletedContracts = contractService.getActiveContracts(deleted.getClientId());

        assertThat(firstContracts).isNotNull().isNotEmpty();
        assertThat(deletedContracts).isNotNull().isEmpty(); // No active contracts

        LocalDate deletionDate = deleted.getDeletionDate().toLocalDate();
        assertThat(deletionDate).isEqualTo(LocalDate.now());
        assertThat(deleted.getIsDeleted()).isTrue();

        if (VERBOSE) {
            LOGGER.info("Client soft deleted: {}", deleted);
        }
    }

    // Delete -> Already Deleted
    @Test
    void shouldNotReDeleteAlreadyDeletedClient() {
        // First deletion
        Client firstClient = clientService.getAllClients(fullPageRequest).getContent().get(0);
        Client firstDeleted = clientService.deleteClient(firstClient.getClientId());
        assertThat(firstDeleted.getIsDeleted()).isTrue();

        // Second deletion should not change anything
        Client secondDeleted = clientService.deleteClient(firstClient.getClientId());
        assertThat(secondDeleted.getIsDeleted()).isTrue();
        assertThat(isSameLocalDateTime(firstDeleted.getDeletionDate(), secondDeleted.getDeletionDate())).isTrue();
    }

    // ------------------
    // --- Exceptions ---
    // ------------------

    // Read, Patch, Delete -> ClientNotFoundException
    @Test
    void shouldThrowClientNotFoundException() {
        UUID fakeId = UUID.randomUUID();

        // Read
        assertThrows(ClientNotFoundException.class, () -> {
            clientService.getClient(fakeId);
        });

        // Patch
        ClientPatchDto updates = new ClientPatchDto();
        assertThrows(ClientNotFoundException.class, () -> {
            clientService.partialUpdate(fakeId, updates);
        });

        // Delete
        assertThrows(ClientNotFoundException.class, () -> {
            clientService.deleteClient(fakeId);
        });
    }

    // Validate fields -> ClientInvalidDataException (unique email and phone)
    @Test
    void shouldContainUniqueEmailAndPhone() {
        Client firstClient = clientService.getAllClients(fullPageRequest).getContent().get(0);
        // Since Client class is abstract, need a concrete child
        if (firstClient instanceof Person original) {
            Person duplicate = new Person();
            duplicate.setEmail(original.getEmail());
            duplicate.setPhone(firstClient.getPhone());
            assertThrows(ClientInvalidDataException.class, () -> {
                clientService.validateUniquePhoneOrEmail(duplicate);
            });
        }
    }

}
