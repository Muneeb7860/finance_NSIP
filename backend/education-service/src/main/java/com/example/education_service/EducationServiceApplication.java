package com.example.education_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"com.example.education_service", "com.example.common"})
public class EducationServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(EducationServiceApplication.class, args);
	}

}
