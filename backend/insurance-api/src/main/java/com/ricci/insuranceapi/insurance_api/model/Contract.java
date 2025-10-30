// https://docs.hibernate.org/orm/current/userguide/html_single/#associations-many-to-one
// https://docs.hibernate.org/orm/current/userguide/html_single/#fetching-fetch-annotation
// https://medium.com/@aedemirsen/hibernate-prepersist-and-preupdate-4a3599d244ec

package com.ricci.insuranceapi.insurance_api.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PostLoad;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = "client")
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Table(name = "contract")
public class Contract {

    @Id
    @GeneratedValue
    @EqualsAndHashCode.Include
    @Column(name = "contract_id", updatable = false, nullable = false)
    private UUID contractId;

    @ManyToOne(fetch = FetchType.LAZY) // SQL executed only if contractObj.getClient() is called
    @JoinColumn(name = "client_id", nullable = false, foreignKey = @ForeignKey(name = "fk_contract_client_id"))
    private Client client;

    @Column(name = "start_date", nullable = false)
    private LocalDateTime startDate;

    @Column(name = "end_date")
    private LocalDateTime endDate;

    @Column(name = "update_date", nullable = false)
    private LocalDateTime updateDate;

    @Column(name = "cost_amount", nullable = false)
    private BigDecimal costAmount;

    @Transient
    private BigDecimal previousCostAmount;

    // Before contract creation
    @PrePersist
    public void prePersist() {
        LocalDateTime now = LocalDateTime.now();
        startDate = (startDate == null) ? now : startDate;
        updateDate = now;
    }

    // On each database fetch
    @PostLoad
    public void postLoad() {
        this.previousCostAmount = this.costAmount;
    }

    // Before each update
    @PreUpdate
    public void preUpdate() {
        if (!previousCostAmount.equals(costAmount)) {
            updateDate = LocalDateTime.now();
        }
    }

}
