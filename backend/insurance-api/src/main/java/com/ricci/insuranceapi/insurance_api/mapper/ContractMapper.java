package com.ricci.insuranceapi.insurance_api.mapper;

import com.ricci.insuranceapi.insurance_api.dto.ContractDto;
import com.ricci.insuranceapi.insurance_api.dto.ContractGetDto;
import com.ricci.insuranceapi.insurance_api.dto.ContractPatchDto;
import com.ricci.insuranceapi.insurance_api.model.Client;
import com.ricci.insuranceapi.insurance_api.model.Contract;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ContractMapper {

    private final ClientMapper clientMapper;

    @Autowired
    public ContractMapper(ClientMapper clientMapper) {
        this.clientMapper = clientMapper;
    }

    // ContractDto -> Contract
    public Contract toEntity(ContractDto contractDto, Client client) {
        Contract contractEntity = new Contract();
        contractEntity.setClient(client); // Client fetched by the caller
        contractEntity.setContractId(contractDto.getContractId());
        contractEntity.setStartDate(contractDto.getStartDate());
        contractEntity.setEndDate(contractDto.getEndDate());
        contractEntity.setCostAmount(contractDto.getCostAmount());
        return contractEntity;
    }

    // Contract -> ContractDto (with only client's uuid)
    public ContractDto toDto(Contract contractEntity) {
        ContractDto contractDto = new ContractDto();
        contractDto.setClientId(contractEntity.getClient().getClientId());
        contractDto.setContractId(contractEntity.getContractId());
        contractDto.setStartDate(contractEntity.getStartDate());
        contractDto.setEndDate(contractEntity.getEndDate());
        contractDto.setCostAmount(contractEntity.getCostAmount());
        return contractDto;
    }

    // Contract -> ContractGetDto (with full client)
    public ContractGetDto toContractGetDto(Contract contract) {
        ContractGetDto dto = new ContractGetDto();
        dto.setContractId(contract.getContractId());
        dto.setClient(clientMapper.toDto(contract.getClient()));
        dto.setStartDate(contract.getStartDate());
        dto.setEndDate(contract.getEndDate());
        dto.setCostAmount(contract.getCostAmount());
        return dto;
    }

    // ContractPatchDto -> Contract
    public Contract toEntityFromUpdate(ContractPatchDto contractDto) {
        Contract contract = new Contract();
        contract.setEndDate(contractDto.getEndDate());
        contract.setCostAmount(contractDto.getCostAmount());
        return contract;
    }

    // List ContractDto
    public List<ContractDto> toDtoList(List<Contract> contracts) {
        List<ContractDto> result = new ArrayList<>();
        if (contracts == null) {
            return result;
        }
        for (Contract contract : contracts) {
            result.add(toDto(contract));
        }
        return result;
    }

    // List ContractGetDto
    public List<ContractGetDto> toContractGetDtoList(List<Contract> contracts) {
        List<ContractGetDto> result = new ArrayList<>();
        if (contracts == null) {
            return result;
        }
        for (Contract contract : contracts) {
            result.add(toContractGetDto(contract));
        }
        return result;
    }

}
