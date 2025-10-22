package com.ricci.insuranceapi.insurance_api.mapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import com.ricci.insuranceapi.insurance_api.dto.ClientDto;
import com.ricci.insuranceapi.insurance_api.dto.CompanyDto;
import com.ricci.insuranceapi.insurance_api.dto.PersonDto;
import com.ricci.insuranceapi.insurance_api.model.Client;
import com.ricci.insuranceapi.insurance_api.model.Company;
import com.ricci.insuranceapi.insurance_api.model.Person;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
class ClientMapperTest {

    @Autowired
    private ClientMapper clientMapper;

    private Person samplePerson;
    private Company sampleCompany;

    @BeforeEach
    void setup() {
        samplePerson = new Person();
        samplePerson.setClientId(UUID.fromString("11111111-1111-1111-1111-111111111111"));
        samplePerson.setName("Alice Dupont");
        samplePerson.setEmail("alice@example.com");
        samplePerson.setPhone("+41791234567");
        samplePerson.setIsDeleted(false);
        samplePerson.setDeletionDate(null);
        samplePerson.setBirthdate(LocalDate.of(1990, 5, 10));

        sampleCompany = new Company();
        sampleCompany.setClientId(UUID.fromString("22222222-2222-2222-2222-222222222222"));
        sampleCompany.setName("Bob SA");
        sampleCompany.setEmail("contact@Bob.com");
        sampleCompany.setPhone("+41224567890");
        sampleCompany.setIsDeleted(true);
        sampleCompany.setDeletionDate(LocalDateTime.parse("2024-01-01T10:00:00"));
        sampleCompany.setCompanyIdentifier("Bob-001");
    }

    // toDto(Person)
    @Test
    void shouldMapPersonEntityToDto() {
        ClientDto dto = clientMapper.toDto(samplePerson);
        assertThat(dto).isInstanceOf(PersonDto.class);

        PersonDto personDto = (PersonDto) dto;
        assertThat(personDto.getClientId()).isEqualTo(samplePerson.getClientId());
        assertThat(personDto.getName()).isEqualTo(samplePerson.getName());
        assertThat(personDto.getBirthdate()).isEqualTo(samplePerson.getBirthdate());
    }

    // toDto(Company)
    @Test
    void shouldMapCompanyEntityToDto() {
        ClientDto dto = clientMapper.toDto(sampleCompany);
        assertThat(dto).isInstanceOf(CompanyDto.class);

        CompanyDto companyDto = (CompanyDto) dto;
        assertThat(companyDto.getCompanyIdentifier()).isEqualTo(sampleCompany.getCompanyIdentifier());
        assertThat(companyDto.getIsDeleted()).isTrue();
    }

    // toEntity(PersonDto)
    @Test
    void shouldMapDtoToPersonEntity() {
        PersonDto dto = new PersonDto();
        dto.setClientId(samplePerson.getClientId());
        dto.setName(samplePerson.getName());
        dto.setEmail(samplePerson.getEmail());
        dto.setPhone(samplePerson.getPhone());
        dto.setIsDeleted(samplePerson.getIsDeleted());
        dto.setDeletionDate(samplePerson.getDeletionDate());
        dto.setBirthdate(samplePerson.getBirthdate());

        Client entity = clientMapper.toEntity(dto);

        assertThat(entity).isInstanceOf(Person.class);
        Person person = (Person) entity;
        assertThat(person.getEmail()).isEqualTo(dto.getEmail());
        assertThat(person.getBirthdate()).isEqualTo(dto.getBirthdate());
    }

    // toEntity(CompanyDto)
    @Test
    void shouldMapDtoToCompanyEntity() {
        CompanyDto dto = new CompanyDto();
        dto.setClientId(sampleCompany.getClientId());
        dto.setName(sampleCompany.getName());
        dto.setEmail(sampleCompany.getEmail());
        dto.setPhone(sampleCompany.getPhone());
        dto.setIsDeleted(sampleCompany.getIsDeleted());
        dto.setDeletionDate(sampleCompany.getDeletionDate());
        dto.setCompanyIdentifier(sampleCompany.getCompanyIdentifier());

        Client entity = clientMapper.toEntity(dto);

        assertThat(entity).isInstanceOf(Company.class);
        Company company = (Company) entity;
        assertThat(company.getCompanyIdentifier()).isEqualTo(dto.getCompanyIdentifier());
    }

    // toDtoList(List<? extends Client>)
    @Test
    void shouldMapEntityListToDtoList() {
        List<ClientDto> result = clientMapper.toDtos(List.of(samplePerson, sampleCompany));
        assertThat(result).hasSize(2);
        assertThat(result.get(0)).isInstanceOf(PersonDto.class);
        assertThat(result.get(1)).isInstanceOf(CompanyDto.class);
    }

    // toEntityList(List<? extends ClientDto>)
    @Test
    void shouldMapDtoListToEntityList() {
        PersonDto personDto = new PersonDto();
        personDto.setClientId(samplePerson.getClientId());
        personDto.setName(samplePerson.getName());

        CompanyDto companyDto = new CompanyDto();
        companyDto.setClientId(sampleCompany.getClientId());
        companyDto.setName(sampleCompany.getName());

        List<Client> result = clientMapper.toEntities(List.of(personDto, companyDto));

        assertThat(result).hasSize(2);
        assertThat(result.get(0)).isInstanceOf(Person.class);
        assertThat(result.get(1)).isInstanceOf(Company.class);
    }
}
