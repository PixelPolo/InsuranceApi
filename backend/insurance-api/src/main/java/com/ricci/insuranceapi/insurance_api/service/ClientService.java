package com.ricci.insuranceapi.insurance_api.service;

import java.time.LocalDate;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.ricci.insuranceapi.insurance_api.exception.ClientNotFoundException;
import com.ricci.insuranceapi.insurance_api.model.Client;
import com.ricci.insuranceapi.insurance_api.repository.ClientRepository;

@Service
public class ClientService {

    // TODO - Improve the service according requirements

    private final ClientRepository clientRepository;

    @Autowired
    public ClientService(ClientRepository clientRepository) {
        this.clientRepository = clientRepository;
    }

    public void validateCommonFields(Client client) {
        if (clientRepository.existsByPhone(client.getPhone())) {
            throw new IllegalArgumentException("Phone already exists");
        }
        if (clientRepository.existsByEmail(client.getEmail())) {
            throw new IllegalArgumentException("Email already exists");
        }
    }

    public Page<Client> getAllClients(Pageable pageable) {
        return clientRepository.findAll(pageable);
    }

    public Client getClient(UUID id) {
        return clientRepository.findById(id)
                .orElseThrow(() -> new ClientNotFoundException(id));
    }

    // PATCH method -> partial update
    public Client partialUpdate(UUID id, Map<String, Object> updates) {
        Client existing = this.getClient(id);

        if (updates.containsKey("name"))
            existing.setName((String) updates.get("name"));
        if (updates.containsKey("email"))
            existing.setEmail((String) updates.get("email"));
        if (updates.containsKey("phone"))
            existing.setPhone((String) updates.get("phone"));

        // ignore birthdate / companyIdentifier even if present
        return clientRepository.save(existing);
    }

    // Soft delete for archives
    public Client deleteClient(UUID id) {
        Client client = this.getClient(id);

        if (client.getIsDeleted() == true) {
            return client;
        }

        client.setIsDeleted(true);
        client.setDeletionDate(LocalDate.now());
        // TODO - Update the end date of the client's contracts

        return clientRepository.save(client);
    }

    /*
     * Since Client as an abstract class
     * POST are on /clients/persons or /clients/companies
     * instead on /clients
     */

    // public Client createClient(Client client) {
    // return clientRepository.save(client);
    // }

    /*
     * PUT was replaced by PATCH
     * Due to partial update logic according requierements:
     * We do not update birthdate or company_identifier
     */

    // public Client updateClient(UUID id, Client updates) {
    // Client existing = this.getClient(id);
    // existing.setName(updates.getName());
    // existing.setEmail(updates.getEmail());
    // existing.setPhone(updates.getPhone());
    // return clientRepository.save(existing);
    // }
}
