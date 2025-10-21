package com.ricci.insuranceapi.insurance_api.controller;

import com.ricci.insuranceapi.insurance_api.dto.ClientDto;
import com.ricci.insuranceapi.insurance_api.dto.CompanyDto;
import com.ricci.insuranceapi.insurance_api.mapper.ClientMapper;
import com.ricci.insuranceapi.insurance_api.model.Company;
import com.ricci.insuranceapi.insurance_api.service.CompanyService;
import com.ricci.insuranceapi.insurance_api.utils.PaginationUtils;

import jakarta.validation.Valid;

import java.net.URI;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/${api.version}/clients/companies")
public class CompanyController {

    private final CompanyService companyService;
    private final PaginationUtils paginationUtils;
    private final ClientMapper clientMapper;

    @Autowired
    public CompanyController(
            CompanyService companyService,
            PaginationUtils paginationUtils,
            ClientMapper clientMapper) {
        this.companyService = companyService;
        this.paginationUtils = paginationUtils;
        this.clientMapper = clientMapper;
    }

    // POST /api/v_/clients/companies
    @PostMapping
    public ResponseEntity<ClientDto> createCompany(@Valid @RequestBody CompanyDto dto) {
        Company created = companyService.createCompany(dto);
        String apiVersion = System.getProperty("api.version", "v1");
        URI location = URI.create("/api/" + apiVersion + "/clients/" + created.getClientId()); // Get on /clients/{id}
        return ResponseEntity.created(location).body(clientMapper.toDto(created)); // 201 Created
    }

    // GET /api/v_/clients/companies?page=0&size=5&sortBy=name&sortDir=asc
    @GetMapping
    public ResponseEntity<List<ClientDto>> getAllCompanies(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "name") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {

        PageRequest pageRequest = this.paginationUtils.buildPageRequest(page, size, sortBy, sortDir);
        Page<Company> companies = companyService.getAllCompanies(pageRequest);

        if (companies.isEmpty()) {
            return ResponseEntity.noContent().build(); // 204 No Content
        } else {
            return ResponseEntity.ok(clientMapper.toDtos(companies.getContent())); // 200 OK
        }
    }

}
