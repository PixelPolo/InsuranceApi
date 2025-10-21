package com.ricci.insuranceapi.insurance_api.mapper;

import com.ricci.insuranceapi.insurance_api.dto.ContractDto;
import com.ricci.insuranceapi.insurance_api.model.Client;
import com.ricci.insuranceapi.insurance_api.model.Contract;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

@Component
public class ContractMapper {

    // ContractDto -> Contract
    public Contract toEntity(ContractDto contractDto, Client client) {
        Contract contractEntity = new Contract();
        contractEntity.setClient(client); // Fetched by the caller
        contractEntity.setContractId(contractDto.getContractId());
        contractEntity.setStartDate(contractDto.getStartDate());
        contractEntity.setEndDate(contractDto.getEndDate());
        contractEntity.setCostAmount(contractDto.getCostAmount());
        return contractEntity;
    }

    // Contract -> ContractDto
    public ContractDto toDto(Contract contractEntity) {
        ContractDto contractDto = new ContractDto();
        contractDto.setContractId(contractEntity.getContractId());
        contractDto.setClientId(contractEntity.getClient().getClientId());
        contractDto.setStartDate(contractEntity.getStartDate());
        contractDto.setEndDate(contractEntity.getEndDate());
        contractDto.setCostAmount(contractEntity.getCostAmount());
        return contractDto;
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

}
