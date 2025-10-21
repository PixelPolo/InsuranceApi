package com.ricci.insuranceapi.insurance_api.repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import com.ricci.insuranceapi.insurance_api.InsuranceApiApplicationTests;
import com.ricci.insuranceapi.insurance_api.model.Client;
import com.ricci.insuranceapi.insurance_api.model.Contract;

import jakarta.transaction.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

/*
 * This class tests the ContractRepository to verify the communication
 * between Spring Boot, Hibernate, and the PostgreSQL database.
 * Test data is loaded from InsuranceApiApplicationTests parent class.
 * Inspired by Spring Academy materials
 */

@SpringBootTest
@ActiveProfiles("test")
public class ContractRepositoryTest extends InsuranceApiApplicationTests {

    @Autowired
    private ContractRepository contractRepository;

    @Autowired
    private ClientRepository clientRepository;

    // ----------------------
    // --- Read Contracts ---
    // ----------------------

    // Read -> Find All Contracts
    @Test
    @Transactional // Keep the database session open until all related Client are fetched
    void shouldFindAllContracts() {
        List<Contract> contracts = contractRepository.findAll();

        assertThat(contracts).isNotNull().hasSize(3);

        if (VERBOSE) {
            LOGGER.info("Contracts found: {}", contracts);
        }
    }

    // Read -> Find By ID
    @Test
    @Transactional
    void shouldFindContractById() {
        Contract first = contractRepository.findAll().get(0);
        Contract found = contractRepository.findById(first.getContractId()).orElse(null);

        assertThat(found).isNotNull();
        assertThat(found.getClient()).isNotNull();
        assertThat(found.getContractId()).isEqualTo(first.getContractId());
        assertThat(found.getCostAmount()).isEqualByComparingTo(first.getCostAmount());

        if (VERBOSE) {
            LOGGER.info("Found contract by ID: {}", found);
        }
    }

    // -----------------------
    // --- Create Contract ---
    // -----------------------

    // Create -> Insert New Contract
    @Test
    void shouldCreateNewContract() {
        LocalDateTime now = LocalDateTime.now();
        Client client = clientRepository.findAll().get(0);

        Contract contract = new Contract();
        contract.setClient(client);
        contract.setStartDate(now);
        contract.setUpdateDate(now);
        contract.setCostAmount(new BigDecimal(999.99));

        Contract created = contractRepository.save(contract);

        List<Contract> all = contractRepository.findAll();
        assertThat(all).hasSize(4);

        assertThat(created).isNotNull();
        assertThat(created.getClient()).isEqualTo(client);
        assertThat(created.getClient().getClientId()).isEqualTo(client.getClientId());

        assertThat(isSameLocalDateTime(created.getStartDate(), now)).isTrue();
        assertThat(isSameLocalDateTime(created.getUpdateDate(), now)).isTrue();
        assertThat(created.getEndDate()).isNull();

        assertThat(created.getCostAmount()).isEqualTo(contract.getCostAmount());
    }

    // Create -> Insert New Contract without date
    @Test
    void shouldCreateNewContractWithoutDate() {
        LocalDateTime now = LocalDateTime.now();
        Client client = clientRepository.findAll().get(0);

        Contract contract = new Contract();
        contract.setClient(client);
        contract.setCostAmount(new BigDecimal(999.99));

        Contract created = contractRepository.save(contract);

        List<Contract> all = contractRepository.findAll();
        assertThat(all).hasSize(4);

        // REQUIREMENT: should have automatically set the startDate to now
        assertThat(isSameLocalDateTime(created.getStartDate(), now)).isTrue();

        // REQUIREMENT: should have automatically set updateDate to now
        assertThat(isSameLocalDateTime(created.getUpdateDate(), now)).isTrue();
    }

    // -----------------------
    // --- Update Contract ---
    // -----------------------

    // Update -> Modify existing contract cost
    @Test
    void shouldUpdateContract() {
        LocalDateTime now = LocalDateTime.now();
        Contract contract = contractRepository.findAll().get(0);
        BigDecimal newCost = new BigDecimal("1234.56");

        contract.setCostAmount(newCost);
        Contract updated = contractRepository.save(contract);

        assertThat(updated.getCostAmount()).isEqualByComparingTo(newCost);

        // REQUIREMENT: on cost update, should have automatically update the updateDate
        assertThat(updated.getUpdateDate()).isAfter(contract.getStartDate());
        assertThat(isSameLocalDateTime(updated.getUpdateDate(), now)).isTrue();
    }

