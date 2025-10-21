package com.ricci.insuranceapi.insurance_api.mapper;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import com.ricci.insuranceapi.insurance_api.dto.*;
import com.ricci.insuranceapi.insurance_api.model.*;

@Component
public final class ClientMapper {

    private ClientMapper() {
    }

    // Client -> ClientDto
    public ClientDto toDto(Client client) {
        if (client instanceof Person person) {
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
        if (client instanceof Company company) {
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
        throw new IllegalArgumentException("Unknown client type: " + client.getClass());
    }

    public List<ClientDto> toDtos(List<? extends Client> clients) {
        List<ClientDto> dtos = new ArrayList<>();
        for (Client client : clients) {
            dtos.add(toDto(client));
        }
        return dtos;
    }

    // ClientDto -> Client
    public Client toEntity(ClientDto dto) {
        if (dto instanceof PersonDto personDto) {
            Person person = new Person();
            person.setClientId(personDto.getClientId());
            person.setPhone(personDto.getPhone());
            person.setEmail(personDto.getEmail());
            person.setName(personDto.getName());
            person.setIsDeleted(personDto.getIsDeleted());
            person.setDeletionDate(personDto.getDeletionDate());
            person.setBirthdate(personDto.getBirthdate());
            return person;
        }
        if (dto instanceof CompanyDto companyDto) {
            Company company = new Company();
            company.setClientId(companyDto.getClientId());
            company.setPhone(companyDto.getPhone());
            company.setEmail(companyDto.getEmail());
            company.setName(companyDto.getName());
            company.setIsDeleted(companyDto.getIsDeleted());
            company.setDeletionDate(companyDto.getDeletionDate());
            company.setCompanyIdentifier(companyDto.getCompanyIdentifier());
            return company;
        }
        throw new IllegalArgumentException("Unknown DTO type: " + dto.getClass());
    }

    public List<Client> toEntities(List<? extends ClientDto> dtos) {
        List<Client> entities = new ArrayList<>();
        for (ClientDto dto : dtos) {
            entities.add(toEntity(dto));
        }
        return entities;
    }
}
