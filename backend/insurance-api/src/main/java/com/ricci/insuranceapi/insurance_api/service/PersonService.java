package com.ricci.insuranceapi.insurance_api.service;

import com.ricci.insuranceapi.insurance_api.model.Person;
import com.ricci.insuranceapi.insurance_api.repository.PersonRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class PersonService {

    private final PersonRepository personRepository;

    @Autowired
    public PersonService(PersonRepository repository) {
        this.personRepository = repository;
    }

    public Person createPerson(Person person) {
        return personRepository.save(person);
    }

    // --- EXTRA ---

    public Page<Person> getAllPersons(Pageable pageable) {
        return personRepository.findAll(pageable);
    }
}
