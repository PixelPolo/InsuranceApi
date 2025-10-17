package com.ricci.insuranceapi.insurance_api.model;

import java.time.LocalDate;

import com.ricci.insuranceapi.insurance_api.dto.PersonDto;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.EqualsAndHashCode;

@Entity
@Table(name = "person")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class Person extends Client {

    @Column()
    private LocalDate birthdate;

    public Person(PersonDto dto) {
        super(dto);
        this.birthdate = dto.getBirthdate();
    }

}
