package com.ricci.insuranceapi.insurance_api.repository;

import java.util.UUID;
import com.ricci.insuranceapi.insurance_api.model.Contract;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ContractRepository extends JpaRepository<Contract, UUID> {
}