package com.example.review_service.controller;

import com.example.review_service.service.HafidaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/hafida")
@CrossOrigin(origins = "*")
public class HafidaController {

    @Autowired
    private HafidaService hafidaService;

    @PostMapping("/consult")
    public ResponseEntity<?> consult(@RequestBody Map<String, Object> request) {
        String userId = (String) request.get("userId");
        String query = (String) request.get("query");
        @SuppressWarnings("unchecked")
        Map<String, Object> context = (Map<String, Object>) request.get("context");

        return ResponseEntity.ok(hafidaService.provideAdvice(userId, query, context));
    }

    @GetMapping("/proactive/{userId}")
    public ResponseEntity<?> getProactiveAdvice(@PathVariable String userId) {
        return ResponseEntity.ok(hafidaService.getProactiveAdvice(userId));
    }
}
