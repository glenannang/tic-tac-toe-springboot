package com.svi.tictactoe.entity;

import com.svi.tictactoe.enums.PlayerSymbol;
import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

import java.util.UUID;

@Table("moves_by_game")
public class Move {

    @PrimaryKey
    private MoveKey key;

    @Column("player_id")
    private UUID playerId;

    @Column("symbol")
    private PlayerSymbol symbol;

    @Column("position")
    private int position;

    public Move() {
    }

    public MoveKey getKey() {
        return key;
    }

    public void setKey(MoveKey key) {
        this.key = key;
    }

    public UUID getPlayerId() {
        return playerId;
    }

    public void setPlayerId(UUID playerId) {
        this.playerId = playerId;
    }

    public PlayerSymbol getSymbol() {
        return symbol;
    }

    public void setSymbol(PlayerSymbol symbol) {
        this.symbol = symbol;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }
}