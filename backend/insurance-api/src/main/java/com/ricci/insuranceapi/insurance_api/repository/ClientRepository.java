// https://medium.com/codex/differences-between-jparepository-and-crudrepository-and-when-you-need-to-chose-each-8d327611818d

package com.ricci.insuranceapi.insurance_api.repository;

import com.ricci.insuranceapi.insurance_api.model.Client;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ClientRepository extends JpaRepository<Client, UUID> {
}
