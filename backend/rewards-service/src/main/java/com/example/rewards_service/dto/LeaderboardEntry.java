package com.example.rewards_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LeaderboardEntry {
    private UUID userId;
    private long totalPoints;
}
