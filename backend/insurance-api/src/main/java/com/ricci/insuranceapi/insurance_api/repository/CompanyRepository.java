package com.ricci.insuranceapi.insurance_api.repository;

import com.ricci.insuranceapi.insurance_api.model.Company;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.UUID;

@Repository
public interface CompanyRepository extends JpaRepository<Company, UUID> {

    boolean existsByCompanyIdentifier(String companyIdentifier);
}
