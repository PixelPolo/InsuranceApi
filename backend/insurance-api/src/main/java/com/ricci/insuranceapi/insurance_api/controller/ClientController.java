package com.ricci.insuranceapi.insurance_api.controller;

import com.ricci.insuranceapi.insurance_api.dto.ClientDto;
import com.ricci.insuranceapi.insurance_api.dto.ClientPatchDto;
import com.ricci.insuranceapi.insurance_api.dto.ContractGetDto;
import com.ricci.insuranceapi.insurance_api.mapper.ClientMapper;
import com.ricci.insuranceapi.insurance_api.mapper.ContractMapper;
import com.ricci.insuranceapi.insurance_api.model.Client;
import com.ricci.insuranceapi.insurance_api.model.Contract;
import com.ricci.insuranceapi.insurance_api.service.ClientService;
import com.ricci.insuranceapi.insurance_api.service.ContractService;
import com.ricci.insuranceapi.insurance_api.utils.PaginationUtils;

import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/${api.version}/clients")
public class ClientController {

    private final ClientService clientService;
    private final ClientMapper clientMapper;
    private final ContractService contractService;
    private final ContractMapper contractMapper;
    private final PaginationUtils paginationUtils;

    @Autowired
    public ClientController(
            ClientService clientService,
            ClientMapper clientMapper,
            ContractService contractService,
            ContractMapper contractMapper,
            PaginationUtils paginationUtils) {
        this.clientService = clientService;
        this.clientMapper = clientMapper;
        this.contractService = contractService;
        this.paginationUtils = paginationUtils;
        this.contractMapper = contractMapper;
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
            return ResponseEntity.ok(clientMapper.toDtos(clients.getContent())); // 200 OK
        }
    }

    // GET /api/v_/clients/{id}
    @GetMapping("/{id}")
    public ResponseEntity<ClientDto> getClient(@PathVariable UUID id) {
        Client client = clientService.getClient(id); // 404 Not Found → GlobalExceptionHandler
        return ResponseEntity.ok(clientMapper.toDto(client)); // 200 OK
    }

    // PATCH /api/v_/clients/{id}
    @PatchMapping("/{id}")
    public ResponseEntity<ClientDto> patchClient(@PathVariable UUID id, @Valid @RequestBody ClientPatchDto update) {
        Client updated = clientService.partialUpdate(id, update); // 404 Not Found → GlobalExceptionHandler
        return ResponseEntity.ok(clientMapper.toDto(updated)); // 200 OK
    }

    // DELETE /api/v_/clients/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<ClientDto> deleteClient(@PathVariable UUID id) {
        Client deleted = clientService.deleteClient(id); // 404 Not Found → GlobalExceptionHandler
        return ResponseEntity.ok(clientMapper.toDto(deleted)); // 200 OK with client (soft delete)
    }

    // -----------------------------------------------
    // --- Custom routes according to requirements ---
    // -----------------------------------------------

    // GET /api/v_/clients/{id}/contracts
    @GetMapping("/{id}/contracts")
    public ResponseEntity<List<ContractGetDto>> getContractsByClient(@PathVariable UUID id) {
        List<Contract> contracts = contractService.getActiveContracts(id);
        if (contracts.isEmpty()) {
            return ResponseEntity.noContent().build(); // 204 No Content
        }
        return ResponseEntity.ok(contractMapper.toContractGetDtoList(contracts)); // 200 OK
    }

    // GET /api/v_/clients/{id}/contracts/costsum
    @GetMapping("/{id}/contracts/costsum")
    public ResponseEntity<BigDecimal> getContractsSummary(@PathVariable UUID id) {
        BigDecimal sum = contractService.getSumOfActiveContractsCost(id);
        return ResponseEntity.ok(sum); // 200 OK
    }

    // GET /api/v_/clients/{id}/contracts/after?date=2025-01-01T00:00:00
    @GetMapping("/{id}/contracts/after")
    public ResponseEntity<List<ContractGetDto>> getContractsUpdatedAfter(
            @PathVariable UUID id,
            @RequestParam("date") LocalDateTime updatedAfter) {
        List<Contract> contracts = contractService.getActiveContractsUpdatedAfter(id, updatedAfter);
        if (contracts.isEmpty()) {
            return ResponseEntity.noContent().build(); // 204 No Content
        }
        return ResponseEntity.ok(contractMapper.toContractGetDtoList(contracts)); // 200 OK
    }

}
