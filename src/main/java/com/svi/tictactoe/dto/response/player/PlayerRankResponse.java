package com.svi.tictactoe.dto.response.player;

import java.util.UUID;

public class PlayerRankResponse {

    private int rank;
    private int totalPlayers;
    private UUID playerId;
    private int wins;
    private int losses;
    private int draws;
    private int gamesPlayed;
    private int incompleteGames;

    public int getRank() {
        return rank;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }

    public int getTotalPlayers() {
        return totalPlayers;
    }

    public void setTotalPlayers(int totalPlayers) {
        this.totalPlayers = totalPlayers;
    }

    public UUID getPlayerId() {
        return playerId;
    }

    public void setPlayerId(UUID playerId) {
        this.playerId = playerId;
    }

    public int getWins() {
        return wins;
    }

    public void setWins(int wins) {
        this.wins = wins;
    }

    public int getLosses() {
        return losses;
    }

    public void setLosses(int losses) {
        this.losses = losses;
    }

    public int getDraws() {
        return draws;
    }

    public void setDraws(int draws) {
        this.draws = draws;
    }

    public int getGamesPlayed() {
        return gamesPlayed;
    }

    public void setGamesPlayed(int gamesPlayed) {
        this.gamesPlayed = gamesPlayed;
    }

    public int getIncompleteGames() {
        return incompleteGames;
    }

    public void setIncompleteGames(int incompleteGames) {
        this.incompleteGames = incompleteGames;
    }
}
