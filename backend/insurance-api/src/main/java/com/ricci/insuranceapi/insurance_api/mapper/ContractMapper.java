package com.ricci.insuranceapi.insurance_api.mapper;

import com.ricci.insuranceapi.insurance_api.dto.ClientDtoFactory;
import com.ricci.insuranceapi.insurance_api.dto.ContractDto;
import com.ricci.insuranceapi.insurance_api.dto.ContractExpandedDto;
import com.ricci.insuranceapi.insurance_api.model.Client;
import com.ricci.insuranceapi.insurance_api.model.Contract;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

@Component
public class ContractMapper {

    // POST & PATCH - ContractDto -> Contract
    public Contract toEntity(ContractDto contractDto, Client client) {
        Contract contractEntity = new Contract();
        contractEntity.setClient(client); // Fetched by the caller
        contractEntity.setContractId(contractDto.getContractId());
        contractEntity.setStartDate(contractDto.getStartDate());
        contractEntity.setEndDate(contractDto.getEndDate());
        contractEntity.setCostAmount(contractDto.getCostAmount());
        return contractEntity;
    }

    // GET (simple) - Contract -> ContractDto (with only the Client's UUID)
    public ContractDto toDto(Contract contractEntity) {
        ContractDto contractDto = new ContractDto();
        contractDto.setContractId(contractEntity.getContractId());
        contractDto.setClientId(contractEntity.getClient().getClientId());
        contractDto.setStartDate(contractEntity.getStartDate());
        contractDto.setEndDate(contractEntity.getEndDate());
        contractDto.setCostAmount(contractEntity.getCostAmount());
        return contractDto;
    }

    // GET (expanded) - Contract -> ContractExpandedDto (with the full Client)
    public ContractExpandedDto toExpandedDto(Contract contractEntity) {
        ContractExpandedDto contractExpandedDto = new ContractExpandedDto();
        contractExpandedDto.setContractId(contractEntity.getContractId());
        contractExpandedDto.setClient(ClientDtoFactory.fromClient(contractEntity.getClient()));
        contractExpandedDto.setStartDate(contractEntity.getStartDate());
        contractExpandedDto.setEndDate(contractEntity.getEndDate());
        contractExpandedDto.setCostAmount(contractEntity.getCostAmount());
        return contractExpandedDto;
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

    // List ContractExpandedDto
    public List<ContractExpandedDto> toExpandedDtoList(List<Contract> contracts) {
        List<ContractExpandedDto> result = new ArrayList<>();
        if (contracts == null) {
            return result;
        }
        for (Contract contract : contracts) {
            result.add(toExpandedDto(contract));
        }
        return result;
    }

}
