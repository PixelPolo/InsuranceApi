package com.ricci.insuranceapi.insurance_api;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import org.junit.jupiter.api.Test;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/*
 * Test data is loaded from a reusable migration script for Flyway:
 * /src/test/resources/db/migration/R__sample-test-data.sql
 */

@SpringBootTest
@ActiveProfiles("test")
@Sql("/db/migration/R__sample-test-data.sql") // Executed before each test
public abstract class InsuranceApiApplicationTests {

	protected static final String API_VERSION = System.getProperty("api.version", "v1");
	protected static final String BASE_PATH = "/api/" + API_VERSION + "/";
	protected static final boolean VERBOSE = "true".equalsIgnoreCase(System.getProperty("test.verbose", "true"));
	protected static final Logger LOGGER = LoggerFactory.getLogger(InsuranceApiApplicationTests.class);

	@Test
	void contextLoads() {
	}

	protected boolean isSameLocalDateTime(LocalDateTime dateOne, LocalDateTime dateTwo) {
		long precisionSeconds = 2; // To avoid time precision error
		return Math.abs(ChronoUnit.SECONDS.between(dateOne, dateTwo)) < precisionSeconds;
	}

}
