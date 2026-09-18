package com.svi.tictactoe.controller;

import com.svi.tictactoe.dto.response.LeaderboardResponse;
import com.svi.tictactoe.service.LeaderboardService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/leaderboard")
public class LeaderboardController {

    private final LeaderboardService leaderboardService;

    public LeaderboardController(LeaderboardService leaderboardService) {
        this.leaderboardService = leaderboardService;
    }

    @GetMapping
    public ResponseEntity<LeaderboardResponse> getLeaderboard() {
        LeaderboardResponse response = leaderboardService.getLeaderboard();
        return ResponseEntity.ok(response);
    }
}
