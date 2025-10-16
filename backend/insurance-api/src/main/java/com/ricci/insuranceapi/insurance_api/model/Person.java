// https://docs.hibernate.org/orm/current/userguide/html_single/#entity-inheritance-joined-table

package com.ricci.insuranceapi.insurance_api.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Entity
@Table(name = "person")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class Person extends Client {

    @Column(nullable = false)
    private LocalDate birthdate;
}
