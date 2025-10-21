package com.ricci.insuranceapi.insurance_api.repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import com.ricci.insuranceapi.insurance_api.model.Contract;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ContractRepository extends JpaRepository<Contract, UUID> {

        // REQUIREMENT: Get only the active contracts for one client
        @Query("""
                        SELECT c FROM Contract c
                        WHERE c.client.clientId = :clientId
                        AND (c.endDate IS NULL OR :currentDate < c.endDate)
                        """)
        List<Contract> findActiveByClient(
                        @Param("clientId") UUID clientId,
                        @Param("currentDate") LocalDateTime currentDate);

        // REQUIREMENT: Possibility to filter by the update date
        // (Get the active contracts after an updated date)
        @Query("""
                        SELECT c FROM Contract c
                        WHERE c.client.clientId = :clientId
                        AND (c.endDate IS NULL OR :currentDate < c.endDate)
                        AND c.updateDate >= :updatedAfter
                        """)
        List<Contract> findActiveByClientUpdatedAfter(
                        @Param("clientId") UUID clientId,
                        @Param("currentDate") LocalDateTime currentDate,
                        @Param("updatedAfter") LocalDateTime updatedAfter);

        // REQUIREMENT: Sum of all the cost amount of the active for one client
        // (Coalesce returns the first non null value betweeen the sum and 0)
        @Query("""
                        SELECT COALESCE(SUM(c.costAmount), 0)
                        FROM Contract c
                        WHERE c.client.clientId = :clientId
                        AND (c.endDate IS NULL OR :currentDate < c.endDate)
                        """)
        BigDecimal sumActiveContractsCost(
                        @Param("clientId") UUID clientId,
                        @Param("currentDate") LocalDateTime currentDate);

}