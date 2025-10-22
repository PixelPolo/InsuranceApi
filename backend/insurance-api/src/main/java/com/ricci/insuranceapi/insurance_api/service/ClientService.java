package com.ricci.insuranceapi.insurance_api.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.ricci.insuranceapi.insurance_api.dto.ClientPatchDto;
import com.ricci.insuranceapi.insurance_api.exception.ClientInvalidDataException;
import com.ricci.insuranceapi.insurance_api.exception.ClientNotFoundException;
import com.ricci.insuranceapi.insurance_api.repository.ClientRepository;
import com.ricci.insuranceapi.insurance_api.model.Client;
import com.ricci.insuranceapi.insurance_api.model.Contract;

@Service
public class ClientService {

    private final ClientRepository clientRepository;
    private final ContractService contractService;

    @Autowired
    public ClientService(ClientRepository clientRepository, ContractService contractService) {
        this.clientRepository = clientRepository;
        this.contractService = contractService;
    }

    // --------------------
    // --- Read clients ---
    // --------------------

    public Page<Client> getAllClients(Pageable pageable) {
        return clientRepository.findAll(pageable);
    }

    public Client getClient(UUID id) {
        return clientRepository.findById(id).orElseThrow(() -> new ClientNotFoundException(id));
    }

    // ----------------------
    // --- Update clients ---
    // ----------------------

    public Client partialUpdate(UUID id, ClientPatchDto update) {
        Client existing = this.getClient(id);

        // All fields could be patched, except
        // clientId, birthdate or company_identifier
        if (update.getName() != null)
            existing.setName(update.getName());
        if (update.getEmail() != null)
            existing.setEmail(update.getEmail());
        if (update.getPhone() != null)
            existing.setPhone(update.getPhone());
        if (update.getIsDeleted() != null)
            existing.setIsDeleted(update.getIsDeleted());
        if (update.getDeletionDate() != null)
            existing.setDeletionDate(update.getDeletionDate());

        return clientRepository.save(existing);
    }

    // ----------------------
    // --- Delete clients ---
    // ----------------------

    public Client deleteClient(UUID id) {
        // Soft delete for archives
        Client client = this.getClient(id);
        if (client.getIsDeleted() == true) {
            return client;
        } else {
            client.setIsDeleted(true);
            client.setDeletionDate(LocalDateTime.now());
            closeContracts(id); // REQUIREMENT: Client is deleted -> end date updated for its contracts
            return clientRepository.save(client);
        }
    }

    private void closeContracts(UUID clientId) {
        List<Contract> contracts = contractService.getActiveContracts(clientId);
        for (Contract contract : contracts) {
            contractService.forceCloseContract(contract.getContractId());
        }
    }

    // ------------------
    // --- Exceptions ---
    // ------------------

    // Helper for POST on concrete childs class
    protected void validateUniquePhoneOrEmail(Client client) {
        if (clientRepository.existsByPhone(client.getPhone())) {
            throw new ClientInvalidDataException("Phone already exists");
        }
        if (clientRepository.existsByEmail(client.getEmail())) {
            throw new ClientInvalidDataException("Email already exists");
        }
    }

}
