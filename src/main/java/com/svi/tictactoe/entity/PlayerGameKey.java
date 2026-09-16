package com.svi.tictactoe.entity;

import org.springframework.data.cassandra.core.cql.PrimaryKeyType;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyClass;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn;

import java.io.Serializable;
import java.util.UUID;

@PrimaryKeyClass
public class PlayerGameKey implements Serializable {

    @PrimaryKeyColumn(
            name = "player_id",
            type = PrimaryKeyType.PARTITIONED
    )
    private UUID playerId;

    @PrimaryKeyColumn(
            name = "game_id",
            ordinal = 0,
            type = PrimaryKeyType.CLUSTERED
    )
    private UUID gameId;

    public PlayerGameKey() {
    }

    public UUID getPlayerId() {
        return playerId;
    }

    public void setPlayerId(UUID playerId) {
        this.playerId = playerId;
    }

    public UUID getGameId() {
        return gameId;
    }

    public void setGameId(UUID gameId) {
        this.gameId = gameId;
    }
}