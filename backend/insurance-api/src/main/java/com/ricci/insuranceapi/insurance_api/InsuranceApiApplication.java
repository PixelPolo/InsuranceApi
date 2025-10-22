package com.ricci.insuranceapi.insurance_api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
public class InsuranceApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(InsuranceApiApplication.class, args);

	}

	@GetMapping("/")
	public String hello() {
		return String.format("Hello Api Factory !");
	}

}
