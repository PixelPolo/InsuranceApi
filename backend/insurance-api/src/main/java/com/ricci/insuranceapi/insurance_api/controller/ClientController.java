package com.ricci.insuranceapi.insurance_api.controller;

import com.ricci.insuranceapi.insurance_api.dto.ClientDto;
import com.ricci.insuranceapi.insurance_api.dto.ClientDtoFactory;
import com.ricci.insuranceapi.insurance_api.dto.ClientPatchDto;
import com.ricci.insuranceapi.insurance_api.model.Client;
import com.ricci.insuranceapi.insurance_api.service.ClientService;
import com.ricci.insuranceapi.insurance_api.utils.PaginationUtils;

import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/${api.version}/clients")
public class ClientController {

    private final ClientService clientService;
    private final PaginationUtils paginationUtils;

    @Autowired
    public ClientController(ClientService clientService, PaginationUtils paginationUtils) {
        this.clientService = clientService;
        this.paginationUtils = paginationUtils;
    }

    // GET /api/v_/clients?page=0&size=5&sortBy=name&sortDir=asc
    @GetMapping
    public ResponseEntity<List<ClientDto>> getAllClients(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "name") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {

        PageRequest pageRequest = this.paginationUtils.buildPageRequest(page, size, sortBy, sortDir);
        Page<Client> clients = clientService.getAllClients(pageRequest);

        if (clients.isEmpty()) {
            return ResponseEntity.noContent().build(); // 204 No Content
        } else {
            return ResponseEntity.ok(ClientDtoFactory.fromClients(clients.getContent())); // 200 OK
        }
    }

    // GET /api/v_/clients/{id}
    @GetMapping("/{id}")
    public ResponseEntity<ClientDto> getClient(@PathVariable UUID id) {
        Client client = clientService.getClient(id); // 404 Not Found → ClientNotFoundAdvice
        return ResponseEntity.ok(ClientDtoFactory.fromClient(client)); // 200 OK
    }

    // PATCH /api/v_/clients/{id}
    @PatchMapping("/{id}")
    public ResponseEntity<ClientDto> patchClient(@PathVariable UUID id, @Valid @RequestBody ClientPatchDto updates) {
        Client updated = clientService.partialUpdate(id, updates); // 404 Not Found → ClientNotFoundAdvice
        return ResponseEntity.ok(ClientDtoFactory.fromClient(updated)); // 200 OK
    }

    // DELETE /api/v_/clients/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<ClientDto> deleteClient(@PathVariable UUID id) {
        Client deleted = clientService.deleteClient(id); // 404 Not Found → ClientNotFoundAdvice
        return ResponseEntity.ok(ClientDtoFactory.fromClient(deleted)); // 200 OK with client (soft delete)
    }
}
