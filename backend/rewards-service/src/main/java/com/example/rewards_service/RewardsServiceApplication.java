package com.example.rewards_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"com.example.rewards_service", "com.example.common"})
public class RewardsServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(RewardsServiceApplication.class, args);
	}

}
