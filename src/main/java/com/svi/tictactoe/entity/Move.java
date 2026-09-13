package com.svi.tictactoe.entity;

import com.svi.tictactoe.enums.PlayerMark;
import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

import java.time.Instant;
import java.util.UUID;

@Table("moves_by_game")
public class Move {

    @PrimaryKey
    private MoveKey key;

    @Column("player_id")
    private UUID playerId;

    @Column("mark")
    private PlayerMark mark;

    @Column("position")
    private int position;

    @Column("created_at")
    private Instant createdAt;

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

    public PlayerMark getMark() {
        return mark;
    }

    public void setMark(PlayerMark mark) {
        this.mark = mark;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

}