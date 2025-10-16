package com.ricci.insuranceapi.insurance_api.repository;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

import com.ricci.insuranceapi.insurance_api.model.Client;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class ClientRepositoryTest {

    private static final Logger log = LoggerFactory.getLogger(ClientRepositoryTest.class);

    @Autowired
    private ClientRepository clientRepository;

    @Test
    void souldFindAllClients() {

        // Find
        List<Client> founded = clientRepository.findAll();

        // Log
        log.info("\n\nClients fonded: {}\n\n", founded);

    }

}