    // ---------------------
    // --- Delete Contract ---
    // ---------------------

    // Delete -> Soft Delete Contract
    @Test
    void shouldSoftDeleteContract() {
        Contract contract = contractRepository.findAll().get(0);
        LocalDateTime now = LocalDateTime.now();

        contract.setEndDate(now);
        contractRepository.save(contract);

        Contract deleted = contractRepository.findById(contract.getContractId()).orElseThrow();
        assertThat(isSameLocalDateTime(deleted.getEndDate(), now)).isTrue();

        // REQUIREMENT: updateDate is automatically set ONLY on cost update
        assertThat(isSameLocalDateTime(deleted.getUpdateDate(), now)).isFalse();

        if (VERBOSE) {
            LOGGER.info("Contract soft deleted: {}", deleted);
        }
    }

    // ------------------------
    // --- Transient fields ---
    // ------------------------

    // On database fetch (postLoad) -> set previousCostAmount
    @Test
    void shouldInitializePreviousCostAmountOnLoad() {
        Contract contract = contractRepository.findAll().get(0);
        assertThat(contract.getPreviousCostAmount())
                .isEqualByComparingTo(contract.getCostAmount());
    }

    // --------------------------
    // --- Custom Queries ---
    // --------------------------

    // Find only the active contracts for one client
    @Test
    void shouldFindActiveContractsForAlice() {
        // Alice -> endDate NULL → active contract
        Client client = clientRepository.findAll().get(0);
        LocalDateTime now = LocalDateTime.now();

        List<Contract> activeContracts = contractRepository.findActiveByClient(client.getClientId(), now);

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

    // Find zero active contract if endDate is before the given date
    @Test
    void shouldFindZeroActiveContractsForBob() {
        // Bob -> endDate = 2025-12-01
        Client client = clientRepository.findAll().get(1);
        LocalDateTime futureDate = LocalDateTime.of(2026, 1, 1, 0, 0);

        List<Contract> activeContracts = contractRepository.findActiveByClient(client.getClientId(), futureDate);

        assertThat(activeContracts).isNotNull();
        assertThat(activeContracts).isEmpty();

        if (VERBOSE) {
            LOGGER.info("No active contracts for Bob ({}): {}", client.getClientId(), activeContracts);
        }
    }

    // Find active contracts for a client after a given update date
    @Test
    void shouldFindActiveContractsUpdatedAfter() {
        // Alice -> endDate NULL, update_date = 2024-01-15
        Client client = clientRepository.findAll().get(0);
        LocalDateTime beforeUpdateDate = LocalDateTime.of(2023, 1, 1, 0, 0);

        List<Contract> activeContracts = contractRepository.findActiveByClientUpdatedAfter(
                client.getClientId(),
                LocalDateTime.now(),
                beforeUpdateDate);

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

    // Find zero active contract if updatedAfter is in the future
    @Test
    void shouldNotFindActiveContractsAfterFutureDate() {
        // Alice -> endDate NULL, update_date = 2024-01-15
        Client client = clientRepository.findAll().get(0);
        LocalDateTime afterUpdateDate = LocalDateTime.of(2025, 1, 1, 0, 0);

        List<Contract> activeContracts = contractRepository.findActiveByClientUpdatedAfter(
                client.getClientId(),
                LocalDateTime.now(),
                afterUpdateDate);

        assertThat(activeContracts).isNotNull();
        assertThat(activeContracts).isEmpty();

        if (VERBOSE) {
            LOGGER.info("No active contracts for Alice updated after {}: {}", afterUpdateDate, activeContracts);
        }
    }

    // Sum of all active contracts for a client
    @Test
    void shouldSumActiveContractsCostForAlice() {
        // Alice -> one active contract, cost = 400.00
        Client client = clientRepository.findAll().get(0);
        LocalDateTime now = LocalDateTime.now();

        BigDecimal total = contractRepository.sumActiveContractsCost(client.getClientId(), now);

        assertThat(total).isNotNull();
        assertThat(total).isEqualByComparingTo(new BigDecimal("400.00"));

        if (VERBOSE) {
            LOGGER.info("Total active contracts cost for Alice ({}): {}", client.getClientId(), total);
        }
    }

}
