package com.example.review_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"com.example.review_service", "com.example.common"})
@org.springframework.web.bind.annotation.RestController
public class ReviewServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(ReviewServiceApplication.class, args);
	}

    @org.springframework.web.bind.annotation.GetMapping("/api/v1/test")
    public String test() {
        return "GATEWAY_REACHED_REVIEW_SERVICE";
    }
}
