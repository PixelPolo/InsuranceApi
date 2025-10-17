package com.ricci.insuranceapi.insurance_api.dto;

import java.util.ArrayList;
import java.util.List;

import com.ricci.insuranceapi.insurance_api.model.Client;
import com.ricci.insuranceapi.insurance_api.model.Person;
import com.ricci.insuranceapi.insurance_api.model.Company;

/*
 * Since /clients returns all clients, regardless of their type,
 * we need a ClientDtoFactory to build a unified DTO list for Person and Company.
 */

public final class ClientDtoFactory {

    private ClientDtoFactory() {
    }

    public static ClientDto fromClient(Client client) {
        if (client instanceof Person) {
            return new PersonDto((Person) client);
        }
        if (client instanceof Company) {
            return new CompanyDto((Company) client);
        }
        throw new IllegalArgumentException("Unknown client type: " + client.getClass());
    }

    public static List<ClientDto> fromClients(List<? extends Client> clients) {
        List<ClientDto> dtos = new ArrayList<>();
        for (Client client : clients) {
            dtos.add(fromClient(client));
        }
        return dtos;
    }

}
