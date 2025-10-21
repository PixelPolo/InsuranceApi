package com.ricci.insuranceapi.insurance_api.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.ricci.insuranceapi.insurance_api.dto.ContractDto;
import com.ricci.insuranceapi.insurance_api.exception.ContractNotFoundException;
import com.ricci.insuranceapi.insurance_api.model.Client;
import com.ricci.insuranceapi.insurance_api.model.Contract;
import com.ricci.insuranceapi.insurance_api.repository.ClientRepository;
import com.ricci.insuranceapi.insurance_api.repository.ContractRepository;

@Service
public class ContractService {

    private final ContractRepository contractRepository;
    private final ClientRepository clientRepository;

    @Autowired
    public ContractService(ContractRepository contractRepository, ClientRepository clientRepository) {
        this.contractRepository = contractRepository;
        this.clientRepository = clientRepository;
    }

    // ----------------------
    // --- Read contracts ---
    // ----------------------

    public Page<Contract> getAllContracts(Pageable pageable) {
        return contractRepository.findAll(pageable);
    }

    public Contract getContract(UUID id) {
        return contractRepository.findById(id).orElseThrow(() -> new ContractNotFoundException(id));
    }

    // ------------------------
    // --- Update contracts ---
    // ------------------------

    public Contract partialUpdate(UUID id, ContractDto update) {
        Contract existing = this.getContract(id);

        if (update.getClientId() != null) {
            Optional<Client> client = clientRepository.findById(update.getClientId());
            if (client.isPresent()) {
                existing.setClient(client.get());
            }
        }
        if (update.getCostAmount() != null) {
            existing.setCostAmount(update.getCostAmount());
        }
        if (update.getEndDate() != null) {
            existing.setEndDate(update.getEndDate());
        }

        return contractRepository.save(existing);
    }

    // ------------------------
    // --- Delete contracts ---
    // ------------------------

    public Contract deleteContract(UUID id) {
        // Soft delete according to requirements
        Contract contract = this.getContract(id);
        if (contract.getEndDate() != null) {
            return contract;
        } else {
            contract.setEndDate(LocalDateTime.now());
            return contractRepository.save(contract);
        }
    }

    // -----------------------
    // --- Custom services ---
    // -----------------------

    public List<Contract> getActiveContracts(UUID clientId) {
        return contractRepository.findActiveByClient(clientId, LocalDateTime.now());
    }

    public List<Contract> getActiveContractsUpdatedAfter(UUID clientId, LocalDateTime updatedAfter) {
        return contractRepository.findActiveByClientUpdatedAfter(clientId, LocalDateTime.now(), updatedAfter);
    }

    public BigDecimal getSumOfActiveContractsCost(UUID clientId) {
        return contractRepository.sumActiveContractsCost(clientId, LocalDateTime.now());
    }

}
