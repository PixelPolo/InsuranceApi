package com.ricci.insuranceapi.insurance_api;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SpringBootTest
public abstract class InsuranceApiApplicationTests {

	protected static final String API_VERSION = System.getProperty("api.version", "v1");
	protected static final String BASE_PATH = "/api/" + API_VERSION + "/";
	protected static final boolean VERBOSE = "true".equalsIgnoreCase(System.getProperty("test.verbose", "true"));
	protected static final Logger LOGGER = LoggerFactory.getLogger(InsuranceApiApplicationTests.class);

	@Autowired
	protected JdbcTemplate jdbc;

	@BeforeEach
	void resetDatabase() throws IOException {
		// Test data is loaded from a reusable migration script for Flyway:
		// /backend/insurance-api/src/test/resources/db/migration/R__sample-test-data.sql
		String sql = Files.readString(Paths.get("src/test/resources/db/migration/R__sample-test-data.sql"));
		jdbc.execute(sql);
	}

	@Test
	void contextLoads() {
	}

}
