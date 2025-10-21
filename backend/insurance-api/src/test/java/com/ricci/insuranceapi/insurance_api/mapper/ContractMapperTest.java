package com.ricci.insuranceapi.insurance_api.mapper;

import com.ricci.insuranceapi.insurance_api.dto.ClientDto;
import com.ricci.insuranceapi.insurance_api.dto.ContractDto;
import com.ricci.insuranceapi.insurance_api.dto.ContractExpandedDto;
import com.ricci.insuranceapi.insurance_api.dto.PersonDto;
import com.ricci.insuranceapi.insurance_api.model.Contract;
import com.ricci.insuranceapi.insurance_api.model.Person;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for ContractMapper to verify correct entity <-> DTO conversions.
 */

@SpringBootTest
@ActiveProfiles("test")
class ContractMapperTest {

    @Autowired
    private ContractMapper contractMapper;

    private Person sampleClient;
    private Contract sampleContract;
    private ContractDto sampleContractDto;

    @BeforeEach
    void setup() {
        sampleClient = new Person();
        sampleClient.setClientId(UUID.fromString("11111111-1111-1111-1111-111111111111"));
        sampleClient.setName("Alice Dupont");
        sampleClient.setEmail("alice@example.com");
        sampleClient.setPhone("+41791234567");
        sampleClient.setIsDeleted(null);
        sampleClient.setDeletionDate(null);
        sampleClient.setBirthdate(LocalDate.of(1995, 3, 12));

        sampleContract = new Contract();
        sampleContract.setContractId(UUID.fromString("22222222-2222-2222-2222-222222222222"));
        sampleContract.setClient(sampleClient);
        sampleContract.setStartDate(LocalDateTime.parse("2024-01-15T12:00:00"));
        sampleContract.setUpdateDate(LocalDateTime.parse("2024-01-15T12:00:00"));
        sampleContract.setEndDate(null);
        sampleContract.setCostAmount(new BigDecimal("400.00"));

        sampleContractDto = new ContractDto();
        sampleContractDto.setContractId(sampleContract.getContractId());
        sampleContractDto.setClientId(sampleClient.getClientId());
        sampleContractDto.setStartDate(sampleContract.getStartDate());
        // Not exposed : updateDate
        sampleContractDto.setEndDate(sampleContract.getEndDate());
        sampleContractDto.setCostAmount(sampleContract.getCostAmount());
    }

    // toEntity()
    @Test
    void shouldMapDtoToEntity() {
        Contract contractEntity = contractMapper.toEntity(sampleContractDto, sampleClient);

        assertThat(contractEntity).isNotNull();
        assertThat(contractEntity.getContractId()).isEqualTo(sampleContractDto.getContractId());
        assertThat(contractEntity.getClient()).isEqualTo(sampleClient);
        assertThat(contractEntity.getStartDate()).isEqualTo(sampleContractDto.getStartDate());
        assertThat(contractEntity.getUpdateDate()).isNull(); // Only updated when @PrePersist or @PreUpdate
        assertThat(contractEntity.getCostAmount()).isEqualByComparingTo(sampleContractDto.getCostAmount());
        assertThat(contractEntity.getEndDate()).isNull();
    }

    // toDto()
    @Test
    void shouldMapEntityToDto() {
        ContractDto contractDto = contractMapper.toDto(sampleContract);

        assertThat(contractDto).isNotNull();
        assertThat(contractDto.getContractId()).isEqualTo(sampleContract.getContractId());
        assertThat(contractDto.getClientId()).isEqualTo(sampleClient.getClientId());
        assertThat(contractDto.getStartDate()).isEqualTo(sampleContract.getStartDate());
        assertThat(contractDto.getEndDate()).isEqualTo(sampleContract.getEndDate()); // null
        assertThat(contractDto.getCostAmount()).isEqualByComparingTo(sampleContract.getCostAmount());

    }

    // toExpandedDto()
    @Test
    void shouldMapEntityToExpandedDto() {
        ContractExpandedDto expandedDto = contractMapper.toExpandedDto(sampleContract);

        assertThat(expandedDto).isNotNull();
        assertThat(expandedDto.getContractId()).isEqualTo(sampleContract.getContractId());
        assertThat(expandedDto.getStartDate()).isEqualTo(sampleContract.getStartDate());
        assertThat(expandedDto.getEndDate()).isEqualTo(sampleContract.getEndDate());
        assertThat(expandedDto.getCostAmount()).isEqualByComparingTo(sampleContract.getCostAmount());

        // Client
        assertThat(expandedDto.getClient()).isNotNull().isInstanceOf(ClientDto.class);
        assertThat(expandedDto.getClient().getName()).isEqualTo(sampleClient.getName());
        assertThat(expandedDto.getClient().getEmail()).isEqualTo(sampleClient.getEmail());
        assertThat(expandedDto.getClient().getPhone()).isEqualTo(sampleClient.getPhone());

        // If a Person, check birthdate
        assertThat(expandedDto.getClient()).isInstanceOf(PersonDto.class);
        PersonDto personDto = (PersonDto) expandedDto.getClient();
        assertThat(personDto.getBirthdate()).isEqualTo(LocalDate.of(1995, 3, 12));
    }

    // toDtoList() Method Test
    @Test
    void shouldMapEntityListToDtoList() {
        List<Contract> list = new ArrayList<>();
        list.add(sampleContract);

        List<ContractDto> result = contractMapper.toDtoList(list);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getClientId()).isEqualTo(sampleClient.getClientId());
        assertThat(result.get(0).getCostAmount()).isEqualByComparingTo(sampleContractDto.getCostAmount());

    }

    // toExpandedDtoList()
    @Test
    void shouldMapEntityListToExpandedDtoList() {
        List<Contract> list = new ArrayList<>();
        list.add(sampleContract);

        List<ContractExpandedDto> result = contractMapper.toExpandedDtoList(list);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getClient()).isInstanceOf(ClientDto.class);
        assertThat(result.get(0).getClient().getName()).isEqualTo("Alice Dupont");
    }

}
