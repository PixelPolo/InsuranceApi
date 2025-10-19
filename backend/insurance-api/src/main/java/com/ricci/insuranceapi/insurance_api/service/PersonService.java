package com.ricci.insuranceapi.insurance_api.service;

import com.ricci.insuranceapi.insurance_api.dto.PersonDto;
import com.ricci.insuranceapi.insurance_api.model.Person;
import com.ricci.insuranceapi.insurance_api.repository.PersonRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class PersonService {

    // TODO - Improve the service according requirements

    private final PersonRepository personRepository;
    private final ClientService clientService;

    @Autowired
    public PersonService(PersonRepository personRepository, ClientService clientService) {
        this.personRepository = personRepository;
        this.clientService = clientService;
    }

    public Person createPerson(PersonDto dto) {
        Person person = new Person(dto);
        clientService.validateCommonFields(person);
        return personRepository.save(person);
    }

    public Page<Person> getAllPersons(Pageable pageable) {
        return personRepository.findAll(pageable);
    }

}
