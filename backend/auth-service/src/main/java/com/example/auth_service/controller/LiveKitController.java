package com.example.auth_service.controller;

import com.example.auth_service.service.LiveKitService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/auth/livekit")
@RequiredArgsConstructor
public class LiveKitController {

    private final LiveKitService liveKitService;

    @PostMapping("/token")
    public ResponseEntity<Map<String, String>> getToken(@RequestBody Map<String, String> request) {
        String roomName = request.getOrDefault("roomName", "nsip-assistant-" + UUID.randomUUID().toString());
        String participantName = SecurityContextHolder.getContext().getAuthentication().getName();

        String token = liveKitService.createToken(roomName, participantName);

        return ResponseEntity.ok(Map.of(
            "token", token,
            "roomName", roomName,
            "participantName", participantName
        ));
    }
}
