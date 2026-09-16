package com.svi.tictactoe.entity;

import org.springframework.data.cassandra.core.cql.Ordering;
import org.springframework.data.cassandra.core.cql.PrimaryKeyType;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyClass;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn;

import java.io.Serializable;
import java.util.UUID;

@PrimaryKeyClass
public class MoveKey implements Serializable {

    @PrimaryKeyColumn(
            name = "game_id",
            type = PrimaryKeyType.PARTITIONED
    )
    private UUID gameId;

    @PrimaryKeyColumn(
            name = "created_at",
            ordinal = 0,
            type = PrimaryKeyType.CLUSTERED,
            ordering = Ordering.ASCENDING
    )
    private int moveNumber;

    public MoveKey() {
    }

    public UUID getGameId() {
        return gameId;
    }

    public void setGameId(UUID gameId) {
        this.gameId = gameId;
    }

    public int getMoveNumber() {
        return moveNumber;
    }

    public void setMoveNumber(int moveNumber) {
        this.moveNumber = moveNumber;
    }
}