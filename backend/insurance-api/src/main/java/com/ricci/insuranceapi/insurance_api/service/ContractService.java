package com.ricci.insuranceapi.insurance_api.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ricci.insuranceapi.insurance_api.dto.ContractDto;
import com.ricci.insuranceapi.insurance_api.dto.ContractPatchDto;
import com.ricci.insuranceapi.insurance_api.exception.ContractNotFoundException;
import com.ricci.insuranceapi.insurance_api.mapper.ContractMapper;
import com.ricci.insuranceapi.insurance_api.model.Client;
import com.ricci.insuranceapi.insurance_api.model.Contract;
import com.ricci.insuranceapi.insurance_api.repository.ContractRepository;

@Service
public class ContractService {

    private final ContractRepository contractRepository;
    private final ContractMapper contractMapper;

    @Autowired
    public ContractService(
            ContractRepository contractRepository,
            ContractMapper contractMapper) {
        this.contractRepository = contractRepository;
        this.contractMapper = contractMapper;
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
    // --- Create contracts ---
    // ------------------------

    @Transactional
    public Contract createContract(ContractDto dto, Client client) {
        Contract contract = contractMapper.toEntity(dto, client);
        return contractRepository.save(contract);
    }

    // ------------------------
    // --- Update contracts ---
    // ------------------------

    @Transactional
    public Contract partialUpdate(UUID id, ContractPatchDto update) {
        Contract existing = this.getContract(id);
        Contract patchEntity = contractMapper.toEntityFromUpdate(update);

        if (patchEntity.getCostAmount() != null)
            existing.setCostAmount(patchEntity.getCostAmount());
        if (patchEntity.getEndDate() != null)
            existing.setEndDate(patchEntity.getEndDate());

        return contractRepository.save(existing);
    }

    // ------------------------
    // --- Delete contracts ---
    // ------------------------

    @Transactional
    public Contract deleteContract(UUID id) {
        // Soft delete according to requirements
        Contract contract = this.getContract(id);
        // If the end date is not null (could be in the future) -> Do nothing
        if (contract.getEndDate() != null) {
            return contract;
        } else {
            contract.setEndDate(LocalDateTime.now());
            return contractRepository.save(contract);
        }
    }

    @Transactional
    public Contract forceCloseContract(UUID id) {
        // Replace any end date (also if not null) to now
        Contract contract = this.getContract(id);
        LocalDateTime now = LocalDateTime.now();
        contract.setEndDate(now);
        return contractRepository.save(contract);
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
