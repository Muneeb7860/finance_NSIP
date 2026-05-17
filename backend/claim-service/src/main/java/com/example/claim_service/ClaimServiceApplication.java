package com.example.claim_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication(scanBasePackages = {"com.example.claim_service", "com.example.common"})
@EnableScheduling
public class ClaimServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(ClaimServiceApplication.class, args);
	}

}
