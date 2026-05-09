package com.example.rewards_service.controller;

import com.example.rewards_service.dto.LeaderboardEntry;
import com.example.rewards_service.service.RewardsService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/rewards/leaderboard")
@RequiredArgsConstructor
public class LeaderboardController {

    private final RewardsService rewardsService;

    @GetMapping
    public List<LeaderboardEntry> getLeaderboard(@RequestParam(defaultValue = "10") int limit) {
        return rewardsService.getLeaderboard(limit);
    }
}
