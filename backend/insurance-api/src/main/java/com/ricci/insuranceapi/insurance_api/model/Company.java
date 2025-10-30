package com.ricci.insuranceapi.insurance_api.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.PrimaryKeyJoinColumn;
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
@EqualsAndHashCode(callSuper = true)
@Table(name = "company")
public class Company extends Client {

    @PrimaryKeyJoinColumn(name = "client_id", foreignKey = @ForeignKey(name = "fk_company_client_id"))

    @Column(name = "company_identifier", length = 32, unique = true)
    private String companyIdentifier;

}
