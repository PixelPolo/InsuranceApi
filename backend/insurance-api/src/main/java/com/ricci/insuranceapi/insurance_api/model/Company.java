package com.ricci.insuranceapi.insurance_api.model;

import com.ricci.insuranceapi.insurance_api.dto.CompanyDto;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;

@Entity
@Table(name = "company")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class Company extends Client {

    @Column(name = "company_identifier", length = 32, unique = true)
    private String companyIdentifier;

    public Company(CompanyDto dto) {
        super(dto);
        this.companyIdentifier = dto.getCompanyIdentifier();
    }

}
