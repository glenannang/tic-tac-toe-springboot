package com.svi.tictactoe.entity;

import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

import java.util.UUID;

@Table("players")
public class Player {

    @PrimaryKey("player_id")
    private UUID playerId;

    @Column("wins")
    private int wins;

    @Column("losses")
    private int losses;

    @Column("draws")
    private int draws;

    @Column("games_played")
    private int gamesPlayed;

    @Column("incomplete_games")
    private int incompleteGames;

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
