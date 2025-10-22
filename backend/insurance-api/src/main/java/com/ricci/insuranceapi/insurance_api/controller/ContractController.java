package com.ricci.insuranceapi.insurance_api.controller;

import java.net.URI;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ricci.insuranceapi.insurance_api.dto.ContractDto;
import com.ricci.insuranceapi.insurance_api.dto.ContractPatchDto;
import com.ricci.insuranceapi.insurance_api.mapper.ContractMapper;
import com.ricci.insuranceapi.insurance_api.model.Client;
import com.ricci.insuranceapi.insurance_api.model.Contract;
import com.ricci.insuranceapi.insurance_api.service.ClientService;
import com.ricci.insuranceapi.insurance_api.service.ContractService;
import com.ricci.insuranceapi.insurance_api.utils.PaginationUtils;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/${api.version}/contracts")
public class ContractController {

    private final ContractService contractService;
    private final ClientService clientService;
    private final PaginationUtils paginationUtils;
    private final ContractMapper contractMapper;

    @Autowired
    public ContractController(
            ContractService contractService,
            ClientService clientService,
            PaginationUtils paginationUtils,
            ContractMapper contractMapper) {
        this.contractService = contractService;
        this.paginationUtils = paginationUtils;
        this.contractMapper = contractMapper;
        this.clientService = clientService;
    }

    // GET /api/v_/contracts?page=0&size=5&sortBy=updateDate&sortDir=asc
    @GetMapping
    public ResponseEntity<List<ContractDto>> getAllContracts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "updateDate") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {

        PageRequest pageRequest = this.paginationUtils.buildPageRequest(page, size, sortBy, sortDir);
        Page<Contract> contracts = contractService.getAllContracts(pageRequest);

        if (contracts.isEmpty()) {
            return ResponseEntity.noContent().build(); // 204 No Content
        } else {
            return ResponseEntity.ok(contractMapper.toDtoList(contracts.getContent())); // 200 OK
        }
    }

    // GET /api/v_/contracts/{id}
    @GetMapping("/{id}")
    public ResponseEntity<ContractDto> getContract(@PathVariable UUID id) {
        Contract contract = contractService.getContract(id); // 404 Not Found -> GlobalExceptionHandler
        return ResponseEntity.ok(contractMapper.toDto(contract)); // 200 OK
    }

    // POST /api/v_/contracts
    @PostMapping
    public ResponseEntity<ContractDto> createContract(@Valid @RequestBody ContractDto dto) {
        Client client = clientService.getClient(dto.getClientId());
        Contract created = contractService.createContract(dto, client);
        String apiVersion = System.getProperty("api.version", "v1");
        URI location = URI.create("/api/" + apiVersion + "/contracts/" + created.getContractId());
        return ResponseEntity.created(location).body(contractMapper.toDto(created)); // 201 Created
    }

    // PATCH /api/v_/contracts/{id}
    @PatchMapping("/{id}")
    public ResponseEntity<ContractDto> patchContract(
            @PathVariable UUID id,
            @Valid @RequestBody ContractPatchDto update) {
        Contract updated = contractService.partialUpdate(id, update); // 404 Not Found -> GlobalExceptionHandler
        return ResponseEntity.ok(contractMapper.toDto(updated));
    }

    // DELETE /api/v_/contracts/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<ContractDto> deleteContract(@PathVariable UUID id) {
        Contract deleted = contractService.deleteContract(id); // 404 Not Found → GlobalExceptionHandler
        return ResponseEntity.ok(contractMapper.toDto(deleted)); // 200 OK
    }

}
