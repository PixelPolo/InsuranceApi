package com.ricci.insuranceapi.insurance_api.service;

import com.ricci.insuranceapi.insurance_api.model.Company;
import com.ricci.insuranceapi.insurance_api.repository.CompanyRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class CompanyService {

    private final CompanyRepository companyRepository;

    @Autowired
    public CompanyService(CompanyRepository repository) {
        this.companyRepository = repository;
    }

    public Company createCompany(Company company) {
        return companyRepository.save(company);
    }

    // --- EXTRA ---

    public Page<Company> getAllCompanies(Pageable pageable) {
        return companyRepository.findAll(pageable);
    }
}
