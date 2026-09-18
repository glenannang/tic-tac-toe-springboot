package com.svi.tictactoe.service;

import com.svi.tictactoe.dto.response.leaderboard.LeaderboardResponse;
import com.svi.tictactoe.dto.response.leaderboard.PlayerRankResponse;

import java.util.UUID;

public interface LeaderboardService {

    LeaderboardResponse getLeaderboard();
    PlayerRankResponse getPlayerStatsAndRank(UUID playerId);

}
