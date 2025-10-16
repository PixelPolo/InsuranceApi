// https://docs.hibernate.org/orm/current/userguide/html_single/
// https://docs.hibernate.org/orm/current/userguide/html_single/#entity-inheritance-joined-table
// https://medium.com/devdomain/using-lombok-in-spring-boot-simplifying-your-code-c38057894cb8

package com.ricci.insuranceapi.insurance_api.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.UUID;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "client")
@Inheritance(strategy = InheritanceType.JOINED)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Client {

    @Id
    @GeneratedValue
    @Column(name = "client_id", updatable = false, nullable = false)
    private UUID clientId;

    @Column(length = 16)
    private String phone;

    @Column(length = 128, unique = true)
    private String email;

    @Column(length = 64)
    private String name;

    @Column(name = "is_deleted")
    private Boolean isDeleted = false;

    @Column(name = "deletion_date")
    private LocalDate deletionDate;

}
