package com.ricci.insuranceapi.insurance_api.service;

import com.ricci.insuranceapi.insurance_api.dto.CompanyDto;
import com.ricci.insuranceapi.insurance_api.exception.ClientInvalidDataException;
import com.ricci.insuranceapi.insurance_api.model.Company;
import com.ricci.insuranceapi.insurance_api.repository.CompanyRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class CompanyService {

    private final CompanyRepository companyRepository;
    private final ClientService clientService;

    @Autowired
    public CompanyService(CompanyRepository companyRepository, ClientService clientService) {
        this.companyRepository = companyRepository;
        this.clientService = clientService;
    }

    // ----------------------
    // --- Read companies ---
    // ----------------------

    public Page<Company> getAllCompanies(Pageable pageable) {
        return companyRepository.findAll(pageable);
    }

    // ----------------------
    // --- Create company ---
    // ----------------------

    public Company createCompany(CompanyDto dto) {
        Company company = new Company(dto);
        checkUniqueCompanyIdentifier(company.getCompanyIdentifier());
        clientService.validateUniquePhoneOrEmail(company);
        return companyRepository.save(company);
    }

    // ------------------
    // --- Exceptions ---
    // ------------------

    private void checkUniqueCompanyIdentifier(String id) {
        if (companyRepository.existsByCompanyIdentifier(id)) {
            throw new ClientInvalidDataException("Company identifier already exists");
        }
    }

}
