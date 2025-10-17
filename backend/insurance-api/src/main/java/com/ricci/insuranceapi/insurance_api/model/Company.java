package com.ricci.insuranceapi.insurance_api.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "company")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class Company extends Client {

    @Column(name = "company_identifier", unique = true, length = 32)
    private String companyIdentifier;
}
