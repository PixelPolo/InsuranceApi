package com.ricci.insuranceapi.insurance_api.controller;

import com.ricci.insuranceapi.insurance_api.model.Company;
import com.ricci.insuranceapi.insurance_api.service.CompanyService;

import java.net.URI;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/${api.version}/clients/companies")
public class CompanyController {

    // TODO - Improve error codes and validation with DTOs

    private final CompanyService companyService;

    @Autowired
    public CompanyController(CompanyService companyService) {
        this.companyService = companyService;
    }

    // POST /api/v_/clients/companies
    @PostMapping
    public ResponseEntity<Company> createCompany(@RequestBody Company company) {
        Company created = companyService.createCompany(company);
        String apiVersion = System.getProperty("api.version", "v1");
        // We get the client only with /clients/{id} and not /clients/companies/{id}
        URI location = URI.create("/api/" + apiVersion + "/clients/" + created.getClientId());
        return ResponseEntity.created(location).body(created); // 201 Created
    }

    // --- EXTRA ---

    // GET /api/v_/clients/companies?page=0&size=5&sortBy=name&sortDir=asc
    @GetMapping
    public ResponseEntity<List<Company>> getAllCompanies(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "name") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {

        Sort sort = "desc".equalsIgnoreCase(sortDir)
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();

        PageRequest pageRequest = PageRequest.of(page, size, sort);
        Page<Company> companies = companyService.getAllCompanies(pageRequest);

        return companies.isEmpty()
                ? ResponseEntity.noContent().build() // 204 No Content
                : ResponseEntity.ok(companies.getContent()); // 200 OK
    }
}
