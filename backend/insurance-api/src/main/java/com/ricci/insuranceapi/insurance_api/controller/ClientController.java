package com.ricci.insuranceapi.insurance_api.controller;

import com.ricci.insuranceapi.insurance_api.model.Client;
import com.ricci.insuranceapi.insurance_api.service.ClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

// import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/${api.version}/clients")
public class ClientController {

    // TODO - Improve error codes and validation with DTOs

    private final ClientService clientService;

    @Autowired
    public ClientController(ClientService clientService) {
        this.clientService = clientService;
    }

    // GET /api/v_/clients?page=0&size=5&sortBy=name&sortDir=asc
    @GetMapping
    public ResponseEntity<List<Client>> getAllClients(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "name") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {

        // If weird values like "desccc" -> fallback to "asc"
        Sort sort = "desc".equalsIgnoreCase(sortDir)
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();

        PageRequest pageRequest = PageRequest.of(page, size, sort);
        Page<Client> clients = clientService.getAllClients(pageRequest);

        return clients.isEmpty()
                ? ResponseEntity.noContent().build() // 204 No Content
                : ResponseEntity.ok(clients.getContent()); // 200 OK
    }

    // GET /api/v_/clients/{id}
    @GetMapping("/{id}")
    public ResponseEntity<Client> getClient(@PathVariable UUID id) {
        Client client = clientService.getClient(id); // 404 Not Found → ClientNotFoundAdvice
        return ResponseEntity.ok(client); // 200 OK
    }

    // PATCH /api/v_/clients/{id}
    @PatchMapping("/{id}")
    public ResponseEntity<Client> patchClient(@PathVariable UUID id, @RequestBody Map<String, Object> updates) {
        Client updated = clientService.partialUpdate(id, updates); // 404 Not Found → ClientNotFoundAdvice
        return ResponseEntity.ok(updated); // 200 OK
    }

    // DELETE /api/v_/clients/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<Client> deleteClient(@PathVariable UUID id) {
        Client deleted = clientService.deleteClient(id); // 404 Not Found → ClientNotFoundAdvice
        // The Client is returned due to the soft delete strategy for archives
        return ResponseEntity.ok(deleted); // 200 OK
    }

    /*
     * Since Client as an abstract class
     * POST are on /clients/persons or /clients/companies
     * instead on /clients
     */

    // // POST /api/v_/clients
    // @PostMapping
    // public ResponseEntity<Client> createClient(@RequestBody Client client) {
    // Client created = clientService.createClient(client);
    // String apiVersion = System.getProperty("api.version", "v1");
    // URI location = URI.create("/api/" + apiVersion + "/clients/" +
    // created.getClientId());
    // return ResponseEntity.created(location).body(created); // 201 Created
    // }

    /*
     * PUT was replaced by PATCH
     * Due to partial update logic according requierements:
     * We do not update birthdate or company_identifier
     */

    // // PUT /api/v_/clients/{id}
    // @PutMapping("/{id}")
    // public ResponseEntity<Client> updateClient(@PathVariable UUID id,
    // @RequestBody Client updates) {
    // Client updated = clientService.updateClient(id, updates); // 404 Not Found →
    // ClientNotFoundAdvice
    // return ResponseEntity.ok(updated); // 200 OK
    // }
}
