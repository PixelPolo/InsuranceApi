package com.ricci.insuranceapi.insurance_api.service;

import com.ricci.insuranceapi.insurance_api.model.Company;
import com.ricci.insuranceapi.insurance_api.repository.CompanyRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class CompanyService {

    // TODO - Improve the service according requirements

    private final CompanyRepository companyRepository;
    private final ClientService clientService;

    @Autowired
    public CompanyService(CompanyRepository companyRepository, ClientService clientService) {
        this.companyRepository = companyRepository;
        this.clientService = clientService;
    }

    public Company createCompany(Company company) {
        clientService.validateCommonFields(company);

        if (companyRepository.existsByCompanyIdentifier(company.getCompanyIdentifier())) {
            throw new IllegalArgumentException("Company identifier already exists");
        }

        return companyRepository.save(company);
    }

    // --- EXTRA ---

    public Page<Company> getAllCompanies(Pageable pageable) {
        return companyRepository.findAll(pageable);
    }
}
