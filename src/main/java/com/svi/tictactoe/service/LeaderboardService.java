package com.svi.tictactoe.service;

import com.svi.tictactoe.dto.response.LeaderboardResponse;
import com.svi.tictactoe.dto.response.PlayerRankResponse;

import java.util.UUID;

public interface LeaderboardService {

    LeaderboardResponse getLeaderboard();
    PlayerRankResponse getPlayerStatsandRank(UUID playerId);

}
