package com.ricci.insuranceapi.insurance_api.repository;

import com.ricci.insuranceapi.insurance_api.model.Person;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.UUID;

@Repository
public interface PersonRepository extends JpaRepository<Person, UUID> {
}
