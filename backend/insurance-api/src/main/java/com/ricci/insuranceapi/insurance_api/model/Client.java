// https://docs.hibernate.org/orm/current/userguide/html_single/
// https://docs.hibernate.org/orm/current/userguide/html_single/#entity-inheritance-joined-table
// https://medium.com/devdomain/using-lombok-in-spring-boot-simplifying-your-code-c38057894cb8

package com.ricci.insuranceapi.insurance_api.model;

import java.time.LocalDateTime;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.Table;
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
@ToString
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Table(name = "client")
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class Client {

    @Id
    @GeneratedValue
    @EqualsAndHashCode.Include
    @Column(name = "client_id", updatable = false, nullable = false)
    private UUID clientId;

    @Column(length = 16, unique = true)
    private String phone;

    @Column(length = 128, unique = true)
    private String email;

    @Column(length = 64)
    private String name;

    @Column(name = "is_deleted", nullable = false)
    private Boolean isDeleted = false;

    @Column(name = "deletion_date")
    private LocalDateTime deletionDate;

}
