package com.ricci.insuranceapi.insurance_api.service;

import java.time.LocalDate;
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

    private final ClientRepository clientRepository;

    @Autowired
    public ClientService(ClientRepository clientRepository) {
        this.clientRepository = clientRepository;
    }

    public Page<Client> getAllClients(Pageable pageable) {
        return clientRepository.findAll(pageable);
    }

    public Client getClient(UUID id) {
        return clientRepository.findById(id)
                .orElseThrow(() -> new ClientNotFoundException(id));
    }

    public Client createClient(Client client) {
        return clientRepository.save(client);
    }

    // PUT method -> set other attributes to null 
    // (not ok with requirements for Person.birthday and Company.companyIdentifier)
    // public Client updateClient(UUID id, Client updates) {
    // Client existing = this.getClient(id);
    // existing.setName(updates.getName());
    // existing.setEmail(updates.getEmail());
    // existing.setPhone(updates.getPhone());
    // return clientRepository.save(existing);
    // }

    // PATCH method -> partial update
    public Client updateClient(UUID id, Client updates) {
        Client existing = this.getClient(id);
        if (updates.getName() != null) {
            existing.setName(updates.getName());
        }
        if (updates.getEmail() != null) {
            existing.setEmail(updates.getEmail());
        }
        if (updates.getPhone() != null) {
            existing.setPhone(updates.getPhone());
        }
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
}
