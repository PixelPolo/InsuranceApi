package com.ricci.insuranceapi.insurance_api.controller;

import com.ricci.insuranceapi.insurance_api.dto.ClientDto;
import com.ricci.insuranceapi.insurance_api.dto.ClientDtoFactory;
import com.ricci.insuranceapi.insurance_api.dto.PersonDto;
import com.ricci.insuranceapi.insurance_api.model.Person;
import com.ricci.insuranceapi.insurance_api.service.PersonService;
import com.ricci.insuranceapi.insurance_api.utils.PaginationUtils;

import jakarta.validation.Valid;

import java.net.URI;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/${api.version}/clients/persons")
public class PersonController {

    private final PersonService personService;
    private final PaginationUtils paginationUtils;

    @Autowired
    public PersonController(PersonService personService, PaginationUtils paginationUtils) {
        this.personService = personService;
        this.paginationUtils = paginationUtils;
    }

    // POST /api/v_/clients/persons
    @PostMapping
    public ResponseEntity<ClientDto> createPerson(@Valid @RequestBody PersonDto dto) {
        Person created = personService.createPerson(dto);
        String apiVersion = System.getProperty("api.version", "v1");
        URI location = URI.create("/api/" + apiVersion + "/clients/" + created.getClientId()); // Get on /clients/{id}
        return ResponseEntity.created(location).body(new PersonDto(created)); // 201 Created
    }

    // --- EXTRA ---

    // GET /api/v_/clients/persons?page=0&size=5&sortBy=name&sortDir=asc
    @GetMapping
    public ResponseEntity<List<ClientDto>> getAllPersons(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "name") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {

        PageRequest pageRequest = this.paginationUtils.buildPageRequest(page, size, sortBy, sortDir);
        Page<Person> persons = personService.getAllPersons(pageRequest);

        if (persons.isEmpty()) {
            return ResponseEntity.noContent().build(); // 204 No Content
        } else {
            return ResponseEntity.ok(ClientDtoFactory.fromClients(persons.getContent())); // 200 OK
        }
    }

}
