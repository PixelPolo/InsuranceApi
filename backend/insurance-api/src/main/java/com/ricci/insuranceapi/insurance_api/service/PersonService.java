package com.ricci.insuranceapi.insurance_api.service;

import com.ricci.insuranceapi.insurance_api.dto.PersonDto;
import com.ricci.insuranceapi.insurance_api.mapper.ClientMapper;
import com.ricci.insuranceapi.insurance_api.model.Person;
import com.ricci.insuranceapi.insurance_api.repository.PersonRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PersonService {

    private final PersonRepository personRepository;
    private final ClientService clientService;
    private final ClientMapper clientMapper;

    @Autowired
    public PersonService(
            PersonRepository personRepository,
            ClientService clientService,
            ClientMapper clientMapper) {
        this.personRepository = personRepository;
        this.clientService = clientService;
        this.clientMapper = clientMapper;
    }

    // ----------------------
    // --- Read persons ---
    // ----------------------

    public Page<Person> getAllPersons(Pageable pageable) {
        return personRepository.findAll(pageable);
    }

    // ----------------------
    // --- Create persons ---
    // ----------------------

    @Transactional
    public Person createPerson(PersonDto dto) {
        Person person = (Person) clientMapper.toEntity(dto);
        clientService.validateUniquePhoneOrEmail(person);
        return personRepository.save(person);
    }

}
