package com.ricci.insuranceapi.insurance_api.service;

import java.time.LocalDateTime;
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

@Service
public class ClientService {

    // TODO - Improve the service according requirements

    private final ClientRepository clientRepository;

    @Autowired
    public ClientService(ClientRepository clientRepository) {
        this.clientRepository = clientRepository;
    }

    public Page<Client> getAllClients(Pageable pageable) {
        return clientRepository.findAll(pageable);
    }

    public Client getClient(UUID id) {
        return clientRepository.findById(id).orElseThrow(() -> new ClientNotFoundException(id));
    }

    public Client partialUpdate(UUID id, ClientPatchDto updates) {
        Client existing = this.getClient(id);

        // All fields could be patched, except
        // clientId, birthdate or company_identifier
        if (updates.getName() != null)
            existing.setName(updates.getName());
        if (updates.getEmail() != null)
            existing.setEmail(updates.getEmail());
        if (updates.getPhone() != null)
            existing.setPhone(updates.getPhone());
        if (updates.getIsDeleted() != null)
            existing.setIsDeleted(updates.getIsDeleted());
        if (updates.getDeletionDate() != null)
            existing.setDeletionDate(updates.getDeletionDate());

        return clientRepository.save(existing);
    }

    public Client deleteClient(UUID id) {
        // Soft delete for archives
        Client client = this.getClient(id);
        if (client.getIsDeleted() == true) {
            return client;
        } else {
            client.setIsDeleted(true);
            client.setDeletionDate(LocalDateTime.now());
            return clientRepository.save(client);
            // TODO - Update end date of client's contracts
        }
    }

    protected void validateCommonFields(Client client) {
        // Helper for POST on concrete childs class
        if (clientRepository.existsByPhone(client.getPhone())) {
            throw new ClientInvalidDataException("Phone already exists");
        }
        if (clientRepository.existsByEmail(client.getEmail())) {
            throw new ClientInvalidDataException("Email already exists");
        }
    }

}
