package com.example.contribution_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"com.example.contribution_service", "com.example.common"})
public class ContributionServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(ContributionServiceApplication.class, args);
	}

}
