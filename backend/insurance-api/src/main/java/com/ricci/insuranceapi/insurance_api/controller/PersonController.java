package com.ricci.insuranceapi.insurance_api.controller;

import com.ricci.insuranceapi.insurance_api.model.Person;
import com.ricci.insuranceapi.insurance_api.service.PersonService;

import java.net.URI;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/${api.version}/clients/persons")
public class PersonController {

    // TODO - Improve error codes and validation with DTOs

    private final PersonService personService;

    @Autowired
    public PersonController(PersonService personService) {
        this.personService = personService;
    }

    // POST /api/v_/clients/persons
    @PostMapping
    public ResponseEntity<Person> createPerson(@RequestBody Person person) {
        Person created = personService.createPerson(person);
        String apiVersion = System.getProperty("api.version", "v1");
        // We get the client only with /clients/{id} and not /clients/persons/{id}
        URI location = URI.create("/api/" + apiVersion + "/clients/" + created.getClientId());
        return ResponseEntity.created(location).body(created); // 201 Created
    }

    // --- EXTRA ---

    // GET /api/v_/clients/persons?page=0&size=5&sortBy=name&sortDir=asc
    @GetMapping
    public ResponseEntity<List<Person>> getAllPersons(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "name") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {

        Sort sort = "desc".equalsIgnoreCase(sortDir)
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();

        PageRequest pageRequest = PageRequest.of(page, size, sort);
        Page<Person> persons = personService.getAllPersons(pageRequest);

        return persons.isEmpty()
                ? ResponseEntity.noContent().build() // 204 No Content
                : ResponseEntity.ok(persons.getContent()); // 200 OK
    }
}
