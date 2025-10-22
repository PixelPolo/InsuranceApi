package com.ricci.insuranceapi.insurance_api.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;

import com.ricci.insuranceapi.insurance_api.InsuranceApiApplicationTests;
import com.ricci.insuranceapi.insurance_api.dto.ContractDto;
import com.ricci.insuranceapi.insurance_api.dto.ContractPatchDto;
import com.ricci.insuranceapi.insurance_api.exception.ContractNotFoundException;
import com.ricci.insuranceapi.insurance_api.model.Client;
import com.ricci.insuranceapi.insurance_api.model.Contract;
import com.ricci.insuranceapi.insurance_api.repository.ClientRepository;

import jakarta.transaction.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/*
 * This class performs integration tests on the ContractService.
 * It verifies the business logic interacting with the repository layer 
 * and ensures data consistency through real database operations.
 * Test data is loaded from the InsuranceApiApplicationTests parent class.
 * Inspired by Spring Academy materials.
 */

@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class ContractServiceTest extends InsuranceApiApplicationTests {

    @Autowired
    private ContractService contractService;

    @Autowired
    private ClientRepository clientRepository;

    private final PageRequest fullPageRequest = PageRequest.of(0, 10);
    private PageRequest firstPageSizeOne = PageRequest.of(0, 1);

    // ----------------------
    // --- Read contracts ---
    // ----------------------

    // Read -> Find All Contracts
    @Test
    void shouldFindAllContracts() {
        Page<Contract> contracts = contractService.getAllContracts(fullPageRequest);

        assertThat(contracts.getContent()).isNotNull().hasSize(3);

        if (VERBOSE) {
            LOGGER.info("Contracts found: {}", contracts.getContent());
        }
    }

    // Read -> Find By ID
    @Test
    void shouldFindContractById() {
        Contract first = contractService.getAllContracts(fullPageRequest).getContent().get(0);
        Contract found = contractService.getContract(first.getContractId());

        assertThat(found).isNotNull();
        assertThat(found.getCostAmount()).isEqualTo(first.getCostAmount());
        assertThat(found.getClient()).isInstanceOf(Client.class);

        if (VERBOSE) {
            LOGGER.info("Found contract by ID: {}", found);
        }
    }

    // Read -> Pagination
    @Test
    void shouldFindFirstContractWithPagination() {
        Page<Contract> page = contractService.getAllContracts(firstPageSizeOne);

        assertThat(page.getContent()).hasSize(1);
        assertThat(page.getTotalElements()).isEqualTo(3);

        Contract first = page.getContent().get(0);
        assertThat(first).isNotNull();

        assertThrows(IndexOutOfBoundsException.class, () -> {
            page.getContent().get(1);
        });

        if (VERBOSE) {
            LOGGER.info("Page 0 size 1 returned contract: {}", first);
        }
    }

    // ------------------------
    // --- Create contracts ---
    // ------------------------

    // Create -> Create a Contract
    @Test
    void shouldCreateContract() {
        Client client = clientRepository.findAll().get(0);

        ContractDto dto = new ContractDto();
        dto.setCostAmount(new BigDecimal("123.45"));

        LocalDateTime now = LocalDateTime.now();
        Contract saved = contractService.createContract(dto, client);

        assertThat(saved).isNotNull();
        assertThat(saved.getContractId()).isNotNull();
        assertThat(saved.getClient()).isEqualTo(client);
        assertThat(saved.getCostAmount()).isEqualByComparingTo(new BigDecimal("123.45"));
        assertThat(isSameLocalDateTime(saved.getStartDate(), now)).isTrue();
        assertThat(isSameLocalDateTime(saved.getUpdateDate(), now)).isTrue();
        assertThat(saved.getEndDate()).isNull();

        if (VERBOSE) {
            LOGGER.info("Contract created successfully: {}", saved);
        }
    }

    // ------------------------
    // --- Update contracts ---
    // ------------------------

    // Update -> Partial update
    @Test
    void shouldPartiallyUpdateContract() {
        ContractPatchDto update = new ContractPatchDto();
        update.setCostAmount(new BigDecimal("1"));

        Contract firstContract = contractService.getAllContracts(fullPageRequest).getContent().get(0);
        BigDecimal oldCost = firstContract.getCostAmount();

        Contract firstContractUpdated = contractService.partialUpdate(firstContract.getContractId(), update);

        assertThat(firstContractUpdated.getContractId()).isEqualTo(firstContract.getContractId());
        assertThat(firstContractUpdated.getCostAmount()).isNotEqualByComparingTo(oldCost);
        assertThat(firstContractUpdated.getCostAmount()).isEqualByComparingTo(new BigDecimal("1"));

        if (VERBOSE) {
            LOGGER.info("Contract partiellement mis à jour : ancien coût={}, nouveau coût={}",
                    oldCost, firstContractUpdated.getCostAmount());
        }
    }

    // ------------------------
    // --- Delete contracts ---
    // ------------------------

    // Delete -> Soft Delete Contract
    @Test
    void shouldSoftDeleteContract() {
        Contract firstContract = contractService.getAllContracts(fullPageRequest).getContent().get(0);
        assertThat(firstContract.getEndDate()).isNull();

        LocalDateTime now = LocalDateTime.now();
        Contract deletedContract = contractService.deleteContract(firstContract.getContractId());

        assertThat(deletedContract.getEndDate()).isNotNull();
        assertThat(isSameLocalDateTime(deletedContract.getEndDate(), now)).isTrue();
        assertThat(firstContract.getContractId()).isEqualTo(deletedContract.getContractId());

        if (VERBOSE) {
            LOGGER.info("Contract soft deleted: {}", deletedContract);
        }
    }

    // ------------------
    // --- Exceptions ---
    // ------------------

    // Read, Patch, Delete -> ContractNotFoundException
    @Test
    void shouldThrowClientNotFoundException() {
        UUID fakeId = UUID.randomUUID();

        // Read
        assertThrows(ContractNotFoundException.class, () -> {
            contractService.getContract(fakeId);
        });

        // Patch
        ContractPatchDto update = new ContractPatchDto();
        assertThrows(ContractNotFoundException.class, () -> {
            contractService.partialUpdate(fakeId, update);
        });

        // Delete
        assertThrows(ContractNotFoundException.class, () -> {
            contractService.deleteContract(fakeId);
        });
    }

    // -----------------------
    // --- Custom services ---
    // -----------------------

    // Read -> Only the active contracts for one client
    @Test
    void shouldFindActiveContracts() {
        // Alice -> endDate NULL → active contract
        Client client = clientRepository.findAll().get(0);
        LocalDateTime now = LocalDateTime.now();

        List<Contract> activeContracts = contractService.getActiveContracts(client.getClientId());

        assertThat(activeContracts).isNotNull();
        assertThat(activeContracts.size()).isGreaterThan(0);

        for (Contract c : activeContracts) {
            if (c.getEndDate() != null) {
                assertThat(c.getEndDate()).isAfter(now);
            }
        }

        if (VERBOSE) {
            LOGGER.info("Active contracts for Alice ({}): {}", client.getClientId(), activeContracts);
        }
    }

    // Read -> Only the active contracts for a client after a given update date
    @Test
    void shouldFindActiveContractsUpdatedAfter() {
        // Alice -> endDate NULL, update_date = 2024-01-15
        Client client = clientRepository.findAll().get(0);
        LocalDateTime beforeUpdateDate = LocalDateTime.of(2023, 1, 1, 0, 0);

        List<Contract> activeContracts = contractService.getActiveContractsUpdatedAfter(
                client.getClientId(), beforeUpdateDate);

        assertThat(activeContracts).isNotNull();
        assertThat(activeContracts.size()).isGreaterThan(0);

        for (Contract c : activeContracts) {
            if (c.getEndDate() != null) {
                assertThat(c.getEndDate()).isAfter(LocalDateTime.now());
            }
            assertThat(c.getUpdateDate()).isAfter(beforeUpdateDate);
        }

        if (VERBOSE) {
            LOGGER.info("Active contracts for Alice after {}: {}", beforeUpdateDate, activeContracts);
        }
    }

    // Read -> zero active contract if updatedAfter is in the future
    @Test
    void shouldNotFindActiveContractsAfterFutureDate() {
        // Alice -> endDate NULL, update_date = 2024-01-15
        Client client = clientRepository.findAll().get(0);
        LocalDateTime afterUpdateDate = LocalDateTime.of(2025, 1, 1, 0, 0);

        List<Contract> activeContracts = contractService.getActiveContractsUpdatedAfter(
                client.getClientId(),
                afterUpdateDate);

        assertThat(activeContracts).isNotNull();
        assertThat(activeContracts).isEmpty();

        if (VERBOSE) {
            LOGGER.info("No active contracts for Alice updated after {}: {}", afterUpdateDate, activeContracts);
        }
    }

    // ----------------------------
    // --- Sum of contract cost ---
    // ----------------------------

    // Sum of all active contracts for a client
    @Test
    void shouldFindSumOfAllActiveContracts() {
        Client client = clientRepository.findAll().get(0);

        BigDecimal total = contractService.getSumOfActiveContractsCost(client.getClientId());

        assertThat(total).isEqualByComparingTo(new BigDecimal("400"));

        if (VERBOSE) {
            LOGGER.info("Total active contracts cost for Alice ({}): {}", client.getClientId(), total);
        }
    }

}
