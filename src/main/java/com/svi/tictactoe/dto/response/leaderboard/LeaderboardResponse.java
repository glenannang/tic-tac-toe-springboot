package com.svi.tictactoe.dto.response.leaderboard;

import java.util.List;

public class LeaderboardResponse {

    private List<LeaderboardEntryResponse> players;

    public List<LeaderboardEntryResponse> getPlayers() {
        return players;
    }

    public void setPlayers(List<LeaderboardEntryResponse> players) {
        this.players = players;
    }
}