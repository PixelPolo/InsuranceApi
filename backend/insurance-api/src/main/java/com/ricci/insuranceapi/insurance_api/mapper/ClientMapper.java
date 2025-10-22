package com.ricci.insuranceapi.insurance_api.mapper;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import com.ricci.insuranceapi.insurance_api.dto.ClientDto;
import com.ricci.insuranceapi.insurance_api.dto.CompanyDto;
import com.ricci.insuranceapi.insurance_api.dto.PersonDto;
import com.ricci.insuranceapi.insurance_api.model.Client;
import com.ricci.insuranceapi.insurance_api.model.Company;
import com.ricci.insuranceapi.insurance_api.model.Person;

@Component
public final class ClientMapper {

    // ClientDto -> Client
    public ClientDto toDto(Client client) {
        if (client instanceof Person person) {
            return toPersonDto(person);
        } else if (client instanceof Company company) {
            return toCompanyDto(company);
        }
        throw new IllegalArgumentException("Unknown client type: " + client.getClass());
    }

    // PersonDto -> Person
    private PersonDto toPersonDto(Person person) {
        PersonDto dto = new PersonDto();
        dto.setClientId(person.getClientId());
        dto.setPhone(person.getPhone());
        dto.setEmail(person.getEmail());
        dto.setName(person.getName());
        dto.setIsDeleted(person.getIsDeleted());
        dto.setDeletionDate(person.getDeletionDate());
        dto.setBirthdate(person.getBirthdate());
        return dto;
    }

    // CompanyDto -> Company
    private CompanyDto toCompanyDto(Company company) {
        CompanyDto dto = new CompanyDto();
        dto.setClientId(company.getClientId());
        dto.setPhone(company.getPhone());
        dto.setEmail(company.getEmail());
        dto.setName(company.getName());
        dto.setIsDeleted(company.getIsDeleted());
        dto.setDeletionDate(company.getDeletionDate());
        dto.setCompanyIdentifier(company.getCompanyIdentifier());
        return dto;
    }

    // Client list -> ClientDto list
    public List<ClientDto> toDtos(List<? extends Client> clients) {
        if (clients == null)
            return List.of();
        List<ClientDto> dtos = new ArrayList<>();
        for (Client client : clients) {
            dtos.add(toDto(client));
        }
        return dtos;
    }

    // ClientDto -> Client
    public Client toEntity(ClientDto dto) {
        if (dto == null)
            return null;

        if (dto instanceof PersonDto personDto) {
            return toPersonEntity(personDto);
        } else if (dto instanceof CompanyDto companyDto) {
            return toCompanyEntity(companyDto);
        }
        throw new IllegalArgumentException("Unknown DTO type: " + dto.getClass());
    }

    // PersonDto -> Person
    private Person toPersonEntity(PersonDto dto) {
        Person person = new Person();
        person.setClientId(dto.getClientId());
        person.setPhone(dto.getPhone());
        person.setEmail(dto.getEmail());
        person.setName(dto.getName());
        person.setIsDeleted(dto.getIsDeleted() != null ? dto.getIsDeleted() : false);
        person.setDeletionDate(dto.getDeletionDate());
        person.setBirthdate(dto.getBirthdate());
        return person;
    }

    // CompanyDto -> Company
    private Company toCompanyEntity(CompanyDto dto) {
        Company company = new Company();
        company.setClientId(dto.getClientId());
        company.setPhone(dto.getPhone());
        company.setEmail(dto.getEmail());
        company.setName(dto.getName());
        company.setIsDeleted(dto.getIsDeleted() != null ? dto.getIsDeleted() : false);
        company.setDeletionDate(dto.getDeletionDate());
        company.setCompanyIdentifier(dto.getCompanyIdentifier());
        return company;
    }

    // ClientDto list -> Client list
    public List<Client> toEntities(List<? extends ClientDto> dtos) {
        if (dtos == null)
            return List.of();
        List<Client> entities = new ArrayList<>();
        for (ClientDto dto : dtos) {
            entities.add(toEntity(dto));
        }
        return entities;
    }
}
