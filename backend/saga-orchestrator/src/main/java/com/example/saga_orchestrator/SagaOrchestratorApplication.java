package com.example.saga_orchestrator;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"com.example.saga_orchestrator", "com.example.common"})
public class SagaOrchestratorApplication {

	public static void main(String[] args) {
		SpringApplication.run(SagaOrchestratorApplication.class, args);
	}

}
